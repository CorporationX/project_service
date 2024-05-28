INSERT INTO project (name, status, visibility)
VALUES
    ('Supershop', 'IN_PROGRESS', 'PUBLIC'),
    ('Warehouse', 'IN_PROGRESS', 'PUBLIC'),
    ('Mobile App', 'IN_PROGRESS', 'PRIVATE');

INSERT INTO team (project_id)
VALUES
    (1),
    (2),
    (3);

INSERT INTO team_member (user_id, team_id)
VALUES
    (1, 1),
    (2, 1),
    (3, 2),
    (4, 2),
    (5, 3);

INSERT INTO team_member_roles (team_member_id, role)
VALUES
    (1, 'MANAGER'),
    (2, 'DEVELOPER'),
    (3, 'DEVELOPER'),
    (4, 'TESTER'),
    (5, 'INTERN');


INSERT INTO candidate (user_id)
VALUES (2),
       (3),
       (4),
       (5),
       (6);

INSERT INTO task (name, status, performer_user_id, reporter_user_id, project_id)
VALUES
    ('Task1', 'TODO', 2, 1, 1),
    ('Task2', 'TODO', 3, 1, 1),
    ('Task3', 'TODO', 4, 1, 1),
    ('Task4', 'DONE', 5, 1, 1);


INSERT INTO vacancy (name, description, project_id, created_by, status, count)
VALUES
    ('Java Developer', 'Java developer in International company', 1, 1, 'OPEN', 5);

INSERT INTO vacancy_skills (vacancy_id, skill_id)
VALUES
    (1, 1),
    (1, 2),
    (1, 3);