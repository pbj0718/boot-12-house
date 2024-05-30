package utils.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description: Excel下载示例实体类
 * @author: pbj
 * @date: 2024-05-30
 * @version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="示例实体类", description="示例实体类")
public class ExcelSampleVo {

    @ApiModelProperty(value = "id")
    @ExcelIgnore
    private String id;

    @ApiModelProperty(value = "名称")
    @ExcelProperty(value = "名称", index = 0)
    private String name;

}
