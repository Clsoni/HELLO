package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(AntiqueDark), contentAlignment = Alignment.Center) {
        Card(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AntiqueLight),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("SWASTIK GOLD", color = SwastikGold, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Admin / User Login", color = Color.White)
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
                if (error) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Invalid credentials", color = PriceRed, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        if (username == "admin" && password == "admin") {
                            onLoginSuccess()
                        } else {
                            error = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SwastikGold)
                ) {
                    Text("LOGIN", color = AntiqueDark, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
