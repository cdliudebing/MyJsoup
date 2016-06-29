package com.jdrx.phone.util;

import java.io.Serializable;

/**
 * @ClassName: FileUtil
 * @Description: 附件属性
 * @author liudebing@evercreative.com.cn
 * @date 2016年5月27日 上午11:07:51
 *
 * @version 1.0.0
 */
public class MailUtil implements Serializable{
	

	/**
	 * @Fields serialVersionUID : (用一句话描述这个变量表示什么)
	 */ 
	private static final long serialVersionUID = -6469198549584285063L;

	/**
	 * 附件地址（包含了附件名称）如：D://test/test.txt
	 */
	private String affix;
	
	/**
	 * 附件名称
	 */
	private String affixName;

	public String getAffix() {
		return affix;
	}

	public void setAffix(String affix) {
		this.affix = affix;
	}

	public String getAffixName() {
		return affixName;
	}

	public void setAffixName(String affixName) {
		this.affixName = affixName;
	}

	@Override
	public String toString() {
		return "FileUtil [affix=" + affix + ", affixName=" + affixName + "]";
	}
	
}
