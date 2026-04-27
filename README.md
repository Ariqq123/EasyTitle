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
- **Staging Workflow**: Stage your title and subtitle separately, tweak timings, and then broadcast, ensuring no mistakes are shown to players.
- **Management GUI**: Use `/etitle gui` to open an interactive control panel to preview, broadcast, edit timings, and clear your staged titles!
- **Action Bar Messages**: Send beautiful action bar messages seamlessly.
- **Fully Customizable**: All command messages and default timings are fully configurable via `config.yml`.

## 📦 Installation
1. Download the latest `easytitle-1.0.0.jar` from the Releases page.
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
| `/etitle times <fadeIn> <stay> <fadeOut>`| Configures title timings in server ticks (20 ticks = 1 sec). |
| `/etitle preview` | Shows the staged title to yourself. |
| `/etitle gui` | Opens an interactive inventory GUI to manage your staged items and timings. |
| `/etitle send <target>` | Sends your currently staged title, subtitle, and action bar to a specific player or all players online (`*`). |
| `/etitle broadcast` | Alias for `/etitle send *`. |
| `/etitle clear [target]` | Instantly removes the active title from your screen, a specific player's screen, or everyone's screen (`*`). |
| `/etitle reset [target]` | Completely resets the active title and restores default display timings for yourself, a target player, or everyone (`*`). |
| `/etitle reload` | Reloads the configuration file. |

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
