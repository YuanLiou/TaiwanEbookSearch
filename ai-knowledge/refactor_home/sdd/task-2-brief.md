### Task 2: `NavigableListDetailPaneScaffold` 遷移與返回／focus

**Files:**
- Modify: `app/src/main/kotlin/liou/rayyuan/ebooksearchtaiwan/booksearch/BookSearchScreen.kt`

**Interfaces:**
- Consumes: Task 1 的 `isWindowWidthCompact()` / `isWidthCompact` 回呼
- Produces: 使用 `NavigableListDetailPaneScaffold(navigator = paneNavigator, ...)`；搜尋框 focus 時仍優先消費 back

- [ ] **Step 1: 替換 scaffold**

將 `ListDetailPaneScaffold(directive = ..., value = ..., listPane = ..., detailPane = ...)` 改為：

```kotlin
NavigableListDetailPaneScaffold(
    navigator = paneNavigator,
    modifier = modifier,
    listPane = { /* 既有 AnimatedPane + BookResultListScreen */ },
    detailPane = { /* 既有 AnimatedPane + WebView / empty */ }
)
```

import 來自 `androidx.compose.material3.adaptive.navigation`（以專案依賴為準）。

- [ ] **Step 2: 收斂 BackHandler**

`NavigableListDetailPaneScaffold` 已處理 pane predictive back。保留**僅**處理搜尋框 focus 的邏輯，例如：

```kotlin
BackHandler(enabled = isTextInputFocused) {
    bookSearchViewModel.forceFocusOrUnfocusKeywordTextInput(false)
}
```

若實機發現 pane back 失效，再依官方加上 `ThreePaneScaffoldPredictiveBackHandler` 或恢復 `paneNavigator.canNavigateBack()` 分支，並寫入 `findings.md`。

- [ ] **Step 3: detail 關閉鈕條件**

維持：

```kotlin
val isDetailPaneVisible =
    paneNavigator.scaffoldValue.secondary == PaneAdaptedValue.Expanded
// showCloseButton = !isDetailPaneVisible
```

關閉時：

```kotlin
scope.launch {
    if (paneNavigator.canNavigateBack()) {
        paneNavigator.navigateBack()
    }
}
```

- [ ] **Step 4: 編譯**

Run: `./gradlew :app:compileApiDebugKotlin`

Expected: SUCCESS

- [ ] **Step 5: 建議 commit（需使用者同意）**

```text
refactor(booksearch): adopt NavigableListDetailPaneScaffold

Align list-detail navigation with Material 3 adaptive canonical APIs
while keeping search-field focus back behavior.
```

---

