package buildcraft.robotics.pathfinding;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.world.WorldSavedData;

import buildcraft.core.lib.utils.NBTUtils;
import buildcraft.robotics.pathfinding.BlockPath.PathDirection;

public class NetworkSavedData extends WorldSavedData {

    List<BlockVolume> volumes = Lists.newArrayList();
    List<BlockPath> paths = Lists.newArrayList();
    private WorldNetworkManager manager = null;

    public NetworkSavedData(String name) {
        super(name);
    }

    public NetworkSavedData(WorldNetworkManager manager, String name) {
        this(name);
        this.manager = manager;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        volumes.clear();
        paths.clear();

        NBTTagList volumeTagList = (NBTTagList) nbt.getTag("volumes");
        for (int idx = 0; idx < volumeTagList.tagCount(); idx++) {
            NBTTagCompound volumeTag = volumeTagList.getCompoundTagAt(idx);
            BlockPos min = NBTUtils.readBlockPos(volumeTag.getTag("min"));
            BlockPos max = NBTUtils.readBlockPos(volumeTag.getTag("max"));
            volumes.add(new BlockVolume(null, min, max));
        }

        for (int idx = 0; idx < volumeTagList.tagCount(); idx++) {
            NBTTagCompound volumeTag = volumeTagList.getCompoundTagAt(idx);
            BlockVolume volume = volumes.get(idx);
            NBTTagList areaTags = (NBTTagList) volumeTag.getTag("areas");
            for (int i = 0; i < areaTags.tagCount(); i++) {
                NBTTagCompound areaTag = areaTags.getCompoundTagAt(i);
                BlockPos min = NBTUtils.readBlockPos(areaTag.getTag("min"));
                BlockPos max = NBTUtils.readBlockPos(areaTag.getTag("max"));
                Axis axis = NBTUtils.readEnum(areaTag.getTag("axis"), Axis.class);
                BlockVolume other = volumes.get(areaTag.getInteger("connected"));
                BlockArea area = new BlockArea(volume, other, min, max, axis);
                volume.linkedAreas.add(area);
                other.linkedAreas.add(area);
            }
        }

        NBTTagList pathTagList = (NBTTagList) nbt.getTag("paths");
        for (int idx = 0; idx < pathTagList.tagCount(); idx++) {
            NBTTagCompound pathTag = pathTagList.getCompoundTagAt(idx);
            BlockPos start = NBTUtils.readBlockPos(pathTag.getTag("start"));
            BlockPos end = NBTUtils.readBlockPos(pathTag.getTag("end"));
            BlockVolume one = volumes.get(pathTag.getInteger("one"));
            BlockVolume two = volumes.get(pathTag.getInteger("two"));

            List<EnumFacing> faceList = Lists.newArrayList();
            List<BlockPos> dependants = Lists.newArrayList();
            dependants.add(start);
            BlockPos current = start;
            NBTTagList pathFacesList = (NBTTagList) pathTag.getTag("faces");
            for (int i = 0; i < pathFacesList.tagCount(); i++) {
                EnumFacing face = NBTUtils.readEnum(pathFacesList.get(i), EnumFacing.class);
                faceList.add(face);
                current = current.offset(face);
                dependants.add(current);
            }
            Map<PathDirection, ImmutableList<EnumFacing>> map = Maps.newHashMap();

            map.put(PathDirection.START_TO_END, ImmutableList.copyOf(faceList));
            map.put(PathDirection.END_TO_START, ImmutableList.copyOf(BlockPath.reverse(faceList)));

            ImmutableMap<PathDirection, ImmutableList<EnumFacing>> immutableFacings = ImmutableMap.copyOf(map);
            ImmutableSet<BlockPos> immutableDependants = ImmutableSet.copyOf(dependants);
            BlockPath path = new BlockPath(null, one, two, start, end, immutableFacings, immutableDependants);
            paths.add(path);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        volumes.clear();
        volumes.addAll(manager.volumeSet);

        paths.clear();
        paths.addAll(manager.pathSet);

        NBTTagList volumeTagList = new NBTTagList();
        for (BlockVolume volume : volumes) {
            NBTTagCompound volumeTag = new NBTTagCompound();
            volumeTag.setTag("min", NBTUtils.writeBlockPos(volume.min));
            volumeTag.setTag("max", NBTUtils.writeBlockPos(volume.max));

            NBTTagList areaTags = new NBTTagList();
            for (BlockArea area : volume.linkedAreas) {
                if (area.two == volume) {
                    continue;
                    // This is the second time the area would have been written so just ignore it
                }
                NBTTagCompound areaTag = new NBTTagCompound();
                areaTag.setTag("min", NBTUtils.writeBlockPos(area.min));
                areaTag.setTag("max", NBTUtils.writeBlockPos(area.max));
                areaTag.setTag("axis", NBTUtils.writeEnum(area.axis));
                areaTag.setInteger("connected", volumes.indexOf(area.two));
                areaTags.appendTag(areaTag);
            }
            volumeTag.setTag("areas", areaTags);
            volumeTagList.appendTag(volumeTag);
        }

        nbt.setTag("volumes", volumeTagList);

        NBTTagList pathTags = new NBTTagList();

        for (BlockPath path : paths) {
            NBTTagCompound pathTag = new NBTTagCompound();
            pathTag.setTag("start", NBTUtils.writeBlockPos(path.start));
            pathTag.setTag("end", NBTUtils.writeBlockPos(path.end));
            pathTag.setInteger("one", volumes.indexOf(path.one));
            pathTag.setInteger("two", volumes.indexOf(path.two));

            NBTTagList pathFacesTag = new NBTTagList();
            for (EnumFacing face : path.path.get(PathDirection.START_TO_END)) {
                pathFacesTag.appendTag(NBTUtils.writeEnum(face));
            }
            pathTag.setTag("faces", pathFacesTag);
        }

        nbt.setTag("paths", pathTags);
    }
}
