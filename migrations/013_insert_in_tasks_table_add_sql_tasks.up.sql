INSERT INTO tasks (course_id, title, description, difficulty, task_order, points, content, solution) VALUES (
      3,
      'Параметризованные запросы',
      'Замените конкатенацию строк на параметризованный запрос',
      'medium',
      1,
      15,
      'function getUser(username) {
        const query = `SELECT * FROM users WHERE username = "${username}"`;
        return db.query(query);
      }',
      'function getUser(username) {
        const query = "SELECT * FROM users WHERE username = ?";
        return db.query(query, [username]);
      }'
);

INSERT INTO tasks (course_id, title, description, difficulty, task_order, points, content, solution) VALUES (
      3,
      'Использование ORM',
      'Замените прямой SQL запрос на вызов ORM',
      'easy',
      2,
      10,
      'function searchProducts(keyword) {
        const query = `SELECT * FROM products WHERE name LIKE "%${keyword}%"`;
        return db.query(query);
      }',
      'function searchProducts(keyword) {
        return Product.findAll({
          where: {
            name: { [Op.like]: `%${keyword}%` }
          }
        });
      }'
);
