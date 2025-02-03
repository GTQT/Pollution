package keqing.pollution.common.metatileentity.multiblock;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.common.blocks.MetaBlocks;
import keqing.gtqtcore.api.metaileentity.MetaTileEntityBaseWithControl;
import keqing.pollution.api.metatileentity.POMultiblockAbility;
import keqing.pollution.api.unification.PollutionMaterials;
import keqing.pollution.client.textures.POTextures;
import keqing.pollution.common.block.PollutionMetaBlocks;
import keqing.pollution.common.block.metablocks.POBotBlock;
import keqing.pollution.common.block.metablocks.POGlass;
import keqing.pollution.common.block.metablocks.POMBeamCore;
import keqing.pollution.common.metatileentity.multiblockpart.MetaTileEntityManaPoolHatch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import vazkii.botania.api.state.BotaniaStateProps;
import vazkii.botania.api.state.enums.PylonVariant;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.item.block.ItemBlockSpecialFlower;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class MetaTileEntityEndoflameArray extends MetaTileEntityBaseWithControl {
    MetaTileEntityManaPoolHatch ManaPool = null;
    private int num = 0;
    private int fireticks = 0;
    private final int MAX_TICKS = 1600000000;
    private int speed = 0;

    public MetaTileEntityEndoflameArray(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    @Override
    protected void updateFormedValid() {
        if (!this.isActive())
            setActive(true);
        if (ManaPool != null && !this.getWorld().isRemote && this.isWorkingEnabled()) {
            if (this.inputInventory != null && this.inputInventory.getSlots() > 0) {
                num = 0;
                for (int i = 0; i < inputInventory.getSlots(); i++) {
                    if ("endoflame".equals(ItemBlockSpecialFlower.getType(inputInventory.getStackInSlot(i)))) {
                        num += inputInventory.getStackInSlot(i).getCount();
                    }
                    ItemStack stack = inputInventory.getStackInSlot(i);
                    int time = TileEntityFurnace.getItemBurnTime(stack);
                    if (time > 0) {
                        inputInventory.extractItem(i, 1, false);
                        this.fireticks += time;
                        this.fireticks = Math.min(fireticks, MAX_TICKS);
                    }
                }
            }
            if (!ManaPool.isFull()) {
                //产出最大速率
                speed = num;
                speed = Math.min(speed, fireticks);
                //削减燃烧时间 产出魔力
                fireticks -= speed;
                this.ManaPool.consumeMana((int) (speed * 1.5));
            }
        }
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("AB   BA", " ABBBA ", "       ", "       ", "       ", "       ")
                .aisle("BABBBAB", "AAAAAAA", " BCCCB ", " D C D ", "       ", "       ")
                .aisle(" BAAAB ", "BAAEAAB", " CXFXC ", "  GXG  ", "   C   ", "       ")
                .aisle(" BAAAB ", "BAEAEAB", " CFXFC ", " CXXXC ", "  CXC  ", "   G   ")
                .aisle(" BAAAB ", "BAAEAAB", " CXFXC ", "  GXG  ", "   C   ", "       ")
                .aisle("BABBBAB", "AAAAAAA", " BCCCB ", " D C D ", "       ", "       ")
                .aisle("AB   BA", " ABSBA ", "       ", "       ", "       ", "       ")
                .where('S', selfPredicate())
                .where('A', states(getCasingState()).setMinGlobalLimited(15)
                        .or(abilities(MultiblockAbility.IMPORT_ITEMS).setPreviewCount(1))
                        .or(abilities(POMultiblockAbility.MANA_POOL_HATCH).setExactLimit(1))
                        .or(abilities(MultiblockAbility.MAINTENANCE_HATCH).setExactLimit(1))
                )
                .where('B', states(getCasingState2()))
                .where('C', states(getCasingState3()))
                .where('D', states(getCasingState4()))
                .where('E', states(getCasingState5()))
                .where('F', states(getCasingState6()))
                .where('G', states(getCasingState7()))
                .where('X', air())
                .where(' ', any())
                .build();
    }

    protected IBlockState getCasingState() {
        return PollutionMetaBlocks.BOT_BLOCK.getState(POBotBlock.BotBlockType.TERRA_4_CASING);
    }

    protected IBlockState getCasingState2() {
        return MetaBlocks.FRAMES.get(PollutionMaterials.keqinggold).getBlock(PollutionMaterials.keqinggold);
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
        return null;
    }

    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        for (Map.Entry<String, Object> battery : context.entrySet()) {
            if (battery.getKey().startsWith("Multi")) {
                HashSet set = (HashSet) battery.getValue();
                for (var s : set
                ) {
                    if (s instanceof MetaTileEntityManaPoolHatch) {
                        this.ManaPool = (MetaTileEntityManaPoolHatch) s;
                    }
                }
            }
        }
    }

    public void invalidateStructure() {
        super.invalidateStructure();
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        super.addDisplayText(textList);
        textList.add(new TextComponentString("火红莲搭载数量:" + num));
        textList.add(new TextComponentString("每t魔力输出速度:" + speed));
        textList.add(new TextComponentString("燃料缓存:" + fireticks));
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
