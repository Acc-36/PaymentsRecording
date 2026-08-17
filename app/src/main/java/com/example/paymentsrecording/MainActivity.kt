package com.example.paymentsrecording

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.paymentsrecording.ui.AppViewModelFactory
import com.example.paymentsrecording.ui.navigation.MainScaffold
import com.example.paymentsrecording.ui.theme.PaymentsTheme
import com.example.paymentsrecording.util.ThemePref

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = this
            val themeMode = remember { mutableIntStateOf(ThemePref.getMode(context)) }
            PaymentsTheme(themeMode = themeMode.intValue) {
                Surface {
                    val container = (application as PaymentsApp).container
                    val factory = remember { AppViewModelFactory(container) }
                    MainScaffold(
                        factory = factory,
                        themeMode = themeMode.intValue,
                        onThemeChange = { mode ->
                            ThemePref.setMode(context, mode)
                            themeMode.intValue = mode
                        }
                    )
                }
            }
        }
    }
}
