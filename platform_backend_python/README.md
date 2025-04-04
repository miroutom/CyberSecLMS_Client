## Двухфакторная система авторизации с JWT-токенами и TOTP.

### Info

Система запускается через docker-compose на порту 8000,
PostgreSQL на порту 5432.

Для генерации QR-кода нужен **username и password**.

При сканировании QR-кода в мобильном приложении аутентификации 
(например, Google Authenticator) будут генерироваться time-based
one-time passwords.

Для входа в систему (login) нужны **username, password, security_code**.

security_code - это и есть time-based one-time password.

Проверка JWT-токенов выполняется с помощью алгоритма
**RS256** используя публичный и приватный ключи.

JWT-токены не хранятся в базе данных.

В разделе Endpoints 
приведен пример использования 2FA.

#### Tokens lifetime

access token lifetime = 60 min

refresh token lifetime = 60 days

totp lifetime = 30 seconds


### Launch

> `cd .../plaform_backend_python`
> 
> `docker-compose up --build`


### API

#### Endpoints

- Создать нового пользователя

  BODY:
  ```
  {
    "username": "test_user1",
    "email": "test_email@gmail.com",
    "password1": "test_password1",
    "password2": "test_password1"
  }
  ```
  
  **POST "127.0.0.1:8000/api/v1/user"**

  При успешном ответе (status = 201)
  отправляется инфа о созданном пользователе.
  
  ```
  {
    "created_at": "2025-02-13T15:15:41.760573Z",
    "username": "test_user1",
    "is_active": true,
    "email": "test_email@gmail.com",
    "uuid": "5c6b4008-1f70-4a38-8e95-a04f456d692b"
  }
  ```

- Сгенерировать QR-код
  
  BODY:
  
  ```
  {
    "username": "test_user1",
    "password": "test_password1"
  }
  ```
  
  **POST "127.0.0.1:8000/api/v1/auth-totp/qrcode"**
  
  При успешном ответе (status = 201) отправляется QR-код.

- Получить инфу по любому пользователю по его username

  **GET "127.0.0.1:8000/api/v1/user/{username}"**

  При успешном ответе (status = 200)
  отправляется **публичная** инфа о пользователе.

  ```
  {
    "created_at": "2025-02-13T15:15:41.760573Z",
    "updated_at": null,
    "username": "test_user1",
    "is_active": true
  }
  ```

- Авторизация 
    
  BODY:
  ```
  {
    "username": "test_user1",
    "password": "test_password1",
    "security_code": "862413"
  }
  ```

  **POST "127.0.0.1:8000/api/v1/auth-jwt/login"**

  При успешном ответе (status = 200) отправляются 
  access и refresh токены как в response body, так и в Cookies. 

  ```
  {
    "access_token": "{access_token}",
    "refresh_token": "{refresh_token}",
    "token_type": "Bearer"
  }
  ```
  

- Получение информации о текущем пользователе по 
  access токену.
  Можно отправить access_token в Cookies или в Headers.
  
  COOKIES:
  ```
  {
    "access_token": "{access_token}"
  }
  ```
  
  HEADERS:
  ```
  Authorization: Bearer {access_token}
  ```
  
  **GET "127.0.0.1:8000/api/v1/auth-jwt/user-info"**

  При успешном ответе (status = 200) 
  отправляется вся инфа о пользователе.

  ```
  {
    "created_at": "2025-02-13T15:15:41.760573Z",
    "updated_at": null,
    "username": "test_user1",
    "email": "test_email@gmail.com",
    "is_active": true,
    "uuid": "5c6b4008-1f70-4a38-8e95-a04f456d692b"
  }
  ```
  
- Обновление access токена через refresh токен.
  
  Можно отправить refresh_token в Cookies или в Headers.
  
  COOKIES:
  ```
  {
    "refresh_token": "{refresh_token}"
  }
  ```
  
  HEADERS:
  ```
  Authorization: Bearer {refresh_token}
  ```
  
  **POST "127.0.0.1:8000/api/v1/auth-jwt/refresh"**

  При успешном ответе (status = 200)
  отправляется новый access_token 
  как в BODY, так и в Cookies.

  ```
  {
    "access_token": "{access_token}",
    "token_type": "Bearer"
  }
  ```