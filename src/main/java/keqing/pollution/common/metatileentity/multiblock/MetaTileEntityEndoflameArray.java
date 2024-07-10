package keqing.pollution.common.metatileentity.multiblock;

import com.google.common.collect.Lists;
import gregtech.api.capability.impl.EnergyContainerList;
import gregtech.api.capability.impl.FluidTankList;
import gregtech.api.capability.impl.ItemHandlerList;
import gregtech.api.items.itemhandlers.GTItemStackHandler;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.util.GTUtility;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import keqing.gtqtcore.common.metatileentities.multi.multiblock.standard.MetaTileEntityBaseWithControl;
import keqing.pollution.api.capability.IManaHatch;
import keqing.pollution.api.metatileentity.POMultiblockAbility;
import keqing.pollution.client.textures.POTextures;
import keqing.pollution.common.block.PollutionMetaBlocks;
import keqing.pollution.common.block.metablocks.POMagicBlock;
import keqing.pollution.common.metatileentity.multiblockpart.MetaTileEntityManaPoolHatch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vazkii.botania.common.item.block.ItemBlockSpecialFlower;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class MetaTileEntityEndoflameArray extends MetaTileEntityBaseWithControl {
    MetaTileEntityManaPoolHatch MnanaPool=null;
    private int num=0;
    private int fireticks=0;
    private int MAX_TICKS=1600000000;
    private int speed = 0;
    public MetaTileEntityEndoflameArray(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    @Override
    protected void updateFormedValid() {
        if(!this.isActive())
            setActive(true);
        if(MnanaPool!=null && !this.getWorld().isRemote && this.isWorkingEnabled())
        {
            if(this.inputInventory!=null && this.inputInventory.getSlots()>0)
            {
                num=0;
                for (int i = 0; i < inputInventory.getSlots(); i++) {
                    if("endoflame".equals(ItemBlockSpecialFlower.getType(inputInventory.getStackInSlot(i))))
                    {
                        num += inputInventory.getStackInSlot(i).getCount();
                    }
                    ItemStack stack = inputInventory.getStackInSlot(i);
                    int time = TileEntityFurnace.getItemBurnTime(stack);
                    if(time>0)
                    {
                        inputInventory.extractItem(i,1,false);
                        this.fireticks+=time;
                        this.fireticks = Math.min(fireticks,MAX_TICKS);
                    }
                }
            }
            if(!MnanaPool.isFull())
            {
                //产出最大速率
                speed = num;
                speed = Math.min(speed,fireticks);
                //削减燃烧时间 产出魔力
                fireticks -= speed;
                this.MnanaPool.consumeMana((int)(speed*1.5));
            }
        }
    }
    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("XXX", "XXX", "XXX")
                .aisle("XXX", "X#X", "XXX")
                .aisle("XXX", "XSX", "XXX")
                .where('S', selfPredicate())
                .where('X', states(getCasingState())
                        .or(abilities(MultiblockAbility.IMPORT_ITEMS).setPreviewCount(1))
                        .or(abilities(POMultiblockAbility.MANA_POOL_HATCH).setExactLimit(1))
                        .or(abilities(MultiblockAbility.MAINTENANCE_HATCH).setExactLimit(1))
                )
                .where('#', air())
                .build();
    }
    protected IBlockState getCasingState() {
        return PollutionMetaBlocks.MAGIC_BLOCK.getState(POMagicBlock.MagicBlockType.MAGIC_BATTERY);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return POTextures.MAGIC_BATTERY;
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
             if(battery.getKey().startsWith("Multi")  ) {
                HashSet set = (HashSet) battery.getValue();
                for (var s: set
                ) {
                    if(s instanceof MetaTileEntityManaPoolHatch)
                    {
                        this.MnanaPool = (MetaTileEntityManaPoolHatch)s;
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
        textList.add(new TextComponentString("Amount:"+num));
        textList.add(new TextComponentString("OutPut Speed:"+speed));
        textList.add(new TextComponentString("Burn Ticks:"+fireticks));
    }
}
