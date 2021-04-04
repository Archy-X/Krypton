package com.archyx.krypton.captcha;

import com.archyx.krypton.configuration.CaptchaMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class CaptchaPlayer {

    private final Player player;
    private final CaptchaMode mode;
    private String mapCode;
    private ItemStack slotItem;
    private int failedAttempts;
    private long captchaStartTime;
    private boolean allowMove;
    private boolean allowCommands;

    public CaptchaPlayer(Player player, CaptchaMode mode, int failedAttempts) {
        this.player = player;
        this.mode = mode;
        this.failedAttempts = failedAttempts;
        allowMove = false;
        allowCommands = false;
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

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public long getCaptchaStartTime() {
        return captchaStartTime;
    }

    public void setCaptchaStartTime(long captchaStartTime) {
        this.captchaStartTime = captchaStartTime;
    }

    public boolean isAllowMove() {
        return allowMove;
    }

    public void setAllowMove(boolean allowMove) {
        this.allowMove = allowMove;
    }

    public boolean isAllowCommands() {
        return allowCommands;
    }

    public void setAllowCommands(boolean allowCommands) {
        this.allowCommands = allowCommands;
    }

}
