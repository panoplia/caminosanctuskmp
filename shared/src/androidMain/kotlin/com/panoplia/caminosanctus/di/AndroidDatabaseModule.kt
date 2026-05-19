package com.panoplia.caminosanctus.di

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.panoplia.caminosanctus.db.CaminoDatabase
import org.koin.dsl.module

fun androidDatabaseModule(context: Context) = module {
    single<CaminoDatabase> {
        CaminoDatabase(AndroidSqliteDriver(CaminoDatabase.Schema, context, "camino.db"))
    }
}
