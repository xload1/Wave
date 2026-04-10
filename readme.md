# Wave Backend

Backend for **Wave** — a music-based matching application where users discover people with similar taste, swipe to like/pass, get matches, and chat after a mutual like.

This repository contains the **server-side** part of the project built with **Java + Spring Boot**.

---

## Overview

Wave is a backend for a mobile-first social application focused on **music taste compatibility**.

The main idea is simple:
- users select favorite and liked tracks,
- users select favorite artists,
- the system builds recommendations based on shared musical preferences,
- users can swipe **LIKE** or **PASS** on recommended people,
- a **match** appears after mutual LIKE,
- matched users can exchange encrypted messages.

In addition to internal preference management, the backend integrates with the **Spotify Web API** for:
- track search,
- artist search,
- track details,
- artist details,
- artist top tracks,
- importing tracks and artists into the local database.

---

## Main Features

### Authentication and profile
- User registration with display name, email, and password hash.
- Session-based authentication.
- Password change.
- Profile editing:
  - display name,
  - description.

### Music preference management
- Search tracks in Spotify.
- Search artists in Spotify.
- Import selected Spotify tracks and artists into the local database.
- Save track preference as `FAVORITE` or `LIKE`.
- Save/remove artist preferences.
- View all saved preferences.

### Recommendations
- Recommendation feed built from user music preferences.
- Shared taste explanations in recommendation cards:
  - shared favorite tracks,
  - shared liked tracks,
  - shared artists.
- Users already swiped are moved to the end of the feed instead of being shown first again.

### Swipes and matches
- Swipe reactions: `LIKE` and `PASS`.
- Mutual `LIKE` creates a logical match.
- Retrieve the current user's match list.

### Messaging
- Only matched users can exchange messages.
- Messages are encrypted before saving to the database.
- Message history is decrypted on read.

---

## Recommendation Logic

The recommendation layer combines two ideas.

### 1. Direct overlap of music preferences
The strongest signals are:
- shared favorite tracks,
- shared liked tracks,
- shared explicitly selected artists.

These direct overlaps are also used to build the **card similarities** shown in the recommendation cards.

### 2. Graph-based compatibility score
A bipartite graph is built from:
- user vertices,
- track vertices,
- edges representing user-track preferences.

The backend then performs a bounded traversal over this graph to estimate similarity between users.

Important characteristics of the algorithm:
- `FAVORITE` edges are stronger than `LIKE` edges,
- short paths are much more important than long paths,
- direct double-favorite overlap is the strongest signal,
- already-swiped users are sent to the tail of the feed,
- a small controlled randomization (Gumbel randomization) is applied to avoid completely static ordering.

This gives a result that is stronger than a pure "same tracks count" baseline, while still remaining explainable.

---

## Technology Stack

- **Java 21+**
- **Spring Boot**
- **Spring Web**
- **Spring Data JPA**
- **Spring Security**
- **Spring Cache**
- **PostgreSQL**
- **Flyway**
- **Spotify Web API**
- **Lombok**

---

## Project Architecture

The backend is organized around standard Spring layers.

### Controllers
Expose REST API endpoints for:
- auth,
- profile,
- Spotify search/import,
- recommendations,
- swipes,
- matches,
- messages.

### Services
Contain business logic such as:
- authentication,
- preference management,
- Spotify integration,
- recommendation generation,
- swipe logic,
- messaging and encryption.

### Repositories
JPA repositories for persistence.

### Entities
Represent database tables:
- `user_account`
- `artist`
- `track`
- `user_track_preference`
- `user_artist_preference`
- `user_swipe`
- `user_message`

### DTOs / Views
Used to keep API contracts separated from JPA entities.

---

## Database Model

### `user_account`
Stores application users.

Fields include:
- `id`
- `display_name`
- `email`
- `password_hash`
- `description`
- `created_at`

### `artist`
Stores imported artists.

### `track`
Stores imported tracks.

### `user_track_preference`
Stores user-track preference with `FAVORITE` or `LIKE`.

### `user_artist_preference`
Stores explicitly selected artists.

### `user_swipe`
Stores reaction from one user to another: `LIKE` or `PASS`.

### `user_message`
Stores encrypted messages between matched users.

---

## Security

### Password storage
Passwords are **not encrypted** and are **never stored in plain text**.
Instead, they are stored as **hashes** using Spring Security's password encoder.

