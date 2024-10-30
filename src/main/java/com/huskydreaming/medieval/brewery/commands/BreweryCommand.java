package com.huskydreaming.medieval.brewery.commands;

import com.huskydreaming.huskycore.HuskyPlugin;
import com.huskydreaming.huskycore.commands.abstraction.AbstractCommand;
import com.huskydreaming.huskycore.commands.annotations.CommandAnnotation;
import com.huskydreaming.huskycore.utilities.general.Parseable;
import com.huskydreaming.medieval.brewery.enumerations.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@CommandAnnotation(label = "brewery")
public class BreweryCommand extends AbstractCommand {

    public BreweryCommand(HuskyPlugin plugin) {
        super(plugin);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return List.of("recipe", "reload", "remove");
    }

    @Override
    public void onCommand(CommandSender commandSender, String[] strings) {
        commandSender.sendMessage(getUsage().prefix());
    }

    @Override
    public Parseable getUsage() {
        return Message.GENERAL_USAGE;
    }

    @Override
    public Parseable getPermission() {
        return Message.GENERAL_PERMISSION;
    }
}