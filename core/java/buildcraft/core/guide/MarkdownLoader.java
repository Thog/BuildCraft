package buildcraft.core.guide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

import buildcraft.api.core.BCLog;

public class MarkdownLoader {
    public static GuidePartFactory<GuidePage> loadMarkdown(ResourceLocation location, GuideManager manager) {
        List<String> lines = Lists.newArrayList();
        try {
            IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(location);
            if (resource == null) {
                BCLog.logger.warn("Could not load the resource location " + location + " because null was returned!");
                return new GuidePageFactory();
            }

            InputStream stream = resource.getInputStream();
            BufferedReader buffered = new BufferedReader(new InputStreamReader(stream));
            String line = "";
            while ((line = buffered.readLine()) != null) {
                lines.add(line);
            }

        } catch (IOException e) {
            BCLog.logger.warn("Could not load the resource location " + location + " because " + e);
            return new GuidePageFactory();
        }

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            // TODO: This :P
        }

        return new GuidePageFactory();
    }
}
