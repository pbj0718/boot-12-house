package com.etoak.controller;

import com.etoak.bean.House;
import com.etoak.bean.HouseVo;
import com.etoak.bean.Page;
import com.etoak.exception.ParamException;
import com.etoak.service.HouseService;
import com.etoak.utils.ValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/house")
@Slf4j
public class HouseController {

    // 文件上传目录
    @Value("${upload.dir}")
    public String uploadDirectory;

    // 读取默认配置文件访问图片获取访问路径
    @Value("${upload.savePathPrefix}")
    public String savePathPrefix;

    @Resource
    HouseService houseService;

    @RequestMapping("/toAdd")
    public String toAdd(){
        return "/house/Add";
    }

    /**
     * 添加房源
     * @param file
     * @param house
     * @return
     */
    @PostMapping("/add")
    public String add(@RequestParam("file")MultipartFile file, @Valid House house, BindingResult bindResult)
            throws IOException,IllegalStateException {

        List<ObjectError> allErrors = bindResult.getAllErrors();
        if(CollectionUtils.isNotEmpty(allErrors)){
            StringBuffer msgBuffer = new StringBuffer();
            for(ObjectError objectError : allErrors){
                String message = objectError.getDefaultMessage();
                msgBuffer.append(message).append(";");
            }
            throw new ParamException("参数校验失败" + msgBuffer.toString());
        }

        String originalFilename = file.getOriginalFilename();
        String prefix = UUID.randomUUID().toString().replaceAll("-","");
        String newFilename = prefix + "_" + originalFilename;

        File destFile = new File(this.uploadDirectory,newFilename);
        file.transferTo(destFile);

        // 给House设置访问地址
        house.setPic(this.savePathPrefix + newFilename);
        houseService.addHouse(house);
        return "redirect:/house/toAdd";
    }

    @PostMapping("/add2")
    public String add2(@RequestParam("file")MultipartFile file, @Valid House house, BindingResult bindResult)
            throws IOException,IllegalStateException {

        ValidationUtil.validate(house);

        String originalFilename = file.getOriginalFilename();
        String prefix = UUID.randomUUID().toString().replaceAll("-","");
        String newFilename = prefix + "_" + originalFilename;

        File destFile = new File(this.uploadDirectory,newFilename);
        file.transferTo(destFile);

        // 给House设置访问地址
        house.setPic(this.savePathPrefix + newFilename);
        houseService.addHouse(house);
        return "redirect:/house/toAdd";
    }

    /**
     * 房源信息查询接口
     * @param pageNum
     * @param pageSize
     * @param houseVo
     * @return
     */
    @GetMapping(value = "/list",produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Page<HouseVo> queryList(
            @RequestParam(required = false,defaultValue = "1") int pageNum,
            @RequestParam(required = false,defaultValue = "10") int pageSize,
            HouseVo houseVo,
            @RequestParam(value = "rentalList",required = false) String[] rentalList){
        log.info("pageNum - {},pageSize - {},houseVo - {}, rentalList -{}", pageNum, pageSize, houseVo, rentalList);
        return houseService.queryList(pageNum,pageSize,houseVo,rentalList);
    }

    @GetMapping("/toList")
    public String toList(){
        return "house/list";
    }

    @PutMapping("/update")
    public String update(House house){
        log.info("house -{}",house);
        houseService.updateHouse(house);
        return "redirect:/house/toList";
    }

    @DeleteMapping("/{id}")
    public String deleteHouse(@PathVariable("id") int id){
        log.info("delete id -{}",id);
        houseService.deleteById(id);
        return "redirect:/house/toList";
    }
}
