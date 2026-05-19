package com.panoplia.caminosanctus.platform

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineDispatcher
import java.text.Normalizer
import java.util.UUID

actual val ioDispatcher: CoroutineDispatcher   = Dispatchers.IO
actual val mainDispatcher: CoroutineDispatcher = Dispatchers.Main

actual fun String.normalizeForComparison(): String =
    Normalizer.normalize(this, Normalizer.Form.NFD)
        .replace(Regex("\\p{InCombiningDiacriticalMarks}"), "")
        .lowercase().trim()

actual fun generateUuid(): String = UUID.randomUUID().toString()
actual fun currentTimeMs(): Long  = System.currentTimeMillis()

actual fun createVpnManager(): VpnManager = object : VpnManager {
    override suspend fun engageSinkhole(packageName: String) {}
    override suspend fun releaseSinkhole() {}
    override fun isSinkholeActive(): Boolean = false
}

actual fun createSLMEngine(): SLMEngine = object : SLMEngine {
    override fun isCapable(): Boolean = false
    override suspend fun load() {}
    override suspend fun generate(systemPrompt: String, userMessage: String, maxTokens: Int) = ""
    override fun unload() {}
    override fun isLoaded(): Boolean = false
}
