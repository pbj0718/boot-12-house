package utils.jsonUtil;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import utils.entity.CommonConstant;
import utils.entity.JsonParser;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JSON操作工具类
 */
@Slf4j
@Component
public class JsonTool {

    /**
     * 判断是否可以转为json对象
     * @param content 带转换文本
     * @return
     */
    public boolean isJsonObject(String content) {
        if(StringUtils.isBlank(content)) {
            return false;
        }
        try {
            com.alibaba.fastjson.JSONObject jsonStr = com.alibaba.fastjson.JSONObject.parseObject(content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 根据jsonpath获取json文本对应节点路径信息
     * @param json
     * @param jsonPath
     * @return
     */
    private Map<String, Object> getJsonPathValue(String json, String jsonPath) {
        Map<String, Object> result = new HashMap<>();
        try {
            Object value = JsonPath.read(json, jsonPath, new Predicate[0]);
            if (value instanceof Integer) {
                result.put(jsonPath, value.toString());
                result.put(jsonPath + "#Type", "number");
            } else if (value instanceof String) {
                result.put(jsonPath, value.toString());
                result.put(jsonPath + "#Type", "string");
            } else if (value instanceof Boolean) {
                result.put(jsonPath, value.toString());
                result.put(jsonPath + "#Type", "boolean");
            } else if (value instanceof java.util.ArrayList) {
                if (ObjectUtil.isNotEmpty(value)) {
                    result.put(jsonPath, value.toString());
                    result.put(jsonPath + "#Type", "array[Object]");
                } else {
                    result.put(jsonPath, "");
                    result.put(jsonPath + "#Type", "array[Object]");
                }
            } else if (value instanceof LinkedHashMap) {
                result.put(jsonPath, value.toString());
                result.put(jsonPath + "#Type", "object");
            } else if (value instanceof Float) {
                result.put(jsonPath, value.toString());
                result.put(jsonPath + "#Type", "number");
            } else if (value instanceof Double) {
                result.put(jsonPath, value.toString());
                result.put(jsonPath + "#Type", "number");
            } else {
                result.put(jsonPath, value.toString());
                result.put(jsonPath + "#Type", "Object");
            }
        } catch (Exception e) {
            log.info("path not found!");
        }
        return result;
    }

    /**
     * 获取并组装json树信息 - api接口
     * @param jsonObject
     * @return
     */
    public List<JsonParser> getApiJsonPath(JSONObject jsonObject) {
        String jsonStr = jsonObject.toString();
        Configuration conf = Configuration.builder().options(Option.AS_PATH_LIST).build();
        List<String> jsonPaths = JsonPath.using(conf)
                .parse(jsonStr).
                read("$..*");
        List<JsonParser> result = new ArrayList<>();
        Iterator<String> jsonpath = jsonPaths.iterator();
        //将/t替换成.
        while (jsonpath.hasNext()) {
            JsonParser jsonParser = new JsonParser();
            String tempjsonPath = jsonpath.next();
            tempjsonPath = tempjsonPath.replace("$", "");
            String parentNode = "";
            if (tempjsonPath.contains("']")) {
                if (tempjsonPath.indexOf("]") != tempjsonPath.lastIndexOf("]")) {
                    parentNode = tempjsonPath.substring(0, tempjsonPath.lastIndexOf("["));
                } else {
                    parentNode = tempjsonPath.substring(0, tempjsonPath.indexOf("']") + 2);
                }
            }
            String name = tempjsonPath.substring(tempjsonPath.lastIndexOf("["), tempjsonPath.lastIndexOf("]") + 1);
            name = name.replaceAll("'", "").replaceAll("\\[", "").replaceAll("]", "");
            jsonParser.setName(name);
            Map<String, Object> pathValue = getJsonPathValueText(jsonStr, tempjsonPath);
            jsonParser.setDefaultValue(pathValue.get(tempjsonPath));
            jsonParser.setType(pathValue.get(tempjsonPath + "#Type").toString());
            jsonParser.setJsonPath(tempjsonPath);
            jsonParser.setParentNode(StringUtils.isNotBlank(parentNode) && parentNode.equals(tempjsonPath) ? "$." : parentNode);
            result.add(jsonParser);
        }
        return result;
    }

    /**
     * 获取并组装json树信息
     * @param jsonStr
     * @return
     */
    public List<JsonParser> getJsonPathByStr(String jsonStr) {
        Configuration conf = Configuration.builder().options(Option.AS_PATH_LIST).build();
        List<String> jsonPaths = JsonPath.using(conf)
                .parse(jsonStr).
                read("$..*");
        List<JsonParser> result = new ArrayList<>();
        Iterator<String> jsonpath = jsonPaths.iterator();
        //将/t替换成.
        while (jsonpath.hasNext()) {
            JsonParser jsonParser = new JsonParser();
            String tempjsonPath = jsonpath.next();
            tempjsonPath = tempjsonPath.replace("$", "");
            String parentNode = "";
            if (tempjsonPath.contains("']")) {
                if (tempjsonPath.indexOf("]") != tempjsonPath.lastIndexOf("]")) {
                    parentNode = tempjsonPath.substring(0, tempjsonPath.lastIndexOf("["));
                } else {
                    parentNode = tempjsonPath.substring(0, tempjsonPath.indexOf("']") + 2);
                }
            }
            String name = tempjsonPath.substring(tempjsonPath.lastIndexOf("["), tempjsonPath.lastIndexOf("]") + 1);
            name = name.replaceAll("'", "").replaceAll("\\[", "").replaceAll("]", "");
            jsonParser.setName(name);
            Map<String, Object> pathValue = getJsonPathValueText(jsonStr, tempjsonPath);
            jsonParser.setDefaultValue(pathValue.get(tempjsonPath));
            jsonParser.setType(pathValue.get(tempjsonPath + "#Type").toString());
            jsonParser.setJsonPath(tempjsonPath);
            jsonParser.setParentNode(StringUtils.isNotBlank(parentNode) && parentNode.equals(tempjsonPath) ? "$." : parentNode);
            result.add(jsonParser);
        }
        return result;
    }

    /**
     * 根据jsonpath获取json文本对应节点路径信息
     * @param json
     * @param jsonPath
     * @return
     */
    private Map<String, Object> getJsonPathValueText(String json, String jsonPath) {
        Map<String, Object> result = new HashMap<>();
        try {
            Configuration conf = Configuration.builder().options(Option.DEFAULT_PATH_LEAF_TO_NULL).build();
            Object value = JsonPath.using(conf).parse(json).read(jsonPath);
            if (value instanceof Integer) {
                result.put(jsonPath, value.toString());
                result.put(jsonPath + "#Type", "数值");
            } else if (value instanceof String) {
                result.put(jsonPath, value.toString());
                result.put(jsonPath + "#Type", "文本");
            } else if (value instanceof Boolean) {
                result.put(jsonPath, value.toString());
                result.put(jsonPath + "#Type", "布尔");
            } else if (value instanceof java.util.ArrayList) {
                if (ObjectUtil.isNotEmpty(value)) {
                    result.put(jsonPath, value.toString());
                    result.put(jsonPath + "#Type", "数组");
                } else {
                    result.put(jsonPath, "");
                    result.put(jsonPath + "#Type", "数组");
                }
            } else if (value instanceof LinkedHashMap) {
                result.put(jsonPath, value.toString());
                result.put(jsonPath + "#Type", "对象");
            } else if (value instanceof Float) {
                result.put(jsonPath, value.toString());
                result.put(jsonPath + "#Type", "数值");
            } else if (value instanceof Double) {
                result.put(jsonPath, value.toString());
                result.put(jsonPath + "#Type", "数值");
            } else if (null == value) {
                result.put(jsonPath, "");
                result.put(jsonPath + "#Type", "文本");
            } else {
                result.put(jsonPath, value.toString());
                result.put(jsonPath + "#Type", "对象");
            }
        } catch (Exception e) {
            log.info("path not found! path===" + jsonPath + "===,jsonInfo====" + json);
        }
        return result;
    }

    /**
     * 获取并组装json树信息
     * @param jsonObject
     * @return
     */
    public List<JsonParser> getOauthJsonPath(JSONObject jsonObject) {
        String jsonStr = jsonObject.toString();
        Configuration conf = Configuration.builder().options(Option.AS_PATH_LIST).build();
        List<String> jsonPaths = JsonPath.using(conf)
                .parse(jsonStr).
                read("$..*");
        List<JsonParser> result = new ArrayList<>();
        Iterator<String> jsonpath = jsonPaths.iterator();
        //将/t替换成.
        while (jsonpath.hasNext()) {
            JsonParser jsonParser = new JsonParser();
            String tempjsonPath = jsonpath.next();
            tempjsonPath = tempjsonPath.replace("$", "");
            String parentNode = "";
            if (tempjsonPath.contains("']")) {
                if (tempjsonPath.indexOf("]") != tempjsonPath.lastIndexOf("]")) {
                    parentNode = tempjsonPath.substring(0, tempjsonPath.lastIndexOf("["));
                } else {
                    parentNode = tempjsonPath.substring(0, tempjsonPath.indexOf("']") + 2);
                }
            }
            String name = tempjsonPath.substring(tempjsonPath.lastIndexOf("["), tempjsonPath.lastIndexOf("]") + 1);
            name = name.replaceAll("'", "").replaceAll("\\[", "").replaceAll("]", "");
            jsonParser.setName(name);
            Map<String, Object> pathValue = getJsonPathValueText(jsonStr, tempjsonPath);
            jsonParser.setDefaultValue(pathValue.get(tempjsonPath));
            jsonParser.setType(pathValue.get(tempjsonPath + "#Type").toString());
            jsonParser.setJsonPath(tempjsonPath);
            jsonParser.setParentNode(StringUtils.isNotBlank(parentNode) && parentNode.equals(tempjsonPath) ? "$." : parentNode);
            result.add(jsonParser);
        }
        return result;
    }

    /**
     * 组装json树信息-jsonobject对象解析调用
     * @return
     */
    public List<JsonParser> assemJsonPath(String resultInfo) {
        List<JsonParser> result = new ArrayList<>();
        // 组装code信息
        JsonParser code = new JsonParser();
        code.setName("code");
        code.setType("数值");
        code.setJsonPath("['code']");
        code.setDefaultValue(200);
        code.setParentNode("$.");
        result.add(code);
        // 组装success信息
        JsonParser successInfo = new JsonParser();
        successInfo.setName("success");
        successInfo.setType("布尔");
        successInfo.setJsonPath("['success']");
        successInfo.setDefaultValue("true");
        successInfo.setParentNode("$.");
        result.add(successInfo);
        // 组装data信息
        JsonParser data = new JsonParser();
        data.setName("data");
        data.setType("文本");
        data.setJsonPath("['data']");
        data.setDefaultValue(resultInfo);
        data.setParentNode("$.");
        result.add(data);
        // 组装message信息
        JsonParser message = new JsonParser();
        message.setName("message");
        message.setType("文本");
        message.setJsonPath("['message']");
        message.setDefaultValue("成功");
        message.setParentNode("$.");
        result.add(message);
        return result;
    }

    /**
     * 截取两个符号中间的字符串
     * @param str
     * @param startChar
     * @param endChar
     * @return
     */
    private String getStringBetweenTwoChars(String str, char startChar, char endChar) {
        String regex = startChar + "([^" + endChar + "]*)" + endChar;
        if ('$' == startChar && '$' == endChar) {
            regex = "\\$" + "([^" + endChar + "]*)" + "\\$";
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * 组装json树信息-非jsonobject对象使用此方法
     * @param statusCode
     * @param resultInfo
     * @return
     */
    public List<JsonParser> assemFailJsonPath(Integer statusCode, String resultInfo) {
        List<JsonParser> result = new ArrayList<>();
        // 组装code信息
        JsonParser code = new JsonParser();
        code.setName("code");
        code.setType("数值");
        code.setJsonPath("['code']");
        code.setDefaultValue(null == statusCode ? CommonConstant.DEFAULT_STATUS_CODE : statusCode);
        code.setParentNode("$.");
        result.add(code);
        // 组装success信息
        JsonParser successInfo = new JsonParser();
        successInfo.setName("success");
        successInfo.setType("布尔");
        successInfo.setJsonPath("['success']");
        successInfo.setDefaultValue("true");
        successInfo.setParentNode("$.");
        result.add(successInfo);
        // 组装data信息
        JsonParser data = new JsonParser();
        data.setName("data");
        data.setType("文本");
        data.setJsonPath("['data']");
        data.setDefaultValue(resultInfo);
        data.setParentNode("$.");
        result.add(data);
        // 组装message信息
        JsonParser message = new JsonParser();
        message.setName("message");
        message.setType("文本");
        message.setJsonPath("['message']");
        message.setDefaultValue("成功");
        message.setParentNode("$.");
        result.add(message);
        return result;
    }

}
