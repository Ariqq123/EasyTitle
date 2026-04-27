# EasyTitle 🏆

![Minecraft Version](https://img.shields.io/badge/Minecraft-1.8.8%20--%201.21+-brightgreen.svg)
![Java Version](https://img.shields.io/badge/Java-8%2B-orange.svg)
![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)

**EasyTitle** is a lightweight, fully backwards-compatible Minecraft server plugin that allows server administrators and players with permissions to easily broadcast and send beautifully formatted screen titles, subtitles, and action bars using simple commands. 

Developed by **azreyzaako**, EasyTitle completely replaces the cumbersome vanilla `/title` command with an intuitive, staged workflow that fully supports the modern [MiniMessage](https://docs.advntr.dev/minimessage/format.html) formatting standard (e.g., `<red>`, `<gradient:gold:yellow>`, `<rainbow>`).

## ✨ Key Features
- **MiniMessage Support**: Create stunning gradients, hex colors, and interactive chat components effortlessly.
- **Legacy Color Support**: Seamlessly mix classic ampersand color codes (e.g. `&a`, `&l`, `&4`) with modern MiniMessage tags.
- **Cross-Version Compatibility**: Native support from **1.8.8 up to the latest 1.21+** via Adventure platform abstraction. Java 8 compatible!
- **PlaceholderAPI Support**: Seamlessly parses PAPI placeholders (`%player_name%`, `%vault_eco_balance%`) per-player before the title is shown!
- **Sound Effects**: Play a custom sound effect (with volume & pitch) to grab players' attention when the title is broadcasted!
- **Interactive Management GUI**: Use `/etitle gui` to open an inventory control panel. Thanks to **AnvilGUI** integration, you can click any item to seamlessly type your text in-game without using chat commands!
- **Staging Workflow**: Stage your title and subtitle separately, tweak timings, and then broadcast, ensuring no mistakes are shown to players.
- **Action Bar Messages**: Send beautiful action bar messages seamlessly.
- **Fully Customizable**: All command messages and default timings are fully configurable via `config.yml`.

## 📦 Installation
1. Download the latest jar file from the [Releases](https://github.com/Ariqq123/EasyTitle/releases/latest) page.
2. Drop it into your Minecraft server's `plugins/` directory.
3. Restart your server.
4. Modify `plugins/EasyTitle/config.yml` to your liking!

## ⌨️ Commands & Usage
The primary command is `/etitle`. Permission node required: `easytitle.use` (Default: OP).

| Command | Description |
|---|---|
| `/etitle title <text>` | Stages the main title. Supports MiniMessage and legacy `&` codes. |
| `/etitle subtitle <text>` | Stages the subtitle. Supports MiniMessage and legacy `&` codes. |
| `/etitle actionbar <text>` | Stages the action bar. Supports MiniMessage and legacy `&` codes. |
| `/etitle sound <sound> [v] [p]` | Stages a sound effect (e.g. `ENTITY_PLAYER_LEVELUP`) with optional volume and pitch. |
| `/etitle times <fadeIn> <stay> <fadeOut>`| Configures title timings in server ticks (20 ticks = 1 sec). |
| `/etitle preview` | Shows the staged title to yourself. |
| `/etitle gui` | Opens the AnvilGUI-powered interactive menu to manage and edit your staged items. |
| `/etitle send <target>` | Sends your currently staged title, subtitle, and action bar to a specific player or all players online (`*`). |
| `/etitle broadcast` | Alias for `/etitle send *`. |
| `/etitle clear [target]` | Instantly removes the active title from your screen, a specific player's screen, or everyone's screen (`*`). |
| `/etitle reset [target]` | Resets the title and its timings back to defaults for a specific player or everyone (`*`). |
| `/etitle reload` | Reloads `config.yml`. |

## 🎨 Color Reference
You can freely mix Legacy `&` codes and modern `<color>` tags in your titles! Here are all the supported standard colors and formats:

| Legacy | MiniMessage Tag | Legacy | MiniMessage Tag |
|:---:|:---|:---:|:---|
| `&0` | `<black>` | `&8` | `<dark_gray>` |
| `&1` | `<dark_blue>` | `&9` | `<blue>` |
| `&2` | `<dark_green>` | `&a` | `<green>` |
| `&3` | `<dark_aqua>` | `&b` | `<aqua>` |
| `&4` | `<dark_red>` | `&c` | `<red>` |
| `&5` | `<dark_purple>` | `&d` | `<light_purple>` |
| `&6` | `<gold>` | `&e` | `<yellow>` |
| `&7` | `<gray>` | `&f` | `<white>` |

**Formatting:**
- `&l` = `<bold>`
- `&o` = `<italic>`
- `&n` = `<underlined>`
- `&m` = `<strikethrough>`
- `&k` = `<obfuscated>`
- `&r` = `<reset>`

*Tip: MiniMessage also supports Hex colors! Example: `<#ff5555>This is custom red!`*

## ⚙️ Configuration
The configuration allows you to define the prefix for all plugin messages, localization, and default title display lengths.

```yaml
defaults:
  fade-in: 10    # 0.5 seconds
  stay: 70       # 3.5 seconds
  fade-out: 20   # 1.0 seconds
```

**Example Workflow:**
```text
/etitle title <gradient:red:gold>&lBoss Incoming!</gradient>
/etitle subtitle <gray>Prepare your weapons! &c&lNOW!
/etitle actionbar <yellow>Spawns in 10 seconds...
/etitle times 10 100 20
/etitle preview
/etitle broadcast
```

## ⚙️ Configuration
The configuration allows you to define the prefix for all plugin messages, localization, and default title display lengths.

```yaml
defaults:
  fade-in: 10    # 0.5 seconds
  stay: 70       # 3.5 seconds
  fade-out: 20   # 1.0 seconds
```

## 📜 License
This plugin is available under the MIT License. Feel free to fork, modify, and distribute!
