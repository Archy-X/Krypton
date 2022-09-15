package com.archyx.krypton.events;

import com.archyx.krypton.captcha.CaptchaPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerQuitDuringCaptchaEvent extends Event {

    public PlayerQuitDuringCaptchaEvent(
        final CaptchaPlayer captchaPlayer
    ) {
        this.captchaPlayer = captchaPlayer;
    }
    final CaptchaPlayer captchaPlayer;

    public CaptchaPlayer getCaptchaPlayer() { return captchaPlayer; }

    private static final HandlerList handlerList = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

}
