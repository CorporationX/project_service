ALTER TABLE project_resource
ADD CONSTRAINT fk_resource_created_by 
FOREIGN KEY (created_by) REFERENCES team_member(id);

ALTER TABLE project_resource
ADD CONSTRAINT fk_resource_updated_by 
FOREIGN KEY (updated_by) REFERENCES team_member(id);

UPDATE project_resource 
SET created_by = (
    SELECT tm.id 
    FROM team_member tm
    JOIN team t ON t.id = tm.team_id
    WHERE t.project_id = project_resource.project_id
    LIMIT 1
)
WHERE created_by IS NULL
AND EXISTS (
    SELECT 1 
    FROM team_member tm
    JOIN team t ON t.id = tm.team_id
    WHERE t.project_id = project_resource.project_id
);

ALTER TABLE project_resource
ALTER COLUMN created_by SET NOT NULL;

CREATE INDEX IF NOT EXISTS idx_resource_project ON project_resource(project_id);
CREATE INDEX IF NOT EXISTS idx_resource_status ON project_resource(status);
CREATE INDEX IF NOT EXISTS idx_resource_key ON project_resource(key);
CREATE INDEX IF NOT EXISTS idx_allowed_roles_resource ON resource_allowed_roles(resource_id);