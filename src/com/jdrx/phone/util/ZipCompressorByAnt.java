package com.jdrx.phone.util;

import java.io.File;
import java.io.FileWriter;

import org.apache.tools.ant.Project;    
import org.apache.tools.ant.taskdefs.Zip;    
import org.apache.tools.ant.types.FileSet;    
    
/** 
 * @ClassName: ZipCompressorByAnt 
 * @CreateTime 2016-06-27 12:23:45 PM 
 * @author : Liu 
 * @Description: CSV文件追加内容 ，压缩文件的通用工具类-采用ant中的org.apache.tools.ant.taskdefs.Zip来实现，更加简单。 
 *  使用包：ant-1.6.5.jar
 * 
 */  
public class ZipCompressorByAnt {    
    
    private File zipFile;    
    /**
	 * @Title: parseString
	 * @Description: 双引号、单引号替换。双引号（"）要替换成 \"\"，单引号（'）要替换成 \'\'
	 * @param arg 要替换的字符串
	 * @return 返回替换以后的字符
	 */
	public String parseString(String arg){
		if(arg!=null){
			arg = arg.replace("\"", "\"\"");
			arg = arg.replace("\'", "\'\'");
			arg = "\""+arg +"\"";
		}
		
		return arg;
	}
	
	/**
	 * @Title: writeCsv
	 * @Description: CSV文件文件内容追加
	 * @param filePath 文件路径
	 */
    public void writeCsv(String filePath){
        try {
        	FileWriter filewriter = new FileWriter(filePath, true);
        	String a1 = "\"23,23\"";//内容前后 需加上（\"）
        	String a2="\'bbb,b";//双引号（"）要替换成 \"\"
        	a2 = parseString(a2);
        	String a3="\"cccc\"";
        	String outString =a1+","+a2+","+a3+"\r\n";//a1+a2+a3;
            filewriter.write(outString+"\r\n");
            filewriter.flush();
            filewriter.close(); 
         } catch (Exception e) {
            e.printStackTrace();  
         }  
    } 
    /** 
     * 构造函数 
     * @param pathName 最终压缩生成的压缩文件：目录+压缩文件名.zip 
     */  
    public ZipCompressorByAnt(String finalFile) {
        zipFile = new File(finalFile);
    }    
        
    /** 
     * 执行压缩操作 
     * @param srcPathName 需要被压缩的文件/文件夹 路径
     */  
    public void compressExe(String srcPathName) {    
        File srcdir = new File(srcPathName);    
        if (!srcdir.exists()){  
            throw new RuntimeException(srcPathName + "不存在！");    
        }
        Project prj = new Project();    
        Zip zip = new Zip();    
        zip.setProject(prj);    
        zip.setDestFile(zipFile);    
        FileSet fileSet = new FileSet();    
        fileSet.setProject(prj);    
        fileSet.setDir(srcdir);    
        //fileSet.setIncludes("**/*.java"); //包括哪些文件或文件夹 eg:zip.setIncludes("*.java");    
        //fileSet.setExcludes(...); //排除哪些文件或文件夹    
        zip.addFileset(fileSet);   
        zip.execute();    
    }    
    public static void main(String[] args) {  
    	ZipCompressorByAnt zc = new  ZipCompressorByAnt("D:\\test1\\test1.zip");
    	zc.writeCsv("D:\\test1\\test1.csv");
        zc.compressExe("D:\\test1");
    }
}   
