### :bulb: Task Description

The application should allow users to search for GitHub repositories by username and display the
results.

Main functionality:

1. **Splash Screen** - A loading screen displayed when the app starts.
2. **Search Screen** - Users can search for repositories by entering a username, and the app will
   display a list of repositories.
3. **Repository Details** - Users can open the repository link in the browser.
4. **Download Feature** - Users can download repositories into the "Download" folder.
5. **Downloads Screen** - Displays a list of downloaded repositories, showing the username and
   repository name, with a well-designed layout.

## Development

* Multi Module & Single Activity Concepts
* UI fully written in [Jetpack Compose](https://developer.android.com/jetpack/compose).
* Built 100% in Kotlin and
  uses [Kotlin Coroutines](https://kotlinlang.org/docs/reference/coroutines/coroutines-guide.html).
* Uses many of
  the [Architecture Components](https://developer.android.com/topic/libraries/architecture/),
  including Lifecycle and Navigation.
* [Hilt](https://dagger.dev/hilt/) for dependency injection.
* Animations are displayed with [Lottie](https://airbnb.io/lottie/).
* Images are shown using [Coil](https://coil-kt.github.io/coil/).
* [Retrofit2 & OkHttp3](https://github.com/square/retrofit) - construct the REST APIs
  for [Github API](https://docs.github.com/es/rest).
* [Gradle version catalog TOML file](https://docs.gradle.org/current/userguide/platforms.html) for
  sharing dependencies.
* This project uses [detekt](https://detekt.dev/) as static code analysis tool and
  [spotless](https://github.com/diffplug/spotless) for checking code style
  (Kotlin with [ktlint](https://github.com/pinterest/ktlint), XMLs and Gradle Files).

## Testing

Unit tests are added for the main layers and modules:

* **domain**: UseCases (`GetPagedKotlinReposUseCase`, `SearchReposUseCase`, `GetRepoUseCase`).
* **data**: Repository, DataSource, mappers (`RepoDtoMapper`, `RepoResultListDtoMapper`,
  `RepoDboMapper`, `UserDtoMapper`, `UserDboMapper`) and cache (`RepositoryCache`).
* **app**: ViewModels (`SearchViewModel`, `HomeViewModel`, `DetailsScreenViewModel`) and
  error-to-UI status mapping extensions.

## :camera_flash: Screenshots

### 🌞 Light Mode

| Home                                                | Search                                                | Details                                                 |
|-----------------------------------------------------|-------------------------------------------------------|---------------------------------------------------------|
| <img src="/screenshots/home_light.png" width="260"> | <img src="/screenshots/search_light.png" width="260"> | <img src="/screenshots/details_light.png" width="260"> |

### 🌚 Dark Mode

| Home                                               | Search                                               | Details                                                |
|----------------------------------------------------|------------------------------------------------------|--------------------------------------------------------|
| <img src="/screenshots/home_dark.png" width="260"> | <img src="/screenshots/search_dark.png" width="260"> | <img src="/screenshots/details_dark.png" width="260"> |

## Find this repository useful? :heart:
