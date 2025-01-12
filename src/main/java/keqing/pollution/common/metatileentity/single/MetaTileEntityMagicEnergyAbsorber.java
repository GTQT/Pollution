package keqing.pollution.common.metatileentity.single;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.ColourMultiplier;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import com.cleanroommc.groovyscript.compat.mods.botania.Botania;
import gregtech.api.GTValues;
import gregtech.api.capability.impl.AbstractRecipeLogic;
import gregtech.api.capability.impl.EnergyContainerHandler;
import gregtech.api.capability.impl.FuelRecipeLogic;
import gregtech.api.capability.impl.RecipeLogicEnergy;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.LabelWidget;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.TieredMetaTileEntity;
import gregtech.api.metatileentity.WorkableTieredMetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.util.GTUtility;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.SimpleSidedCubeRenderer;
import gregtech.client.utils.PipelineUtil;
import keqing.pollution.POConfig;
import keqing.pollution.client.textures.POTextures;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDragonEgg;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ArrayUtils;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.world.aura.AuraHandler;
import vazkii.botania.api.state.BotaniaStateProps;
import vazkii.botania.api.state.enums.PylonVariant;
import vazkii.botania.common.block.ModBlocks;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static java.lang.Math.pow;


public class MetaTileEntityMagicEnergyAbsorber extends TieredMetaTileEntity {

        public boolean isActive = false;
        public int OutPutTier=0;

        public MetaTileEntityMagicEnergyAbsorber(ResourceLocation metaTileEntityId,int tier) {
        super(metaTileEntityId, tier);
    }
        @Override
        public MetaTileEntity createMetaTileEntity(IGregTechTileEntity GregTechTileEntity) {
        return new MetaTileEntityMagicEnergyAbsorber(metaTileEntityId, getTier());
    }

        @Override
        public void update() {
            super.update();
            if (!getWorld().isRemote) {
                int XPos = this.getPos().getX();
                int YPos = this.getPos().getY();
                int ZPos = this.getPos().getZ();
                IBlockState state = getWorld().getBlockState(new BlockPos(XPos,YPos+1 ,ZPos));
                Block block=state.getBlock();
                int meta = block.getMetaFromState(state);
                if (block.equals(Blocks.DRAGON_EGG)){
                    OutPutTier=3;
                    isActive=true;
                } else if (block.equals(ModBlocks.pylon) && meta == 0) {
                    OutPutTier=3;
                    isActive=true;
                } else if (block.equals(ModBlocks.pylon) && meta == 2) {
                    OutPutTier=4;
                    isActive=true;
                } else if (block.equals(ModBlocks.gaiaHead)){
                    OutPutTier=5;
                    isActive=true;
                } else {OutPutTier=0; isActive=false;}
                if(isActive){
                    this.energyContainer.addEnergy(GTValues.V[OutPutTier]);
                }
            }
        }


        @Override
        public boolean isActive(){
            return this.isActive() && isActive;
        }


        public boolean getIsWeatherOrTerrainResistant () {
            return true;
        }
        public long getMaxInputOutputAmperage () {
            return 1L;
        }

        public boolean isEnergyEmitter () {
            return true;
        }

        public long getEnergyCapacity () {
            return (long) (8192 * pow(4, getTier()));
        }
        protected void reinitializeEnergyContainer () {
            super.reinitializeEnergyContainer();
            long tierVoltage = GTValues.V[this.getTier()];
            super.energyContainer = EnergyContainerHandler.emitterContainer(this, getEnergyCapacity(), tierVoltage, this.getMaxInputOutputAmperage());

        }


        public void addInformation (ItemStack stack, World player, List < String > tooltip,boolean advanced){
            String key = this.metaTileEntityId.getPath().split("\\.")[0];
            String mainKey = String.format("gregtech.machine.%s.tooltip", key);
            if (I18n.hasKey(mainKey)) {
                tooltip.add(1, I18n.format(mainKey, new Object[0]));
            }

            tooltip.add(I18n.format("gregtech.universal.tooltip.voltage_out", new Object[]{this.energyContainer.getOutputVoltage(), GTValues.VNF[this.getTier()]}));
            tooltip.add(I18n.format("gregtech.universal.tooltip.energy_storage_capacity", new Object[]{this.energyContainer.getEnergyCapacity()}));
            tooltip.add(I18n.format("pollution.universal.tooltip.pollution_magic_energy_absorber"));
        }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        IVertexOperation[] colouredPipeline = ArrayUtils.add(pipeline,
                new ColourMultiplier(GTUtility.convertRGBtoOpaqueRGBA_CL(getPaintingColorForRendering())));
        getRenderer().render(renderState, translation, colouredPipeline);
    }
    @SideOnly(Side.CLIENT)
    private ICubeRenderer getRenderer() {
        return isActive ? Textures.MAGIC_ENERGY_ABSORBER_ACTIVE : Textures.MAGIC_ENERGY_ABSORBER;
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected SimpleSidedCubeRenderer getBaseRenderer() {
        return POTextures.MAGIC_VOLTAGE_CASINGS[getTier()];
    }

}