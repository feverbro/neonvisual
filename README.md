# NeonVisuals

Client-side visual mod for **Minecraft 1.21.11 (Fabric)**.

## Build requirements
- Java 21
- Gradle 9.2.1
- Fabric Loader 0.18.1
- Fabric API 0.141.6+1.21.11

Run `gradle build`. The remapped mod JAR is produced in `build/libs/`.

## Controls
- **Right Shift** — open settings.

## Current baseline
- Neon HUD with coordinates and FPS.
- Persistent client settings.
- Settings screen for HUD, fog profile, particles, and enchantment-glint preservation.
- HUD uses Fabric's current HUD element API.
- No item-rendering or enchantment-glint hooks are used in this baseline, so vanilla glint rendering is left untouched.

Fog rendering and particle effects are staged for the next implementation pass; their settings are currently saved but do not yet alter world rendering.
