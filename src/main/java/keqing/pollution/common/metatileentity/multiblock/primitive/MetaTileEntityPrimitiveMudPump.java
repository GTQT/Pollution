package keqing.pollution.common.metatileentity.multiblock.primitive;

import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.IPrimitivePump;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockControllerBase;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.LocalizationUtils;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockSteamCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.metatileentities.MetaTileEntities;

import keqing.pollution.api.unification.PollutionMaterials;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class MetaTileEntityPrimitiveMudPump extends MultiblockControllerBase implements IPrimitivePump {

    private IFluidTank waterTank;
    private int hatchModifier = 0;

    public MetaTileEntityPrimitiveMudPump(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
        resetTileAbilities();
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityPrimitiveMudPump(metaTileEntityId);
    }

    @Override
    public void update() {
        super.update();
        if (!getWorld().isRemote && getOffsetTimer() % 20 == 0 && isStructureFormed()&&getWorld().provider.getDimension() == 20) {
            waterTank.fill(PollutionMaterials.Mud.getFluid(getFluidProduction()), true);
        }
    }

    @Override
    protected boolean openGUIOnRightClick() {
        return false;
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        return null;
    }

    @Override
    protected void updateFormedValid() {}

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        initializeAbilities();
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        resetTileAbilities();
    }

    private void initializeAbilities() {
        List<IFluidTank> tanks = getAbilities(MultiblockAbility.PUMP_FLUID_HATCH);
        if (tanks == null || tanks.size() == 0) {
            tanks = getAbilities(MultiblockAbility.EXPORT_FLUIDS);
            this.hatchModifier = tanks.get(0).getCapacity() == 8000 ? 2 : 4;
        } else {
            this.hatchModifier = 1;
        }
        this.waterTank = tanks.get(0);
    }

    private void resetTileAbilities() {
        this.waterTank = new FluidTank(0);
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("F*F","F*F","F*F", "XXX", "XXX")
                .aisle("***","***","*H*", "XXX", "XXX")
                .aisle("F*F","F*F","F*F", "XSX", "XXX")
                .where('S', selfPredicate())
                .where('X', states(MetaBlocks.STEAM_CASING.getState(BlockSteamCasing.SteamCasingType.PUMP_DECK)))
                .where('F', frames(Materials.TreatedWood))
                .where('H',
                        abilities(MultiblockAbility.PUMP_FLUID_HATCH).or(metaTileEntities(
                                MetaTileEntities.FLUID_EXPORT_HATCH[0], MetaTileEntities.FLUID_EXPORT_HATCH[1])))
                .where('*', any())
                .build();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return Textures.PRIMITIVE_PUMP;
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.PRIMITIVE_PUMP_OVERLAY;
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        this.getFrontOverlay().renderOrientedState(renderState, translation, pipeline, getFrontFacing(), true, true);
    }

    @Override
    public String[] getDescription() {
        List<String> list = new ArrayList<>();
        list.add(I18n.format("gregtech.multiblock.primitive_water_pump.description"));
        Collections.addAll(list, LocalizationUtils.formatLines("只要在交错就会输出基础倍率为 100L 的 沼泽泥浆"));
        Collections.addAll(list, LocalizationUtils.formatLines("gregtech.multiblock.primitive_water_pump.extra2"));
        return list.toArray(new String[0]);
    }

    private boolean isRainingInBiome() {
        World world = getWorld();
        if (!world.isRaining()) {
            return false;
        }
        return world.getBiome(getPos()).canRain();
    }

    @Override
    public int getFluidProduction() {
        int biomeModifier = 100;
        return (int) (biomeModifier * hatchModifier * (isRainingInBiome() ? 1.5 : 1));
    }

    @Override
    public boolean allowsExtendedFacing() {
        return false;
    }
}