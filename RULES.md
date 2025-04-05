# 📘 RULES.md — Coding Guidelines for `project_service`

## 📦 Общая структура проекта

1. Проект организован по слоям:
    - `client`
    - `controller`
    - `service`
    - `repository`
    - `dto`
    - `model` / `entity`
    - `config`
    - `mapper`
    - `exception`

Каждый слой отвечает только за свою зону ответственности. Не смешиваем логику между слоями.

---

## 🧱 Entity / Model

- Используем JPA-аннотации: `@Entity`, `@Id`, `@Table`, `@Column`, `@ManyToOne`, и т.д.
- Все связи по умолчанию — `LAZY`: `fetch = FetchType.LAZY`
- Используем Lombok: `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`
- Не использовать `@Data` в Entity-классах (во избежание проблем с JPA)
- Используем `UUID` как идентификатор, если это целесообразно

---

## 🔁 DTO

- Именование: `UserDto`, `RequestDto` — **с большой D и маленькими to**
- DTO реализуются по возможности как `record`
- Добавляем `@Builder` (Lombok или вручную)
- DTO не содержит бизнес-логики
- Добавляем валидацию через `javax.validation` (`@NotNull`, `@Size`, `@Email` и т.д.)

---

## 🛠 Mapper

- Используем **MapStruct** или ручной маппинг
- Названия: `XxxMapper`
- Отвечает за маппинг `Entity ↔ Dto`
- Мапперы размещаем в пакете `mapper`

---

## 💼 Сервисный слой

- Название классов: `XxxService`
- Методы должны быть атомарными и говорящими: `getUserById`, `createProject`
- Бизнес-логика должна находиться исключительно в сервисах
- Используем JPA или `JdbcTemplate`, но избегаем их смешивания в одном методе

---

## 🌐 Контроллеры

- Названия: `XxxController`
- Используем `@RestController`, `@RequestMapping`, `@GetMapping`, `@PostMapping` и т.п.
- Все входящие и исходящие данные — только через DTO
- Ответы формируются с использованием `ResponseEntity`

---

## 🔄 Работа с Базой Данных

- Для миграций используется **Liquibase**
- Файлы миграций: `src/main/resources/db/changelog`
- Нестандартные запросы можно реализовать через `JdbcTemplate`
- Не размещать SQL-запросы в коде — использовать `.sql` файлы или `@Query`

---

## 🧪 Тестирование

- Фреймворки: `JUnit 5`, `Mockito`, `Testcontainers`
- Контроллеры тестируем через `MockMvc`
- Используем интеграционные тесты с PostgreSQL и Redis в контейнерах
- Расположение тестов: `src/test/java/...`

---

## ⚙️ Сборка и окружение

- Сборка: **Gradle Kotlin DSL**
- Локальный запуск через Docker Compose (`docker-compose.yml`)
- Поддерживается `.env` файл для переменных окружения

---

## 🧼 Стиль кода

- Стиль: CheckStyle-IDEA + Kotlin conventions (для `build.gradle.kts`)
- CamelCase для переменных и методов, PascalCase для классов
- Использовать `final` для неизменяемых полей
- Форматирование — через IDE или pre-commit hook

---

## 🚨 Обработка ошибок

- Все исключения должны быть обработаны и проброшены как кастомные (например, `NotFoundException`)
- Глобальный хендлер в пакете `exception` (`@ControllerAdvice`)

---

## 📈 Git и Pull Requests

- Оформлять PR по формату: `код-задачи:feature/описание`, `код-задачи:fix/описание`,
- Описания PR должны быть информативными
- Ветка `titan-master-stream9` — основная

---

Если что-то меняется — не забываем обновить этот файл ✍️
