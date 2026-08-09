# Task 2 Report — NavigableListDetailPaneScaffold 遷移與返回／focus

## 狀態

DONE

## 實作內容

- 將 `BookSearchScreen` 的 `ListDetailPaneScaffold` 遷移為 `NavigableListDetailPaneScaffold`。
- 由 `androidx.compose.material3.adaptive.navigation` 匯入新 scaffold，並以 `navigator = paneNavigator` 交由 canonical API 管理 pane navigation／predictive back。
- 將 App 自訂 `BackHandler` 收斂為只在搜尋欄位有 focus 時啟用，並取消 focus。
- 保留 detail pane 顯示判定、close button 條件及關閉時 `canNavigateBack()`／`navigateBack()` 邏輯。
- 未更動 `LocalDeviceInfo`、`isTabletSize`、detail empty state、overlay、字串、測試或 Spec。

## Code / Spec 對焦

- 本次為框架 scaffold 遷移，維持 `NAV-003` 的大畫面分欄 WebView 行為。
- 保留 detail pane 關閉操作，未改變 `NAV-004` 的返回清單能力。
- 無刻意產品行為變更，因此依要求未修改 Spec。

## 文件更新

- `ai-knowledge/refactor_home/progress.md`：Task 2 標記完成、目前 Task 前進至 Task 3，補上編譯紀錄。
- `ai-knowledge/refactor_home/findings.md`：記錄 Adaptive 1.1.0 的 navigable scaffold package 與 navigator API 差異。

## 驗證

- `git diff --check`：通過。
- `./gradlew :app:compileApiDebugKotlin`：`BUILD SUCCESSFUL`。
- 編譯仍顯示既有 JDK 25 fallback、Gradle／AGP deprecated option 與 Kotlin annotation 等警告；本次沒有新增編譯錯誤。

## 未驗證／疑慮

- 未執行 emulator／實機的 system back 與 predictive-back 手勢驗證；依 Task brief 使用 `NavigableListDetailPaneScaffold` 內建 pane back，若後續實機發現失效，再評估 `ThreePaneScaffoldPredictiveBackHandler`。
- 依要求未新增或修改測試。

## Review fix — BackHandler composition order

- **問題**：focus-only `BackHandler` 原先 compose 在 `NavigableListDetailPaneScaffold` 之前；Compose 反向呼叫 back callback，scaffold 內建 handler 可能先於搜尋 unfocus。
- **修正**：將 `BackHandler(enabled = isTextInputFocused)` 移到 scaffold 之後 compose，確保 compact + detail 開啟 + 搜尋 focus 時 back 先取消 focus。
- **未改動**：`showCloseButton`、`isDetailPaneVisible`、close 時 `canNavigateBack()`／`navigateBack()`。
- **Commit**：`fix(booksearch): register search focus BackHandler after scaffold`
- **驗證**：`./gradlew :app:compileApiDebugKotlin` → `BUILD SUCCESSFUL`（2026-08-09）。
