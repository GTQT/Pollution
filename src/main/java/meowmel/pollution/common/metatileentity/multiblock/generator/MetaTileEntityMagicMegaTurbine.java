package meowmel.pollution.common.metatileentity.multiblock.generator;

import gregtech.api.GTValues;
import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.impl.EnergyContainerList;
import gregtech.api.metatileentity.ITieredMetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.pattern.casing.DeclarativePatternBuilder;
import gregtech.api.pattern.element.Elements;
import gregtech.api.pattern.element.StructureDefinition;
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
        super(metaTileEntityId, new MagicTurbineType(metaTileEntityId.toString(), recipeMap, tier, casingState,
                gearboxState, casingRenderer, hasMufflerHatch, frontOverlay));
    }

    @Override
    protected void initializeAbilities() {
        super.initializeAbilities();
        this.energyContainer = new EnergyContainerList(getAbilities(POMultiblockAbility.MANA_OUTPUT_HATCH));
    }

    @Nonnull
    @Override
    protected StructureDefinition<?> createStructureDefinition() {
        return StructureDefinition.getOrBuild("pollution:magic_mega_turbine_" + tier, () -> {
            MetaTileEntity[] reinforcedRotors = MultiblockAbility.REGISTRY.get(GTQTMultiblockAbility.REINFORCED_ROTOR_HOLDER).stream()
                    .filter(mte -> mte instanceof ITieredMetaTileEntity && ((ITieredMetaTileEntity) mte).getTier() >= tier)
                    .toArray(MetaTileEntity[]::new);
            MetaTileEntity[] manaOutputs = MultiblockAbility.REGISTRY.get(POMultiblockAbility.MANA_OUTPUT_HATCH).stream()
                    .filter(mte -> {
                        IEnergyContainer container = mte.getCapability(GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER, null);
                        return container != null && container.getOutputVoltage() * container.getOutputAmperage() >= GTValues.V[tier];
                    })
                    .toArray(MetaTileEntity[]::new);
            return DeclarativePatternBuilder.start()
                .aisle("CCCCCCC", "CCCCCCC", "CCMMMCC", "CCMMMCC", "CCMMMCC", "CCCCCCC", "CCCCCCC")
                .aisle("CCCCCCC", "RGGGGGR", "CCCCCCC", "CCCCCCC", "CCCCCCC", "RGGGGGR", "CCCCCCC")
                .aisle("CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC")
                .aisle("CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC")
                .aisle("CCCCCCC", "RGGGGGR", "CCCCCCC", "CCCCCCC", "CCCCCCC", "RGGGGGR", "CCCCCCC")
                .aisle("CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC")
                .aisle("CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC")
                .aisle("CCCCCCC", "RGGGGGR", "CCCCCCC", "CCCCCCC", "CCCCCCC", "RGGGGGR", "CCCCCCC")
                .aisle("CCCCCCC", "CAAAAAC", "CAAAAAC", "CAASAAC", "CAAAAAC", "CAAAAAC", "CCCCCCC")
                .self('S', MetaTileEntityMagicMegaTurbine.class)
                .block('C', type.getCasingState())
                .block('G', type.getGearboxState())
                .where('R', Elements.metaTileEntitiesAsAbility(GTQTMultiblockAbility.REINFORCED_ROTOR_HOLDER,
                        0, -1, -1, reinforcedRotors))
                .hatch('M', MultiblockAbility.MUFFLER_HATCH)
                    .where('A', Elements.choice(
                            Elements.block(type.getCasingState()),
                            Elements.counted(0, 18, Elements.choice(
                                    Elements.metaTileEntitiesAsAbility(POMultiblockAbility.MANA_OUTPUT_HATCH,
                                            0, 8, 1, manaOutputs),
                                    Elements.abilities(MultiblockAbility.MAINTENANCE_HATCH,
                                            MultiblockAbility.IMPORT_ITEMS,
                                            MultiblockAbility.IMPORT_FLUIDS,
                                            MultiblockAbility.EXPORT_FLUIDS)))))
                    .globalAbilityLimit(POMultiblockAbility.MANA_OUTPUT_HATCH, 0, 8)
                    .globalAbilityLimit(MultiblockAbility.MAINTENANCE_HATCH, 1, 1)
                    .globalAbilityLimit(MultiblockAbility.IMPORT_ITEMS, 0, 1)
                    .globalAbilityLimit(MultiblockAbility.IMPORT_FLUIDS, 1, 4)
                    .globalAbilityLimit(MultiblockAbility.EXPORT_FLUIDS, 1, 4)
                    .buildStructureDefinition();
        });
    }

    @Override
    public void addInformation(ItemStack stack, World player, @NotNull List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(TextFormatting.GREEN + I18n.format("-魔力能源仓支持："));
        tooltip.add(TextFormatting.GRAY + I18n.format("只允许使用魔力能源仓作为能量输入接口"));
        tooltip.add(TextFormatting.GRAY + I18n.format("耗能，双仓升压等计算同普通能源仓"));
    }
}
