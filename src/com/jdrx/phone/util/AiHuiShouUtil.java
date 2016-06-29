package com.jdrx.phone.util;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jdrx.httpClinent.HttpClientPorxyUtil;
import com.jdrx.phone.entity.PhoneEntity;
import com.jdrx.properties.PropertiesConfigs;

public class AiHuiShouUtil implements Serializable {
	/**
	 * @Fields serialVersionUID : (用一句话描述这个变量表示什么)
	 */
	private static final long serialVersionUID = 9105904952773646545L;
	public static String baseUrl = "http://www.aihuishou.com"; // 爱回收 首页入口

	public static String formUrl2 = "/product/Search.html?c=1&p=";
	public static String formUrl = "/userinquiry/create.html"; // 爱回收查询商品回收单价
	public static int pageSize = 0;
	public static int pageNo = 1;
	public static int proxyIPCount = 0;
	/**
	 * 全部手机（手机不分型号、渠道 、网络制式）
	 */
	// public List<PhoneEntity> listPhone = new ArrayList<PhoneEntity>();
	/**
	 * 第一次获取失败 以后 重新获取数据的实体集合
	 */
	public static List<PhoneEntity> failurePhone = new ArrayList<PhoneEntity>();

	/**
	 * 查询到结果的 phoneEntity
	 */
	public static List<PhoneEntity> resultPhoneList = new ArrayList<PhoneEntity>();

	/**
	 * 查询到结果的 phoneEntity 失败的 放入此Map已便再次查询
	 */
	public static Map<Integer, PhoneEntity> finalFailurePhoneMap = new HashMap<Integer, PhoneEntity>();

	// 递归方法使用
	public static int count = 0;
	public static int documentCount = 0;

