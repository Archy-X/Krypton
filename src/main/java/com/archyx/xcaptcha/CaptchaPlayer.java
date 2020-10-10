package com.archyx.xcaptcha;

import com.archyx.xcaptcha.configuration.CaptchaMode;
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

    public CaptchaPlayer(Player player, CaptchaMode mode) {
        this.player = player;
        this.mode = mode;
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

}
