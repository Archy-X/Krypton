package com.archyx.krypton.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.archyx.krypton.Krypton;
import com.archyx.krypton.captcha.CaptchaManager;
import com.archyx.krypton.captcha.CaptchaPlayer;
import com.archyx.krypton.configuration.CaptchaMode;
import com.archyx.krypton.messages.MessageKey;
import com.archyx.krypton.util.TextUtil;
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
        plugin.getMessageManager().load();
        sender.sendMessage(plugin.getMessage(MessageKey.RELOAD));
    }

    @Subcommand("unlock")
    @CommandPermission("krypton.unlock")
    @CommandCompletion("@players")
    public void onUnlock(CommandSender sender, @Flags("other") Player player) {
        CaptchaPlayer captchaPlayer = manager.getCaptchaPlayer(player);
        if (captchaPlayer != null) {
            if (captchaPlayer.getMode() == CaptchaMode.MAP) {
                player.getInventory().setItem(0, captchaPlayer.getSlotItem());
            } else if (captchaPlayer.getMode() == CaptchaMode.MENU) {
                player.closeInventory();
            }
            manager.removeCaptchaPlayer(player);
            sender.sendMessage(TextUtil.replace(plugin.getMessage(MessageKey.UNLOCK_SENDER), "{player}", player.getName()));
            player.sendMessage(plugin.getMessage(MessageKey.UNLOCK_TARGET));
        } else {
            sender.sendMessage(TextUtil.replace(plugin.getMessage(MessageKey.UNLOCK_NOT_FOUND), "{player}", player.getName()));
        }
    }

    @Subcommand("enable")
    @CommandPermission("krypton.toggle")
    public void onEnable(CommandSender sender) {
        if (!manager.isEnabled()) {
            manager.setEnabled(true);
            sender.sendMessage(plugin.getMessage(MessageKey.ENABLE_ENABLED));
        } else {
            sender.sendMessage(plugin.getMessage(MessageKey.ENABLE_ALREADY_ENABLED));
        }
    }

    @Subcommand("disable")
    @CommandPermission("krypton.toggle")
    public void onDisable(CommandSender sender) {
        if (manager.isEnabled()) {
            manager.setEnabled(false);
            sender.sendMessage(plugin.getMessage(MessageKey.DISABLE_DISABLED));
        } else {
            sender.sendMessage(plugin.getMessage(MessageKey.DISABLE_ALREADY_DISABLED));
        }
    }

}
