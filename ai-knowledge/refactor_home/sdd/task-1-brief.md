### Task 1: Compact 判定 helper + Activity 導流改接

**Files:**
- Create: `app/src/main/kotlin/liou/rayyuan/ebooksearchtaiwan/booksearch/util/WindowWidthAdaptive.kt`
- Modify: `app/src/main/kotlin/liou/rayyuan/ebooksearchtaiwan/booksearch/BookSearchScreen.kt`
- Modify: `app/src/main/kotlin/liou/rayyuan/ebooksearchtaiwan/booksearch/BookSearchActivity.kt`

**Interfaces:**
- Produces: `@Composable fun isWindowWidthCompact(): Boolean`（或非 Composable 的 `WindowSizeClass.isWidthCompact()` extension）
- Produces: `onBookSearchItemClick` 參數由 `isTabletSize: Boolean` 改為 `isWidthCompact: Boolean`（語意：`true` = Compact 單欄寬度）

- [ ] **Step 1: 確認專案 Adaptive API 符號**

在實作機查目前 BOM 下可用 API（擇一對齊官方文件）：

```kotlin
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.window.core.layout.WindowSizeClass
// 常見寫法（實作時以編譯器／文件為準）:
// val widthCompact = !windowSizeClass.isWidthAtLeastBreakpoint(
//     WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND
// )
```

若符號名稱不同，以能編譯的官方等效 API 為準，並記入 `findings.md`。

- [ ] **Step 2: 新增 helper**

```kotlin
package liou.rayyuan.ebooksearchtaiwan.booksearch.util

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowSizeClass

@Composable
fun isWindowWidthCompact(): Boolean {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    return !windowSizeClass.isWidthAtLeastBreakpoint(
        WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND
    )
}
```

（若 `WindowSizeClass` 套件／常數名不同，改成專案可編譯版本，保持「未達 Medium = Compact」語意。）

- [ ] **Step 3: 更新 `BookSearchScreen` 回呼簽名**

將：

```kotlin
onBookSearchItemClick: (book: Book, paneNavigator: ThreePaneScaffoldNavigator<Book>, isTabletSize: Boolean) -> Unit
```

改為：

```kotlin
onBookSearchItemClick: (book: Book, paneNavigator: ThreePaneScaffoldNavigator<Book>, isWidthCompact: Boolean) -> Unit
```

內部：

```kotlin
val isWidthCompact = isWindowWidthCompact()
// ...
onBookSearchItemClick = { onBookSearchItemClick(it, paneNavigator, isWidthCompact) }
```

暫時可仍使用既有 `ListDetailPaneScaffold`（Task 2 再遷移）。刪除對 `LocalDeviceInfo` 的讀取。

- [ ] **Step 4: 更新 `BookSearchActivity` 分支**

```kotlin
onBookSearchItemClick = { book, paneNavigator, isWidthCompact ->
    if (userPreferenceManager.isPreferCustomTab() && isWidthCompact) {
        openInCustomTab(book.asUiModel().getLink())
    } else {
        scope.launch {
            paneNavigator.navigateTo(ListDetailPaneScaffoldRole.Detail, book)
        }
    }
    // ...既有 rank 計數邏輯不變
}
```

- [ ] **Step 5: 編譯確認**

Run: `./gradlew :app:compileApiDebugKotlin`

Expected: SUCCESS

- [ ] **Step 6: 更新 progress；建議 commit（需使用者同意）**

```text
refactor(booksearch): gate Custom Tab on WindowSizeClass Compact

Replace isTabletSize orientation thresholds so pane layout and product
navigation share one adaptive width source.
```

---

