# Shaale-Vikas

Shaale-Vikas is a Kotlin + Jetpack Compose Android app that helps rural schools publish infrastructure needs and enables alumni to pledge support with live progress tracking.

## Stack

- Kotlin
- Jetpack Compose + Material 3
- MVVM + repository-driven architecture
- Firebase Authentication
- Firebase Firestore with offline persistence
- Firebase Storage for image uploads
- Hilt for dependency injection
- Coil for image loading

## Project Structure

- `app/src/main/java/com/shaalevikas/ui/` - Compose screens, components, navigation, theme
- `app/src/main/java/com/shaalevikas/viewmodel/` - screen state and business coordination
- `app/src/main/java/com/shaalevikas/repository/` - repository interfaces and Firebase-backed implementations
- `app/src/main/java/com/shaalevikas/data/` - models and remote collection constants
- `app/src/main/java/com/shaalevikas/di/` - Hilt modules
- `app/src/main/java/com/shaalevikas/utils/` - shared state wrappers and formatting helpers

## Features Included

- Email/password signup and login
- Persistent Firebase session
- Role-aware UX for `Admin` and `Alumni`
- Live needs dashboard with search and sort
- Need details with progress bar, images, and pledge feed
- Simulated pledge workflow with live Firestore updates
- Admin create, edit, and delete flows
- Firebase Storage image upload support
- Donor hall of fame leaderboard
- Dark mode-ready Material 3 theme
- Basic offline caching through Firestore persistence

## Android Studio Setup

1. Open Android Studio Hedgehog or newer.
2. Choose `Open` and select:
   `C:\Users\harsh\Documents\Codex\2026-05-05-files-mentioned-by-the-user-whatsapp`
3. Let Android Studio install the Android SDK for API 35 if prompted.
4. Use JDK 17 in Android Studio:
   `File > Settings > Build, Execution, Deployment > Build Tools > Gradle`
5. If Android Studio asks for a Gradle wrapper, allow it to generate or sync one automatically.

## Firebase Setup

1. Create a Firebase project in the [Firebase Console](https://console.firebase.google.com/).
2. Add an Android app with package name:
   `com.shaalevikas`
3. Download `google-services.json`.
4. Place it here:
   [google-services.json](C:/Users/harsh/Documents/Codex/2026-05-05-files-mentioned-by-the-user-whatsapp/app/google-services.json)
5. In Firebase Console, enable:
   - Authentication > Email/Password
   - Firestore Database
   - Storage
6. Apply the included rules:
   - [firestore.rules](/C:/Users/harsh/Documents/Codex/2026-05-05-files-mentioned-by-the-user-whatsapp/firestore.rules)
   - [storage.rules](/C:/Users/harsh/Documents/Codex/2026-05-05-files-mentioned-by-the-user-whatsapp/storage.rules)
7. Add the included Firestore composite index:
   - [firestore.indexes.json](/C:/Users/harsh/Documents/Codex/2026-05-05-files-mentioned-by-the-user-whatsapp/firestore.indexes.json)

## Firestore Data Model

### `users`

- `id`
- `name`
- `email`
- `role`
- `photoUrl`
- `createdAt`

### `needs`

- `id`
- `title`
- `description`
- `location`
- `category`
- `priority`
- `estimatedCost`
- `amountCollected`
- `heroImageUrl`
- `beforeImageUrl`
- `afterImageUrl`
- `status`
- `createdBy`
- `createdAt`
- `updatedAt`

### `pledges`

- `id`
- `needId`
- `userId`
- `donorName`
- `donorEmail`
- `amount`
- `note`
- `pledgedAt`

## How To Run

1. Add `google-services.json` to the `app/` folder.
2. Sync the project in Android Studio.
3. Create a Firestore database in production or test mode.
4. Publish the rules and index files from this repo.
5. Run the app on an emulator or physical Android device.

## Recommended Demo Flow

1. Sign up once as `Admin`.
2. Sign up again as `Alumni`.
3. Log in as admin and create a few needs with images.
4. Log in as alumni and pledge amounts.
5. Watch the dashboard, detail page, and hall of fame update in real time.

## Notes

- The pledge flow is intentionally simulated and does not process real payments.
- Firestore offline persistence is enabled in `AppModule.kt`.
- The local terminal environment used to generate this project did not include Java or Gradle, so build verification must be completed in Android Studio after Firebase setup.
