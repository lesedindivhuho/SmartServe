// Tests for the API. Run them with "npm test".
//
// Each test sends a real request to the API, just like the app or a browser
// would, and checks we get the right answer back. So if someone changes
// something and breaks a route, one of these will fail and you'll know which.
//
// We're using the test runner that comes with Node, so there's nothing extra
// to install.

const { test, before, after } = require("node:test");
const assert = require("node:assert/strict");
const { createApp } = require("../src/app");

let server;
let baseUrl;

// Start up our own copy of the API before the tests run. Passing 0 as the port
// lets the computer pick any free one, so this still works even if you've got
// the normal API running on 3000 at the same time.
before(async () => {
  server = createApp().listen(0);
  await new Promise((resolve) => server.once("listening", resolve));
  baseUrl = `http://localhost:${server.address().port}`;
});

// Shut it down again once we're done.
after(() => server.close());

// Little helper so the tests don't have to repeat the same fetch code every
// time. Gives back the status code and the JSON we got back.
async function call(method, path, body) {
  const response = await fetch(baseUrl + path, {
    method,
    headers: body ? { "Content-Type": "application/json" } : undefined,
    body: body ? JSON.stringify(body) : undefined,
  });
  return { status: response.status, body: await response.json() };
}

test("health check responds ok", async () => {
  const { status, body } = await call("GET", "/api/health");
  assert.equal(status, 200);
  assert.equal(body.status, "ok");
});

test("menu lists the five seeded items", async () => {
  const { status, body } = await call("GET", "/api/menu");
  assert.equal(status, 200);
  assert.equal(body.length, 5);
  assert.equal(body[0].name, "Chicken Burger");
});

test("unknown menu item returns 404", async () => {
  const { status } = await call("GET", "/api/menu/999");
  assert.equal(status, 404);
});

test("placing an order calculates the total from menu prices", async () => {
  // Two Chicken Burgers at R45 and one Chips at R25. We left the quantity off
  // the chips on purpose to check it defaults to 1.
  const { status, body } = await call("POST", "/api/orders", {
    customerName: "Lesedi",
    studentNumber: "ST10000001",
    phone: "0820000000",
    items: [
      { foodId: 1, quantity: 2 },
      { foodId: 4 },
    ],
  });

  assert.equal(status, 201);
  assert.match(body.orderId, /^ORD-\d{6}$/);
  assert.equal(body.status, "NEW");
  assert.equal(body.total, 45 * 2 + 25); // should come to R115
});

test("orders can be filtered by student number and moved through statuses", async () => {
  const { body: created } = await call("POST", "/api/orders", {
    customerName: "Thabo",
    studentNumber: "ST10000002",
    phone: "0830000000",
    items: [{ foodId: 2 }],
  });

  // The other tests placed orders too, so we're checking we only get Thabo's back.
  const { body: mine } = await call("GET", "/api/orders?studentNumber=ST10000002");
  assert.deepEqual(mine.map((order) => order.orderId), [created.orderId]);

  // Sending it in lowercase on purpose, it should still save as PREPARING.
  const { status, body: updated } = await call(
    "PATCH",
    `/api/orders/${created.orderId}/status`,
    { status: "preparing" }
  );
  assert.equal(status, 200);
  assert.equal(updated.status, "PREPARING");
});

test("invalid orders and statuses are rejected", async () => {
  // No name, student number or phone, so this shouldn't go through.
  const missingFields = await call("POST", "/api/orders", { items: [{ foodId: 1 }] });
  assert.equal(missingFields.status, 400);

  // There's no food item 42 on the menu.
  const unknownFood = await call("POST", "/api/orders", {
    customerName: "A",
    studentNumber: "B",
    phone: "C",
    items: [{ foodId: 42 }],
  });
  assert.equal(unknownFood.status, 400);

  // "EATEN" isn't a real status, so the API should refuse it.
  const { body: order } = await call("POST", "/api/orders", {
    customerName: "A",
    studentNumber: "B",
    phone: "C",
    items: [{ foodId: 3 }],
  });
  const badStatus = await call("PATCH", `/api/orders/${order.orderId}/status`, { status: "EATEN" });
  assert.equal(badStatus.status, 400);
});
