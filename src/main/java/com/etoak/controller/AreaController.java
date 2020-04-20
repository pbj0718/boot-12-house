package com.etoak.controller;

import com.etoak.bean.Area;
import com.etoak.service.AreaService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/area")
@Slf4j
@Api(tags = "地区查询")
public class AreaController {

    @Autowired
    AreaService areaService;

    /**
     * 地区查询接口
     * @param pid
     * @return
     */
    @GetMapping("/queryByPid")
    @ApiOperation(value = "根据父id查询子级地区列表",notes = "根据父id查询子级地区列表")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "接口不存在"),
            @ApiResponse(code = 401, message = "无权限访问"),
            @ApiResponse(code = 403, message = "禁止访问"),
            @ApiResponse(code = 200, message = "成功"),
            @ApiResponse(code = 500, message = "服务端错误")
    })
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value="父id",required = false,
                    defaultValue = "0",paramType = "query"
            )
    })
    public List<Area> queryByPid(@RequestParam(required = false,defaultValue = "0")int pid){
        log.info("pid -{}",pid);
        return areaService.queryByPid(pid);
    }
}
