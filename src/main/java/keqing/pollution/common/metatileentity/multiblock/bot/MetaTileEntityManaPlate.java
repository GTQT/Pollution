package keqing.pollution.common.metatileentity.multiblock.bot;

import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.Widget;
import gregtech.api.gui.widgets.ClickButtonWidget;
import gregtech.api.gui.widgets.WidgetGroup;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockControllerBase;
import gregtech.api.metatileentity.multiblock.RecipeMapSteamMultiblockController;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.GTUtility;
import gregtech.client.renderer.ICubeRenderer;
import keqing.gtqtcore.api.metaileentity.MetaTileEntityBaseWithControl;
import keqing.pollution.api.metatileentity.POMultiblockAbility;
import keqing.pollution.client.textures.POTextures;
import keqing.pollution.common.block.PollutionMetaBlocks;
import keqing.pollution.common.block.metablocks.POManaPlate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;


public class MetaTileEntityManaPlate extends MetaTileEntityBaseWithControl {
    @Override
    public boolean usesMui2() {
        return false;
    }
    int speed = 0;
    int speedMax = 1;

    public MetaTileEntityManaPlate(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }


    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityManaPlate(metaTileEntityId);
    }

    @Override
    public List<ITextComponent> getDataInfo() {
        return Collections.emptyList();
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        speedMax = getTier();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        data.setInteger("speed", this.speed);
        return super.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.speed = data.getInteger("speed");
    }

    @Override
    @Nonnull
    protected Widget getFlexButton(int x, int y, int width, int height) {
        WidgetGroup group = new WidgetGroup(x, y, width, height);
        group.addWidget(new ClickButtonWidget(0, 0, 9, 18, "", this::decrementThreshold)
                .setButtonTexture(GuiTextures.BUTTON_THROTTLE_MINUS)
                .setTooltipText("speed down"));
        group.addWidget(new ClickButtonWidget(9, 0, 9, 18, "", this::incrementThreshold)
                .setButtonTexture(GuiTextures.BUTTON_THROTTLE_PLUS)
                .setTooltipText("speed up"));
        return group;
    }

    private void incrementThreshold(Widget.ClickData clickData) {
        this.speed = MathHelper.clamp(speed + 1, 0, speedMax);
    }

    private void decrementThreshold(Widget.ClickData clickData) {
        this.speed = MathHelper.clamp(speed - 1, 0, speedMax);
    }

    public int getRapid() {
        return speed;
    }


    @Override
    protected void updateFormedValid() {
        if (speed == 0) return;
        final int xDir = this.getFrontFacing().getOpposite().getXOffset() * 5;
        final int zDir = this.getFrontFacing().getOpposite().getZOffset() * 5;
        for (int x = -5; x <= 5; ++x) {
            for (int y = -5; y <= 5; ++y) {
                BlockPos poss = this.getPos().add(xDir + x, 0, zDir + y);
                if (GTUtility.getMetaTileEntity(this.getWorld(), poss.add(0, 1, 0)) instanceof MetaTileEntity) {

                    MetaTileEntity mte = GTUtility.getMetaTileEntity(this.getWorld(), poss.add(0, 1, 0));
                    TileEntity te = this.getWorld().getTileEntity(poss.add(0, 1, 0));

                    if (mte.isActive() && !work()) return;
                    //多方块
                    if (mte instanceof MultiblockControllerBase mcb) {
                        if (mcb.isStructureFormed() && mcb.isValid()) {
                            final var inenergy = mcb.getAbilities(MultiblockAbility.INPUT_ENERGY);
                            if (inenergy.size() > 0) {
                                long[] energys = new long[inenergy.size()];
                                for (int j = 0; j < inenergy.size(); j++) {
                                    energys[j] = inenergy.get(j).getEnergyStored();
                                }
                                if (te instanceof ITickable) {
                                    for (int i = 0; i < getRapid(); i++) {
                                        ((ITickable) te).update();
                                        for (int j = 0; j < inenergy.size(); j++) {
                                            if (inenergy.get(j).getEnergyStored() < energys[j])
                                                inenergy.get(j).addEnergy(energys[j] - inenergy.get(j).getEnergyStored());
                                        }
                                    }
                                }
                            } else if (mte instanceof RecipeMapSteamMultiblockController smte) {
                                final var fin = smte.getSteamFluidTank();
                                int[] energys = new int[fin.getTanks()];
                                for (int j = 0; j < fin.getTanks(); j++) {
                                    energys[j] = fin.getTankAt(j).getFluidAmount();
                                }
                                if (te instanceof ITickable) {
                                    for (int i = 0; i < getRapid(); i++) {
                                        ((ITickable) te).update();
                                        for (int j = 0; j < fin.getTanks(); j++) {
                                            if (fin.getTankAt(j).getFluidAmount() < energys[j]) {
                                                fin.getTankAt(j).fill(Materials.Steam.getFluid(energys[j] - fin.getTankAt(j).getFluidAmount()), true);
                                            }

                                        }
                                    }
                                }
                            } else {
                                for (int i = 0; i < getRapid(); i++) {
                                    ((ITickable) te).update();
                                }
                                ((ITickable) te).update();
                            }
                        }
                    } else {
                        long cache = 0;
                        for (EnumFacing facing : EnumFacing.VALUES) {
                            if (mte.getCapability(GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER, facing) instanceof IEnergyContainer) {
                                IEnergyContainer container = mte.getCapability(GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER, facing);
                                cache = container.getEnergyStored();
                            }
                        }
                        if (te instanceof ITickable tickable) {
                            for (int i = 0; i < getRapid(); i++) {
                                tickable.update();
                                addEnergy(this.getWorld(), poss.add(0, 1, 0), cache);
                            }
                        }
                    }
                }
            }
        }
    }

    public void addEnergy(World world, BlockPos pos, long cache) {
        MetaTileEntity mte = GTUtility.getMetaTileEntity(world, pos);
        for (EnumFacing facing : EnumFacing.VALUES) {
            if (mte.getCapability(GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER, facing) instanceof IEnergyContainer) {
                IEnergyContainer container1 = mte.getCapability(GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER, facing);
                if (cache > container1.getEnergyStored())
                    container1.changeEnergy(cache - container1.getEnergyStored());
            }
        }

    }

    @Nonnull
    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("CCCCCCCCCCC")
                .aisle("CCCCCCCCCCC")
                .aisle("CCCCCCCCCCC")
                .aisle("CCCCCCCCCCC")
                .aisle("CCCCCCCCCCC")
                .aisle("CCCCCCCCCCC")
                .aisle("CCCCCCCCCCC")
                .aisle("CCCCCCCCCCC")
                .aisle("CCCCCCCCCCC")
                .aisle("CCCCCCCCCCC")
                .aisle("CCCCCSCCCCC")
                .where('S', selfPredicate())
                .where(' ', any())
                .where('C', states(getCasingAState())
                        .or(abilities(POMultiblockAbility.MANA_HATCH).setExactLimit(1)))
                .build();
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        super.addDisplayText(textList);
        if (isStructureFormed()) {
            textList.add(new TextComponentTranslation("等级: %s |魔力： %s / %s", this.getAbilities(POMultiblockAbility.MANA_HATCH).get(0).getTier(), this.getAbilities(POMultiblockAbility.MANA_HATCH).get(0).getMana(), this.getAbilities(POMultiblockAbility.MANA_HATCH).get(0).getMaxMana()));
            textList.add(new TextComponentTranslation("加速速率: %s |魔力消耗： %s", speed, Math.pow(2, speed - 1)));
        }
    }


    public int getTier() {
        return this.getAbilities(POMultiblockAbility.MANA_HATCH).get(0).getTier();
    }


    public boolean work() {
        return this.getAbilities(POMultiblockAbility.MANA_HATCH).get(0).consumeMana((int) Math.pow(2, speed - 1), false);
    }

    private IBlockState getCasingAState() {
        return PollutionMetaBlocks.MANA_PLATE.getState(POManaPlate.ManaBlockType.MANA_BASIC);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return POTextures.MANA_BASIC;
    }

    @Override
    public boolean hasMufflerMechanics() {
        return false;
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    @Override
    public boolean shouldShowVoidingModeButton() {
        return false;
    }

}
