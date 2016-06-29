package com.jdrx.phone.util;

import java.io.IOException;
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

import com.jdrx.httpClinent.HttpClientUtil;
import com.jdrx.phone.entity.PhoneEntity;

public class YijiJsoupUtil {
	public static String baseUrl = "http://www.58yiji.com";
	public static String phone_URL = "shouji";
	public static String pageUrl = "http://www.58yiji.com/ajax/proList.html";
	public static String formUrl = "http://www.58yiji.com/doGuJia.html";
	public static int count = 0;
	public static int priceCount = 0;
	/**
	 * 全部手机（手机不分型号、渠道 、网络制式）
	 */
	public static List<PhoneEntity> listPhone = new ArrayList<PhoneEntity>();
	// 最终要查询的手机
	public static List<PhoneEntity> detailSumPhone = new ArrayList<PhoneEntity>();

	/**
	 * @Title: getDocument
	 * @Description: 获取网页数据
	 * @param url
	 *            要抓取的网页URL
	 * @return 返回Document格式的数据
	 */
	private Document getDocument(String url) {
		Document doc = null;
		try {
			doc = Jsoup.connect(url).data("query", "Java") // 请求参数
					.userAgent("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0; MALC)") // 设置
					.cookie("auth", "token") // 设置 cookie
					.timeout(30000) // 设置连接超时时间
					.post();// 使用 POST 方法访问 URL

		} catch (IOException e) {
		}
		if (doc == null && count < 10) {
			count++;
			doc = getDocument(url);
		}
		count = 0;
		return doc;
	}

