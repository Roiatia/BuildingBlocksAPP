# Claude Development Tasks

> Working file for Claude while building the Android LEGO collection app skeleton and first MVP slices.

## Product North Star

Build a practical Android collection manager for LEGO collectors. The app should help users know which sets they own, which are unbuilt, where sets are stored, and what to build next. Keep the UI clean, collector-friendly, and lightly LEGO-inspired without making it childish or visually noisy.

Primary stack:

- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- Room
- Coroutines and Flow
- WorkManager for future sync
- Supabase Auth and PostgreSQL sync after the local foundation is stable

Reference spec: `docs/superpowers/specs/2026-05-23-lego-collection-android-app-design.md`

STITCH design reference:

- Design system: `docs/stitch-design/stitch_brick_archive_manager/brickvault/DESIGN.md`
- Home: `docs/stitch-design/stitch_brick_archive_manager/home_dashboard/screen.png`
- My Collection: `docs/stitch-design/stitch_brick_archive_manager/my_collection/screen.png`
- Build Backlog: `docs/stitch-design/stitch_brick_archive_manager/build_backlog/screen.png`
- Set Details: `docs/stitch-design/stitch_brick_archive_manager/set_details/screen.png`
- Add Set: `docs/stitch-design/stitch_brick_archive_manager/add_set/screen.png`
- Storage Management: `docs/stitch-design/stitch_brick_archive_manager/storage_management/screen.png`
- Profile/Premium: `docs/stitch-design/stitch_brick_archive_manager/profile_premium/screen.png`

Treat the STITCH screens as the visual target for the Compose implementation. The HTML files are useful as layout and token references, but the Android app should be implemented natively in Jetpack Compose rather than embedding web views.

## Current Review Gate: Fix Before Continuing

Codex reviewed the implementation after the reported "phase 12" work. Do not add new product features until every item in this gate is fixed and verified.

Required proof before continuing:

```powershell
.\gradlew.bat test
.\gradlew.bat assembleDebug
```

Both commands must run from the repository root and finish successfully. Report the full command output summary, including whether tests/build passed or failed.

### Blocker 1: Gradle Wrapper Is Incomplete

Current problem:

- `gradle/wrapper/gradle-wrapper.properties` exists.
- `gradlew`, `gradlew.bat`, and `gradle/wrapper/gradle-wrapper.jar` are missing.
- Because of this, `.\gradlew.bat assembleDebug` cannot run from a fresh checkout.

Fix:

- Add the complete Gradle wrapper files.
- Keep the wrapper version aligned with `gradle/wrapper/gradle-wrapper.properties`.
- After adding the wrapper, run:

```powershell
.\gradlew.bat --version
.\gradlew.bat test
.\gradlew.bat assembleDebug
```

Definition of done:

- A fresh checkout can run the wrapper without requiring a globally installed Gradle.
- `.\gradlew.bat --version` prints the Gradle version.

### Blocker 2: Room Gradle Plugin Configuration

Current problem:

- `app/build.gradle.kts` contains a `room { schemaDirectory(...) }` block.
- The Room Gradle plugin is not declared or applied in the version catalog/root build/app build files.
- This is expected to fail Kotlin DSL evaluation.

Fix option A:

- Add the Room Gradle plugin to `gradle/libs.versions.toml`.
- Add it to root `build.gradle.kts` with `apply false`.
- Apply it in `app/build.gradle.kts`.
- Keep the `room { schemaDirectory(...) }` block.

Fix option B:

- Remove the `room { ... }` block.
- Configure Room schema export through KSP arguments instead.

Definition of done:

- Gradle configuration evaluates successfully.
- Room schema export is either correctly configured or intentionally disabled with a documented reason.

### Blocker 3: Authentication Entry Flow Is Not Active

Current problem:

- `app/src/main/java/com/buildingblocks/app/navigation/AppNavigation.kt` starts at `Screen.Home.route`.
- `AuthScreen` exists, but it does not control the initial signed-out experience.
- The MVP requires signed-out, sign-in, sign-in error, loading, and returning signed-in states.

Fix:

- Make startup route depend on auth/session state.
- Signed-out users should start at `Screen.Auth.route`.
- Authenticated users should go to `Screen.Home.route`.
- If local/offline mode is intentionally supported, make that choice explicit in UI and state.
- Profile sign-out should navigate back to Auth or otherwise clearly leave the signed-in experience.

Definition of done:

- Fresh install shows the intended signed-out or local-ready state.
- Successful Google sign-in enters the main app.
- Sign-out returns to the correct signed-out state.
- Returning signed-in users do not see Auth unnecessarily.

