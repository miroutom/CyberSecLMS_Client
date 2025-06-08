INSERT INTO tasks (course_id, title, description, difficulty, task_order, points, content, solution) VALUES (
      1,
      'Безопасный рендеринг пользовательского ввода',
      'Замените опасный innerHTML на безопасную альтернативу',
      'medium',
      1,
      15,
      'function renderComment(comment) {
        document.getElementById("comment-box").innerHTML = comment;
      }',
      'function renderComment(comment) {
        document.getElementById("comment-box").textContent = comment;
      }'
);


INSERT INTO tasks (course_id, title, description, difficulty, task_order, points, content, solution) VALUES (
      1,
      'Экранирование HTML-сущностей',
      'Реализуйте функцию экранирования HTML-тегов',
      'hard',
      2,
      20,
      'function displayUserInput(input) {
        return input;
      }',
      'function displayUserInput(input) {
        return input.replace(/</g, "&lt;").replace(/>/g, "&gt;");
      }'
);
