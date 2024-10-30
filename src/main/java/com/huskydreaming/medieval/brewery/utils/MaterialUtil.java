package com.huskydreaming.medieval.brewery.utils;

import org.bukkit.Material;

public class MaterialUtil {

    public static  String formatName(Material material) {
        String[] words = material.toString().toLowerCase().split("_");
        StringBuilder formattedName = new StringBuilder();

        for (String word : words) {
            formattedName.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
        }

        return formattedName.toString().trim();
    }
}
