# AI-Powered Support Ticket Triage (Rule-Based)

This is a take-home style full-stack app that triages support tickets using local, rule-based logic (no external AI APIs).
It persists analyzed tickets in an in-memory H2 database and exposes a small REST API for a plain vanilla JS frontend.

## Endpoints

`POST /tickets/analyze`
* Request: `{ "message": "..." }`
* Response: category, priority, extracted keywords, urgency signals, signals, confidence, and the stored ticket id.

`GET /tickets`
* Returns all stored tickets sorted by latest first.

## Project Structure

* `backend/` Spring Boot REST API + rule-based analyzer + JPA persistence (H2)
* `frontend/` Plain HTML/CSS/Vanilla JS UI (no React)
* `docker-compose.yml` Starts `backend` + a small `nginx` static server for the frontend

## Run (Docker)

```bash
docker-compose up --build
```

* Backend: `http://localhost:8080`
* Frontend UI: `http://localhost:8081`

## Run (Backend only, local)

```bash
cd backend
mvn test
mvn spring-boot:run
```

## How the “AI” Works (Explainable Rules)

The backend analyzer is split into:
* `controller` (HTTP)
* `service` (business orchestration + persistence)
* `analyzer` (classification + priority + confidence)
* `rules` (config-like keyword maps)
* `repository` (DB)
* `model/entity` (Ticket)

### Classification (keyword-based)
The analyzer checks the message (lowercased) for configured keywords per category and chooses the category with the most matches.

### Urgency Detection
It searches for urgency terms like `urgent`, `asap`, and `down`.
Each matched urgency term adds a weight into priority scoring.

### Priority Scoring
Priority is driven by:
* urgency keyword weights
* a small base score per category type

Priority thresholds:
* score >= 10 => `P0`
* score >= 7  => `P1`
* score >= 4  => `P2`
* else         => `P3`

### Confidence Score
Confidence is computed from the number of unique matched “signals”:
* signals = matched category keywords + matched urgency keywords (+ a custom-rule signal when it triggers)
* more matched signals => higher confidence (capped for stability)

## Custom Rule (Required)

This stands out and is intentionally simple:
* If the message contains `refund`, then:
  * `category = Billing`
  * `priority = P0`

This custom rule is implemented in `RuleBasedTicketAnalyzer` and also adds a dedicated signal string so you can see it in the UI result.

## Reflection (Short)

Design trade-offs:
* I kept the analyzer rule-based and transparent to match the “interview-friendly” requirement.
* I chose keyword substring checks (instead of NLP libraries) for reliability and simplicity.
* I persisted extracted keywords + urgency signals + overall signals so the “why” remains visible after refresh.

Limitations:
* Keyword matching can misclassify edge cases (no stemming, no semantic understanding).
* Confidence is heuristic and not calibrated like a real ML probability.

What I would improve with more time:
* Add more curated keyword patterns and more targeted unit tests per category.
* Expand normalization (tokenization, punctuation handling) while keeping the logic explainable.
* Add pagination to `GET /tickets` if the dataset grows.

