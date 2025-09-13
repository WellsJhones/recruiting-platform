create table application (
    _id bigint not null auto_increment,
    applicant_id bigint,
    job_id bigint,
    status varchar(255),
    applied_at datetime,
    primary key (_id),
    foreign key (applicant_id) references user(_id),
    foreign key (job_id) references job(_id)
) engine=InnoDB;