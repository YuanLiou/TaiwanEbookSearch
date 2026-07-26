# TaiwanEbookSearch Agent Instructions

## 溝通與範圍

- 對使用者使用台灣繁體中文。
- 本 repository 是「台灣電子書搜尋」Android App。後端搜尋與快照服務不在本 repository 範圍內。
- 正式產品基準是 `apiRelease`；`apiDebug` 是開發版本，`mockDebug` 只用於開發與測試。

## 規格載入

開始處理產品、架構或行為相關任務時，先讀取 `docs/spec/README.md`，再依其中的路由只載入與任務相關的文件。不要一次載入所有 Spec。

規格狀態決定衝突處理方式：

- `草案`：目前可建置程式碼的實際行為仍是依據。發現差異時先提出，不得用草案覆蓋程式碼。
- `現況基線`：已核准 Spec 是產品行為的權威來源。刻意改變行為時必須同步更新 Spec；非刻意偏離時修正程式碼。

產品規則以 Requirement ID 引用，不要在 Agent 規則或 Skill 中複製規格全文。

## Code 與 Spec 對焦

凡是產品行為、外部契約、持久化語意或穩定技術責任可能改變時，使用 `.agents/skills/align-code-and-spec/SKILL.md` 執行對焦。

Skill 預設只能做唯讀 audit。只有使用者明確要求同步、更新 Spec 或修正 Code 時，才可修改檔案。意圖不明時一次只問一題，不自行判定 Code 或 Spec 哪一方應改。

## 實作原則

- 維持產品可觀察行為，不把現有 class、函式或 Library 當成不可替換契約。
- `app` 與 `commonMain` 的責任分界是穩定方向，但模組名稱不是永久限制。
- 新增或修改使用者可見文字時，同步維護英文、台灣繁體中文與中國簡體中文資源，不得硬編碼可見字串。
- 不把 README、Fastlane 商店文案或 Mock 資料當成正式產品真相；它們可能過期或僅供測試。
- 保留使用者既有變更，不回復無關 diff。

## 驗證

CI 使用 JDK 17。與目前 CI 接近的本機驗證為：

```shell
./gradlew test lint assembleApiDebug
```

目前測試多為範例測試，Lint 也有既有問題且不阻擋建置。不得把 Gradle 成功誤報為完整行為驗證或零 Lint 問題。依變更風險補做相關手動操作，並清楚回報實際驗證與未驗證範圍。
