# 📱 DailyPulse

DailyPulse is a **Habit Tracking and Social Motivation App** built with **Kotlin Multiplatform (KMP)** using **Jetpack Compose Multiplatform**, targeting Android (with planned support for iOS).

The app encourages users to build healthy routines, share progress with friends, and stay motivated through beautiful UI and responsive animations.

---

## Features

- Habit management: Create, edit, and track personal goals.
- Social posts: Share progress with friends and like each other’s updates.
- Firebase authentication for secure sign-in/sign-up.
- Optional image uploads for posts.
- Polished and responsive UI with consistent theming.

---

## Project Structure

This is a **Kotlin Multiplatform (KMP)** project. The main module layout is:

- `commonMain` holds all platform-agnostic code.
- `androidMain` / `iosMain` allow using platform-specific APIs when necessary.
- `composeApp` is our main module, where we develop our shared UI using **Jetpack Compose Multiplatform**.

---

## Tech Stack

- **Kotlin Multiplatform**
- **Jetpack Compose Multiplatform**
- **Material 3 (M3) Design System**
- **Firebase Authentication**
- **Koin (DI)**
- **Coil** for image loading
- **Cloudinary** for image uploads (optional)
- **StateFlow + ViewModels** for reactive state management

---

## Getting Started

1. Clone the repository:
   git clone https://github.com/RotemArdani/DailyPulse.git
2. Open the project in Android Studio Hedgehog or later.

3. Run the Android app: Select a device/emulator and click Run.

4. To build for iOS (future support):

- Use Xcode with the iosApp module as the host.

- Connect the composeApp shared logic.


## Design
The app's design is available on DRIVE: [View here](https://drive.google.com/file/d/1smp1zKe6_BhxUputIAwQAU6BlzdE1xJp/view?usp=sharing)  
And on Figma: [View here](https://www.figma.com/board/0kVkL4V89N8KwLKAig0GKn/FigJam-basics?node-id=0-1&t=hPdegU0FgXzdBmc6-1 )
