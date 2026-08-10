package meowmel.pollution.common.command;

import gregtech.api.GTValues;
import meowmel.pollution.api.amplification.MagicMachineProfileRegistry;
import meowmel.pollution.api.amplification.MagicProcessTag;
import meowmel.pollution.api.astral.AstralNbtHelper;
import meowmel.pollution.common.items.PollutionMetaItems;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * Lightweight server-side inspection tool for the magic amplification system.
 * It deliberately never alters hatches, recipes or player research data.
 */
public final class CommandMagicAmplification extends CommandBase {

    @Override
    public String getName() {
        return "pollutionmagic";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/pollutionmagic [inspect|profile <machine_id>]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length > 0 && "profile".equalsIgnoreCase(args[0])) {
            showProfile(sender, args);
            return;
        }
        showInspection(sender, getCommandSenderAsPlayer(sender));
    }

    private void showInspection(ICommandSender sender, EntityPlayerMP player) {
        World world = player.world;
        long dayTime = world.getWorldTime() % 24000L;
        sender.sendMessage(line(TextFormatting.LIGHT_PURPLE, "[Pollution 魔导诊断]"));
        sender.sendMessage(line(TextFormatting.GRAY, "维度 " + world.provider.getDimension()
                + "，时间 " + dayTime + "，" + (world.isDaytime() ? "白天" : "夜晚")
                + "，月相 " + world.getMoonPhase()));

        ItemStack held = player.getHeldItemMainhand();
        if (held.isEmpty()) {
            sender.sendMessage(line(TextFormatting.YELLOW, "主手未持有星座数据晶圆；手持晶圆后再次执行可检查其 NBT。"));
            return;
        }
        if (!held.isItemEqual(PollutionMetaItems.CONSTELLATION_DATA_WAFER.getStackForm())) {
            sender.sendMessage(line(TextFormatting.YELLOW, "主手物品不是星座数据晶圆：" + held.getDisplayName()));
            return;
        }

        @Nullable IConstellation constellation = AstralNbtHelper.readConstellation(held);
        if (constellation == null) {
            sender.sendMessage(line(TextFormatting.RED, "该晶圆没有可读取的星座 NBT。"));
            return;
        }
        String function = held.hasTagCompound()
                ? held.getTagCompound().getString(AstralNbtHelper.CELESTIAL_FUNCTION) : "";
        sender.sendMessage(line(TextFormatting.AQUA, "晶圆星座：" + constellation.getSimpleName()
                + (function.isEmpty() ? "" : "，功能：" + function)));
        sender.sendMessage(line(TextFormatting.GRAY,
                "基础强度：MV 透镜仓 10%，LuV+ 高级透镜仓 30%；当天空可见且该星座活跃时额外 +10%。"));
    }

    private void showProfile(ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            throw new CommandException("用法：/pollutionmagic profile <machine_id>");
        }
        ResourceLocation id;
        try {
            id = args[1].indexOf(':') >= 0 ? new ResourceLocation(args[1])
                    : new ResourceLocation("pollution", args[1]);
        } catch (IllegalArgumentException exception) {
            throw new CommandException("无效机器 ID：" + args[1]);
        }
        long tags = MagicMachineProfileRegistry.getFallbackTags(id);
        sender.sendMessage(line(TextFormatting.LIGHT_PURPLE, "[Pollution 魔导诊断] " + id));
        sender.sendMessage(line(tags == 0L ? TextFormatting.YELLOW : TextFormatting.AQUA,
                "后备工序标签：" + MagicProcessTag.describeMask(tags)));
        sender.sendMessage(line(TextFormatting.GRAY,
                "显式配方标签会覆盖此后备标签；JEI 会显示显式标签与可保护催化剂槽。"));
    }

    private static TextComponentString line(TextFormatting color, String text) {
        return new TextComponentString(color + text);
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
                                          @Nullable BlockPos targetPos) {
        if (args.length == 1) return getListOfStringsMatchingLastWord(args, "inspect", "profile");
        if (args.length == 2 && "profile".equalsIgnoreCase(args[0])) {
            return getListOfStringsMatchingLastWord(args, MagicMachineProfileRegistry.getProfiles().keySet());
        }
        return Collections.emptyList();
    }
}
