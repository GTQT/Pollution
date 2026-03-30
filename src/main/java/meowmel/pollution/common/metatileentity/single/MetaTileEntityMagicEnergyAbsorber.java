package meowmel.pollution.common.metatileentity.single;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.ColourMultiplier;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.GTValues;
import gregtech.api.capability.impl.EnergyContainerHandler;
import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.TieredMetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.util.GTUtility;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.SimpleSidedCubeRenderer;
import meowmel.pollution.client.textures.POTextures;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.common.block.ModBlocks;

import java.util.List;

import static gregtech.api.GTValues.V;
import static gregtech.api.GTValues.VOCNF;
import static gregtech.api.capability.GregtechDataCodes.IS_WORKING;
import static java.lang.Math.pow;


public class MetaTileEntityMagicEnergyAbsorber extends TieredMetaTileEntity {

    public boolean isActive = false;
    public int OutPutTier = 0;

    public MetaTileEntityMagicEnergyAbsorber(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId, tier);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity GregTechTileEntity) {
        return new MetaTileEntityMagicEnergyAbsorber(metaTileEntityId, getTier());
    }

    @SideOnly(Side.CLIENT)
    private ICubeRenderer getRenderer() {
        return isActive ? Textures.MAGIC_ENERGY_ABSORBER_ACTIVE : Textures.MAGIC_ENERGY_ABSORBER;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Pair<TextureAtlasSprite, Integer> getParticleTexture() {
        return Pair.of(getRenderer().getParticleSprite(), getPaintingColorForRendering());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        IVertexOperation[] colouredPipeline = ArrayUtils.add(pipeline,
                new ColourMultiplier(GTUtility.convertRGBtoOpaqueRGBA_CL(getPaintingColorForRendering())));
        getRenderer().render(renderState, translation, colouredPipeline);
    }

    private void setActive(boolean isActive) {
        if (this.isActive != isActive) {
            this.isActive = isActive;
            if (!getWorld().isRemote) {
                writeCustomData(IS_WORKING, w -> w.writeBoolean(isActive));
            }
        }
    }

    @Override
    public boolean isActive() {
        return this.isActive;
    }

    @Override
    public void receiveCustomData(int dataId, @NotNull PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if (dataId == IS_WORKING) {
            this.isActive = buf.readBoolean();
        }
    }

    @Override
    public void writeInitialSyncData(@NotNull PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeBoolean(isActive);
    }

    @Override
    public void receiveInitialSyncData(@NotNull PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        this.isActive = buf.readBoolean();
    }


    @Override
    protected boolean isEnergyEmitter() {
        return true;
    }

    @Override
    public void update() {
        super.update();
        if (!getWorld().isRemote) {
            int XPos = this.getPos().getX();
            int YPos = this.getPos().getY();
            int ZPos = this.getPos().getZ();
            IBlockState state = getWorld().getBlockState(new BlockPos(XPos, YPos + 1, ZPos));
            Block block = state.getBlock();
            int meta = block.getMetaFromState(state);
            if (block.equals(Blocks.DRAGON_EGG)) {
                OutPutTier = 3;
                setActive(true);
            } else if (block.equals(ModBlocks.pylon) && meta == 0) {
                OutPutTier = 3;
                setActive(true);
            } else if (block.equals(ModBlocks.pylon) && meta == 2) {
                OutPutTier = 4;
                setActive(true);
            } else if (block.equals(ModBlocks.gaiaHead)) {
                OutPutTier = 5;
                setActive(true);
            } else {
                OutPutTier = 0;
                setActive(false);
            }
            if (isActive) {
                this.energyContainer.addEnergy(GTValues.V[OutPutTier]);
            }
        }
    }

    @Override
    public void addInformation(ItemStack stack, World player, @NotNull List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("tooltip.ender_power_generator.voltage", V[getTier()], VOCNF[getTier()]));
        tooltip.add(I18n.format("pollution.machine.pollution_magic_energy_absorber.tooltip"));
        tooltip.add(I18n.format("tooltip.ender_power_generator.current_output", V[getTier()-1]));
    }

    @Override
    public void addToolUsages(ItemStack stack, @Nullable World world, List<String> tooltip, boolean advanced) {
        tooltip.add(I18n.format("gregtech.tool_action.screwdriver.access_covers"));
        tooltip.add(I18n.format("gregtech.tool_action.wrench.set_facing"));
        super.addToolUsages(stack, world, tooltip, advanced);
    }

    @Override
    protected boolean openGUIOnRightClick() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick() {
        if (isActive()) {
            final BlockPos pos = getPos();
            for (int i = 0; i < 4; i++) {
                getWorld().spawnParticle(EnumParticleTypes.PORTAL,
                        pos.getX() + 0.5F,
                        pos.getY() + GTValues.RNG.nextFloat(),
                        pos.getZ() + 0.5F,
                        (GTValues.RNG.nextFloat() - 0.5F) * 0.5F,
                        (GTValues.RNG.nextFloat() - 0.5F) * 0.5F,
                        (GTValues.RNG.nextFloat() - 0.5F) * 0.5F);
            }
        }
    }


    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        return null;
    }

}