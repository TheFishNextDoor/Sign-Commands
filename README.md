# Sign Commands
Run commands when a player clicks a sign or block.

## Setup
1. Add Sign Commands jar into your plugins folder.
2. Restart your server.

## Commands
- **/signcommands help:** Show the help message.
- **/signcommands &lt;reload | rl&gt;:** Reload the plugin.
- **/signcommands &lt;list | l&gt;:** List all command signs.
- **/signcommands &lt;info | i&gt; &lt;signName&gt;:** List a command sign’s commands and other information.
- **/signcommands &lt;goto | gt&gt; &lt;signName&gt;:** Teleport to a command sign.
- **/signcommands &lt;rename | rn&gt; &lt;newSignName&gt;:** Rename the command sign you are looking at. This resets the sign’s cooldown and max clicks.
- **/signcommands &lt;delete | dt&gt; &lt;signName&gt;:** Delete all commands from the command sign you are looking at.
- **/signcommands &lt;addcommand | ac&gt; &lt;[clickType](#sign-click-types)&gt; &lt;[commandType](#sign-command-types)&gt; &lt;command&gt;:** Add a command to the sign you are looking at.
- **/signcommands &lt;removecommand | rc&gt; &lt;commandIndex&gt;:** Remove a command from the command sign you are looking at.
- **/signcommands &lt;editcommand | ec&gt; &lt;commandIndex&gt; &lt;[clickType](#sign-click-types)&gt; &lt;[commandType](#sign-command-types)&gt; &lt;command&gt;:** Edit a command on the command sign you are looking at.
- **/signcommands &lt;addrequiredpermission | arp&gt; &lt;permission&gt;:** Add a required permission to the command sign you are looking at. Players must have **all** required permissions in order to use the command sign.
- **/signcommands &lt;removerequiredpermission | rrp&gt; &lt;permission&gt;:** Remove a required permission from the command sign you are looking at. Players must have **all** required permissions in order to use the command sign.
- **/signcommands &lt;listrequiredpermissions | lrp&gt;:** List the required permissions of the command sign you are looking at. Players must have **all** required permissions in order to use the command sign.
- **/signcommands &lt;addblockedpermission | abp&gt; &lt;permission&gt;:** Add a blocked permission to a command sign. Players must not have **any** blocked permissions in order to use the command sign.
- **/signcommands &lt;removeblockedpermission | rbp&gt; &lt;permission&gt;:** Remove a blocked permission from the command sign you are looking at. Players must not have **any** blocked permissions in order to use the command sign.
- **/signcommands &lt;listblockedpermissions | lbp&gt;:** List blocked permissions of the command sign you are looking at. Players must not have **any** blocked permissions in order to use the command sign.
- **/signcommands &lt;setglobalclickcooldown | sgcc&gt; &lt;cooldownMilliseconds&gt;:** Set the global click cooldown for the command sign you are looking at. Global click cooldown is the minimum amount of time between any player executing the command sign.
- **/signcommands &lt;resetglobalclickcooldown | rgcc&gt;:** Reset the global click cooldown of the command sign you are looking at. Global click cooldown is the minimum amount of time between any player executing the command sign.
- **/signcommands &lt;setglobalclicklimit | sgcl&gt; &lt;clickLimit&gt;:** Set the global click limit for the command sign you are looking at. Global click limit is the maximum amount of times the command sign can be executed by all players combined.
- **/signcommands &lt;resetglobalclicklimit | rgcl&gt;:** Reset the global click limit of the command sign you are looking at. Global click limit is the maximum amount of times the command sign can be executed by all players combined.
- **/signcommands &lt;setuserclickcooldown | succ&gt; &lt;cooldownMilliseconds&gt;:** Set the per-user click cooldown for the command sign you are looking at. User click cooldown is the minimum amount of time between a specific player executing the command sign.
- **/signcommands &lt;resetuserclickcooldown | rucc&gt; &lt;player | all&gt;:** Reset the per-user click cooldown for the command sign you are looking at for a specific player or all players. User click cooldown is the minimum amount of time between a specific player executing the command sign.
- **/signcommands &lt;setuserclicklimit | sucl&gt; &lt;clickLimit&gt;:** Set the per-user click limit for the command sign you are looking at. Per user click limit is the maximum amount of times each player can execute the command sign.
- **/signcommands &lt;resetuserclicklimit | rucl&gt; &lt;player | all&gt;:** Reset the per-user click limit for the command sign you are looking at for a specific player or all players. Per user click limit is the maximum amount of times each player can execute the command sign.
- **/signcommands &lt;setclickcost | scc&gt; &lt;clickCost&gt;:** Set the click cost for the command sign you are looking at. The click cost is the price a player must pay in order to execute a command sign.
- **/signedit &lt;setline | sl&gt; &lt;side&gt; &lt;lineNumber&gt; &lt;text&gt;:** Set a specific line on the sign you are looking at.
- **/signedit &lt;set | s&gt; &lt;side&gt; &lt;line1;line2;line3;line4&gt;:** Set all lines on the sign you are looking at at once.

## Permissions
- **signcommands.use (default):** Allows the player to use command signs.

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
- **signcommands.admin.globalmaxclicks**: Allows the player to manage the global max clicks on command signs.
- **signcommands.admin.userclickcooldown**: Allows the player to manage the user click cooldown on command signs.
- **signcommands.admin.usermaxclicks**: Allows the player to manage the user max clicks on command signs.
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
- **Message:** Sends a message to the player who clicked the sign.
- **Broadcast:** Broadcasts a message to all players on the server.