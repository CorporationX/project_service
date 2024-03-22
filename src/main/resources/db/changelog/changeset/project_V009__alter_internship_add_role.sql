ALTER TABLE internship
ADD COLUMN internship_role varchar(20) NOT NULL;

--
--create table if not exists internship_roles
--(
--    internship_id bigint,
--    role           varchar(20) NOT NULL,
--    CONSTRAINT fk_internship_roles FOREIGN KEY (internship_id) REFERENCES internship (id)
--);
