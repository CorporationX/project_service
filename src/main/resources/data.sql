insert into team_member(id, user_id, team_id, role)
VALUES (1, 1, 1, 'DEVELOPER');

insert into team(id, team_member_id, project_id)
VALUES (1, 1, 1);

insert into task(id, name, description, status, performer_user_id, reporter_user_id, minutes_tracked, created_at,
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