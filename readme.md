# Medscape 2.0

Medscape 2.0 is an innovative and comprehensive **Waste Management Application** designed to tackle
India's escalating garbage disposal challenges. This cutting-edge Android app serves as a powerful
tool to educate users about effective waste management practices and streamline the waste collection
process.

The app provides a wealth of _educational content_, _real-time waste dumping request systems_, and
_advanced features for waste collectors_, all wrapped in a user-friendly interface. Medscape 2.0 is
poised to make a significant impact in creating cleaner, more sustainable urban environments across
India.

> The `2.0` in the app name refers to `version-2` of this Application.

## Features

### Educational Content

- **Types of Waste**: Learn about various waste categories.
- **Sources of Waste**: Get to know about various waste sources like _Commercial Waste_,
  _Agricultural Waste_, etc
- **Latest News**: Stay informed with articles about global waste management initiatives.
- **Data Visualization**: Access graphical representations of waste-related statistics, includes:
    - Global waste type percentages
    - Waste generation by Indian states
    - Proportions of treated, landfilled, and untreated waste
    - Global income-wise waste amount generation, and more...

### Waste Dumping Request System

- Users can submit requests to dump waste, categorized as:
    - General
    - Metal
    - Medical
    - Plastic
- This feature aids in efficient waste segregation at the source.

### News Search

- Search for specific articles according to your interest with various filters available.
- Browse top headlines from different countries.

### User Profile Management

- View and update personal details including name, email, password, profile photo, address, etc.

### Collector Interface

- **Real-time Data**: View the number of waste dump requests at `city`, `state`, and `country`
  levels.
- **Dumper List**: Access detailed information about waste dumpers, including waste types and
  locations.
- **Map View**: Visualize waste dumper locations with customizable filters like radius and display
  options.
- **Live Location**: Get the `live location` of collector and nearby waste dumpers on map.

## Architecture and Design

Medscape 2.0 is built with modern Android development best practices in mind:

- **MVVM Clean Architecture**: Ensures separation of concerns, making the codebase more maintainable
  and testable.
- **Material 3 Components**: Utilizes the latest Material Design practices and components for a
  modern and consistent user interface, including:
    - Bottom sheets
    - Sliders
    - Collapsable toolbar
    - Autocomplete dropdowns
    - Segmented buttons
    - Animated search bars and search views
    - Chips, etc.

## Technologies Used

- Kotlin
- XML
- Firebase

## Dependencies

Medscape 2.0 leverages a variety of modern libraries and tools:

1. **Kotlin Coroutines and Flow**:
    - **Coroutines**: For managing background threads with simplified code and reducing needs for
      callbacks.
    - **Flow**: For handling streams of data asynchronously.
2. **Jetpack Components**:
    - **ViewModel**: For managing UI-related data in a lifecycle-conscious way.
    - **Data Binding**: For declaratively binding UI components in layouts to data sources.
    - **View Binding**: For more efficient view access.
    - **Navigation**: For handling in-app navigation.
    - **DataStore**: For storing key-value pairs or typed objects with protocol buffers.
3. **Dependency Injection**:
    - **Hilt**: For dependency injection, reducing boilerplate and promoting modularity.
4. **Networking**:
    - **Retrofit**: Type-safe HTTP client for Android and Java.
    - **OkHttp**: Efficient HTTP client that's the backbone of Retrofit.
    - **Gson**: For JSON serialization/deserialization.
5. **UI and Design**:
    - **Material 3**: For implementing Material Design components and themes.
    - **Glide**: Fast and efficient image loading library.
    - **AnyChart**: For creating interactive and customizable charts.
6. **Maps and Location**:
    - **Google Maps SDK**: For integrating mapping functionality.
    - **Maps Utils**: Additional utilities for Google Maps.
7. **Logging**:
    - **Timber**: A logger with a small, extensible API.

## Screenshots

[Insert screenshots of your app here]

<!-- You can add screenshots using the following format:
![Screen Name](path/to/screenshot.png)
Repeat this for each screenshot you want to include
-->

## Installation

1. Clone the repository:

```bash
git clone https://github.com/itssinghankit/Medscape-2.0.git
```

2. Install necessary dependencies.
3. Add `Google Map API key` from _Google Maps SDK_ to `secrets.properties`

```bash
MAPS_API_KEY= <Your API key>
```

4. Generate a `News API key` and add it to `local.properties`

```bash
BASE_URL=https://newsapi.org/v2/
NEWS_API_KEY= <Your API key>
```

5. Configure Firebase credentials.
6. Run the app on an Android device or emulator.

## Contributing

We welcome contributions to improve Medscape 2.0. Fork the repository, create a branch for your
changes, and submit a pull request.

## Contact

Feel free to reach out with any questions or feedback at
[singhankit.kr@gmail.com](singhankit.kr@gmail.com)

---

Medscape 2.0 is committed to making waste management more accessible and efficient, contributing to
a cleaner, healthier environment for all.
