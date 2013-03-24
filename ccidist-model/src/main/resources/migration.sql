/*DDL*/
create database cciservice;
use cciservice;

create table organization(
id varchar(255) not null,
version int not null,
name varchar(255),
created datetime,
updated datetime,
primary key(id)
);

create table publication (
id varchar(255) not null,
version int not null,
name varchar(255),
organization_id varchar(255),
created datetime,
updated datetime,
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

create table events (
id int not null auto_increment,
epub_file_id int,
path varchar(255),
category int,
created datetime,
primary key(id)
);

/*DML*/

/*Organization Data*/
insert into organization (id, name, created, updated) values ('polaris', 'Polaris', current_timestamp, current_timestamp);
insert into organization (id, name, created, updated) values ('nhst', 'NHST', current_timestamp, current_timestamp);
insert into organization (id, name, created, updated) values ('axelspringer', 'AxelSpringer', current_timestamp, current_timestamp);

/*Publication Data*/

insert into publication (id, name, organization_id, created, updated) values ('addressa', 'Addressa', 'polaris', current_timestamp, current_timestamp);
insert into publication (id, name, organization_id, created, updated) values ('harstadtidende', 'Harstadtidende', 'polaris', current_timestamp, current_timestamp);
insert into publication (id, name, organization_id, created, updated) values ('nhst-sports', 'NHST-SPORTS', 'nhst', current_timestamp, current_timestamp);
insert into publication (id, name, organization_id, created, updated) values ('nhst-news', 'NHST-NEWS', 'nhst', current_timestamp, current_timestamp);
insert into publication (id, name, organization_id, created, updated) values ('axelSpringer-sports', 'AxelSpringer-SPORTS', 'axelspringer', current_timestamp, current_timestamp);
insert into publication (id, name, organization_id, created, updated) values ('axelSpringer-entertainment', 'AxelSpringer-ENTERTAINMENT', 'axelspringer', current_timestamp, current_timestamp);

/*Platform Data*/
insert into platform (id, name) value ('ipad', 'iPad');
insert into platform (id, name) value ('mini-ipad', 'Mini-iPad');
insert into platform (id, name) value ('iphone', 'iPhone');

/*epub Files*/
insert into epub_file (file) values (LOAD_FILE('E:/IdeaProjects/ccidist/ccidist-model/src/main/resources/regime-anticancer-arabic-20121022.epub'));
insert into epub_file (file) values (LOAD_FILE('E:/IdeaProjects/ccidist/ccidist-model/src/main/resources/sash-for-you-20120827.epub'));
insert into epub_file (file) values (LOAD_FILE('E:/IdeaProjects/ccidist/ccidist-model/src/main/resources/widget-figure-gallery-20121022.epub'));
insert into epub_file (file) values (LOAD_FILE('E:/IdeaProjects/ccidist/ccidist-model/src/main/resources/widget-quiz-20121022.epub'));



/*Issue Data*/
insert into issue (id, epub_file_id, name, platform_id, publication_id, created, updated) values ('regime-anticancer-arabic-20121022', 1, 'regime-anticancer-arabic-20121022.epub', 'ipad', 'addressa', current_timestamp, current_timestamp);
insert into issue (id, epub_file_id, name, platform_id, publication_id, created, updated) values ('sash-for-you-20120827', 2, 'sash-for-you-20120827.epub', 'ipad', 'addressa', current_timestamp, current_timestamp);
insert into issue (id, epub_file_id, name, platform_id, publication_id, created, updated) values ('widget-figure-gallery-20121022', 3, 'widget-figure-gallery-20121022.epub', 'ipad', 'addressa', current_timestamp, current_timestamp);
insert into issue (id, epub_file_id, name, platform_id, publication_id, created, updated) values ('widget-quiz-20121022', 4, 'widget-quiz-20121022.epub', 'ipad', 'addressa', current_timestamp, current_timestamp);



