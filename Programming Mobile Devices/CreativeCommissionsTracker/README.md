# Assignment 2 - Creative Commissions Tracker

**Student Name:** Ice Ybañez\
**Student Number:** R00176611\
**Student Group:** SDH3-B


## App Overview

"Creative Commissions Tracker" is for artists, to find new art, new artists and manage their commissions from their clients.

It combines a curated art Home screen with a simple client & order tracker, using Firebase as a remote database.


## Main Features

### Home Screen (for artist/art curator)
- Fetches **featured artworks** from Firebase Firestore (``artworks`` collection), showing a randomized subset with images using Coil.
- Fetches **featured artists** from Firestore (``artists`` collection), showing a randomized subset with their names and Instagram handles.
- **Filters** for:
    - **Art type:** Contemporary, Custom Art, Portraits.
    - **Medium:** Acrylic, Oil.
    - **Style:** Abstract, Pop, Photorealistic.
    - **Price ranges:** Under €200, Under €500, Under €1000.
- **Dynamic MaterialTheme**:
    - Contemporary - minimal black & white.
    - Custom Art - bright and colourful.
    - Portraits - warm, traditional palette.

### Clients & Orders (Commission Tracker)
- **Clients screen**
    - Lists clients from Firestore (``clients`` collection)
    - “Add” dialog to create new clients (name, social handle, notes)
- **Orders screen**
    - Lists all orders from Firestore (``orders`` collection)
    - Shows client name, art type, medium, style, price, due date, status, and paid/unpaid flag
    - Actions: **Mark Done** and **Mark Paid** (updates Firestore)
- **Add Order screen**
    - Form to create a new order:
        - Title, description
        - Art type, medium, style (chips)
        - Price (€)
        - Due date (dd/MM/yyyy)
        - Client selection (from existing clients)
    - Saves the order to Firestore.
- **Order Details screen**
    - Shows full details: title, description, client name, art type, medium, style, price, due date, status, paid/unpaid
    - Buttons to **Mark Done** and **Mark Paid**, updating Firestore and the UI

### Notifications & Service Logic
- **Notifications toggle** on Orders screen:
    - Uses ``POST_NOTIFICATIONS`` permission
    - Saves on/off state in ``SharedPreferences``
- When creating an order with a due date:
    - Schedules a **WorkManager** job (``DueDateNotificationWorker`` via ``NotificationHelper``) to show a “Order due soon” notification before the due date
    - Notification channel is created via ``NotificationHelper``

### Navigation & Animations
- **Bottom navigation bar** with three tabs: Home, Clients, Orders.
- Separate **Add Order** screen opened from the Orders screen.
- Simple **screen transition animations** using ``AnimatedVisibility``:
    - Content fades and slides slightly when screens appear.


## Running the App

- Open the project in Android Studio
- Ensure ``google-services.json`` is present and Firebase is configured (Firestore enabled)
- Sync Gradle and build project
- Run on an Android emulator (Device API 36.0)

