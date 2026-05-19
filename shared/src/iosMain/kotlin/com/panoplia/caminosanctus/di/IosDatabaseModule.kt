package com.panoplia.caminosanctus.di

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.panoplia.caminosanctus.db.CaminoDatabase
import org.koin.dsl.module

val iosDatabaseModule = module {
    single<CaminoDatabase> {
        CaminoDatabase(NativeSqliteDriver(CaminoDatabase.Schema, "camino.db"))
    }
}
