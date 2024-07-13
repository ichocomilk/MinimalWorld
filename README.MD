# SchematicAPI
Schematic system for MineLC.
More faster than WorldEdit and with the possibility of async

## How get and load a schematic

```
// Get schematic
final File file = SchematicStorage.get("schematicName.copy");

// Load schematic
final Schematic schematic = new SchematicFile().deserialize(file);
```

## How to serialize a schematic
```
final File destinationFile = ...
final Schematic schematic = ...

// Save a schematic file in a destination
new SchematicFile().serialize(destinationFile, schematic);
```
## How to compile:
> [!NOTE]
> We need java 8 to compile
1) Clone the repository
2) Download spigot 1.8.8 ```https://getbukkit.org/get/hNiHm0tuqAg1Xg7w7zudk63uHr0xo48D```
3) Create a folder named "lib" and Put the spigot
4) Execute in your terminal "mvn install"