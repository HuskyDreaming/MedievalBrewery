package com.huskydreaming.medieval.brewery.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Json {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public static void write(Plugin plugin, String fileName, Object object) {
        Path path = create(plugin, fileName);
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            GSON.toJson(object, bufferedWriter);
        } catch (IOException e) {
            plugin.getLogger().severe(e.getMessage());
        }
    }

    public static <T> T read(Plugin plugin, String fileName, Type type) {
        Path path = create(plugin, fileName);
        BufferedReader bufferedReader;
        try {
            bufferedReader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return null;
        }

        JsonReader jsonReader = new JsonReader(bufferedReader);
        try {
            return GSON.fromJson(jsonReader, type);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }

    public static Path create(Plugin plugin, String fileName) {
        Path path = Paths.get(plugin.getDataFolder() + File.separator + fileName + ".json");
        Path parentPath = path.getParent();
        try {
            if (!Files.exists(parentPath)) {
                Files.createDirectories(parentPath);
            }
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
        } catch (IOException e) {
            plugin.getLogger().severe(e.getMessage());
        }
        return path;
    }
}
