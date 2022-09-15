package com.archyx.krypton.events;

import com.archyx.krypton.captcha.CaptchaActivateReason;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerCaptchaActivateEvent extends Event implements Cancellable {

    public PlayerCaptchaActivateEvent(
        final Player player,
        final CaptchaActivateReason activateReason
    ) {
        this.player = player;
        this.activateReason = activateReason;
    }

    final Player player;
    final CaptchaActivateReason activateReason;

    public Player getPlayer() { return player; }
    public CaptchaActivateReason getActivateReason() { return activateReason; }

    private boolean isCancelled;

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    private static final HandlerList handlerList = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

}
