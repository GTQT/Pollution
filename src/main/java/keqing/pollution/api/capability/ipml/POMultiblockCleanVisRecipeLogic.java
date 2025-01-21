package keqing.pollution.api.capability.ipml;

import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import keqing.pollution.api.capability.ICleanVis;
import net.minecraft.util.math.BlockPos;
import thaumcraft.api.aura.AuraHelper;

public class POMultiblockCleanVisRecipeLogic extends MultiblockRecipeLogic implements ICleanVis {

	RecipeMapMultiblockController tileEntity;

	public POMultiblockCleanVisRecipeLogic(RecipeMapMultiblockController tileEntity) {
		super(tileEntity);
		this.tileEntity=tileEntity;
	}

	@Override
	public boolean isCleanVis() {
		int aX = tileEntity.getPos().getX();
		int aY = tileEntity.getPos().getY();
		int aZ = tileEntity.getPos().getZ();
		BlockPos pos = new BlockPos(aX, aY, aZ);
		float fluxThisChunk = AuraHelper.getFlux(tileEntity.getWorld(), pos);
		float difference = AuraHelper.getVis(tileEntity.getWorld(), pos) - AuraHelper.getAuraBase(tileEntity.getWorld(), pos);
		return fluxThisChunk <= 4.2f && Math.abs(difference) <= 4.2f;
	}

}