	public void queryAiHuiShou() {
		try {
			List<PhoneEntity> listEntitys = startQuery();
			List<PhoneEntity> resultEntitys = new ArrayList<PhoneEntity>();
			for (int i = 0; i < listEntitys.size(); i++) {
				PhoneEntity phone = listEntitys.get(i);
				try {
					System.out.println("获取手机详情" + phone);
					// 每款手机 每个型号和渠道组合成一个新手机（查询价格）
					paserPhoneDetail(phone, resultEntitys);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("===============-------" + phone.toString());
				}
			}
			//封装查询参数
			StringBuffer sb = null;
			for(int i=0;i<resultEntitys.size();i++){
				try{
					PhoneEntity entity = resultEntitys.get(i);
					List<String> parameterList = entity.getPriceUnits();
					sb = new StringBuffer();
					for(int j=0;j<parameterList.size();j++){
						
						if(j==(parameterList.size()-1)){
							sb.append(parameterList.get(j));
						}else{
							sb.append(parameterList.get(j)+";");
						}
					}
					entity.setParameterQuery(sb.toString());
					recursion(entity,0);
				}catch(Exception e){
					e.printStackTrace();
				}
				
			}
			ExcelUtil excelUtil = new ExcelUtil();
			
			Date date = new Date();
			SimpleDateFormat dateformat = new SimpleDateFormat("YYYYMMdd_HHmmss");
			excelUtil.outExcel(resultEntitys, "aihuishou_phone_"+dateformat.format(date)+".xls");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @Title: recursion
	 * @Description: 递归查询
	 */
	public void recursion(PhoneEntity entity,int count) {
		try{
			String price = queryPice(entity);
			if(price != null && !"".equals(price.trim())){
				entity.setPrice(price);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		count++;
		if(entity.getPrice()==null && count<10){
			recursion(entity,count);
		}
	}

	public Document getUrlDetail(String urlDetail) {
		Document doc = null;
		try {
			doc = Jsoup.connect(urlDetail).data("query", "Java") // 请求参数
					.userAgent("I ’ m jsoup") // 设置 // User-Agent
					.cookie("auth", "token") // 设置 cookie
					.timeout(30000) // 设置连接超时时间
					.post();// 使用 POST 方法访问 URL
		} catch (Exception e) {

		}
		if (doc == null && documentCount < 10) {
			documentCount++;
			try {
				Thread.sleep(5000);
				doc = getUrlDetail(urlDetail);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		documentCount = 0;
		return doc;
	}

	public Map<String, Object> paresDetail(String urlDetail) {
		try {
			Document doc1 = getDocument(urlDetail);
			// 获取每个手机的 产品 id
			String productId = doc1.getElementById("submit").attr("data-pid");
			// 封装请求数据
			Map<String, Object> map = new HashMap<String, Object>();
			Elements els = doc1.select("div.right ul.clearfix");

			// 获取 选择项 默认全部为第一项
			ArrayList<String> dataIdList = new ArrayList<String>();
			for (int i = 0; i < els.size(); i++) {
				dataIdList.add(els.get(i).child(0).attr("data-id"));
			}
			if (dataIdList.size() > 0) {
				dataIdList.remove(dataIdList.size() - 1);
			}
			// 获取 多选项 获取默认值
			Elements elsCheckBox = doc1.select("dl.checkbox ul.clearfix li");
			if (elsCheckBox != null && elsCheckBox.size() > 0) {
				for (int i = 0; i < elsCheckBox.size(); i++) {
					// System.out.println(elsCheckBox.get(i).attr("data-default")+","+elsCheckBox.get(i).attr("data-id"));
					dataIdList.add(elsCheckBox.get(i).attr("data-default"));
				}
			}

			// 获取有 网络制式 或者 型号的手机 已作循环查询
			Elements dt = doc1.select("div.right div.base-property dt");
			// System.out.println(dt);
			if (dt != null && dt.size() > 0) {
				String flag1 = dt.get(0).html();
				String flag2 = "";
				if (dt.size() > 2) {
					flag2 = dt.get(1).html();
				}
				// 当手机又购买渠道和型号双向选择时 需要交叉查询
				if (dt.size() > 2 && flag1.contains("购买渠道") && flag2.contains("型号")) {
					Elements channelsDt = doc1.select("div.right div.base-property dd");
					Elements childrens = channelsDt.get(1).children().get(0).children();
					if (childrens != null) {
						// 网络制式 或者型号 map（每个型号 或者每个网络制式 需要单独分成一个 PhoneEntity）
						Map<String, String> modelMap = new HashMap<String, String>();
						for (int i = 0; i < childrens.size(); i++) {
							modelMap.put(childrens.get(i).attr("data-id"), childrens.get(i).text());
						}
						map.put("modelMap", modelMap);
					}
				}
				if (flag1.contains("型号")) {
					// 获取 网络制式 或者 购买渠道 下数据
					Element channelsDt = doc1.select("div.right div.base-property dd").first();
					Elements childrens = channelsDt.children().get(0).children();
					if (childrens != null) {
						// 网络制式 或者型号 map（每个型号 或者每个网络制式 需要单独分成一个 PhoneEntity）
						Map<String, String> mapChannel = new HashMap<String, String>();
						for (int i = 0; i < childrens.size(); i++) {
							mapChannel.put(childrens.get(i).attr("data-id"), childrens.get(i).text());
						}
						map.put("modelMap", mapChannel);
					}
				}
				// 网络制式、 购买渠道、型号 需要循环查询()
				if (flag1.contains("购买渠道") || flag1.contains("网络制式")) {
					// 获取 网络制式 或者 购买渠道 下数据
					Element channelsDt = doc1.select("div.right div.base-property dd").first();
					Elements childrens = channelsDt.children().get(0).children();
					if (childrens != null) {
						// 网络制式 或者型号 map（每个型号 或者每个网络制式 需要单独分成一个 PhoneEntity）
						Map<String, String> mapChannel = new HashMap<String, String>();
						for (int i = 0; i < childrens.size(); i++) {
							mapChannel.put(childrens.get(i).attr("data-id"), childrens.get(i).text());
						}
						map.put("channelMap", mapChannel);
					}
				}

			}
			// 封装 请求form参数
			map.put("AuctionProductId", productId);
			map.put("ProductModelId", "");
			map.put("PriceUnits", dataIdList);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			// LOGGER.error("获取详情页面数据 error paresDetail", e);
		}
		return null;
	}

	/**
	 * @Title: paresResult
	 * @Description: 解析详情查询结果
	 * @param docResult
	 */
	public String paresResult(Document docResult) {
		try {
			// 解析当前页面数据
			Elements els = docResult.select("div.ql_left div.price");
			return els.get(0).html();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @Title: start(开始第一步)
	 * @Description: 开始抓取页面数据
	 */
	public List<PhoneEntity> startQuery() {
		try {
			// 获取总的产品 start
			Document docPage1 = getDocument(baseUrl + "/shouji");
			List<PhoneEntity> listPhone = new ArrayList<PhoneEntity>();
			if (docPage1 == null) {
				docPage1 = getUrlDetail(baseUrl + "/shouji");
			}
			// 获取第一页数据
			paseDoc(docPage1, true, listPhone);

			String detailUrl = "";
			// 分页获取数据 pageNo为总页数 从第二页开始获取
			for (int i = 1; i <= pageNo; i++) {
				detailUrl = baseUrl + "/shouji-p" + i;
				Document docPage = getUrlDetail(detailUrl);
				if (docPage == null) {
					documentCount = 0;
					docPage = getUrlDetail(detailUrl);
				}

				boolean formUrl = paseDoc(docPage, false, listPhone);
				if (!formUrl) {
					detailUrl = baseUrl + formUrl2 + i;
					docPage = getUrlDetail(detailUrl);
					if (docPage == null) {
						documentCount = 0;
						docPage = getUrlDetail(detailUrl);
					}
					paseDoc(docPage, false, listPhone);
				}
				System.out.println("已获取第 " + (i + 1) + " 页数据,已获取 " + listPhone.size() + " 条记录");
			}
			return listPhone;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据详情页面 获取最终价格
	 * 
	 * @Title: getPice
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param phone
	 * @param proxyIPCount
	 * @throws Exception
	 */
	public void paserPhoneDetail(PhoneEntity phone, List<PhoneEntity> resultEntitys) throws Exception {
		// 开始抓取详情页面信息 start
		Map<String, Object> map = paresDetail(phone.getUrlDetail());
		if (map == null) {
			throw new Exception("获取详情页面失败：" + phone);
		}
		String productId = (String) map.get("AuctionProductId");
		// String submitUrl = (String)map.get("submitUrl");
		ArrayList<String> dataIdArray = (ArrayList<String>) map.get("PriceUnits");
		Map<String, String> channelMap = (Map<String, String>) map.get("channelMap");
		Map<String, String> modelMap = (Map<String, String>) map.get("modelMap");
		// System.out.println("channelMap=======" + channelMap);
		// System.out.println("modelMap=======" + modelMap);
		// StringBuffer sb = new StringBuffer();
		// 只有渠道 没有型号
		if (channelMap != null && modelMap == null) {
			Iterator itChannel = channelMap.entrySet().iterator();
			while (itChannel.hasNext()) {
				Map.Entry entry = (Map.Entry) itChannel.next();
				Object channel_id = entry.getKey();
				Object channelText = entry.getValue();
				// System.out.println("data_id=" + channel_id + " channelText="
				// + channelText);
				// if(!channel_id.toString().equals(dataIdArray.get(0))){
				PhoneEntity entity = new PhoneEntity();
				entity.setName(phone.getName());
				entity.setUrlDetail(phone.getUrlDetail());
				entity.setChannel(channelText.toString());
				entity.setChannel_id(channel_id.toString());
				dataIdArray.set(0, channel_id.toString());
				entity.setProductId(productId);
				ArrayList<String> priceList = (ArrayList<String>) dataIdArray.clone();
				entity.setPriceUnits(priceList);
				resultEntitys.add(entity);
			}
			// 只有型号 没有渠道
		} else if (modelMap != null && channelMap == null) {
			Iterator itModel = modelMap.entrySet().iterator();
			while (itModel.hasNext()) {
				Map.Entry entry = (Map.Entry) itModel.next();
				Object modelId = entry.getKey();
				Object modelText = entry.getValue();
				// System.out.println("modelId=" + modelId + " modelText=" +
				// modelText);
				PhoneEntity entity = new PhoneEntity();
				entity.setName(phone.getName());
				entity.setUrlDetail(phone.getUrlDetail());
				entity.setModel(modelText.toString());
				entity.setModelID(modelId.toString());
				dataIdArray.set(0, modelId.toString());
				entity.setProductId(productId);
				ArrayList<String> priceList = (ArrayList<String>) dataIdArray.clone();
				entity.setPriceUnits(priceList);
				resultEntitys.add(entity);
			}
			// 有渠道 也有型号
		} else if (modelMap != null && channelMap != null) {
			Iterator itChannel = channelMap.entrySet().iterator();
			while (itChannel.hasNext()) {
				Map.Entry channel_entry = (Map.Entry) itChannel.next();
				Object channel_id = channel_entry.getKey();
				Object channelText = channel_entry.getValue();
				dataIdArray.set(0, channel_id.toString());
				Iterator itModel = modelMap.entrySet().iterator();
				while (itModel.hasNext()) {
					try {
						Map.Entry model_entry = (Map.Entry) itModel.next();
						Object model_id = model_entry.getKey();
						Object modelText = model_entry.getValue();
						PhoneEntity entity = new PhoneEntity();
						entity.setName(phone.getName());
						entity.setUrlDetail(phone.getUrlDetail());
						entity.setChannel(channelText.toString());
						entity.setChannel_id(channel_id.toString());
						entity.setModel(modelText.toString());
						entity.setModelID(model_id.toString());
						dataIdArray.set(1, model_id.toString());
						ArrayList<String> priceList = (ArrayList<String>) dataIdArray.clone();
						entity.setPriceUnits(priceList);
						entity.setProductId(productId);
						resultEntitys.add(entity);
					} catch (Exception e) {// 异常时 不管异常 ，继续下一循环
						// LOGGER.error("（有渠道 有型号）循环 型号时异常", e);
					}
				}
			}
			// 没有渠道 也没有型号
		} else {
			PhoneEntity entity = new PhoneEntity();
			entity.setName(phone.getName());
			entity.setUrlDetail(phone.getUrlDetail());
			entity.setChannel(phone.getChannel());
			entity.setChannel_id(phone.getChannel_id());
			entity.setModel(phone.getModel());
			entity.setModelID(phone.getModelID());
			ArrayList<String> priceList = (ArrayList<String>) dataIdArray.clone();
			entity.setPriceUnits(priceList);
			entity.setProductId(productId);
			resultEntitys.add(entity);
		}
	}

	/**
	 * 根据详情页面数据 请求查询 价格@Title: queryPice
	 * 
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param phones
	 * @param flag
	 *            第一次查询价格时为true 需要加入到 查询失败list
	 */
	public synchronized String queryPice(PhoneEntity phone) {
		Map<String, String> mapParameter = new HashMap<String, String>();
		String price = null;
		// 获取每部手机的最终 评估价格
		String result = null;
		if (proxyIPCount > PropertiesConfigs.getIPList().size()-5) {
			proxyIPCount = 0;
		}
		HttpClientPorxyUtil httpClinet = new HttpClientPorxyUtil();
		try {
			proxyIPCount++;
			mapParameter.put("AuctionProductId", phone.getProductId());
			mapParameter.put("ProductModelId", "");
			mapParameter.put("PriceUnits", phone.getParameterQuery());
			System.out.println("要查询的手机===" + phone);
			// 通过 httpClient访问网址 获取返回数据
			result = httpClinet.doPost(baseUrl + formUrl, mapParameter, "UTF-8",
					PropertiesConfigs.getIPList().get(proxyIPCount));
		} catch (Exception e) {// 第一次访问失败，换个代理IP再次访问
			proxyIPCount = proxyIPCount + 1;
			try {
				// 通过 httpClient访问网址 获取返回数据
				result = httpClinet.doPost(baseUrl + formUrl, mapParameter, "UTF-8",
						PropertiesConfigs.getIPList().get(proxyIPCount));
			} catch (Exception ex) {
				// LOGGER.error("获取最总数据 error paresResult" + phone, e);
			}
		}
		if (result != null && !"".equals(result.trim())) {
			try {
				Map<String, Object> resultMap = httpClinet.paresResult(result);
				if (resultMap.get("redirectUrl") != null && (Boolean) resultMap.get("success")) {
					Document docResult = getDocument(baseUrl + resultMap.get("redirectUrl").toString());
					price = paresResult(docResult);
					if (price == null || "".equals(price)) {
						throw new Exception("价格获取失败");
					}
				}
			} catch (Exception e) {
				// 获取失败 放入失败集合 等待再次获取数据
				e.printStackTrace();
			}
		}
		return price;
	}

	/**
	 * @Title: getDocument
	 * @Description: 获取网页数据
	 * @param url
	 *            要抓取的网页URL
	 * @return 返回Document格式的数据
	 */
	public Document getDocument(String url) {
		try {
			Document doc = Jsoup.connect(url).data("query", "Java") // 请求参数
					.userAgent("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0; MALC)") // 设置
																										// //
																										// User-Agent
					.cookie("auth", "token") // 设置 cookie
					.timeout(30000) // 设置连接超时时间
					.post();// 使用 POST 方法访问 URL
			if(doc == null && documentCount<10){
				documentCount++;
				doc = getDocument(url);
			}
			documentCount = 0;
			return doc;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @Title: paseDoc
	 * @Description: (分页)解析 Document 并封装数据 数据用作抓取详情页面
	 * @param doc1
	 *            要解析的Document
	 * @param flag
	 */
	public boolean paseDoc(Document doc1, boolean flag, List<PhoneEntity> listPhone) {
		boolean flagForm = false;
		try {
			// 是否第一页 第一页需获取到总页数
			if (flag) {
				// 获取分页条码和 分页
				Elements a_els = doc1.select("a.page");
				pageSize = a_els.size();
				for (int i = 0; i < pageSize; i++) {
					Element element = a_els.get(i);
					// System.out.println("href==========" + element.html() + "
					// i=" + i);
					if (i == (pageSize - 1)) {
						String pageNoStr = element.html();
						pageNo = Integer.parseInt(pageNoStr);
					}
				}
			}

			// 解析当前页面数据
			Elements els = doc1.select("ul.products li a");
			for (Element element : els) {
				flagForm = true;
				String href = element.attr("href");
				String title = element.attr("title");
				PhoneEntity phone = new PhoneEntity();
				if (title != null && title.contains("回收")) {
					title = title.replaceAll("回收", "").trim();
				}
				phone.setName(title);
				phone.setUrlDetail(baseUrl + href);
				listPhone.add(phone);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flagForm;
	}
}
