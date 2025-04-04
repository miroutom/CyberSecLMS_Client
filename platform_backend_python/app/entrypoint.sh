#!/bin/sh
set -e

# Create certs directory if not exists
mkdir -p /usr/src/app/certs

# Generate RSA keys if they don't exist
if [ ! -f "/usr/src/app/certs/jwt-private.pem" ]; then
    echo "Generating RSA keys..."
    openssl genrsa -out /usr/src/app/certs/jwt-private.pem 2048
    openssl rsa -in /usr/src/app/certs/jwt-private.pem -outform PEM -pubout -out /usr/src/app/certs/jwt-public.pem
else
    echo "Keys already exist, skipping generation."
fi

alembic_result=$(alembic current)

# Check if the alembic_result is empty
if [ -z "$alembic_result" ]; then
  echo "No migrations found. Creating an initial migration..."
  alembic revision --autogenerate -m "Initial migration"
else
  echo "Migrations already exist. Skipping migration creation."
fi

# Run Alembic migrations
echo "Applying any pending migrations..."
alembic upgrade head

# Debug launch with 'reload' option
uvicorn main:app --reload --workers 1 --host 0.0.0.0 --port 8000