### Blocker 4: Add Set Validation Does Not Match Spec

Current problem:

- The spec says Add Set requires "set number or name".
- `AddEditSetViewModel.save()` currently requires `name` only.

Fix:

- Allow saving when either `name` or `legoSetNumber` is present.
- If only set number is provided, derive a display name such as `Set <number>` or add a clear UI rule requiring users to enter a name after lookup is implemented.
- Update validation messages to mention both fields.
- Add a unit test for:
  - blank name and blank number fails
  - name only succeeds
  - set number only succeeds

Definition of done:

- Manual set creation supports the MVP requirement.
- Tests cover the validation behavior.

### Blocker 5: Supabase Schema Must Match Domain and Local Models

Current problem:

- `supabase/migrations/001_initial_schema.sql` does not match the app models.
- `lego_sets` is missing `purchased_at`.
- `storage_locations` is missing `parent_location_id` and `notes`.
- `missing_parts` uses `quantity_needed` and `found` instead of the app model fields `quantity`, `status`, `notes`, and `deleted_at`.
- `build_logs` uses `action` and `build_date` instead of `started_at`, `completed_at`, and `build_time_minutes`.

Fix:

- Update the SQL schema to match these app model contracts:

```text
LegoSet:
- id
- user_id
- lego_set_number
- name
- theme
- year
- piece_count
- image_url
- status
- added_at
- purchased_at
- difficulty
- priority
- storage_location_id
- notes
- created_at
- updated_at
- deleted_at

StorageLocation:
- id
- user_id
- name
- type
- parent_location_id
- notes
- created_at
- updated_at
- deleted_at

MissingPart:
- id
- user_id
- set_id
- part_number
- color
- quantity
- status
- notes
- created_at
- updated_at
- deleted_at

BuildLog:
- id
- user_id
- set_id
- started_at
- completed_at
- build_time_minutes
- notes
- created_at
- updated_at
```

- Keep Row Level Security enabled on every business table.
- Keep policies scoped to `auth.uid() = user_id`.
- Add indexes for `user_id`, `updated_at`, `deleted_at`, statuses, and common filters.
- Update remote DTOs and sync mappers to match the SQL names exactly.

Definition of done:

- SQL schema, DTOs, Room entities, and domain models use one coherent contract.
- Sync does not silently drop fields like `purchasedAt`, `parentLocationId`, or `notes`.

### Blocker 6: Sync Logic Is Partial and Has a Remote Write Bug

Current problem:

- `SyncManager` syncs only LEGO sets and storage locations.
- Missing parts and build logs are not synced.
- In storage download, remote changes are written back to Supabase again instead of only being merged locally.
- Some downloaded fields are discarded, such as `purchasedAt`, `parentLocationId`, and `notes`.

Fix:

- Remove the accidental remote upsert during remote storage download.
- Apply the same newer-`updatedAt` conflict rule to storage locations as sets.
- Decide whether MVP sync includes missing parts/build logs. If yes, implement them. If no, document this limitation in README and code comments.
- Preserve all fields supported by the schema and local entities.
- Add tests for sync merge behavior where practical, especially newer remote wins and newer local wins.

Definition of done:

- Sync upload/download behavior is deterministic and documented.
- No remote download path writes the same data back to Supabase unnecessarily.
- Soft deletes sync correctly.

### Blocker 7: Tests Must Exercise Production Logic

Current problem:

- Current tests mostly duplicate logic inside test files.
- Example: sorting tests define a local `applySortOrder()` instead of testing production sorting/use case behavior.
- Recommendation tests define a local `recommend()` instead of testing production recommendation logic.

Fix:

- Move sorting, filtering, backlog eligibility, waiting-days, and recommendation behavior into domain use cases or other testable production functions.
- Update tests to call those production functions.
- Add validation tests for Add Set.
- Add at least one repository/Room test or document why it is deferred.

Definition of done:

- Tests fail if production sorting/recommendation/validation breaks.
- Tests are not just copies of the implementation.

### Blocker 8: README Is Not Alpha-Ready

Current problem:

- `README.md` currently only contains the project title.
- Phase 11 requires setup, Supabase config, local build commands, and known limitations.

Fix:

- Add:
  - Project overview
  - Android Studio requirements
  - How to create `secrets.properties`
  - Required Supabase values
  - Google Sign-In setup notes
  - Build commands
  - Test commands
  - Known limitations
  - Current MVP status

Definition of done:

- A new developer can clone the repo, configure local secrets, run tests, and build the debug app.

