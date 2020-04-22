package com.etoak.bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class Area implements Serializable {
    private Integer id;
    private Integer pid;
    private String name;
    private Integer sort;
}
