package com.github.tartaricacid.touhoulittlemaid.command;

import com.github.tartaricacid.touhoulittlemaid.capability.MaidNumHandler;
import com.github.tartaricacid.touhoulittlemaid.capability.MaidNumSerializer;
import com.github.tartaricacid.touhoulittlemaid.capability.PowerHandler;
import com.github.tartaricacid.touhoulittlemaid.capability.PowerSerializer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author TartaricAcid
 * @date 2019/8/28 17:39
 **/
public class MainCommand extends CommandBase {
    @Nonnull
    @Override
    public String getName() {
        return "touhou_little_maid";
    }

    @Nonnull
    @Override
    public List<String> getAliases() {
        List<String> aliases = new ArrayList<>();
        aliases.add("tlm");
        aliases.add("touhou");
        aliases.add("th");
        return aliases;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
        if (args.length >= 3) {
            if ("power".equals(args[0])) {
                commandPower(server, sender, args);
                return;
            }
            if ("maid_num".equals(args[0])) {
                commandMaidNum(server, sender, args);
                return;
            }
        }
        sender.sendMessage(new TextComponentTranslation("commands.touhou_little_maid.main.usage"));
    }

    @Nonnull
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        List<String> command = new ArrayList<>();

        switch (args.length) {
            case 1:
                command.add("power");
                command.add("maid_num");
                break;
            case 2:
                command.add("get");
                command.add("set");
                command.add("add");
                command.add("min");
                break;
            case 3:
                command.addAll(Arrays.asList(server.getOnlinePlayerNames()));
                break;
            default:
        }
        return getListOfStringsMatchingLastWord(args, command);
    }

    private void commandPower(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        EntityPlayer player = getPlayer(server, sender, args[2]);
        PowerHandler power = player.getCapability(PowerSerializer.POWER_CAP, null);
        float num = (args.length >= 4) ? (float) parseDouble(args[3]) : 1;
        if (power == null) {
            return;
        }

        switch (args[1]) {
            case "get":
                sender.sendMessage(new TextComponentTranslation("commands.touhou_little_maid.main.power.info", power.get()));
                break;
            case "set":
                power.set(num);
                sender.sendMessage(new TextComponentTranslation("commands.touhou_little_maid.main.power.info", power.get()));
                break;
            case "add":
                power.add(num);
                sender.sendMessage(new TextComponentTranslation("commands.touhou_little_maid.main.power.info", power.get()));
                break;
            case "min":
                power.min(num);
                sender.sendMessage(new TextComponentTranslation("commands.touhou_little_maid.main.power.info", power.get()));
                break;
            default:
                sender.sendMessage(new TextComponentTranslation(getUsage(sender)));
        }
    }

    private void commandMaidNum(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        EntityPlayer player = getPlayer(server, sender, args[2]);
        MaidNumHandler numHandler = player.getCapability(MaidNumSerializer.MAID_NUM_CAP, null);
        int num = (args.length >= 4) ? parseInt(args[3]) : 1;
        if (numHandler == null) {
            return;
        }

        switch (args[1]) {
            case "get":
                sender.sendMessage(new TextComponentTranslation("commands.touhou_little_maid.main.maid_num.info", numHandler.get()));
                break;
            case "set":
                numHandler.set(num);
                sender.sendMessage(new TextComponentTranslation("commands.touhou_little_maid.main.maid_num.info", numHandler.get()));
                break;
            case "add":
                numHandler.add(num);
                sender.sendMessage(new TextComponentTranslation("commands.touhou_little_maid.main.maid_num.info", numHandler.get()));
                break;
            case "min":
                numHandler.min(num);
                sender.sendMessage(new TextComponentTranslation("commands.touhou_little_maid.main.maid_num.info", numHandler.get()));
                break;
            default:
                sender.sendMessage(new TextComponentTranslation(getUsage(sender)));
        }
    }

    @Nonnull
    @Override
    public String getUsage(@Nonnull ICommandSender sender) {
        return "commands.touhou_little_maid.main.usage";
    }
}
