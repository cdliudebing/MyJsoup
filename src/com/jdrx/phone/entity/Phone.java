package com.jdrx.phone.entity;

import java.io.Serializable;

public class Phone implements Serializable{
	
	/**
	 * @Fields serialVersionUID : (用一句话描述这个变量表示什么)
	 */ 
	private static final long serialVersionUID = -4259001885657587166L;

	private Long id;//���
	
	private String name;//����
	
	//地址链接
	private String urlDetail;
		
	//所属网站（1：爱回收，2：易机网，3：淘绿网）
	private int flagType;

	/**
	 * 用作手机价格是否查询标记（0：未查询，1：已查询）
	 */
	private int marking;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrlDetail() {
		return urlDetail;
	}

	public void setUrlDetail(String urlDetail) {
		this.urlDetail = urlDetail;
	}

	public int getFlagType() {
		return flagType;
	}

	public void setFlagType(int flagType) {
		this.flagType = flagType;
	}

	public int getMarking() {
		return marking;
	}

	public void setMarking(int marking) {
		this.marking = marking;
	}

	@Override
	public String toString() {
		return "Phone [id=" + id + ", name=" + name + ", urlDetail=" + urlDetail + ", flagType=" + flagType
				+ ", marking=" + marking + "]";
	}

}
