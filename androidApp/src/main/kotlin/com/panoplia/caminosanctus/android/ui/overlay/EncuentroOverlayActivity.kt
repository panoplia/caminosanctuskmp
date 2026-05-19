package com.panoplia.caminosanctus.android.ui.overlay

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.panoplia.caminosanctus.android.ui.theme.CaminoTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class EncuentroOverlayActivity : ComponentActivity() {

    private val vm: EncuentroViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        val phase = intent.getIntExtra(EXTRA_PHASE, 1)
        vm.initializeIntercept(depthTier = phase)

        setContent {
            CaminoTheme {
                EncuentroOverlayScreen(
                    viewModel = vm,
                    onUnlock  = { finish() }
                )
            }
        }
    }

    companion object {
        const val EXTRA_PACKAGE_NAME = "pkg_name"
        const val EXTRA_PHASE        = "phase"
    }
}
