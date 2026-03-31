FROM --platform=linux/amd64 php:7.4-cli

# Install system dependencies
RUN apt-get update && apt-get install -y \
    libpng-dev \
    libjpeg-dev \
    libfreetype6-dev \
    libzip-dev \
    libssl-dev \
    libcurl4-openssl-dev \
    unzip \
    git \
    && rm -rf /var/lib/apt/lists/*

# Install PHP extensions
RUN docker-php-ext-configure gd --with-freetype --with-jpeg \
    && docker-php-ext-install -j$(nproc) \
    gd \
    pdo_mysql \
    mysqli \
    bcmath \
    zip \
    pcntl \
    sockets \
    opcache

# Install Redis extension
RUN pecl install redis && docker-php-ext-enable redis

# Install Swoole extension
RUN pecl install swoole-4.8.13 && docker-php-ext-enable swoole

# Install Composer
COPY --from=composer:2 /usr/bin/composer /usr/bin/composer

WORKDIR /var/www/html

EXPOSE 8324

# Install dependencies then start Swoole server
CMD ["sh", "-c", "composer install --no-dev --no-interaction 2>/dev/null; php think swoole start"]
