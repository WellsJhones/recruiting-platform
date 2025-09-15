CREATE TABLE savejob (
    _id BIGINT PRIMARY KEY AUTO_INCREMENT,
    jobseeker BIGINT,
    job BIGINT,
    created_at DATETIME,
    updated_at DATETIME,
    __v INT
) ENGINE=InnoDB;