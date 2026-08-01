# Settings Browser

A standalone Minecraft Forge 1.18.2 mod that indexes configuration settings and provides a searchable interface. Press **O** to open the built-in browser. When JEI 10.2.1.1011 is installed, the mod also registers a JEI Settings category so settings can be discovered through JEI.

> The JEI integration targets **10.2.1.1011**, the latest verified 1.18.2-compatible coordinate available from the configured Maven repository. The original 9.7.0 coordinate is not published there.

## Dependencies

Required on the client and server:

- Minecraft 1.18.2
- Minecraft Forge 40.2.0–40.x

Optional on the client:

- Just Enough Items (JEI) Forge 1.18.2, version **10.2.1.1011** or a compatible 10.2.x release

The mod loads without JEI. The built-in searchable screen remains available, while JEI adds the Settings category integration.

## Build

Requires Java 17:

```bash
./gradlew clean build
```

## Extension API

Register additional settings in `SettingsIndex` using `SettingEntry`. Each entry carries an ID, display name, category, description, and Forge config value. The index is intentionally small and explicit for this initial release; future versions can add a safe Forge config discovery adapter.

## License

MIT. Minecraft, Forge, and JEI are not owned by this project.
