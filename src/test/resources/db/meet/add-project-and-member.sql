WITH project_insert AS (
    INSERT INTO project (name, description, owner_id, status, visibility)
    VALUES ('project1', 'project description', 1, 'IN_PROGRESS', 'PUBLIC')
    RETURNING id
)
, team_insert AS (
    INSERT INTO team (project_id)
    SELECT id FROM project_insert
    RETURNING id
)
INSERT INTO team_member (user_id, team_id)
SELECT 1, id FROM team_insert;
