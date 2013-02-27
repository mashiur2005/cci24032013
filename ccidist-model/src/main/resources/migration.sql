/*DDL*/
create database cciservice;

create table organization(
id int(11) not null auto_increment,
version int not null,
name varchar(255),
primary key(id)
);

create table publication (
id int not null auto_increment,
version int not null,
name varchar(255),
organization_id int,
primary key (id),
FOREIGN KEY (organization_id) REFERENCES organization (id));

create table platform (
id int not null auto_increment,
version int not null,
name varchar(255),
primary key (id));

create table issue (id int not null auto_increment,
version int not null,
epubFile longblob,
name varchar(255),
platform_id int,
publication_id int,
primary key (id),
foreign key (publication_id) references publication(id),
foreign key (platform_id) references platform (id));

/*DML*/

/*Organization Data*/
insert into organization (name) values ('Polaris');
insert into organization (name) values ('NHST');
insert into organization (name) values ('AxelSpringer');

/*Publication Data*/

insert into publication (name, organization_id) values ('Addressa', 1);
insert into publication (name, organization_id) values ('Harstadtidende', 1);
insert into publication (name, organization_id) values ('NHST-SPORTS', 2);
insert into publication (name, organization_id) values ('NHST-NEWS', 2);
insert into publication (name, organization_id) values ('AxelSpringer-SPORTS', 3);
insert into publication (name, organization_id) values ('AxelSpringer-ENTERTAINMENT', 3);

/*Platform Data*/
insert into platform (name) value ('ipad');
/*Issue Data*/
insert into issue (epubFile, name, platform_id, publication_id) values (LOAD_FILE('C:\\Users\\mashiur\\epubs\\Polaris\\Addressa\\epub30-spec-20121128.epub'), 'epub30-spec-20121128.epub', 1, 1);
insert into issue (epubFile, name, platform_id, publication_id) values (LOAD_FILE('C:\\Users\\mashiur\\epubs\\Polaris\\Addressa\\regime-anticancer-arabic-20121022.epub'), 'regime-anticancer-arabic-20121022.epub', 1, 1);
insert into issue (epubFile, name, platform_id, publication_id) values (LOAD_FILE('C:\\Users\\mashiur\\epubs\\Polaris\\Addressa\\sash-for-you-20120827.epub'), 'sash-for-you-20120827.epub', 1, 1);
insert into issue (epubFile, name, platform_id, publication_id) values (LOAD_FILE('C:\\Users\\mashiur\\epubs\\Polaris\\Addressa\\widget-figure-gallery-20121022.epub'), 'widget-figure-gallery-20121022.epub', 1, 1);
insert into issue (epubFile, name, platform_id, publication_id) values (LOAD_FILE('C:\\Users\\mashiur\\epubs\\Polaris\\Addressa\\widget-quiz-20121022.epub'), 'widget-quiz-20121022.epub', 1, 1);


