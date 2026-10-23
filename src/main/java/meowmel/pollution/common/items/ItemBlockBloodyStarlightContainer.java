package meowmel.pollution.common.items;

import meowmel.pollution.common.block.tile.TileEntityBloodyStarlightContainer;
import meowmel.pollution.common.entity.dragon.DragonContainmentData;
import meowmel.pollution.common.entity.dragon.DragonContainmentService;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public class ItemBlockBloodyStarlightContainer extends ItemBlock {
    public ItemBlockBloodyStarlightContainer(Block block) {
        super(block);
        setRegistryName(block.getRegistryName());
        setMaxStackSize(1);
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side,
                                           float hitX, float hitY, float hitZ, EnumHand hand) {
        if (player.isSneaking() && DragonContainmentData.isOccupied(player.getHeldItem(hand))) {
            return release(player, world, pos, side, hand);
        }
        return EnumActionResult.PASS;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand,
                                     EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack held = player.getHeldItem(hand);
        boolean occupied = DragonContainmentData.isOccupied(held);
        int count = held.getCount();
        if (player.isSneaking() && occupied) return release(player, world, pos, facing, hand);
        if (occupied && !DragonContainmentData.isValid(DragonContainmentData.get(held))) {
            if (!world.isRemote) DragonContainmentService.message(player, "invalid");
            return EnumActionResult.FAIL;
        }
        EnumActionResult result = super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
        if (!world.isRemote && occupied && result == EnumActionResult.SUCCESS
                && held.getCount() < count && player.capabilities.isCreativeMode) {
            // Creative placement normally keeps its item. Transfer the dragon instead of copying it.
            DragonContainmentData.set(held, new NBTTagCompound());
            player.inventory.markDirty();
        }
        return result;
    }

    private EnumActionResult release(EntityPlayer player, World world, BlockPos pos, EnumFacing side, EnumHand hand) {
        if (!world.isRemote) {
            ItemStack held = player.getHeldItem(hand);
            if (!DragonContainmentService.canEdit(player, pos, held)) {
                DragonContainmentService.message(player, "permission");
            } else if (DragonContainmentService.release(player, DragonContainmentData.get(held), pos.offset(side))) {
                DragonContainmentData.set(held, new NBTTagCompound());
                player.inventory.markDirty();
            }
        }
        return EnumActionResult.SUCCESS;
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos,
                                EnumFacing side, float hitX, float hitY, float hitZ, IBlockState state) {
        if (!super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, state)) return false;
        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileEntityBloodyStarlightContainer) {
                ((TileEntityBloodyStarlightContainer) tile).setPayload(DragonContainmentData.get(stack));
            } else return false;
        }
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> lines, ITooltipFlag flag) {
        if (DragonContainmentData.isOccupied(stack)) {
            NBTTagCompound display = DragonContainmentData.get(stack).getCompoundTag("Display");
            lines.add(I18n.format("pollution.containment.occupied"));
            if (!display.getString("Name").isEmpty()) lines.add(display.getString("Name"));
            lines.add(I18n.format("entity.pollution.ender_dragon.stage."
                    + display.getString("Stage").toLowerCase(Locale.ROOT)));
            lines.add(I18n.format("pollution.containment.health", display.getFloat("Health"), display.getFloat("MaxHealth")));
            lines.add(I18n.format("pollution.containment.progress", DragonContainmentData.progress(display)));
            lines.add(I18n.format("pollution.containment.suspended"));
        } else {
            lines.add(I18n.format("pollution.containment.empty"));
            lines.add(I18n.format("pollution.containment.capture_hint"));
        }
        lines.add(I18n.format("pollution.containment.use_hint"));
        lines.add(I18n.format("pollution.containment.loss_hint"));
    }
}
