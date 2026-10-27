import com.google.gson.*;
import com.mojang.authlib.properties.PropertyMap;
import meowmel.pollution.client.tesr.BloodyBloodExtractorVisual;
import meowmel.pollution.client.tesr.BloodyStarlightContainerVisual;
import meowmel.pollution.common.entity.dragon.*;
import meowmel.pollution.common.metatileentity.multiblock.bloodMagic.BloodyBloodExtractorShape;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.*;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraft.world.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Proxy;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** Source-model layout preview plus the real orb/dragon/optics renderers. Not a world screenshot. */
public final class DragonBloodExtractorRenderSmoke {
    private static Minecraft mc;
    private static JsonObject sealed, glass, metal;
    public static void main(String[] args) {
        Pbuffer context = null;
        try {
            File project = new File(args[0]);
            int width = 1000, height = 1200;
            context = new Pbuffer(width, height, new PixelFormat().withDepthBits(24), null);
            context.makeCurrent();
            mc = new Minecraft(new GameConfiguration(
                    new GameConfiguration.UserInformation(new Session("ExtractorRender", "0", "", "legacy"),
                            new PropertyMap(), new PropertyMap(), Proxy.NO_PROXY),
                    new GameConfiguration.DisplayInformation(width, height, false, true),
                    new GameConfiguration.FolderInformation(new File(project, "build/extractor-render"),
                            new File(project, "src/main/resources"), new File(project, "build/resources/patchedMc"), null),
                    new GameConfiguration.GameInformation(false, "1.12.2", "release"),
                    new GameConfiguration.ServerInformation(null, 0)));
            mc.gameSettings = new GameSettings();
            OpenGlHelper.initializeTextures();
            SimpleReloadableResourceManager resources = new SimpleReloadableResourceManager(new MetadataSerializer());
            resources.reloadResources(Arrays.asList(new FolderResourcePack(new File(project, "build/resources/patchedMc")),
                    new FolderResourcePack(new File(project, "src/main/resources"))));
            mc.renderEngine = new TextureManager(resources);
            mc.world = new WorldClient(null, new WorldSettings(0, GameType.CREATIVE, false, false, WorldType.FLAT),
                    0, EnumDifficulty.PEACEFUL, new Profiler());
            sealed = model(project, "dragon_blood_extraction_casing");
            JsonObject legacySealed = model(project, "blood_extractor_sealed_casing");
            if (!legacySealed.get("textures").equals(sealed.get("textures"))
                    || !legacySealed.get("elements").equals(sealed.get("elements")))
                throw new AssertionError("Legacy bloody starstream casing must use the redrawn dragon-blood material");
            glass = model(project, "blood_extractor_cylinder_glass");
            metal = new JsonParser().parse("{\"textures\":{\"all\":\"minecraft:blocks/iron_block\"},\"elements\":[{\"from\":[0,0,0],\"to\":[16,16,16],\"faces\":{\"north\":{\"texture\":\"#all\"},\"south\":{\"texture\":\"#all\"},\"west\":{\"texture\":\"#all\"},\"east\":{\"texture\":\"#all\"},\"up\":{\"texture\":\"#all\"},\"down\":{\"texture\":\"#all\"}}}]}").getAsJsonObject();
            // Validate every referenced texture before drawing; missing textures must fail this check.
            for (JsonObject model : Arrays.asList(sealed, legacySealed, glass, metal,
                    model(project, "dragon_incubator_casing"), model(project, "dragon_incubator_runed_casing"))) {
                for (Map.Entry<String, JsonElement> texture : model.getAsJsonObject("textures").entrySet()) {
                    ResourceLocation id = textureId(texture.getValue().getAsString());
                    resources.getResource(id).getInputStream().close();
                    if (id.getPath().startsWith("textures/blocks/dragon_machines/")) {
                        BufferedImage tile;
                        try (InputStream stream = resources.getResource(id).getInputStream()) { tile = ImageIO.read(stream); }
                        if (tile == null || tile.getWidth() != 64 || tile.getHeight() != 64)
                            throw new AssertionError("Casing texture must be 64x64: " + id);
                        for (int y = 0; y < 64; y++) for (int x = 0; x < 64; x++)
                            if ((tile.getRGB(x, y) >>> 24) != 255) throw new AssertionError("Opaque casing has transparent pixels: " + id);
                    }
                }
            }
            GlStateManager.viewport(0, 0, width, height);
            GlStateManager.matrixMode(GL11.GL_PROJECTION);
            GlStateManager.loadIdentity();
            GlStateManager.ortho(-6, 6, -1.5, 15.7, -30, 30);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.loadIdentity();
            GlStateManager.enableDepth();
            GlStateManager.clearColor(0.07F, 0.085F, 0.13F, 1);
            GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            GlStateManager.translate(0, 6, 0);
            GlStateManager.rotate(12, 1, 0, 0);
            GlStateManager.rotate(-10, 0, 1, 0);
            GlStateManager.translate(0, -6, 0);
            GlStateManager.disableLighting();
            GlStateManager.enableTexture2D();
            GlStateManager.enableCull();
            GlStateManager.color(1, 1, 1, 1);
            layout(false);
            EntityPollutionDragon dragon = new EntityPollutionDragon(mc.world);
            dragon.setStage(DragonLifeStage.ADULT);
            NBTTagCompound payload = DragonContainmentData.capture(dragon);
            for (int i = 0; i < 10; i++) {
                net.minecraft.util.math.BlockPos pos = BloodyBloodExtractorShape.slot(i);
                GlStateManager.pushMatrix();
                GlStateManager.translate(pos.getX() - 0.5, pos.getY(), pos.getZ() - 0.5);
                BloodyStarlightContainerVisual.render(payload, 60 + i * 8, 0, true);
                GlStateManager.popMatrix();
            }
            boolean texture = GL11.glIsEnabled(GL11.GL_TEXTURE_2D), blend = GL11.glIsEnabled(GL11.GL_BLEND);
            int src = GL11.glGetInteger(GL11.GL_BLEND_SRC), dst = GL11.glGetInteger(GL11.GL_BLEND_DST);
            BloodyBloodExtractorVisual.renderOptics(55, 8000, 1023, true, 70);
            if (!GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK)
                    || texture != GL11.glIsEnabled(GL11.GL_TEXTURE_2D) || blend != GL11.glIsEnabled(GL11.GL_BLEND)
                    || src != GL11.glGetInteger(GL11.GL_BLEND_SRC) || dst != GL11.glGetInteger(GL11.GL_BLEND_DST))
                throw new AssertionError("Extractor renderer leaked GL state");
            layout(true);
            int error = GL11.glGetError();
            if (error != GL11.GL_NO_ERROR) throw new AssertionError("GL error " + error);
            ByteBuffer pixels = BufferUtils.createByteBuffer(width * height * 4);
            GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);
            BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int y = 0; y < height; y++) for (int x = 0; x < width; x++) {
                int p = (y * width + x) * 4;
                result.setRGB(x, height - 1 - y, (pixels.get(p) & 255) << 16 | (pixels.get(p + 1) & 255) << 8 | pixels.get(p + 2) & 255);
            }
            Graphics2D text = result.createGraphics();
            text.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            text.setColor(new Color(245, 223, 233));
            text.setFont(new Font("Microsoft YaHei", Font.BOLD, 30));
            text.drawString("血腥血液提取器 · UHV", 40, 55);
            text.setFont(new Font("Microsoft YaHei", Font.PLAIN, 19));
            text.drawString("7×13×7 针筒结构 · 十个实际收容位", 40, 91);
            text.drawString("满载：5,000 mB/批 · 基础 5 秒 · 双加速 1.25 秒 · 1,835,008 EU/t", 40, 1128);
            text.setFont(new Font("Microsoft YaHei", Font.PLAIN, 15));
            text.drawString("源模型结构预览与实际球体／光效渲染；针管和活塞杆简化。非游戏场景截图。", 40, 1165);
            text.dispose();
            File image = new File(project, "docs/images/bloody-blood-extractor.png");
            image.getParentFile().mkdirs();
            ImageIO.write(result, "png", image);
            casingPreview(project);
            System.out.println("PASS: extractor optics GL/state restoration, source model textures, ten containers. " + image);
            context.destroy();
            System.exit(0);
        } catch (Throwable failure) { failure.printStackTrace(); if (context != null) context.destroy(); System.exit(1); }
    }
    private static JsonObject model(File project, String name) throws IOException {
        try (Reader input = new InputStreamReader(new FileInputStream(new File(project,
                "src/main/resources/assets/pollution/models/block/" + name + ".json")), StandardCharsets.UTF_8)) {
            JsonObject result = new JsonParser().parse(input).getAsJsonObject();
            if (result.has("parent") && result.get("parent").getAsString().startsWith("pollution:block/")) {
                JsonObject parent = model(project, result.get("parent").getAsString().substring("pollution:block/".length()));
                JsonObject textures = new JsonObject();
                for (Map.Entry<String, JsonElement> entry : parent.getAsJsonObject("textures").entrySet())
                    textures.add(entry.getKey(), entry.getValue());
                for (Map.Entry<String, JsonElement> entry : result.getAsJsonObject("textures").entrySet())
                    textures.add(entry.getKey(), entry.getValue());
                result.add("textures", textures);
                if (!result.has("elements")) result.add("elements", parent.get("elements"));
            }
            return result;
        }
    }
    private static ResourceLocation textureId(String name) {
        ResourceLocation id = new ResourceLocation(name);
        return new ResourceLocation(id.getNamespace(), "textures/" + id.getPath() + ".png");
    }
    private static void layout(boolean transparent) {
        GlStateManager.enableTexture2D();
        GlStateManager.color(1, 1, 1, 1);
        if (transparent) {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.depthMask(false);
        }
        String[][] aisles = BloodyBloodExtractorShape.createAisles();
        for (int z = 0; z < 7; z++) for (int y = 0; y < 13; y++) for (int x = 0; x < 7; x++) {
            char c = aisles[z][y].charAt(x);
            if (transparent ? c != 'G' : "XHSPN".indexOf(c) < 0) continue;
            GlStateManager.pushMatrix();
            GlStateManager.translate(x - 3.5, y, z - 3.5);
            draw(c == 'G' ? glass : c == 'P' || c == 'N' ? metal : sealed);
            GlStateManager.popMatrix();
        }
        GlStateManager.depthMask(true);
    }
    private static void casingPreview(File project) throws IOException {
        int width = 1000, height = 600;
        GlStateManager.viewport(0, 0, width, height);
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(-3.7, 3.7, -1.8, 2.5, -10, 10);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.loadIdentity();
        GlStateManager.clearColor(0.07F, 0.085F, 0.13F, 1);
        GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GlStateManager.enableCull();
        String[] models = {"dragon_blood_extraction_casing", "dragon_incubator_casing", "dragon_incubator_runed_casing"};
        for (int i = 0; i < models.length; i++) {
            GlStateManager.pushMatrix();
            GlStateManager.translate((i - 1) * 2.3, 0.2, 0);
            GlStateManager.rotate(22, 1, 0, 0);
            GlStateManager.rotate(-32, 0, 1, 0);
            GlStateManager.scale(1.2, 1.2, 1.2);
            GlStateManager.translate(-0.5, -0.5, -0.5);
            draw(model(project, models[i]), true);
            GlStateManager.popMatrix();
        }
        if (GL11.glGetError() != GL11.GL_NO_ERROR) throw new AssertionError("Casing preview GL error");
        ByteBuffer pixels = BufferUtils.createByteBuffer(width * height * 4);
        GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) for (int x = 0; x < width; x++) {
            int p = (y * width + x) * 4;
            output.setRGB(x, height - 1 - y, (pixels.get(p) & 255) << 16 | (pixels.get(p + 1) & 255) << 8 | pixels.get(p + 2) & 255);
        }
        Graphics2D labels = output.createGraphics();
        labels.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        labels.setColor(new Color(245, 223, 233));
        labels.setFont(new Font("Microsoft YaHei", Font.BOLD, 28));
        labels.drawString("龙系机器专用外壳 · 重绘材质", 40, 55);
        labels.setFont(new Font("Microsoft YaHei", Font.PLAIN, 19));
        labels.drawString("龙血提取密封外壳", 100, 465);
        labels.drawString("生息孵化外壳", 435, 465);
        labels.drawString("生息符文外壳", 745, 465);
        labels.setFont(new Font("Microsoft YaHei", Font.PLAIN, 15));
        labels.drawString("使用实际 64×64 PNG 与模型离屏绘制；不是游戏场景截图。", 40, 555);
        labels.dispose();
        ImageIO.write(output, "png", new File(project, "docs/images/dragon-casings.png"));
    }
    private static void draw(JsonObject model) { draw(model, false); }
    private static void draw(JsonObject model, boolean shaded) {
        JsonObject textures = model.getAsJsonObject("textures");
        for (JsonElement element : model.getAsJsonArray("elements")) {
            JsonObject cube = element.getAsJsonObject();
            double x0 = cube.getAsJsonArray("from").get(0).getAsDouble() / 16, y0 = cube.getAsJsonArray("from").get(1).getAsDouble() / 16;
            double z0 = cube.getAsJsonArray("from").get(2).getAsDouble() / 16, x1 = cube.getAsJsonArray("to").get(0).getAsDouble() / 16;
            double y1 = cube.getAsJsonArray("to").get(1).getAsDouble() / 16, z1 = cube.getAsJsonArray("to").get(2).getAsDouble() / 16;
            for (Map.Entry<String, JsonElement> face : cube.getAsJsonObject("faces").entrySet()) {
                String name = face.getValue().getAsJsonObject().get("texture").getAsString().substring(1);
                mc.renderEngine.bindTexture(textureId(textures.get(name).getAsString()));
                float shade = !shaded || face.getKey().equals("up") ? 1.0F
                        : face.getKey().equals("down") ? 0.5F
                        : face.getKey().equals("east") || face.getKey().equals("west") ? 0.65F : 0.85F;
                GlStateManager.color(shade, shade, shade, 1);
                double[][] vertices;
                switch (face.getKey()) {
                    case "north": vertices = new double[][]{{x0,y1,z0},{x1,y1,z0},{x1,y0,z0},{x0,y0,z0}}; break;
                    case "south": vertices = new double[][]{{x0,y0,z1},{x1,y0,z1},{x1,y1,z1},{x0,y1,z1}}; break;
                    case "west": vertices = new double[][]{{x0,y0,z0},{x0,y0,z1},{x0,y1,z1},{x0,y1,z0}}; break;
                    case "east": vertices = new double[][]{{x1,y0,z1},{x1,y0,z0},{x1,y1,z0},{x1,y1,z1}}; break;
                    case "up": vertices = new double[][]{{x0,y1,z0},{x0,y1,z1},{x1,y1,z1},{x1,y1,z0}}; break;
                    default: vertices = new double[][]{{x0,y0,z1},{x0,y0,z0},{x1,y0,z0},{x1,y0,z1}};
                }
                GL11.glBegin(GL11.GL_QUADS);
                for (int v = 0; v < 4; v++) {
                    GL11.glTexCoord2d(v == 1 || v == 2 ? 1 : 0, v >= 2 ? 1 : 0);
                    GL11.glVertex3d(vertices[v][0], vertices[v][1], vertices[v][2]);
                }
                GL11.glEnd();
            }
        }
        GlStateManager.color(1, 1, 1, 1);
    }
}
