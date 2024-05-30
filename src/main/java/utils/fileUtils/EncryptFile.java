package utils.fileUtils;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import java.io.File;

/**
 * 文档名加密类，防止文件名重复，防止在服务中发生并发
 * @Description
 * @create 2024-05-28 9:19
 */
public class EncryptFile extends File {

    /**
     * 加密文件类型后缀
     */
    public static final String ENCRYPTFILE_XLS = ".xls";

    public static final String ENCRYPTFILE_XLSX = ".xlsx";

    public static final String ENCRYPTFILE_PDF= ".pdf";

    public static final String ENCRYPTFILE_TEXT= ".text";

    public static final String ENCRYPTFILE_PNG= ".png";

    public static final String ENCRYPTFILE_JPG =  ".jpg";

    public static final String ENCRYPTFILE_ZIP=  ".zip";

    public static final String ENCRYPTFILE_RAR=  ".rar";

    //雪花算法下标
    int idStrLength;

    public int getIdStrLength(){
        return this.idStrLength;
    }

    public void setIdStrLength(int idStrLength){
        this.idStrLength = idStrLength;
    }


    //获取文件加密前的名字
    public String getOriginalName(){
        return this.getName().substring(this.idStrLength);
    }


    /**
     * 构造器私有化，不许更改，防止它人走不加密文件名
     * @param pathname
     */
    private EncryptFile(String pathname) {
        super(pathname);
    }


    /**
     * 文件名防重复处理
     * @param fileName
     * @return
     */
    public static EncryptFile encryptFileFactory(String fileName) throws Exception {
        return encryptFileFactory(fileName,null);
    }

    /**
     * 文件名防重复处理，支持指定文档后缀
     * @param fileName
     * @return
     */
    public static EncryptFile encryptFileFactory(String fileName, String fileSuffix) throws Exception {
        if(StringUtils.isBlank(fileName)){
            throw new Exception("文件名不能为空！");
        }
        //后缀处理
        if(null != fileSuffix){
            String fileSuffixs = "";
            int fileLastd = fileName.lastIndexOf(".");
            if (fileLastd > 0) {
                fileSuffixs = fileName.substring(fileLastd);
            }
            //校验是否后缀名重复
            if(!fileSuffixs.equals(fileSuffix)){
                //不重复文件后缀处理
                fileName = fileName + fileSuffix;
            }
        }
        //名称处理 加雪花算法，防止并发下载重复
        String idStr = IdWorker.getIdStr();
        fileName = idStr + fileName;
        EncryptFile encryptFile = new EncryptFile(fileName);
        //判断是否存在
        if(encryptFile.exists()){
            // 如果存在将之前的文件操作删除
            encryptFile.delete();
        }
        //存入雪花算法下标
        encryptFile.setIdStrLength(idStr.length());
        return encryptFile;
    }

}
