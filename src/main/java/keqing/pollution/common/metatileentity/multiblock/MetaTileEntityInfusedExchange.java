package keqing.pollution.common.metatileentity.multiblock;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.util.GTTransferUtils;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import keqing.gtqtcore.api.metatileentity.MetaTileEntityBaseWithControl;
import keqing.pollution.api.unification.PollutionMaterials;
import keqing.pollution.client.textures.POTextures;
import keqing.pollution.common.block.PollutionMetaBlocks;
import keqing.pollution.common.block.metablocks.POMagicBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.tiles.essentia.TileJarFillable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MetaTileEntityInfusedExchange extends MetaTileEntityBaseWithControl {
    @Override
    public boolean usesMui2() {
        return false;
    }
    protected static final int NO_COLOR = 0;
    public Aspect al;
    public String name;
    public int storage = 0;
    public int number = 0;
    FluidStack AIR_STACK = PollutionMaterials.infused_air.getFluid(144);
    FluidStack FIRE_STACK = PollutionMaterials.infused_fire.getFluid(144);
    FluidStack WATER_STACK = PollutionMaterials.infused_water.getFluid(144);
    FluidStack ERATH_STACK = PollutionMaterials.infused_earth.getFluid(144);
    FluidStack ORDER_STACK = PollutionMaterials.infused_order.getFluid(144);
    FluidStack ENTROPY_STACK = PollutionMaterials.infused_entropy.getFluid(144);

    public MetaTileEntityInfusedExchange(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    public boolean hasMufflerMechanics() {
        return false;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityInfusedExchange(this.metaTileEntityId);
    }

    @Override
    public List<ITextComponent> getDataInfo() {
        return Collections.emptyList();
    }

    public void fillTanks(FluidStack stack) {
        GTTransferUtils.addFluidsToFluidHandler(this.outputFluidInventory, false, Collections.singletonList(stack));
    }

    public void updateFormedValid() {
        if(!this.getWorld().isRemote)return;
        if (this.getWorld().getTileEntity(this.getPos().add(0, 1, 0)) instanceof TileJarFillable s) {
            this.al = s.getEssentiaType(s.getFacing());
            this.storage = s.getEssentiaAmount(s.getFacing());
            if (this.al != null) {
                this.name = this.al.getName();
            }
        }
        if (this.number == 0) {
            this.number = this.storage;
            this.clearInfused();
            this.name = null;
        }

        if (this.number > 0 && this.outputFluidInventory != null) this.doFillTank();
    }

    private void doFillTank() {
        if (Objects.equals(this.name, "Aer")) {
            this.fillTanks(this.AIR_STACK);
            --this.number;
        }

        if (Objects.equals(this.name, "Terra")) {
            this.fillTanks(this.ERATH_STACK);
            --this.number;
        }

        if (Objects.equals(this.name, "Aqua")) {
            this.fillTanks(this.WATER_STACK);
            --this.number;
        }

        if (Objects.equals(this.name, "Ignis")) {
            this.fillTanks(this.FIRE_STACK);
            --this.number;
        }

        if (Objects.equals(this.name, "Ordo")) {
            this.fillTanks(this.ORDER_STACK);
            --this.number;
        }

        if (Objects.equals(this.name, "Perdition")) {
            this.fillTanks(this.ENTROPY_STACK);
            --this.number;
        }

    }

    public void clearInfused() {
        if (this.getWorld().getTileEntity(this.getPos().add(0, 2, 0)) instanceof TileJarFillable s) {
            s.amount = 0;
        }

    }

    protected void addDisplayText(List<ITextComponent> textList) {
        super.addDisplayText(textList);
        if (this.isStructureFormed()) {
            textList.add(new TextComponentTranslation("源质罐内缓存:Infused: %s  Amount: %s", this.name, this.storage));
            textList.add(new TextComponentTranslation("多方块内缓存：Infused: %s  Amount: %s", this.name, this.number));
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        data.setInteger("number", this.number);
        data.setString("name",this.name);
        return super.writeToNBT(data);
    }

    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.number = data.getInteger("number");
        this.name=data.getString("name");
    }
    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("A","S")
                .where('S', selfPredicate())
                .where('A', abilities(MultiblockAbility.EXPORT_FLUIDS).setMaxGlobalLimited(1).setPreviewCount(1))
                .build();
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        this.getFrontOverlay().renderOrientedState(renderState, translation, pipeline, getFrontFacing(), true, true);
    }

    @Override
    protected OrientedOverlayRenderer getFrontOverlay() {
        return Textures.HPCA_OVERLAY;
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return POTextures.VOID_PRISM;
    }

    @Override
    public boolean shouldShowVoidingModeButton() {
        return false;
    }

    @Override
    public void addInformation(ItemStack stack, World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("在控制器正上方放置源质罐子即可输入对应的源质"));
        tooltip.add(I18n.format("仅限：Aer，Terra，Aqua，Ignis，Ordo，Perditio源质"));
    }

}