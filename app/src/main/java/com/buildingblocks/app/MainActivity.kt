package com.buildingblocks.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.buildingblocks.app.navigation.AppNavigation
import com.buildingblocks.app.ui.theme.BuildingBlocksTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BuildingBlocksTheme {
                AppNavigation()
            }
        }
    }
}
