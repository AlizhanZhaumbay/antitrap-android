package com.example.exit_plan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.exit_plan.ui.navigation.AppNavHost
import com.example.exit_plan.ui.theme.AntitrapTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AntitrapTheme {
                AppNavHost()
            }
        }
    }
}
