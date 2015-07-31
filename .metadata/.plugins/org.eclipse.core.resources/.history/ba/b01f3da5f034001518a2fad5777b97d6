/*
 * 实体解析类：调用parser解析XML文档
 */

package com.example.rssnews.util;

import java.io.IOException;
import java.net.URL;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.example.rssnews.domain.RSSFeed;

public class RSSSaxActivity{

		public RSSFeed getFeed(String urlStr) throws ParserConfigurationException,
			SAXException, IOException{

			URL url = new URL(urlStr);


			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			SAXParser saxParser = saxParserFactory.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();

			RSSHandler rssHandler = new RSSHandler();
			xmlReader.setContentHandler(rssHandler);

			InputSource is =new InputSource(url.openStream());

			xmlReader.parse(is);

			return rssHandler.getRSSFeed();
		}
}