# Sign Commands
Run commands when a player clicks a sign or block.

## Requirements
- Spigot or Paper server (Minecraft 1.20+)
- Java 21+
- [Vault](https://www.spigotmc.org/resources/vault.34315/) (optional, required for click cost feature)

## Setup
1. Download the SignCommands jar from the releases page or build it from source with `mvn clean package`.
2. Place the jar in your server's `plugins` folder.
3. Restart your server.

## Usage Tips
- Sneak (hold shift) while clicking a command sign to interact with it normally without triggering commands.
- Sneak while breaking a command sign to remove the block (requires permission).
- Command signs are protected from being broken or modified by players without permission.
- Use `{player}` in any command to substitute the clicking player's name.
- Use `&` color codes in Message and Broadcast command types (e.g. `&aGreen text`).

## Commands

Command aliases: `/signcommands` can be shortened to `/sc`.

- **/signcommands help:** Show the help message.
- **/signcommands \<reload | rl>:** Reload the plugin.
- **/signcommands \<list | l>:** List all command signs.
- **/signcommands \<info | i> \<signName>:** List a command sign's commands and other information.
- **/signcommands \<goto | gt> \<signName>:** Teleport to a command sign.
- **/signcommands \<rename | rn> \<newSignName>:** Rename the command sign you are looking at. This resets the sign's cooldown and max clicks.
- **/signcommands \<delete | dt> \<signName>:** Delete a command sign by name.
- **/signcommands \<addcommand | ac> \<[clickType](#sign-click-types)> \<[commandType](#sign-command-types)> \<command>:** Add a command to the sign you are looking at.
- **/signcommands \<removecommand | rc> \<commandIndex>:** Remove a command from the command sign you are looking at.
- **/signcommands \<editcommand | ec> \<commandIndex> \<[clickType](#sign-click-types)> \<[commandType](#sign-command-types)> \<command>:** Edit a command on the command sign you are looking at.
- **/signcommands \<addrequiredpermission | arp> \<permission>:** Add a required permission to the command sign you are looking at. Players must have **all** required permissions in order to use the command sign.
- **/signcommands \<removerequiredpermission | rrp> \<permission>:** Remove a required permission from the command sign you are looking at.
- **/signcommands \<listrequiredpermissions | lrp>:** List the required permissions of the command sign you are looking at.
- **/signcommands \<addblockedpermission | abp> \<permission>:** Add a blocked permission to the command sign you are looking at. Players must not have **any** blocked permissions in order to use the command sign.
- **/signcommands \<removeblockedpermission | rbp> \<permission>:** Remove a blocked permission from the command sign you are looking at.
- **/signcommands \<listblockedpermissions | lbp>:** List blocked permissions of the command sign you are looking at.
- **/signcommands \<setglobalclickcooldown | sgcc> \<cooldownMilliseconds>:** Set the global click cooldown for the command sign you are looking at. Global click cooldown is the minimum amount of time between any player executing the command sign.
- **/signcommands \<resetglobalclickcooldown | rgcc>:** Reset the global click cooldown of the command sign you are looking at.
- **/signcommands \<setglobalclicklimit | sgcl> \<clickLimit>:** Set the global click limit for the command sign you are looking at. Global click limit is the maximum amount of times the command sign can be executed by all players combined.
- **/signcommands \<resetglobalclicklimit | rgcl>:** Reset the global click limit of the command sign you are looking at.
- **/signcommands \<setuserclickcooldown | succ> \<cooldownMilliseconds>:** Set the per-user click cooldown for the command sign you are looking at.
- **/signcommands \<resetuserclickcooldown | rucc> \<player | all>:** Reset the per-user click cooldown for the command sign you are looking at for a specific player or all players.
- **/signcommands \<setuserclicklimit | sucl> \<clickLimit>:** Set the per-user click limit for the command sign you are looking at. Per user click limit is the maximum amount of times each player can execute the command sign.
- **/signcommands \<resetuserclicklimit | rucl> \<player | all>:** Reset the per-user click limit for the command sign you are looking at for a specific player or all players.
- **/signcommands \<setclickcost | scc> \<clickCost>:** Set the click cost for the command sign you are looking at. Requires Vault. The click cost is the price a player must pay in order to execute a command sign.

### Sign Edit Commands

Command aliases: `/signedit` can be shortened to `/se` or `/editsign`.

- **/signedit \<setline | sl> \<side> \<lineNumber> \<text>:** Set a specific line on the sign you are looking at.
- **/signedit \<set | s> \<side> \<line1;line2;line3;line4>:** Set all lines on the sign you are looking at at once.

## Permissions
- **signcommands.use:** Allows the player to use command signs.

**Admin Permissions**
- **signcommands.admin.reload**: Allows the player to reload the SignCommands configuration.
- **signcommands.admin.info**: Allows the player to view information about a command sign.
- **signcommands.admin.list**: Allows the player to list all command signs.
- **signcommands.admin.goto**: Allows the player to teleport to a command sign.
- **signcommands.admin.rename**: Allows the player to rename a command sign.
- **signcommands.admin.delete**: Allows the player to delete a command sign.
- **signcommands.admin.commands**: Allows the player to manage commands on command signs.
- **signcommands.admin.permissions**: Allows the player to manage permissions on command signs.
- **signcommands.admin.globalclickcooldown**: Allows the player to manage the global click cooldown on command signs.
- **signcommands.admin.globalclicklimit**: Allows the player to manage the global click limit on command signs.
- **signcommands.admin.userclickcooldown**: Allows the player to manage the user click cooldown on command signs.
- **signcommands.admin.userclicklimit**: Allows the player to manage the user click limit on command signs.
- **signcommands.admin.clickcost**: Allows the player to manage the click cost on command signs.

**Sign Edit Permissions**
- **signedit.use**: Allows the player to use the sign edit command.
- **signedit.color**: Allows the player to use color codes on signs.
- **signedit.bypassprotections**: Allows the player to bypass protections when editing signs.

## Config
```yaml
only-allow-signs: true # If true, only allow signs to have commands assigned to them. If false, any block can have commands assigned.
sign-click-delay-ticks: 5 # The number of ticks a player must wait between running sign commands.
```

## Sign Click Types
- **Left-Click:** Triggers the command when the player left clicks the sign.
- **Right-Click:** Triggers the command when the player right clicks the sign.
- **Any-Click:** Triggers the command when the player left or right clicks the sign.

## Sign Command Types
- **Console:** Runs the command as the server console.
- **Player:** Runs the command as the player who clicked the sign.
- **Op:** Runs the command as the player who clicked the sign ignoring permission requirements.
- **Message:** Sends a message to the player who clicked the sign. Supports `&` color codes.
- **Broadcast:** Broadcasts a message to all players on the server. Supports `&` color codes.
