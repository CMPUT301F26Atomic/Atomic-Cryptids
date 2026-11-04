package com.atomiccryptids.campsiteapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.atomiccryptids.campsiteapp.ui.theme.MyApplicationTheme
import com.example.myapplication.CampsiteApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CampsiteApp()
            }
        }
    }
}