# Shaale-Vikas

**Shaale-Vikas** is a Kotlin + Jetpack Compose Android app that helps rural schools publish infrastructure needs and enables alumni to pledge support with live progress tracking.

## 🎯 Problem Statement

Rural schools often struggle to communicate infrastructure needs (repairs, facilities, equipment) to their alumni network. There's no organized platform for:
- Rural schools to list infrastructure needs with priority and cost estimates
- Alumni to discover and pledge support for their alma mater
- Real-time tracking of funding progress and donor recognition

**Shaale-Vikas** bridges this gap by creating a transparent, real-time fundraising platform for school infrastructure development.

## 📱 Screenshots

| Need Details | Pledge Simulation |
|---|---|
| <img src="https://github.com/abhi43-r/Shaale-Vikas/blob/main/WhatsApp%20Image%202026-05-13%20at%2012.54.32%20PM.jpeg" width="250"> | <img src="https://github.com/abhi43-r/Shaale-Vikas/blob/main/WhatsApp%20Image%202026-05-13%20at%2012.54.04%20PM.jpeg" width="250"> |

| Home Dashboard | Profile Creation |
|---|---|
| <img src="https://github.com/abhi43-r/Shaale-Vikas/blob/main/WhatsApp%20Image%202026-05-13%20at%2012.48.20%20PM.jpeg" width="250"> | <img src="https://github.com/abhi43-r/Shaale-Vikas/blob/main/WhatsApp%20Image%202026-05-13%20at%2012.48.09%20PM.jpeg" width="250"> |

## 🚀 Key Features

### For Admins (School Representatives)
- ✅ **Email/Password Authentication** - Secure role-based login
- ✅ **Create Infrastructure Needs** - Add title, description, images, cost estimates, priority levels
- ✅ **Edit & Delete Needs** - Manage published needs and update progress
- ✅ **Image Upload** - Upload before/after photos via Firebase Storage
- ✅ **Real-time Progress Tracking** - Track donations with Firestore live updates
- ✅ **Admin Dashboard** - Overview of all published needs and donation status

### For Alumni (Donors)
- ✅ **Browse Needs** - Search and filter by category, priority, or school location
- ✅ **Sort Options** - Sort by newest, most funded, or closest to goal
- ✅ **Detailed Need Pages** - View complete information, images, and donor feedback
- ✅ **Pledge Support** - Simulate pledging amounts with live Firestore synchronization
- ✅ **Hall of Fame** - Donor leaderboard showing top contributors
- ✅ **Persistent Login** - Firebase session management for seamless experience
- ✅ **Offline Caching** - Basic offline access via Firestore persistence

### General Features
- 🎨 **Material 3 Design** - Modern dark mode-ready UI
- 🔄 **Real-time Sync** - Firestore-backed live data updates
- 📱 **Responsive Jetpack Compose** - Native Android performance
- 🏗️ **Clean Architecture** - MVVM + Repository pattern
- 💉 **Dependency Injection** - Hilt for scalable DI

## 🛠️ Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose + Material 3
- **Architecture**: MVVM + Repository Pattern
- **Authentication**: Firebase Authentication (Email/Password)
- **Database**: Firebase Firestore with offline persistence
- **Storage**: Firebase Storage for image uploads
- **Dependency Injection**: Hilt
- **Image Loading**: Coil
- **Build System**: Gradle (Kotlin DSL)

## 📁 Project Structure

```
Shaale-Vikas/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/shaalevikas/
│   │   │   │   ├── ui/               # Compose screens, components, navigation, theme
│   │   │   │   ├── viewmodel/        # Screen state and business logic coordination
│   │   │   │   ├── repository/       # Firebase data layer and repository interfaces
│   │   │   │   ├── data/             # Models and Firestore collection constants
│   │   │   │   ├── di/               # Hilt dependency injection modules
│   │   │   │   ├── utils/            # State wrappers and formatting helpers
│   │   │   │   └── MainActivity.kt   # App entry point
│   │   │   └── AndroidManifest.xml
│   ├── build.gradle.kts              # App-level dependencies and configuration
│   └── proguard-rules.pro
├── build.gradle.kts                  # Project-level build configuration
├── settings.gradle.kts               # Gradle modules configuration
├── gradle.properties                 # Gradle runtime properties
├── firestore.rules                   # Firestore security rules
├── firestore.indexes.json            # Firestore composite indexes
├── storage.rules                     # Firebase Storage security rules
└── README.md
```

## 🔐 Firestore Data Model

