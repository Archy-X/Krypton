package com.archyx.krypton.messages;

public enum MessageKey {

    MAP_INFO("map.info"),
    MAP_INCORRECT("map.incorrect"),
    MENU_TITLE("menu.title"),
    MENU_FILL_ITEM("menu.fill_item"),
    MENU_CLICK_ITEM("menu.click_item"),
    KICK("kick"),
    COMPLETE("complete"),
    RELOAD("commands.reload"),
    UNLOCK_SENDER("commands.unlock.sender"),
    UNLOCK_TARGET("commands.unlock.target"),
    UNLOCK_NOT_FOUND("commands.unlock.not_found"),
    ENABLE_ENABLED("commands.enable.enabled"),
    ENABLE_ALREADY_ENABLED("commands.enable.already_enabled"),
    DISABLE_DISABLED("commands.disable.disabled"),
    DISABLE_ALREADY_DISABLED("commands.disable.already_disabled");

    private final String path;

    MessageKey(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
