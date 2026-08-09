### Task 6: 主流程 contentDescription 字串資源化

**Files:**
- Modify: `app/src/main/res/values/strings.xml`
- Modify: `app/src/main/res/values-zh-rTW/strings.xml`
- Modify: `app/src/main/res/values-zh-rCN/strings.xml`
- Modify: `BookResultListScreen.kt`、`SimpleWebViewScreen.kt`
- 視需要: `BookSearchResultScreen.kt`、`SearchRecords.kt`、`BookSearchItem.kt`（主流程相關者）

**Interfaces:**
- Produces: 字串資源 key（名稱可微調，但三語系必須齊）：

| key（建議） | en | zh-TW | zh-CN |
| --- | --- | --- | --- |
| `content_desc_option_menu` | Option menu | 選項選單 | 选项菜单 |
| `content_desc_close_detail` | Close | 關閉 | 关闭 |
| `content_desc_book_cover` | Book cover | 書封 | 书封 |
| `content_desc_clear_search_record` | Clear search record | 清除搜尋紀錄 | 清除搜索记录 |
| `content_desc_scroll_to_top` | Scroll to top | 回到頂端 | 回到顶部 |

（`bottom button` 若實為 scroll-to-top，用語意正確的描述。）

- [ ] **Step 1: 加入三語系字串**
- [ ] **Step 2: 替換主流程硬編碼 `contentDescription = "..."`**
- [ ] **Step 3: 編譯**

Run: `./gradlew :app:compileApiDebugKotlin`

- [ ] **Step 4: 建議 commit（需使用者同意）**

```text
a11y(booksearch): localize content descriptions on main flow

Replace hardcoded TalkBack labels with en, zh-TW, and zh-CN resources.
```

---

