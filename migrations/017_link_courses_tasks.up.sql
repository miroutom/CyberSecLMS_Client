ALTER TABLE courses
ADD COLUMN tasks JSON DEFAULT (JSON_ARRAY()) COMMENT 'Массив объектов задач';

UPDATE courses c
SET c.tasks = (
  SELECT JSON_ARRAYAGG(
    JSON_OBJECT(
      'id', t.id,
      'courseId', t.course_id,
      'title', t.title,
      'description', t.description,
      'difficulty', t.difficulty,
      'order', t.task_order,
      'points', t.points,
      'content', t.content,
      'solution', t.solution,
      'isCompleted', t.is_completed,
      'vulnerabilityType', t.vulnerability_type
    )
  )
  FROM tasks t
  WHERE t.course_id = c.id
);
