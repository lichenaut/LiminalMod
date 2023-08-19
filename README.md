# LiminalMod
LiminalMod allows for configurable structure spawning, restoring old Minecraft's atmosphere.

[![Github All Releases](https://img.shields.io/github/downloads/lichenaut/LiminalMod/total.svg)]()

## Premise

I decided to make this plugin as an answer to [this video I saw.](https://www.youtube.com/watch?v=R9RXZSBdom8)

This plugin allows for users to change the presence of structures, and thus the "liminality" of Minecraft, or how often a player will go without finding a structure. I also included two preset configs!

As opposed to using a datapack, which would be slightly faster, using a plugin means using a format a lot more people are familiar with. Let me know if you want a datapack generator that accomplishes this same purpose!

## Configuration

### Example

>VILLAGE_PLAINS: <br>
&nbsp;&nbsp;spawn-rate: 50 <br>
&nbsp;&nbsp;abandoned-rate: 75 <br>

Plains Villages will spawn 50% of the time, and 75% of those that spawn will not spawn their structure-specific mobs. Obviously, some structures will not have structure-specific mobs, so you wouldn't need to specify that for them. Feel free to ask me if you have any questions! https://discord.gg/txJDdYcRV6

## Secret Features

These are the features I would only entrust to those who read documentation, as they could crash the server if not used wisely.

### Structure Transformation

LiminalMod will allow you to set the rate loot in Chests, loot in Chest Minecarts, and Sponge Block counts in Monuments decrease when they are in an abandoned structure. LiminalMod can also make Witch Huts, Mansions, Pillager Outposts, and all five Village types appear abandoned, replicating the style of the default structure "Abandoned Village". Doing so requires the plugin to scan blocks and make changes, which can be resource intensive, so please don't overdo it!

### Example

>VILLAGE_PLAINS: <br>
&nbsp;&nbsp;abandoned-rate: 75 <br>
&nbsp;&nbsp;loot-abandon-rate: 90 <br>
&nbsp;&nbsp;transform-structure: true <br> <br>
blocks-per-tick: 1024 <br>

To enable this feature, add the "blocks-per-tick: ___" field somewhere in your config.yml, then add the "transform-structure: true" field to a structure you want the plugin to alter when it is abandoned. The "loot-abandon-rate" field in this case means that 90% of all chest contents will be removed when the structure is abandoned.

The "blocks-per-tick" field is the amount of blocks the plugin can scan and make changes to per tick, and does not increase when multiple structures have just loaded. Consider pre-loading your world to get this process out of the way. Also, consider pre-loading your worlds in general!
