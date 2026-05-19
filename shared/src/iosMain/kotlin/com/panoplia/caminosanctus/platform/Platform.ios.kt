package com.panoplia.caminosanctus.platform

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineDispatcher
import platform.Foundation.NSString
import platform.Foundation.NSUUID
import platform.Foundation.NSDate
import platform.Foundation.stringByFoldingWithOptions
import platform.Foundation.NSDiacriticInsensitiveSearch
import platform.Foundation.NSCaseInsensitiveSearch

actual val ioDispatcher: CoroutineDispatcher   = Dispatchers.Default
actual val mainDispatcher: CoroutineDispatcher = Dispatchers.Main

actual fun String.normalizeForComparison(): String =
    (this as NSString).stringByFoldingWithOptions(
        NSDiacriticInsensitiveSearch or NSCaseInsensitiveSearch, locale = null
    ).trim()

actual fun generateUuid(): String = NSUUID().UUIDString()
actual fun currentTimeMs(): Long  = (NSDate().timeIntervalSince1970 * 1000).toLong()

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