### Session authentication
The application uses **session-based authentication**:
- user logs in with email and password,
- the backend creates an authenticated session,
- subsequent requests use the session cookie.

### Message encryption
Messages are encrypted at rest using:
- **AES-GCM**,
- a random nonce per message,
- a secret key provided via environment variable.

This is **server-side encryption at rest**, not end-to-end encryption.
The server can decrypt messages when needed.

---

## Spotify Integration

The backend uses the Spotify Web API for:
- track search,
- artist search,
- track details,
- artist details,
- artist top tracks.

### Notes
- Spotify track popularity has become unreliable / unavailable for many track responses in recent API behavior, so the backend treats it as optional.
- Tracks and artists are imported into the local database on demand.
- Imported rows are identified by:
  - `external_source = 'spotify'`
  - `external_id = <spotify_id>`

---

## Caching

Caching is used to reduce unnecessary repeated work for:
- Spotify search results,
- Spotify object lookups,
- recommendation lists,
- card similarities,
- match lists.

The current project uses Spring Cache with a simple in-memory cache provider.

---

## API Areas

The backend exposes endpoints grouped by responsibility.

### Auth
- register
- login
- current session user
- change password
- logout

### Profile
- get current profile
- update display name / description

### Spotify
- search tracks
- search artists
- get track by Spotify id
- get artist by Spotify id
- get artist top tracks
- add/remove track preferences by Spotify id
- add/remove artist preferences by Spotify id

### Recommendations
- get recommendation feed
- react to recommendations with LIKE / PASS

### Matches
- get matches
- get conversation with a match
- send message to a match

---

## Running the Project Locally

### 1. Requirements
Make sure the following are available:
- Java 21+
- Maven
- PostgreSQL
- Spotify developer credentials

### 2. Environment variables
Set the following variables before running the backend:

```text
SPOTIFY_CLIENT_ID=<your_spotify_client_id>
SPOTIFY_CLIENT_SECRET=<your_spotify_client_secret>
MESSAGE_ENCRYPTION_KEY=<base64_encoded_aes_key>
```

### 3. Database configuration
Configure PostgreSQL connection in `application.properties`.

Example:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/wavedb
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=validate
spring.flyway.enabled=true
spring.cache.type=simple
spotify.client-id=${SPOTIFY_CLIENT_ID}
spotify.client-secret=${SPOTIFY_CLIENT_SECRET}
message.encryption-key=${MESSAGE_ENCRYPTION_KEY}
```

### 4. Flyway migrations
On startup, Flyway creates and updates the schema automatically.

### 5. Run the application
Run the Spring Boot application normally from IDE or with Maven.

---

## Frontend Integration Notes

This backend is intended to be consumed by a separate frontend application.

If the frontend runs on another origin (for example `localhost:5173`) and the backend runs on `localhost:8080`, then:
- CORS must allow the frontend origin,
- credentials must be enabled,
- frontend requests must send cookies.

This is important because authentication is session-based.

---

## Typical User Flow

1. User registers.
2. User logs in.
3. User selects initial music preferences.
4. User opens the recommendation feed.
5. User sees compatibility cards with shared music explanations.
6. User reacts with `LIKE` or `PASS`.
7. Mutual `LIKE` creates a match.
8. Matched users exchange encrypted messages.

---

## Limitations / Current Scope

This is a diploma-level backend MVP, not a production-grade commercial system.

Current limitations include:
- no JWT-based auth, only session auth,
- no avatar upload,
- no end-to-end encryption,
- no advanced moderation or abuse protection,
- no delivery/read receipts in chat,
- simple in-memory cache instead of distributed cache.

These limitations are intentional to keep the project focused and explainable.

---

## Possible Future Improvements

- user avatars,
- recommendation cooldown / reshuffle strategy for passed users,
- better analytics for recommendation quality,
- Redis cache,
- JWT auth,
- media messages,
- richer profile settings,
- recommendation tuning dashboard,
- more advanced graph scoring and clustering.

---

## Why This Project Is Interesting

This project is more than a simple CRUD application because it combines:
- backend engineering,
- relational data modeling,
- third-party API integration,
- recommendation logic,
- session authentication,
- encrypted message storage,
- mobile-oriented product flow.

It is a good fit for a diploma project because it is:
- practical,
- explainable,
- modular,
- large enough to document thoroughly,
- but still realistic to implement fully.

---

## Author

Wave backend was developed as a diploma project by Marat Tsasiuk for WSPA  University.
