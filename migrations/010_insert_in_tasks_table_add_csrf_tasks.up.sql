INSERT INTO tasks (course_id, title, description, difficulty, task_order, points, content, solution) VALUES (
      2,
      'Добавление CSRF-токена',
      'Модифицируйте запрос для отправки CSRF-токена',
      'medium',
      1,
      15,
      'async function updateProfile(data) {
        const response = await fetch("/api/profile", {
          method: "POST",
          body: JSON.stringify(data)
        });
        return response.json();
      }',
      'async function updateProfile(data) {
        const csrfToken = getCSRFToken(); // Предполагаем, что токен доступен
        const response = await fetch("/api/profile", {
          method: "POST",
          headers: {
            "X-CSRF-Token": csrfToken,
            "Content-Type": "application/json"
          },
          body: JSON.stringify(data)
        });
        return response.json();
      }'
);

INSERT INTO tasks (course_id, title, description, difficulty, task_order, points, content, solution) VALUES (
      2,
      'Проверка Origin заголовка',
      'Добавьте проверку заголовка Origin на сервере',
      'hard',
      2,
      20,
      'function handleRequest(req, res) {
        // Опасная обработка без проверки Origin
        processRequest(req.body);
        res.send("OK");
      }',
      'function handleRequest(req, res) {
        const allowedOrigins = ["https://yourdomain.com"];
        if (!allowedOrigins.includes(req.headers.origin)) {
          return res.status(403).send("Forbidden");
        }
        processRequest(req.body);
        res.send("OK");
      }'
);
