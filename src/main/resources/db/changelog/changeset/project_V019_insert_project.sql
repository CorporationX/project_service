INSERT INTO project (
    id, name, description, storage_size, max_storage_size, owner_id,
    parent_project_id, created_at, updated_at, status, visibility,
    cover_image_id, presentation_file_key, presentation_generated_at
) VALUES
(1, 'AI Research', 'Project for AI development', 500000, 1000000, 101, NULL, NOW(), NOW(), 'CREATED', 'PUBLIC', NULL, NULL, NULL),
(2, 'Blockchain Initiative', 'Exploring blockchain use cases', 250000, 500000, 102, NULL, NOW(), NOW(), 'CREATED', 'PUBLIC', NULL, NULL, NULL);
