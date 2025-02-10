-- Вставка проекта
INSERT INTO project (id, name, description, storage_size, max_storage_size, owner_id, status, visibility, created_at, updated_at)
VALUES (1, 'Test Project', 'Test project description', 0, 2147483648, 1, 'CREATED', 'PUBLIC', NOW(), NOW());

-- Вставка команды (Team)
INSERT INTO team (id, project_id, avatar_key)
VALUES (1, 1, '');

-- Вставка участника команды (TeamMember)
INSERT INTO team_member (id, user_id, nickname, team_id)
VALUES (1, 1, 'Ivan', 1);

-- Вставка ролей для участника (TeamMember roles)
INSERT INTO team_member_roles (team_member_id, role)
VALUES (1, 'OWNER');

INSERT INTO team_member_roles (team_member_id, role)
VALUES (1, 'MANAGER');
