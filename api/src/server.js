// This is what runs when you type "npm start". It doesn't do much, it just
// starts the API up. All the actual routes are in app.js.

const { createApp } = require("./app");

// Runs on port 3000 unless you tell it otherwise, e.g. "PORT=4000 npm start".
// Useful if something else on your computer is already using 3000.
const port = Number(process.env.PORT) || 3000;

createApp().listen(port, () => {
  console.log(`SmartServe API running at http://localhost:${port}`);
});
