package com.archyx.krypton.captcha;

import com.archyx.krypton.configuration.CaptchaMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class CaptchaPlayer {

    private final Player player;
    private final CaptchaMode mode;
    private String mapCode;
    private ItemStack slotItem;
    private int totalFailedAttempts;
    private int failedAttempts;
    private boolean allowMove;

    public CaptchaPlayer(Player player, CaptchaMode mode, int totalFailedAttempts) {
        this.player = player;
        this.mode = mode;
        this.totalFailedAttempts = totalFailedAttempts;
        allowMove = false;
    }

    public Player getPlayer() {
        return player;
    }

    public CaptchaMode getMode() {
        return mode;
    }

    public String getMapCode() {
        return mapCode;
    }

    public void setMapCode(String mapCode) {
        this.mapCode = mapCode;
    }

    public ItemStack getSlotItem() {
        return slotItem;
    }

    public void setSlotItem(ItemStack slotItem) {
        this.slotItem = slotItem;
    }

    public int getTotalFailedAttempts() {
        return totalFailedAttempts;
    }

    public void setTotalFailedAttempts(int totalFailedAttempts) {
        this.totalFailedAttempts = totalFailedAttempts;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void incrementFailedAttempts() {
        this.failedAttempts++;
        this.totalFailedAttempts++;
    }

    public boolean isAllowMove() {
        return allowMove;
    }

    public void setAllowMove(boolean allowMove) {
        this.allowMove = allowMove;
    }

}
