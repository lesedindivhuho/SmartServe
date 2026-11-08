# SmartServe – Cafeteria Management System

SmartServe is an Android app for ordering food from a campus cafeteria. Students sign in, browse the menu, build a cart, check out and track their orders. Cafeteria staff and managers use the same app to move orders through preparation and to manage the menu.

## 🎬 Demo video

**[▶ Click here for the demo video](assets/demo-video.mp4)**

The link opens the video on GitHub, where it can be watched in the browser or downloaded (use the **Download raw file** button at the top right of the file view).

---

## Features

### User roles

| Role | How they log in | What they can do |
|---|---|---|
| **Student** | Register with full name, student ID and password. Log in with student ID + password. | Browse menu, cart, checkout, receipt, track **their own** orders, edit profile |
| **Staff** | Demo account: `staff` / `staff123` | Everything a student can do, see **all** orders and update their status, Inventory screen |
| **Manager** | Demo account: `manager` / `manager123` | Everything staff can do, **add / edit / delete menu items**, Reports screen |

### Screens

- **Splash** – SmartServe logo while the app starts.
- **Login** – Student ID + password.
- **Register** – Full name, student ID and password, with password rules (at least 8 characters, an uppercase letter, a lowercase letter and a number). Duplicate student IDs are rejected.
- **Dashboard** – Welcome message and a slide-out side menu. Every user gets Menu, Cart, Orders and Profile; staff also get Inventory, and managers also get Reports and Manage Menu. Log out is at the bottom of the menu.
- **Menu** – Food items shown in a 2-column grid with pictures, descriptions and prices (R), with **Add to cart**.
- **Cart** – Items shown in a table (item, price, remove button) with the total. Checkout is greyed out while the cart is empty.
- **Checkout** – Enter name, student number and phone number to place the order.
- **Receipt** – Order number, customer details, the items ordered and the total.
- **Orders** – Students see their own orders. Staff and managers see every order and can mark it **Preparing** or **Completed**.
- **Profile** – View role, edit name and username, log out.
- **Manage Menu** (manager) – Menu items shown in a table with Edit and Delete buttons. Add and edit forms check each field (name, description, price) and show the error right on the field.
- **Reports** (manager) and **Inventory** (staff/manager) – placeholder screens for future work.

### Student ordering flow

1. Browse the **Menu** and add items to the cart.
2. Review the **Cart** and tap Checkout.
3. Enter details on **Checkout** and place the order (status **NEW**).
4. See the **Receipt**.
5. Track the order on **Orders** while staff move it to **PREPARING** and then **COMPLETED**.

---

## Tech stack

- **Language:** Kotlin, View Binding
- **Android:** minSdk 24 (Android 7.0), targetSdk 35
- **UI:** Material Components, RecyclerView, CardView, ConstraintLayout
- **API:** Node.js + Express (in the [`api/`](api) folder)

## Project structure

```
app/                     Android app
  src/main/java/.../
    Activities/          Screens (Login, Register, Dashboard, Menu, Cart, ...) + MenuManager, OrderManager
    models/              User, Food, UserRole
    utils/
      SessionManager     Logged-in user, demo staff/manager accounts, logout
      CartManager        Cart contents
      RecieptManger      Receipt helpers
api/                     SmartServe REST API (Node.js + Express)
assets/                  Demo video
```

## Data storage

| Data | Where it's stored |
|---|---|
| Student accounts and passwords | In the app's memory while it runs (reset when the app is closed) |
| Menu, cart, orders | In the app's memory while it runs (reset when the app is closed) |
| Staff and manager demo accounts | Built into the app (`SessionManager`) |

---

## SmartServe API

A very simple REST API that serves the cafeteria **menu** and handles **orders**. It uses the same menu items, prices and order statuses as the app, so it can act as the shared backend for the app or for a staff dashboard on a computer.

> The API is a standalone companion service. In this version, the Android app still keeps its menu and orders on the device, and the API is run and demonstrated separately.

### Run and test the API yourself

You don't need Android Studio or Firebase for this, just Node.js.

**1. Install Node.js**

