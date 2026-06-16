# Android Club Technical Interview

Below are 25 technical questions assessing knowledge related to the implementation and structural boundaries of **DevPulse**, alongside their ideal answers.

### Jetpack Compose & UI
**1. How do you manage recomposition effectively in Compose?**
*Answer*: By correctly using `remember` and immutable states. Avoid passing rapidly changing, unbound objects. `derivedStateOf` is used to limit recomposition frequency when deriving state from lists or properties that change frequently.

**2. What is the difference between `LazyColumn` and `Column`?**
*Answer*: `Column` renders all its children instantly, taking up memory, whereas `LazyColumn` only composes and caches elements that are currently visible on the screen, improving performance for long lists.

**3. In `AnalyticsScreen`, how did you implement the custom chart?**
*Answer*: The chart leverages the Compose `Canvas` boundary. By capturing the layout size properties within `onDraw` (via `size.width` and `size.height`), I calculate the dynamic coordinates to draw the underlying bar elements via `drawRect`.

**4. How does `Navigation Compose` handle backstack entries?**
*Answer*: It utilizes a `NavHostController` that binds to the single activity root. Screens are registered internally, creating a backstack. Selecting a bottom nav item clears the stack (`popUpTo(startDestinationId)`) to avoid piling up infinite history trees.

### State & MVVM
**5. What is the role of `StateFlow` over standard `LiveData`?**
*Answer*: `StateFlow` is a purely Kotlin-native flow inherently lifecycle aware when collected correctly. It removes dependencies on the Android framework while offering native coroutine cancellation and map operations.

**6. Why use `SharingStarted.WhileSubscribed(5000)`?**
*Answer*: It prevents the flow from terminating immediately during configuration changes (like rotations) preventing unnecessary databse / network fetches by buffering the active observer lifetime for up to 5 seconds.

**7. How do you inject the repository into your ViewModel?**
*Answer*: I implemented a custom `ViewModelProvider.Factory` that receives the repository instance and manually seeds it into the `ViewModel` constructor upon runtime request.

**8. What happens to the ViewModel when the screen is rotated?**
*Answer*: The ViewModel inherently survives configuration changes. As state flows are bound to `viewModelScope`, running queries survive and emit updated values when the UI reconciles.

### Advanced Data & Room
**9. How do you implement the "Offline-First" behavior in Room?**
*Answer*: All screen Data originates purely from the Room Database DAOs via reactive `Flow<T>`. When operations occur (adding a task), they are written sequentially to the DB, which implicitly emits its updated list downward to the UI cache automatically.

**10. What does `@Insert(onConflict = OnConflictStrategy.REPLACE)` achieve?**
*Answer*: If an entity is inserted that matches an existing Primary Key in the SQLite representation, it overwrites the existing row instantly rather than failing the transaction constraint.

**11. Why do we run DB populated seed code inside `CoroutineScope(Dispatchers.IO)`?**
*Answer*: SQLite Room operations block the active thread. Executing on `Dispatchers.IO` sends the heavy load to an optimized worker thread pool guaranteeing the Main UI thread does not skip frames or invoke ANRs.

**12. Explain the significance of the `exportSchema` property on the Database?**
*Answer*: Disabling it is suitable for localized side projects. If `exportSchema = true`, Room outputs JSON snapshot structures of the tables, allowing engineers to diff structure changes chronologically.

### Networking & Retrofit
**13. What advantages does Retrofit offer over manual `HttpURLConnection`?**
*Answer*: Retrofit provides type-safe abstractions generating internal proxy classes parsing interfaces to dynamic requests, inherently deserializing HTTP JSON streams seamlessly mapped via tools like Moshi.

**14. What occurs if GitHub's rate limit is reached?**
*Answer*: Retrofit triggers an `HttpException` bearing the HTTP status code (likely 403 or 429). The `GitHubViewModel` catches this boundary, intercepts the error code, and pushes a graceful error string downward to the Compose layout.

**15. Why use Moshi over older tools like GSON?**
*Answer*: Moshi is modern, memory efficient, has native Kotlin code-generation features (`@JsonClass(generateAdapter = true)`), bypassing reflection which shrinks the overall application speed drastically.

### Miscellaneous Architecture & Android Best Practices
**16. Why does `.getInstance(context.applicationContext)` require `applicationContext`?**
*Answer*: Room singletons live beyond the lifespan of an Activity shell. Using an `Activity Context` causes extreme memory leaks because the Database caches those activity boundaries eternally.

**17. What is modern Edge-To-Edge architecture?**
*Answer*: An OS standard where our Compose UI renders actively beneath the status bar and navigation pill blocks seamlessly, managing boundaries through `WindowInsets` padded scaffold properties.

**18. Contrast a `Flow` against a standard `suspend` return class?**
*Answer*: A `suspend` returns a single, absolute result when processing ceases. A `Flow` creates a constant, asynchronous streaming pipe that executes chronologically firing continuous updates.

**19. How did you structure the Activity logs on trigger events?**
*Answer*: Inside the Repository methods, immediately upon completing the SQL `insert()` for a new object, the Repository executes a secondary function inserting the raw string audit string directly into the Activity DAO.

**20. What is `CoroutineScope(Dispatchers.Main)` primarily utilized for?**
*Answer*: Modifying interactive View properties or directly handling rendering instructions.

**21. Where did you add the Internet Manifest permission and why?**
*Answer*: Added in `AndroidManifest.xml` atop the application boundary. The OS demands explicit internet declaration constraints primarily to prevent rogue offline apps from executing outbound telemetry.

**22. If your app required Authentication, how would you design it?**
*Answer*: Using the OAuth 2.0 Auth flow via `AppAuth` or Google Identity Services. A Bearer token is intercepted via Retrofit Interceptors cleanly sending constraints back dynamically.

**23. Why use `AsyncImage` from Coil?**
*Answer*: Coil perfectly manages the bitmap pooling network request asynchronously mapped natively into Compose rendering pipelines with disk caching bounds by default.

**24. In the `DevPulseBottomBar`, how exactly is state "restored" when switching tabs?**
*Answer*: Due to the boolean property `restoreState = true` executing on `popUpTo`, Compose saves and retrieves the destination's nested backstack states natively bypassing arbitrary redraws.

**25. If the project grows immensely, how do we fix scaling issues?**
*Answer*: Transition from generic MVVM to Clean Architecture via specific UseCase bounds. Segregate modules, adopt `Hilt` for abstract Dependency Injection, and create complex visual regression screenshot tests to guarantee component fidelity.