	/**
	 * @Title: recursion
	 * @Description: 递归查询手机价格（查询10次）如果10次还没查询到价格 则不再查询
	 * @param url
	 * @param mapParameter
	 * @return
	 */
	public String recursion(String url, Map<String, String> mapParameter) {
		String result = null;
		try {
			result = HttpClientUtil.httpPost(url, mapParameter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		count++;
		if (result == null && count < 10) {
			result = recursion(url, mapParameter);
		}
		return result;
	}

	/**
	 * @Title: paresPagePhone
	 * @Description: 解析分页获取的手机列表
	 * @param result
	 *            分页获取到得数据
	 */
	public void paresPagePhone(String result, List<PhoneEntity> listPhone) {
		Document doc = Jsoup.parse(result);
		if (doc != null) {
			Elements els = doc.select("li");
			for (int i = 0; i < els.size(); i++) {
				PhoneEntity entity = new PhoneEntity();
				String href = els.get(i).child(0).attr("href");
				String name = els.get(i).child(0).child(1).html();
				String miaoshu = els.get(i).child(0).child(2).html();
				if (miaoshu.contains("当前平板")) {
					continue;
				}
				entity.setName(name);
				entity.setUrlDetail(baseUrl + href);
				listPhone.add(entity);
			}
		}
	}

	/**
	 * @Title: savePhone
	 * @Description: 根据（型号+渠道）拆分组合成新的手机
	 * @param phone 需要拆分的手机
	 * @param map 拆分条件
	 * @param savePhone 组合后的新手机list
	 * @return
	 */
	public List<PhoneEntity> savePhone(PhoneEntity phone, Map<String, Object> map, List<PhoneEntity> savePhone) {
		Map<String, String> channelMap = (Map<String, String>) map.get("channelMap");
		Map<String, String> modelMap = (Map<String, String>) map.get("modelMap");
		ArrayList<String> list = (ArrayList<String>) map.get("list");
		// 只有渠道 没有型号
		if (channelMap != null && modelMap == null) {
			Iterator itChannel = channelMap.entrySet().iterator();
			while (itChannel.hasNext()) {
				Map.Entry entry = (Map.Entry) itChannel.next();
				Object channel_id = entry.getKey();
				Object channelText = entry.getValue();
				if (channel_id == null || "".equals(channelText.toString().trim())) {
					continue;
				}
				PhoneEntity entity = new PhoneEntity();
				entity.setName(phone.getName());
				entity.setUrlDetail(phone.getUrlDetail());
				entity.setChannel(channelText.toString());
				entity.setChannel_id(channel_id.toString());
				entity.setProductId(phone.getProductId());
				entity.setFlagType(2);
				list.set(0, channel_id.toString());
				ArrayList<String> priceList = (ArrayList<String>) list.clone();
				entity.setPriceUnits(priceList);
				savePhone.add(entity);
			}
			// 只有型号 没有渠道
		} else if (modelMap != null && channelMap == null) {
			Iterator itModel = modelMap.entrySet().iterator();
			while (itModel.hasNext()) {
				Map.Entry entry = (Map.Entry) itModel.next();
				Object modelId = entry.getKey();
				Object modelText = entry.getValue();
				if (modelId == null || "".equals(modelId.toString().trim())) {
					continue;
				}
				PhoneEntity entity = new PhoneEntity();
				entity.setName(phone.getName());
				entity.setUrlDetail(phone.getUrlDetail());
				entity.setModel(modelText.toString());
				entity.setModelID(modelId.toString());
				entity.setProductId(phone.getProductId());
				entity.setFlagType(2);
				list.set(0, modelId.toString());
				ArrayList<String> priceList = (ArrayList<String>) list.clone();
				entity.setPriceUnits(priceList);
				savePhone.add(entity);

			}
			// 有渠道 也有型号
		} else if (modelMap != null && channelMap != null) {
			Iterator itModel = modelMap.entrySet().iterator();
			while (itModel.hasNext()) {
				Map.Entry model_entry = (Map.Entry) itModel.next();
				Object model_id = model_entry.getKey();
				Object modelText = model_entry.getValue();
				if (model_id == null || "".equals(model_id.toString().trim())) {
					continue;
				}
				list.set(0, model_id.toString());
				Iterator itChannel = channelMap.entrySet().iterator();
				while (itChannel.hasNext()) {
					Map.Entry channel_entry = (Map.Entry) itChannel.next();
					Object channel_id = channel_entry.getKey();
					Object channelText = channel_entry.getValue();
					if (channel_id == null || "".equals(channelText.toString().trim())) {
						continue;
					}
					PhoneEntity entity = new PhoneEntity();
					entity.setName(phone.getName());
					entity.setUrlDetail(phone.getUrlDetail());
					entity.setChannel(channelText.toString());
					entity.setChannel_id(channel_id.toString());
					entity.setModel(modelText.toString());
					entity.setModelID(model_id.toString());
					entity.setProductId(phone.getProductId());
					entity.setFlagType(2);
					list.set(1, channel_id.toString());
					ArrayList<String> priceList = (ArrayList<String>) list.clone();
					entity.setPriceUnits(priceList);
					savePhone.add(entity);
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
			entity.setProductId(phone.getProductId());
			ArrayList<String> priceList = (ArrayList<String>) list.clone();
			entity.setPriceUnits(priceList);
			savePhone.add(entity);
		}
		return savePhone;
	}

	/**
	 * @Title: parsePhoneDetail
	 * @Description: 手机根据型号和购买渠道拆分成单个手机
	 * @param entity
	 *            要拆分的手机
	 * @param savePhone
	 *            拆分后的手机存入此list
	 * @return
	 */
	public void parsePhoneDetail(PhoneEntity entity, List<PhoneEntity> savePhone) {
		try {
			Document doc = null;
			ArrayList<String> list = null;
			doc = getDocument(entity.getUrlDetail());
			// 获取手机 产品编码
			String productId = doc.getElementById("togujia").attr("gujiaPid");
			entity.setProductId(productId);
			Elements els = doc.select("div.step-left div.stepSubitem dl");
			Map<String, String> modelMap = null;
			Map<String, String> channelMap = null;
			Map<String, Object> map = new HashMap<String, Object>();
			list = new ArrayList<String>();
			// els == 0 表示只有多选项
			if (els.size() == 0) {

			} else {
				for (int i = 0; i < els.size(); i++) {
					Element element = els.get(i);
					String xinghao = element.child(0).html();
					if (i == els.size() - 1) {
						break;
					}
					// 获取每个选项的第一个值
					Elements childrens = element.child(1).child(0).children();
					list.add(childrens.get(0).attr("pid"));
					// 获取型号
					if (xinghao.contains("型号")) {
						modelMap = new HashMap<String, String>();
						for (int j = 0; j < childrens.size(); j++) {
							modelMap.put(childrens.get(j).attr("pid"), childrens.get(j).text());
						}
						map.put("modelMap", modelMap);
					}
					// 获取渠道
					if (xinghao.contains("购买渠道")) {
						channelMap = new HashMap<String, String>();
						for (int j = 0; j < childrens.size(); j++) {
							channelMap.put(childrens.get(j).attr("pid"), childrens.get(j).text());
						}
						map.put("channelMap", channelMap);
					}
				}
			}
			map.put("list", list);
			savePhone(entity, map, savePhone);
		} catch (Exception e) {
			System.out.println("按渠道拆分数据异常："+entity.toString());
			e.printStackTrace();
		}
	}

	/**
	 * @Title: getPrice
	 * @Description: 获取手机价格
	 * @param phone
	 * @return
	 */
	public synchronized String getPrice(PhoneEntity phone) {
		String price = null;
		try {
			Map<String, String> map = new HashMap<String, String>();
			map.put("modelId", phone.getProductId());
			map.put("subOptions", phone.getParameterQuery());
			map.put("plusPrice", "0");
			count = 0;
			String result = recursion(formUrl, map);
			if (result != null && !"".equals(result)) {
				Document doc = Jsoup.parse(result);
				Elements elements = doc.select("div.buyr div.buytop strong.color");
				price = elements.get(0).html();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return price;
	}

	/**
	 * @Title: queryListPhone
	 * @Description: 查询所有手机，手机部分型号和渠道
	 * @return 返回所有待查询价格的手机（部分渠道和型号）
	 */
	public List<PhoneEntity> queryListPhone() {
		Map<String, String> mapParameter = new HashMap<String, String>();
		List<PhoneEntity> listPhone = new ArrayList<PhoneEntity>();
		for (int i = 1; i <= 88; i++) {
			mapParameter.put("offset", "" + i);
			mapParameter.put("brandId", "");
			mapParameter.put("proName", "");
			mapParameter.put("sortId", "18");
			count = 0;
			String result = recursion(pageUrl, mapParameter);

			paresPagePhone(result, listPhone);
			System.out.println("易机网 第" + i + "页数据获取完成.........");
		}
		return listPhone;
	}

	/**
	 * @Title: queryYiJiPhone
	 * @Description: 开始抓取数据
	 */
	public void queryYiJiPhone() {

		List<PhoneEntity> listEntitys = queryListPhone();

		List<PhoneEntity> resultList = new ArrayList<PhoneEntity>();
		// 按购买渠道和型号拆分手机
		for (PhoneEntity entity : listEntitys) {
			parsePhoneDetail(entity, resultList);
		}

		String price = null;
		StringBuffer sb = null;
		// 查询手机价格
		for (PhoneEntity entity : resultList) {
			List<String> parameterList = entity.getPriceUnits();
			sb = new StringBuffer();
			for (int j = 0; j < parameterList.size(); j++) {

				if (j == (parameterList.size() - 1)) {
					sb.append(parameterList.get(j));
				} else {
					sb.append(parameterList.get(j) + ",");
				}
			}
			entity.setParameterQuery(sb.toString());
			price = getPrice(entity);
			if (price != null) {
				entity.setPrice(price);
			}
		}
		ExcelUtil excelUtil = new ExcelUtil();
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("YYYYMMdd_HHmmss");
		excelUtil.outExcel(resultList, "yiji_phone_" + df.format(date) + ".xls");
	}

}
