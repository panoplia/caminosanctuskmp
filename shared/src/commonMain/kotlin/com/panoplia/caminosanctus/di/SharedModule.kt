package com.panoplia.caminosanctus.di

import com.panoplia.caminosanctus.data.ContentRepositoryImpl
import com.panoplia.caminosanctus.data.InterceptLogRepositoryImpl
import com.panoplia.caminosanctus.data.UserProfileRepositoryImpl
import com.panoplia.caminosanctus.domain.ContentRepository
import com.panoplia.caminosanctus.domain.InterceptLogRepository
import com.panoplia.caminosanctus.domain.UserProfileRepository
import com.panoplia.caminosanctus.intercept.PhaseEngine
import com.panoplia.caminosanctus.platform.createSLMEngine
import com.panoplia.caminosanctus.platform.createVpnManager
import org.koin.core.module.Module
import org.koin.dsl.module

val sharedModule: Module = module {
    single { createVpnManager() }
    single { createSLMEngine() }

    single<ContentRepository>      { ContentRepositoryImpl(get()) }
    single<InterceptLogRepository> { InterceptLogRepositoryImpl(get()) }
    single<UserProfileRepository>  { UserProfileRepositoryImpl(get()) }

    single { PhaseEngine(get()) }
}
