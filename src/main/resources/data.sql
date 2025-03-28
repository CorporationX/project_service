INSERT INTO project (id, name, status, visibility)
VALUES (1, 'name', 'CREATED', 'PUBLIC');

INSERT INTO campaign(id, title, description, goal,
amount_raised, status, created_by, updated_by, project_id, currency)
VALUES
(2, 'title', 'description', 1, 0, 'COMPLETED', 1, 1, 1, 'USD'),
(3, 'title2', 'description2', 2, 0, 'ACTIVE', 1, 1, 1, 'EUR'),
(4,'title3', 'description3', 3, 0, 'ACTIVE', 1, 1, 1, 'USD');

INSERT INTO team (id, project_id)
VALUES (1, 1);

INSERT INTO team_member (id, user_id, team_id, nickname)
VALUES (1, 1, 1, 'nick');

INSERT INTO team_member_roles (team_member_id, role)
VALUES (1, 'OWNER');

INSERT INTO task(id, name, description, status, performer_user_id, reporter_user_id, minutes_tracked, created_at,
                 updated_at, parent_task_id, project_id, stage_id)
VALUES (1,
        'task',
        'description',
        'IN_PROGRESS',
        1,
        2,
        null,
        null,
        null,
        null,
        null,
        null);