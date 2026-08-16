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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import kotlin.random.Random

class MarketViewModel : ViewModel() {

    private val httpClient = OkHttpClient()

    private val _silverSpot = MutableStateFlow(61.53)
    val silverSpot: StateFlow<Double> = _silverSpot.asStateFlow()

    private val _goldSpot = MutableStateFlow(4254.30)
    val goldSpot: StateFlow<Double> = _goldSpot.asStateFlow()

    private val _usdInr = MutableStateFlow(95.210)
    val usdInr: StateFlow<Double> = _usdInr.asStateFlow()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _marketQuotes = MutableStateFlow<List<MarketQuote>>(emptyList())
    val marketQuotes: StateFlow<List<MarketQuote>> = _marketQuotes.asStateFlow()

    private val _messages = MutableStateFlow<List<BroadcastMessage>>(emptyList())
    val messages: StateFlow<List<BroadcastMessage>> = _messages.asStateFlow()

    private val _bankAccounts = MutableStateFlow<List<BankAccount>>(emptyList())
    val bankAccounts: StateFlow<List<BankAccount>> = _bankAccounts.asStateFlow()

    private val _marqueeText = MutableStateFlow("नमस्कार, SWASTIK GOLD में आपका स्वागत है। ❖ यह भाव रेफ्रेन्स के तौर पर दिए जा रहे हैं ❖")
    val marqueeText = _marqueeText.asStateFlow()

    private val _popupText = MutableStateFlow("SWASTIK GOLD में आपका स्वागत है। हमारी बुकिंग सेवा सुबह 10:00 बजे शुरू होती है और रात को 8:00 बजे बंद होती है।")
    val popupText = _popupText.asStateFlow()
    
    private val _showPopup = MutableStateFlow(false) // Disable popup for UI matching
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
        _bankAccounts.value = listOf(
            BankAccount("HDFC Bank Ltd", "50200084712035", "HDFC0000241", "gandhi chowk, Jalore", "Bullion Current Account"),
            BankAccount("State Bank of India", "38147295103", "SBIN0001034", "Jalore", "Current Account")
        )
    }

    private fun startConfigSync() {
        viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                try {
                    // Sync aliases from website config
                    val request = Request.Builder()
                        .url("https://swastikgold.net/api/get_config.php")
                        .build()
                    val response = httpClient.newCall(request).execute()
                    val bodyString = response.body?.string()
                    if (response.isSuccessful && !bodyString.isNullOrBlank()) {
                        val json = org.json.JSONObject(bodyString)
                        if (json.has("aliases")) {
                            val aliasesObj = json.getJSONObject("aliases")
                            val newAliases = mutableMapOf<String, String>()
                            aliasesObj.keys().forEach { key ->
                                newAliases[key] = aliasesObj.getString(key)
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
                            .url("https://bcast.sundhagold.com:7768/VOTSBroadcastStreaming/Services/xml/GetLiveRateByTemplateID/sundhagold?_=${System.currentTimeMillis()}")
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
                // Do not apply alias here, keep original name for mapping, but we can expose a getter or apply it dynamically in the UI
                val name = originalName

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
                        val existingIndex = newQuotes.indexOfFirst { it.symbol.equals(name, ignoreCase = true) }
                        if (existingIndex >= 0) {
                            val existing = newQuotes[existingIndex]
                            newQuotes[existingIndex] = existing.copy(
                                bid = buy ?: existing.bid,
                                ask = sell ?: existing.ask,
                                high = maxOfNullable(existing.high, buy, sell, high),
                                low = minOfNullable(existing.low, buy, sell, low)
                            )
                        } else {
                            newQuotes.add(MarketQuote(name, name, buy ?: 0.0, sell ?: 0.0, high, low))
                        }
                    }
                    else -> {
                        val existingIndex = newProducts.indexOfFirst { it.name.equals(name, ignoreCase = true) }
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

    fun updateMarquee(text: String) {
        _marqueeText.value = text
    }

    fun updatePopup(text: String) {
        _popupText.value = text
    }

    fun updateAlias(originalName: String, newName: String) {
        val currentAliases = _aliases.value.toMutableMap()
        if (newName.isBlank()) {
            currentAliases.remove(originalName)
        } else {
            currentAliases[originalName] = newName
        }
        _aliases.value = currentAliases

        // Save to backend
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val json = JSONObject()
                val aliasObj = JSONObject()
                currentAliases.forEach { (k, v) -> aliasObj.put(k, v) }
                json.put("aliases", aliasObj)
                
                val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
                val request = Request.Builder()
                    .url("https://swastikgold.net/api/save_config.php")
                    .post(body)
                    .build()
                httpClient.newCall(request).execute()
            } catch (e: Exception) {
                Log.e("MarketViewModel", "Failed to save config", e)
            }
        }
    }

    fun updateProductPremium(id: String, buyPremium: Double, sellPremium: Double) {
        _products.update { current ->
            current.map { if (it.id == id) it.copy(buyPremium = buyPremium, sellPremium = sellPremium) else it }
        }
    }

    fun toggleProductRowVisibility(id: String) {
        _products.update { current ->
            current.map { if (it.id == id) it.copy(isRowHidden = !it.isRowHidden) else it }
        }
    }

    fun toggleProductBuyVisibility(id: String) {
        _products.update { current ->
            current.map { if (it.id == id) it.copy(isBuyHidden = !it.isBuyHidden) else it }
        }
    }

    fun toggleProductSellVisibility(id: String) {
        _products.update { current ->
            current.map { if (it.id == id) it.copy(isSellHidden = !it.isSellHidden) else it }
        }
    }

    fun moveProductUp(id: String) {
        _products.update { current ->
            val index = current.indexOfFirst { it.id == id }
            if (index > 0) {
                val newList = current.toMutableList()
                val temp = newList[index - 1]
                newList[index - 1] = newList[index]
                newList[index] = temp
                newList
            } else {
                current
            }
        }
    }

    fun moveProductDown(id: String) {
        _products.update { current ->
            val index = current.indexOfFirst { it.id == id }
            if (index < current.size - 1) {
                val newList = current.toMutableList()
                val temp = newList[index + 1]
                newList[index + 1] = newList[index]
                newList[index] = temp
                newList
            } else {
                current
            }
        }
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
