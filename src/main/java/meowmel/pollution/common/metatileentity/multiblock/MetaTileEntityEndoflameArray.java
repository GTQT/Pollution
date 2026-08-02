package meowmel.pollution.common.metatileentity.multiblock;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MetaTileEntityBaseWithControl;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.ui.KeyManager;
import gregtech.api.metatileentity.multiblock.ui.MultiblockUIBuilder;
import gregtech.api.metatileentity.multiblock.ui.UISyncer;
import gregtech.api.pattern.casing.DeclarativePatternBuilder;
import gregtech.api.pattern.element.Elements;
import gregtech.api.pattern.element.StructureDefinition;
import gregtech.api.util.KeyUtil;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.common.blocks.MetaBlocks;
import meowmel.pollution.api.capability.ipml.ManaHandlerList;
import meowmel.pollution.api.metatileentity.POMultiblockAbility;
import meowmel.pollution.api.unification.PollutionMaterials;
import meowmel.pollution.client.textures.POTextures;
import meowmel.pollution.common.block.PollutionMetaBlocks;
import meowmel.pollution.common.block.metablocks.POBotBlock;
import meowmel.pollution.common.block.metablocks.POGlass;
import meowmel.pollution.common.block.metablocks.POMBeamCore;
import meowmel.pollution.common.metatileentity.multiblockpart.MetaTileEntityManaHatch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import vazkii.botania.api.state.BotaniaStateProps;
import vazkii.botania.api.state.enums.PylonVariant;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.item.block.ItemBlockSpecialFlower;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class MetaTileEntityEndoflameArray extends MetaTileEntityBaseWithControl {

    private static final String NBT_FUEL_CACHE = "FuelBurnTime";

    private final StructureDefinition STRUCTURE_DEFINITION = StructureDefinition.getOrBuild(
            "pollution:endoflame_array", () -> {
                DeclarativePatternBuilder builder = DeclarativePatternBuilder.start()
                        .aisle("AB   BA", " ABBBA ", "       ", "       ", "       ", "       ")
                        .aisle("BABBBAB", "AAAAAAA", " BCCCB ", " D C D ", "       ", "       ")
                        .aisle(" BAAAB ", "BAAEAAB", " CXFXC ", "  GXG  ", "   C   ", "       ")
                        .aisle(" BAAAB ", "BAEAEAB", " CFXFC ", " CXXXC ", "  CXC  ", "   G   ")
                        .aisle(" BAAAB ", "BAAEAAB", " CXFXC ", "  GXG  ", "   C   ", "       ")
                        .aisle("BABBBAB", "AAAAAAA", " BCCCB ", " D C D ", "       ", "       ")
                        .aisle("AB   BA", " ABSBA ", "       ", "       ", "       ", "       ")
                        .self('S', MetaTileEntityEndoflameArray.class)
                        .block('A', getCasingState())
                        .block('B', getCasingState2())
                        .block('C', getCasingState3())
                        .block('D', getCasingState4())
                        .block('E', getCasingState5())
                        .block('F', getCasingState6())
                        .block('G', getCasingState7())
                        .air('X')
                        .any(' ');
                return builder
                        .where('A', Elements.choice(
                                Elements.block(getCasingState()),
                                Elements.abilities(0, 29,
                                        MultiblockAbility.IMPORT_ITEMS,
                                        POMultiblockAbility.MANA_OUTPUT_POOL,
                                        MultiblockAbility.MAINTENANCE_HATCH)))
                        .globalAbilityLimit(MultiblockAbility.IMPORT_ITEMS, 1, 27)
                        .globalAbilityLimit(POMultiblockAbility.MANA_OUTPUT_POOL, 1, 1)
                        .globalAbilityLimit(MultiblockAbility.MAINTENANCE_HATCH, 1, 1)
                        .buildStructureDefinition();
            });

    private int num = 0;
    private int fuelCount = 0;
    private int fireticks = 0;
    private final int MAX_TICKS = 1600000000;
    private int speed = 0;
    private long manaOutput = 0L;

    public MetaTileEntityEndoflameArray(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger(NBT_FUEL_CACHE, fireticks);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        fireticks = Math.max(0, Math.min(MAX_TICKS, data.getInteger(NBT_FUEL_CACHE)));
    }

    @Override
    protected void updateFormedValid() {
        if (!this.getWorld().isRemote) {
            manaOutput = 0L;
        }
        if (manaHandler != null && !this.getWorld().isRemote && this.isWorkingEnabled()) {
            int previousFireticks = fireticks;
            if (this.inputInventory != null && this.inputInventory.getSlots() > 0) {
                num = 0;
                fuelCount = 0;
                for (int i = 0; i < inputInventory.getSlots(); i++) {
                    ItemStack stack = inputInventory.getStackInSlot(i);
                    if ("endoflame".equals(ItemBlockSpecialFlower.getType(stack))) {
                        num += stack.getCount();
                    }
                    if (TileEntityFurnace.getItemBurnTime(stack) > 0) {
                        fuelCount += stack.getCount();
                    }
                }
                for (int i = 0; i < inputInventory.getSlots(); i++) {
                    ItemStack stack = inputInventory.getStackInSlot(i);
                    int time = TileEntityFurnace.getItemBurnTime(stack);
                    if (num > 0 && time > 0 && !manaHandler.isFull() && fireticks <= MAX_TICKS - time) {
                        ItemStack extracted = inputInventory.extractItem(i, 1, false);
                        if (!extracted.isEmpty()) {
                            this.fireticks += TileEntityFurnace.getItemBurnTime(extracted);
                            fuelCount -= extracted.getCount();
                        }
                    }
                }
            }
            if (!manaHandler.isFull()) {
                //产出最大速率
                speed = num;
                speed = Math.min(speed, fireticks);
                //削减燃烧时间 产出魔力
                long requestedMana = speed * 3L / 2L;
                long acceptedMana = requestedMana - manaHandler.addMana(requestedMana);
                manaOutput = acceptedMana;
                if (acceptedMana > 0L && requestedMana > 0L) {
                    speed = (int) Math.min(speed,
                            (acceptedMana * speed + requestedMana - 1L) / requestedMana);
                    fireticks -= speed;
                    setActive(true);
                } else {
                    speed = 0;
                    setActive(false);
                }
            } else {
                speed = 0;
                setActive(false);
            }
            if (fireticks != previousFireticks) {
                markDirty();
            }
        }
    }

    @Override
    protected StructureDefinition<?> createStructureDefinition() {
        return STRUCTURE_DEFINITION;
    }

    protected IBlockState getCasingState() {
        return PollutionMetaBlocks.BOT_BLOCK.getState(POBotBlock.BotBlockType.TERRA_4_CASING);
    }

    protected IBlockState getCasingState2() {
        return MetaBlocks.FRAMES.get(PollutionMaterials.KQGold).getBlock(PollutionMaterials.KQGold);
    }

    protected IBlockState getCasingState3() {
        return PollutionMetaBlocks.GLASS.getState(POGlass.MagicBlockType.AAMINATED_GLASS);
    }

    protected IBlockState getCasingState4() {
        return ModBlocks.pylon.getDefaultState().withProperty(BotaniaStateProps.PYLON_VARIANT, PylonVariant.MANA);
    }

    protected IBlockState getCasingState5() {
        assert Blocks.DIRT != null;
        return Blocks.DIRT.getDefaultState();
    }

    protected IBlockState getCasingState6() {
        return ModBlocks.floatingFlower.getDefaultState();
    }

    protected IBlockState getCasingState7() {
        return PollutionMetaBlocks.BEAM_CORE.getState(POMBeamCore.MagicBlockType.BEAM_CORE_0);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return POTextures.TERRA_4_CASING;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityEndoflameArray(this.metaTileEntityId);
    }

    @Override
    public List<ITextComponent> getDataInfo() {
        return Collections.emptyList();
    }

    ManaHandlerList manaHandler;

    @Override
    protected void initializeAbilities() {
        super.initializeAbilities();
        manaHandler = new ManaHandlerList(getAbilities(POMultiblockAbility.MANA_OUTPUT_POOL));
    }

    @Override
    protected void resetTileAbilities() {
        super.resetTileAbilities();
        manaHandler = new ManaHandlerList(new ArrayList<>());
    }

    public void invalidateStructure() {
        super.invalidateStructure();
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        super.addDisplayText(textList);
        textList.add(new TextComponentTranslation("pollution.machine.endoflame_array.display.flowers", num));
        textList.add(new TextComponentTranslation("pollution.machine.endoflame_array.display.fuel_items", fuelCount));
        textList.add(new TextComponentTranslation("pollution.machine.endoflame_array.display.fuel_cache", fireticks, MAX_TICKS));
        textList.add(new TextComponentTranslation("pollution.machine.endoflame_array.display.output", manaOutput));
        if (manaHandler != null) {
            textList.add(new TextComponentTranslation("pollution.machine.endoflame_array.display.mana_pool",
                    manaHandler.getMana(), manaHandler.getMaxMana()));
        }
    }

    @Override
    protected void configureDisplayText(MultiblockUIBuilder builder) {
        builder.setWorkingStatus(isWorkingEnabled(), isActive())
                .addCustom(this::addEndoflameDisplayText)
                .addWorkingStatusLine();
    }

    private void addEndoflameDisplayText(KeyManager keyManager, UISyncer syncer) {
        int syncedFlowers = syncer.syncInt(num);
        int syncedFuelCount = syncer.syncInt(fuelCount);
        int syncedFuelCache = syncer.syncInt(fireticks);
        long syncedOutput = syncer.syncLong(manaOutput);
        long storedMana = syncer.syncLong(manaHandler == null ? 0L : manaHandler.getMana());
        long maxMana = syncer.syncLong(manaHandler == null ? 0L : manaHandler.getMaxMana());

        keyManager.add(KeyUtil.lang(TextFormatting.AQUA,
                "pollution.machine.endoflame_array.display.flowers", syncedFlowers));
        keyManager.add(KeyUtil.lang(TextFormatting.GRAY,
                "pollution.machine.endoflame_array.display.fuel_items", syncedFuelCount));
        keyManager.add(KeyUtil.lang(TextFormatting.GRAY,
                "pollution.machine.endoflame_array.display.fuel_cache", syncedFuelCache, MAX_TICKS));
        keyManager.add(KeyUtil.lang(TextFormatting.GREEN,
                "pollution.machine.endoflame_array.display.output", syncedOutput));
        keyManager.add(KeyUtil.lang(TextFormatting.AQUA,
                "pollution.machine.endoflame_array.display.mana_pool", storedMana, maxMana));
    }

    //tooltip
    public void addInformation(ItemStack stack, World world, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        tooltip.add(I18n.format("pollution.machine.endoflame_array.tooltip.1"));
        tooltip.add(I18n.format("pollution.machine.endoflame_array.tooltip.2"));
        tooltip.add(I18n.format("pollution.machine.endoflame_array.tooltip.3"));
        tooltip.add(I18n.format("pollution.machine.endoflame_array.tooltip.4"));

    }
}
