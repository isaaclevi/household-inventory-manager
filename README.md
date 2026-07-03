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

- `GET/POST /api/item-types` — product kinds ("2% Milk"); `?search=` and `?category=` filters
- `GET/POST /api/items` — physical items in the house, soonest expiration first
- `GET /api/items/expiring-soon?days=3` — expiring or expired items
- `POST /api/items/{id}/consume` — mark an item used up
- `GET/POST /api/shopping-list`, `POST /api/shopping-list/{id}/purchase`

## Roadmap

- **Phase 1 (current):** manual entry, expiration dashboard, shared shopping list, then auth (Spring Security + JWT) and real-time sync (WebSocket)
- **Phase 2:** photo capture → Claude API (server-side) identifies product + expiration date; voice input
- **Phase 3:** recipes (TheMealDB), nutrition (USDA), recall alerts (openFDA)
