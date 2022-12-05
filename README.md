# Structure Fixer

This mod bulk-updates any structure nbt files located in the `structures` folder in the minecraft folder that the mod is being run from.
The updated structure files will be placed in the `structures_new` folder. Please note that any non-structure files in the `structures` folder will be ignored.
The directory structure of the `structures` folder will be preserved in the `structures_new` folder. Make sure that you run this from the client, if you run this from the server it will not work!

This is significant as structures are data-fixed on demand when they're loaded into the game- which can cause severe lag spikes when loading many older structures at once.
Using this mod allows you to update all of your structures at once, so they're all at the latest version.

This mod *should* work with any 1.19+ version- please make an issue if it doesn't.

You can find jars at the [releases](https://github.com/SuperCoder7979/structure-fixer/releases) tab and you can discuss and report issues at my [discord server](https://discord.gg/EwQmvQtshV).