INSERT INTO project (
    name, description, storage_size, max_storage_size, owner_id,
    parent_project_id, created_at, updated_at, status, visibility,
    cover_image_id, presentation_file_key, presentation_generated_at
) VALUES
('AI Research', 'Project for AI development', 500000, 1000000, 101, NULL, NOW(), NOW(), 'CREATED', 'PUBLIC', NULL, NULL, NULL),
('Blockchain Initiative', 'Exploring blockchain use cases', 250000, 500000, 102, NULL, NOW(), NOW(), 'CREATED', 'PUBLIC', NULL, NULL, NULL);
