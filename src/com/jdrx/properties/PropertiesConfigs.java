package com.jdrx.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JOptionPane;

import java.util.Properties;
import java.util.Set;

import com.jdrx.phone.entity.IPUtil;

/**
 * @ClassName: PropertiesConfigs
 * @Description: 读取IP属性文件
 * @author liudebing@evercreative.com.cn
 * @date 2016年6月21日 下午3:56:30
 *
 * @version 1.0.0
 */
public class PropertiesConfigs implements Serializable {
	
	/**
	 * @Fields serialVersionUID
	 */ 
	private static final long serialVersionUID = -5652722746405094604L;

//	private static final Logger LOGGER = Logger.getLogger(PropertiesConfigs.class);
	
	private static final List<IPUtil> ipList = new ArrayList<IPUtil>();
	
	private static PropertiesConfigs propertiesConfigs = new PropertiesConfigs();
	
	public static PropertiesConfigs getInstance() {
		if(propertiesConfigs == null){
			return new PropertiesConfigs();
		}
		return propertiesConfigs;
	}
	public PropertiesConfigs(){
		InputStream inputStream = null;
		
		try {
			File directory = new File("");//参数为空
			//获取当前路径
			String courseFile = directory.getCanonicalPath();
			//获取当前路径下的ip.properties文件
			File ipConfig = new File(courseFile+"/ip.properties");
			inputStream = new FileInputStream(ipConfig);
//			inputStream = Object.class.getResourceAsStream("/ip.properties");;
			Properties properties = new Properties();
			properties.load(inputStream);
			Set<Entry<Object, Object>>  set = properties.entrySet();
			Iterator<Entry<Object, Object>> iterator = set.iterator();
			IPUtil ip =null;
			int i=0;
			while(iterator.hasNext()){
				i++;
				 Map.Entry<Object, Object> entry=(Map.Entry<Object, Object>)iterator.next();
			     String key = (String) entry.getKey();
			     String value = (String) entry.getValue();
			     ip = new IPUtil();
			     ip.setPort(Integer.parseInt(value));
			     ip.setProxyIP(key);
			     ipList.add(ip);
			}
		}catch(IOException e){
			e.printStackTrace();
//			LOGGER.error("PropertiesConfigs 加载配置文件异常。" + e);
		}finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public static List<IPUtil> getIPList() {
		return ipList;
	}
	 
//	public static void main(String[] args) throws Exception{
//		
//		try {
//			PropertiesConfigs pc = new PropertiesConfigs();
//			JOptionPane.showMessageDialog(new JOptionPane(), "courseFile=" + pc.getIPList().size(), "提示信息",
//					JOptionPane.ERROR_MESSAGE);
//		} catch (Exception e) {
//
//		}
		
//		InputStream input = Object.class.getResourceAsStream("/ip.properties");
//		Properties properties = new Properties();
//		properties.load(input);
//		Set<Entry<Object, Object>>  set = properties.entrySet();
//		Iterator<Entry<Object, Object>> iterator = set.iterator();
//		IPUtil ip =null;
//		int i=0;
//		while(iterator.hasNext()){
//			i++;
//			 Map.Entry<Object, Object> entry=(Map.Entry<Object, Object>)iterator.next();
//		     String key = (String) entry.getKey();
//		     String value = (String) entry.getValue();
//		     ip = new IPUtil();
//		     ip.setPort(Integer.parseInt(value));
//		     ip.setProxyIP(key);
//		     System.out.println("key="+key  +"       value="+value);
//		}
//		JOptionPane.showMessageDialog(new JOptionPane(),ip.toString(),
//				"提示信息", JOptionPane.ERROR_MESSAGE);
//		File file= new File("D:/ip.properties");
//		if(!file.exists()){
//			JOptionPane.showMessageDialog(new JOptionPane(),
//					"抓取程序已启动，请稍后在D盘查询结果（xls的文件）!"
//					+"\r\n查询（淘绿网、爱回收、回收宝、易机网）",
//					"提示信息", JOptionPane.ERROR_MESSAGE);
//		}else{
//			new InputStream(file);
//		}
//	}
	
}
