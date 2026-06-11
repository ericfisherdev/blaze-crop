<p align="center">
  <img src="docs/logo.png" alt="Blaze Crop" width="517">
</p>

A multi-loader (Fabric + NeoForge) Minecraft mod that lets you farm Blaze Rods.
Built with [Architectury](https://docs.architectury.dev/) for Minecraft **1.21.1**.

> Heavily based on [Ender Crop](https://github.com/DrManganese/ender-crop) by DrManganese
> (GPL-3.0), reworked to grow Blaze Rods on tilled netherrack instead of Ender Pearls
> on tilled end stone.

## The Crop

The blaze crop has 8 growth stages. Harvesting it at any stage drops Blaze Seeds; at
the 8<sup>th</sup> stage it drops 1–2 Blaze Rods and sometimes an extra seed.

### Growth

Blaze crops **only grow on tilled netherrack** — not farmland.

* Follows vanilla Minecraft [growth rules](https://minecraft.fandom.com/wiki/Tutorials/Crop_farming#Growth_rate) at **half the rate of wheat**, i.e. roughly double the base growth time (configurable via `tilledNetherMultiplier`).
* Grows at any light level.
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

Tilled netherrack is kept moist by **nearby lava** (within 4 blocks) — water and rain
have no effect. Dry tilled netherrack with nothing planted on it reverts to netherrack.

## Mod Compatibility

* **Jade / WTHIT** — shows growth-blocked / light-level / tillability hints.
* **The One Probe** (NeoForge) — same HUD info.
* **Botany Pots** — blaze seeds grow in pots only on tilled netherrack (place netherrack in a pot and till it); the pot grow time is doubled, with Ender Crop's `2.0` soil growth modifier.

## Building

```sh
./gradlew build
```

Built jars land in `fabric/build/libs/` and `neoforge/build/libs/`.

## License

GPL-3.0-only — see [LICENSE](LICENSE). Inherited from the original Ender Crop project.
