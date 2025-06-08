#!/bin/sh

echo "Waiting for MySQL to be ready..."
sleep 20

echo "Running migrations..."
python manage.py migrate

echo "Collecting static files..."
python manage.py collectstatic --noinput

echo "Starting Django server..."
python manage.py runserver 0.0.0.0:9000