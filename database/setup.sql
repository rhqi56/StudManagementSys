CREATE TABLE IF NOT EXISTS students (
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    course     VARCHAR(50)  NOT NULL,
    year_level VARCHAR(20)  NOT NULL
);

INSERT INTO students (name, course, year_level) VALUES
    ('Juan dela Cruz',   'BSCS', '1st Year'),
    ('Maria Santos',     'BSIT', '2nd Year'),
    ('Jose Rizal',       'BSIS', '3rd Year'),
    ('Andres Bonifacio', 'BSCS', '4th Year');
