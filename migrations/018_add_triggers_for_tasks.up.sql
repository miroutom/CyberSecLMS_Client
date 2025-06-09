CREATE TRIGGER after_task_insert
AFTER INSERT ON tasks
FOR EACH ROW
BEGIN
  UPDATE courses
  SET tasks = (
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
    WHERE t.course_id = NEW.course_id
  )
  WHERE id = NEW.course_id;
END;

CREATE TRIGGER after_task_update
AFTER UPDATE ON tasks
FOR EACH ROW
BEGIN
  UPDATE courses
  SET tasks = (
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
    WHERE t.course_id = OLD.course_id
  )
  WHERE id = OLD.course_id;
END;

CREATE TRIGGER after_task_delete
AFTER DELETE ON tasks
FOR EACH ROW
BEGIN
  UPDATE courses
  SET tasks = (
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
    WHERE t.course_id = OLD.course_id
  )
  WHERE id = OLD.course_id;
END;
