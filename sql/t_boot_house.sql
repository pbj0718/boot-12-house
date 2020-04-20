drop table if exists t_boot_house;
create table if not exists t_boot_house(
	id int not null auto_increment comment '主键',
	area_name varchar(128) not null comment '所在区名称',
	rent_mode VARCHAR(1) not null COMMENT '租赁方式 1:整租 2:合租',
	house_type VARCHAR(1) not null COMMENT '户型',
	orientation VARCHAR(1) not null COMMENT '朝向',
	rental int not null comment '租金',
	province int not null comment '省id',
	city int not null comment '市id',
	area int not null comment '区县id',
	address VARCHAR(256) not null comment '地址',
	pic VARCHAR(128) not null comment '图片地址',
	publish_time datetime not null comment '发布时间',
	PRIMARY key(id)
);