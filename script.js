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
        if (element.flashTimeout) clearTimeout(element.flashTimeout);
        
        if (previousValues[key] !== undefined && nextValue !== '-' && previousValues[key] !== '-') {
            let isUp = Number(nextValue) > Number(previousValues[key]);
            element.className = 'price-box ' + (isUp ? 'flash-up' : 'flash-down');
            
            element.flashTimeout = setTimeout(() => {
                element.className = 'price-box';
            }, 500);
        } else {
            element.className = 'price-box';
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
    
    let needsFullRender = false;
    if (tbody.children.length !== data.length) {
        needsFullRender = true;
    } else {
        for (let i = 0; i < data.length; i++) {
            if (tbody.children[i].dataset.original !== data[i].original) {
                needsFullRender = true;
                break;
            }
        }
    }

    if (needsFullRender) {
        tbody.innerHTML = '';
        data.forEach(item => {
            const name = item.name || item.symbol;
            let tr = document.createElement('tr');
            tr.dataset.original = item.original;
            tr.innerHTML = `
                <td>
                    ${name}
                    <span class="high-low" id="${item.original}-hl"></span>
                </td>
                <td><div class="price-box" id="${item.original}-buy"></div></td>
                <td><div class="price-box" id="${item.original}-sell"></div></td>
            `;
            tbody.appendChild(tr);
        });
    }

    data.forEach(item => {
        const buy = item[buyKey];
        const sell = item[sellKey];
        const high = item.buyHigh || item.high;
        const low = item.buyLow || item.low;
        
        let decimals = 0;
        let itemName = (item.name || item.symbol || "").toUpperCase();
        if (itemName.includes("USDINR")) decimals = 3;
        else if (itemName.includes("COMEX") || itemName === "GOLD" || itemName === "SILVER") decimals = 2;
        
        let buyStr = (buy === 0 || isNaN(buy) || item.hideBuy) ? '-' : buy.toFixed(decimals);
        let sellStr = (sell === 0 || isNaN(sell) || item.hideSell) ? '-' : sell.toFixed(decimals);
        let highStr = (high === 0 || isNaN(high)) ? '-' : high.toFixed(decimals);
        let lowStr = (low === 0 || isNaN(low)) ? '-' : low.toFixed(decimals);

        let hlEl = document.getElementById(`${item.original}-hl`);
        if (hlEl) hlEl.innerHTML = `H: ${highStr} &nbsp; L: ${lowStr}`;
        
        let buyEl = document.getElementById(`${item.original}-buy`);
        if (buyEl) flash(buyEl, buyStr, `${item.original}-buy`);
        
        let sellEl = document.getElementById(`${item.original}-sell`);
        if (sellEl) flash(sellEl, sellStr, `${item.original}-sell`);
    });
}

// Socket.IO logic replaces manual fetchMarketData
const socket = io();

socket.on('connect', () => {
    console.log("Connected to Swastik Gold Live Server");
});

socket.on('market-data', (text) => {
    try {
        parseResponse(text);
        
        let spotSilverEl = document.getElementById('spot-silver');
        if (spotSilverEl) flash(spotSilverEl, silverSpot === 0 ? '-' : silverSpot.toFixed(2), 'spot-silver');
        
        let spotGoldEl = document.getElementById('spot-gold');
        if (spotGoldEl) flash(spotGoldEl, goldSpot === 0 ? '-' : goldSpot.toFixed(2), 'spot-gold');
        
        let spotUsdEl = document.getElementById('spot-usd');
        if (spotUsdEl) flash(spotUsdEl, usdinrSpot === 0 ? '-' : usdinrSpot.toFixed(3), 'spot-usd');
        
        renderTable(products, 'products-table', 'buy', 'sell');
        renderTable(quotes, 'quotes-table', 'bid', 'ask');
    } catch (e) {
        console.error("Error processing market data:", e);
    }
});

window.onload = () => {
    fetchConfig().then(() => {
        if(config.popup) alert(config.popup); // Simple popup for the web
        setInterval(fetchConfig, 5000); 
    });
};
