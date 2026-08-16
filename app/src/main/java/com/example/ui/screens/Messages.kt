package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.viewmodel.MarketViewModel
import com.example.ui.theme.*

@Composable
fun MessagesScreen(viewModel: MarketViewModel) {
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    var name by remember { mutableStateOf("") }
    var whatsapp by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AntiqueDark),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Notifications, contentDescription = "Alerts", tint = SwastikGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SWASTIK LIVE PERSONAL DESK", color = SwastikGold, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    messages.forEach { msg ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = AntiqueLight),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(msg.type, color = SwastikGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(msg.date, color = Color.Gray, fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(msg.text, color = Color.White, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("SEND MESSAGE OR BOOKING LOCK REQUEST", color = BrandGreen, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Submit your message, rate booking, or delivery schedule. This updates in real-time on our server.", color = Color.Gray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Your Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = whatsapp,
                        onValueChange = { whatsapp = it },
                        label = { Text("WhatsApp Number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = { Text("Your Booking / Query Message") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { /* Simulate Submit */ },
                        colors = ButtonDefaults.buttonColors(containerColor = PriceGreen),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("SUBMIT TO SERVER", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
