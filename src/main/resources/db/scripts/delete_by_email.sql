-- MySQL procedure to delete all references to a user or employer by email
-- Usage: CALL delete_by_email('user_or_employer@email.com');

DELIMITER //
CREATE PROCEDURE delete_by_email(IN target_email VARCHAR(255))
BEGIN
    DECLARE user_id BIGINT;
    DECLARE emp_id BIGINT;

    -- Find user id by email
    SELECT _id INTO user_id FROM user WHERE email = target_email LIMIT 1;
    -- Find employer id by email
    SELECT _id INTO emp_id FROM employer WHERE email = target_email LIMIT 1;

    -- If user exists, delete all related data
    IF user_id IS NOT NULL THEN
        DELETE FROM savejob WHERE jobseeker = user_id;
        DELETE FROM application WHERE applicant_id = user_id;
        DELETE FROM user WHERE _id = user_id;
    END IF;

    -- If employer exists, delete all related data
    IF emp_id IS NOT NULL THEN
        -- Delete applications for jobs owned by employer
        DELETE FROM application WHERE job_id IN (SELECT _id FROM job WHERE employer_id = emp_id);
        -- Delete savejob for jobs owned by employer
        DELETE FROM savejob WHERE job IN (SELECT _id FROM job WHERE employer_id = emp_id);
        -- Delete jobs
        DELETE FROM job WHERE employer_id = emp_id;
        -- Delete employer
        DELETE FROM employer WHERE _id = emp_id;
    END IF;
END //
DELIMITER ;
