package keqing.pollution.common.items;

import gregtech.api.GTValues;
import gregtech.api.GregTechAPI;
import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IElectricItem;
import gregtech.api.items.metaitem.ElectricStats;
import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.items.metaitem.StandardMetaItem;
import gregtech.api.items.metaitem.stats.IItemBehaviour;
import keqing.gtqtcore.common.items.GTQTMetaItems;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

import static keqing.pollution.common.CommonProxy.Pollution_TAB;

public class POBattery extends StandardMetaItem {
    public POBattery() {
        this.setRegistryName("pollution_battery");
        setCreativeTab(Pollution_TAB);
    }
    @Override
    public void registerSubItems() {
        PollutionMetaItems.MAGIC_BATTERY_LV = addItem(260, "battery.lv.magic").addComponents(ElectricStats.createRechargeableBattery(112500, GTValues.LV)).setModelAmount(8);
        PollutionMetaItems.MAGIC_BATTERY_MV = addItem(261, "battery.mv.magic").addComponents(ElectricStats.createRechargeableBattery(450000, GTValues.MV)).setModelAmount(8);
        PollutionMetaItems.MAGIC_BATTERY_HV = addItem(262, "battery.hv.magic").addComponents(ElectricStats.createRechargeableBattery(1800000, GTValues.HV)).setModelAmount(8);
        PollutionMetaItems.MAGIC_BATTERY_EV = addItem(263, "battery.ev.magic").addComponents(ElectricStats.createRechargeableBattery(7200000, GTValues.EV)).setModelAmount(8);
        PollutionMetaItems.MAGIC_BATTERY_IV = addItem(264, "battery.iv.magic").addComponents(ElectricStats.createRechargeableBattery(28800000, GTValues.IV)).setModelAmount(8);
        PollutionMetaItems.MAGIC_BATTERY_LuV = addItem(265, "battery.luv.magic").addComponents(ElectricStats.createRechargeableBattery(115200000, GTValues.LuV)).setModelAmount(8);
        PollutionMetaItems.MAGIC_BATTERY_ZPM = addItem(266, "battery.zpm.magic").addComponents(ElectricStats.createRechargeableBattery(460800000, GTValues.ZPM)).setModelAmount(8);
        PollutionMetaItems.MAGIC_BATTERY_UV = addItem(267, "battery.uv.magic").addComponents(ElectricStats.createRechargeableBattery(1843200000, GTValues.UV)).setModelAmount(8);

    }
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, @Nullable World worldIn, List<String> lines, ITooltipFlag tooltipFlag) {
        MetaItem<?>.MetaValueItem item = getItem(itemStack);
        if (item == null) return;
        String unlocalizedTooltip = "metaitem." + item.unlocalizedName + ".tooltip";
        if (I18n.hasKey(unlocalizedTooltip)) {
            lines.addAll(Arrays.asList(I18n.format(unlocalizedTooltip).split("/n")));
        }

        IElectricItem electricItem = itemStack.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
        if (electricItem != null) {
            lines.add(I18n.format("metaitem.generic.electric_item.tooltip",
                    electricItem.getCharge(),
                    electricItem.getMaxCharge(),
                    GTValues.VN[electricItem.getTier()]));
        }

        IFluidHandlerItem fluidHandler = ItemHandlerHelper.copyStackWithSize(itemStack, 1)
                .getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        if (fluidHandler != null) {
            IFluidTankProperties fluidTankProperties = fluidHandler.getTankProperties()[0];
            FluidStack fluid = fluidTankProperties.getContents();
            if (fluid != null) {
                lines.add(I18n.format("metaitem.generic.fluid_container.tooltip",
                        fluid.amount,
                        fluidTankProperties.getCapacity(),
                        fluid.getLocalizedName()));
            } else lines.add(I18n.format("metaitem.generic.fluid_container.tooltip_empty"));
        }

        for (IItemBehaviour behaviour : getBehaviours(itemStack)) {
            behaviour.addInformation(itemStack, lines);
        }
    }
}
