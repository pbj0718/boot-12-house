package com.etoak.service.impl;

import com.etoak.bean.Area;
import com.etoak.bean.House;
import com.etoak.mapper.AreaMapper;
import com.etoak.mapper.HouseMapper;
import com.etoak.service.HouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HouseServiceImpl implements HouseService {

    @Autowired
    HouseMapper houseMapper;

    @Autowired
    AreaMapper areaMapper;

    @Override
    public int addHouse(House house) {
        Area area = areaMapper.queryById(house.getArea());
        if(area == null){
            log.error("未查询到所在区,查询所在区的id为- {}",house.getArea());
            throw new RuntimeException("服务端异常");
        }
        house.setAreaName(area.getName());
        houseMapper.addHouse(house);
        return 0;
    }
}
