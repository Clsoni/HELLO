package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.viewmodel.MarketViewModel
import com.example.ui.theme.*

@Composable
fun AdminScreen(viewModel: MarketViewModel) {
    var marqueeInput by remember { mutableStateOf("") }
    var popupInput by remember { mutableStateOf("") }
    val isFrozen by viewModel.isFrozen.collectAsStateWithLifecycle()
    val isSpotHidden by viewModel.isSpotHidden.collectAsStateWithLifecycle()

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
                    Text("SUB-ADMIN CONSOLE", color = SwastikGold, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("RUNNING TEXT LINE MARQUEE BROADCAST", color = SwastikGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = marqueeInput,
                        onValueChange = { marqueeInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = AntiqueLight,
                            focusedContainerColor = AntiqueLight,
                            unfocusedTextColor = Color.White,
                            focusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { viewModel.updateMarquee(marqueeInput) },
                        colors = ButtonDefaults.buttonColors(containerColor = SwastikGold),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("APPLY TO MARQUEE", color = AntiqueDark, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AntiqueDark),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("SEND CUSTOMER POP-UP ALERT ANNOUNCEMENT", color = SwastikGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = popupInput,
                        onValueChange = { popupInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = AntiqueLight,
                            focusedContainerColor = AntiqueLight,
                            unfocusedTextColor = Color.White,
                            focusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { popupInput = "" },
                            colors = ButtonDefaults.buttonColors(containerColor = PriceRed),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("CLEAR POPUP", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { viewModel.updatePopup(popupInput) },
                            colors = ButtonDefaults.buttonColors(containerColor = SwastikGold),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("BROADCAST ALERT POPUP", color = AntiqueDark, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AntiqueDark),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("LIVE ENGINE & SPOT PRICES CONTROLS", color = SwastikGold, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = AntiqueLight),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isFrozen) PriceRed else PriceGreen)
                    ) {
                        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("LIVE PRICES FREEZE COUNTER", color = SwastikGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(if (isFrozen) "Current Engine Status: FROZEN" else "Current Engine Status: RATES REAL-TIME LIVE", color = if (isFrozen) PriceRed else PriceGreen, fontSize = 12.sp)
                            }
                            Button(onClick = { viewModel.toggleFreeze() }, colors = ButtonDefaults.buttonColors(containerColor = PriceRed)) {
                                Text(if (isFrozen) "UNFREEZE" else "FREEZE PRICE")
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = AntiqueLight),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSpotHidden) PriceRed else PriceGreen)
                    ) {
                        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("SPOT PRICES VISIBILITY CONTROL", color = SwastikGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(if (isSpotHidden) "Current Spot Price Status: HIDDEN" else "Current Spot Price Status: PRICES DISPLAYED", color = if (isSpotHidden) PriceRed else PriceGreen, fontSize = 12.sp)
                            }
                            Button(onClick = { viewModel.toggleHideSpot() }, colors = ButtonDefaults.buttonColors(containerColor = PriceRed)) {
                                Text(if (isSpotHidden) "SHOW PRICE" else "HIDE PRICE")
                            }
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AntiqueDark),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("PRODUCT ALIAS CONTROLS", color = SwastikGold, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    val products by viewModel.products.collectAsStateWithLifecycle()
                    val aliases by viewModel.aliases.collectAsStateWithLifecycle()
                    products.forEach { product ->
                        ProductControlItem(product, aliases[product.name], viewModel)
                        Divider(color = AntiqueLight, modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ProductControlItem(product: com.example.model.Product, currentAlias: String?, viewModel: MarketViewModel) {
    var aliasInput by remember(currentAlias) { mutableStateOf(currentAlias ?: "") }
    
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(product.name, color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = { viewModel.moveProductUp(product.id) }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Filled.ArrowUpward, contentDescription = "Move Up", tint = Color.White)
                }
                IconButton(onClick = { viewModel.moveProductDown(product.id) }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Filled.ArrowDownward, contentDescription = "Move Down", tint = Color.White)
                }
                Button(
                    onClick = { viewModel.toggleProductRowVisibility(product.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = if (product.isRowHidden) PriceRed else PriceGreen),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                ) {
                    Text(if (product.isRowHidden) "HIDDEN" else "VISIBLE", fontSize = 10.sp)
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = aliasInput,
                onValueChange = { aliasInput = it },
                label = { Text("Display Name Alias (Optional)") },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = AntiqueLight,
                    focusedContainerColor = AntiqueLight,
                    unfocusedTextColor = Color.White,
                    focusedTextColor = Color.White
                )
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Button(
                    onClick = { viewModel.toggleProductBuyVisibility(product.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = if (product.isBuyHidden) PriceRed else PriceGreen)
                ) {
                    Text(if (product.isBuyHidden) "HIDE BUY" else "SHOW BUY", fontSize = 10.sp)
                }
                Button(
                    onClick = { viewModel.updateAlias(product.name, aliasInput) },
                    colors = ButtonDefaults.buttonColors(containerColor = SwastikGold)
                ) {
                    Text("SET ALIAS", color = AntiqueDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { viewModel.toggleProductSellVisibility(product.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = if (product.isSellHidden) PriceRed else PriceGreen)
                ) {
                    Text(if (product.isSellHidden) "HIDE SELL" else "SHOW SELL", fontSize = 10.sp)
                }
            }
        }
    }
}
