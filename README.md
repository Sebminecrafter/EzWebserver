# EzWebserver

#### by Sebminecrafter

## Overview

EzWebserver is a plugin for Bukkit, Spigot, PaperMC or any other fork,
that allows running an HTTP server on the same Java instance as
your Minecraft server.

## Installation

1. Download the latest version from the [Modrinth page](https://modrinth.com/plugin/ezwebserver)
   or [Github Releases](https://github.com/sebminecrafter/EzWebserver/releases).
2. Copy the `.jar` file into your server's `plugins/` directory
3. Restart your server

You can now edit the `config.yml` or the files in `plugins/EzWebserver/public/`

## Placeholders

EzWebserver supports PlaceholderAPI,
and has built-in placeholders,
all of which can be used in content to be served by EzWebserver.
For built-in placeholders, surround them with curly braces. (e.g. `{{player_count}}`)
PlaceholderAPI placeholders are surrounded by percent symbols, like normal. (e.g. `%players_online%`)

### Built-in placeholders

The built-in placeholders are:

- `player_count` - Online players
- `max_players` - Max online players
- `server_name` - Server brand (e.g. Paper, Bukkit, Spigot)
- `server_version` - Game version of the server
- `motd` - Message of the Day, from `server.properties`
- `player_list` - The full player list

These placeholders are all obtained via the Bukkit API,
and therefore may not be the same as what is displayed to the client
from AdvancedServerList, MiniMOTD, or other plugins that change server display.

### Valid "text" filetypes

EzWebserver does not parse all files for placeholders,
only filetypes that are known as text.

The list of these filetypes is as shown:

- `.html` or `.htm`
- `.txt`
- `.css`
- `.js`
- `.json`
- `.xml`