package com.huskydreaming.medieval.brewery.commands;

import com.huskydreaming.huskycore.HuskyPlugin;
import com.huskydreaming.huskycore.commands.annotations.CommandAnnotation;
import com.huskydreaming.huskycore.commands.providers.PlayerCommandProvider;
import com.huskydreaming.medieval.brewery.data.Effect;
import com.huskydreaming.medieval.brewery.data.Ingredient;
import com.huskydreaming.medieval.brewery.data.Item;
import com.huskydreaming.medieval.brewery.data.Recipe;
import com.huskydreaming.medieval.brewery.enumerations.Message;
import com.huskydreaming.medieval.brewery.handlers.interfaces.ConfigHandler;
import com.huskydreaming.medieval.brewery.repositories.interfaces.RecipeRepository;
import com.huskydreaming.medieval.brewery.utils.MaterialUtil;
import com.huskydreaming.medieval.brewery.utils.TimeUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

@CommandAnnotation(label = "recipe")
public class RecipeCommand implements PlayerCommandProvider {

    private final ConfigHandler configHandler;
    private final RecipeRepository recipeRepository;

    public RecipeCommand(HuskyPlugin plugin) {
        this.recipeRepository = plugin.provide(RecipeRepository.class);
        this.configHandler = plugin.provide(ConfigHandler.class);
    }

    @Override
    public void onCommand(Player player, String[] strings) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        if (meta == null) return;

        meta.setTitle("Recipe Book");
        meta.setAuthor("Brewery Master");

        for (Recipe recipe : recipeRepository.getRecipes()) {
            StringBuilder pageContent = new StringBuilder();
            Item item = recipe.getItem();
            String title = ChatColor.translateAlternateColorCodes('&', item.getDisplayName());
            pageContent.append(ChatColor.BLUE).append(ChatColor.stripColor(title)).append("\n");

            pageContent.append(ChatColor.DARK_GRAY).append(item.getDescription()).append("\n\n");

            int neededWater = recipe.getWater();
            pageContent.append(ChatColor.BLACK).append("Water: ").append(ChatColor.DARK_GRAY).append(neededWater).append(" Buckets\n");

            int uses = recipe.getUses();
            pageContent.append(ChatColor.BLACK).append("Uses: ").append(ChatColor.DARK_GRAY).append(uses).append("\n");

            int seconds = recipe.getSeconds();
            String brewTime = TimeUtil.format(seconds);
            pageContent.append(ChatColor.BLACK).append("Brew Time: ").append(ChatColor.DARK_GRAY).append(brewTime).append("\n");


            pageContent.append(ChatColor.BLACK).append("Ingredients:\n").append(ChatColor.DARK_GRAY);
            for (Ingredient ingredient : recipe.getIngredients()) {
                String ingredientName = MaterialUtil.formatName(ingredient.getMaterial());
                int amount = ingredient.getAmount();
                pageContent.append("- ").append(ingredientName).append(" x").append(amount).append("\n");
            }

            pageContent.append(ChatColor.BLACK).append("Effects:\n").append(ChatColor.DARK_GRAY);
            for (Effect effect : recipe.getEffects()) {
                String durationTime = TimeUtil.format(effect.duration());
                String effectName = effect.type().toLowerCase();
                int level = effect.amplifier();
                pageContent.append("- ").append(effectName).append(" ").append(durationTime).append(", Lvl ").append(level).append("\n");
            }

            meta.addPage(pageContent.toString());
        }

        book.setItemMeta(meta);

        if(configHandler.isReceiveBook()) {
            player.getInventory().addItem(book);
            player.sendMessage(Message.GENERAL_RECIPE_BOOK.prefix());
        } else {
            player.openBook(book);
        }
    }
}