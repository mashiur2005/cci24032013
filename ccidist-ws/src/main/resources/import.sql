--use ccitest;
insert into organization (id, name, created, updated, version) values ('polaris', 'Polaris', current_timestamp, current_timestamp, 0);
insert into organization (id, name, created, updated, version) values ('nhst', 'NHST', current_timestamp, current_timestamp, 0);
insert into organization (id, name, created, updated, version) values ('axelspringer', 'AxelSpringer', current_timestamp, current_timestamp, 0);

insert into publication (id, name, organization_id, created, updated, version) values ('addressa', 'Addressa', 'polaris', current_timestamp, current_timestamp, 0);
insert into publication (id, name, organization_id, created, updated, version) values ('harstadtidende', 'Harstadtidende', 'polaris', current_timestamp, current_timestamp, 0);
insert into publication (id, name, organization_id, created, updated, version) values ('nhst-sports', 'NHST-SPORTS', 'nhst', current_timestamp, current_timestamp, 0);
insert into publication (id, name, organization_id, created, updated, version) values ('nhst-news', 'NHST-NEWS', 'nhst', current_timestamp, current_timestamp, 0);
insert into publication (id, name, organization_id, created, updated, version) values ('axelSpringer-sports', 'AxelSpringer-SPORTS', 'axelspringer', current_timestamp, current_timestamp, 0);
insert into publication (id, name, organization_id, created, updated, version) values ('axelSpringer-entertainment', 'AxelSpringer-ENTERTAINMENT', 'axelspringer', current_timestamp, current_timestamp, 0);

insert into platform (id, name, version) values ('ipad', 'iPad', 0);
insert into platform (id, name, version) values ('mini-ipad', 'Mini-iPad', 0);
insert into platform (id, name, version) values ('iphone', 'iPhone', 0);

insert into epub_file (file, version) values (LOAD_FILE('E:/IdeaProjects/ccidist/ccidist-model/src/main/resources/regime-anticancer-arabic-20121022.epub'), 0);
insert into epub_file (file, version) values (LOAD_FILE('E:/IdeaProjects/ccidist/ccidist-model/src/main/resources/sash-for-you-20120827.epub'), 0);
insert into epub_file (file, version) values (LOAD_FILE('E:/IdeaProjects/ccidist/ccidist-model/src/main/resources/widget-figure-gallery-20121022.epub'), 0);
insert into epub_file (file, version) values (LOAD_FILE('E:/IdeaProjects/ccidist/ccidist-model/src/main/resources/widget-quiz-20121022.epub'), 0);



insert into issue (id, epub_file_id, name, platform_id, publication_id, created, updated, version) values ('regime-anticancer-arabic-20121022', 1, 'regime-anticancer-arabic-20121022.epub', 'ipad', 'addressa', current_timestamp, current_timestamp, 0);
insert into issue (id, epub_file_id, name, platform_id, publication_id, created, updated, version) values ('sash-for-you-20120827', 2, 'sash-for-you-20120827.epub', 'ipad', 'addressa', current_timestamp, current_timestamp, 0);
insert into issue (id, epub_file_id, name, platform_id, publication_id, created, updated, version) values ('widget-figure-gallery-20121022', 3, 'widget-figure-gallery-20121022.epub', 'ipad', 'addressa', current_timestamp, current_timestamp, 0);
insert into issue (id, epub_file_id, name, platform_id, publication_id, created, updated, version) values ('widget-quiz-20121022', 4, 'widget-quiz-20121022.epub', 'ipad', 'addressa', current_timestamp, current_timestamp, 0);



