package com.example.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import com.example.model.Product
import com.example.model.MarketQuote
import com.example.viewmodel.MarketViewModel
import com.example.ui.theme.*

@Composable
fun AppNavigation(viewModel: MarketViewModel) {
    var currentScreen by remember { mutableStateOf("LiveRates") }
    var isAdminAuthenticated by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val showPopup by viewModel.showPopup.collectAsStateWithLifecycle()
    val popupText by viewModel.popupText.collectAsStateWithLifecycle()

    if (showPopup) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissPopup() },
            title = { Text("Welcome", color = SwastikGold) },
            text = { Text(popupText, color = Color.White) },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissPopup() }) {
                    Text("OK", color = SwastikGold)
                }
            },
            containerColor = HeaderBg
        )
    }

    Scaffold(
        topBar = { 
            Column(modifier = Modifier.background(HeaderBg)) {
                TopHeader(
                    onMenuClick = { showMenu = !showMenu },
                    showMenu = showMenu,
                    onDismissMenu = { showMenu = false },
                    onNavigateToAdmin = { 
                        currentScreen = "Admin"
                        showMenu = false 
                    }
                )
                if (!isLandscape || currentScreen != "LiveRates") {
                    CustomTopNavigation(currentScreen) { currentScreen = it }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(AppBg)
        ) {
            if (isLandscape && currentScreen == "LiveRates") {
                LandscapeLiveRates(viewModel)
            } else {
                when (currentScreen) {
                    "LiveRates" -> PortraitLiveRates(viewModel)
                    "ContactBank" -> ContactBankScreen(viewModel)
                    "Messages" -> MessagesScreen(viewModel)
                    "AboutUs" -> AboutUsScreen()
                    "Admin" -> {
                        if (isAdminAuthenticated) {
                            AdminScreen(viewModel)
                        } else {
                            LoginScreen(onLoginSuccess = { isAdminAuthenticated = true })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopHeader(
    onMenuClick: () -> Unit,
    showMenu: Boolean,
    onDismissMenu: () -> Unit,
    onNavigateToAdmin: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(HeaderBg)
            .statusBarsPadding()
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Box(modifier = Modifier.align(Alignment.CenterStart)) {
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier
                    .size(40.dp)
                    .background(AccentRed, shape = androidx.compose.foundation.shape.CircleShape)
            ) {
                Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = Color.White, modifier = Modifier.size(24.dp))
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = onDismissMenu,
                modifier = Modifier.background(HeaderBg)
            ) {
                DropdownMenuItem(
                    text = { Text("Admin Panel", color = Color.White) },
                    onClick = onNavigateToAdmin,
                    leadingIcon = { Icon(Icons.Filled.AdminPanelSettings, contentDescription = null, tint = SwastikGold) }
                )
            }
        }
        Text(
            text = "卐 SWASTIK GOLD",
            color = SwastikGold,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun CustomTopNavigation(currentScreen: String, onNavigate: (String) -> Unit) {
    val tabs = listOf(
        Triple("LiveRates", "Live Rates", Icons.Filled.ShowChart),
        Triple("ContactBank", "Contact & Bank", Icons.Filled.AccountBalance),
        Triple("Messages", "Messages", Icons.Filled.Message),
        Triple("AboutUs", "About Us", Icons.Filled.Info)
    )
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(HeaderBg)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        tabs.forEach { tab ->
            val isSelected = tab.first == currentScreen
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onNavigate(tab.first) }
                    .weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .height(32.dp)
                        .width(64.dp)
                        .background(
                            color = if (isSelected) ActiveTabPill else Color.Transparent,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = tab.third,
                        contentDescription = tab.second,
                        modifier = Modifier.size(20.dp),
                        tint = if (isSelected) Color.Black else InactiveTabText
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = tab.second,
                    color = if (isSelected) ActiveTabText else InactiveTabText,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
        }
    }
}