### Blocker 9: Encoding Cleanup

Current problem:

- Some files contain mojibake such as `â`, `Â`, or broken box-drawing comments.
- This has appeared in UI strings, comments, and SQL comments.

Fix:

- Search for broken text:

```powershell
rg -n "â|Â" app supabase docs README.md
```

- Replace user-visible text with plain ASCII or correct UTF-8.
- Prefer ASCII in comments unless there is a real product reason for Unicode.

Definition of done:

- The search above returns no broken user-visible strings.
- UI labels render cleanly.

### Required Response After This Gate

When these fixes are complete, report:

- Files changed.
- Whether `.\gradlew.bat --version` passed.
- Whether `.\gradlew.bat test` passed.
- Whether `.\gradlew.bat assembleDebug` passed.
- Any remaining known limitations.
- Any decisions made differently from this document and why.

## General Rules

- Work in small, testable slices.
- Prefer local-first behavior before cloud sync.
- Keep domain models independent from Room and Supabase DTOs.
- Do not hard-code secrets, API keys, or Supabase credentials.
- Keep feature packages focused and close to their screens/use cases.
- Use soft delete fields for records that will sync later.
- Add meaningful tests for sorting, filtering, backlog rules, and persistence behavior.
- Do not implement payments, BrickLink, Rebrickable, or full parts inventory in the MVP.
- Match the STITCH BrickVault visual direction unless it conflicts with Android usability.

## Visual Direction From STITCH

Use the BrickVault design system as the default UI language:

- App identity: `BrickVault`
- Style: clean, archival, modern Material 3 inventory tool for adult LEGO collectors.
- Background: near-white gallery surface, roughly `#F9F9F9`.
- Primary action color: LEGO-inspired red, roughly `#D0021B`.
- Secondary/navigation accent: blue, roughly `#0055A4` / `#175EAD`.
- Positive status: green, used for sealed/complete states.
- In-progress highlight: yellow/amber.
- Typography: Inter-like feel. On Android, use the closest available setup unless Inter is added cleanly.
- Layout: 4px grid, 16px mobile page margins, 12px card gutters, 48px minimum touch targets.
- Cards: white or near-white surfaces, subtle outline, restrained elevation, image-first for set cards.
- Inputs: outlined Material style with blue focus state.
- Status badges: pill-shaped, uppercase, compact, color-coded.
- Avoid heavy shadows, loud toy-store styling, excessive gradients, and childish decoration.

When implementing Compose screens, translate STITCH structure into native components:

- Top app bars, bottom navigation, FABs, cards, chips, lists, forms, and dialogs should be Compose/Material 3.
- Preserve the information hierarchy and spacing from the screenshots.
- Use the STITCH screenshots for visual QA after each UI phase.
- Keep image aspect ratios stable so collection cards do not jump while images load.

## Suggested Package Shape

Use this structure unless the generated Android project strongly suggests a better local convention:

```text
app/src/main/java/.../
- ui/
- feature/auth/
- feature/home/
- feature/collection/
- feature/backlog/
- feature/setdetails/
- feature/addset/
- feature/storage/
- feature/profile/
- data/local/
- data/remote/
- data/repository/
- domain/model/
- domain/usecase/
- sync/
```

## Phase 1: Android Project Skeleton

Goal: Create a clean Android app shell that can compile, launch, and navigate between placeholder MVP screens.

Tasks:

- Create the Android project using Kotlin and Jetpack Compose.
- Configure Gradle, Android SDK versions, Kotlin, Compose, and Material 3.
- Add Navigation Compose.
- Create a root app theme based on the STITCH BrickVault palette.
- Add reusable design tokens for colors, spacing, shapes, and status colors.
- Add placeholder routes for:
  - Auth
  - Home
  - My Collection
  - Build Backlog
  - Set Details
  - Add Set
  - Storage
  - Profile
  - Premium placeholder
- Add a main navigation surface suitable for a mobile app.
- Add empty/loading/error UI primitives that can be reused later.
- Use the STITCH screen names and structure as placeholders where possible.

Definition of done:

- App builds successfully.
- App launches into a usable placeholder screen.
- Navigation between top-level MVP screens works.
- No secrets or local machine paths are committed.
- Theme colors and placeholder surfaces visibly resemble the STITCH BrickVault direction.

Suggested verification:

```powershell
.\gradlew assembleDebug
.\gradlew test
```

## Phase 2: Domain Model Foundation

Goal: Define stable app concepts before persistence and UI grow around them.

Tasks:

