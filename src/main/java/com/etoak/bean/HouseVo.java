package com.etoak.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class HouseVo extends House{

    // 租赁方式中文名称
    private String rentModeName;
    // 户型中文名称
    private String houseTypeName;
    // 朝向中文名称
    private String orientationName;

    // 接收前端的户型参数
    @JsonIgnore  // 不把接收到的值封装到返回结果里面
    private String[] houseTypeList;

    @JsonIgnore
    private List<String> orientationList;

    // 不用于接收参数 而是传入到mybatis的参数
    @JsonIgnore
    List<Map<String,Integer>> rentalMapList;
}
