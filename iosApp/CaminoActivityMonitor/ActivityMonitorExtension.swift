import DeviceActivity
import ManagedSettings
import FamilyControls

// Runs in its own sandboxed extension process — no UIKit calls allowed.
// Signals main app via shared App Group UserDefaults.
class CaminoActivityMonitor: DeviceActivityMonitor {

    let store = ManagedSettingsStore(named: .init("camino.shield"))
    let shared = UserDefaults(suiteName: "group.com.panoplia.caminosanctus")!

    override func eventDidReachThreshold(
        _ event: DeviceActivityEvent.Name,
        activity: DeviceActivityName
    ) {
        let phase = shared.integer(forKey: "umbral_phase_\(activity.rawValue)")

        // Signal main app to show Encuentro overlay on next foreground
        shared.set(activity.rawValue, forKey: "pending_intercept_activity")
        shared.set(max(phase, 1), forKey: "pending_intercept_phase")

        // Phase 3: apply ManagedSettings shield immediately, no UI needed
        if phase >= 3, let tokens = loadBlockedTokens(for: activity) {
            store.shield.applications = tokens
        }
    }

    override func intervalDidEnd(for activity: DeviceActivityName) {
        // Midnight reset — lift all shields, clear pending state
        store.clearAllSettings()
        shared.removeObject(forKey: "pending_intercept_activity")
        shared.removeObject(forKey: "pending_intercept_phase")
    }

    private func loadBlockedTokens(for activity: DeviceActivityName) -> Set<ApplicationToken>? {
        guard let data = shared.data(forKey: "blocked_tokens_\(activity.rawValue)") else { return nil }
        return try? JSONDecoder().decode(Set<ApplicationToken>.self, from: data)
    }
}
