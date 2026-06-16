# DevPulse

**DevPulse** is a beautifully designed, developer productivity and GitHub activity tracker built exclusively for Android. Designed as an evaluation project for the Android Club recruitment, DevPulse demonstrates proficiency in modern Android development best practices including Clean Architecture, MVVM, Room Database persistence, and the consumption of public REST APIs via Retrofit.

## 🚀 Features

*   **Dashboard Overview**: A clean summary of your developer stats: tracking completed tasks, active projects, and recent activity logs.
*   **Projects Management**: Full CRUD operations for developer projects containing name, description, and source language.
*   **Tasks & Progress**: Track daily goals and associate them with projects. Supports checking off items which updates activity feeds.
*   **Analytics Engine**: Interactive data visualization of weekly productivity trends and project specific completion metrics using custom Compose Canvas graphs.
*   **GitHub Integration**: Public profile viewer. Instantly fetch and display public repositories, follower counts, and languages via the GitHub REST API.
*   **Offline-First & Seeded Local Data**: Data is persisted seamlessly using Jetpack Room database, complete with realistic mocked data on first launch.
*   **Dark Mode Support**: Beautiful Material 3 colors adapting perfectly to both Light and Dark system settings.

## 📸 Screenshots

*(To the developer submitting this, replace the text below with actual screenshots of your app running horizontally or in a grid.)*

1.  **Dashboard Screen** (`screenshots/1_dashboard.png`) - Showcases stats and activity feed.
2.  **Projects Screen** (`screenshots/2_projects.png`) - Show active projects. Capture the dialogue for adding a new project.
3.  **Tasks Screen** (`screenshots/3_tasks.png`) - Capture pending and completed tasks.
4.  **Analytics Screen** (`screenshots/4_analytics.png`) - Demonstrate the Canvas bar chart rendering the weekly tasks and project completion metrics.
5.  **GitHub Profile View** (`screenshots/5_github.png`) - Entering a valid GitHub tag and seeing the downloaded profile and repos.
6.  **Dark Mode Theme** (`screenshots/6_dark_mode.png`) - Capture any screen with Dark mode toggled.

## 🛠 Tech Stack

*   **Language**: Kotlin
*   **UI Toolkit**: Jetpack Compose (Material 3)
*   **Architecture**: MVVM (Model-View-ViewModel), leveraging Repository pattern.
*   **Local Database**: Room persistence library supporting complex SQL Queries, Coroutines, and Flow streams.
*   **Networking**: Retrofit 2 + Moshi for JSON parsing.
*   **Image Loading**: Coil for Jetpack Compose (used internally in GitHub API profile badges).
*   **Asynchrony**: Kotlin Coroutines & kotlinx.coroutines.flow.
*   **Navigation**: Jetpack Navigation Compose.

## 🏗 Architecture Explained

The application relies on the **MVVM architecture**:

*   **Data Layer**: Contains Room entities (`Project.kt`, `Task.kt`, `ActivityLog.kt`), DAOs, the `AppDatabase`, and the network API service `GitHubApiService`. A central `DevPulseRepository` mediates access.
*   **UI Layer / ViewModel**: The ViewModels (`DevPulseViewModel`, `GitHubViewModel`) use `StateFlow` to manage the UI state efficiently.
*   **Presentation / Views**: Jetpack Composable functions map the state onto the screen (e.g., `DashboardScreen`, `AnalyticsScreen`). Recomposition happens reactively when the `StateFlow` updates.

## ⚙️ Setup & Installation

To run this project locally:

1.  Clone this repository: `git clone <repo-link>`
2.  Open in Android Studio (Jellyfish or later recommended).
3.  Ensure Java 17 and latest Android SDK are configured.
4.  Build and run on an Emulator or Physical Device. No external API keys are required as it utilizes the public, unauthenticated GitHub read-only endpoints.

## 🔮 Future Improvements

*   Implement proper OAuth2 Flow for detailed private GitHub stats and commit tracking.
*   Complete Git integration to auto-resolve "Tasks" when developers push specific commit message forms (e.g. `Fix #23`).
*   Schedule WorkManager background syncs to fetch data silently when the app is minimized.
