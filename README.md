# Settings Browser

## 1.1.3+1.18.2

- Moves the key-bind finder to a readable **bottom-left panel** on the dedicated Key Binds screen.
- Adds search by key-bind name, translated key, category, and mod namespace text.
- Adds **All**, **Conflicts**, and **Unbound** filters.
- Adds result counts and a Clear button.
- Adds **Ctrl+F** to focus the finder.
- Clicking a search result selects and centers the matching vanilla key-bind row.
- Keeps the search scoped only to **Options → Controls → Key Binds**.

## 1.1.2+1.18.2

- Restricts the search field and conflict panel to the dedicated **Key Binds** screen only.
- The parent Controls screen is no longer modified.

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

The mod loads without JEI. Open Minecraft **Options → Controls → Key Binds** to use the bottom-left finder. Press **Ctrl+F** to focus it, type a mod/name/key/category, choose a filter, and click a result to jump to the vanilla row.

## Build

Requires Java 17:

```bash
./gradlew clean build
```

## Extension API

Register additional settings in `SettingsIndex` using `SettingEntry`. Each entry carries an ID, display name, category, description, and Forge config value. The index is intentionally small and explicit for this initial release; future versions can add a safe Forge config discovery adapter.

## License

MIT. Minecraft, Forge, and JEI are not owned by this project.
