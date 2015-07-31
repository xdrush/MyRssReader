package com.example.rssnews.util;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import com.example.rssnews.domain.RSSFeed;
import com.example.rssnews.domain.RSSItem;
//import android.util.Log;

public class RSSHandler extends DefaultHandler{

	private RSSFeed rssFeed;
	private RSSItem rssItem;

	//private String lastElementName = "";

	final int RSS_TITLE = 1;
	final int RSS_LINK = 2;
	final int RSS_AUTHOR = 3;
	final int RSS_CATEGORY = 4;
	final int RSS_PUBDATE = 5;
	final int RSS_COMMENTS = 6;
	final int RSS_DESCRIPTION = 7;

	private int currentFlag = 0;

	public RSSHandler(){

	}

	@Override
	/*
	 * 第一个需要重写的方法，每处理一个XML文档都会响应一次，
	 * 这个方法里可以写需要初始化的代码
	 */
	public void startDocument() throws SAXException{

		super.startDocument();

		rssFeed = new RSSFeed();
		rssItem = new RSSItem();
	}


	@Override
	/*
	 * 处理一个节点之间的文本时触发的方法，
	 * 该方法并不会告诉当前文本的所属标签，而仅仅告诉文本的内容
	 */
	public void characters(char[] ch, int start, int length) throws SAXException{

		super.characters(ch, start, length);

		String text = new String(ch, start, length);	// 获取字符串

		switch(currentFlag){

		case RSS_TITLE:
			rssItem.setTitle(text);
			currentFlag = 0;
			break;
		case RSS_PUBDATE:
			rssItem.setPubdate(text);
			currentFlag = 0;
			break;
		case RSS_CATEGORY:
			rssItem.setCategory(text);
			currentFlag = 0;
			break;
		case RSS_LINK:
			rssItem.setLink(text);
			currentFlag = 0;
			break;
		case RSS_AUTHOR:
			rssItem.setAuthor(text);
			currentFlag = 0;
			break;
		case RSS_DESCRIPTION:
			rssItem.setDescription(text);
			currentFlag = 0;
			break;
		case RSS_COMMENTS:
			rssItem.setComments(text);
			currentFlag = 0;
			break;
		default:
				break;
		}
	}

	@Override
	/*
	 * 处理每个节点所触发的方法，
	 * 可以直接处理当前节点的名称以及属性
	 */
	public void startElement(String url, String localName, String qName, Attributes attributes)
		throws SAXException{

		super.startElement(url, localName, qName, attributes);

		if("channel".equals(localName)){

			currentFlag = 0;
			return ;
		}

		if("item".equals(localName)){

			rssItem = new RSSItem();
			return ;
		}

		if("title".equals(localName)){

			currentFlag = RSS_TITLE;
			return ;
		}

		if("description".equals(localName)){

			currentFlag = RSS_DESCRIPTION;
			return ;
		}

		if("link".equals(localName)){

			currentFlag = RSS_LINK;
			return ;
		}

		if("pubDate".equals(localName)){

			currentFlag = RSS_PUBDATE;
			return ;
		}

		if("category".equals(localName)){

			currentFlag = RSS_CATEGORY;
			return ;
		}

		if("author".equals(localName)){

			currentFlag = RSS_AUTHOR;
			return ;
		}

		if("comments".equals(localName)){

			currentFlag = RSS_COMMENTS;
			return ;
		}
	}

	@Override
	/*
	 * 遇到一个节点的结束标签时，将会触发这个方法，
	 * 并且传递结束标签的名称
	 */
	public void endElement(String url, String localName, String qName)
		throws SAXException{

		super.endElement(url, localName, qName);

		if("item".equals(localName)){

			rssFeed.addItem(rssItem);
			return ;
		}
	}

	@Override
	/*
	 * 当前XML文档处理完毕之后，将会触发该方法，
	 * 在此方法内，可以将最终的结果保存并且销毁不需要使用的变量
	 */
	public void endDocument() throws SAXException{

		super.endDocument();
	}

	public RSSFeed getRSSFeed(){

		return rssFeed;
	}


}