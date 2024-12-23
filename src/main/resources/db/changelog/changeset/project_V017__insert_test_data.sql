INSERT INTO project (id, name, description, status, visibility, owner_id)
VALUES
    (1, 'Project Alpha', 'Description for Project Alpha', 'IN_PROGRESS', 'PUBLIC', 1),
    (2, 'Project Beta', 'Description for Project Beta', 'COMPLETED', 'PRIVATE', 2);

INSERT INTO project_stage (project_stage_id, project_stage_name, project_id)
VALUES
    (1, 'Design Phase', 1),
    (2, 'Development Phase', 1),
    (3, 'Testing Phase', 2);

INSERT INTO team (id, project_id)
VALUES
    (1, 1),
    (2, 2);

INSERT INTO task (name, description, status, performer_user_id, reporter_user_id, project_id, stage_id)
VALUES
    ('Design UI', 'Design the user interface', 'IN_PROGRESS', 3, 4, 1, 1),
    ('Implement Backend', 'Develop the backend services', 'TODO', 5, 6, 1, 2),
    ('Test Application', 'Perform application testing', 'DONE', 7, 8, 2, 3);

INSERT INTO team_member (user_id, team_id)
VALUES
    (1, 1),
    (2, 1),
    (3, 2),
    (4, 2);

INSERT INTO project_stage_roles (role, count, project_stage_id)
VALUES
    ('OWNER', 1, 1),
    ('MANAGER', 2, 1),
    ('DEVELOPER', 3, 2);