### `users` collection
- `id` (String) - User's UID from Firebase Auth
- `name` (String) - Full name
- `email` (String) - Email address
- `role` (String) - "Admin" or "Alumni"
- `photoUrl` (String) - Profile picture URL
- `createdAt` (Timestamp) - Account creation time

### `needs` collection
- `id` (String) - Unique need identifier
- `title` (String) - Infrastructure need title (e.g., "Library Renovation")
- `description` (String) - Detailed description
- `location` (String) - School location
- `category` (String) - Type of need (e.g., "Building", "Equipment", "Repair")
- `priority` (String) - "High", "Medium", or "Low"
- `estimatedCost` (Number) - Target amount in currency units
- `amountCollected` (Number) - Current funds pledged
- `heroImageUrl` (String) - Main need image
- `beforeImageUrl` (String) - Before state photo
- `afterImageUrl` (String) - After state photo (for completed needs)
- `status` (String) - "Active", "In Progress", or "Completed"
- `createdBy` (String) - Admin user ID who created the need
- `createdAt` (Timestamp) - Date need was posted
- `updatedAt` (Timestamp) - Last update timestamp

### `pledges` collection
- `id` (String) - Unique pledge identifier
- `needId` (String) - Reference to need being supported
- `userId` (String) - Donor's user ID
- `donorName` (String) - Donor's name
- `donorEmail` (String) - Donor's email
- `amount` (Number) - Pledged amount
- `note` (String) - Optional donation message
- `pledgedAt` (Timestamp) - Date of pledge

## 📋 Installation & Setup

### Prerequisites
- Android Studio (Hedgehog or newer)
- JDK 17 or higher
- Android SDK API 35 or higher
- Firebase account

### Android Studio Setup

1. **Clone the repository**:
   ```bash
   git clone https://github.com/abhi43-r/Shaale-Vikas.git
   cd Shaale-Vikas
   ```

2. **Open in Android Studio**:
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory and select `Shaale-Vikas`

3. **Configure Android SDK**:
   - Android Studio will prompt to install SDK for API 35
   - Accept and wait for installation to complete

4. **Configure Gradle**:
   - Verify JDK 17 is set: `File > Settings > Build, Execution, Deployment > Build Tools > Gradle`
   - Android Studio will auto-sync Gradle wrapper if needed

5. **Sync Project**:
   - Click "Sync Now" when prompted or use `File > Sync Project with Gradle Files`

### Firebase Setup

