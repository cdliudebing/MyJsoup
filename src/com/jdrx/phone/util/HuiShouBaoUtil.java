package com.jdrx.phone.util;


import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jdrx.httpClinent.HttpClientUtil;
import com.jdrx.phone.entity.Phone;
import com.jdrx.phone.entity.PhoneEntity;

public class HuiShouBaoUtil {
	public static String baseUrl = "http://www.huishoubao.com";
	public static String prouductUrl = "http://www.huishoubao.com/fendi/fendi/index/query?pid=1001";
	
	public static String prouductPriceUrl = "http://www.huishoubao.com/fendi/fendi/index/quote/?";
	public static int count = 0;

	/**
	 * 第一位：国行，第二位：港行，第三位：其它国家（有锁），第一位：其它国家（无锁）
	 */
	public static Double[] priceArray = new Double[] { 1.00, 0.76, 0.36, 0.86 };
	/**
	 * 全部手机（手机不分型号、渠道 、网络制式）
	 */
	public List<PhoneEntity> listPhone = new ArrayList<PhoneEntity>();
	
	/**
	 * @Title: serchPhone
	 * @Description: (这里用一句话描述这个方法的作用)
	 */
	public void queryHuiShouBao() {
		Document doc = getDocument(prouductUrl);
		List<Phone> listPhone = new ArrayList<Phone>();
		// 获取 分页产品列表
		Elements liElements = doc.select("div.col-wrap div.col-10 ul a");
		if (liElements != null) {
			for (int i = 0; i < liElements.size(); i++) {
				Phone entity = new Phone();
				Element elemtnt = liElements.get(i);
				String url = elemtnt.attr("href");
				String name = elemtnt.html();
				entity.setName(name);
				entity.setUrlDetail(url);
				listPhone.add(entity);
			}
		}
		List<Phone> phoneList = new ArrayList<Phone>();
		for (Phone phone : listPhone) {
			try {
				doc = getDocument(baseUrl + phone.getUrlDetail());
				serchPagePhone(doc, phone, 0, phoneList);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		List<PhoneEntity> phoneEntityList = new ArrayList<PhoneEntity>();
		queryPrice(phoneList,phoneEntityList);
		ExcelUtil excelUtil = new ExcelUtil();
		
		Date date = new Date();
		SimpleDateFormat dateformat = new SimpleDateFormat("YYYYMMdd_HHmmss");
		excelUtil.outExcel(phoneEntityList, "huishoubao_phone_"+dateformat.format(date)+".xls");
		
	}
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
																										// //
																										// User-Agent
					.cookie("auth", "token") // 设置 cookie
					.timeout(30000) // 设置连接超时时间
					.post();// 使用 POST 方法访问 URL

		} catch (IOException e) {
		}
		// 如果根据URL 获取10次都失败则不再获取数据
		if (doc == null && count < 10) {
			count++;
			doc = getDocument(url);
		}
		count = 0;
		return doc;
	}

