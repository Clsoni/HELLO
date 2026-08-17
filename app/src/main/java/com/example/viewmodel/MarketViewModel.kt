package com.example.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.BankAccount
import com.example.model.BroadcastMessage
import com.example.model.MarketQuote
import com.example.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class MarketViewModel : ViewModel() {

    private val httpClient = OkHttpClient()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products = _products.asStateFlow()

    private val _marketQuotes = MutableStateFlow<List<MarketQuote>>(emptyList())
    val marketQuotes = _marketQuotes.asStateFlow()

    private val _silverSpot = MutableStateFlow(0.0)
    val silverSpot = _silverSpot.asStateFlow()

    private val _goldSpot = MutableStateFlow(0.0)
    val goldSpot = _goldSpot.asStateFlow()

    private val _usdInr = MutableStateFlow(0.0)
    val usdInr = _usdInr.asStateFlow()

    private val _marqueeText = MutableStateFlow("Welcome to Swastik Gold")
    val marqueeText = _marqueeText.asStateFlow()

    private val _popupText = MutableStateFlow("")
    val popupText = _popupText.asStateFlow()

    private val _messages = MutableStateFlow<List<BroadcastMessage>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _bankAccounts = MutableStateFlow<List<BankAccount>>(emptyList())
    val bankAccounts = _bankAccounts.asStateFlow()

    private val _showPopup = MutableStateFlow(true) // Used by Compose to show/hide the popup for UI matching
    val showPopup = _showPopup.asStateFlow()

    private val _isFrozen = MutableStateFlow(false)
    val isFrozen = _isFrozen.asStateFlow()

    private val _isSpotHidden = MutableStateFlow(false)
    val isSpotHidden = _isSpotHidden.asStateFlow()

    private val _aliases = MutableStateFlow<Map<String, String>>(emptyMap())
    val aliases = _aliases.asStateFlow()

    init {
        loadInitialData()
        startRealtimeSync()
        startConfigSync()
    }

    private fun loadInitialData() {
        _products.value = emptyList()
        _marketQuotes.value = emptyList()

        _messages.value = listOf(
            BroadcastMessage("1", "OFFICIAL BULLETIN", "25 Jun 2026, 02:24 am", "Swastik Gold में मेसेज सेवाएं भी उपलब्ध है जिसके जरिए आप Swastik Gold से हमेशा जुड़े रहेंगे धन्यवाद")
        )

        _bankAccounts.value = emptyList()
    }

    private fun startConfigSync() {
        viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                try {
                    // Sync full config from website config
                    val request = Request.Builder()
                        .url("https://mygoldking.net/api/get_config.php")
                        .build()
                    val response = httpClient.newCall(request).execute()
                    val bodyString = response.body?.string()
                    
                    if (response.isSuccessful && !bodyString.isNullOrBlank()) {
                        val json = JSONObject(bodyString)
                        
                        // Parse Marquee
                        if (json.has("marquee")) {
                            _marqueeText.value = json.getString("marquee")
                        }
                        
                        // Parse Popup
                        if (json.has("popup")) {
                            _popupText.value = json.getString("popup")
                            if (_popupText.value.isNotBlank() && _showPopup.value == false) {
                                // optional: could re-trigger popup if changed, but lets just update text
                            }
                        }
                        
                        // Parse Banks
                        if (json.has("banks")) {
                            val banksArr = json.getJSONArray("banks")
                            val newBanks = mutableListOf<BankAccount>()
                            for (i in 0 until banksArr.length()) {
                                val b = banksArr.getJSONObject(i)
                                newBanks.add(
                                    BankAccount(
                                        b.optString("name"),
                                        b.optString("account"),
                                        b.optString("ifsc"),
                                        b.optString("branch"),
                                        "Bank Account"
                                    )
                                )
                            }
                            _bankAccounts.value = newBanks
                        }
                        
                        // Parse Products Overrides (Alias, Premium, Visibility)
                        if (json.has("products")) {
                            val prodsObj = json.getJSONObject("products")
                            val newAliases = mutableMapOf<String, String>()
                            
                            // We will update the _products list directly if the product exists
                            _products.update { currentList ->
                                currentList.map { prod ->
                                    if (prodsObj.has(prod.id)) { // ID here is the original name from API
                                        val pconf = prodsObj.getJSONObject(prod.id)
                                        val alias = pconf.optString("alias", "")
                                        if (alias.isNotBlank()) newAliases[prod.id] = alias
                                        
                                        prod.copy(
                                            isRowHidden = pconf.optBoolean("hideRow", false),
                                            buyPremium = pconf.optDouble("buyPrem", 0.0),
                                            isBuyHidden = pconf.optBoolean("hideBuy", false),
                                            sellPremium = pconf.optDouble("sellPrem", 0.0),
                                            isSellHidden = pconf.optBoolean("hideSell", false)
                                        )
                                    } else {
                                        prod
                                    }
                                }
                            }
                            _aliases.value = newAliases
                        }
                    }
                } catch (e: Exception) {
                    // Silently fail if not deployed yet
                }
                delay(5000)
            }
        }
    }

    private fun startRealtimeSync() {
        viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                if (!_isFrozen.value) {
                    try {
                        val request = Request.Builder()
                            .url("https://mygoldking.net/api/live-rates?_=${System.currentTimeMillis()}")
                            .build()
                        
                        val response = httpClient.newCall(request).execute()
                        val bodyString = response.body?.string()
                        
                        if (response.isSuccessful && !bodyString.isNullOrBlank()) {
                            parseApiResponse(bodyString)
                        }
                    } catch (e: Exception) {
                        Log.e("MarketViewModel", "Error fetching live rates", e)
                    }
                }
                delay(1000) // Poll exactly every second as requested
            }
        }
    }

    private fun parseApiResponse(response: String) {
        val lines = response.split("\r\n", "\n")
        
        val newProducts = _products.value.toMutableList()
        val newQuotes = _marketQuotes.value.toMutableList()

        for (line in lines) {
            if (line.isBlank()) continue
            val parts = line.split("\t")
            if (parts.size >= 7) {
                val originalName = parts[2].trim()
                val name = originalName // We use original name as ID

                val buyStr = parts[3].trim()
                val sellStr = parts[4].trim()
                val highStr = parts[5].trim()
                val lowStr = parts[6].trim()

                val buy = buyStr.toDoubleOrNull()
                val sell = sellStr.toDoubleOrNull()
                val high = highStr.toDoubleOrNull()
                val low = lowStr.toDoubleOrNull()

                when {
                    name.equals("SILVER", ignoreCase = true) -> buy?.let { _silverSpot.value = it }
                    name.equals("GOLD", ignoreCase = true) -> buy?.let { _goldSpot.value = it }
                    name.equals("USDINR", ignoreCase = true) -> buy?.let { _usdInr.value = it }
                    name.contains("FUTURE", ignoreCase = true) -> {
                        val existingIndex = newQuotes.indexOfFirst { it.id.equals(name, ignoreCase = true) }
                        if (existingIndex >= 0) {
                            val existing = newQuotes[existingIndex]
                            newQuotes[existingIndex] = existing.copy(
                                bid = buy ?: existing.bid,
                                ask = sell ?: existing.ask,
                                high = maxOfNullable(existing.high, buy, sell, high),
                                low = minOfNullable(existing.low, buy, sell, low)
                            )
                        } else {
                            // Using symbol as both ID and symbol for futures
                            newQuotes.add(MarketQuote(name, name, buy ?: 0.0, sell ?: 0.0, high, low))
                        }
                    }
                    else -> {
                        val existingIndex = newProducts.indexOfFirst { it.id.equals(name, ignoreCase = true) }
                        if (existingIndex >= 0) {
                            val existing = newProducts[existingIndex]
                            newProducts[existingIndex] = existing.copy(
                                buy = buy,
                                sell = sell,
                                buyHigh = maxOfNullable(existing.buyHigh, buy, high),
                                buyLow = minOfNullable(existing.buyLow, buy, low),
                                sellHigh = maxOfNullable(existing.sellHigh, sell, high),
                                sellLow = minOfNullable(existing.sellLow, sell, low)
                            )
                        } else {
                            // First time we see this product, id=originalName, name=originalName
                            newProducts.add(Product(name, name, buy, sell, high, low))
                        }
                    }
                }
            }
        }
        
        _products.value = newProducts
        _marketQuotes.value = newQuotes
    }

    fun dismissPopup() {
        _showPopup.value = false
    }

    fun toggleFreeze() {
        _isFrozen.value = !_isFrozen.value
    }

    fun toggleHideSpot() {
        _isSpotHidden.value = !_isSpotHidden.value
    }

    private fun maxOfNullable(vararg values: Double?): Double? {
        val list = values.filterNotNull()
        return if (list.isEmpty()) null else list.maxOrNull()
    }

    private fun minOfNullable(vararg values: Double?): Double? {
        val list = values.filterNotNull()
        return if (list.isEmpty()) null else list.minOrNull()
    }
}
