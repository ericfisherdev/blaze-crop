# Blaze Crop

A multi-loader (Fabric + NeoForge) Minecraft mod that lets you farm Blaze Rods.
Built with [Architectury](https://docs.architectury.dev/) for Minecraft **1.21.1**.

> Heavily based on [Ender Crop](https://github.com/DrManganese/ender-crop) by DrManganese
> (GPL-3.0), reworked to grow Blaze Rods on tilled netherrack instead of Ender Pearls
> on tilled end stone.

## The Crop

The blaze crop has 8 growth stages. Harvesting it at any stage drops Blaze Seeds; at
the 8<sup>th</sup> stage it drops 1–2 Blaze Rods and sometimes an extra seed.

### Growth

**On farmland:**

* Follows vanilla Minecraft [growth rules](https://minecraft.fandom.com/wiki/Tutorials/Crop_farming#Growth_rate), but at 50% the rate of wheat (configurable).
* Requires light level < 7 to grow.

**On tilled netherrack:**

* Follows vanilla Minecraft [growth rules](https://minecraft.fandom.com/wiki/Tutorials/Crop_farming#Growth_rate) at the same rate as wheat (configurable).
* Requires any light level.
* 1 in 50 chance to spawn a Blaze on harvest (configurable).

## The Seeds

Blaze seeds can be crafted by surrounding any seed (`#c:seeds`) with 4 Blaze Rods:

```
 R
RSR
 R
```

`R` = Blaze Rod, `S` = any seed.

## Tilled Netherrack (configurable)

To obtain tilled netherrack, right-click regular netherrack with a hoe that has been
enchanted with Unbreaking I (configurable) or a hoe-like tool (AIOT, Kama, Scythe).

## Mod Compatibility

* **Jade / WTHIT** — shows growth-blocked / light-level / tillability hints.
* **The One Probe** (NeoForge) — same HUD info.
* **Botany Pots** — blaze seeds can be grown in pots on dirt or nether soil.

## Building

```sh
./gradlew build
```

Built jars land in `fabric/build/libs/` and `neoforge/build/libs/`.

## License

GPL-3.0-only — see [LICENSE](LICENSE). Inherited from the original Ender Crop project.
