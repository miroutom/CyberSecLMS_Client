CREATE TABLE labs (
                      id INT AUTO_INCREMENT PRIMARY KEY,
                      title VARCHAR(255) NOT NULL,
                      description TEXT NOT NULL,
                      vulnerability_type VARCHAR(50) NOT NULL,
                      difficulty VARCHAR(50) NOT NULL,
                      content TEXT NOT NULL,
                      solution TEXT NOT NULL,
                      lab_archive_url VARCHAR(512),
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO labs (title, description, vulnerability_type, difficulty, content, solution) VALUES
    ('SQL Injection', 'Исправьте уязвимость SQL-инъекции', 'SQL_INJECTION', 'MEDIUM',
     'import pyyaml\nimport sys\nimport os\n\ndef load_config(filename):\n    try:\n        with open(filename, ''r'') as f:\n            data = yaml.load(f, Loader=yaml.Loader)\n            print("Конфигурация загружена:")\n            print(data)\n            return data\n    except FileNotFoundError:\n        print(f"Ошибка: Файл ''{filename}'' не найден.")\n        return None\n    except Exception as e:\n        print(f"Ошибка при загрузке YAML: {e}")\n        return None',
     'import pyyaml\nimport sys\nimport os\n\ndef load_config(filename):\n    try:\n        with open(filename, ''r'') as f:\n            data = yaml.safe_load(f)\n            print("Конфигурация загружена:")\n            print(data)\n            return data\n    except FileNotFoundError:\n        print(f"Ошибка: Файл ''{filename}'' не найден.")\n        return None\n    except Exception as e:\n        print(f"Ошибка при загрузке YAML: {e}")\n        return None');
