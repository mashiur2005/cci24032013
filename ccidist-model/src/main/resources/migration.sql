/*DDL*/
create database cciservice;

create table organization(
id varchar(255) not null,
version int not null,
name varchar(255),
primary key(id)
);

create table publication (
id varchar(255) not null,
version int not null,
name varchar(255),
organization_id varchar(255),
primary key (id),
FOREIGN KEY (organization_id) REFERENCES organization (id)
);

create table platform (
id varchar(255) not null,
version int not null,
name varchar(255),
primary key (id)
);

create table epub_file (
id int not null auto_increment,
version int not null,
file longblob,
primary key (id));

create table issue (
id varchar(255) not null,
version int not null,
name varchar(255),
platform_id varchar(255) ,
publication_id varchar(255) ,
epub_file_id int,
created datetime,
updated datetime,
primary key (id),
foreign key (publication_id) references publication(id),
foreign key (platform_id) references platform (id),
foreign key (epub_file_id) references epub_file (id));

create table publication_platform (
publication_id varchar(255) not null,
platform_id varchar(255) not null,
primary key (publication_id, platform_id),
foreign key (publication_id) references publication(id),
foreign key (platform_id) references platform (id)
);

/*DML*/

/*Organization Data*/
insert into organization (id, name) values ('polaris', 'Polaris');
insert into organization (id, name) values ('nhst', 'NHST');
insert into organization (id, name) values ('axelspringer', 'AxelSpringer');

/*Publication Data*/

insert into publication (id, name, organization_id) values ('addressa', 'Addressa', 'polaris');
insert into publication (id, name, organization_id) values ('harstadtidende', 'Harstadtidende', 'polaris');
insert into publication (id, name, organization_id) values ('nhst-sports', 'NHST-SPORTS', 'nhst');
insert into publication (id, name, organization_id) values ('nhst-news', 'NHST-NEWS', 'nhst');
insert into publication (id, name, organization_id) values ('axelSpringer-sports', 'AxelSpringer-SPORTS', 'axelspringer');
insert into publication (id, name, organization_id) values ('axelSpringer-entertainment', 'AxelSpringer-ENTERTAINMENT', 'axelspringer');

/*Platform Data*/
insert into platform (id, name) value ('ipad', 'ipad');

/*epub Files*/
insert into epub_file (file) values (LOAD_FILE('C:\\Users\\mashiur\\epubs\\Polaris\\Addressa\\epub30-spec-20121128.epub'));
insert into epub_file (file) values (LOAD_FILE('C:\\Users\\mashiur\\epubs\\Polaris\\Addressa\\regime-anticancer-arabic-20121022.epub'));
insert into epub_file (file) values (LOAD_FILE('C:\\Users\\mashiur\\epubs\\Polaris\\Addressa\\sash-for-you-20120827.epub'));
insert into epub_file (file) values (LOAD_FILE('C:\\Users\\mashiur\\epubs\\Polaris\\Addressa\\widget-figure-gallery-20121022.epub'));
insert into epub_file (file) values (LOAD_FILE('C:\\Users\\mashiur\\epubs\\Polaris\\Addressa\\widget-quiz-20121022.epub'));



/*Issue Data*/
insert into issue (id, epub_file_id, name, platform_id, publication_id, created, updated) values ('epub30-spec-20121128', 1, 'epub30-spec-20121128.epub', 'ipad', 'addressa', current_timestamp, current_timestamp);
insert into issue (id, epub_file_id, name, platform_id, publication_id, created, updated) values ('regime-anticancer-arabic-20121022', 2, 'regime-anticancer-arabic-20121022.epub', 'addressa', 'polaris', current_timestamp, current_timestamp);
insert into issue (id, epub_file_id, name, platform_id, publication_id, created, updated) values ('sash-for-you-20120827', 3, 'sash-for-you-20120827.epub', 'ipad', 'addressa', current_timestamp, current_timestamp);
insert into issue (id, epub_file_id, name, platform_id, publication_id, created, updated) values ('widget-figure-gallery-20121022', 4, 'widget-figure-gallery-20121022.epub', 'ipad', 'addressa', current_timestamp, current_timestamp);
insert into issue (id, epub_file_id, name, platform_id, publication_id, created, updated) values ('widget-quiz-20121022', 5, 'widget-quiz-20121022.epub', 'ipad', 'addressa', current_timestamp, current_timestamp);