	// 查询每个品牌手机 包括分页
	public void serchPagePhone(Document doc, Phone entity, int count, List<Phone> phoneEntitys) {
		try {
			String pageUrl = null;
			count ++;
			Phone phoneEntity = null;
			// doc = getDocument(baseUrl + entity.getUrlDetail());
			// 取得元素
			Elements els = doc.select("div.query-products div.col-wrap li");
			// 获取分页以后的列表
			for (Element el : els) {
				String url = el.child(0).attr("href");
				String name = el.child(1).html();
				System.out.println(name + "   " + url);
				// 如果是IPAD则不加入phoneEntitys（不查询价格）
				if (name.contains("iPad")) {
					continue;
				}
				// 获取当前页数据
				if (url != null && url.contains("skuid=sku")) {
					Document doc1 = getDocument(baseUrl + url);
					Elements skuidEls = doc1.select("div.query-products div.col-wrap li");
					for (Element element : skuidEls) {
						String skuidElsUrl = element.child(0).attr("href");
						String skuidElsName = element.child(1).html();
						phoneEntity = new Phone();
						phoneEntity.setUrlDetail(skuidElsUrl);
						phoneEntity.setName(skuidElsName);
						phoneEntitys.add(phoneEntity);
					}
				} else {
					phoneEntity = new Phone();
					phoneEntity.setUrlDetail(url);
					phoneEntity.setName(name);
					phoneEntitys.add(phoneEntity);
				}
			}
			// 获取是否有下一页分页数据
			Elements pageElements = doc.select("div.page div.item-page a");
			if (pageElements != null) {
				Element element = pageElements.get(pageElements.size() - 1);
				String flagPage = element.html();

				if (flagPage.contains("下一页")) {
					pageUrl = element.attr("href");
				}
				if (!pageUrl.contains("/fendi/fendi/index/query/")) {
					return;
				}
			}
			if (pageUrl != null && count < 26) {
				// 获取品牌分页数据
				doc = getDocument(baseUrl + "/" + pageUrl);
				serchPagePhone(doc, entity, count, phoneEntitys);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("--------------count=" + count + doc);
		}

	}

	public void queryPrice(List<Phone> phoneList,List<PhoneEntity> phoneEntityList) {
		Document doc = null;
		String pid = "";
		String itemid = "";
		String productname = "";
		String maxprice = "";
		String picurl = "";
		StringBuffer sb = new StringBuffer();
		String name = "";
		PhoneEntity phoneEntity = null;
		for (Phone entity : phoneList) {
			try {
				sb = new StringBuffer();
				doc = getDocument(baseUrl + entity.getUrlDetail());
				Elements inputs = doc.select("input");
				for (Element element : inputs) {
					if ("pid".equals(element.attr("name"))) {
						pid = element.attr("value");
					}
					if ("itemid".equals(element.attr("name"))) {
						itemid = element.attr("value");
					}
					if ("productname".equals(element.attr("name"))) {
						productname = URLEncoder.encode(element.attr("value"), "UTF-8");
					}
					if ("maxprice".equals(element.attr("name"))) {
						maxprice = URLEncoder.encode(element.attr("value"), "UTF-8");
					}
					if ("picurl".equals(element.attr("name"))) {
						picurl = URLEncoder.encode(element.attr("value"), "UTF-8");
						break;
					}
				}
				Elements lis = doc.select("div.assess-main-leftline li");
				if (lis != null && lis.size() > 0) {
					String isChannel = lis.get(0).child(0).html();
					for (int i = 1; i < lis.size(); i++) {
						Element radio = lis.get(i).child(1).child(0).child(0);
						if (i != lis.size() - 1) {
							sb.append(radio.attr("name") + "=" + URLEncoder.encode(radio.attr("value"), "UTF-8") + "&");
						} else {
							sb.append(radio.attr("name") + "=" + URLEncoder.encode(radio.attr("value"), "UTF-8"));
						}
							
					}
					//如果有渠道 需按渠道查询价格
					if (isChannel.contains("购买渠道")) {
						String value1 = null;
						String value2 = null;
						String value3 = null;
						name = "qst_140";
						value1 = URLEncoder.encode("141_国行有保1个月以上", "UTF-8");
						value2 = URLEncoder.encode("142_港行有保1个月以上", "UTF-8");
						value3 = URLEncoder.encode("143_其他", "UTF-8");
						try {
							String url = null;
							if(value1 != null){
								phoneEntity = new PhoneEntity();
								phoneEntity.setChannel("国行有保1个月以上");
								if(entity.getName().contains("三星") || entity.getName().contains("诺基亚")){
									name = "qst_110";
									phoneEntity.setChannel("大陆国行");
									value1 = URLEncoder.encode("111_大陆国行", "UTF-8");
								}
								url = null;
								url = "pid=" + pid + "&itemid=" + itemid + "&productname=" + productname + "&maxprice=" + maxprice
										+ "&picurl=" + picurl + "&" + name + "=" + value1 + "&" + sb.toString();
								url = prouductPriceUrl + url;
								String price = getPrice(url,doc,0);
								phoneEntity.setName(entity.getName());
								phoneEntity.setPrice(price);
								phoneEntity.setFlagType(4);
								phoneEntity.setProductId(itemid);
								phoneEntity.setUrlDetail(baseUrl+entity.getUrlDetail());
								phoneEntityList.add(phoneEntity);
							}
							if(value2 != null){
								url = null;
								phoneEntity = new PhoneEntity();
								phoneEntity.setChannel("港行有保1个月以上");
								if(entity.getName().contains("三星") || entity.getName().contains("诺基亚")){
									name = "qst_110";
									phoneEntity.setChannel("香港行货");
									value2 = URLEncoder.encode("112_香港行货", "UTF-8");
								}
								url = "pid=" + pid + "&itemid=" + itemid + "&productname=" + productname + "&maxprice=" + maxprice
										+ "&picurl=" + picurl + "&" + name + "=" + value2 + "&" + sb.toString();
								url = prouductPriceUrl + url;
								String price = getPrice(url,doc,0);
								phoneEntity.setName(entity.getName());
								phoneEntity.setPrice(price);
								phoneEntity.setFlagType(4);
								phoneEntity.setProductId(itemid);
								phoneEntity.setUrlDetail(baseUrl+entity.getUrlDetail());
								phoneEntityList.add(phoneEntity);
							}
							if(value3 != null){
								url = null;
								phoneEntity = new PhoneEntity();
								phoneEntity.setChannel("其他");
								if(entity.getName().contains("三星") || entity.getName().contains("诺基亚")){
									name = "qst_113";
									value3 = URLEncoder.encode("112_其他", "UTF-8");
								}
								url = "pid=" + pid + "&itemid=" + itemid + "&productname=" + productname + "&maxprice=" + maxprice
										+ "&picurl=" + picurl + "&" + name + "=" + value3 + "&" + sb.toString();
								url = prouductPriceUrl + url;
								String price = getPrice(url,doc,0);
								phoneEntity.setName(entity.getName());
								phoneEntity.setPrice(price);
								phoneEntity.setFlagType(4);
								phoneEntity.setProductId(itemid);
								phoneEntity.setUrlDetail(baseUrl+entity.getUrlDetail());
								phoneEntityList.add(phoneEntity);
							}
						} catch (Exception e) {

						}
					} else {
						String url = null;
						Element radio = lis.get(0).child(1).child(0).child(0);
						url = radio.attr("name") + "=" + URLEncoder.encode(radio.attr("value"), "UTF-8");
						url = "pid=" + pid + "&itemid=" + itemid + "&productname=" + productname + "&maxprice=" + maxprice
								+ "&picurl=" + picurl + "&" + url + "&" + sb.toString();
						url = prouductPriceUrl + url;
						String price = getPrice(url,doc,0);
						phoneEntity = new PhoneEntity();
						phoneEntity.setName(entity.getName());
						phoneEntity.setPrice(price);
						phoneEntity.setFlagType(4);
						phoneEntity.setProductId(itemid);
						phoneEntity.setUrlDetail(baseUrl+entity.getUrlDetail());
						phoneEntityList.add(phoneEntity);
					}
					
				}else{
					String url = "pid=" + pid + "&itemid=" + itemid + "&productname=" + productname + "&maxprice=" + maxprice
							+ "&picurl=" + picurl;
					String price = getPrice(url,doc,0);
					phoneEntity = new PhoneEntity();
					phoneEntity.setName(entity.getName());
					phoneEntity.setPrice(price);
					phoneEntity.setFlagType(4);
					phoneEntity.setProductId(itemid);
					phoneEntity.setUrlDetail(baseUrl+entity.getUrlDetail());
					phoneEntityList.add(phoneEntity);
				}
			} catch (Exception e) {
				e.printStackTrace();
				try{
					phoneEntity = new PhoneEntity();
					phoneEntity.setName(entity.getName());
					phoneEntity.setPrice(null);
					phoneEntity.setFlagType(4);
					phoneEntity.setUrlDetail(baseUrl+entity.getUrlDetail());
					phoneEntity.setProductId(itemid);
					phoneEntityList.add(phoneEntity);
				}catch(Exception e1){
					e1.printStackTrace();
				}
			}

		}
	}
	public String getPrice(String url,Document doc,int count){
		count ++;
		String price = null;
		try{
			String resutl = new HttpClientUtil().httpGet(url);
			doc = Jsoup.parse(resutl);
			price  = doc.select("div.mobile-price-info span.mobile-price-num").html();
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("price=" + price);
		if(price == null && count<3){
			price  = getPrice(url,doc,count);
		}
		return price;
	}
//	public static void main(String[] args) {
//		HuiShouBaoJsoup huishoubao = new HuiShouBaoJsoup();
//		huishoubao.serchPhone();
		//List<PhoneEntity> phoneEntityList = new ArrayList<PhoneEntity>();
//		String urlDetail = "/fendi/fendi/index/estimate/?pid=1001&itemid=47";
		//PhoneEntity entity = new PhoneEntity();
//		entity.setUrlDetail(urlDetail);
//		phoneEntityList.add(entity);
		//huishoubao.queryPrice(phoneEntityList);

//		Document doc = huishoubao.getDocument(prouductUrl);
		//phoneEntityList = huishoubao.serchPhone();
//		System.out.println(doc);
		// huishoubao.serchPagePhone(doc,new ArrayList<Phone>(),0,null);
		// Document doc =
		// taolv.getDocument("http://tf.taolv365.com/ProductPrice_7185.html");
		// Elements els = doc.select("div.pro_cont_list dl");
		// Element el = doc.getElementById("spanProdPrice2");
		// System.out.println(els.size());
		// Element el1 = els.get(0);
		// System.out.println(el.html());
		// System.out.println(el1.html());
//	}
}
