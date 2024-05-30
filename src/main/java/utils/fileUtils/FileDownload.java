package utils.fileUtils;

import utils.entity.ExcelSampleVo;
import com.alibaba.excel.EasyExcelFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @description: 文件操作工具类
 * @author: pbj
 * @date: 2024-05-30
 * @version: V1.0
 */
@Component
@Slf4j
public class FileDownload {

    /**
     * 调用该方法，不会直接下载写好的文件，而是会先写出源文件到服务根目录，然后修改文件后缀名后将文件输出到响应中
     * 适用于文件加密操作
     * 通过流下载文件，支持通过服务下载到服务根目录下，然后更改文件后缀名之后重新操作下载
     * @param response
     * @throws Exception
     */
    public void excelDownload(HttpServletResponse response) throws Exception {
        // 修改后展示的文件后缀名称
        String fileSuffix = ".sample";
        Calendar calendar = Calendar.getInstance();
        // 源文件名示例
        String excelName = "excelName(" + calendar.get(Calendar.YEAR) + "年" + (calendar.get(Calendar.MONTH) + 1) + "月"
                + calendar.get(Calendar.DAY_OF_MONTH) + "日" + calendar.get(Calendar.HOUR_OF_DAY) + "时"
                + calendar.get(Calendar.MINUTE) + "分).xls";
        List<ExcelSampleVo> result = new ArrayList<>();
        ExcelSampleVo vo = new ExcelSampleVo();
        vo.setId("111");
        vo.setId("下载示例名称");
        result.add(vo);
        //创建文件
        EncryptFile file = EncryptFile.encryptFileFactory(excelName,EncryptFile.ENCRYPTFILE_XLS);
        //下载到服务目录下
        // 不指定目录默认放置在文件顶级目录下，即boot-12-house文件夹下
        EasyExcelFactory.write(file, ExcelSampleVo.class)
                .sheet("示例下载").doWrite(result);

        //源文件路径(全路径)
        String srcFileName = file.getAbsolutePath();
        //操作后文件路径
        String encFileName = srcFileName + fileSuffix;

        //获取修改后文件并导出
        File fileDsps = new File(encFileName);
        if(!fileDsps.exists()){
            throw new Exception("下载加密文件失败！未获取到加密后文件");
        }
        // 修改后的文件信息
        String filename = file.getOriginalName() + fileSuffix;
        // 以流的形式下载文件。
        InputStream fis = new BufferedInputStream(new FileInputStream(fileDsps.getPath()));
        byte[] buffer = new byte[fis.available()];
        fis.read(buffer);
        fis.close();
        //清空response重新设置值
        response.reset();
        response.setContentType("application/octet-stream;charset=UTF-8");
        String fileName = URLEncoder.encode(filename, "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);
        OutputStream ouputStream = response.getOutputStream();
        ouputStream.write(buffer);
        ouputStream.flush();
        ouputStream.close();

        //清除源文件
        if (file.exists()) {
            file.delete();
        }
        //清除修改后文件
        if (fileDsps.exists()) {
            fileDsps.delete();
        }
    }

}
