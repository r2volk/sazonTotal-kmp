package org.sazontotal.project

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

import org.jetbrains.compose.resources.painterResource
import org.sazontotal.project.screens.Dashboard
import org.sazontotal.project.screens.LoginScreen

import sazontotal.shared.generated.resources.Res
import sazontotal.shared.generated.resources.compose_multiplatform

@Composable
@Preview
fun App() {
    MaterialTheme {
        var pantallaActual by remember { mutableStateOf("login") }

        when (pantallaActual){
            "login" -> LoginScreen(
                onLogin = {  empleadoId,pin ->
                    if(empleadoId =="EMP-104"&&pin=="1234"){
                        pantallaActual = "dashboard"
                    }
                }
            )
            "dashboard" -> Dashboard()
        }
    }
}