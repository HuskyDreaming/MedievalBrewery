
# Medieval Brewery
![Spigot](https://img.shields.io/badge/Spigot-1.19_--_1.21.1-yellow.svg)
![Version](https://img.shields.io/badge/Version-1.5-lightgray.svg)

Medieval Brewery is a customizable brewing plugin designed for Minecraft servers running on Spigot. It adds an brewing system where players can craft various medieval-inspired drinks, complete with unique ingredients, brewing times, and special effects. Whether you’re creating potent potions like ale, wine, or mead, or experimenting with new recipes, Medieval Brewery offers a rich and engaging experience for players who want to add more depth to their survival or role-playing gameplay.



## Features

- **Easily create barrels**: Craft and use barrels to store and age your brews.
- **Holograms**: Visualize brewing progress without the need for extra dependencies.
- **Custom Recipes**: Define unique recipes using Minecraft’s existing items to brew drinks like beer, wine, cider, whisky, mead, and more.
- **Custom Qualities**: Implement varying qualities for each brew, affecting the potency and value of the drinks.
- **Custom Effects**: Assign special effects to each recipe, from beneficial buffs to fun or challenging debuffs.
- **100% Translatable**: Full translation support for multiple languages, ensuring a localized experience for players worldwide.
- **Hex Colour Support**: Customize your drink labels and messages with hex color codes for personalized aesthetics.
- **Configuration Options**: Fine-tune the plugin’s behavior with configuration settings to match your server's needs.
- **Super Optimized**: Built with performance in mind, ensuring minimal impact on server resources.

With Medieval Brewery, your players can enjoy a fully immersive brewing experience, crafting a variety of beverages that add depth to Minecraft’s gameplay. Whether it’s brewing for fun or for strategic benefits, this plugin is designed to provide endless possibilities.
## Dependencies

- **JAVA 17+**
- **Worldguard** (optional)
## Installation

1. Download the latest version of **Medieval Brewery**.
2. Place the downloaded `.jar` file into your `plugins` folder (for Bukkit, Spigot, or Paper).
3. Restart your server or reload the plugin (`/reload` or `/restart`) to activate it.

    
## Configuration
After installation, you can adjust settings via the `config.yml`:
```yaml
# Which language file to use
language: en_us

# How many breweries per player
brewery-limit: 3

# notify player when their brew has finished
notify-player: true

# Enable qualities
# When you change this, it will break the already brewed drinks
qualities: true
````
## Commands

The Medieval Brewery plugin provides the following commands:

- **/brewery reload**: Reloads the plugin configuration. (Requires permission: `brewery.admin`)
- **/brewery remove**: Hides your shop in the Shop Overview. (Requires permission: `brewery.admin`)
## Authors

- [@HuskyDreaming](https://github.com/HuskyDreaming)


## LICENSE
This plugin is released under the MIT License. See the [LICENSE](LICENSE) file for details.
