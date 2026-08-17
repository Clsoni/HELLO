package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import kotlinx.coroutines.delay
import com.example.model.MarketQuote
import com.example.model.Product
import com.example.ui.theme.*
import com.example.viewmodel.MarketViewModel

@Composable
fun PortraitLiveRates(viewModel: MarketViewModel) {
    val products by viewModel.products.collectAsStateWithLifecycle()
    val quotes by viewModel.marketQuotes.collectAsStateWithLifecycle()
    val silverSpot by viewModel.silverSpot.collectAsStateWithLifecycle()
    val goldSpot by viewModel.goldSpot.collectAsStateWithLifecycle()
    val usdInr by viewModel.usdInr.collectAsStateWithLifecycle()
    val marqueeText by viewModel.marqueeText.collectAsStateWithLifecycle()
    val isSpotHidden by viewModel.isSpotHidden.collectAsStateWithLifecycle()
    val aliases by viewModel.aliases.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().background(AppBg)) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 4.dp, vertical = 2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = marqueeText,
                color = MarqueeText,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MarqueeBg)
                    .padding(horizontal = 4.dp, vertical = 2.dp)
                    .basicMarquee(),
                maxLines = 1,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            
            if (!isSpotHidden) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SpotBg, shape = RoundedCornerShape(4.dp))
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SpotPriceItem("SILVER", silverSpot)
                    Divider(modifier = Modifier.height(30.dp).width(1.dp), color = Color.LightGray.copy(alpha = 0.3f))
                    SpotPriceItem("GOLD", goldSpot)
                    Divider(modifier = Modifier.height(30.dp).width(1.dp), color = Color.LightGray.copy(alpha = 0.3f))
                    SpotPriceItem("USDINR", usdInr)
                }
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SpotBg, shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                    .padding(vertical = 4.dp, horizontal = 4.dp)
            ) {
                Text("PRODUCTS", color = Color.White, modifier = Modifier.weight(2.5f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("BUY", color = Color.White, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("SELL", color = Color.White, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            
            products.forEach { product ->
                if (!product.isRowHidden) {
                    ProductCard(product, aliases[product.name])
                }
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SpotBg, shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                    .padding(vertical = 4.dp, horizontal = 4.dp)
            ) {
                Text("PRODUCTS", color = Color.White, modifier = Modifier.weight(2.5f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("BID", color = Color.White, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("ASK", color = Color.White, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            
            quotes.forEach { quote ->
                QuoteCard(quote, aliases[quote.symbol])
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            FloatingActionButton(
                onClick = { /* Call */ },
                containerColor = BoxGreen,
                contentColor = Color.White,
                shape = androidx.compose.foundation.shape.CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Filled.Call, "Call")
            }
            Text(
                "Powered by : Champalal Soni.",
                color = Color.Black,
                fontSize = 11.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            FloatingActionButton(
                onClick = { /* Message */ },
                containerColor = BoxGreen,
                contentColor = Color.White,
                shape = androidx.compose.foundation.shape.CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Filled.Email, "Message")
            }
        }
    }
}

@Composable
fun SpotPriceItem(label: String, price: Double) {
    val decimals = if (label.equals("USDINR", ignoreCase = true)) 3 else 2
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        FlashingPriceText(
            price = price,
            decimals = decimals,
            restingTextColor = Color.White
        )
    }
}

@Composable
fun FlashingPriceText(
    price: Double?,
    modifier: Modifier = Modifier,
    isHidden: Boolean = false,
    decimals: Int = 0,
    restingTextColor: Color = Color.Black
) {
    var previousPrice by remember { mutableStateOf(price) }
    var bgColor by remember { mutableStateOf(Color.Transparent) }
    var textColor by remember { mutableStateOf(restingTextColor) }
    
    LaunchedEffect(price) {
        if (price != null && previousPrice != null && price != previousPrice) {
            bgColor = if (price > previousPrice!!) BoxGreen else BoxRed
            textColor = Color.White
            kotlinx.coroutines.delay(500)
            bgColor = Color.Transparent
            textColor = restingTextColor
        }
        previousPrice = price
    }
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (!isHidden && price != null) {
            val priceStr = if (decimals > 0) String.format("%.${decimals}f", price) else price.toInt().toString()
            Text(
                text = priceStr,
                modifier = Modifier
                    .background(bgColor, RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = textColor
            )
        }
    }
}

fun getDecimals(name: String): Int {
    if (name.contains("USDINR", ignoreCase = true)) return 3
    if (name.contains("COMEX", ignoreCase = true)) return 2
    if (name.equals("GOLD", ignoreCase = true) || name.equals("SILVER", ignoreCase = true)) return 2
    return 0
}

fun formatPrice(price: Double?, decimals: Int): String {
    if (price == null) return "-"
    return if (decimals > 0) String.format("%.${decimals}f", price) else price.toInt().toString()
}

@Composable
fun ProductCard(product: Product, alias: String? = null) {
    val displayName = alias ?: product.name
    val decimals = getDecimals(product.name)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(4.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(2.5f)) {
                Text(displayName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                if (product.buyHigh != null) {
                    Row {
                        Text("H: ${formatPrice(product.buyHigh, decimals)}", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("L: ${formatPrice(product.buyLow, decimals)}", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
            FlashingPriceText(
                price = if (product.buy != null) product.buy + product.buyPremium else null,
                modifier = Modifier.weight(1f),
                isHidden = product.isBuyHidden,
                decimals = decimals
            )
            FlashingPriceText(
                price = if (product.sell != null) product.sell + product.sellPremium else null,
                modifier = Modifier.weight(1f),
                isHidden = product.isSellHidden,
                decimals = decimals
            )
        }
    }
}

@Composable
fun QuoteCard(quote: MarketQuote, alias: String? = null) {
    val displayName = alias ?: quote.symbol
    val decimals = getDecimals(quote.symbol)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(4.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(2.5f)) {
                Text(displayName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                if (quote.high != null) {
                    Row {
                        Text("H: ${formatPrice(quote.high, decimals)}", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("L: ${formatPrice(quote.low, decimals)}", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
            FlashingPriceText(
                price = quote.bid,
                modifier = Modifier.weight(1f),
                decimals = decimals
            )
            FlashingPriceText(
                price = quote.ask,
                modifier = Modifier.weight(1f),
                decimals = decimals
            )
        }
    }
}

@Composable
fun LandscapeLiveRates(viewModel: MarketViewModel) {
    val products by viewModel.products.collectAsStateWithLifecycle()
    val quotes by viewModel.marketQuotes.collectAsStateWithLifecycle()
    val marqueeText by viewModel.marqueeText.collectAsStateWithLifecycle()
    val silverSpot by viewModel.silverSpot.collectAsStateWithLifecycle()
    val goldSpot by viewModel.goldSpot.collectAsStateWithLifecycle()
    val usdInr by viewModel.usdInr.collectAsStateWithLifecycle()
    val isSpotHidden by viewModel.isSpotHidden.collectAsStateWithLifecycle()
    val aliases by viewModel.aliases.collectAsStateWithLifecycle()
    
    Column(modifier = Modifier.fillMaxSize().background(AppBg)) {
        Text(
            text = marqueeText,
            color = MarqueeText,
            modifier = Modifier
                .fillMaxWidth()
                .background(MarqueeBg)
                .padding(8.dp),
            maxLines = 1,
            fontSize = 14.sp
        )
        if (!isSpotHidden) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SpotBg)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SpotPriceItem("SILVER", silverSpot)
                SpotPriceItem("GOLD", goldSpot)
                SpotPriceItem("USDINR", usdInr)
            }
        }
        Row(modifier = Modifier.weight(1f)) {
            // Left Panel (Physical)
            Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                Row(
                    modifier = Modifier.fillMaxWidth().background(SpotBg).padding(vertical = 4.dp, horizontal = 4.dp)
                ) {
                    Text("PRODUCTS", color = Color.White, modifier = Modifier.weight(2.5f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("BUY", color = Color.White, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("SELL", color = Color.White, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                LazyColumn(contentPadding = PaddingValues(bottom = 4.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    items(products) { product ->
                        if (!product.isRowHidden) {
                            ProductCard(product, aliases[product.name])
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.width(4.dp))
            // Right Panel (MCX/Comex)
            Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                Row(
                    modifier = Modifier.fillMaxWidth().background(SpotBg).padding(vertical = 4.dp, horizontal = 4.dp)
                ) {
                    Text("PRODUCTS", color = Color.White, modifier = Modifier.weight(2.5f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("BID", color = Color.White, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("ASK", color = Color.White, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                LazyColumn(contentPadding = PaddingValues(bottom = 4.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    items(quotes) { quote ->
                        QuoteCard(quote, aliases[quote.symbol])
                    }
                }
            }
        }
    }
}
