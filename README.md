# Household Inventory Manager

AI-powered household inventory management system. Track what's in the house, get expiration alerts, and share a shopping list across the household. See [project-requirements.md](project-requirements.md) for the full spec and phased roadmap.

## Structure

| Directory | What it is | Stack |
|---|---|---|
| `backend/` | REST API | Java 21, Spring Boot, Spring Data JPA (H2 in dev, PostgreSQL in prod) |
| `frontend/` | Web UI | Angular (standalone components, signals) |

## Running locally

**Backend** (starts on http://localhost:8080, H2 file database in `backend/data/`):

```sh
cd backend
./mvnw spring-boot:run
```

**Frontend** (starts on http://localhost:4200, calls the API on :8080 — CORS is preconfigured):

```sh
cd frontend
npm install
npm start
```

## API (Phase 1)

All endpoints except `/api/auth/**` require a JWT: `Authorization: Bearer <token>`.

**Auth**
- `POST /api/auth/register` — `{username, password, displayName}` → `{token, username, displayName}`
- `POST /api/auth/login` — `{username, password}` → `{token, username, displayName}`

**Inventory**
- `GET/POST /api/item-types` — product kinds ("2% Milk"); `?search=` and `?category=` filters
- `GET/POST /api/items` — physical items in the house, soonest expiration first
- `GET /api/items/expiring-soon?days=3` — expiring or expired items
- `POST /api/items/{id}/consume` — mark an item used up

**Shopping lists** (creator = owner; owner grants add/edit/delete per member)
- `GET/POST /api/lists` — lists you own or are a member of; create a list
- `GET/POST /api/lists/{id}/permissions`, `DELETE .../permissions/{username}` — owner only
- `GET/POST /api/lists/{id}/items` — view (members) / add (needs canAdd); items carry device-generated UUIDs
- `PUT /api/lists/{id}/items/{itemId}` — edit (needs canEdit)
- `POST /api/lists/{id}/items/{itemId}/purchase` — check off (needs canEdit)
- `DELETE /api/lists/{id}/items/{itemId}` — tombstone, not hard delete (needs canDelete)

## Roadmap

- **Phase 1 (current):** manual entry, expiration dashboard, shopping lists — CRUD ✅, auth + permissions ✅ (backend); next: Angular login UI + list-scoped shopping list page
- **Phase 1.5:** offline-first sync — PWA + IndexedDB on devices, user-arbitrated merge dialog, permission-gated (spec in project-requirements.md)
- **Phase 2:** photo capture → Claude API (server-side) identifies product + expiration date; voice input
- **Phase 3:** recipes (TheMealDB), nutrition (USDA), recall alerts (openFDA)

> Note: the Angular app still targets the old open endpoints and needs its login UI next — until then, API calls from the frontend return 401.
