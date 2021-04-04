package com.archyx.krypton.configuration;

public enum Option {

    CAPTCHA_EVERY_JOIN("captcha.every_join", OptionType.BOOLEAN),
    ENABLE_FAIL_KICK("captcha.enable_fail_kick", OptionType.BOOLEAN),
    FAIL_KICK_MAX_ATTEMPTS("captcha.fail_kick_max_attempts", OptionType.INT),
    MAP_CODE_LENGTH("captcha.map.code_length", OptionType.INT),
    MAP_USE_NUMBERS("captcha.map.use_numbers", OptionType.BOOLEAN),
    MAP_USE_CAPITAL_LETTERS("captcha.map.use_capital_letters", OptionType.BOOLEAN),
    MAP_BLOCK_LOOK("captcha.map.block_look", OptionType.BOOLEAN),
    MAP_USE_PROTOCOL_LIB("captcha.map.use_protocol_lib", OptionType.BOOLEAN),
    MAP_FORCE_PITCH_ENABLED("captcha.map.force_pitch.enabled", OptionType.BOOLEAN),
    MAP_FORCE_PITCH_PITCH("captcha.map.force_pitch.pitch", OptionType.DOUBLE),
    MAP_ALLOW_MOVE_ENABLED("captcha.map.allow_move.enabled", OptionType.BOOLEAN),
    MAP_ALLOW_MOVE_DURATION_TICKS("captcha.map.allow_move.duration_ticks", OptionType.INT),
    MENU_REOPEN_DELAY("captcha.menu.reopen_delay", OptionType.INT);

    private final String path;
    private final OptionType type;

    Option(String path, OptionType type) {
        this.path = path;
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public OptionType getType() {
        return type;
    }

}