Download the **LTS** version from [nodejs.org](https://nodejs.org/) (version 18 or newer) and install it. To check it worked, open a terminal (PowerShell on Windows, Terminal on Mac) and run:

```bash
node --version
```

**2. Get the code**

```bash
git clone https://github.com/lesedindivhuho/SmartServe.git
cd SmartServe/api
```

No Git? On the GitHub page click **Code → Download ZIP**, unzip it, and open a terminal inside the `api` folder.

**3. Install the API's packages** (first time only)

```bash
npm install
```

**4. Start the API**

```bash
npm start
```

You should see `SmartServe API running at http://localhost:3000`. Leave this terminal open while you test; press **Ctrl + C** to stop the API.

**5. Try it in your browser**

Click these while the API is running:

- <http://localhost:3000> – list of all endpoints
- <http://localhost:3000/api/health> – should say `"status": "ok"`
- <http://localhost:3000/api/menu> – the full menu
- <http://localhost:3000/api/menu/1> – just the Chicken Burger
- <http://localhost:3000/api/orders> – all orders (empty until you place one)

**6. Place an order and update its status**

Browsers can only *read* data, so placing and updating orders needs a command. Open a **second** terminal (keep the API running in the first) and paste the commands for your system.

*Windows (PowerShell):*

```powershell
# Place an order (2 Chicken Burgers + 1 Chips)
$order = Invoke-RestMethod -Method Post -Uri http://localhost:3000/api/orders -ContentType "application/json" -Body '{"customerName":"Lesedi","studentNumber":"ST10000001","phone":"0820000000","items":[{"foodId":1,"quantity":2},{"foodId":4,"quantity":1}]}'
$order

# Move it to PREPARING
Invoke-RestMethod -Method Patch -Uri "http://localhost:3000/api/orders/$($order.orderId)/status" -ContentType "application/json" -Body '{"status":"PREPARING"}'

# See all orders for that student
Invoke-RestMethod "http://localhost:3000/api/orders?studentNumber=ST10000001"
```

*Mac / Linux (or Git Bash on Windows):*

```bash
# Place an order (2 Chicken Burgers + 1 Chips)
curl -X POST http://localhost:3000/api/orders -H "Content-Type: application/json" -d '{"customerName":"Lesedi","studentNumber":"ST10000001","phone":"0820000000","items":[{"foodId":1,"quantity":2},{"foodId":4,"quantity":1}]}'

# Move it to PREPARING (use the orderId from the previous response)
curl -X PATCH http://localhost:3000/api/orders/ORD-000001/status -H "Content-Type: application/json" -d '{"status":"PREPARING"}'

# See all orders for that student
curl "http://localhost:3000/api/orders?studentNumber=ST10000001"
```

Prefer clicking to typing? Tools like [Postman](https://www.postman.com/) or the **Thunder Client** extension for VS Code can send the same requests. Use the method, URL and JSON body from the [examples below](#example-placing-an-order).

After placing an order, refresh <http://localhost:3000/api/orders> in your browser to see it.

**7. Run the automated tests (optional)**

```bash
npm test
```

This starts its own copy of the API, so the one from step 4 doesn't need to be running. All tests should show `ok`.

**Troubleshooting**

| Problem | Fix |
|---|---|
| `node` or `npm` is not recognized | Node.js isn't installed, or the terminal was open during install. Install it and open a new terminal. |
| `Cannot find module 'express'` | Run `npm install` inside the `api` folder first. |
| `EADDRINUSE` / port 3000 already in use | Something else is using port 3000. Start on another port: `$env:PORT=4000; npm start` (PowerShell) or `PORT=4000 npm start` (Mac/Linux), then use `localhost:4000` instead. |
| `curl` errors on Windows PowerShell | In PowerShell, `curl` isn't the real curl. Use the PowerShell commands above instead. |

### Endpoints

| Method | Endpoint | What it does |
|---|---|---|
| `GET` | `/api/health` | Checks the API is running |
| `GET` | `/api/menu` | Lists all menu items |
| `GET` | `/api/menu/:id` | Gets one menu item |
| `POST` | `/api/orders` | Places an order |
| `GET` | `/api/orders` | Lists all orders (add `?studentNumber=ST123` for one student's orders) |
| `GET` | `/api/orders/:orderId` | Gets one order |
| `PATCH` | `/api/orders/:orderId/status` | Updates an order's status: `NEW`, `PREPARING`, `READY` or `COMPLETED` |

### Example: placing an order

```http
POST /api/orders
Content-Type: application/json

{
  "customerName": "Lesedi",
  "studentNumber": "ST10000001",
  "phone": "0820000000",
  "items": [
    { "foodId": 1, "quantity": 2 },
    { "foodId": 4, "quantity": 1 }
  ]
}
```

Response (`201 Created`):

```json
{
  "orderId": "ORD-000001",
  "customerName": "Lesedi",
  "studentNumber": "ST10000001",
  "phone": "0820000000",
  "items": [
    { "foodId": 1, "name": "Chicken Burger", "price": 45, "quantity": 2 },
    { "foodId": 4, "name": "Chips", "price": 25, "quantity": 1 }
  ],
  "total": 115,
  "status": "NEW",
  "createdAt": "2026-10-19T19:00:00.000Z"
}
```

The **total is always calculated by the API from the menu prices**, so a client can't change what an order costs. Missing details, unknown food items, invalid quantities and invalid statuses return `400 Bad Request` with an error message. Unknown items or orders return `404 Not Found`.

### Example: updating an order's status

```http
PATCH /api/orders/ORD-000001/status
Content-Type: application/json

{ "status": "PREPARING" }
```

The API keeps data in memory, so orders reset when it restarts.

---

## Running the app

1. Open the project in **Android Studio** and run it on a device or emulator.
2. Build and run the `app` module on a device or emulator.

## Current limitations

- Menu changes, carts, orders and accounts in the app are stored in memory only and reset when the app closes.
- Reports and Inventory are placeholder screens.
- No Google Sign-In / Firebase in this build — Student ID + password only.
