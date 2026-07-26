---
name: align-code-and-spec
description: Audit and align TaiwanEbookSearch code changes with docs/spec requirements. Use when reviewing a working tree, commit, branch, or PR that may change product behavior, API contracts, persistence semantics, module responsibilities, Build Variants, or when explicitly asked to sync Code and Spec.
---

# 對焦 Code 與 Spec

## 目的

以最少必要 Context 檢查程式碼與 `docs/spec/` 是否一致。預設只做唯讀 audit；沒有使用者明確授權，不修改 Code、Spec 或 Git 狀態。

本 Skill 只保存程序。產品規則一律從 Spec 的 Requirement ID 讀取，不在此複製。

## 先判定規格權威

先讀 `docs/spec/README.md` 的狀態與任務路由。

若狀態是「草案」，目前可建置 Code 的實際行為仍是依據。發現衝突時列出證據並請使用者判定，不以草案直接覆蓋 Code。

若狀態是「現況基線」，已核准 Spec 是產品行為權威。刻意的產品變更需要同步更新 Spec；非刻意漂移通常應修正 Code。

## 決定比較範圍

先執行 `git status --short`。

工作目錄有變更時，以目前 diff 為範圍，使用 `git diff --name-only`、`git diff` 與必要的 staged diff 調查。

工作目錄乾淨時，不猜測比較基準。若使用者尚未指定 commit、branch 或 PR，只問一題取得範圍後停止。

不要把未追蹤的使用者檔案、既有 dirty diff 或其他 Agent 的變更當成可丟棄內容。

## 漸進載入 Context

先把變更檔案映射到 `docs/spec/README.md` 的任務表，只讀最接近的 Spec。只有變更跨越多個領域時，才載入額外文件。

追查 Code 時，先用 `rg` 找 Requirement 對應入口，再讀最少必要的 ViewModel、Use Case、Repository、mapper、持久化或平台整合。README、Fastlane 文案與 Mock 資產只能作輔助證據。

## 執行唯讀 Audit

逐一判斷變更是否影響：

1. 使用者可觀察行為與錯誤狀態。
2. 搜尋 API、快照、價格、來源狀態或 Deep Link 契約。
3. 搜尋紀錄、設定預設值、資料遷移或跨重啟語意。
4. `app` 與 `commonMain` 責任方向或 Build Variant。
5. 三語系、Android API 26 相容性、測試及驗證證據。

每個差異都引用 Requirement ID，並分類為：

| 分類 | 判定 |
| --- | --- |
| 一致 | Code 與 Spec 行為相符 |
| 刻意變更 | 使用者已表明要改產品行為，需要同步 Spec |
| 非刻意漂移 | Code 無意間違反已核准 Spec |
| 規格缺口 | 行為存在，但 Spec 無法回答 |
| 證據不足 | 無法從指定 diff、測試或實際操作確定 |

疑似 bug 不自動升格成 Requirement，也不因 Code 現況就默認為產品規則。

## 修改門檻

只有使用者明確要求「同步」、「更新 Spec」、「依 Spec 修正 Code」或同等意思時才修改。授權只涵蓋使用者指定方向，不自行擴大到另一側。

若應改 Code 或 Spec 仍不清楚，一次問一題取得產品意圖。確認後：

1. 刻意行為變更：修改 Code 與受影響 Requirement，並保留 Requirement ID。
2. 非刻意漂移：修正 Code，除非 Spec 文字本身不精確。
3. 規格缺口：先取得產品決策，再新增最小必要 Requirement。
4. 框架重構：只有可觀察行為或穩定責任改變時才更新 Spec。

## 驗證與回報

依風險執行相關測試、`lint`、建置及 Emulator 或實機操作。基準命令見 `AGENTS.md` 與 `docs/spec/verification.md`。

不要把 `BUILD SUCCESSFUL` 說成完整產品驗證。分開回報通過項目、既有問題、新增問題與未驗證項目。

唯讀 audit 的輸出先列需要處理的差異，包含 Requirement ID、Code 證據、風險與建議方向；若沒有差異，明確說明比較範圍及殘餘未驗證風險。
