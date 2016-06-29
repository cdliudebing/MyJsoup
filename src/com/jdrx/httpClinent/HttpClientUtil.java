package com.jdrx.httpClinent;

import java.io.IOException;
import java.io.Serializable;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @ClassName: HttpClientUtil
 * @Description: 利用HttpClient进行post请求的工具类
 * @author liudebing@evercreative.com.cn
 * @date 2016年6月2日 下午3:48:51
 *
 * @version 1.0.0
 */
public class HttpClientUtil implements Serializable {

	/**
	 * @Fields serialVersionUID : (用一句话描述这个变量表示什么)
	 */
	private static final long serialVersionUID = 2224695362222565310L;
	
	/**
     * post请求
     * @param url url地址
     * @param map 参数
     * @return
     */
    public static String httpPost(String url,Map<String, String> map){
        String resultString = null;
        try {
        	DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost method = new HttpPost(url);
            if (null != map) {
            	// 设置参数
    			List<NameValuePair> list = new ArrayList<NameValuePair>();
    			Iterator iterator = map.entrySet().iterator();
    			while (iterator.hasNext()) {
    				Entry<String, String> elem = (Entry<String, String>) iterator.next();
    				list.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
    			}
    			if (list.size() > 0) {
    				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
    				method.setEntity(entity);
    			}
            }
            HttpResponse result = httpClient.execute(method);
            url = URLDecoder.decode(url, "UTF-8");
            /**请求发送成功，并得到响应**/
            if (result.getStatusLine().getStatusCode() == 200) {
                try {
                    /**读取服务器返回过来的json字符串数据**/
                	resultString = EntityUtils.toString(result.getEntity());
                } catch (Exception e) {
                	e.printStackTrace();
                }
            }
        } catch (IOException e) {
        	e.printStackTrace();
        }
        return resultString;
    }
   
