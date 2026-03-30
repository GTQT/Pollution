package meowmel.pollution.common.block.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class TileEntityMagicCircle extends TileEntity {
	private static ResourceLocation rl = null;
	private int speed = 1;
	private boolean canRotation = true;
	private float scala = 2.5f;


	public ResourceLocation getRl() {
		return rl;
	}

	public void setRl(ResourceLocation rl) {
		TileEntityMagicCircle.rl = rl;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public boolean isCanRotation() {
		return canRotation;
	}

	public void setCanRotation(boolean canRotation) {
		this.canRotation = canRotation;
	}

	public float getScala() {
		return scala;
	}

	public void setScala(float scala) {
		this.scala = scala;
	}

	public void setProperty(ResourceLocation rl, int speed, boolean canRotation, float scala) {
		this.canRotation = canRotation;
		this.rl = rl;
		this.speed = speed;
		this.scala = scala;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (compound.hasKey("canRotation"))
			this.canRotation = compound.getBoolean("canRotation");
		if (compound.hasKey("speed"))
			this.speed = compound.getInteger("speed");
		if (compound.hasKey("rl"))
			this.rl = new ResourceLocation(compound.getString("rl"));
		if (compound.hasKey("scala"))
			this.scala = compound.getFloat("scala");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setBoolean("canRotation", canRotation);
		compound.setInteger("speed", speed);
		compound.setString("rl", rl.toString());
		compound.setFloat("scala", this.scala);
		return compound;
	}
}
