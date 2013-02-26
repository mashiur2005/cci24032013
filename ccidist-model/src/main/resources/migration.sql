/*DDL*/
create table organization(
id int(11) not null auto_increment,
name varchar(255),
primary key(id)
);

/*DML*/
insert into organization (name) values ('Polaris');
insert into organization (name) values ('NHST');
insert into organization (name) values ('AxelSpringer');