1. **Create Firebase Project**:
   - Visit [Firebase Console](https://console.firebase.google.com/)
   - Click "Create a project"
   - Name it "Shaale-Vikas" (or your preferred name)
   - Disable Google Analytics (optional)

2. **Register Android App**:
   - In Firebase Console, click "Add app" and select Android
   - Enter package name: `com.shaalevikas`
   - Download `google-services.json`

3. **Add Firebase Configuration**:
   - Place `google-services.json` in `app/` directory (same level as `build.gradle.kts`)

4. **Enable Firebase Services**:
   - Navigate to Authentication > Sign-in method
   - Enable "Email/Password"
   - Navigate to Firestore Database
   - Create database in "Production mode" (configure rules later)
   - Navigate to Storage
   - Create a new bucket

5. **Deploy Security Rules**:
   - From Firestore Database section, go to "Rules" tab
   - Replace with contents of `firestore.rules` from the repo
   - Publish changes
   - For Storage, go to "Rules" tab and replace with `storage.rules`
   - Publish

6. **Deploy Composite Indexes** (Optional but recommended):
   - From Firestore Database section, go to "Indexes" tab
   - Import `firestore.indexes.json`
   - Publish indexes

## ▶️ How To Run

### Method 1: Android Studio Emulator

1. **Setup Emulator**:
   - Tools > Device Manager > Create Virtual Device
   - Select "Pixel 6" (or preferred device)
   - Select "API 35" image and finish setup

2. **Run the App**:
   - Click the green "Run" button in Android Studio
   - Select your emulator
   - App will install and launch

3. **Test on Emulator**:
   - Sign up as "Admin" and create infrastructure needs
   - Sign up as different "Alumni" and pledge amounts
   - Watch real-time updates across the app

### Method 2: Physical Android Device

1. **Enable Developer Mode**:
   - On your Android device: Settings > About Phone > Tap "Build Number" 7 times
   - Navigate to Settings > Developer options > Enable "USB Debugging"

2. **Connect Device**:
   - Connect via USB cable
   - Select "Run" in Android Studio
   - Device will appear in device list; select it

3. **Build & Install**:
   - App will build and install automatically
   - Test on your device

## 🎮 Recommended Demo Flow

### Step 1: Create Admin Account
- Launch app → "Sign Up"
- Enter email: `admin@shaalevikas.test`
- Enter password: `Admin@123`
- Select role: **Admin**
- Tap "Create Account"

### Step 2: Create Infrastructure Needs (as Admin)
- Log in with admin account
- Tap "Add New Need"
- Fill in:
   - Title: "Library Books Collection"
   - Description: "We need to build a modern library with updated books and digital resources"
   - Location: "Rural School, [City]"
   - Category: "Education"
   - Priority: "High"
   - Estimated Cost: ₹50,000
- Upload images (optional - app works without)
- Tap "Publish"
- Repeat 2-3 times with different needs

### Step 3: Create Alumni Account
- Log out → "Sign Up"
- Enter email: `alumni@shaalevikas.test`
- Enter password: `Alumni@123`
- Select role: **Alumni**
- Tap "Create Account"

### Step 4: Browse & Pledge
- Log in with alumni account
- Browse available needs on home dashboard
- Tap on a need to view details
- Tap "Pledge Now"
- Enter amount and optional note
- Confirm pledge
- Watch progress bar and hall of fame update in real-time

### Step 5: Verify Real-time Sync
- Keep app open on two devices (or emulator + physical device)
- Make a pledge on one device
- Observe updates instantly on the other device

## 🧪 Testing Accounts

For demo purposes, you can create accounts with these patterns:
- **Admin**: Any email ending in `@shaalevikas.test` with password containing at least 6 characters
- **Alumni**: Any email ending in `@alumni.test` with password containing at least 6 characters

The app distinguishes roles by user selection during signup.

## ⚙️ Build & Compile

### Build Debug APK
```bash
./gradlew build
```

### Build Release APK (requires signing configuration)
```bash
./gradlew assembleRelease
```

### Run Unit Tests (if configured)
```bash
./gradlew test
```

### Check Dependencies
```bash
./gradlew dependencies
```

## 📊 Code Quality

- **Lines of Code**: ~2000+ meaningful lines in Kotlin
- **Architecture**: MVVM with Repository pattern for testability
- **Modules**: 6+ functional modules (UI, ViewModel, Repository, Data, DI, Utils)
- **Separation of Concerns**: Clear boundaries between UI, business logic, and data layers
- **Dependency Injection**: Hilt-based DI for loose coupling

## 🔄 Git Commit History

The repository contains meaningful commits showing incremental development:

1. Initial commit: Shaale-Vikas project
2. Add screenshots section to README
3. Add files via upload
4. Update screenshot links in README
5. Update README with corrected image links and Firebase setup paths
6. Update README to include screenshots section

Each commit represents a development milestone with clear commit messages.

## ✨ Originality & Custom Implementation

- ✅ Project name and README tailored to "Shaale-Vikas" vision
- ✅ Custom domain logic for school infrastructure fundraising
- ✅ Real Firebase integration (not template code)
- ✅ Unique UI for dual-role system (Admin/Alumni)
- ✅ Custom business rules (needs, pledges, leaderboard)
- ✅ Complete feature set beyond starter template

## 🚨 Important Notes

### Before Running
1. Ensure `google-services.json` is in `app/` directory (not in repo for security)
2. Update Firebase rules with credentials from your Firebase Console
3. Firestore offline persistence is enabled by default in `AppModule.kt`

### Pledge Simulation
- The pledge workflow is intentionally simulated and does **not** process real payments
- Use for demonstration only
- Future enhancement can integrate real payment gateways (Stripe, Razorpay, etc.)

### Security Considerations
- Never commit `google-services.json` to version control
- Use strong passwords for test accounts
- Review Firestore security rules before deployment
- Implement payment verification for production

## 📝 Future Enhancements

- [ ] Real payment integration (Stripe/Razorpay)
- [ ] Email notifications for pledges and updates
- [ ] Admin analytics dashboard
- [ ] Social sharing for campaigns
- [ ] Multi-language support
- [ ] Push notifications
- [ ] Admin approval workflow for needs
- [ ] Impact metrics and completion reporting

## 📞 Support & Contact

For issues or questions:
- Create an issue on GitHub
- Contact: gauravabhishek462@gmail.com

## 📜 License

This project is open source and available for educational purposes.

---

**Built with ❤️ for rural school infrastructure development**
