UPDATE project
SET storage_size = 0 
WHERE storage_size IS NULL;

UPDATE project 
SET max_storage_size = 2147483648 --2GB
WHERE max_storage_size IS NULL;

ALTER TABLE project
ALTER COLUMN storage_size SET DEFAULT 0;

ALTER TABLE project
ALTER COLUMN max_storage_size SET DEFAULT 2147483648;