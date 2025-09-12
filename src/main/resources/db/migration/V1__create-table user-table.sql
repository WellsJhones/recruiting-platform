create table user (
        _id bigint not null auto_increment,
        active bit not null,
        avatar varchar(255),
        email varchar(255),
        name varchar(255),
        password varchar(255),
        role varchar(255),
        primary key (_id)
    ) engine=InnoDB