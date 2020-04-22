package com.etoak.controller;

import com.etoak.bean.House;
import com.etoak.service.HouseService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Controller
@RequestMapping("/house")
public class HouseController {

    // 文件上传目录
    @Value("${upload.dir}")
    public String uploadDirectory;

    // 读取默认配置文件访问图片获取访问路径
    @Value("${upload.savePathPrefix}")
    public String savePathPrefix;

    @Autowired
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
    public String add(@RequestParam("file")MultipartFile file, House house)
            throws IOException,IllegalStateException {

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
}
