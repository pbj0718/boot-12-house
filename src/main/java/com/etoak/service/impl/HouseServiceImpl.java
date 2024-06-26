package com.etoak.service.impl;

import com.etoak.bean.Area;
import com.etoak.bean.House;
import com.etoak.bean.HouseVo;
import com.etoak.bean.Page;
import com.etoak.mapper.AreaMapper;
import com.etoak.mapper.HouseMapper;
import com.etoak.service.HouseService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class HouseServiceImpl implements HouseService {

    @Resource
    HouseMapper houseMapper;

    @Resource
    AreaMapper areaMapper;

    @Override
    public int addHouse(House house) {
        Area area = areaMapper.queryById(house.getArea());
        if(area == null){
            log.error("未查询到所在区,查询所在区的id为- {}",house.getArea());
            throw new RuntimeException("服务端异常");
        }
        house.setAreaName(area.getName());
        return houseMapper.addHouse(house);
    }

    @Override
    public Page<HouseVo> queryList(int pageNum, int pageSize, HouseVo houseVo, String[] rentalList) {
        // 处理价格范围
        this.handleRental(houseVo,rentalList);

        PageHelper.startPage(pageNum,pageSize);
        List<HouseVo> houseVoList = houseMapper.queryList(houseVo);
        PageInfo<HouseVo> pageInfo = new PageInfo<>(houseVoList);
        return new Page<HouseVo>(pageInfo.getPageNum(),
                pageInfo.getPageSize(),
                houseVoList,
                pageInfo.getTotal(),
                pageInfo.getPages());
    }

    /**
     * 更新房源信息
     * @param house
     * @return
     */
    @Override
    public int updateHouse(House house) {
        if(house.getCity() !=null){
            Area area = areaMapper.queryById(house.getArea());
            house.setAreaName(area.getName());
        }

        return houseMapper.updateHouse(house);
    }

    /**
     * 处理价格范围
     * @param houseVo
     * @param rentalList
     */
    private void handleRental(HouseVo houseVo, String[] rentalList) {
        if(ArrayUtils.isNotEmpty(rentalList)){
            // 存储转换结果
            List<Map<String,Integer>> rentalMapList = new ArrayList<>();

            // rentalList = [100-1000,1000-1500]
            for(String rental: rentalList){
                String[] rentalArray = rental.split("-");
                Map<String, Integer> rentalMap = new HashMap<>();
                rentalMap.put("start",Integer.valueOf(rentalArray[0]));
                rentalMap.put("end",Integer.valueOf(rentalArray[1]));
                rentalMapList.add(rentalMap);
            }
            houseVo.setRentalMapList(rentalMapList);
        }
    }

    @Override
    public int deleteById(int id) {
        return houseMapper.deleteById(id);
    }
}
