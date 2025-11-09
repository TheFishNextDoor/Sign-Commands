# Sign Commands Plugin
Run commands when a player clicks a sign or block.

## Setup
- Add Sign Commands jar into your plugins folder
- Restart your server

## Commands
- **/signcommands help:** Show this help message.
- **/signcommands &lt;reload | rl&gt;:** Reload the plugin.
- **/signcommands &lt;list | l&gt;:** List all command signs.
- **/signcommands &lt;info | i&gt; &lt;signName&gt;:** List a command sign’s commands and other information.
- **/signcommands &lt;goto | gt&gt; &lt;signName&gt;:** Teleport to a command sign.
- **/signcommands &lt;rename | rn&gt; &lt;newSignName&gt;:** Rename a command sign. This resets the sign’s cooldown and max clicks.
- **/signcommands &lt;delete | dt&gt; &lt;signName&gt;:** Delete all commands from a command sign.
- **/signcommands &lt;addcommand | ac&gt; &lt;clickType&gt; &lt;commandType&gt; &lt;command&gt;:** Add a command to a command sign.  
- **/signcommands &lt;removecommand | rc&gt; &lt;commandIndex&gt;:** Remove a command from a command sign.  
- **/signcommands &lt;editcommand | ec&gt; &lt;commandIndex&gt; &lt;clickType&gt; &lt;commandType&gt; &lt;command&gt;:** Edit a command on a command sign.  
- **/signcommands &lt;addrequiredpermission | arp&gt; &lt;permission&gt;:** Add a required permission to a command sign (players must have **all** required permissions).  
- **/signcommands &lt;removerequiredpermission | rrp&gt; &lt;permission&gt;:** Remove a required permission from a command sign.  
- **/signcommands &lt;listrequiredpermissions | lrp&gt;:** List required permissions of a command sign.  
- **/signcommands &lt;addblockedpermission | abp&gt; &lt;permission&gt;:** Add a blocked permission to a command sign (players with **any** blocked permission cannot use the sign).  
- **/signcommands &lt;removeblockedpermission | rbp&gt; &lt;permission&gt;:** Remove a blocked permission from a command sign.  
- **/signcommands &lt;listblockedpermissions | lbp&gt;:** List blocked permissions of a command sign.   
- **/signcommands &lt;setglobalclickcooldown | sgcc&gt; &lt;cooldownMilliseconds&gt;:** Set the global click cooldown for a command sign.  
- **/signcommands &lt;resetglobalclickcooldown | rgcc&gt;:** Reset the global click cooldown.  
- **/signcommands &lt;setglobalclicklimit | sgcl&gt; &lt;clickLimit&gt;:** Set the global click limit for a command sign.  
- **/signcommands &lt;resetglobalclicklimit | rgcl&gt;:** Reset the global click limit.  
- **/signcommands &lt;setuserclickcooldown | succ&gt; &lt;cooldownMilliseconds&gt;:** Set the per-user click cooldown for a command sign.  
- **/signcommands &lt;resetuserclickcooldown | rucc&gt; &lt;player | all&gt;:** Reset the per-user click cooldown for a player or all players.  
- **/signcommands &lt;setuserclicklimit | sucl&gt; &lt;clickLimit&gt;:** Set the per-user click limit for a command sign.  
- **/signcommands &lt;resetuserclicklimit | rucl&gt; &lt;player | all&gt;:** Reset the per-user click limit for a player or all players.  
- **/signcommands &lt;setclickcost | scc&gt; &lt;clickCost&gt;:** Set the click cost for a command sign.