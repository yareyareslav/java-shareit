# java-shareit

## Локальный запуск для Postman

### Вариант 1: H2 (по умолчанию)

Запуск приложения из IDE или Maven — профиль `h2` подключается автоматически.

- API: `http://localhost:8080`
- H2 Console: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:shareit`
  - User: `sa`, Password: *(пусто)*

Заголовок для запросов: `X-Sharer-User-Id: 1`

### Вариант 2: PostgreSQL в Docker

```bash
docker compose up -d
```

Запуск с профилем `postgres`:

```bash
# Windows PowerShell
$env:SPRING_PROFILES_ACTIVE="postgres"

# Git Bash / Linux / macOS
export SPRING_PROFILES_ACTIVE=postgres
```

Параметры БД: `localhost:5432`, база `shareit`, логин/пароль `shareit` / `shareit`.
