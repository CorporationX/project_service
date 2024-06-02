INSERT INTO project (name, description, owner_id, status, visibility)
VALUES ('Market', 'Market description', 1, 'CREATED', 'PUBLIC');

UPDATE project
SET owner_id    = 1,
    description = 'Supershop description'
WHERE id = 1;

UPDATE project
SET owner_id    = 2,
    description = 'Warehouse description'
WHERE id = 2;

UPDATE project
SET owner_id    = 3,
    description = 'Mobile App description'
WHERE id = 3;