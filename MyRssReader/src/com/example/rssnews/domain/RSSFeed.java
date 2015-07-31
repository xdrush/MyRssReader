package com.example.rssnews.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.view.Gravity;
import android.widget.Toast;

public class RSSFeed {

	private String title; 	 	// 标题
	private String pubdate; 	// 发布日期
	//private String category;	// 类别

	private int itemCount; 	// 计算列表的数目
	public List<RSSItem> rssItems; 	// 描述列表item

	public RSSFeed() {
		rssItems = new ArrayList<RSSItem>();
	}

	// 添加RssItem条目,返回列表长度
	public int addItem(RSSItem rssItem) {
		rssItems.add(rssItem);
		itemCount ++;
		return itemCount;
	}

	// 根据下标获取RssItem
	public RSSItem getItem(int position) {
		return rssItems.get(position);
	}

	public List<HashMap<String, Object>> getAllItems() {

		List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

		for (int i = 0; i < rssItems.size(); i++) {

			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put(RSSItem.TITLE, rssItems.get(i).getTitle());
			item.put(RSSItem.PUBDATE, rssItems.get(i).getPubdate());

			data.add(item);
		}

		return data;
	}

	public String getTitle() {

		return title;
	}

	public void setTitle(String title) {

		this.title = title;
	}
/*
	public String getCategory() {

		return category;
	}

	public void getCategory(String category) {

		this.category = category;
	}
*/
	public String getPubdate() {

		return pubdate;
	}

	public void setPubdate(String pubdate) {

		this.pubdate = pubdate;
	}

	public int getItemCount() {

		return itemCount;
	}

	public void setItemCount(int itemCount) {

		this.itemCount = itemCount;
	}
}