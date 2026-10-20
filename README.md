# StepPay - Walk, Run & Earn Real Rewards

[![Android Studio](https://img.shields.io/badge/Android%20Studio-2024.2+-brightgreen.svg)](https://developer.android.com/studio)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-blue.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material3-purple.svg)](https://developer.android.com/jetpack/compose)
[![Firebase](https://img.shields.io/badge/Firebase-Firestore%20%26%20Auth-orange.svg)](https://firebase.google.com)
[![Room DB](https://img.shields.io/badge/Room%20DB-2.6.1-blueviolet.svg)](https://developer.android.com/training/data-storage/room)

StepPay is a modern, full-featured Android application built with Kotlin and Jetpack Compose. It accurately tracks daily physical steps and GPS workout sessions, turning healthy physical activity into virtual StepPay Coins that users can redeem for real PayPal cash rewards.

---

## Features

- Accurate Step Counter & GPS Activity Tracking  
  Uses hardware pedometer sensors (`Sensor.TYPE_STEP_COUNTER`) for automatic daily step logging and `FusedLocationProviderClient` for GPS route workout tracking (walking, running, distance, speed, and calories).

- Virtual Sneakers & Earning Boosts  
  Upgrade shoe levels to multiply your coin earning rate per step.

- Daily Water Intake Tracker  
  Log daily hydration goals and unlock extra bonus coin rewards.

- Progressive Step Goals & Milestone Rewards  
  Step milestones (from 50 up to 10,000+ steps) with coin rewards and optional 2x video boosts.

- Lucky Spin & Bonus Center  
  Daily wheel spin, daily login check-in streaks, offerwall integration (Tapjoy & BitLabs), and reward video bonuses.

- Live Global Leaderboard  
  Real-time global rankings powered by Firebase Firestore.

- Referral & Invite System  
  Unique referral codes for inviting friends to earn bonus coins.

- PayPal Cash Out  
  Flexible payout tiers starting from $1.00 USD (100,000 StepPay Coins) directly to PayPal.

- Offline-First & Cloud Data Sync  
  Local-first data persistence with Room Database, seamlessly synced with Firebase Firestore in real-time.

- Anti-Fraud Security System  
  7-day daily login verification streak to ensure fair play and prevent automated bot fraud.

---

## Tech Stack & Architecture

- UI Framework: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material Design 3
- Language: Kotlin 100%
- Architecture: Clean Architecture + MVVM + Repository Pattern
- Local Database: [Room Database](https://developer.android.com/training/data-storage/room)
- Cloud & Backend: Firebase Authentication & Cloud Firestore
- State Management: Kotlin Coroutines & `StateFlow`
- Location Services: Google Play Services Location (`FusedLocationProviderClient`)
- JSON Parsing: Gson
- Image Loading: Coil

---

## Project Structure

```text
com.steppay.app/
├── data/
│   ├── firestore/       # Firebase Firestore Real-Time Sync Manager
│   ├── local/           # Room Database, DAOs, and Entities
│   └── repository/      # StepPay Repository layer
├── ui/
│   ├── components/      # Reusable Compose UI Components
│   ├── navigation/      # Compose Navigation graph & screen destinations
│   ├── screens/         # UI Screens (Home, Activity, Sneakers, Bonus, Leaderboard, Withdrawal, etc.)
│   ├── theme/           # Color Palette, Typography, and Shapes
│   └── StepPayViewModel.kt # Main ViewModel handling state & UI flows
├── util/                # Location, Pedometer, and Helper utilities
└── MainActivity.kt      # Application entry point
```

---

## Getting Started

### Prerequisites

1. Android Studio Ladybug (2024.2.1) or newer.
2. JDK 17 configured in Android Studio.
3. Android device or emulator running Android 8.0 (API level 26) or higher with Google Play Services.

### Setup Instructions

1. Clone the Repository:
   ```bash
   git clone https://github.com/YOUR_USERNAME/StepPay.git
   cd StepPay
   ```

2. Add Firebase Configuration:
   - Download your `google-services.json` from the Firebase Console.
   - Place `google-services.json` inside the `app/` directory: `app/google-services.json`.

3. Build the Project:
   - Open the project in Android Studio.
   - Sync Gradle files and run the project on your device or emulator:
     ```bash
     ./gradlew assembleDebug
     ```

---

## Firestore Security Rules

Ensure your Firestore database is configured with the following security rules:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    function isAuthenticated() {
      return request.auth != null;
    }

    match /users/{userId} {
      allow read, write: if isAuthenticated();
    }
    match /leaderboard/{userId} {
      allow read, write: if isAuthenticated();
    }
    match /coin_transactions/{txId} {
      allow read, create, update, delete: if isAuthenticated();
    }
    match /withdrawals/{withdrawalId} {
      allow read, create, update, delete: if isAuthenticated();
    }
    match /activity_sessions/{sessionId} {
      allow read, create, update, delete: if isAuthenticated();
    }
    match /app_config/settings {
      allow read: if true;
      allow write: if false;
    }
  }
}
```

---

## License

This project is licensed under the MIT License - see the LICENSE file for details.
