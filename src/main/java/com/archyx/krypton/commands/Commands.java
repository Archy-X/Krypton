package com.archyx.krypton.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.archyx.krypton.Krypton;
import com.archyx.krypton.captcha.CaptchaManager;
import com.archyx.krypton.captcha.CaptchaPlayer;
import com.archyx.krypton.configuration.CaptchaMode;
import com.archyx.krypton.messages.MessageKey;
import org.apache.commons.lang.StringUtils;
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
            sender.sendMessage(StringUtils.replace(plugin.getMessage(MessageKey.UNLOCK_SENDER), "{player}", player.getName()));
            player.sendMessage(plugin.getMessage(MessageKey.UNLOCK_TARGET));
        } else {
            sender.sendMessage(StringUtils.replace(plugin.getMessage(MessageKey.UNLOCK_NOT_FOUND), "{player}", player.getName()));
        }
    }
}
