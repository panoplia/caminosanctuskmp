package com.panoplia.caminosanctus.platform

import kotlinx.coroutines.CoroutineDispatcher

expect val ioDispatcher: CoroutineDispatcher
expect val mainDispatcher: CoroutineDispatcher
expect fun String.normalizeForComparison(): String
expect fun generateUuid(): String
expect fun currentTimeMs(): Long

interface VpnManager {
    suspend fun engageSinkhole(packageName: String)
    suspend fun releaseSinkhole()
    fun isSinkholeActive(): Boolean
}
expect fun createVpnManager(): VpnManager

interface SLMEngine {
    fun isCapable(): Boolean
    suspend fun load()
    suspend fun generate(systemPrompt: String, userMessage: String, maxTokens: Int = 300): String
    fun unload()
    fun isLoaded(): Boolean
}
expect fun createSLMEngine(): SLMEngine
