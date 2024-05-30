package mybatisPlusDataSourceSwitch.dataSourceEnum;

/**
 * 数据源示例枚举类
 **/
public enum DataSourceEnum {

    /**
     * 示例数据源1
     */
    eg1("11","dataSource1"),

    /**
     * 示例数据源2
     */
    eg2("12","dataSource2");

    /**
     * 编码
     */
    private final String code;

    /**
     * 编码对应数据源
     */
    private final String value;

    DataSourceEnum(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public String getCode(){
        return code;
    }

    public String getValue(){
        return value;
    }

    /**
     * 依据code获取value
     **/
    public static String getDataSource(String code) {
        for (DataSourceEnum sourceEnum : DataSourceEnum.values()) {
            if (code.equals(sourceEnum.getCode())) {
                return sourceEnum.value;
            }
        }
        return null;
    }
}
