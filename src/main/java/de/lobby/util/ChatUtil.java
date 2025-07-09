package de.lobby.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ChatUtil {

    private static final Component PREFIX = Component.text("[1vs1] ", NamedTextColor.GRAY);

    public static Component msg(String text, NamedTextColor color) {
        return PREFIX.append(Component.text(text, color));
    }

    public static Component error(String text) {
        return msg(text, NamedTextColor.RED);
    }

    public static Component success(String text) {
        return msg(text, NamedTextColor.GREEN);
    }
}
