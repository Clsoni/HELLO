package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
fun ContactBankScreen(viewModel: MarketViewModel) {
    val bankAccounts by viewModel.bankAccounts.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Call, contentDescription = "Contact", tint = AntiqueDark)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("CONTACT SWASTIK GOLD JALORE", color = AntiqueDark, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Connect with us or visit Jalore's elite bullion merchants at our official holding premises:", color = Color.Gray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    ContactRow("PHONE NUMBERS:", "9414152854 / 9772277054")
                    Spacer(modifier = Modifier.height(8.dp))
                    ContactRow("OFFICIAL EMAIL:", "swastikgoldjalore@gmail.com")
                    Spacer(modifier = Modifier.height(8.dp))
                    ContactRow("HOLDING ADDRESS:", "Jiwibai complex, gandhi chowk, Jalore (raj.)343001")
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
                    Text("SWASTIK BULLION OFFICIAL ACCOUNTS", color = AntiqueDark, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("To settle physical deliveries or deposit margin funds for booking locks, transfer payment to following accounts:", color = Color.Gray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    bankAccounts.forEach { bank ->
                        BankRow(bank.bankName, bank.accountNo, bank.ifsc, bank.branch, bank.type)
                        Divider(modifier = Modifier.padding(vertical = 12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ContactRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(label, color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1f))
        Text(value, color = AntiqueDark, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(2f))
    }
}

@Composable
fun BankRow(bankName: String, accountNo: String, ifsc: String, branch: String, type: String) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("BANK NAME:", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1f))
            Text(bankName, color = AntiqueDark, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(2f))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("ACCOUNT NO:", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1f))
            Text(accountNo, color = AntiqueDark, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(2f))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("IFSC CODE:", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1f))
            Text(ifsc, color = AntiqueDark, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(2f))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("BRANCH:", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1f))
            Text(branch, color = AntiqueDark, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(2f))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("ACCOUNT TYPE:", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1f))
            Text(type, color = AntiqueDark, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(2f))
        }
    }
}
