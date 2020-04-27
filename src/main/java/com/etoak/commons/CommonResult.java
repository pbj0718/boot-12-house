package com.etoak.commons;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonResult {

    public static final Integer SUCCESS_CODE = 200;
    public static final Integer FAILED_CODE = 0;

    public static final String SUCCESS_MSG = "SUCCESS";
    public static final String FAILED_MSG = "FAILED";

    // 0: 失败,200: 成功
    private Integer code;
    private String msg;
}
