const express = require('express');
const http = require('http');
const fs = require('fs');
const path = require('path');
const { Server } = require('socket.io');
const axios = require('axios');

const app = express();
const server = http.createServer(app);
const io = new Server(server, {
  cors: { origin: "*", methods: ["GET", "POST"] }
});

// Middleware to parse JSON bodies
app.use(express.json());

// Serve static HTML, CSS, and JS files from the current directory
app.use(express.static(__dirname));

const API_DIR = path.join(__dirname, 'api');
const CONFIG_FILE = path.join(API_DIR, 'config.json');

// Ensure the api directory and config.json exist
if (!fs.existsSync(API_DIR)) {
    fs.mkdirSync(API_DIR);
}
if (!fs.existsSync(CONFIG_FILE)) {
    fs.writeFileSync(CONFIG_FILE, JSON.stringify({"products":{},"marquee":"","popup":"","banks":[]}, null, 2));
}

// API endpoint to GET configuration
app.get(['/api/get_config', '/api/get_config.php'], (req, res) => {
    fs.readFile(CONFIG_FILE, 'utf8', (err, data) => {
        if (err) {
            return res.json({"products":{},"marquee":"","popup":"","banks":[]});
        }
        try {
            res.json(JSON.parse(data));
        } catch (e) {
            res.json({"products":{},"marquee":"","popup":"","banks":[]});
        }
    });
});

// API endpoint to SAVE configuration
app.post(['/api/save_config', '/api/save_config.php'], (req, res) => {
    fs.writeFile(CONFIG_FILE, JSON.stringify(req.body, null, 2), (err) => {
        if (err) {
            console.error("Failed to save:", err);
            return res.status(500).json({status: "error", message: "Failed to save configuration"});
        }
        res.json({status: "success"});
    });
});

// --- Socket.IO Polling Logic ---
let latestMarketData = "";

async function fetchSundhaGold() {
    try {
        const url = "https://bcast.sundhagold.com:7768/VOTSBroadcastStreaming/Services/xml/GetLiveRateByTemplateID/sundhagold?_=" + Date.now();
        const response = await axios.get(url);
        latestMarketData = response.data;
        io.emit('market-data', latestMarketData);
    } catch (error) {
        console.error("Error fetching Sundha Gold:", error.message);
    }
}

setInterval(fetchSundhaGold, 1000);

io.on('connection', (socket) => {
    if (latestMarketData) {
        socket.emit('market-data', latestMarketData);
    }
});

// Proxy endpoint for Android App
app.get('/api/live-rates', (req, res) => {
    res.send(latestMarketData);
});

// Start the server
const PORT = process.env.PORT || 8080;
server.listen(PORT, () => {
    console.log(`Swastik Gold Server is running on port ${PORT}`);
});
