package com.rasya0020.valrank

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.rasya0020.valrank.navigation.SetupNavGraph
import com.rasya0020.valrank.ui.theme.ValRankTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ValRankTheme{
                SetupNavGraph()
            }
        }
    }
}