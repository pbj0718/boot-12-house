package webService.Type2;

import java.util.List;

/**
 * @author: panbojin
 * @date: 2022-11-11
 */
public class WebServiceDemo1 {

    public static void main(String[] args) {
        MobileCodeWS mobileCodeWS = new MobileCodeWS();
        MobileCodeWSSoap soap = mobileCodeWS.getMobileCodeWSSoap();
        String mobileCodeInfo = soap.getMobileCodeInfo("1386999", null);
        System.out.println(mobileCodeInfo);
        List<String> dbInfo = soap.getDatabaseInfo().getString();
        System.out.println(dbInfo);
    }

}