- Add domain models:
  - `User`
  - `LegoSet`
  - `StorageLocation`
  - `MissingPart`
  - `BuildLog`
- Add enums:
  - `AuthProvider`
  - `PremiumStatus`
  - `SetStatus`
  - `Difficulty`
  - `Priority`
  - `StorageLocationType`
  - `MissingPartStatus`
- Add use cases for:
  - Backlog eligibility
  - Waiting-days calculation
  - Recommended set selection
  - Collection sorting
  - Collection filtering
- Keep time handling explicit and testable.

Definition of done:

- Domain models match the spec.
- Backlog status rules include `SEALED`, `UNBUILT`, `IN_PROGRESS`, `MISSING_PARTS`, and `WAITING_FOR_DISPLAY_SPACE`.
- Recommendation prefers high-priority unbuilt sets, then oldest `addedAt`.
- Unit tests cover the rules above.

Suggested verification:

```powershell
.\gradlew test
```

## Phase 3: Room Local Storage

Goal: Make the app useful offline before adding Supabase.

Tasks:

- Add Room dependencies.
- Create Room entities for:
  - LEGO sets
  - Storage locations
  - Missing parts
  - Build logs
- Add DAOs for create, read, update, soft delete, search, filtering, and sorting.
- Add mappers between Room entities and domain models.
- Add repository interfaces in the domain/data boundary.
- Add local repository implementations backed by Room.
- Add database migrations strategy from version 1 onward.

Definition of done:

- Sets can be stored locally.
- Soft-deleted records are hidden from normal queries.
- Repository tests cover create, update, delete, and query flows.

Suggested verification:

```powershell
.\gradlew test
```

## Phase 4: Collection, Backlog, and Home With Local Data

Goal: Build the core collector experience with real local data.

Tasks:

- Implement Home screen summary:
  - Total sets
  - Unbuilt count
  - Sealed count
  - Longest-waiting set
  - Basic build recommendation
- Implement My Collection:
  - Search by name, set number, and theme
  - Filter by status
  - Filter by theme
  - Filter by storage location
  - Sort by date added, purchase date, piece count, difficulty, priority, and name
- Implement Build Backlog:
  - Show only backlog-relevant statuses
  - Show waiting-days label
  - Sort by oldest in storage, piece count, difficulty, and priority
  - Filter by theme and status
- Use Compose state flows from ViewModels.
- Match the STITCH Home, My Collection, and Build Backlog screenshots closely in layout, spacing, card density, and status treatment.

Definition of done:

- Screens render real local data from repositories.
- Empty states are useful and action-oriented.
- Sorting and filtering behavior is covered by tests.
- UI remains readable for larger collections.
- Visual QA compares each screen against the matching STITCH `screen.png`.

Suggested verification:

```powershell
.\gradlew test
.\gradlew assembleDebug
```

## Phase 5: Add, Edit, Details, and Delete Set Flows

Goal: Let a user manage their collection end to end locally.

Tasks:

- Implement Add Set screen.
- Required fields:
  - Set number or name
  - Status
  - Auto-created `addedAt`
- Optional fields:
  - Name
  - Theme
  - Year
  - Piece count
  - Image URL
  - Purchase date
  - Difficulty
  - Priority
  - Storage location
  - Notes
- Implement Set Details screen.
- Implement Edit Set flow.
- Implement status changes from details.
- Implement delete confirmation using soft delete.
- Add missing-parts list UI on details, even if basic.
- Match the STITCH Add Set and Set Details screenshots for form layout, metadata hierarchy, and action placement.

Definition of done:

- A user can add, view, edit, and delete a set.
- Validation prevents saving an empty set.
- Status changes update Home, Collection, and Backlog.
- Tests cover validation and repository updates.

Suggested verification:

```powershell
.\gradlew test
.\gradlew assembleDebug
```

## Phase 6: Storage Locations

Goal: Support practical organization of physical sets.

Tasks:

- Implement Storage screen.
- Support create, edit, and soft delete for storage locations.
- Support location types:
  - Cabinet
  - Shelf
  - Box
  - Drawer
  - Compartment
  - Other
- Support optional `parentLocationId` in the data model.
- Allow assigning a set to a storage location from Add/Edit Set.
- Show storage location names in Collection, Backlog, and Details where useful.
- Match the STITCH Storage Management screen for hierarchy and compactness.

Definition of done:

- Locations can be managed locally.
- Sets can be assigned and unassigned.
- Deleting a location does not crash screens that referenced it.

Suggested verification:

```powershell
.\gradlew test
.\gradlew assembleDebug
```

