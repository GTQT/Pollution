package keqing.pollution.common.metatileentity.multiblock;

import gregicality.multiblocks.api.GCYMValues;
import gregicality.multiblocks.api.render.GCYMTextures;
import gregicality.multiblocks.common.block.GCYMMetaBlocks;
import gregicality.multiblocks.common.block.blocks.BlockLargeMultiblockCasing;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.RecipeMaps;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.common.blocks.BlockGlassCasing;
import gregtech.common.blocks.MetaBlocks;
import keqing.pollution.api.metatileentity.POManaMultiblock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;

import java.util.List;

import static gregtech.api.util.RelativeDirection.*;

public class MetaTileEntityBotDistillery extends POManaMultiblock {

    public MetaTileEntityBotDistillery(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, new RecipeMap[]{RecipeMaps.DISTILLATION_RECIPES, RecipeMaps.DISTILLERY_RECIPES});
    }
    //工作配方
    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
        return new MetaTileEntityBotDistillery(this.metaTileEntityId);
    }
    //方块注册
    @Override
    protected  BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start(RIGHT, FRONT, UP)
                .aisle("___XXX___","___XXX___","__XXXXX__","XXXXXXXXX","XXXXXXXXX","XXXXXXXXX","__XXXXX__","___XXX___","___XXX___")
                .aisle("_________","___XXX___","__XXXXX__","_XXXXXXX_","_XXXXXXX_","XXXXXXXX_","__XXXXX__","___XXX___","_________")
                .aisle("_________","____X____","___XXX___","__XXXXX__","_XXXXXXX_","__XXXXX__","___XXX___","____X____","_________")
                .aisle("_________","_________","___Y_Y___","__Y___Y__","____Z____","__Y___Y__","___Y_Y___","_________","_________").setRepeatable(1, 12)
                .aisle("_________","_________","_________","_________","____Z____","_________","_________","_________","_________")
                .aisle("_________","_________","_________","_________","____Z____","_________","_________","_________","_________")

                .where('S', selfPredicate())
                .where('X', states(getCasingState()).setMinGlobalLimited(20)
                        .or(autoAbilities(false, true, true, true, true, false, false))
                        .or(abilities(MultiblockAbility.INPUT_ENERGY).setExactLimit(1)))
                .where('Y', states(getCasingState())
                        .or(abilities(MultiblockAbility.EXPORT_FLUIDS).setMaxLayerLimited(1).setMinLayerLimited(1)))
                .where('Z', states(getCasingState2()))
                .where('_', any())
                .build();
    }

    private static IBlockState getCasingState() {
        return GCYMMetaBlocks.LARGE_MULTIBLOCK_CASING.getState(BlockLargeMultiblockCasing.CasingType.ASSEMBLING_CASING);
    }
    //设置外壳方块1
    private static IBlockState getCasingState2() {
        return MetaBlocks.TRANSPARENT_CASING.getState(BlockGlassCasing.CasingType.TEMPERED_GLASS);
    }
    //设置外壳方块2
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return GCYMTextures.ASSEMBLING_CASING;
    }
    //设置纹理
    @Override
    protected  OrientedOverlayRenderer getFrontOverlay() {
        return GCYMTextures.LARGE_ASSEMBLER_OVERLAY;
    }

    @Override
    public boolean canBeDistinct() {
        return true;
    }

    @Override
    public void addInformation(ItemStack stack,  World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("pollution.machine.bot_distillery", 1));
    }
//工具提示
}