<div style="text-align: center;">
  <img src="/Users/hacker/IdeaProjects/CorporationX/project_service/src/main/java/faang/school/projectservice/controller/calendar/readme/calendar-diagram.svg" 
       alt="Diagram of API Flow" width="800"/>
</div>

**Примечание:** Данная диаграмма **не включает точки принятия решений** (ромбы), такие как:

- **Решение 1:** “Успешна ли авторизация OAuth?”
    - **Да:** Продолжить обмен кода на access token.
    - **Нет:** Показать сообщение об ошибке пользователю.

- **Решение 2:** “Действителен ли access token?”
    - **Да:** Выполнить операции с календарем.
    - **Нет:** Обновить токен или запросить повторную авторизацию у пользователя.

#### и тд
Диаграмма **предполагает, что все операции выполняются без ошибок** и не рассматривает обработку исключений.

# API для управления Google Календарем в Project Service

## Описание

Этот API позволяет управлять календарями и событиями Google через проектный сервис. С помощью API можно:
- Авторизовать проект в Google Calendar.
- Создавать календари и события.
- Управлять событиями и правами доступа (ACL).
- Удалять календари и события.

---

## Использование API

### Важно: Авторизация через Google в Postman

Postman не перенаправляет автоматически на страницу авторизации и возвращает HTML-страницу. Поэтому для успешной авторизации выполните следующие действия:

1. Запустите ваш **Project Service** и перейдите на страницу:  
   [http://localhost:8082/projects/calendars/auth](http://localhost:8082/projects/calendars/auth).

2. После успешной авторизации вы будете перенаправлены на:  
   `http://localhost:8888/Callback?code=<код_доступа>&scope=...`

3. Скопируйте **код доступа** из URL и используйте его в следующем запросе:

   ```http
   POST /projects/{projectId}/calendars/auth?code=<код_доступа>
    ```
Это привяжет проект к вашему Google-аккаунту. На один Google-аккаунт может быть привязан только один проект.


## Эндпоинты API

### 1. Авторизация и учетные данные проекта

#### Установка учетных данных проекта

   ```http
   POST /projects/{projectId}/calendars/auth
   ```

- Параметры:
   - projectId (Path) — ID проекта.
   - code (Query) — Код доступа из шага авторизации.

### 2. Создание и управление календарями
#### Создать календарь
   ```http
   POST /projects/{projectId}/calendars
   ```


#### Тело запроса (JSON):
   ```json
   {
      "summary": "Командный календарь",
      "description": "Календарь для внутренних встреч",
      "location": "Москва"
   }
   ```

#### Получить календарь по ID
   ```http
   GET /projects/{projectId}/calendars?calendarId=<calendarId>
   ```

### 3. Работа с событиями
#### Создать событие
   ```http
   POST /projects/{projectId}/calendars/events
   ```

- Параметры:
    - projectId (Path) — ID проекта.
    - calendarId (Query)
    - 
#### Тело запроса (JSON):
   ```json
   {
      "summary": "Встреча с командой",
      "description": "Обсуждение планов",
      "location": "Офис",
      "startTime": "2024-10-14T10:00:00",
      "endTime": "2024-10-14T11:00:00"
   }
   ```

#### Получить список событий
   ```http
   GET /projects/{projectId}/calendars/events?calendarId=<calendarId>
   ```

- Параметры:
    - projectId (Path) — ID проекта.
    - calendarId (Query)

#### Удалить событие
   ```http
   DELETE /projects/{projectId}/calendars/events/{eventId}?calendarId=<calendarId>
   ```

- Параметры:
    - projectId (Path) — ID проекта.
    - calendarId (Query)

### 4. Управление правами доступа (ACL)
#### Создать правило доступа
   ```http
   POST /projects/{projectId}/calendars/acl
   ```

- Параметры:
    - projectId (Path) — ID проекта.
    - calendarId (Query)

#### Тело запроса (JSON):
   ```json
   {
     "role": "writer",
     "scope": {
       "type": "user",
       "value": "user@example.com"
     }
   }
```
#### Получить список правил доступа
   ```http
   GET /projects/{projectId}/calendars/acl?calendarId=<calendarId>
   ```

- Параметры:
    - projectId (Path) — ID проекта.
    - calendarId (Query)

#### Удалить правило доступа
   ```http
   DELETE /projects/{projectId}/calendars/acl?calendarId=<calendarId>&aclId=<aclId>
   ```

- Параметры:
    - projectId (Path) — ID проекта.
    - calendarId (Query)



