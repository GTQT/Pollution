package meowmel.pollution.api.metatileentity;

import gregtech.api.metatileentity.multiblock.ui.MultiblockUIBuilder;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.unification.material.Material;
import gregtech.api.util.KeyUtil;
import meowmel.pollution.api.capability.IVisHatch;
import meowmel.pollution.api.capability.ipml.MagicMultiblockRecipeLogic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidTank;

import java.util.ArrayList;
import java.util.List;

public abstract class MagicRecipeMapMultiblockController extends ManaMultiblockController {

    protected IVisHatch visHatch;
    protected IFluidTank infusedFluidTank;

    public MagicRecipeMapMultiblockController(ResourceLocation metaTileEntityId, RecipeMap<?>[] recipeMaps) {
        super(metaTileEntityId, recipeMaps);
        this.recipeMapWorkable = new MagicMultiblockRecipeLogic(this);
    }

    @Override
    protected void configureWarningText(MultiblockUIBuilder builder) {
        super.configureWarningText(builder);
        builder.addCustom((manager, syncer) -> {
            if (syncer.syncBoolean(checkInfusedFluid())) {
                manager.add(KeyUtil.lang(TextFormatting.RED,
                        "要素不符合"));
            }
        });
    }

    public boolean checkInfusedFluid() {
        if (infusedFluidTank == null) return false;
        return infusedFluidTank.getFluid().getFluid() == getMaterial().getFluid();
    }


    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        List<IVisHatch> visHatches = this.getAbilities(POMultiblockAbility.VIS_HATCH);
        if (visHatches != null && !visHatches.isEmpty() && visHatches.get(0) != null) {
            this.visHatch = visHatches.get(0);
        }

        List<IFluidTank> infusedFluidTanks = this.getAbilities(POMultiblockAbility.INFUSED_FLUID_HATCH);
        if (infusedFluidTanks != null && !infusedFluidTanks.isEmpty() && infusedFluidTanks.get(0) != null) {
            this.infusedFluidTank = infusedFluidTanks.get(0);
        }
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        visHatch = null;
        manaHatchList = new ArrayList<>();
        infusedFluidTank = null;
    }

    public int getVisCapacity() {
        if (visHatch == null) return 0;
        return visHatch.getMaxVisStore();
    }

    public int getVisStore() {
        if (visHatch == null) return 0;
        return visHatch.getVisStore();
    }

    public boolean consumeVis(int vis, boolean simulate) {
        if (visHatch == null) return false;
        return visHatch.drainVis(vis, simulate);
    }

    public abstract Material getMaterial();

    public boolean drainInfusedFluid(int amount, boolean simulate) {
        if (infusedFluidTank == null) return false;
        if (!checkInfusedFluid()) return false;
        if (infusedFluidTank.getFluidAmount() < amount) return false;
        if (simulate) return true;
        infusedFluidTank.drain(amount, true);
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
    }

    @Override
    public TraceabilityPredicate autoAbilities(boolean checkEnergyIn, boolean checkMaintenance, boolean checkItemIn, boolean checkItemOut, boolean checkFluidIn, boolean checkFluidOut, boolean checkMuffler) {
        TraceabilityPredicate predicate = super.autoAbilities(checkEnergyIn, checkMaintenance, checkItemIn, checkItemOut, checkFluidIn, checkFluidOut, checkMuffler);
        predicate = predicate.or(abilities(POMultiblockAbility.VIS_HATCH).setMaxGlobalLimited(1));
        return predicate;
    }

}
