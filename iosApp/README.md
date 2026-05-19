# iosApp — Camino Sanctus iOS

## Prerequisites
- macOS with Xcode 16+
- Apple Developer account with FamilyControls entitlement approved

## Setup
1. From project root: `./gradlew :shared:assembleXCFramework`
2. Open `iosApp/CaminoSanctus.xcworkspace` in Xcode
3. The KMP XCFramework links at `iosApp/Frameworks/Shared.xcframework`

## Targets
| Target | Role |
|---|---|
| CaminoSanctus | Main SwiftUI app |
| CaminoActivityMonitor | DeviceActivityMonitor app extension |

## Entitlements
`com.apple.developer.family-controls` requires explicit Apple approval.
Without it, `DeviceActivityMonitor` callbacks are silently suppressed on device.

## App Group
Shared UserDefaults suite: `group.com.panoplia.caminosanctus`
Must be configured in both target capabilities in Xcode.
