package keqing.pollution.common.metatileentity.multiblock;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.GTValues;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.impl.EnergyContainerList;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.BlockMultiblockCasing;
import gregtech.common.blocks.MetaBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aura.AuraHelper;

import javax.annotation.Nonnull;
import java.util.List;

public class MetaTileEntityFluxClear extends MultiblockWithDisplayBase {
	private final double VisTicks;
	private final int tier;
	private final long energyAmountPer;
	private IEnergyContainer energyContainer;
	private boolean isWorkingEnabled;

	public MetaTileEntityFluxClear(ResourceLocation metaTileEntityId, int tier) {
		super(metaTileEntityId);
		this.tier = tier;
		this.VisTicks = (tier - 3) * 4 * 0.05;
		this.energyAmountPer = GTValues.V[tier];
	}

	@Override
	protected void formStructure(PatternMatchContext context) {
		super.formStructure(context);
		this.energyContainer = new EnergyContainerList(getAbilities(MultiblockAbility.INPUT_ENERGY));
	}

	int updateTime=0;
	@Override
	protected void updateFormedValid() {
		updateTime++;
		if(updateTime<200)return;
		updateTime=0;
		int aX = this.getPos().getX();
		int aY = this.getPos().getY();
		int aZ = this.getPos().getZ();
		for (int x = -tier; x <= tier; x++)
			for (int y = -tier; y <= tier; y++) {
				BlockPos pos = new BlockPos(aX + x * 16, aY, aZ + y * 16);
				if (AuraHelper.drainFlux(getWorld(), pos, (float) VisTicks, true) > 0.01) {
					if (!getWorld().isRemote && energyContainer.getEnergyStored() >= energyAmountPer) {
						energyContainer.removeEnergy(energyAmountPer);
						isWorkingEnabled = true;
						AuraHelper.drainFlux(getWorld(), pos, (float) VisTicks, false);
					} else isWorkingEnabled = false;
				}
			}
	}

	@Override
	public void addInformation(ItemStack stack, World player, List<String> tooltip, boolean advanced) {
		super.addInformation(stack, player, tooltip, advanced);
		tooltip.add(I18n.format("pollution.flux_clear.tire", tier, VisTicks));
		tooltip.add(I18n.format("pollution.flux_clear.tooltip"));
	}

	@Override
	protected void addDisplayText(List<ITextComponent> textList) {
		super.addDisplayText(textList);
		textList.add(new TextComponentTranslation("pollution.flux_clear.tire", tier, VisTicks));
		textList.add(new TextComponentTranslation("pollution.flux_clear.amount", tier));
	}

	@Nonnull
	@Override
	protected BlockPattern createStructurePattern() {
		return FactoryBlockPattern.start()
				.aisle("XXX", "XXX", "XXX", "AAA", "AAA")
				.aisle("XXX", "XXX", "XXX", "AXA", "AAA")
				.aisle("XXX", "XSX", "XXX", "AAA", "AAA")
				.where('S', selfPredicate())
				.where('A', states(getIntakeState()).addTooltips("gregtech.multiblock.pattern.clear_amount_1"))
				.where('X', states(getCasingAState()).setMinGlobalLimited(15)
						.or(abilities(MultiblockAbility.MAINTENANCE_HATCH).setExactLimit(1))
						.or(abilities(MultiblockAbility.INPUT_ENERGY).setExactLimit(1)))
				.where(' ', any())
				.build();
	}

	@SideOnly(Side.CLIENT)
	@Nonnull
	@Override
	protected ICubeRenderer getFrontOverlay() {
		return Textures.POWER_SUBSTATION_OVERLAY;
	}

	@Override
	public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
		super.renderMetaTileEntity(renderState, translation, pipeline);
		getFrontOverlay().renderOrientedState(renderState, translation, pipeline, getFrontFacing(), this.isActive(),
				this.isWorkingEnabled());
	}

	private boolean isWorkingEnabled() {
		return isWorkingEnabled;
	}

	public IBlockState getIntakeState() {
		return tier == GTValues.IV ? MetaBlocks.MULTIBLOCK_CASING.getState(BlockMultiblockCasing.MultiblockCasingType.EXTREME_ENGINE_INTAKE_CASING) :
				MetaBlocks.MULTIBLOCK_CASING.getState(BlockMultiblockCasing.MultiblockCasingType.ENGINE_INTAKE_CASING);
	}

	private IBlockState getCasingAState() {
		if (tier == GTValues.EV)
			return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.TITANIUM_STABLE);

		return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.TUNGSTENSTEEL_ROBUST);
	}

	@Override
	public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
		if (tier == GTValues.EV) return Textures.STABLE_TITANIUM_CASING;
		return Textures.ROBUST_TUNGSTENSTEEL_CASING;
	}

	@Override
	public boolean hasMufflerMechanics() {
		return false;
	}

	@Override
	public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
		return new MetaTileEntityFluxClear(metaTileEntityId, tier);
	}
}
