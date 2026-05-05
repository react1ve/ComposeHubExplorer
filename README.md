### :bulb: Task Description

The application allows users to browse trending Kotlin repositories on GitHub and search for
repositories by name. Results are displayed with pagination support.

Main functionality:

1. **Home Screen** - Displays a paginated list of popular Kotlin repositories from GitHub.
2. **Search Screen** - Users can search for repositories with debounced input, and the app displays
   paginated results.
3. **Repository Details** - Shows detailed information about a selected repository including stars,
   forks, language, and owner info.

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

## Testing (TDD Approach)

All unit tests in this project were developed following the **Test-Driven Development (TDD)**
methodology:

1. **RED** — Write a failing test that defines the expected behavior before implementing the code.
2. **GREEN** — Write the minimal production code to make the test pass.
3. **REFACTOR** — Improve the code while keeping all tests green.

Tests follow the **Given / When / Then** structure and use the naming convention:
```
GIVEN <precondition> WHEN <action> THEN <expected result>
```

### Test coverage by module:

* **domain** — UseCases (`GetPagedKotlinReposUseCase`, `SearchReposUseCase`, `GetRepoUseCase`),
  domain models.
* **data** — Repository (`GithubRepositoryImpl`), DataSource (`GithubDataSource`),
  mappers (`RepoDtoMapper`, `RepoResultListDtoMapper`, `RepoDboMapper`, `UserDtoMapper`,
  `UserDboMapper`), cache (`RepositoryCache`), paging (`SearchPagingSource`),
  interceptor (`PagingInterceptor`).
* **app** — ViewModels (`SearchViewModel`, `HomeViewModel`, `DetailsViewModel`),
  UI state classes (`ScreenStatus`, `SearchState`, `DetailsScreenState`),
  and error-to-UI status mapping extensions.

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
