package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.atomiccryptids.campsiteapp.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val session: AppSessionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {

            MyApplicationTheme {
                CampsiteApp(session)
            }
        }
    }
}
