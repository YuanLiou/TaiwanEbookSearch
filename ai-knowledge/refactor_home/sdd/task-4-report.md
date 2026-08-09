# Task 4 Report — Detail 空狀態與關閉鈕 chrome

## Status

**Complete**

## Summary

Extracted the detail pane empty state from `BookSearchScreen` into `DetailPaneEmptyState` composable. Wired back via `DetailPaneEmptyState(modifier = Modifier.fillMaxSize())` when `book == null`. WebView path and close-button logic unchanged.

## Step 1: Extract empty state

Created `app/src/main/kotlin/liou/rayyuan/ebooksearchtaiwan/booksearch/composable/DetailPaneEmptyState.kt` preserving:

- `WindowInsets.safeDrawing`
- `RoundedCornerShape(8.dp)` + `pale_slate` background
- `big_icon` at 200.dp with 0.3 alpha

## Step 2: Wire into detailPane

`BookSearchScreen.kt` detail branch now calls `DetailPaneEmptyState` instead of inline Scaffold/Box/Image.

## Step 3: Visual checklist

Not run on device/emulator in this session. Manual verification recommended:

- Medium+ with no book selected: right pane shows idle empty state
- Compact: no spurious empty page when list is primary

## Step 4: Compile

```shell
./gradlew :app:compileApiDebugKotlin
```

**Result:** BUILD SUCCESSFUL (see verification log)

## Commit

`37f9fa8` — ui(booksearch): polish list-detail empty detail pane

## Concerns

None blocking. Device visual pass deferred to Task 9.

## Files changed

- `app/src/main/kotlin/liou/rayyuan/ebooksearchtaiwan/booksearch/composable/DetailPaneEmptyState.kt` (new)
- `app/src/main/kotlin/liou/rayyuan/ebooksearchtaiwan/booksearch/BookSearchScreen.kt`
- `ai-knowledge/refactor_home/progress.md`
