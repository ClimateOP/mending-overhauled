# Mending Overhauled

A Fabric mod for Minecraft 26.2 that changes how **Mending** works.

Instead of waiting for XP orbs to automatically repair Mending equipment, you can manually repair a damaged Mending item directly from your inventory using the **middle mouse button**.

## Features

* **Hold Middle Mouse Button** over a damaged Mending item to repair it.
* Repair happens **continuously over time** instead of instantly.
* Move the cursor between different Mending items while holding the button to switch repairs.
* Move the cursor away from an item to stop repairing.
* Release the middle mouse button to stop repairing.
* Uses the player's existing XP.
* **1 XP repairs 2 durability**, following vanilla Mending's repair ratio.
* Repair speed is approximately **280 durability per second**.
* Includes a subtle **XP pickup sound** while repairing.
* Repair and XP changes are handled **server-side**.

## How It Works

When you open an inventory and hold the middle mouse button over a damaged item with Mending:

```text
Hold Middle Mouse
        ↓
Detect hovered item
        ↓
Check for Mending
        ↓
Check available XP
        ↓
Consume XP
        ↓
Repair item
```

The repair process runs continuously while the button is held.

For example:

```text
1 XP  →  2 durability
7 XP  → 14 durability per tick
20 ticks/sec
      ↓
≈ 280 durability/sec
```

This makes heavily damaged tools repair in a few seconds while still giving the player control over when XP is spent.

## Requirements

* Minecraft **26.2**
* Fabric Loader
* Fabric API

The mod must be installed on both:

* The **server**
* The **client**

Use the same mod version on both sides.

## Installation

### Client

1. Install Fabric Loader for Minecraft 26.2.
2. Install Fabric API.
3. Place the mod JAR in your `mods` folder:

```text
.minecraft/
└── mods/
    ├── fabric-api-<version>.jar
    └── mending-overhauled-<version>.jar
```

### Server

Install Fabric Loader on the server and place the required JARs in the server's `mods` folder:

```text
server/
└── mods/
    ├── fabric-api-<version>.jar
    └── mending-overhauled-<version>.jar
```

Start the server using the Fabric server launcher.

## Usage

1. Obtain an item enchanted with **Mending**.
2. Damage the item.
3. Open your inventory or another container.
4. Hover over the damaged Mending item.
5. Hold the **middle mouse button**.
6. The item will continuously repair while XP is available.
7. Move the cursor to another damaged Mending item to repair that item instead.
8. Move away from the item or release the button to stop.

## Compatibility

Mending Overhauled is designed for:

```text
Minecraft: 26.2
Mod Loader: Fabric
```

The mod is intended to work with Mending-compatible tools and armor.

## Building From Source

Clone the repository and open the project in IntelliJ IDEA.

Build the mod with:

```powershell
.\gradlew.bat build
```

The generated JAR will be located in:

```text
build/libs/
```

## Development

The project uses:

* Java
* Fabric Loader
* Fabric API
* Mixins
* Custom Fabric networking

The client handles input and identifies the hovered inventory slot, while the server validates and performs the actual XP consumption and item repair.

## Project Structure

```text
src/
├── client/
│   └── java/
│       └── com/
│           └── climateop/
│               └── mendingoverhauled/
│                   └── client/
│                       └── mixin/
│                           └── ExampleClientMixin.java
│
└── main/
    └── java/
        └── com/
            └── climateop/
                └── mendingoverhauled/
                    ├── MendingOverhauled.java
                    └── MendingRepairPayload.java
```

## License

See the [`LICENSE`](LICENSE) file for license information.

## Author

**ClimateOP**

GitHub: [ClimateOP](https://github.com/ClimateOP)
