package keqing.pollution.common.metatileentity.multiblockpart;


import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregicality.multiblocks.api.render.GCYMTextures;
import gregtech.api.capability.GregtechTileCapabilities;
import gregtech.api.capability.IControllable;
import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import keqing.pollution.api.capability.ITankHatch;
import keqing.pollution.api.metatileentity.POMultiblockAbility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.tiles.essentia.TileJarFillable;

import java.util.List;

public class MetaTileEntityTankHatch extends MetaTileEntityMultiblockPart implements IMultiblockAbilityPart<ITankHatch>, ITankHatch {
	int tier;
	public Aspect al = null;
	public int storage = 0;
	String name = null;
	int aX = 0;
	int aY = 0;
	int aZ = 0;

	public void update() {
		super.update();
		this.aX = this.getPos().getX();
		this.aY = this.getPos().getY();
		this.aZ = this.getPos().getZ();
		if (this.getWorld().getTileEntity(this.getPos().add(0, 1, 0)) instanceof TileJarFillable) {
			TileJarFillable s = (TileJarFillable) this.getWorld().getTileEntity(this.getPos().add(0, 1, 0));
			this.al = s.getEssentiaType(s.getFacing());
			this.storage = s.getEssentiaAmount(s.getFacing());
			if (this.al != null) {
				this.name = this.al.getName();
			}
		}

	}

	public MetaTileEntityTankHatch(ResourceLocation metaTileEntityId, int tier) {
		super(metaTileEntityId, tier);
		this.tier = tier;
	}

	public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
		return new MetaTileEntityTankHatch(this.metaTileEntityId, this.getTier());
	}

	public void addInformation(ItemStack stack, World player, List<String> tooltip, boolean advanced) {
	}

	public int getTier() {
		return this.tier;
	}

	protected ModularUI createUI(EntityPlayer entityPlayer) {
		ModularUI.Builder builder = ModularUI.defaultBuilder();
		builder.dynamicLabel(7, 30, () -> {
			return "Tank Hatch";
		}, 2302755);
		builder.dynamicLabel(7, 50, () -> {
			return "Tier: " + this.getTier();
		}, 2302755);
		builder.dynamicLabel(7, 70, () -> {
			return "locate: " + this.aX + " / " + this.aY + " / " + this.aZ;
		}, 2302755);
		builder.dynamicLabel(7, 110, () -> {
			return "Infused: " + this.name + " Amount: " + this.storage;
		}, 2302755);
		return builder.build(this.getHolder(), entityPlayer);
	}

	public MultiblockAbility<ITankHatch> getAbility() {
		return POMultiblockAbility.TANK_HATCH;
	}

	public void registerAbilities(List<ITankHatch> list) {
		list.add(this);
	}

	public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
		super.renderMetaTileEntity(renderState, translation, pipeline);
		if (this.shouldRenderOverlay()) {
			OrientedOverlayRenderer overlayRenderer;
			if (this.getTier() <= 3) {
				overlayRenderer = GCYMTextures.PARALLEL_HATCH_MK1_OVERLAY;
			} else if (this.getTier() <= 7) {
				overlayRenderer = GCYMTextures.PARALLEL_HATCH_MK2_OVERLAY;
			} else if (this.getTier() <= 10) {
				overlayRenderer = GCYMTextures.PARALLEL_HATCH_MK3_OVERLAY;
			} else {
				overlayRenderer = GCYMTextures.PARALLEL_HATCH_MK4_OVERLAY;
			}

			if (this.getController() != null && this.getController() instanceof RecipeMapMultiblockController) {
				overlayRenderer.renderOrientedState(renderState, translation, pipeline, this.getFrontFacing(), this.getController().isActive(), ((IControllable) this.getController().getCapability(GregtechTileCapabilities.CAPABILITY_CONTROLLABLE, (EnumFacing) null)).isWorkingEnabled());
			} else {
				overlayRenderer.renderOrientedState(renderState, translation, pipeline, this.getFrontFacing(), false, false);
			}
		}

	}

	public boolean canPartShare() {
		return false;
	}

	public int getX() {
		return 0;
	}

	public int getY() {
		return 0;
	}

	public int getZ() {
		return 0;
	}
}
