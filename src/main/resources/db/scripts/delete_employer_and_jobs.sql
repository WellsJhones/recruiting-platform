-- Delete an employer and all related jobs by employer ID
-- Usage: SET @employerId = 123; (replace 123 with the actual employer ID)

SET @employerId = 123;

DELETE FROM job WHERE employer_id = @employerId;
DELETE FROM employer WHERE _id = @employerId;
