package com.eatcontrolai

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eatcontrolai.ui.EatControlRoot
import com.eatcontrolai.ui.EatControlViewModel
import com.eatcontrolai.ui.theme.EatControlAITheme
import com.eatcontrolai.ui.theme.EcColors

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = (application as EatControlApp).container

        setContent {
            EatControlAITheme {
                Box(Modifier.fillMaxSize().background(EcColors.Background)) {
                    val viewModel: EatControlViewModel =
                        viewModel(factory = EatControlViewModel.Factory(container))
                    EatControlRoot(viewModel)
                }
            }
        }
    }

    /** Mantém o deep link de retorno do Meta AI na Activity existente (`singleTop`). */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}
