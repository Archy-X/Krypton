package com.archyx.krypton.captcha;

import java.util.UUID;

public class OfflineCaptchaPlayer {

    private final UUID id;
    private final int failedAttempts;

    public OfflineCaptchaPlayer(UUID id, int failedAttempts) {
        this.id = id;
        this.failedAttempts = failedAttempts;
    }

    public UUID getId() {
        return id;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

}
