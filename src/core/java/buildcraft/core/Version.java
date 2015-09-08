/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL. Please check the contents
 * of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt */
package buildcraft.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInterModComms;

import buildcraft.api.core.BCLog;
import buildcraft.core.proxy.CoreProxy;

public class Version implements Runnable {
    public enum EnumUpdateState {
        CURRENT,
        OUTDATED,
        CONNECTION_ERROR
    }

    public static final String VERSION = "@VERSION@";
    public static EnumUpdateState currentVersion = EnumUpdateState.CURRENT;

    private static final String REMOTE_VERSION_FILE =
        "https://raw.githubusercontent.com/BuildCraft/BuildCraft/master/buildcraft_resources/versions.txt";

    private static final String NEW_REMOTE_VERSION_FILE =
        "https://raw.githubusercontent.com/BuildCraft/BuildCraft/master/src/core/resources/versions.txt";

    private static final String REMOTE_CHANGELOG_ROOT =
        "https://raw.githubusercontent.com/BuildCraft/BuildCraft/master/buildcraft_resources/changelog/";

    public static final String NEW_REMOTE_CHANGELOG_ROOT =
        "https://raw.githubusercontent.com/BuildCraft/BuildCraft/master/src/core/resources/changelog/";

    private static String recommendedVersion;
    private static String[] cachedChangelog;

    private static Version instance = new Version();

    private static boolean sentIMCOutdatedMessage = false;

    public static String getVersion() {
        return VERSION;
    }

    public static boolean isOutdated() {
        return currentVersion == EnumUpdateState.OUTDATED;
    }

    public static boolean needsUpdateNoticeAndMarkAsSeen() {
        if (!isOutdated() || sentIMCOutdatedMessage) {
            return false;
        }

        Property property = BuildCraftCore.mainConfiguration.get("vars", "version.seen", VERSION);
        property.comment = "indicates the last version the user has been informed about and will suppress further notices on it.";
        String seenVersion = property.getString();

        if (recommendedVersion == null || recommendedVersion.equals(seenVersion)) {
            return false;
        }

        property.set(recommendedVersion);
        BuildCraftCore.mainConfiguration.save();
        return true;
    }

    public static String getRecommendedVersion() {
        return recommendedVersion;
    }

    public static void versionCheck() {
        if ("0.0.0".equals(VERSION)) {
            return;
        }
        if ("@VERSION@".equals(VERSION)) {
            BCLog.logger.info("Found a development version. Not checking for updates.");
            currentVersion = EnumUpdateState.CURRENT;
            return;
        }

        String mcVersion = CoreProxy.proxy.getMinecraftVersion();
        String[] lines = getLines(NEW_REMOTE_VERSION_FILE);
        if (lines == null) {
            lines = getLines(REMOTE_VERSION_FILE);
        }
        if (lines == null) {
            BCLog.logger.warn("Unable to read from remote version authority.");
            currentVersion = EnumUpdateState.CONNECTION_ERROR;
            return;
        } else {
            boolean foundThisMcVersion = false;

            for (String line : lines) {
                String[] tokens = line.split(":");
                if (mcVersion.matches(tokens[0])) {
                    if (DefaultProps.MOD.matches(tokens[1])) {
                        foundThisMcVersion = true;
                        recommendedVersion = tokens[2];

                        if (VERSION.matches(tokens[2])) {
                            BCLog.logger.trace("Using the latest version [" + getVersion() + "] for Minecraft " + mcVersion);
                            currentVersion = EnumUpdateState.CURRENT;
                            return;
                        }
                    }
                }
            }
            if (foundThisMcVersion) {
                BCLog.logger.warn("Using outdated version [" + VERSION + "] for Minecraft " + mcVersion + ". Consider updating to "
                    + recommendedVersion + ".");
                currentVersion = EnumUpdateState.OUTDATED;
                sendIMCOutdatedMessage();
            } else {
                BCLog.logger.info("Using a development version [" + VERSION + "] for Minecraft " + mcVersion
                    + ". Consider developing and finishing this one please? :)");
                currentVersion = EnumUpdateState.CURRENT;
            }
        }
    }

