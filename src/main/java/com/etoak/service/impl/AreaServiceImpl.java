package com.etoak.service.impl;

import com.etoak.bean.Area;
import com.etoak.mapper.AreaMapper;
import com.etoak.service.AreaService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AreaServiceImpl implements AreaService {

    @Resource
    AreaMapper areaMapper;

    @Override
    public List<Area> queryByPid(int pid) {
        return areaMapper.queryByPid(pid);
    }
}
