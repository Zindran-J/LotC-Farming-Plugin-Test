package commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import scheduleHandler.handler;

public class croptrample implements CommandExecutor {
    // This creates a valid "command" from which the initializer can pull
    // Usage is /croptrample <true/false> <user>
    // The command must persist over restarts/shutdowns, and is NOT togglable by the player themselves.

    public boolean onCommand(@NotNull CommandSender commandSender,
                             @NotNull Command command,
                             @NotNull String s,
                             @NotNull String[] args) {
        if (commandSender.hasPermission("farmplugin.croptrample")) {
            if (args.length != 2) {
                commandSender.sendMessage(Component.text(
                        "Incorrect command usage. Use /croptrample username true/false.",
                        NamedTextColor.RED));
            } else {
                String username = args[0];
                // Check if file exists
                boolean fileExists = handler.fileExists("croptrample.csv");
                if (args[1].equals("true") || args[1].equals("True")) {
                    if (fileExists) {
                        if (!handler.existsInFile("croptrample.csv", username)) {
                            handler.writeToFile("croptrample.csv", username);
                            commandSender.sendMessage(Component.text(
                                     username + " added to list successfully.",
                                    NamedTextColor.DARK_GREEN));
                        } else {
                            commandSender.sendMessage(Component.text(
                                    "Crop trampling is already enabled for " + username + ".",
                                    NamedTextColor.GOLD));
                        }
                    }
                } else if (args[1].equals("false") || args[1].equals("False")) {
                    if (fileExists) {
                        if (handler.existsInFile("croptrample.csv", username)) {
                            handler.deleteFromFile("croptrample.csv", username);
                            commandSender.sendMessage(Component.text(
                                    username + " removed from list successfully.",
                                    NamedTextColor.DARK_GREEN));
                        } else {
                            commandSender.sendMessage(Component.text(
                                    "Crop trampling is already disabled for " + username + ".",
                                    NamedTextColor.GOLD));
                        }
                    }
                } else {
                    commandSender.sendMessage(Component.text(
                            "Incorrect command usage. Use /croptrample username true/false.",
                            NamedTextColor.RED));
                }
            }
            return true;
        } else {
            commandSender.sendMessage(Component.text("You do not have permission to use this command!", NamedTextColor.RED));
            return false;
        }
    }
}
