ALTER TABLE project
    ADD COLUMN IF NOT EXISTS jira_key VARCHAR(100);