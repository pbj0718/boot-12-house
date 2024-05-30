package utils.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * json树显示信息
 */
@Data
public class JsonShowTree {

    @ApiModelProperty(value = "json树 - 节点名")
    private String title;

    @ApiModelProperty(value = "json树 - 参数值")
    private Object value;

    @ApiModelProperty(value = "json树 - 参数类型")
    private String type;

    @ApiModelProperty(value = "json树 - 是否根节点")
    private String isRoot;

    @ApiModelProperty(value = "json树 - 参数说明")
    private String note;

    @ApiModelProperty(value = "json树 - 叶子节点信息")
    private List<JsonShowTree> children;

    @ApiModelProperty(value = "jsonpath")
    private String jsonPath;

    @ApiModelProperty(value = "parentNode")
    private String parentNode;

}
