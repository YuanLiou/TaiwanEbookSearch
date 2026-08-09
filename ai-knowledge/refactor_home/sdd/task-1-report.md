# Task 1 Report — Compact 判定 helper + Activity 導流改接

## Status

DONE

## Commit

- `a5a626c` — `refactor(booksearch): gate Custom Tab on WindowSizeClass Compact`
- Commit message includes the required `Assisted-by: Cursor:Grok-4.5` trailer.
- No push was performed.

## Implementation

- Added `isWindowWidthCompact()` based on `currentWindowAdaptiveInfo().windowSizeClass`.
- Changed `BookSearchScreen`'s item-click callback parameter from `isTabletSize` to `isWidthCompact`.
- Removed the `LocalDeviceInfo` read from `BookSearchScreen`; the CompositionLocal definition remains for Task 3.
- Changed `BookSearchActivity` routing so Custom Tab is used only when the preference is enabled and width is Compact.
- Left `ListDetailPaneScaffold` unchanged for Task 2.
- Preserved the existing rank-counting logic.
- Marked Task 1 complete and added the compile result to `progress.md`.

## Adaptive API finding

Compose BOM `2025.10.00` resolves Material 3 Adaptive `1.1.0` and
`androidx.window:window-core-android:1.3.0`. That Window Core version does not
provide the brief's newer `isWidthAtLeastBreakpoint()` or
`WIDTH_DP_MEDIUM_LOWER_BOUND` APIs.

The helper therefore uses the official API available in the resolved version:

```kotlin
windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT
```

This preserves the required semantics: Compact is below the Medium width
breakpoint. The version/API discrepancy is recorded in
`ai-knowledge/refactor_home/findings.md`.

## Verification

Command:

```text
./gradlew -Dorg.gradle.java.home=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home :app:compileApiDebugKotlin
```

Result: `BUILD SUCCESSFUL` on JDK 17. A fresh post-commit run also succeeded.
The build still reports existing Gradle deprecation warnings; no new compile
error or warning was attributable to this task.

## Self-review

- Confirmed the committed callback semantics are consistently named
  `isWidthCompact`.
- Confirmed `BookSearchScreen` no longer reads `LocalDeviceInfo` or
  `isTabletSize`.
- Confirmed Custom Tab is gated by both the user preference and Compact width.
- Confirmed the fallback still navigates to the existing detail pane.
- Confirmed no Task 2 navigable-scaffold migration or Task 3 CompositionLocal
  deletion was included.
- `git show --check` found no whitespace errors, and the working tree was clean
  immediately after the commit.

## Residual scope

- No Compose UI or manual Desktop Mode behavior test was run; those are
  explicitly deferred to later plan tasks.
- The approved current Spec describes the user-facing single-column versus
  large-screen behavior, while the planned breakpoint wording update remains
  deferred to Task 8 as instructed.
