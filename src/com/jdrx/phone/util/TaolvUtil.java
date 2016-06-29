package com.jdrx.phone.util;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jdrx.phone.entity.Phone;
import com.jdrx.phone.entity.PhoneEntity;

public class TaolvUtil {
	public static String baseUrl = "http://tf.taolv365.com";

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
	 * @Title: getDocument
	 * @Description: 获取网页数据
	 * @param url
	 *            要抓取的网页URL
	 * @return 返回Document格式的数据
	 */
	public Document getDocument(String url) {
		Document doc = null;
		try {
			doc = Jsoup.connect(url).data("query", "Java") // 请求参数
					.userAgent("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0; MALC)") // 设置User-Agent
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
	public void serchPagePhone(Document doc, List<Phone> listPhone, int count) {
		String pageUrl = null;
		// 获取 分页产品列表
		Elements liElements = doc.select("div.Inside_Brand_02 li");
		if (liElements != null) {
			for (int j = 0; j < liElements.size(); j++) {
				Phone entity = new Phone();
				Element elemtnt = liElements.get(j).child(0).child(0);
				String url = elemtnt.attr("href");
				String name = elemtnt.attr("title");
				entity.setName(name);
				entity.setUrlDetail(url);
				entity.setFlagType(3);
				listPhone.add(entity);
			}
		}
		count++;
		// 获取 产品列表
		Elements pageElements = doc.select("div.scott div.paginator-box div.plist_page a");
		if (pageElements != null && pageElements.size() > 1) {
			Element element = pageElements.get(pageElements.size() - 1);
			String flagPage = element.html();

			if (flagPage.contains("下一页")) {
				pageUrl = element.attr("href");
			}
		}
		if (pageUrl != null && count < 12) {
			// 获取品牌分页数据
			doc = getDocument(baseUrl + "/" + pageUrl);
			serchPagePhone(doc, listPhone, count);
		}
	}

	// 循环计算 选择了渠道的价格
	public void pasePhone(Phone entity, List<PhoneEntity> detailPhone, String price, DecimalFormat df) {
		if (price != null && !"".equals(price.trim())) {
			Double priceDou = Double.parseDouble(price);
			price = df.format(priceDou);
		}
		// 循环计算 选择了渠道的价格
		for (int i = 0; i < priceArray.length; i++) {
			PhoneEntity entityDetail = new PhoneEntity();
			entityDetail.setName(entity.getName());
			entityDetail.setUrlDetail(entity.getUrlDetail());
			entityDetail.setFlagType(3);
			entityDetail.setPrice(price);
			String price1 = null;
			try {
				// 判断渠道
				switch (i) {
				case 0:// 当为国行时 需要计算成色 （即新旧程度）
					entityDetail.setChannel("国行");
					if (price != null && !"".equals(price.trim())) {
						Double c = 0.99;
						try {
							Double priceDou = Double.parseDouble(price);
							price1 = df.format(c * priceDou);
						} catch (Exception e) {

						}
					}
					break;
				case 1:
					entityDetail.setChannel("港行");
					break;
				case 2:
					entityDetail.setChannel("其它国家（有锁）");
					break;
				case 3:
					entityDetail.setChannel("其它国家（无锁）");
					break;
				default:
					break;
				}
				// 根据 渠道计算价格
				if (price != null && i != 0) {
					Double priceDou = Double.parseDouble(price);
					price1 = df.format(priceArray[i] * priceDou);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			entityDetail.setPrice(price1);
			detailPhone.add(entityDetail);
		}
	}

	/**
	 * @Title: queryPhone
	 * @Description: 查询 每个品牌手机 不分渠道
	 */
	public void queryTaoLvPhone() {
		Document doc = getDocument(TaolvUtil.baseUrl + "/Search_1___1_.html");
		// 获取每个品牌
		Elements elements = doc.select("div.Inside_right div.Inside_Brand_01 li a");
		try {
			// 获取总的手机（没分渠道）
			List<Phone> listPhone = new ArrayList<Phone>();
			for (int i = 0; i < elements.size(); i++) {
				// 获取品牌分页数据
				doc = getDocument(TaolvUtil.baseUrl + elements.get(i).attr("href"));
				serchPagePhone(doc, listPhone, 0);
			}
			queryListPhone(listPhone);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 淘绿网 开始查询
	public void queryListPhone(List<Phone> phoneList) {
		try {
			// 获取每个品牌手机 循环并根据渠道计算价格
			String price = null;
			List<PhoneEntity> detailPhone = new ArrayList<PhoneEntity>();
			DecimalFormat df = new DecimalFormat("#");
			for (Phone entity : phoneList) {
				// 保存
				try {
					Document docDetail = getDocument(TaolvUtil.baseUrl + entity.getUrlDetail());
					Elements els = docDetail.select("div.pro_cont_list dl");
					Element el = docDetail.getElementById("spanProdPrice2");
					price = el.html();
					// 没有选择项的手机
					if (els == null || els.size() < 1) {
						PhoneEntity entityDetail = new PhoneEntity();
						entityDetail.setPrice(price);
						entityDetail.setName(entity.getName());
						entityDetail.setFlagType(3);
						entityDetail.setUrlDetail(entity.getUrlDetail());
						detailPhone.add(entityDetail);
					} else {
						pasePhone(entity, detailPhone, price, df);
					}
				} catch (Exception e) {
					System.out.println(entity.toString());
					e.printStackTrace();
				}
			}
			ExcelUtil excelUtil = new ExcelUtil();
			Date date = new Date();
			SimpleDateFormat dateformat = new SimpleDateFormat("YYYYMMdd_HHmmss");
			excelUtil.outExcel(detailPhone, "taolv_phone_" + dateformat.format(date) + ".xls");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
