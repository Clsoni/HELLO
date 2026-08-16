// script.js - Real-time Live Rates via Polling

let products = [];
let quotes = [];
let silverSpot = 0;
let goldSpot = 0;
let usdinrSpot = 0;

let previousValues = {};
let config = { products: {}, marquee: "", popup: "", banks: [] };

function flash(element, nextValue, key) {
    if (element.innerText !== String(nextValue)) {
        if (previousValues[key] !== undefined) {
            let isUp = nextValue > previousValues[key];
            element.className = 'price-box ' + (isUp ? 'flash-up' : 'flash-down');
            setTimeout(() => {
                element.className = 'price-box';
            }, 500);
        }
        element.innerText = nextValue;
        previousValues[key] = nextValue;
    }
}

async function fetchConfig() {
    try {
        let res = await fetch('api/get_config.php?_=' + Date.now());
        if (res.ok) {
            config = await res.json();
            if (!config.products) config.products = {};
            
            let m = document.querySelector('marquee');
            if(m && config.marquee) m.innerText = config.marquee;
        }
    } catch(e) {
        console.error("Config fetch error:", e);
    }
}

function parseResponse(text) {
    products = [];
    quotes = [];
    
    let lines = text.split(/\r?\n/);
    lines.forEach(line => {
        if (line.trim() === '') return;
        let parts = line.split('\t');
        if (parts.length >= 7) {
            let originalName = parts[2].trim();
            let pconf = config.products[originalName] || {};
            
            if (pconf.hideRow) return;

            let displayName = pconf.alias || originalName;

            let baseBuy = parseFloat(parts[3]) || 0;
            let baseSell = parseFloat(parts[4]) || 0;
            let high = parseFloat(parts[5]) || 0;
            let low = parseFloat(parts[6]) || 0;
            
            let buy = baseBuy + (pconf.buyPrem || 0);
            let sell = baseSell + (pconf.sellPrem || 0);

            let nameUpper = originalName.toUpperCase();
            
            if (nameUpper === 'SILVER') {
                silverSpot = baseBuy;
            } else if (nameUpper === 'GOLD') {
                goldSpot = baseBuy;
            } else if (nameUpper === 'USDINR') {
                usdinrSpot = baseBuy;
            } else if (nameUpper.includes('FUTURE')) {
                quotes.push({ symbol: displayName, original: originalName, bid: buy, ask: sell, high: high, low: low, hideBuy: pconf.hideBuy, hideSell: pconf.hideSell });
            } else {
                products.push({ id: parts[1], name: displayName, original: originalName, buy: buy, sell: sell, buyHigh: high, buyLow: low, hideBuy: pconf.hideBuy, hideSell: pconf.hideSell });
            }
        }
    });
}

function renderTable(data, tableId, buyKey, sellKey) {
    const tbody = document.getElementById(tableId);
    if (!tbody) return;
    
    tbody.innerHTML = '';
    data.forEach(item => {
        const name = item.name || item.symbol;
        const buy = item[buyKey];
        const sell = item[sellKey];
        const high = item.buyHigh || item.high;
        const low = item.buyLow || item.low;
        
        let buyStr = (buy === 0 || isNaN(buy) || item.hideBuy) ? '-' : buy.toFixed(0);
        let sellStr = (sell === 0 || isNaN(sell) || item.hideSell) ? '-' : sell.toFixed(0);
        let highStr = (high === 0 || isNaN(high)) ? '-' : high.toFixed(0);
        let lowStr = (low === 0 || isNaN(low)) ? '-' : low.toFixed(0);

        tbody.innerHTML += `
            <tr>
                <td>
                    ${name}
                    <span class="high-low">H: ${highStr} &nbsp; L: ${lowStr}</span>
                </td>
                <td><div class="price-box" id="${item.original}-buy">${buyStr}</div></td>
                <td><div class="price-box" id="${item.original}-sell">${sellStr}</div></td>
            </tr>
        `;
    });
}

async function fetchMarketData() {
    try {
        let url = "https://bcast.sundhagold.com:7768/VOTSBroadcastStreaming/Services/xml/GetLiveRateByTemplateID/sundhagold?_=" + Date.now();
        let res = await fetch(url);
        let text = await res.text();
        
        parseResponse(text);
        
        let spotSilverEl = document.getElementById('spot-silver');
        if (spotSilverEl) flash(spotSilverEl, silverSpot, 'spot-silver');
        
        let spotGoldEl = document.getElementById('spot-gold');
        if (spotGoldEl) flash(spotGoldEl, goldSpot, 'spot-gold');
        
        let spotUsdEl = document.getElementById('spot-usd');
        if (spotUsdEl) flash(spotUsdEl, usdinrSpot, 'spot-usd');
        
        renderTable(products, 'products-table', 'buy', 'sell');
        renderTable(quotes, 'quotes-table', 'bid', 'ask');
        
    } catch (e) {
        console.error("Failed to fetch market data", e);
    }
}

window.onload = () => {
    fetchConfig().then(() => {
        if(config.popup) alert(config.popup); // Simple popup for the web
        fetchMarketData(); 
        setInterval(fetchMarketData, 1000); 
        setInterval(fetchConfig, 5000); 
    });
};
