package com.etoak.service;

import com.etoak.bean.House;
import com.etoak.bean.HouseVo;
import com.etoak.bean.Page;

public interface HouseService {

    /**
     * 添加房源
     * @param house
     * @return
     */
    int addHouse(House house);

    /**
     * 房源列表查询
     * @param pageNum
     * @param pageSize
     * @param houseVo
     * @param rentalList
     * @return
     */
    Page<HouseVo> queryList(int pageNum, int pageSize, HouseVo houseVo, String[] rentalList);

    /**
     * 更新房源
     * @param house
     * @return
     */
    int updateHouse(House house);

    /**
     * 根据id删除房源
     * @param id
     * @return
     */
    int deleteById(int id);
}