    private static String[] getLines(String remoteUrl) {
        try {
            String location = remoteUrl;
            HttpURLConnection conn = null;
            while (location != null && !location.isEmpty()) {
                URL url = new URL(location);

                if (conn != null) {
                    conn.disconnect();
                }

                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("User-Agent",
                        "Mozilla/5.0 (Windows; U; Windows NT 6.0; ru; rv:1.9.0.11) Gecko/2009060215 Firefox/3.0.11 (.NET CLR 3.5.30729)");
                conn.connect();
                location = conn.getHeaderField("Location");
            }

            if (conn == null) {
                throw new NullPointerException("Did not find a connection!");
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line;
            List<String> strings = Lists.newArrayList();
            while ((line = reader.readLine()) != null) {
                strings.add(line);
            }
            conn.disconnect();
            reader.close();
            if (strings.size() == 0) {
                return null;
            }
            BCLog.logger.info("Successfully read from " + remoteUrl + ", found " + strings.size() + " lines");
            return strings.toArray(new String[strings.size()]);

        } catch (IOException e) {
            BCLog.logger.warn("Failed to read from " + remoteUrl);
            return null;
        }
    }

    public static String[] getChangelog() {
        if (cachedChangelog == null) {
            cachedChangelog = grabChangelog(recommendedVersion);
        }

        return cachedChangelog;
    }

    public static String[] grabChangelog(String version) {
        String[] lines = getLines(NEW_REMOTE_CHANGELOG_ROOT + version);
        if (lines == null) {
            lines = getLines(REMOTE_CHANGELOG_ROOT + version);
        }
        if (lines == null) {
            BCLog.logger.warn("Unable to read changelog from remote site.");
            return new String[] { String.format("Unable to retrieve changelog for %s %s", DefaultProps.MOD, version) };
        } else {
            return lines;
        }
    }

    @Override
    public void run() {

        int count = 0;
        currentVersion = null;

        BCLog.logger.info("Beginning version check");

        try {
            while ((count < 3) && ((currentVersion == null) || (currentVersion == EnumUpdateState.CONNECTION_ERROR))) {
                versionCheck();
                count++;

                if (currentVersion == EnumUpdateState.CONNECTION_ERROR) {
                    BCLog.logger.info("Version check attempt " + count + " failed, trying again in 10 seconds");
                    Thread.sleep(10000);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (currentVersion == EnumUpdateState.CONNECTION_ERROR) {
            BCLog.logger.info("Version check failed");
        }

        BCLog.logger.info("Finished version check");
    }

    /** This is an integration with Dynious Version Checker See http://www.minecraftforum.net/topic/2721902- */
    public static void sendIMCOutdatedMessage() {
        if (Loader.isModLoaded("VersionChecker")) {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setString("modDisplayName", "BuildCraft");
            compound.setString("oldVersion", VERSION);
            compound.setString("newVersion", getRecommendedVersion());

            compound.setString("updateUrl", "http://www.mod-buildcraft.com/download/");
            compound.setBoolean("isDirectLink", false);

            StringBuilder stringBuilder = new StringBuilder();
            for (String changeLogLine : getChangelog()) {
                stringBuilder.append(changeLogLine).append("\n");
            }
            compound.setString("changeLog", stringBuilder.toString());

            FMLInterModComms.sendRuntimeMessage("BuildCraft|Core", "VersionChecker", "addUpdate", compound);
            sentIMCOutdatedMessage = true;
        }
    }

    public static void displayChangelog(ICommandSender sender) {
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "" + EnumChatFormatting.BOLD + StatCollector.translateToLocalFormatted(
                "command.buildcraft.changelog_header", getRecommendedVersion())));
        for (String updateLine : Version.getChangelog()) {
            String colour = "\u00A79";
            if (updateLine.startsWith("*")) {
                colour = "\u00A7f";
            } else if (updateLine.trim().endsWith(":")) {
                colour = "\u00A76";
            }
            sender.addChatMessage(new ChatComponentText(colour + updateLine));
        }
    }

    public static void check() {
        new Thread(instance, "BuildCraft Update Checker").start();
    }
}
