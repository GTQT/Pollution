package meowmel.pollution.common.metatileentity.multiblock.generator;

import gregtech.api.GTValues;
import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.impl.EnergyContainerList;
import gregtech.api.metatileentity.ITieredMetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.recipes.RecipeMap;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityLaserHatch;
import meowmel.gtqtcore.api.metatileentity.multiblock.GTQTMultiblockAbility;
import meowmel.gtqtcore.common.metatileentities.multi.electric.generator.MetaTileEntityMegaTurbine;
import meowmel.pollution.api.metatileentity.POMultiblockAbility;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;

public class MetaTileEntityMagicMegaTurbine extends MetaTileEntityMegaTurbine {

    public MetaTileEntityMagicMegaTurbine(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap, int tier, IBlockState casingState, IBlockState gearboxState, ICubeRenderer casingRenderer, boolean hasMufflerHatch, ICubeRenderer frontOverlay) {
        super(metaTileEntityId, recipeMap, tier, casingState, gearboxState, casingRenderer, hasMufflerHatch, frontOverlay);
    }

    @Override
    protected void initializeAbilities() {
        super.initializeAbilities();
        this.energyContainer = new EnergyContainerList(getAbilities(POMultiblockAbility.MANA_OUTPUT_HATCH));
    }

    @Nonnull
    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("CCCCCCC", "CCCCCCC", "CCMMMCC", "CCMMMCC", "CCMMMCC", "CCCCCCC", "CCCCCCC")
                .aisle("CCCCCCC", "RGGGGGR", "CCCCCCC", "CCCCCCC", "CCCCCCC", "RGGGGGR", "CCCCCCC")
                .aisle("CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC")
                .aisle("CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC")
                .aisle("CCCCCCC", "RGGGGGR", "CCCCCCC", "CCCCCCC", "CCCCCCC", "RGGGGGR", "CCCCCCC")
                .aisle("CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC")
                .aisle("CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC")
                .aisle("CCCCCCC", "RGGGGGR", "CCCCCCC", "CCCCCCC", "CCCCCCC", "RGGGGGR", "CCCCCCC")
                .aisle("CCCCCCC", "CAAAAAC", "CAAAAAC", "CAASAAC", "CAAAAAC", "CAAAAAC", "CCCCCCC")
                .where('S', this.selfPredicate())
                .where('C', states(getCasingState()))
                .where('G', states(getGearBoxState()))
                .where('R', metaTileEntities(MultiblockAbility.REGISTRY.get(GTQTMultiblockAbility.REINFORCED_ROTOR_HOLDER).stream()
                        .filter(mte -> (mte instanceof ITieredMetaTileEntity) &&
                                (((ITieredMetaTileEntity) mte).getTier() >= tier))
                        .toArray(MetaTileEntity[]::new))
                        .addTooltips("gregtech.multiblock.pattern.clear_amount_3"))
                .where('M', abilities(MultiblockAbility.MUFFLER_HATCH))
                .where('A', states(getCasingState())
                        .or(metaTileEntities(MultiblockAbility.REGISTRY.get(POMultiblockAbility.MANA_OUTPUT_HATCH).stream()
                                .filter(mte -> {
                                    IEnergyContainer container = mte
                                            .getCapability(GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER, null);
                                    return container != null &&
                                            container.getOutputVoltage() * container.getOutputAmperage() >= GTValues.V[tier];
                                })
                                .toArray(MetaTileEntity[]::new))
                                .setMaxGlobalLimited(8))
                        .or(abilities(MultiblockAbility.MAINTENANCE_HATCH)
                                .setExactLimit(1))
                        .or(abilities(MultiblockAbility.IMPORT_ITEMS)
                                .setMaxGlobalLimited(1)
                                .setPreviewCount(1))
                        .or(abilities(MultiblockAbility.IMPORT_FLUIDS)
                                .setMinGlobalLimited(1)
                                .setMaxGlobalLimited(4)
                                .setPreviewCount(1))
                        .or(abilities(MultiblockAbility.EXPORT_FLUIDS)
                                .setMinGlobalLimited(1)
                                .setMaxGlobalLimited(4)
                                .setPreviewCount(1)))
                .build();
    }

    @Override
    public void addInformation(ItemStack stack, World player, @NotNull List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(TextFormatting.GREEN + I18n.format("-魔力能源仓支持："));
        tooltip.add(TextFormatting.GRAY + I18n.format("只允许使用魔力能源仓作为能量输入接口"));
        tooltip.add(TextFormatting.GRAY + I18n.format("耗能，双仓升压等计算同普通能源仓"));
    }
}
