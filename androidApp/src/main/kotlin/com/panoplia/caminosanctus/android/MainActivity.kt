package com.panoplia.caminosanctus.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.work.*
import com.panoplia.caminosanctus.android.ui.theme.CaminoTheme
import com.panoplia.caminosanctus.android.wellbeing.UsagePollWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scheduleUsagePolling()
        setContent {
            CaminoTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    CaminoNavHost()
                }
            }
        }
    }

    private fun scheduleUsagePolling() {
        val request = PeriodicWorkRequestBuilder<UsagePollWorker>(15, TimeUnit.MINUTES)
            .setConstraints(Constraints.Builder().setRequiresBatteryNotLow(false).build())
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "camino_usage_poll",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}
