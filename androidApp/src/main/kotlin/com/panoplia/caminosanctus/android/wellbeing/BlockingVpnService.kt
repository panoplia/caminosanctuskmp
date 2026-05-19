package com.panoplia.caminosanctus.android.wellbeing

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import androidx.core.app.NotificationCompat

class BlockingVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val blockedPackages = intent?.getStringArrayListExtra(EXTRA_BLOCKED_PACKAGES)
            ?: return START_NOT_STICKY
        startForeground(NOTIF_ID, buildNotification())
        establishSinkhole(blockedPackages)
        return START_STICKY
    }

    private fun establishSinkhole(blockedPackages: List<String>) {
        vpnInterface?.close()
        val builder = Builder()
            .setSession("CaminoSanctus-Sinkhole")
            .addAddress("10.0.0.2", 32)
            .addRoute("0.0.0.0", 0)
            .addDnsServer("10.0.0.1")
            .setMtu(1500)
            .addDisallowedApplication(packageName)

        blockedPackages.forEach { pkg ->
            runCatching { builder.addAllowedApplication(pkg) }
        }

        vpnInterface = builder.establish()
    }

    override fun onDestroy() {
        vpnInterface?.close()
        vpnInterface = null
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Umbral Sagrado",
            NotificationManager.IMPORTANCE_LOW
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun buildNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("Umbral Sagrado activo")
        .setContentText("Aplicaciones bloqueadas por disciplina espiritual")
        .setSmallIcon(android.R.drawable.ic_lock_lock)
        .setOngoing(true)
        .build()

    companion object {
        const val EXTRA_BLOCKED_PACKAGES = "extra_blocked_packages"
        const val CHANNEL_ID = "camino_sinkhole"
        const val NOTIF_ID  = 1002

        fun startIntent(packages: List<String>) = Intent().apply {
            putStringArrayListExtra(EXTRA_BLOCKED_PACKAGES, ArrayList(packages))
        }
    }
}
