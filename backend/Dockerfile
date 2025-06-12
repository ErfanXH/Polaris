FROM python:3.13

# Install system dependencies
RUN apt-get update && apt-get install -y \
    build-essential \
    curl \
    libssl-dev \
    pkg-config \
    git \
    default-libmysqlclient-dev \
    default-mysql-client \
    gcc \
    python3-dev \
    netcat-openbsd \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /back

COPY requirements.txt .

RUN pip install --upgrade pip && pip install -r requirements.txt

COPY . .

# Make sure entrypoint is executable
RUN chmod +x entrypoint.sh

EXPOSE 9000