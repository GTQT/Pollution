import com.mojang.authlib.properties.PropertyMap;
import meowmel.pollution.client.tesr.BloodyStarlightContainerVisual;
import meowmel.pollution.common.entity.dragon.DragonContainmentData;
import meowmel.pollution.common.entity.dragon.DragonLifeStage;
import meowmel.pollution.common.entity.dragon.EntityPollutionDragon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.Session;
import net.minecraft.world.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.Proxy;
import java.nio.ByteBuffer;
import java.util.Collections;

/** Renders the production model in an offscreen GL context; never opens or modifies a world save. */
public final class DragonContainmentRenderSmoke {
    public static void main(String[] args) {
        Pbuffer buffer = null;
        try {
            File project = new File(args[0]);
            int width = 1200, height = 800;
            buffer = new Pbuffer(width, height, new PixelFormat().withDepthBits(24), null);
            buffer.makeCurrent();
            Minecraft mc = new Minecraft(new GameConfiguration(
                    new GameConfiguration.UserInformation(new Session("RenderSmoke", "0", "", "legacy"),
                            new PropertyMap(), new PropertyMap(), Proxy.NO_PROXY),
                    new GameConfiguration.DisplayInformation(width, height, false, true),
                    new GameConfiguration.FolderInformation(new File(project, "build/containment-render"),
                            new File(project, "src/main/resources"), new File(project, "build/resources/patchedMc"), null),
                    new GameConfiguration.GameInformation(false, "1.12.2", "release"),
                    new GameConfiguration.ServerInformation(null, 0)));
            mc.gameSettings = new GameSettings();
            OpenGlHelper.initializeTextures();
            SimpleReloadableResourceManager resources = new SimpleReloadableResourceManager(new MetadataSerializer());
            resources.reloadResources(Collections.singletonList(new FolderResourcePack(new File(project, "src/main/resources"))));
            mc.renderEngine = new TextureManager(resources);
            mc.world = new WorldClient(null, new WorldSettings(0, GameType.CREATIVE, false, false, WorldType.FLAT),
                    0, EnumDifficulty.PEACEFUL, new Profiler());

            GlStateManager.viewport(0, 0, width, height);
            GlStateManager.matrixMode(GL11.GL_PROJECTION);
            GlStateManager.loadIdentity();
            GlStateManager.ortho(-1.35, 1.35, -0.9, 0.9, -10, 10);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.loadIdentity();
            GlStateManager.enableDepth();
            GlStateManager.enableAlpha();
            GlStateManager.clearColor(0.79F, 0.82F, 0.86F, 1);
            GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            EntityPollutionDragon dragon = new EntityPollutionDragon(mc.world);
            dragon.setStage(DragonLifeStage.ADULT);
            dragon.setAppearance(2);
            NBTTagCompound payload = DragonContainmentData.capture(dragon);
            draw(payload, -0.62, 0.95, 60, true, false);
            draw(payload, 0.62, 0.95, 60, true, true);
            int error = GL11.glGetError();
            if (error != GL11.GL_NO_ERROR) throw new AssertionError("OpenGL error: " + error);
            if (!GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK)) throw new AssertionError("Renderer leaked depth mask");
            ByteBuffer pixels = BufferUtils.createByteBuffer(width * height * 4);
            GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);
            BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int y = 0; y < height; y++) for (int x = 0; x < width; x++) {
                int i = (y * width + x) * 4;
                int rgb = (pixels.get(i) & 255) << 16 | (pixels.get(i + 1) & 255) << 8 | pixels.get(i + 2) & 255;
                output.setRGB(x, height - 1 - y, rgb);
            }
            Graphics2D text = output.createGraphics();
            text.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            text.setColor(new Color(36, 31, 49));
            text.setFont(new Font("Microsoft YaHei", Font.BOLD, 30));
            text.drawString("血腥星光收容器 · 底座显示切换", 45, 63);
            text.setFont(new Font("Microsoft YaHei", Font.PLAIN, 22));
            text.drawString("显示底座", 270, 700);
            text.drawString("隐藏底座 · 仅球体", 780, 700);
            text.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
            text.drawString("使用项目模型、贴图与 OpenGL 渲染器离屏生成；不是游戏场景截图。", 45, 755);
            text.dispose();
            File image = new File(project, "docs/images/bloody-starlight-container.png");
            image.getParentFile().mkdirs();
            ImageIO.write(output, "png", image);
            System.out.println("PASS: production GL renderer, full/empty containers, texture loading and depth-state restoration. " + image);
            buffer.destroy();
            System.exit(0);
        } catch (Throwable failure) {
            failure.printStackTrace();
            if (buffer != null) buffer.destroy();
            System.exit(1);
        }
    }

    private static void draw(NBTTagCompound data, double x, double scale, double time, boolean detailed, boolean hideBase) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, -0.04, 0);
        GlStateManager.rotate(16, 1, 0, 0);
        GlStateManager.rotate(-12, 0, 1, 0);
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.translate(-0.5, -0.5, -0.5);
        BloodyStarlightContainerVisual.render(data, time, 0, detailed, hideBase);
        GlStateManager.popMatrix();
    }
}
