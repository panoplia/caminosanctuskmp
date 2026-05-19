package com.panoplia.caminosanctus.android

import android.app.Application
import com.panoplia.caminosanctus.android.di.androidUiModule
import com.panoplia.caminosanctus.di.androidDatabaseModule
import com.panoplia.caminosanctus.di.sharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CaminoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@CaminoApplication)
            modules(androidDatabaseModule(this@CaminoApplication), sharedModule, androidUiModule)
        }
    }
}
