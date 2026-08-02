# Settings Browser

## 1.1.0+1.18.2

- Removed the separate Settings Browser screen from the normal user flow.
- Added a search box directly to Minecraft's vanilla Options menu.
- Search filters visible option and key-bind labels to help diagnose control conflicts.


A standalone Minecraft Forge 1.18.2 mod that adds a search field directly to Minecraft's vanilla **Options** menu. Use it to find key binds and option buttons by their visible labels. When JEI 10.2.1.1011 is installed, the mod also registers a JEI Settings category.

## Dependencies

Required on the client and server:

- Minecraft 1.18.2
- Minecraft Forge 40.2.0–40.x

Optional on the client:

- Just Enough Items (JEI) Forge 1.18.2, version **10.2.1.1011** or a compatible 10.2.x release

The mod loads without JEI. The search field is added to the vanilla Options menu, so there is no separate settings screen. Use the normal Minecraft **Options** button, then type in the search field to filter visible options and key binds.

## Build

Requires Java 17:

```bash
./gradlew clean build
```

## Extension API

Register additional settings in `SettingsIndex` using `SettingEntry`. Each entry carries an ID, display name, category, description, and Forge config value. The index is intentionally small and explicit for this initial release; future versions can add a safe Forge config discovery adapter.

## License

MIT. Minecraft, Forge, and JEI are not owned by this project.
