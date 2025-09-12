CREATE TABLE job (
    _id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    requirements TEXT,
    location VARCHAR(255),
    category VARCHAR(255),
    type VARCHAR(100),
    salary_min INT,
    salary_max INT,
    employer_id BIGINT,
    is_closed BOOLEAN DEFAULT FALSE,
    is_saved BOOLEAN DEFAULT FALSE,
    application_status INT,
    created_at DATETIME,
    updated_at DATETIME,
    CONSTRAINT fk_job_employer FOREIGN KEY (employer_id) REFERENCES employer(_id)
) ENGINE=InnoDB;
