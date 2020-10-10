package com.archyx.xcaptcha;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import com.archyx.xcaptcha.configuration.CaptchaMode;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("xcaptcha|xc")
public class Commands extends BaseCommand {

    private final XCaptcha plugin;
    private final CaptchaManager manager;

    public Commands(XCaptcha plugin) {
        this.plugin = plugin;
        this.manager = plugin.getManager();
    }

    @Subcommand("reload")
    @CommandPermission("xcaptcha.reload")
    public void onReload(CommandSender sender) {
        plugin.reloadConfig();
        plugin.getOptionL().loadOptions();
        sender.sendMessage(ChatColor.GREEN + "Config reloaded!");
    }

    @Subcommand("unlock")
    @CommandPermission("xcaptcha.unlock")
    public void onUnlock(CommandSender sender, @Flags("other") Player player) {
        CaptchaPlayer captchaPlayer = manager.getCaptchaPlayer(player);
        if (captchaPlayer != null) {
            if (captchaPlayer.getMode() == CaptchaMode.MAP) {
                player.getInventory().setItem(0, captchaPlayer.getSlotItem());
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
