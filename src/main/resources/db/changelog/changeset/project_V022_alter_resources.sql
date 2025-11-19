ALTER TABLE project_resource
ADD COLUMN IF NOT EXISTS created_by BIGINT,
ADD COLUMN IF NOT EXISTS updated_by BIGINT;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint 
        WHERE conname = 'fk_resource_created_by'
    ) THEN
        ALTER TABLE project_resource
        ADD CONSTRAINT fk_resource_created_by 
        FOREIGN KEY (created_by) REFERENCES team_member(id);
    END IF;
    
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint 
        WHERE conname = 'fk_resource_updated_by'
    ) THEN
        ALTER TABLE project_resource
        ADD CONSTRAINT fk_resource_updated_by 
        FOREIGN KEY (updated_by) REFERENCES team_member(id);
    END IF;
END $$;

UPDATE project_resource 
SET created_by = (SELECT id FROM team_member LIMIT 1)
WHERE created_by IS NULL 
AND EXISTS (SELECT 1 FROM team_member);

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'project_resource' 
        AND column_name = 'created_by'
        AND is_nullable = 'YES'
    ) THEN
        ALTER TABLE project_resource
        ALTER COLUMN created_by SET NOT NULL;
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_resource_project ON project_resource(project_id);
CREATE INDEX IF NOT EXISTS idx_resource_status ON project_resource(status);
CREATE INDEX IF NOT EXISTS idx_resource_key ON project_resource(key);
CREATE INDEX IF NOT EXISTS idx_allowed_roles_resource ON resource_allowed_roles(resource_id);