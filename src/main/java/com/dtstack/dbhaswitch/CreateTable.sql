        create  database rdsha;


        CREATE TABLE INSTANCE(
          id int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
          is_delete varchar(1) NOT NULL DEFAULT 'N' COMMENT '是否删除,Y删除，N未删除',
          gmt_create datetime DEFAULT NULL COMMENT '创建时间',
          gmt_modified datetime DEFAULT NULL COMMENT '修改时间',
          rds_code varchar(50) NOT NULL COMMENT '实例名称',
          ip varchar(50) NOT NULL COMMENT '实例所在ip地址',
          port varchar(50) NOT NULL COMMENT '实例所在的端口',
          user_name varchar(50) NOT NULL COMMENT '登陆用户名',
          pass_word varchar(50) NOT NULL COMMENT '登陆密码',
          host_name varchar(50) NOT NULL COMMENT '实例所在域名',
          judge_master int(1) NOT NULL COMMENT '是否为主实例,0是主，1是备',
          brother_ip varchar(50) DEFAULT NULL COMMENT '对端实例的ip地址',
          rds_id int(11) NOT NULL  COMMENT '全局唯一性Id',
          rds_url varchar(50) NOT NULL COMMENT 'rds域名',
          instance_id INTEGER(11) NOT NULL COMMENT 'rds区分主备的Id',
          rds_status integer (5) not null default 0 comment 'rds运行状态，0是正常运行，1是实例异常，2是升级实例',
          PRIMARY KEY (id)
        )ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='实例列表';



        create table SWITCHSTATUS(
          id int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
          is_delete varchar(1) NOT NULL DEFAULT 'N' COMMENT '是否删除,Y删除，N未删除',
          gmt_create datetime DEFAULT NULL COMMENT '创建时间',
          gmt_modified datetime DEFAULT NULL COMMENT '修改时间',
          rds_code varchar(50) NOT NULL COMMENT '实例名称',
          master_ip varchar(50) NOT NULL COMMENT '主实例IP',
          slave_ip varchar(50) NOT NULL COMMENT '备实例IP',
          host_name varchar(50) NOT NULL COMMENT '域名',
          port varchar(50) NOT NULL COMMENT '端口',
          switch_time_begin varchar(50) NOT NULL COMMENT '倒换开始时间',
          switch_time_end varchar(50) NOT NULL COMMENT '倒换结束时间',
          switch_type varchar(50) NOT NULL COMMENT '倒换类型',
          db_type varchar(10) DEFAULT NULL DEFAULT 'MYSQL' COMMENT '数据库类型',
          switch_result  varchar(20) NOT NULL COMMENT '是否倒换成功',
          schedule varchar(20) NOT NULL DEFAULT '0%' COMMENT '倒换进度',
          swtich_info varchar(50)  DEFAULT NULL  COMMENT '倒换原因',
          log_str LongText DEFAULT NULL  COMMENT '日志保存',
          rds_id INTEGER(11) NOT NULL COMMENT '实例的rdsId',
          PRIMARY KEY (id)
        )ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='实例倒换信息';


        create table REMOVALDATA(
          id int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
          is_delete varchar(1) NOT NULL DEFAULT 'N' COMMENT '是否删除,Y删除，N未删除',
          gmt_create datetime DEFAULT NULL COMMENT '创建时间',
          gmt_modified datetime DEFAULT NULL COMMENT '修改时间',
          rds_code varchar(50) NOT NULL COMMENT '实例名称',
          ip varchar(50) NOT NULL COMMENT '实例所在ip地址',
          port varchar(50) NOT NULL COMMENT '实例所在的端口',
          user_name varchar(50) NOT NULL COMMENT '登陆用户名',
          pass_word varchar(50) NOT NULL COMMENT '登陆密码',
          host_name varchar(50) NOT NULL COMMENT '实例所在域名',
          rds_id int(11) NOT NULL COMMENT '全局唯一性Id',
          db_status varchar(50) NOT NULL COMMENT '实例状态码',
          update_id varchar(50) NOT NULL COMMENT '唯一ID',
          instance_id int(11) NOT NULL COMMENT '备实例指向主实例',
          rds_url varchar (50) NOT NULL COMMENT 'rds域名',
          judge_master int(5) NOT NULL COMMENT 'rds主备',
          PRIMARY KEY (id)
        )ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='实例升级信息';