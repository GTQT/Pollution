package meowmel.pollution.common.metatileentity.multiblock.bot;

import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.Widget;
import gregtech.api.gui.widgets.ClickButtonWidget;
import gregtech.api.gui.widgets.WidgetGroup;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.*;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.util.GTUtility;
import gregtech.client.renderer.ICubeRenderer;
import meowmel.pollution.api.capability.ipml.ManaHandlerList;
import meowmel.pollution.api.metatileentity.POMultiblockAbility;
import meowmel.pollution.client.textures.POTextures;
import meowmel.pollution.common.block.PollutionMetaBlocks;
import meowmel.pollution.common.block.metablocks.POManaPlate;
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
import java.util.ArrayList;
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
        if (work(true)) {
            final int xDir = this.getFrontFacing().getOpposite().getXOffset() * 5;
            final int zDir = this.getFrontFacing().getOpposite().getZOffset() * 5;
            for (int x = -5; x <= 5; ++x) {
                for (int y = -5; y <= 5; ++y) {
                    BlockPos poss = this.getPos().add(xDir + x, 0, zDir + y);
                    if (GTUtility.getMetaTileEntity(this.getWorld(), poss.add(0, 1, 0)) instanceof MetaTileEntity) {

                        MetaTileEntity mte = GTUtility.getMetaTileEntity(this.getWorld(), poss.add(0, 1, 0));
                        TileEntity te = this.getWorld().getTileEntity(poss.add(0, 1, 0));

                        if(mte!=null) {
                            for (int i = 0; i < speed; i++) {
                                mte.update();
                            }
                            work(false);
                            continue;
                        }

                        if(te instanceof ITickable iTickable) {
                            for (int i = 0; i < speed; i++) {
                                iTickable.update();
                            }
                            work(false);
                        }
                    }
                }
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
                        .or(abilities(POMultiblockAbility.MANA_INPUT_POOL).setExactLimit(1)))
                .build();
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        super.addDisplayText(textList);
        if (isStructureFormed()) {
            textList.add(new TextComponentTranslation("等级: %s |魔力： %s / %s", this.manaHandler.getTier(), this.manaHandler.getMana(), this.manaHandler.getMaxMana()));
            textList.add(new TextComponentTranslation("加速速率: %s |魔力消耗： %s", speed, Math.pow(2, speed - 1)));
        }
    }

    ManaHandlerList manaHandler;

    @Override
    protected void initializeAbilities() {
        super.initializeAbilities();
        manaHandler = new ManaHandlerList(getAbilities(POMultiblockAbility.MANA_INPUT_POOL));
    }

    @Override
    protected void resetTileAbilities() {
        super.resetTileAbilities();
        manaHandler = new ManaHandlerList(new ArrayList<>());
    }


    private boolean consumeMana(long amount,boolean simulate) {
        return this.manaHandler.consumeMana(amount,simulate);
    }

    public int getTier() {
        return this.manaHandler.getTier();
    }


    public boolean work(boolean simulate) {
        return this.consumeMana((long) Math.pow(2, speed - 1), simulate);
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
