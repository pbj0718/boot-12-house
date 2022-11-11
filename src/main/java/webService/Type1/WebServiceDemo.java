package webService.Type1;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.poi.ss.formula.functions.T;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;

/**
 * java 调用 wsdl请求
 * @author: panbojin
 * @date: 2022-11-11
 */
public class WebServiceDemo {

    /**
     * 方式一:
     * operation 代表方法名， documentation中 地址后会有参数说明，根据说明拼装参数
     * input 和 output 分别对应请求实体类和输出实体类
     * <wsdl:operation name="getMobileCodeInfo">
     *   <wsdl:documentation xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/"><br /><h3>获得国内手机号码归属地省份、地区和手机卡类型信息</h3><p>输入参数：mobileCode = 字符串（手机号码，最少前7位数字），userID = 字符串（商业用户ID） 免费用户为空字符串；返回数据：字符串（手机号码：省份 城市 手机卡类型）。</p><br /></wsdl:documentation>
     *   <wsdl:input message="tns:getMobileCodeInfoSoapIn"/>
     *   <wsdl:output message="tns:getMobileCodeInfoSoapOut"/>
     * </wsdl:operation>
     *
     * @throws ServiceException
     * @throws RemoteException
     */

    public void getMobileCodeInfo() throws ServiceException, RemoteException {
        Service service = new Service();
        Call call = (Call) service.createCall();
        // wsdl完整地址
        call.setTargetEndpointAddress("http://ws.webxml.com.cn/WebServices/MobileCodeWS.asmx?wsdl");
        /**
         * 设置方法名
         * new QName(String namespaceURI, String localPart) namespaceURI即为wsdl中的targetNamespace, localPart即为接口名
         */
        call.setOperationName(new QName("http://WebXml.com.cn/", "getMobileCodeInfo"));
        /**
         * 添加参数
         * addParameter方法的参数包括：参数名（namespace+参数名）、参数类型、ParameterMode(入参即为IN)
         */
        call.addParameter(new QName("http://WebXml.com.cn/", "mobileCode"), XMLType.XSD_STRING, ParameterMode.IN);
        call.setUseSOAPAction(true);
        // SOAPActionURI格式为targetNamespace+方法名
        call.setSOAPActionURI("http://WebXml.com.cn/getMobileCodeInfo");
        // 指定返回值类型，为字符串
        call.setReturnType(XMLType.XSD_STRING);
        call.setReturnClass(java.lang.String.class);
        /**
         * 调用无参的webservice接口无需添加参数，并且在invoke方法中传入的是一个空的对象数组
         * T result = (T)call.invoke(new Object[]{});
         */
        String result = (String) call.invoke(new Object[]{"手机号码"});
        System.out.println(result);
    }

    /**
     * 方式二: 直接使用jdk自带的wsimport命令
     * wsimport -d <生成.class文件的目录> -s <生成.java文件的目录> -p<包名> <wsdl地址>
     * eg:
     * D:\wsdl>wsimport -d class -s java -p mobileCode http://ws.webxml.com.cn/WebServices/MobileCodeWS.asmx?wsdl
     */

}
