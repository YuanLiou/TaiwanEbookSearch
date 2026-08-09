# Task 8 Report — Spec patch 套用

## Status

Complete. Spec patch applied per Spike §6; align audit confirms Code↔Spec consistency for adaptive breakpoints.

## Spec changes

| File | Change |
| --- | --- |
| `docs/spec/known-limitations.md` | `LIMIT-003` rewritten: `WindowSizeClass` Compact single / Medium+ dual; NAV-001–004 constraint retained; 600–840dp deliberate change noted |
| `docs/spec/navigation-sharing-deep-links.md` | `NAV-001`–`NAV-003` Compact/Medium+ wording; footer points to `LIMIT-003` |

## Code↔Spec alignment (read-only audit)

**Scope:** Adaptive breakpoint / navigation routing (Tasks 1–3 implementation vs updated Spec).

| Requirement | Classification | Evidence |
| --- | --- | --- |
| `LIMIT-003` | 一致 | `WindowWidthAdaptive.kt`: `currentWindowAdaptiveInfo()` + `WindowWidthSizeClass.COMPACT`; scaffold uses official `NavigableListDetailPaneScaffold` |
| `NAV-001` | 一致 | `shouldOpenCustomTab(prefer=true, isWidthCompact=true)` → Custom Tab; default preference is Custom Tab |
| `NAV-002` | 一致 | `shouldOpenCustomTab(prefer=false, isWidthCompact=true)` → false → in-app WebView via `navigateTo(Detail)` |
| `NAV-003` | 一致 | `shouldOpenCustomTab(..., isWidthCompact=false)` → false regardless of preference; Medium+ uses in-app detail pane |
| `NAV-004` | 一致 | No Spec change; existing `SimpleWebViewScreen` behavior unchanged |
| 600–840dp change | 刻意變更 | Documented in `LIMIT-003`; old `isTabletSize` thresholds removed from Code (Task 3) |

**Residual risks (not blocking Spec patch):**

- Manual verification of 600–840dp band deferred to Task 9.
- JVM tests cover routing contract only, not pane visibility at exact dp widths.
- Spec baseline commit/date in `docs/spec/README.md` not bumped (requirement text update only).

## Commits

(Pending — see parent agent commit message.)

`3f7eaf4` — docs(spec): align adaptive breakpoints with WindowSizeClass

## Concerns

None blocking. Task 9 should cover manual Desktop / landscape 600–840dp regression per Spike §7.1.
