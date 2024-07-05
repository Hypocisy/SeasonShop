package com.kumoe.SeasonShop.data;

import com.kumoe.SeasonShop.init.SeasonShop;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Locale;

public enum SSLangData {

    SHIPPING_BIN_TOOLTIP_1("block.season_shop.shipping_bin.tooltip_1", "单价: %s", 1, ChatFormatting.DARK_AQUA),
    SHIPPING_BIN_TOOLTIP_2("block.season_shop.shipping_bin.tooltip_2", "总价: %s", 1, ChatFormatting.YELLOW),
    SHIPPING_BIN_TIPS("block.season_shop.shipping_bin.tips", "You can only place up to %s of these blocks!", 1, ChatFormatting.RED),
    ;
    private final String key, def;
    private final int arg;
    private final ChatFormatting format;

    SSLangData(String key, String def, int arg, ChatFormatting format) {
        this.key = SeasonShop.MODID + "." + key;
        this.def = def;
        this.arg = arg;
        this.format = format;
    }

    public static String asId(String name) {
        return name.toLowerCase(Locale.ROOT);
    }

    public static void genLang(RegistrateLangProvider pvd) {
        for (SSLangData lang : SSLangData.values()) {
            pvd.add(lang.key, lang.def);
        }
    }

    public MutableComponent get(Object... args) {
        if (args.length != arg)
            throw new IllegalArgumentException("for " + name() + ": expect " + arg + " parameters, got " + args.length);
        MutableComponent ans = Component.translatable(key, args);
        return ans.withStyle(format);
    }

    public String key() {
        return key;
    }

    public ChatFormatting format() {
        return format;
    }
}
