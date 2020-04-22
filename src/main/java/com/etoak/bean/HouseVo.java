package com.etoak.bean;

import lombok.Data;

@Data
public class HouseVo extends House{

    // 租赁方式中文名称
    private String rentModeName;
    // 户型中文名称
    private String houseTypeName;
    // 朝向中文名称
    private String orientationName;
}
