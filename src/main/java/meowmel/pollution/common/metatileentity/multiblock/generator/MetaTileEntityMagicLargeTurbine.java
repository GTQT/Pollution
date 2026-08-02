package meowmel.pollution.common.metatileentity.multiblock.generator;

import gregtech.api.GTValues;
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
import gregtech.common.metatileentities.multi.electric.generator.MetaTileEntityLargeTurbine;
import meowmel.pollution.api.metatileentity.POMultiblockAbility;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MetaTileEntityMagicLargeTurbine extends MetaTileEntityLargeTurbine {

    public MetaTileEntityMagicLargeTurbine(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap, int tier, IBlockState casingState, IBlockState gearboxState, ICubeRenderer casingRenderer, boolean hasMufflerHatch, ICubeRenderer frontOverlay) {
        super(metaTileEntityId, new MagicTurbineType(metaTileEntityId.toString(), recipeMap, tier, casingState,
                gearboxState, casingRenderer, hasMufflerHatch, frontOverlay));
    }

    @Override
    protected void initializeAbilities() {
        super.initializeAbilities();
        this.energyContainer = new EnergyContainerList(getAbilities(POMultiblockAbility.MANA_OUTPUT_HATCH));
    }

    @Override
    protected @NotNull StructureDefinition<?> createStructureDefinition() {
        return StructureDefinition.getOrBuild("pollution:magic_large_turbine_" + tier, () -> {
            return DeclarativePatternBuilder.start()
                .aisle("CCCC", "CHHC", "CCCC")
                .aisle("CHHC", "RGGR", "CHHC")
                .aisle("CCCC", "CSHC", "CCCC")
                .self('S', MetaTileEntityMagicLargeTurbine.class)
                .block('G', type.getGearboxState())
                .block('C', type.getCasingState())
                .where('R', Elements.chain(
                        Elements.metaTileEntities(1, 1, MultiblockAbility.REGISTRY.get(MultiblockAbility.ROTOR_HOLDER).stream()
                        .filter(mte -> (mte instanceof ITieredMetaTileEntity) &&
                                (((ITieredMetaTileEntity) mte).getTier() >= tier))
                        .toArray(MetaTileEntity[]::new)),
                        Elements.abilities(1, 1, POMultiblockAbility.MANA_OUTPUT_HATCH)))
                    .where('H', Elements.choice(
                            Elements.block(type.getCasingState()),
                            Elements.abilities(0, 7,
                                    MultiblockAbility.MAINTENANCE_HATCH,
                                    MultiblockAbility.MUFFLER_HATCH,
                                    MultiblockAbility.IMPORT_FLUIDS,
                                    MultiblockAbility.EXPORT_FLUIDS)))
                    .globalAbilityLimit(MultiblockAbility.ROTOR_HOLDER, 1, 1)
                    .globalAbilityLimit(POMultiblockAbility.MANA_OUTPUT_HATCH, 1, 1)
                    .globalAbilityLimit(MultiblockAbility.MAINTENANCE_HATCH, 1, 1)
                    .globalAbilityLimit(MultiblockAbility.MUFFLER_HATCH, 0, 1)
                    .globalAbilityLimit(MultiblockAbility.IMPORT_FLUIDS, 0, 4)
                    .globalAbilityLimit(MultiblockAbility.EXPORT_FLUIDS, 0, 4)
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
