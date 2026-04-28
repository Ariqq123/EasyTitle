# Security Audit — EasyTitle
**MC Version:** 1.13+ | **Date:** 2026-04-28

## Findings
| ID   | Category   | Exploit                                     | Severity | Status        |
|------|------------|---------------------------------------------|----------|---------------|
| S-01 | Inventory  | Drag-based dupe in `TitleGui`               | HIGH     | Fixed (Pat-A) |
| S-02 | Inventory  | Missing `ignoreCancelled` on GUI click      | LOW      | Fixed         |

*Note: Formatting exploits (color/format injection) and hover event command injection vectors were verified to be securely mitigated via Adventure MiniMessage `safeMiniMessage` standard tags restrictions.*

## Severity Scale
| Level | Meaning |
|-------|---------|
| CRITICAL | Crash, RCE, data loss |
| HIGH | Dupe, permission escalation, economy exploit |
| MEDIUM | Game-balance bypass, grief vector |
| LOW | Cosmetic abuse |

## Mitigations Applied
- **Pattern A:** `TitleGui.java` modified to add `onDrag(InventoryDragEvent e)` handler that safely cancels any dragging over the UI pane to prevent possible inventory item duplications. Also added the `ignoreCancelled = true` parameter to `onClick` event to properly respect already-cancelled events.

## Out of Scope / Server Config Required
- paper-world-defaults.yml: `allow-permanent-block-break-exploits: false`
- spigot.yml: `bungeecord: false` unless explicitly needed
