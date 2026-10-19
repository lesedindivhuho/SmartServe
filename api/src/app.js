// This is where all the API's routes live.
//
// We used Express to build it. If you haven't used Express before, every route
// looks something like app.get("/some/path", (req, res) => { ... }).
// "req" is whatever the caller sent us (URL bits, query strings, the JSON body)
// and "res" is what we use to send something back.
//
// Quick note on the status codes you'll see: 200 means it worked, 201 means we
// created a new order, 400 means the caller sent something wrong, and 404 means
// whatever they asked for doesn't exist.

const express = require("express");
const { createStore, ORDER_STATUSES } = require("./data");

// Builds the app but doesn't start it. server.js is the one that actually
// starts it. We split it like this so the tests can spin up their own copy.
function createApp(store = createStore()) {
  const app = express();

  // Without this, req.body would just be undefined when someone sends us JSON.
  app.use(express.json());

  // If you open the API in your browser, this is the first thing you'll see.
  // It just lists what routes are available.
  app.get("/", (req, res) => {
    res.json({
      name: "SmartServe API",
      endpoints: [
        "GET    /api/health",
        "GET    /api/menu",
        "GET    /api/menu/:id",
        "POST   /api/orders",
        "GET    /api/orders?studentNumber=...",
        "GET    /api/orders/:orderId",
        "PATCH  /api/orders/:orderId/status",
      ],
    });
  });

  // Handy for checking the API is actually up and running.
  app.get("/api/health", (req, res) => {
    res.json({ status: "ok", time: new Date().toISOString() });
  });

  // ----- Menu -----

  // Sends back the whole menu.
  app.get("/api/menu", (req, res) => {
    res.json(store.menu);
  });

  // Gets one item by its id, so /api/menu/1 gives you the Chicken Burger.
  app.get("/api/menu/:id", (req, res) => {
    // The id comes through as text ("1") so we turn it into a number first,
    // otherwise it'll never match.
    const item = store.menu.find((food) => food.id === Number(req.params.id));
    if (!item) return res.status(404).json({ error: "Menu item not found" });
    res.json(item);
  });

  // ----- Orders -----

  // This is where orders get placed. The caller needs to send something like:
  //
  //   {
  //     "customerName": "Lesedi",
  //     "studentNumber": "ST10000001",
  //     "phone": "0820000000",
  //     "items": [{ "foodId": 1, "quantity": 2 }, { "foodId": 4 }]
  //   }
  //
  // If you leave out the quantity we just assume they want one.
  app.post("/api/orders", (req, res) => {
    // The "?? {}" is there so it doesn't crash if someone sends no body at all.
    const { customerName, studentNumber, phone, items } = req.body ?? {};

    // Make sure we actually know who's ordering.
    if (!isFilled(customerName) || !isFilled(studentNumber) || !isFilled(phone)) {
      return res.status(400).json({ error: "customerName, studentNumber and phone are required" });
    }

    // And that they're ordering at least something.
    if (!Array.isArray(items) || items.length === 0) {
      return res.status(400).json({ error: "items must be a non-empty list of { foodId, quantity }" });
    }

    // Go through each item, look it up on our menu, and build the order lines.
    // If even one item is wrong we reject the whole order.
    const lines = [];
    for (const { foodId, quantity = 1 } of items) {
      const food = store.menu.find((item) => item.id === Number(foodId));
      if (!food) {
        return res.status(400).json({ error: `Unknown foodId: ${foodId}` });
      }
      if (!Number.isInteger(quantity) || quantity < 1) {
        return res.status(400).json({ error: `Invalid quantity for foodId ${foodId}` });
      }
      lines.push({ foodId: food.id, name: food.name, price: food.price, quantity });
    }

    // We work the total out ourselves using our own menu prices. We don't trust
    // any price the caller sends, otherwise someone could just make up a cheaper one.
    const total = lines.reduce((sum, line) => sum + line.price * line.quantity, 0);

    // Order IDs look like ORD-000001, ORD-000002 and so on, similar to the ones
    // in the app. Every order starts off as NEW.
    const order = {
      orderId: `ORD-${String(store.nextOrderNumber++).padStart(6, "0")}`,
      customerName: customerName.trim(),
      studentNumber: studentNumber.trim(),
      phone: phone.trim(),
      items: lines,
      total,
      status: "NEW",
      createdAt: new Date().toISOString(),
    };

    store.orders.push(order);
    res.status(201).json(order);
  });

  // Lists every order. You can add ?studentNumber=... to only get one
  // student's orders, which is basically what the Orders screen in the app does.
  app.get("/api/orders", (req, res) => {
    const { studentNumber } = req.query;
    const orders = studentNumber
      ? store.orders.filter((order) => order.studentNumber === studentNumber)
      : store.orders;
    res.json(orders);
  });

  // Gets a single order, e.g. /api/orders/ORD-000001
  app.get("/api/orders/:orderId", (req, res) => {
    const order = findOrder(store, req.params.orderId);
    if (!order) return res.status(404).json({ error: "Order not found" });
    res.json(order);
  });

  // Lets staff update where an order is at. Send { "status": "PREPARING" } or
  // whichever status you want. The allowed ones are in data.js.
  app.patch("/api/orders/:orderId/status", (req, res) => {
    const order = findOrder(store, req.params.orderId);
    if (!order) return res.status(404).json({ error: "Order not found" });

    // We uppercase it so "preparing" works just as well as "PREPARING".
    const status = String(req.body?.status ?? "").toUpperCase();
    if (!ORDER_STATUSES.includes(status)) {
      return res.status(400).json({ error: `status must be one of: ${ORDER_STATUSES.join(", ")}` });
    }

    order.status = status;
    res.json(order);
  });

  // Anything that didn't match a route above ends up here. Keep this one at the
  // bottom, otherwise it'll catch requests before the real routes get a chance.
  app.use((req, res) => {
    res.status(404).json({ error: "Route not found" });
  });

  return app;
}

// Checks that something is actually text and not just empty or spaces.
function isFilled(value) {
  return typeof value === "string" && value.trim().length > 0;
}

// Looks up an order by its ID. Gives back undefined if it can't find it.
function findOrder(store, orderId) {
  return store.orders.find((order) => order.orderId === orderId);
}

module.exports = { createApp };
