INSERT INTO project (
    name,
    description,
    storage_size,
    max_storage_size,
    owner_id,
    status,
    visibility,
    cover_image_id,
    created_at,
    updated_at
) VALUES (
    'Test Project',
    'Description for test project',
    1024,
    2048,
    1,
    'IN_PROGRESS',
    'PUBLIC',
    NULL,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO campaign (
    title,
    description,
    goal,
    amount_raised,
    status,
    created_at,
    created_by,
    updated_at,
    updated_by,
    project_id,
    currency,
    deleted
) VALUES
(
    'Campaign 1',
    'This is the first test campaign',
    1000.00,
    250.00,
    'ACTIVE',
    '2023-01-01 10:00:00',
    1,
    '2023-01-02 10:00:00',
    1,
    (SELECT id FROM project WHERE name = 'Test Project'),
    'USD',
    false
),
(
    'Campaign 2',
    'This is the second test campaign',
    2000.00,
    500.00,
    'ACTIVE',
    '2023-02-01 11:00:00',
    2,
    '2023-02-02 11:00:00',
    2,
    (SELECT id FROM project WHERE name = 'Test Project'),
    'USD',
    false
),
(
    'Campaign 3',
    'This is the third test campaign',
    1500.00,
    1500.00,
    'COMPLETED',
    '2023-03-01 12:00:00',
    2,
    '2023-03-02 12:00:00',
    3,
    (SELECT id FROM project WHERE name = 'Test Project'),
    'USD',
    true
);
