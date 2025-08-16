# FocusFlow Anime App

A modern Android anime application built with Jetpack Compose, MVVM architecture, and clean architecture principles. The app fetches anime data from the Jikan API and provides a beautiful, intuitive user interface for browsing and viewing anime details.

## Features

### ✅ Implemented Features

1. **Anime List Page**
   - Fetches top anime from Jikan API
   - Displays anime with title, episodes, rating, and poster images
   - Pull-to-refresh functionality
   - Error handling with retry mechanism
   - Loading states

2. **Anime Detail Page**
   - Comprehensive anime information display
   - Synopsis, genres, ratings, and additional details
   - Trailer placeholder (YouTube integration ready)
   - Favorite toggle functionality
   - Beautiful Material Design 3 UI

3. **Architecture & Design Patterns**
   - **MVVM Architecture** with clean separation of concerns
   - **Domain Layer**: Use cases, repository interfaces, and domain models
   - **Data Layer**: Repository implementation, API service, and local database
   - **Presentation Layer**: ViewModels, UI states, and Compose screens
   - **StateFlow** for reactive data handling (no callbacks)
   - **Dependency Injection** with Hilt

4. **Local Database with Room**
   - Offline data storage
   - Automatic data synchronization
   - Favorite anime management
   - Type converters for complex data structures

5. **Networking & Image Loading**
   - **Retrofit** for API calls
   - **Coil** for efficient image loading
   - **OkHttp** with logging interceptor
   - Error handling and offline support

6. **Video Playback**
   - **ExoPlayer** integration for video playback
   - Proper instance management
   - YouTube trailer support (placeholder implementation)

7. **UI/UX Features**
   - Modern Material Design 3
   - Responsive layout
   - Smooth animations and transitions
   - Accessibility support
   - Dark/light theme support

### 🔧 Technical Implementation

- **Minimum SDK**: 29 (Android 10)
- **Target SDK**: 35 (Android 15)
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM + Clean Architecture
- **Dependency Injection**: Hilt
- **Database**: Room
- **Networking**: Retrofit + OkHttp
- **Image Loading**: Coil
- **Video Player**: ExoPlayer
- **Navigation**: Navigation Compose
- **State Management**: StateFlow

## Project Structure

```
app/src/main/java/com/focus/flow/
├── data/
│   ├── local/
│   │   ├── AnimeDao.kt
│   │   └── AnimeDatabase.kt
│   ├── remote/
│   │   └── JikanApiService.kt
│   └── repository/
│       └── AnimeRepositoryImpl.kt
├── di/
│   ├── DatabaseModule.kt
│   ├── NetworkModule.kt
│   └── RepositoryModule.kt
├── domain/
│   ├── model/
│   │   └── Anime.kt
│   ├── repository/
│   │   └── AnimeRepository.kt
│   └── usecase/
│       ├── GetAnimeByIdUseCase.kt
│       ├── GetTopAnimeUseCase.kt
│       └── ToggleFavoriteUseCase.kt
├── presentation/
│   ├── component/
│   │   ├── AnimeCard.kt
│   │   ├── ExoPlayerManager.kt
│   │   └── VideoPlayer.kt
│   ├── navigation/
│   │   └── AnimeNavigation.kt
│   ├── screen/
│   │   ├── AnimeDetailScreen.kt
│   │   └── AnimeListScreen.kt
│   ├── state/
│   │   ├── AnimeDetailState.kt
│   │   └── AnimeListState.kt
│   └── viewmodel/
│       ├── AnimeDetailViewModel.kt
│       └── AnimeListViewModel.kt
├── FocusFlowApplication.kt
└── MainActivity.kt
```

## API Endpoints Used

- **Top Anime**: `https://api.jikan.moe/v4/top/anime`
- **Anime Details**: `https://api.jikan.moe/v4/anime/{anime_id}`

## Assumptions Made

1. **Legal Compliance**: The app assumes that displaying anime images and data from Jikan API is legally compliant for educational/demo purposes.

2. **Network Handling**: The app gracefully handles network failures by falling back to cached data.

3. **YouTube Integration**: For simplicity, YouTube trailers show a placeholder. In production, you'd integrate the YouTube Player API.

4. **Image Loading**: Uses Coil for efficient image loading with proper error handling.

5. **Offline Support**: The app works offline using cached data from Room database.

## Known Limitations

1. **YouTube Trailer Playback**: Currently shows a placeholder. Requires YouTube Player API integration for full functionality.

2. **Pagination**: The current implementation loads all top anime at once. For better performance, pagination could be implemented.

3. **Search Functionality**: Not implemented but can be easily added using Jikan API search endpoints.

4. **User Authentication**: No user accounts or personalized features.

5. **Push Notifications**: Not implemented for new anime releases.

## Error Handling

The app implements comprehensive error handling:

- **Network Errors**: Graceful fallback to cached data
- **API Errors**: User-friendly error messages with retry options
- **Database Errors**: Proper exception handling
- **Image Loading Errors**: Fallback placeholders
- **Video Playback Errors**: Graceful degradation

## Offline Mode & Syncing

- **Local Storage**: All anime data is cached in Room database
- **Offline Access**: App functions without internet connection
- **Data Sync**: Automatic synchronization when online
- **Conflict Resolution**: Latest data takes precedence

## Design Constraint Handling

The app is designed to be flexible and handle UI changes gracefully:

- **Responsive Layout**: Adapts to different screen sizes
- **Image Fallbacks**: Handles missing images gracefully
- **Content Adaptation**: UI adjusts based on available data
- **Accessibility**: Proper content descriptions and navigation

## Getting Started

### Prerequisites

- Android Studio Hedgehog or later
- Android SDK 29+
- Kotlin 2.0.21+

### Installation

1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Build and run the app

### Building the APK

```bash
./gradlew assembleDebug
```

The APK will be generated at: `app/build/outputs/apk/debug/app-debug.apk`

## Future Enhancements

1. **YouTube Player Integration**: Full trailer playback support
2. **Search & Filtering**: Advanced search functionality
3. **Pagination**: Efficient data loading for large lists
4. **User Preferences**: Personalized anime recommendations
5. **Push Notifications**: New episode notifications
6. **Social Features**: Reviews, ratings, and comments
7. **Watchlist Management**: Better favorite organization
8. **Dark/Light Theme**: User theme preferences

## Contributing

This is a demo project showcasing modern Android development practices. Feel free to fork and enhance it with additional features.

## License

This project is for educational purposes. Please respect the Jikan API terms of service and anime content copyrights.

## Acknowledgments

- **Jikan API**: For providing anime data
- **MyAnimeList**: For the comprehensive anime database
- **Jetpack Compose**: For the modern UI framework
- **Material Design 3**: For the beautiful design system
