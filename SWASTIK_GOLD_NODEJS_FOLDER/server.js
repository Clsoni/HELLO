const express = require('express');
const fs = require('fs');
const path = require('path');
const app = express();

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
// Supporting both the clean route and the old .php route just in case
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

// Start the server (GoDaddy will inject the port via process.env.PORT)
const PORT = process.env.PORT || 8080;
app.listen(PORT, () => {
    console.log(`Swastik Gold Server is running on port ${PORT}`);
});
