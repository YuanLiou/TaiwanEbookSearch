### Task 4: Detail 空狀態與關閉鈕 chrome

**Files:**
- Create（建議）: `app/src/main/kotlin/liou/rayyuan/ebooksearchtaiwan/booksearch/composable/DetailPaneEmptyState.kt`
- Modify: `app/src/main/kotlin/liou/rayyuan/ebooksearchtaiwan/booksearch/BookSearchScreen.kt`

**Interfaces:**
- Produces: `@Composable fun DetailPaneEmptyState(modifier: Modifier = Modifier)`

- [ ] **Step 1: 抽出空狀態**

將 `BookSearchScreen` detail 的「無 book」分支（目前 `Scaffold` + 半透明大 icon）移到 `DetailPaneEmptyState`，保持：

- `WindowInsets.safeDrawing`
- 圓角／`pale_slate`（或 theme 對應色）
- icon alpha 適中，分欄右側看起來像待機面板而非壞掉頁面

- [ ] **Step 2: 接回 detailPane**

```kotlin
if (book != null) {
    SimpleWebViewScreen(...)
} else {
    DetailPaneEmptyState(modifier = Modifier.fillMaxSize())
}
```

- [ ] **Step 3: 目視檢查清單（實機／模擬器）**

- Medium+ 未選書：右側空狀態正常
- Compact 不應無故顯示怪異空頁（無 destination 時以 list 為主）

- [ ] **Step 4: 建議 commit（需使用者同意）**

```text
ui(booksearch): polish list-detail empty detail pane

Extract a dedicated empty state for the adaptive detail pane.
```

---

