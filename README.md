# Clear & Glow - Skincare Routine App

**Clear & Glow** is a skincare management app designed to help users create, track, and optimize their skincare routines. Users can add products, detect ingredient conflicts, share routines, and discover skincare tips for a healthier glow!

---

## Features

✅ **Personalized Routines:** Organize morning & evening skincare routines.
✅ **Community-Driven:** Discover and share skincare routines with other users.
✅ **Product Management:** Add products from a global database or your personal collection.
✅ **Profile Management:** Upload and update profile pictures via Firebase.
✅ **Firebase Authentication:** Secure sign-in via Email/Password or Google.
✅ **Offline Support:** Access your saved routines anytime, even without an internet connection.

---

## Tech Stack

- **Language:** Kotlin
- **Architecture:** MVVM (Model-View-ViewModel)
- **UI:** XML, Material Design
- **Database:** Firebase Firestore
- **Storage:** Firebase Storage (Profile and Product Images)
- **Authentication:** Firebase Auth (Email/Password & Google Sign-In)
- **Dependency Injection:** ViewModel & LiveData
- **Other:** Glide for image loading, RecyclerView for displaying lists

---

## Installation & Setup

### Prerequisites
- Android Studio (Latest version)
- Firebase Project (Configured with Authentication, Firestore, and Storage)
- A real device or Android Emulator

### Steps
1. Clone this repository:
   ```bash
   git clone https://github.com/your-username/clear-and-glow.git
   ```
2. Open the project in **Android Studio**.
3. Add your **google-services.json** file (Firebase Configuration) inside `app/`.
4. Sync Gradle and build the project.
5. Run the app on an emulator or physical device.

---

## Usage Guide

1. **Sign Up & Login:** Create an account using Email/Password or Google Sign-In.
2. **Add Products:** Choose from global products.
3. **Create Routines:** Organize your skincare products into morning & evening routines.
4. **Share & Discover:** Post your routine to the feed and explore others'.
5. **Profile Management:** Upload profile pictures and manage preferences.

---

⭐ **Star this repo if you found it useful!**
