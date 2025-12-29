# NBA 2K Random Team Generator
### Readme needs an update
### TODOS
1. Big picture
   - (done?)edit profile screen
   - (done?)create posts backend
   - (done?)create posts screen
2. various things to be fixed
    - (done?)sparge Composable-urile mari in bucati mici (Header, List, EmptyState, ErrorState).
    - (done?)adauga loading/empty/error - afiseaza elemente specific pentru siguranta (spinners, empty placeholders, retry).
    - (done?)pune contentDescription pe imagini/icoane, foloseste doar culori din tema (fara hex hardcodate), verifica contrastul si suportul Dark Mode.
    - lazy loading(.limit() & .startAfter())

An Android application that generates fair, random NBA teams and lets users save, revisit, and share matchups through a global posts feed — built with **Kotlin**, **Jetpack Compose (Material 3)**, and **Firebase**.

> **Author:** Stefan Calmac  
> **Android SDK:** min 24 → target 36  
> **Gradle / AGP:** 8.13.2  
> **Java / Kotlin:** 17 / 2.3.0

---

## ✨ Features

- **One‑tap team randomizer**  
  Generate balanced, random NBA teams from a player pool.

- **Save & manage matches**  
  Persist generated matchups to Firestore and revisit them later.

- **Global Posts system**  
  Publish matchups as posts and browse posts from all users via an Explore feed.

- **Create Post workflow**  
  A dedicated Post screen allows creating and publishing posts from:
   - Explore
   - History
   - Favorites

- **Modern UI with Jetpack Compose**  
  Material 3, edge‑to‑edge layouts, and declarative UI patterns.

- **Bottom navigation**  
  Five main routes implemented with Navigation‑Compose:
  `Home`, `Favorites`, `Explore`, `Post`, `Profile`.

- **Firebase Authentication**  
  Email/password authentication with session handling.

- **Real‑time data updates**  
  Firestore snapshot listeners exposed as Kotlin `Flow`s.

- **Scalable architecture**  
  Repository + RemoteDataSource separation, coroutine‑first design.

---

## 🧱 Tech Stack

- **Language:** Kotlin (2.3.0)
- **UI:** Jetpack Compose (Material 3), Navigation‑Compose
- **State Management:** Compose state + `StateFlow`
- **Async:** Kotlin Coroutines + Flow
- **Dependency Injection:** Hilt (KSP)
- **Firebase:**
   - Authentication
   - Cloud Firestore
- **Serialization:** `kotlinx.serialization`
- **Build:** Gradle (KTS), Android Gradle Plugin 8.13.2

---

## 🗄️ Firestore Data Model

### Posts (global collection)

```
posts/{postId}
 ├─ ownerId: String   // Firebase Auth UID
 ├─ title: String
 ├─ json: String      // serialized matchup / content
 └─ timestamp: Long
```

- **Explore feed** queries `/posts` ordered by `timestamp`.
- **My Posts** queries `/posts` filtered by `ownerId`.
- Pagination is implemented using `limit()` and `startAfter()`.

### User‑scoped data

```
users/{userId}
 └─ matches/{matchId}
```

Used for saved matches and history.

---

## 🗺️ App Flow (High‑level)

1. **Authentication**  
   Users sign up or sign in using Firebase Auth.

2. **Home**  
   Entry point to generate random teams.

3. **Randomize**  
   Teams are generated and displayed; users can save the matchup.

4. **History / Favorites**  
   View and manage saved matches.

5. **Post**  
   Create and publish a post (optionally prefilled from a match).

6. **Explore**  
   Browse posts from all users with paginated loading.

7. **Profile**  
   Access personal actions such as My Posts, History, and Sign Out.

---

## 🧪 How to Use

1. Sign up or sign in with email and password.
2. Generate random teams from the Home screen.
3. Save a matchup to your history.
4. Publish a matchup as a post using the Post tab.
5. Explore posts from other users in the Explore tab.
6. Manage your saved matches and posts from Profile / History.

---

## 🚀 Architecture Notes

- **Firestore queries are server‑side and indexed** — the app never loads all posts at once.
- **Pagination** is handled manually with Firestore cursors for simplicity and control.
- **Coroutines over threads** — no manual threading is used anywhere in the app.
- **Compose‑first navigation** with route argument support.

---

## 🛣️ Roadmap Ideas

- Infinite scroll improvements and prefetching
- Likes and comments on posts
- Post moderation and reporting
- Offline caching for matches
- Advanced team balancing (ratings, positions)
- Material You dynamic color
- UI tests and snapshot tests

---

## 🤝 Contributing

Contributions, issues, and suggestions are welcome.
Feel free to open an issue or submit a pull request.

---
