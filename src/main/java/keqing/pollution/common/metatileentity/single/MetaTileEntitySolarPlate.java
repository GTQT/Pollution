package keqing.pollution.common.metatileentity.single;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.TieredMetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

import static gregtech.api.GTValues.VA;
import static keqing.pollution.client.textures.POTextures.*;

public class MetaTileEntitySolarPlate extends TieredMetaTileEntity {
	int kind;
	protected final ICubeRenderer renderer;
	boolean isActive = true;
	boolean isWorkingEnabled = true;

	public MetaTileEntitySolarPlate(ResourceLocation metaTileEntityId, int tier, int kind, ICubeRenderer renderer) {
		super(metaTileEntityId, tier);
		this.kind = kind;
		this.renderer = renderer;
	}

	@Override
	public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
		return new MetaTileEntitySolarPlate(metaTileEntityId, getTier(), kind, renderer);
	}

	protected ModularUI createUI(EntityPlayer entityPlayer) {
		ModularUI.Builder builder = ModularUI.defaultBuilder();
		builder.dynamicLabel(7, 30, () -> {
			return "Solar Plate Tire:" + this.getTier();
		}, 2302755);
		builder.dynamicLabel(7, 50, () -> {
			return "Kind: " + kind;
		}, 2302755);
		builder.dynamicLabel(7, 70, () -> {
			return "Statue: " + checkNaturalLighting();
		}, 2302755);
		builder.dynamicLabel(7, 90, () -> {
			return "Booster: " + checkBooster(kind);
		}, 2302755);
		builder.dynamicLabel(7, 110, () -> {
			return "Generator: " + VA[getTier() + (checkBooster(kind) ? 1 : 0)] / 3 + "EU/t";
		}, 2302755);
		return builder.build(this.getHolder(), entityPlayer);
	}

	@Override
	public void update() {
		super.update();
		isActive = checkNaturalLighting();
		isWorkingEnabled = isActive;
		if (!getWorld().isRemote && isWorkingEnabled) {
			energyContainer.changeEnergy(VA[getTier() + (checkBooster(kind) ? 1 : 0)] / 3);
		}
	}

	private boolean checkBooster(int kind) {
		if (kind == 1) {
			return this.getPos().getY() > 160;
		}
		if (kind == 2) {
			return !this.getWorld().isDaytime();
		}
		if (kind == 3) {
			return this.getPos().getY() < 10;
		}
		if (kind == 4) {
			return this.getWorld().provider.getDimension() == -1;
		}
		if (kind == 5) {
			return this.getWorld().isDaytime();
		}
		return this.getWorld().getBlockState(this.getPos().add(0, -1, 0)) == Blocks.WATER.getDefaultState();
	}

	public void addInformation(ItemStack stack, World player, List<String> tooltip, boolean advanced) {
		String key = this.metaTileEntityId.getPath().split("\\.")[0];
		String mainKey = String.format("gregtech.machine.%s.tooltip", key);
		if (I18n.hasKey(mainKey)) {
			tooltip.add(1, I18n.format(mainKey));
		}
		tooltip.add(I18n.format("等级：%s 种类：%s", getTier(), kind));
		tooltip.add(I18n.format("期望发电：%s EU/t 满足增产条件后发电：%s EU/t", VA[getTier()] / 3, VA[getTier() + 1] / 3));
		if (kind == 1) tooltip.add(I18n.format("增产条件：高度大于160"));
		if (kind == 2) tooltip.add(I18n.format("增产条件：只能在夜晚工作"));
		if (kind == 3) tooltip.add(I18n.format("增产条件：高度小于10"));
		if (kind == 4) tooltip.add(I18n.format("增产条件：只能在地狱工作"));
		if (kind == 5) tooltip.add(I18n.format("增产条件：只能在白天工作"));
		if (kind == 6) tooltip.add(I18n.format("增产条件：正下方有水"));
	}

	@Override
	public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
		super.renderMetaTileEntity(renderState, translation, pipeline);
		this.renderOverlays(renderState, translation, pipeline);
		OVERLAY(kind).renderSided(EnumFacing.NORTH, renderState, translation, pipeline);
		OVERLAY(kind).renderSided(EnumFacing.SOUTH, renderState, translation, pipeline);
		OVERLAY(kind).renderSided(EnumFacing.EAST, renderState, translation, pipeline);
		OVERLAY(kind).renderSided(EnumFacing.WEST, renderState, translation, pipeline);

	}

	public SimpleOverlayRenderer OVERLAY(int kind) {
		if (kind == 1) return AIR;
		if (kind == 2) return DARK;
		if (kind == 3) return EARTH;
		if (kind == 4) return FIRE;
		if (kind == 5) return ORDER;
		return WATER;
	}


	protected void renderOverlays(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
		this.renderer.renderOrientedState(renderState, translation, pipeline, this.getFrontFacing(), isActive(), isWorkingEnabled());
	}


	public boolean isActive() {
		return isActive;
	}

	public boolean isWorkingEnabled() {
		return isWorkingEnabled;
	}

	public boolean checkNaturalLighting() {

		if (kind == 2||kind==4) return true;
		if (!this.getWorld().isDaytime())
			return false;
		if(kind==3)return true;
		for (BlockPos pos : BlockPos.getAllInBox(this.getPos().up(8).offset(this.frontFacing.rotateY(), 3),
				this.getPos().up(8).offset(this.getFrontFacing().rotateYCCW(), 3).offset(this.getFrontFacing().getOpposite(), 6))) {
			if (!this.getWorld().canSeeSky(pos.up())) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected boolean isEnergyEmitter() {
		return true;
	}

}