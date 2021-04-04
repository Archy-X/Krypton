package com.archyx.krypton.captcha;

import com.archyx.krypton.Krypton;
import com.archyx.krypton.configuration.Option;
import com.archyx.krypton.configuration.OptionL;
import com.archyx.krypton.messages.MessageKey;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CaptchaMenu implements InventoryProvider {

    private final Random r = new Random();
    private final CaptchaPlayer captchaPlayer;
    private final CaptchaManager manager;
    private final Krypton plugin;
    private final int rows;
    private final ItemStack fillItem;
    private final ItemStack clickItem;

    public CaptchaMenu(CaptchaPlayer captchaPlayer, CaptchaManager manager, Krypton plugin) {
        this.captchaPlayer = captchaPlayer;
        this.manager = manager;
        this.plugin = plugin;
        this.rows = 6;
        this.fillItem = generateFillItem();
        this.clickItem = generateClickItem(fillItem);
    }

    public CaptchaMenu(CaptchaPlayer captchaPlayer, CaptchaManager manager, Krypton plugin, ItemStack fillItem, ItemStack clickItem) {
        this.captchaPlayer = captchaPlayer;
        this.manager = manager;
        this.plugin = plugin;
        this.rows = 6;
        this.fillItem = fillItem;
        this.clickItem = clickItem;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fill(ClickableItem.of(fillItem, e -> {
            if (manager.isCaptchaPlayer(player)) {
                captchaPlayer.incrementFailedAttempts();
                //Increment fail kick and check
                if (OptionL.getBoolean(Option.ENABLE_FAIL_KICK)) {
                    if (captchaPlayer.getFailedAttempts() >= OptionL.getInt(Option.FAIL_KICK_MAX_ATTEMPTS)) {
                        player.closeInventory();
                        player.kickPlayer(plugin.getMessage(MessageKey.KICK));
                    }
                }
            }
            else {
                player.closeInventory();
            }
        }));
        contents.set(r.nextInt(rows), r.nextInt(9), ClickableItem.of(clickItem, e -> {
            if (manager.isCaptchaPlayer(player)) {
                player.closeInventory();
                manager.removeCaptchaPlayer(player);
                player.sendMessage(plugin.getMessage(MessageKey.COMPLETE));
            }
            else {
                player.closeInventory();
            }
        }));
        scheduleReopen(player);
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }

    public static SmartInventory getInventory(CaptchaPlayer captchaPlayer, Krypton plugin) {
        return SmartInventory.builder()
                .provider(new CaptchaMenu(captchaPlayer, plugin.getManager(), plugin))
                .size(6, 9)
                .title(plugin.getMessage(MessageKey.MENU_TITLE))
                .manager(plugin.getInventoryManager())
                .build();
    }

    public static SmartInventory getInventory(CaptchaPlayer captchaPlayer, Krypton plugin, ItemStack fillItem, ItemStack clickItem) {
        return SmartInventory.builder()
                .provider(new CaptchaMenu(captchaPlayer, plugin.getManager(), plugin, fillItem, clickItem))
                .size(6, 9)
                .title(plugin.getMessage(MessageKey.MENU_TITLE))
                .manager(plugin.getInventoryManager())
                .build();
    }

    private void scheduleReopen(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                CaptchaPlayer captchaPlayer = manager.getCaptchaPlayer(player);
                if (captchaPlayer != null) {
                    if (!plugin.getInventoryManager().getInventory(player).isPresent()) {
                        CaptchaMenu.getInventory(captchaPlayer, plugin, fillItem, clickItem).open(player);

                    }
                }
                else {
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, OptionL.getInt(Option.MENU_REOPEN_DELAY));
    }

    private ItemStack generateFillItem() {
        List<Material> valid = getValidMaterials();
        ItemStack item = new ItemStack(valid.get(r.nextInt(valid.size())));
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(plugin.getMessage(MessageKey.MENU_FILL_ITEM));
            item.setItemMeta(meta);
        }
        return item;
    }

    public ItemStack generateClickItem(ItemStack fillItem) {
        ItemStack item;
        List<Material> valid = getValidMaterials();
        do {
            item = new ItemStack(valid.get(r.nextInt(valid.size())));
        } while (item.getType() == fillItem.getType());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(plugin.getMessage(MessageKey.MENU_CLICK_ITEM));
            item.setItemMeta(meta);
        }
        return item;
    }

    private List<Material> getValidMaterials() {
        List<Material> valid = new ArrayList<>();
        for (Material mat : Material.values()) {
            if (mat.isItem()) {
                valid.add(mat);
            }
        }
        return valid;
    }
}
