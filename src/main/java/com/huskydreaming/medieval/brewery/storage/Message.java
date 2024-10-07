package com.huskydreaming.medieval.brewery.storage;

import com.google.common.base.Functions;
import com.huskydreaming.medieval.brewery.utils.Parseable;
import com.huskydreaming.medieval.brewery.utils.TextUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public enum Message implements Parseable {

    // General Menu Items
    GENERAL_PREFIX("#b5e74aBrewery: #b5b5b5"),
    GENERAL_BARREL("The barrel is already a brewery."),
    GENERAL_BLOCK("Could not find a brewery."),
    GENERAL_CREATE("You have created a brewery."),
    GENERAL_RECIPE("You must provide a valid recipe."),
    GENERAL_IN_PROGRESS("There is already a brew in progress."),
    GENERAL_LIMIT("You can only have &f<0>#b5b5b5 brewery at a time."),
    GENERAL_NOTIFY("The <0><1> #b5b5b5brew is ready for consumption."),
    GENERAL_OWNER("You must be owner to remove the brewery."),
    GENERAL_RELOAD("Successfully reloaded the plugin!"),
    GENERAL_REMOVE("You have removed the brewery."),
    GENERAL_WORLD_GUARD_PROTECTED("You are not able to create a brewery inside a protected region."),
    TITLE_IDLE_HEADER("#b5e74aBrewery"),
    TITLE_IDLE_FOOTER("#b5b5b5Open barrel to begin!"),
    TITLE_READY_HEADER("<0><1>"),
    TITLE_READY_FOOTER("#b5b5b5Ready to collect! #b4e52a<0>#75902c/#b4e52a<1>"),
    TITLE_TIME_HEADER("<0><1>"),
    TITLE_TIME_FOOTER("#b5b5b5Finishes in #b5e74a<0>");


    private final String def;
    private final List<String> list;
    private static FileConfiguration menuConfiguration;

    Message(String def) {
        this.def = def;
        this.list = null;
    }

    Message(List<String> list) {
        this.list = list;
        this.def = null;
    }

    @Override
    public String prefix(Object... objects) {
        return TextUtils.hex(GENERAL_PREFIX.parse() + parameterize(objects));
    }

    @Nullable
    public String parse() {
        return menuConfiguration.getString(toString(), def);
    }

    @Nullable
    public List<String> parseList() {
        List<?> objects = menuConfiguration.getList(toString(), list);
        if (objects == null) return null;
        return objects.stream().map(Functions.toStringFunction()).collect(Collectors.toList());
    }

    @NotNull
    public String toString() {
        String path = name().toLowerCase();
        if(name().contains("TITLE")) {
            return path.replace("_", ".");
        } else {
            int length = path.split("_")[0].length() + 1;
            String qualifier = path.substring(0, length).replace("_", ".");
            String subString = path.substring(length);
            return qualifier + subString;
        }
    }

    public static void load(Yaml yaml) {
        Message.menuConfiguration = yaml.getConfiguration();
        for (Message message : Message.values()) {
            menuConfiguration.set(message.toString(), message.parseList() != null ? message.parseList() : message.parse());
        }
        yaml.save();
    }
}