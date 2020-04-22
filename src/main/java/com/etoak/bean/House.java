package com.etoak.bean;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class House {

    private Integer id;
    @NotNull(message = "省份必填")
    private Integer province;
    @NotNull(message = "市必填")
    private Integer city;
    @NotNull(message = "所在区必填")
    private Integer area;
    // 所在县区名称

    private String areaName;
    // 租赁方式
    @NotBlank(message = "租赁方式不能为空")
    private String rentMode;
    // 朝向
    private String orientation;
    // 户型
    private String houseType;
    // 租金
    @NotNull(message = "租金必填")
    @Max(value = 100000,message = "租金不能超过10万")
    @Min(value = 100,message = "租金不能低于100")
    private Integer rental;
    // 住址
    @Size(min = 1,max = 10,message = "地址长度介于1到10个字符之间")
    @NotBlank(message = "住址必填")
    private String address;
    // 房屋图片
    private String pic;
    // 发布时间
    private String publishTime;
}
