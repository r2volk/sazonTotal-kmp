package org.sazontotal.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.SystemBarStyle
import android.content.res.Configuration
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.sazontotal.project.data.db.initAndroidContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        initAndroidContext(applicationContext)

        setContent {
            val oscuro = (LocalConfiguration.current.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
            SideEffect {
                val transparente = android.graphics.Color.TRANSPARENT
                val estilo = if (oscuro) SystemBarStyle.dark(transparente)
                    else SystemBarStyle.light(transparente, transparente)
                enableEdgeToEdge(statusBarStyle = estilo, navigationBarStyle = estilo)
                window.setBackgroundDrawableResource(
                    if (oscuro) android.R.color.black else android.R.color.white
                )
            }
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}