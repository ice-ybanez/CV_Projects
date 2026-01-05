# Assignment 1 - Fitness Tracker
*Student Name: Ice Ybanez*
*Student Number: R00176611*

Short README explaining Extra features, animations, or creative UI choices
How lifecycle-aware components, effect handlers, search, and FAB features were implemented

---

## ViewModel & Screens diagram
`FitnessApp` - hosts navigation (Activities, Details, Logs) and shows Toast when vm.uiMessage != null
|
`FitnessViewModel` - activities, logs, searchQuery, uiMessage
|               \
`Activities`    `Logs + FAB -> AddLogDialog`
|
`Details` — inputs (rememberSaveable), LaunchedEffect preview, Save -> vm.addLog(), navigate back

---

## Extra features, animations, creative UI choices

- **Calorie preview** on the Details screen gives instant feedback as you change duration and/or intensity.
- Subtle **layout motion** with `animateContentSize()` on the Details form.
- Dropdown arrow rotation with `animateFloatAsState`.
- Added a crossfade to Logs screen as required in lab exam with `Crossfade()`
> Images on Details Screen use `ContentScale.Fit` to avoid awkward cropping.
> Images found in `res/drawable/` under names that match exercises listed in Screen 1

---

## Lifecycle-aware components, effect handlers, search, and FAB

- **Lifecycle-aware state**
    - One shared **`FitnessViewModel`** is used across all screens (state survives configuration changes).
    - Compose snapshot state (`mutableStateOf`, `mutableStateListOf`, `derivedStateOf`) is used.

- **Effect handlers**
    - Used `LaunchedEffect` in `DetailsScreen` to recalculate the calorie preview when inputs change.
    - `SideEffect` in `FitnessApp` shows a Toast when `vm.uiMessage` is set, then clears it.

- **Search**
    - `searchQuery` is found in the ViewModel.
    - Logs screen filters the in-memory list based on `searchQuery` and updates instantly.

- **FAB**
    - On the Logs screen, the FAB opens `AddLogDialog`.
    - The dialog uses a simple `DropdownMenu` to pick an activity and saves directly to the ViewModel.

---

## Notes

- Basic validation on duration (must be > 0)
- There is a [table](https://metscalculator.com/) found on this site that I use as values for METs in `SampleData.kt`
- I also Googled "what is the MET of ___ exercise/training" and used the general result as the value for MET.
- The formula used in DetailsScreen and FitnessViewModel is (estimated) Calories Burned = MET * 3.5 * weight(70kg)/200 * intensity.
- The intensity formula is on a linear scale from 0.8 - 1.2, intensity_factor = 0.8 + intensity-1/9 * 0.4
- Both formulas aren't 100% accurate but it's the general formula I used to keep things simple.
- Images are stock photos found by browsing Google Images with search example: "<Activity> stock image shape"