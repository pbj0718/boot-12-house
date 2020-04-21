package com.etoak.mapper;

import com.etoak.bean.Dict;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DictMapper {
    List<Dict> queryList(@Param("groupId") String groupId);
}
