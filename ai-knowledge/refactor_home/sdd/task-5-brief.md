### Task 5: 搜尋紀錄 overlay 與清單大螢幕小修

**Files:**
- Modify: `app/src/main/kotlin/liou/rayyuan/ebooksearchtaiwan/booksearch/BookResultListScreen.kt`
- 視需要: `app/.../booksearch/composable/SearchRecords.kt`、結果 list composable

**Interfaces:**
- Produces: overlay 僅覆蓋 list pane 內容區（隨 `BookResultListScreen` 邊界，不逃出 pane）

- [ ] **Step 1: 確認 overlay 範圍**

`BookResultListScreen` 的 scrim／`SearchRecords` 已在 list pane 的 `AnimatedPane` 內則通常已隔離。若仍用全螢幕感覺擋住 detail：

- 確保遮罩 `Modifier` 綁在 list `Box`/`Scaffold` content，而非外層 Activity 全螢幕 overlay。

- [ ] **Step 2: 清單可讀性小修（僅明顯問題）**

在 Medium+ 檢查：

- 水平 padding（`R.dimen.search_list_padding_horizontal`）是否過窄／過怪
- 廣告橫幅與結果區塊間距

只做小幅 spacing 調整；**不加** grid、**不加** maxWidth。

- [ ] **Step 3: 手測 Journey E（分欄 + 搜尋紀錄）**

Expected: 開啟紀錄時仍可看到／操作 detail（或至少不被半透明全螢幕鎖死）

- [ ] **Step 4: 建議 commit（需使用者同意）**

```text
fix(booksearch): keep search-record overlay inside list pane

Prevent the dimmed search-history layer from blocking the detail pane
on medium and wider windows.
```

---

