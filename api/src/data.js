// We didn't set up a real database for the API. Everything is just stored in
// memory while it runs, so if you restart it all the orders are gone. We kept it
// this way so anyone can run it without having to install a database first.

// Same menu as the app (it's copied from MenuManager.kt) so the two match up.
// Prices are in Rand.
const seedMenu = [
  { id: 1, name: "Chicken Burger", description: "Grilled chicken burger with lettuce and sauce", price: 45.0 },
  { id: 2, name: "Pizza", description: "Cheesy pizza with tomato and mixed toppings", price: 65.0 },
  { id: 3, name: "Chicken Meal", description: "Grilled chicken served with chips", price: 55.0 },
  { id: 4, name: "Chips", description: "Crispy golden potato chips", price: 25.0 },
  { id: 5, name: "Sandwich", description: "Fresh sandwich with chicken, lettuce and cheese", price: 35.0 },
];

// The statuses an order goes through: it gets placed (NEW), the kitchen starts on
// it (PREPARING), it's ready to collect (READY), then it's collected (COMPLETED).
// If someone tries to set a status that isn't in here, the API says no.
const ORDER_STATUSES = ["NEW", "PREPARING", "READY", "COMPLETED"];

// Gives you a fresh, empty "store" to keep the menu and orders in.
// nextOrderNumber is just a counter we use to give each order its own ID.
// We copy the menu instead of using seedMenu directly so that each copy of the
// API (like the one the tests start) has its own menu and they don't mess with
// each other.
function createStore() {
  return {
    menu: seedMenu.map((item) => ({ ...item })),
    orders: [],
    nextOrderNumber: 1,
  };
}

module.exports = { createStore, ORDER_STATUSES };
