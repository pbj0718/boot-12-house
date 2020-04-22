package com.etoak.bean;

import lombok.Data;

@Data
public class House {
    private Integer id;
    private Integer province;
    private Integer city;
    private Integer area;
    // 所在县区名称
    private String areaName;
    // 租赁方式
    private String rentMode;
    // 朝向
    private String orientation;
    // 户型
    private String houseType;
    // 租金
    private Integer rental;
    // 住址
    private String address;
    // 房屋图片
    private String pic;
    // 发布时间
    private String publishTime;
}
