# Kean Local Shop Finder

A team coursework project designed to help Kean University exchange students explore local businesses by rating, price, and business attributes.

The application uses Vue 3, Spring Boot, and MySQL 8. Recommendations are based on filtering rules rather than a trained personalization model.

## Features

- Combined business filters, pagination, and business details.
- Value-for-money, family-friendly, and high-end query presets.
- Rating distributions and monthly rating trends.
- Python scripts for geographic filtering, business-ID filtering, and database import.
- Synthetic demonstration data, with no real Yelp records included.

## My contribution

Yawei Xiao ([YARA-111111](https://github.com/YARA-111111)) was responsible for filtering, validating, and categorizing Yelp business data, and assisted with feature debugging and data presentation. The frontend and backend are team contributions.

## Requirements

JDK 17, Maven 3.9, Node.js 22, Python 3.10 or later, and MySQL 8.

## Local setup

### 1. Prepare a dedicated database

Create a new database named `yelp_recommendation` and a dedicated MySQL user. Run `database/schema.sql` in that database. The schema contains no real records or destructive DROP statements.

### 2. Configure credentials

In each PowerShell terminal used for the backend or importer:

```powershell
$env:DB_USER = "your_database_user"
$env:DB_PASSWORD = "your_database_password"
```

These are placeholders; use your local credentials and do not commit them.

The default connection is localhost on port 3306. Override the backend connection with `DB_URL`. The Python importer supports `DB_HOST`, `DB_PORT`, and `DB_NAME`.

### 3. Import the synthetic example

From the repository root:

```shell
python -m pip install -r scripts/requirements.txt
python scripts/import_data.py --data-dir data/demo
```

The demo business is fictional. Reviews and tips are empty, so empty review lists and trend charts are expected.

### 4. Start the backend

```shell
cd backend
mvn clean package
mvn spring-boot:run
```

### 5. Start the frontend

In a separate terminal, from the repository root:

```shell
cd frontend
npm ci
npm run dev
```

Open the address printed by Vite.

## Data processing

Filter businesses by geographic bounds:

```shell
python scripts/filter_data.py INPUT OUTPUT --bounds 40.665 40.690 -74.255 -74.215
```

Alternatively, use `--business-ids ids.txt`, with one business ID per line, to filter associated records.

The filter accepts JSON arrays and newline-delimited JSON, produces newline-delimited JSON, and refuses to overwrite an existing output file. JSON arrays are loaded into memory; prefer newline-delimited JSON for large inputs.

The importer expects `businesses.json`, `reviews.json`, and `tips.json`; each supports either input format. It does not clear existing tables. Imports commit in batches, so a failure can leave previously committed records. Use a dedicated demonstration database.

Keep independently obtained data that you are authorized to use under the ignored `data/private/` directory. This repository excludes real Yelp data, reviews, user records, and original database exports. Consult the [official Yelp dataset terms](https://www.yelp.com/dataset) before obtaining or using their data.

## Validation

```shell
python scripts/test_data.py
```

Backend packaging, frontend production build, and JSON/JSONL filtering tests have passed. Full database integration testing has not been completed.

## Scope and attribution

This is a local educational demonstration without authentication, not a production-ready public service. Original reports with unverified claims are not included.

See [CONTRIBUTORS.md](CONTRIBUTORS.md). The team has not yet selected an open-source license.
