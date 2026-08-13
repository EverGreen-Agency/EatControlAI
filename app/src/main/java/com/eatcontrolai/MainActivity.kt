package com.eatcontrolai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eatcontrolai.ui.analyze.AnalyzeScreen
import com.eatcontrolai.ui.analyze.AnalyzeViewModel
import com.eatcontrolai.ui.theme.EatControlAITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = (application as EatControlApp).container

        setContent {
            EatControlAITheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val viewModel: AnalyzeViewModel =
                        viewModel(factory = AnalyzeViewModel.Factory(container))
                    AnalyzeScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
