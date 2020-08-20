# Obj2Mc
Obj2Mc is a command line program written in Java to convert an OBJ file into a set of Minecraft region files. It can be run from the command line with no arguments, or you can specify one or more models in the arguments. If no arguments are specified, the user is prompted for an input file. 

## Material Definition Format
This program supports what I call a Material Definition format. When running the program, this is optional, you do not have to specify one. However, by not specifying one, you are at the mercy of the default material definition. Adding material definitions allows you to override the default one per material.
The Material Definition format is a text file, containing a set of commands. The `material` command defines a new material, and all subsequent commands are a part of this material definition, until another `material` command has been reached. There are two commands for adding blocks to a Material Definition file, `opaque` and `translucent`.
 - `opaque <block name> <r> <g> <b>` add a block to the current material and specify its RGB values.
 - `translucent <block name> <r> <g> <b> <a>` add a block to the current material and specify its RGBA values.
 
**Example**:
```
material bricks
opaque red_terracotta 144 62 47
opaque white_terracotta 210 180 162

material grass
opaque lime_wool 108 182 25
opaque green_wool 82 105 29
```
 
## Using Generated Files
Once the program has finished, it will have created a folder called "region" where there will now be a set of .mca files. Put these files into a world's "region" folder to see them in-game. 
