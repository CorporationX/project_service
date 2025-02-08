INSERT INTO project(name, description, owner_id, status, visibility)
VALUES ('project', 'description', 1, 'ACTIVE', 'PUBLIC');

insert into team(project_id)
VALUES  (1);

insert into team_member(user_id, team_id, nickname)
VALUES (1, 1, 'user');

INSERT INTO team_member_roles(team_member_id, role)
VALUES (1, 'DEVELOPER');

insert into task(name, description, status, performer_user_id, reporter_user_id, minutes_tracked, created_at,
                 updated_at, parent_task_id, project_id, stage_id)
VALUES (
        'task',
        'description',
        'IN_PROGRESS',
        1,
        2,
        null,
        null,
        null,
        null,
        1,
        null);