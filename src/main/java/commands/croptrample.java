package commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.io.InputStream;

import static scheduleHandler.handler.*;

public class croptrample implements CommandExecutor {
    /*
     This creates a valid "command" from which the initializer can pull
     Usage is "/croptrample user" for the admin usage, and just "/croptrample" for yourself.
     The command must persist over restarts/shutdowns, and is NOT togglable by the player themselves.
     - Unless they have been given permission.
     */

    public boolean onCommand(@NotNull CommandSender user,
                             @NotNull Command command,
                             @NotNull String s,
                             @NotNull String[] args) {
        switch (command.getName()) {
            case "croptrample":
                if (user instanceof ConsoleCommandSender) {
                    // This is the console using the command.
                    sendMsg(user, "You cannot use this command from the console!", NamedTextColor.RED);
                    return false;
                }

                // No need to check for it being a player. Will always be true (I think)
                if (args.length == 0) {
                    // User has used the croptrample.default command.
                    if (user.hasPermission("farmplugin.croptrample.default") || user.hasPermission("farmplugin.croptrample.admin")) {
                        return changeTramplePermission(((Player) user).getUniqueId(), user, user.getName());
                    } else {
                        sendMsg(user, "You do not have permission to use this command.", NamedTextColor.RED);
                        return false;
                    }
                } else {
                    sendMsg(user, "Invalid command usage. Use /croptrample.", NamedTextColor.RED);
                    return false;
                }

            case "toggletrample":
                // Make sure the correct syntax is being used when in the console.
                if (args.length == 1) {
                    // Console has automatic access. No need to check for console.
                    if (user.hasPermission("farmplugin.croptrample.admin")) {
                        if (Bukkit.getPlayerExact(args[0]) == null) {
                            sendMsg(user, "This user is not online!", NamedTextColor.RED);
                            return false;
                        }
                        return changeTramplePermission(Bukkit.getPlayerUniqueId(args[0]), user, args[0]);
                    }
                } else {
                    sendMsg(user, "Incorrect command usage. Use /toggletrample username.", NamedTextColor.RED);
                    return false;
                }
                return false;
        }
        return true;
    }

    public static void sendMsg (@NotNull CommandSender commandSender, String reason, NamedTextColor color) {
        /*
            This is where all command messages are routed. This is for 'no permission' messages, etc.
         */
        commandSender.sendMessage(Component.text(String.valueOf(reason), color));
    }

    public static boolean changeTramplePermission (java.util.UUID uuid, CommandSender user, String name) {
        InputStream fileRead;

        if (uuid == null) {
            // Since the uuid can be null when the player is not found, check for it.
            sendMsg(user, "Player not found.", NamedTextColor.RED);
            return false;
        }

        // This statement helps ensure there are also no duplicate entries.
        if (existsInYAML("croptrample.yaml", uuid.toString())) {
            // User already has crop trampling disabled.
            deleteFromYAML("croptrample.yaml", uuid.toString());
            sendMsg(
                    user,
                    "Crop trampling disabled for " + name + ".",
                    NamedTextColor.GOLD
            );
        } else {
            // User does not have crop trampling enabled.
            writeToYAML("croptrample.yaml", uuid.toString());
            sendMsg(
                    user,
                    "Crop trampling enabled for " + name + ".",
                    NamedTextColor.GOLD
            );
        }
        return true;
    }
}