## Phase 7: Authentication Foundation

Goal: Add account structure without blocking local-first development.

Tasks:

- Add Supabase client setup without committing secrets.
- Add signed-out, loading, signed-in, and error auth states.
- Add Google Sign-In as the primary MVP path.
- Prepare email sign-in UI space, but do not build it unless requested.
- Create or load the app user after successful sign-in.
- Store current auth state in a repository abstraction.

Definition of done:

- Auth state controls whether the user sees Auth or the main app.
- Returning signed-in users skip the signed-out screen.
- Sign-out works from Profile.
- Missing Supabase config fails gracefully in development.

Suggested verification:

```powershell
.\gradlew test
.\gradlew assembleDebug
```

## Phase 8: Supabase Schema and RLS

Goal: Prepare cloud storage securely before syncing app data.

Tasks:

- Add SQL migrations or documented schema files for:
  - `lego_sets`
  - `storage_locations`
  - `missing_parts`
  - `build_logs`
- Use `user_id` on every business table.
- Enable Row Level Security for every business table.
- Add policies so users can only read/write rows where `user_id` matches their authenticated account.
- Add indexes for `user_id`, `updated_at`, `deleted_at`, status, and common filters.
- Keep schema names aligned with app models.

Definition of done:

- Schema can be applied to Supabase.
- RLS is enabled and tested conceptually or with SQL checks.
- No anonymous cross-user access is possible.

Suggested verification:

```powershell
.\gradlew test
```

## Phase 9: Basic Offline-First Sync

Goal: Sync local Room data with Supabase while preserving offline usability.

Tasks:

- Add sync metadata where needed.
- Upload local creates, updates, and soft deletes.
- Download remote creates, updates, and soft deletes.
- Apply MVP conflict rule: newer `updatedAt` wins.
- Add WorkManager sync job.
- Add manual sync trigger for debugging.
- Track sync status for Profile.

Definition of done:

- Local changes remain available offline.
- Sync can upload and download sets and storage locations.
- Soft deletes sync correctly.
- Sync failures do not lose local data.
- Tests cover create, update, delete, and conflict behavior.

Suggested verification:

```powershell
.\gradlew test
.\gradlew assembleDebug
```

## Phase 10: Profile, Premium Placeholder, and Free Limit

Goal: Prepare product structure without implementing billing.

Tasks:

- Implement Profile screen:
  - User details
  - Auth status
  - Sync status
  - Premium status
  - Sign out
- Implement Premium placeholder screen.
- Add configurable free collection limit, choose 100 unless the product decision changes.
- Show a clear limit message when a free user reaches the cap.
- Do not integrate payments yet.
- Match the STITCH Profile/Premium screenshot while keeping billing inactive.

Definition of done:

- Profile shows real auth and sync state.
- Premium screen is reachable but clearly inactive.
- Free limit logic is isolated and testable.

Suggested verification:

```powershell
.\gradlew test
.\gradlew assembleDebug
```

## Phase 11: UX Polish and Alpha Readiness

Goal: Make the MVP pleasant and reliable enough for early hands-on testing.

Tasks:

- Review navigation flow from signed-out to adding the first set.
- Improve empty states.
- Check long set names, missing images, unknown themes, and large numbers.
- Add loading and error states where repositories or sync can fail.
- Review accessibility labels for buttons and inputs.
- Check light theme contrast.
- Run through a manual alpha checklist.
- Update README with build/run instructions.

Definition of done:

- A tester can install the debug build and manage a small collection.
- README explains setup, Supabase config, and local build commands.
- Known limitations are documented.

Suggested verification:

```powershell
.\gradlew test
.\gradlew assembleDebug
```

## Manual Alpha Checklist

- Fresh install opens signed-out or local-ready state correctly.
- User can sign in.
- User can add a sealed set.
- User can add an unbuilt set.
- Home counts update.
- Backlog shows only relevant statuses.
- Collection search finds by name, number, and theme.
- Sorting by priority works.
- Sorting by piece count works.
- Set details opens correctly.
- Editing a set updates all screens.
- Deleting a set removes it from normal lists.
- Storage location can be created and assigned.
- Profile sign-out works.
- App does not crash when offline.

## Claude Handoff Notes

- Start with Phase 1 and stop after the app skeleton compiles.
- Keep changes small enough that another agent can review them.
- When a phase is done, report:
  - Files created or changed
  - Commands run
  - What passed
  - What remains incomplete
- If a decision is needed, document the default choice and keep moving when it is low-risk.
- Ask before adding major dependencies outside the stack listed above.
