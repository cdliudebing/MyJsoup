package com.jdrx.httpClinent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jdrx.phone.entity.IPUtil;

/**
 * @ClassName: HttpClientUtil
 * @Description: 利用HttpClient进行post请求的工具类
 * @author liudebing@evercreative.com.cn
 * @date 2016年6月2日 下午3:48:51
 *
 * @version 1.0.0
 */
public class HttpClientPorxyUtil implements Serializable {

	/**
	 * @Fields serialVersionUID : (用一句话描述这个变量表示什么)
	 */
	private static final long serialVersionUID = 2224695362222565310L;
	
	public static String charset = "UTF-8";
	
	/**
	 * @Title: doPost
	 * @Description: (这里用一句话描述这个方法的作用)
	 * @param url
	 *            目标URL
	 * @param map
	 *            请求参数
	 * @param charset
	 *            字符集（UTF-8）
	 * @param proxyIp
	 *            代理IP
	 * @param port
	 *            代理端口号
	 * @return
	 */
	public String doPost(String url, Map<String, String> map, String charset, IPUtil ipUtil) throws Exception {
		HttpClient httpClient = null;
		HttpPost httpPost = null;
		String result = null;
		try {
			httpClient = new SSLClient();
			httpPost = new HttpPost(url);
			HttpHost proxyHost = new HttpHost(ipUtil.getProxyIP(), ipUtil.getPort());// 代理
			httpPost.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxyHost);// 设置代理

			// 设置参数
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			Iterator iterator = map.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, String> elem = (Entry<String, String>) iterator.next();
				list.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
			}
			if (list.size() > 0) {
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, charset);
				httpPost.setEntity(entity);
			}
			HttpResponse response = httpClient.execute(httpPost);
			if (response != null) {
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					result = EntityUtils.toString(resEntity, charset);
				}
			}
		} catch (Exception ex) {
			throw ex;
		}
		return result;
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

}