    public String httpGet(String url){
        String resultString = null;
        try {
        	DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet method = new HttpGet(url);
            HttpResponse result = httpClient.execute(method);
            /**请求发送成功，并得到响应**/
            if (result.getStatusLine().getStatusCode() == 200) {
                try {
                    /**读取服务器返回过来的json字符串数据**/
                	resultString = EntityUtils.toString(result.getEntity());
                } catch (Exception e) {
                	e.printStackTrace();
                }
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return resultString;
    }

	/**
	 * @Title: paresResult
	 * @Description: 转换返回数据
	 * @param result
	 *            要转换的数据 格式为 JSON格式的数据
	 * @return 返回map 解析异常 map数据为空
	 */
	public Map<String, Object> paresResult(String result) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (result != null) {
				JSONObject jb = JSON.parseObject(result);
				String redirectUrl = (String) jb.get("RedirectUrl");
				Object success = (Object) jb.get("Success");
				map.put("redirectUrl", redirectUrl);
				map.put("success", success);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
//	public static void main(String[] args) {
//		try {
//		String uri="pid=1001&itemid=12&productname=iPhone 6 A1586 公开版&maxprice=3233&"
//				+ "picurl=Apple_iPhone6.jpg&qst_140=141_国行有保1个月以上"
//				+ "&qst_220=221_银色&qst_360=361_16G&qst_410=411_iCloud已解除绑定"
//				+ "&qst_1100=1101_能开机&qst_2100=2101_正常通话&qst_4100=4101_正常"
//				+ "&qst_5200=5201_屏幕完好&qst_8300=8301_显示正常"
//				+ "&qst_6100=6101_无进水，无维修&qst_7300=7301_全新手机";
//		String url = "http://www.huishoubao.com/fendi/fendi/index/quote/?";
//		get_method.addRequestHeader("Content-type" , "text/html; charset=utf-8"); 
//		
//		
//		HttpClientUtil clinet = new HttpClientUtil();
//		String urq = "pid=1001&itemid=12&productname=";
//		String productname = URLEncoder.encode("iPhone 6 A1586 公开版","UTF-8");
//		String picurl = URLEncoder.encode("Apple_iPhone6.jpg","UTF-8");
//		String qst_140 = URLEncoder.encode("141_国行有保1个月以上","UTF-8");
//		String qst_220 = URLEncoder.encode("221_银色","UTF-8");
//		String qst_360 = URLEncoder.encode("361_16G","UTF-8");
//		String qst_410 = URLEncoder.encode("411_iCloud已解除绑定","UTF-8");
//		String qst_1100 = URLEncoder.encode("1101_能开机","UTF-8");
//		String qst_2100 = URLEncoder.encode("2101_正常通话","UTF-8");
//		
//		String qst_4100 = URLEncoder.encode("4101_正常","UTF-8");
//		String qst_5200 = URLEncoder.encode("5201_屏幕完好","UTF-8");
//		
//		String qst_8300 = URLEncoder.encode("8301_显示正常","UTF-8");
//		String qst_6100 = URLEncoder.encode("6101_无进水，无维修","UTF-8");
//		String qst_7300 = URLEncoder.encode("7301_全新手机","UTF-8");
//		String uri="pid=1001&itemid=12&productname="+productname+"&maxprice=3233&"
//				+ "picurl="+picurl+"&qst_140="+qst_140
//				+ "&qst_220="+qst_220+"&qst_360="+qst_360+"&qst_410="+qst_410
//				+ "&qst_1100="+qst_1100+"&qst_2100="+qst_2100+"&qst_4100="+qst_4100
//				+ "&qst_5200="+qst_5200+"&qst_8300="+qst_8300
//				+ "&qst_6100="+qst_6100+"&qst_7300="+qst_7300;
//		String result = clinet.httpGet(url+uri);
//		System.out.println(result);
//		String result = clinet.httpGet("http://www.huishoubao.com/fendi/fendi/index/quote/?pid=1001"
//				+ "&itemid=12&productname=iPhone+6+A1586+%E5%85%AC%E5%BC%80%E7%89%88"
//				+ "&maxprice=3333"
//				+ "&picurl=Apple_iPhone6.jpg"
//				+ "&qst_140=141_%E5%9B%BD%E8%A1%8C%E6%9C%89%E4%BF%9D1%E4%B8%AA%E6%9C%88%E4%BB%A5%E4%B8%8A"
//				+ "&qst_220=221_%E9%93%B6%E8%89%B2"
//				+ "&qst_360=361_16G"
//				+ "&qst_410=411_iCloud%E5%B7%B2%E8%A7%A3%E9%99%A4%E7%BB%91%E5%AE%9A"
//				+ "&qst_1100=1101_%E8%83%BD%E5%BC%80%E6%9C%BA"
//				+ "&qst_2100=2101_%E6%AD%A3%E5%B8%B8%E9%80%9A%E8%AF%9D"
//				+ "&qst_4100=4101_%E6%AD%A3%E5%B8%B8"
//				+ "&qst_5200=5201_%E5%B1%8F%E5%B9%95%E5%AE%8C%E5%A5%BD"
//				+ "&qst_8300=8301_%E6%98%BE%E7%A4%BA%E6%AD%A3%E5%B8%B8"
//				+ "&qst_6100=6101_%E6%97%A0%E8%BF%9B%E6%B0%B4%EF%BC%8C%E6%97%A0%E7%BB%B4%E4%BF%AE"
//				+ "&qst_7300=7301_%E5%85%A8%E6%96%B0%E6%89%8B%E6%9C%BA");
//		System.out.println(result);
//		
		
//			String uriString = new String(uri.getBytes("UTF-8"),"ISO-");
			
//			System.out.println(result);
//			Document doc = Jsoup.parse(result);
//			Elements els = doc.select("div.mobile-price-info span.mobile-price-num");
//			System.out.println(els);
//			System.out.println(els.get(0).html());
//		} catch (UnsupportedEncodingException e) {
//			
//			e.printStackTrace();
//		}
		//		String sss ="<i class=\"icon iconfont icon-xiayiye\"></i><span>下一页</span>";
//		if(sss.contains("/fendi/fendi/index/query/")){
//			System.out.println(1111);
//		}else{
//			System.out.println(2222);
//		}
		
//	}
//	/**
//	 * @Title: getDocument
//	 * @Description: 获取网页数据
//	 * @param url
//	 *            要抓取的网页URL
//	 * @return 返回Document格式的数据
//	 */
//	private Document getDocument(String url) {
//		Document doc = null;
//		try {
//			doc = Jsoup.connect(url).data("query", "Java") // 请求参数
//					.userAgent("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0; MALC)") // 设置																				// User-Agent
//					.cookie("auth", "token") // 设置 cookie
//					.timeout(30000) // 设置连接超时时间
//					.post();// 使用 POST 方法访问 URL
//			
//		} catch (IOException e) {
//			
//		}
//		return doc;
//	}
}
