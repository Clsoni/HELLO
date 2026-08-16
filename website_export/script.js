// script.js - Simulated Live Rates

let products = [
    { id: '1', name: 'GOLD 999 1 KG', buy: 71350, sell: 71400, buyHigh: 71500, buyLow: 71200 },
    { id: '2', name: 'GOLD 999 100 GM', buy: 71380, sell: 71430, buyHigh: 71530, buyLow: 71230 },
    { id: '3', name: 'SILVER 999 30 KG', buy: 80250, sell: 80350, buyHigh: 80500, buyLow: 80000 }
];

let quotes = [
    { symbol: 'GOLD MCX', bid: 71350, ask: 71355, high: 71500, low: 71200 },
    { symbol: 'SILVER MCX', bid: 80250, ask: 80260, high: 80500, low: 80000 },
    { symbol: 'GOLD COMEX', bid: 2350.50, ask: 2351.00, high: 2360.0, low: 2340.0 }
];

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

        tbody.innerHTML += `
            <tr>
                <td>
                    ${name}
                    <span class="high-low">H: ${high} &nbsp; L: ${low}</span>
                </td>
                <td><div class="price-box" id="${item.id || item.symbol}-buy">${buy}</div></td>
                <td><div class="price-box" id="${item.id || item.symbol}-sell">${sell}</div></td>
            </tr>
        `;
    });
}

function simulateMarket() {
    setInterval(() => {
        let spotGold = document.getElementById('spot-gold');
        if(spotGold) {
            let cur = parseInt(spotGold.innerText);
            let next = cur + (Math.random() > 0.5 ? 10 : -10);
            spotGold.innerText = next;
            flash(spotGold, next > cur);
        }

        products.forEach(p => {
            let oldBuy = p.buy;
            p.buy += Math.random() > 0.5 ? 5 : -5;
            p.sell += Math.random() > 0.5 ? 5 : -5;
            
            let buyEl = document.getElementById(p.id + '-buy');
            let sellEl = document.getElementById(p.id + '-sell');
            if (buyEl) { buyEl.innerText = p.buy; flash(buyEl, p.buy > oldBuy); }
            if (sellEl) { sellEl.innerText = p.sell; flash(sellEl, p.sell > oldBuy); }
        });
        
    }, 2000);
}

function flash(element, isUp) {
    element.className = 'price-box ' + (isUp ? 'flash-up' : 'flash-down');
    setTimeout(() => {
        element.className = 'price-box';
    }, 500);
}

window.onload = () => {
    renderTable(products, 'products-table', 'buy', 'sell');
    renderTable(quotes, 'quotes-table', 'bid', 'ask');
    simulateMarket();
};
