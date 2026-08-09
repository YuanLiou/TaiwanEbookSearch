# Task 9 Report — 手動驗證與收尾

日期：2026-08-09  
狀態：**DONE_WITH_CONCERNS**（自動化收尾完成；裝置／Desktop 人工旅程未執行）

## Commands and results

| 命令 | 結果 |
| --- | --- |
| `JAVA_HOME=$(/usr/libexec/java_home -v 17) ./gradlew test lint assembleApiDebug` | PASS；JDK 17.0.7；`BUILD SUCCESSFUL`，168 tasks；成功產生 `apiDebug`。Lint 報告仍為 **1 error、95 warnings**，不是 Lint clean。既有 error 是 `SuspiciousModifierThen`（`composable/DraggableItem.kt:56`）。 |
| `JAVA_HOME=$(/usr/libexec/java_home -v 17) ./gradlew :app:testApiDebugUnitTest --tests 'liou.rayyuan.ebooksearchtaiwan.booksearch.BookSearchAdaptiveNavigationTest'` | PASS；2 tests，0 failures／errors／skipped。覆蓋 Compact + Custom Tab 與 Medium+ detail 導流純契約。 |
| `JAVA_HOME=$(/usr/libexec/java_home -v 17) ./gradlew spotlessCheck` | 初次 FAIL；只有 `DetailPaneEmptyState.kt` 格式違規。 |
| `JAVA_HOME=$(/usr/libexec/java_home -v 17) ./gradlew :app:spotlessKotlinApply` | PASS；只產生 `DetailPaneEmptyState.kt` 函式宣告格式 diff。 |
| `JAVA_HOME=$(/usr/libexec/java_home -v 17) ./gradlew spotlessCheck`（修正後） | PASS；全專案 Spotless check successful。 |

## Human device verification remaining（Spike §7.1）

- [ ] Journey A：Compact + Custom Tab 外開，回 App 保留清單狀態
- [ ] Journey B：Compact + 偏好關閉，單欄 WebView 與返回
- [ ] Journey C：Medium+ 分欄、右側 WebView、偏好不影響
- [ ] Journey D：Desktop Compact↔Medium↔Expanded 反覆縮放、detail 保留與返回
- [ ] Journey E：搜尋紀錄 overlay 不阻擋 detail
- [ ] Journey F：手機直向、快照選單與設定入口回歸
- [ ] Medium+ 未選書空狀態目視、predictive back／focus back、TalkBack contentDescription 抽樣

## Residual risks

- JVM 導流測試不驗證真實 Compose pane、Custom Tab、WebView、返回堆疊或 Desktop resize；Spike 明確要求人工量窗作為權威證據。
- Lint 因 `abortOnError = false` 可在 1 error、95 warnings 下成功；本次未修正既有 `SuspiciousModifierThen`。
- `docs/spec/README.md` 仍指向已核准基準 `fbe2a4c`。本次未猜測重設基準；建議負責人在 Journey A–F 驗收並核准 Task 8 Spec 變更後，再以當時整合 commit／日期重設現況基線。
