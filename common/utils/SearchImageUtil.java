package myproject.common.utils;


import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 传入搜索的关键字，返回对应图片的网络地址
 */
public class SearchImageUtil {

	private static String getUrl(String word, int num) {
		String url = "https://image.so.com/j?q=" + word + "&src=srp&correct=" + word + "&sn=" + num + "&pn=" + num;
		url = URLUtil.encode(url);
		return url;
	}

	public static void main(String[] args) {
		System.out.println(getImageUrl("123"));
	}

	/**
	 * 耗时操作，请调用者在工作线程使用
	 *
	 * @param searchWord
	 * @return
	 */
	public static String getImageUrl(String searchWord) {
		String url = "https://image.so.com/j";
		Map<String, Object> param = new HashMap<>();
		param.put("q", searchWord);
		param.put("src", "srp");
		param.put("correct", searchWord);
		param.put("pn", "60");
		param.put("sn", "0");
		String string = HttpUtil.get(url, param);
		System.out.println(string);
		JSONObject AllData = new JSONObject(string);
		JSONArray lrcArray = AllData.getJSONArray("list");
		for (int i = 0; i < lrcArray.size(); i++) {
			JSONObject oneLrcObject = lrcArray.getJSONObject(i);
			String resultUrl = oneLrcObject.get("img").toString();
			return resultUrl; /*只返回一个*/
		}
		return null;
	}
}
