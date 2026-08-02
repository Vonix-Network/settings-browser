# Settings Browser

## 1.1.0+1.18.2

- Adds a compact, vanilla-styled search field only to the **Controls / Key Binds** screen.
- Searches all registered mod key binds by translated name and category.
- Shows current key assignments and highlights conflicts.
- Does not add anything to the general Options screen.

A standalone Minecraft Forge 1.18.2 mod for finding key binds in large modpacks. When JEI 10.2.1.1011 is installed, the mod also registers a JEI Settings category.

## Dependencies

Required on the client and server:

- Minecraft 1.18.2
- Minecraft Forge 40.2.0–40.x

Optional on the client:

- Just Enough Items (JEI) Forge 1.18.2, version **10.2.1.1011** or a compatible 10.2.x release

The mod loads without JEI. Open Minecraft **Options → Controls / Key Binds** to use the search field. The search panel is intentionally limited to that tab and uses vanilla-compatible colors and spacing.

## Build

Requires Java 17:

```bash
./gradlew clean build
```

## Extension API

Register additional settings in `SettingsIndex` using `SettingEntry`. Each entry carries an ID, display name, category, description, and Forge config value. The index is intentionally small and explicit for this initial release; future versions can add a safe Forge config discovery adapter.

## License

MIT. Minecraft, Forge, and JEI are not owned by this project.
