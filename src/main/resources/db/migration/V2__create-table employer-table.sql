create table employer (
        _id BIGINT AUTO_INCREMENT,
        active bit not null,
        avatar varchar(255),
        email varchar(255),
        name varchar(255),
        password varchar(255),
        role varchar(255),
        company_name varchar(255),
        company_description varchar(255),
        company_logo varchar(255),
        primary key (_id)
    ) engine=InnoDB