create sequence hibernate_sequence start with 1 increment by 1
create table destination (id bigint not null, ae_title varchar(255), description varchar(255), headers varchar(4096), hostname varchar(255), notify varchar(255), notify_interval integer, notify_object_error_prefix varchar(255), notify_object_pattern varchar(255), notify_object_values varchar(255), port integer, type integer not null, url varchar(255), url_credentials varchar(255), useaetdest boolean, forward_node_id bigint, primary key (id))
create table forward_node (id bigint not null, description varchar(255), fwd_ae_title varchar(16), primary key (id))
create table input_destination (id bigint not null, ae_title varchar(16), description varchar(255), hostname varchar(255), notify varchar(255), port integer not null check (port<=65535 AND port>=1), secure boolean, useaetdest boolean, source_node_id bigint, primary key (id))
create table input_source_node (id bigint not null, check_hostname boolean, description varchar(255), dst_ae_title varchar(16), hostname varchar(255), src_ae_title varchar(16), primary key (id))
create table source_node (id bigint not null, ae_title varchar(16), description varchar(255), hostname varchar(255), forward_node_id bigint, primary key (id))
alter table destination add constraint FKp3a8vsvewjod5lj16mtp9ji8q foreign key (forward_node_id) references forward_node
alter table input_destination add constraint FKehtklldbfhahd5ikncrw0kaig foreign key (source_node_id) references input_source_node
alter table source_node add constraint FKccivbb06qwsyudycyarv9v0n foreign key (forward_node_id) references forward_node
