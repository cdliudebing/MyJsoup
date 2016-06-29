package com.jdrx.phone.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PhoneEntity implements Serializable{

	/**
	 * @Fields serialVersionUID : (用一句话描述这个变量表示什么)
	 */ 
	private static final long serialVersionUID = 8586636529016791011L;
	//
	private Long id;
	// 品牌名称
	private String name;
	// 购进渠道
	private String channel;
	// 购进渠道Id
	private String channel_id;
	//型号ID
	private String model;
	//型号ID
	private String modelID;
	//产品ID
	private String productId;
	// 价格
	private String price;
	//地址链接
	private String urlDetail;
	
	//查询价格参数list
	private ArrayList<String> priceUnits;
	//查询价格参数
	private String parameterQuery;
	//所属网站（1：爱回收，2：易机网，3：淘绿网）
	private int flagType;
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

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getUrlDetail() {
		return urlDetail;
	}

	public void setUrlDetail(String urlDetail) {
		this.urlDetail = urlDetail;
	}

	public String getModelID() {
		return modelID;
	}

	public void setModelID(String modelID) {
		this.modelID = modelID;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getChannel_id() {
		return channel_id;
	}

	public void setChannel_id(String channel_id) {
		this.channel_id = channel_id;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}


	public ArrayList<String> getPriceUnits() {
		return priceUnits;
	}


	public void setPriceUnits(ArrayList<String> priceUnits) {
		this.priceUnits = priceUnits;
	}

	public String getParameterQuery() {
		return parameterQuery;
	}

	public void setParameterQuery(String parameterQuery) {
		this.parameterQuery = parameterQuery;
	}

	public int getFlagType() {
		return flagType;
	}

	public void setFlagType(int flagType) {
		this.flagType = flagType;
	}

	@Override
	public String toString() {
		return "PhoneEntity [id=" + id + ", name=" + name + ", channel=" + channel + ", channel_id=" + channel_id
				+ ", model=" + model + ", modelID=" + modelID + ", productId=" + productId + ", price=" + price
				+ ", urlDetail=" + urlDetail + ", priceUnits=" + priceUnits + ", parameterQuery=" + parameterQuery
				+ ", flagType=" + flagType + "]";
	}

}
