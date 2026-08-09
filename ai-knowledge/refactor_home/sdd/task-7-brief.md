### Task 7: Compose UI 測試（導流分支）

**Files:**
- Create: `app/src/androidTest/kotlin/liou/rayyuan/ebooksearchtaiwan/booksearch/BookSearchAdaptiveNavigationTest.kt`
- 視需要抽出可測 callback／小 wrapper 以便不啟動全 Activity 廣告／網路

**Interfaces:**
- Produces: 至少兩個測試——Compact 偏好 Custom Tab 不 navigate detail；非 Compact 會 navigate detail

> 若全 Activity 測試過重，改測純 Compose 宿主：傳入 fake `onBookSearchItemClick` 記錄參數，用 `Modifier.width(...)` 或測試用 Adaptive 設定模擬寬度。實作時選**能穩定跑在 CI** 的最小方案，細節記入 `findings.md`。

- [ ] **Step 1: 寫失敗測試（契約）**

偽代碼目標：

```kotlin
@Test
fun compact_withCustomTabPreference_doesNotNavigateToDetail() {
    // arrange: width compact, preferCustomTab = true
    // act: click book
    // assert: openCustomTab called; detail navigation not called
}

@Test
fun mediumWidth_ignoresCustomTabPreference_navigatesToDetail() {
    // arrange: width medium+, preferCustomTab = true
    // act: click book
    // assert: navigateTo Detail with book; custom tab not opened
}
```

- [ ] **Step 2: 跑測試確認失敗／或先紅燈**

Run: `./gradlew :app:connectedApiDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=liou.rayyuan.ebooksearchtaiwan.booksearch.BookSearchAdaptiveNavigationTest`

（若環境無裝置，改用可在 JVM 跑的最小契約測試，或記錄「待 CI／本機裝置」於 `progress.md`。）

- [ ] **Step 3: 實作到綠燈（必要時加測試替身）**
- [ ] **Step 4: 建議 commit（需使用者同意）**

```text
test(booksearch): cover Custom Tab gating by window width

Add instrumentation coverage for compact vs medium+ navigation branches.
```

---

