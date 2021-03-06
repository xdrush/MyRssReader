package com.example.myrssreader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.example.rssnews.domain.RSSFeed;
import com.example.rssnews.domain.RSSItem;
import com.example.rssnews.util.RSSHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements OnItemClickListener{

	private final String RSS_URL = "http://news.163.com/special/00011K6L/rss_newstop.xml";
	private String currentRssUrl = RSS_URL;
	private RSSFeed feed = new RSSFeed();
	private String mainRssUrl = "http://news.163.com/special/00011K6L/rss_newstop.xml";		// 默认打开的RSS源

	private int reOpenFlag = 0;		// 重新打开APP标志

	private int textsize = R.layout.medium_show;	// 默认情况下是中等字体



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new MyTask().execute(mainRssUrl);
    }

    private class MyTask extends AsyncTask<String, Void, Void>{

    	@Override
    	protected Void doInBackground(String... arg0) {
    	   try {
    		   		URL	rssUrl = new URL(arg0[0]);
		    	    SAXParserFactory mySAXParserFactory = SAXParserFactory.newInstance();
		    	    SAXParser mySAXParser = mySAXParserFactory.newSAXParser();
		    	    XMLReader myXMLReader = mySAXParser.getXMLReader();

		    	    RSSHandler myRSSHandler = new RSSHandler();
		    	    myXMLReader.setContentHandler(myRSSHandler);

		    	    InputSource myInputSource = new InputSource(rssUrl.openStream());
		    	    myXMLReader.parse(myInputSource);

		    	    if(MainActivity.this.reOpenFlag == 1){

		    	    	MainActivity.this.feed = myRSSHandler.getRSSFeed();
		    	    } else {

		    	    	MainActivity.this.onAppOpenSetFeed();
		    	    }

	    	   } catch (MalformedURLException e) {
	    	    e.printStackTrace();
	    	   } catch (ParserConfigurationException e) {
	    	    e.printStackTrace();
	    	   } catch (SAXException e) {
	    	    e.printStackTrace();
	    	   } catch (IOException e) {
	    	    e.printStackTrace();
	    	   }

	    	   return null;
    	  }

    	@Override
    	  protected void onPostExecute(Void result) {

    		super.onPostExecute(result);
    		showListView();
    	  }
    }


    private void showListView(){

    	ListView itemlist = (ListView) this.findViewById(R.id.itemlist);
    	if(null == feed){

    		setTitle("访问的RSS无效");
    		return ;
    	}

    	SimpleAdapter adapter = new SimpleAdapter(this,
    			feed.getAllItems(), textsize, new String[]{
    			RSSItem.TITLE, RSSItem.PUBDATE}, new int[] {
    			android.R.id.text1, android.R.id.text2});

    	itemlist.setAdapter(adapter);
    	itemlist.setOnItemClickListener(this);
    	itemlist.setSelection(0);
    }

    // 重新打开APP时调用的函数， reOpenFlag == 0
    private void onAppOpenSetFeed(){

    	// 获取上次关闭时的RSS_URL
    	SharedPreferences mySPUrl = getSharedPreferences("last_url", Activity.MODE_PRIVATE);

    	Map<String, ?> mapStrUrl = mySPUrl.getAll();
    	Set set0 = mapStrUrl.entrySet();
    	Iterator k = set0.iterator();
    	String valueUrl = new String();
    	while(k.hasNext()){

    	     Map.Entry<String, String> entry1=(Map.Entry<String, String>)k.next();
    	     valueUrl = entry1.getValue();

    	     // 设置上次的URL
    	     // mainRssUrl = valueUrl;
    	}

    	// 获取listview中的内容
    	SharedPreferences mySPRss = getSharedPreferences("last_read", Activity.MODE_PRIVATE);
    	Map<String, ?> mapStr = mySPRss.getAll();

    	// 用last_read.xml初始化feed
    	String strId = new String();
    	String strContent = new String();

    	String[] retStr = new String[4];

    	int cnt = 0;
    	Set set = mapStr.entrySet();
    	Iterator i = set.iterator();
    	while(i.hasNext()){
    		cnt ++;
    		i.next();
    	}

    	RSSItem[] itemArray = new RSSItem[cnt];
    	Set set2 = mapStr.entrySet();
    	Iterator j = set2.iterator();
    	while(j.hasNext()){

    	     Map.Entry<String, String> entry1=(Map.Entry<String, String>)j.next();
    	     strId = entry1.getKey();
    	     strContent = entry1.getValue();

    	     retStr = getRSSItemInfo(strContent);

    	     RSSItem rssitem = new RSSItem();
    	     rssitem.setTitle(retStr[0]);
    	     rssitem.setPubdate(retStr[1]);
    	     rssitem.setLink(retStr[2]);
    	     rssitem.setDescription(retStr[3]);

    	     int index = Integer.parseInt(strId);

    	     itemArray[index] = rssitem;
    	}

    	MainActivity.this.feed.rssItems = Arrays.asList(itemArray);
    	reOpenFlag = 1;
    }

    // 根据last_read.xml解析出title, link, pubdate.
    private String[] getRSSItemInfo(String content){

    	int length = content.length();
    	int index = 0;

    	String[] retStr = new String[4];
    	retStr[0] = "";
    	retStr[1] = "";
    	retStr[2] = "";
    	retStr[3] = "";

    	for(int i=0; i<length; i++){	// get title

    		if(content.charAt(i) != '*')
    			retStr[0] += content.charAt(i);
    		else {
    			index = i + 1;
    			break;
    		}
    	}

    	for(int i=index; i<length; i++){	// get link

    		if(content.charAt(i) != '*')
    			retStr[1] += content.charAt(i);
    		else {
    			index = i + 1;
    			break;
    		}
    	}

    	for(int i=index; i<length; i++){	// get pubdate

    		if(content.charAt(i) != '*')
    			retStr[2] += content.charAt(i);
    		else {
    			index = i + 1;
    			break;
    		}
    	}

    	for(int i=index; i<length; i++){	// get description

    		retStr[3] += content.charAt(i);
    	}

    	return retStr;
    }

    public void onItemClick(AdapterView parent, View v, int position, long id) {// itemclick事件代理方法{

        Intent itemintent = new Intent(this, ActivityShowDescription.class);// 构建一个“意图”，用于指向activity

        Bundle b = new Bundle();// 构建buddle，并将要传递参数都放入buddle
        b.putString("title", feed.getItem(position).getTitle());
        b.putString("description", feed.getItem(position).getDescription());
        b.putString("link", feed.getItem(position).getLink());
        b.putString("pubdate", feed.getItem(position).getPubdate());
        itemintent.putExtra("Android.intent.extra.RSSItem", b); // 用android.intent.extra.INTENT的名字来传递参数
        startActivityForResult(itemintent, 0);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id){

        case R.id.action_refresh:	// 处理刷新

        	refreshRSS();
        	return true;

        case R.id.about:			// 显示关于
        	aboutMsg();
        	return true;

        case R.id.action_settings:	// 处理设置
        	settingMsg();
        	return true;

        case R.id.add_description:	// 添加订阅

        	addDescription();
        	return true;

        case R.id.delete_description:	// 删除订阅

        	deleteDescription();
        	return true;

        case R.id.shift_description:	// 切换订阅

        	shiftDescription();
        	return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void refreshRSS(){

    	if(currentRssUrl == null){

    		showTips("请选择一个RSS源！");
    	} else{

    		new MyTask().execute(mainRssUrl);
    		showTips("刷新成功");
    	}
    }

    private void showTips(String tip) {

    	 Toast toast = Toast.makeText(this, tip, Toast.LENGTH_SHORT);
    	 toast.setGravity(Gravity.CENTER, 0, 10);
    	 toast.show();
    }

    private void aboutMsg(){	// 处理关于信息

    	AlertDialog.Builder aboutDialog = new AlertDialog.Builder(this);
    	aboutDialog.setTitle(R.string.aboutme);
    	aboutDialog.setMessage(R.string.about_info);
    	aboutDialog.setPositiveButton("确定", new OnClickListener(){
    		@Override
    		public void onClick(DialogInterface dialog, int which){

    			dialog.dismiss();
    		}});

    	aboutDialog.create().show();
    }

    private void settingMsg(){			// 处理设置信息

    	LayoutInflater layoutInflater = LayoutInflater.from(this);
    	final View mySettingView = layoutInflater.inflate(R.layout.setting_show, null);

    	final RadioGroup rg = (RadioGroup) mySettingView.findViewById(R.id.text_size);
        final RadioButton mRadio1 = (RadioButton) mySettingView.findViewById(R.id.text_small);
        final RadioButton mRadio2 = (RadioButton) mySettingView.findViewById(R.id.text_big);

        // 显示关于信息
        final Button aboutBtn = (Button) mySettingView.findViewById(R.id.about_page);
        aboutBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				AlertDialog.Builder aboutDialog = new AlertDialog.Builder(MainActivity.this);
		    	aboutDialog.setTitle(R.string.aboutme);
		    	aboutDialog.setMessage(R.string.about_info);
		    	aboutDialog.setPositiveButton("确定", new OnClickListener(){
		    		@Override
		    		public void onClick(DialogInterface dialog, int which){

		    			dialog.dismiss();
		    		}});

		    	aboutDialog.create().show();

			}
		});

        // 字体大小
        final ListView itemlist = (ListView) this.findViewById(R.id.itemlist);

    	AlertDialog.Builder settingDialog = new AlertDialog.Builder(this);
    	settingDialog.setView(mySettingView);
    	settingDialog.setTitle(R.string.settings);
    	settingDialog.setPositiveButton("确定", new OnClickListener(){
    		@Override
    		public void onClick(DialogInterface dialog, int which){

    			// 获取选中的按钮
    	        String str = new String();
    			int selectId = rg.getCheckedRadioButtonId();

    			if(selectId == mRadio1.getId()){	// 小字体

    				textsize = R.layout.small_show;

    			} else if(selectId == mRadio2.getId()){		// 大号字体

    				textsize = R.layout.medium_show;
    			}

		    	SimpleAdapter adapter = new SimpleAdapter(MainActivity.this,
		    			feed.getAllItems(), textsize, new String[]{
		    			RSSItem.TITLE, RSSItem.PUBDATE }, new int[] {
		    			android.R.id.text1, android.R.id.text2 });

		    	itemlist.setAdapter(adapter);
		    	itemlist.setOnItemClickListener(MainActivity.this);
		    	itemlist.setSelection(0);
    		}});

    	settingDialog.create().show();
    }

    private void addDescription(){			// 添加订阅

    	LayoutInflater layoutInflater = LayoutInflater.from(this);
    	View mySettingView = layoutInflater.inflate(R.layout.description_show, null);

    	AlertDialog.Builder settingDialog = new AlertDialog.Builder(this);
    	settingDialog.setView(mySettingView);
    	settingDialog.setTitle("添加RSS源");

    	settingDialog.setPositiveButton("确定", new OnClickListener(){
    		@Override
    		public void onClick(DialogInterface dialog, int which){

    		}});

    	settingDialog.create().show();

    	// 处理订阅
    	final EditText rssEdit = (EditText) mySettingView.findViewById(R.id.description_url);
    	final EditText rssName = (EditText) mySettingView.findViewById(R.id.description_name);

    	final ListView itemlist = (ListView) this.findViewById(R.id.descriptionlist);
    	final Button rssBtn = (Button) mySettingView.findViewById(R.id.description_btn);
    	rssBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final String rssUrlStr = rssEdit.getText().toString();
		    	final String rssNameStr = rssName.getText().toString();

		    	SharedPreferences mySPRss = getSharedPreferences("rss_file", Activity.MODE_PRIVATE);
		    	SharedPreferences.Editor editorRss = mySPRss.edit();
		    	editorRss.putString(rssNameStr, rssUrlStr);

		    	if(!rssUrlStr.equals("") && !rssNameStr.equals("")){

		    		// 输入有效，则提交到sharedPreferences中
		    		editorRss.commit();

		    		showTips("订阅成功");
		    	} else{

		    		showTips("请输入有效的url和中文名");
		    	}
			}
		});
    }

    private void deleteDescription(){		// 删除订阅

    	LayoutInflater layoutInflater = LayoutInflater.from(this);
    	final View mySettingView = layoutInflater.inflate(R.layout.delete_description, null);

    	AlertDialog.Builder settingDialog = new AlertDialog.Builder(this);
    	settingDialog.setView(mySettingView);
    	settingDialog.setTitle("请选择要删除的RSS源");

    	// 获取rss_name并用checkbox动态显示
    	final LinearLayout ll = (LinearLayout) mySettingView.findViewById(R.id.delete_layout);

    	final SharedPreferences readRssInfo = getSharedPreferences("rss_file", Activity.MODE_PRIVATE);
    	final SharedPreferences.Editor editorRss = readRssInfo.edit();

    	Map<String, ?> mapStr = readRssInfo.getAll();
    	Set set = mapStr.entrySet();
    	Iterator i = set.iterator();
    	String strName = new String();
    	int checkBox_id = 0;
    	while(i.hasNext()){

    	     Map.Entry<String, String> entry1=(Map.Entry<String, String>)i.next();
    	     strName = entry1.getKey();
    	     if(strName != ""){
	    	     CheckBox checkBox = new CheckBox(MainActivity.this);
	    	     checkBox.setText(strName);
	    	     checkBox.setId(checkBox_id);
	    	     ll.addView(checkBox);

	    	     checkBox_id ++;
    	     }
    	}

    	final int checkBox_cnt = checkBox_id;
    	settingDialog.setPositiveButton("确定", new OnClickListener(){
    		@Override
    		public void onClick(DialogInterface dialog, int which){

    			int deleteCnt = 0;
    			// 根据所选取的checkbox，删除指定的RSS源
    			for(int i=0; i<checkBox_cnt; i++){

    				CheckBox checkBox = (CheckBox) mySettingView.findViewById(i);
    				if(checkBox.isChecked()){	// 当前的checkbox被选中了

    					// 获取首选项，相应的删除
    					String str = checkBox.getText().toString();

    					editorRss.remove(str);
    					editorRss.commit();
    					deleteCnt ++;
    				}
    			}

    			if(0 != deleteCnt){		// 提示删除成功

    				showTips("删除成功");
    			}
    		}});

    	settingDialog.create().show();
    }

    private void shiftDescription(){		// 切换订阅

    	LayoutInflater layoutInflater = LayoutInflater.from(this);
    	final View mySettingView = layoutInflater.inflate(R.layout.shift_description, null);

    	AlertDialog.Builder settingDialog = new AlertDialog.Builder(this);
    	settingDialog.setView(mySettingView);
    	settingDialog.setTitle("请选择要读取的RSS源");

    	// 获取rss_name并用groupbox动态显示，每次只能选取一个RSS源
    	final LinearLayout ll = (LinearLayout) mySettingView.findViewById(R.id.list_rss);
    	SharedPreferences readSharedPreferences = getSharedPreferences("rss_file", Activity.MODE_PRIVATE);

    	Map<String, ?> mapStr = readSharedPreferences.getAll();
    	Set set = mapStr.entrySet();
    	Iterator i = set.iterator();
    	String strName = new String();
    	int rb_id = 0;
    	while(i.hasNext()){

    	     Map.Entry<String, String> entry1=(Map.Entry<String, String>)i.next();
    	     strName = entry1.getKey();
    	     if(strName != ""){
	    	     RadioButton rb = new RadioButton(MainActivity.this);
	    	     rb.setText(strName);
	    	     rb.setId(rb_id);
	    	     ll.addView(rb);

	    	     rb_id ++;
    	     }
    	}

    	final RadioGroup radioGroup = (RadioGroup) mySettingView.findViewById(R.id.list_rss);
    	final SharedPreferences readRssInfo = getSharedPreferences("rss_file", Activity.MODE_PRIVATE);

    	settingDialog.setPositiveButton("确定", new OnClickListener(){
    		@Override
    		public void onClick(DialogInterface dialog, int which){

    			// 根据选择的RadioButton，重新设置RSS_URL值，重新显示
    			RadioButton radioButton = (RadioButton) mySettingView.findViewById(radioGroup.getCheckedRadioButtonId());
    			if(null != radioButton){

    				String text = radioButton.getText().toString();
	    			String rssUrl = readRssInfo.getString(text, "");
/*
	    			if(text.equals("手机")){

	    				String newStr = "选择了手机";
	    			}
*/
	    			new MyTask().execute(rssUrl);

					setUrlStr(rssUrl);
    			}
    		}});

    	settingDialog.create().show();
    }

    private void setUrlStr(String strUrl){

    	this.mainRssUrl = strUrl;
    }

    @Override
    protected void onDestroy(){

    	super.onDestroy();

    	// 获取当前RSS_URL并保存再sp中
    	SharedPreferences mySPUrl = getSharedPreferences("last_url", Activity.MODE_PRIVATE);
    	SharedPreferences.Editor editorUrl = mySPUrl.edit();

    	editorUrl.clear();		// 先清除，再保存
    	editorUrl.putString("last_url", mainRssUrl);
    	editorUrl.commit();

    	// 获取ListView内容，并保存再sp中
    	String title = new String();
    	String link = new String();
    	String anthor = new String();
    	String category = new String();
    	String pubdate = new String();
    	String comments = new String();
    	String description = new String();

    	SharedPreferences mySPRss = getSharedPreferences("last_read", Activity.MODE_PRIVATE);
    	SharedPreferences.Editor editorRss = mySPRss.edit();

    	editorRss.clear();		// 先清除，再保存

    	String keyStrId = new String();
    	String valueStr = new String();

    	List<RSSItem> rssItems = feed.rssItems;
    	for (int i = 0; i < rssItems.size(); i++) {

    		title = rssItems.get(i).getTitle();
    		link = rssItems.get(i).getLink();
    		pubdate = rssItems.get(i).getPubdate();
    		description = rssItems.get(i).getDescription();

    		keyStrId = Integer.toString(i);

    		valueStr = title + "*" + pubdate + "*" + link + "*" + description + "\n";	// 以*隔开
    		editorRss.putString(keyStrId, valueStr);

    		editorRss.commit();
		}
    }

}