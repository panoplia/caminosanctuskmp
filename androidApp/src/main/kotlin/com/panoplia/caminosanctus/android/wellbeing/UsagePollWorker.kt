package com.panoplia.caminosanctus.android.wellbeing

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.panoplia.caminosanctus.android.ui.overlay.EncuentroOverlayActivity
import com.panoplia.caminosanctus.domain.Phase
import com.panoplia.caminosanctus.intercept.PhaseEngine
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UsagePollWorker(
    private val appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params), KoinComponent {

    private val phaseEngine: PhaseEngine by inject()
    private val usm = appContext.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    override suspend fun doWork(): Result {
        val foregroundPkg = getForegroundApp() ?: return Result.success()

        val phase = phaseEngine.determinePhase(foregroundPkg)
        if (phase == Phase.IDLE) return Result.success()

        when (phase) {
            Phase.PROFUNDIDAD -> {
                val sinkIntent = BlockingVpnService.startIntent(listOf(foregroundPkg)).apply {
                    setClass(appContext, BlockingVpnService::class.java)
                }
                appContext.startForegroundService(sinkIntent)
                launchOverlay(foregroundPkg, phaseNumber = 3)
            }
            Phase.DISCIPLINA -> launchOverlay(foregroundPkg, phaseNumber = 2)
            Phase.ENCUENTRO  -> launchOverlay(foregroundPkg, phaseNumber = 1)
            Phase.IDLE       -> Unit
        }

        return Result.success()
    }

    private fun getForegroundApp(): String? {
        val now = System.currentTimeMillis()
        return usm
            .queryUsageStats(UsageStatsManager.INTERVAL_DAILY, now - 10_000L, now)
            ?.filter { it.lastTimeUsed > 0 }
            ?.maxByOrNull { it.lastTimeUsed }
            ?.packageName
    }

    private fun launchOverlay(pkg: String, phaseNumber: Int) {
        val intent = Intent(appContext, EncuentroOverlayActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(EncuentroOverlayActivity.EXTRA_PACKAGE_NAME, pkg)
            putExtra(EncuentroOverlayActivity.EXTRA_PHASE, phaseNumber)
        }
        appContext.startActivity(intent)
    }
}
