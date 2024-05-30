package utils.entity;

import lombok.Data;

/**
 * json树信息
 */
@Data
public class JsonParser {

    /**
     * 唯一标识
     */
    private String id;

    /**
     * 参数唯一id
     */
    private String paramId;

    /**
     * 参数组id
     */
    private String paramGroupId;

    /**
     * 参数组名称
     */
    private String paramGroupName;

    /**
     * 参数显示名称
     */
    private String typeFlag;

    /**
     * 名称
     */
    private String name;

    /**
     * 类型
     */
    private String type;

    /**
     * 参考值
     */
    private Object defaultValue;

    /**
     * jsonpath
     */
    private String jsonPath;

    /**
     * 父节点key值
     */
    private String parentNode;

    /**
     * 变量参数名称
     */
    private String paramName;

    /**
     * 组唯一id
     */
    private String uniqueId;

}
