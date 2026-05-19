package com.panoplia.caminosanctus.android.di

import com.panoplia.caminosanctus.android.ui.overlay.EncuentroViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val androidUiModule = module {
    viewModel { EncuentroViewModel(get()) }
}
