package com.jdrx.phone.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class IPUtil implements Serializable{

	/**
	 * @Fields serialVersionUID 
	 */ 
	private static final long serialVersionUID = -146222171797210413L;

	/** 代理IP */
	private String proxyIP;
	
	/** 代理端口 */
	private int port;

	public String getProxyIP() {
		return proxyIP;
	}

	public void setProxyIP(String proxyIP) {
		this.proxyIP = proxyIP;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public String toString() {
		return "IPUtil [proxyIP=" + proxyIP + ", port=" + port + "]";
	}
	public static void main(String[] args) {
		String str = "网络制式";
//		String[] arrays = str.split(";");
		System.out.println(str.contains("网络制式"));
		if(str.contains("购买渠道") || str.contains("网络制式")){
			System.out.println(str.replaceAll("制式", ""));
		}
		ArrayList<String> list = new ArrayList<String>();
		list.add("1111");
		list.add("2222");
		list.add("3333");
		list.add("4444");
		List<String> a= (List<String>) list.clone();
		list.set(0, "替换元素");
		System.out.println(list);
		System.out.println(a);
	}
}
