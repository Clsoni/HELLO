package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun AboutUsScreen() {
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
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("SWASTIK GOLD", color = SwastikGold, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Trust • Purity • Excellence", color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Swastik Gold Jalore is a premier name in the bullion market of Rajasthan. We provide real-time accurate live rates for Gold, Silver, and MCX Fut, ensuring unmatched transparency and premium services for jewellers, investors, and individuals alike.",
                        color = Color.LightGray,
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp
                    )
                }
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = AntiqueLight),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Star, contentDescription = "Vision", tint = SwastikGold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("OUR VISION", color = SwastikGold, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("To bring the most transparent and advanced digital rate solutions directly to every jeweler's fingertips.", color = Color.LightGray, fontSize = 12.sp)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = AntiqueLight),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Star, contentDescription = "Promise", tint = SwastikGold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("OUR PROMISE", color = SwastikGold, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("100% verified rates, clean OTC charts, zero latency data updates, and authentic investment advisory.", color = Color.LightGray, fontSize = 12.sp)
                    }
                }
            }
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AntiqueLight),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Key Information", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    InfoRow("FOUNDER / PROPRIETOR", "Champalal Soni")
                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.DarkGray)
                    InfoRow("HEADQUARTERS", "Jalore, Rajasthan, India")
                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.DarkGray)
                    InfoRow("ESTABLISHED YEAR", "1998")
                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.DarkGray)
                    InfoRow("SERVICES OFFERED", "Real-time metal rates, physical gold delivery, and portfolio management.")
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column {
        Text(label, color = SwastikGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, color = Color.White, fontSize = 14.sp)
    }
}
