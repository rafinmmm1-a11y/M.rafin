package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.screens.MainUiContainer
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.VideoViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: VideoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainUiContainer(viewModel = viewModel)
            }
        }
    }
}
