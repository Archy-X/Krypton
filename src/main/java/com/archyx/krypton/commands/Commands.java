package com.archyx.krypton.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import com.archyx.krypton.Krypton;
import com.archyx.krypton.captcha.CaptchaManager;
import com.archyx.krypton.captcha.CaptchaPlayer;
import com.archyx.krypton.configuration.CaptchaMode;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("krypton|kr")
public class Commands extends BaseCommand {

    private final Krypton plugin;
    private final CaptchaManager manager;

    public Commands(Krypton plugin) {
        this.plugin = plugin;
        this.manager = plugin.getManager();
    }

    @Subcommand("reload")
    @CommandPermission("krypton.reload")
    public void onReload(CommandSender sender) {
        plugin.reloadConfig();
        plugin.getOptionL().loadOptions();
        sender.sendMessage(ChatColor.GREEN + "Config reloaded!");
    }

    @Subcommand("unlock")
    @CommandPermission("krypton.unlock")
    public void onUnlock(CommandSender sender, @Flags("other") Player player) {
        CaptchaPlayer captchaPlayer = manager.getCaptchaPlayer(player);
        if (captchaPlayer != null) {
            if (captchaPlayer.getMode() == CaptchaMode.MAP) {
                player.getInventory().setItem(0, captchaPlayer.getSlotItem());
            }
            else if (captchaPlayer.getMode() == CaptchaMode.MENU) {
                player.closeInventory();
            }
            manager.removeCaptchaPlayer(player);
            sender.sendMessage(ChatColor.GOLD + player.getName() + " has been unlocked from the captcha!");
            player.sendMessage(ChatColor.AQUA + "You have been unlocked from the captcha!");
        }
        else {
            sender.sendMessage(ChatColor.GOLD + player.getName() + ChatColor.RED + " is not in a captcha!");
        }
    }
}
