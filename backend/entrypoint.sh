#!/bin/sh

# Wait for MySQL to be ready
echo "Waiting for MySQL to be ready..."
while ! nc -z database 3306; do
  sleep 1
done
echo "MySQL is ready!"

echo "Running migrations..."
python manage.py migrate

echo "Collecting static files..."
python manage.py collectstatic --noinput

echo "Starting Django server..."
python manage.py runserver 0.0.0.0:9000