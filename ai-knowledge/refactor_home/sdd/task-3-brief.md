### Task 3: 移除 `isTabletSize` / `LocalDeviceInfo`

**Files:**
- Modify: `app/src/main/kotlin/liou/rayyuan/ebooksearchtaiwan/ui/theme/EBookTheme.kt`
- Grep 全專案確認無剩餘引用

**Interfaces:**
- Produces: `EBookTheme` 不再提供 `LocalDeviceInfo`

- [ ] **Step 1: 搜尋殘留**

Run: `rg "isTabletSize|LocalDeviceInfo|DeviceInfo" --glob '*.kt'`

Expected: 僅剩 `EBookTheme.kt` 定義（或已無）

- [ ] **Step 2: 刪除**

自 `EBookTheme.kt` 移除：

- `LocalDeviceInfo`
- `DeviceInfo`
- `isTabletSize()`
- `CompositionLocalProvider` 中對 `LocalDeviceInfo` 的提供

保留 color／drawable CompositionLocals 與 `MaterialTheme`。

- [ ] **Step 3: 編譯**

Run: `./gradlew :app:compileApiDebugKotlin`

Expected: SUCCESS

- [ ] **Step 4: 建議 commit（需使用者同意）**

```text
refactor(ui): remove unused isTabletSize device info

WindowSizeClass now owns adaptive width decisions for book search.
```

---

