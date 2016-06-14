package com.google.gh.todolist;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewsActivity extends AppCompatActivity {

    ListView listview;
    Handler handler;
    List<Map<String, Object>> data;

    final String CSDNURL = "http://www.csdn.net/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        handler = getHandler();
        ThreadStart();
    }
    /**
     * 新开辟线程处理联网操作
     * @author Lai Huan
     * @created 2013-6-20
     */
    private void ThreadStart() {
        new Thread() {
            public void run() {
                Message msg = new Message();
                try {
                    data = getCsdnNetDate();
                    msg.what = data.size();
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = -1;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }
    /**
     * 联网获得数据
     * @return 数据
     * @author Lai Huan
     * @created 2013-6-20
     */
    private List<Map<String, Object>> getCsdnNetDate() {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        String csdnString = http_get(CSDNURL);
        //<li><a title="(.*?)" href="(.*?)" target="_blank" onclick="LogClickCountthis,363;">\1</a></li>
        //title="(.*?)" href="(.*?)".*?,363\)
        Pattern p = Pattern.compile("title=\"(.*?)\" href=\"(.*?)\".*?33");
        Matcher m = p.matcher(csdnString);
        while (m.find()) {
            MatchResult mr=m.toMatchResult();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("title", mr.group(1));
            map.put("url", mr.group(2));
            result.add(map);
        }
        return result;
//        Cloud Foundry      </a>  <a title="Redis" href="http://www.csdn.net/tag/redis" target="_blank" onclick="LogClickCount(this,336);">
    }
    /**
     * 处理联网结果，显示在listview
     * @return
     * @author Lai Huan
     * @created 2013-6-20
     */
    private Handler getHandler() {
        return new Handler(){
            public void handleMessage(Message msg) {
                if (msg.what < 0) {
                    Toast.makeText(NewsActivity.this, "数据获取失败", Toast.LENGTH_SHORT).show();
                }else {
                    initListview();
                }
            }
        };
    }
    /**
     * 在listview里显示数据
     * @author Lai Huan
     * @created 2013-6-20
     */
    private void initListview() {
        listview = (ListView) findViewById(R.id.listview);
        SimpleAdapter adapter = new SimpleAdapter(this, data,
                android.R.layout.simple_list_item_1, new String[] { "title"},
                new int[] { android.R.id.text1 });
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Map<String, Object> map = data.get(arg2);
                String url = (String)(map.get("url"));
                Intent intent = new Intent(NewsActivity.this,NewsContentActivity.class);
                intent .setData(Uri.parse(url));
                startActivity(intent);
            }
        });
    }

    /**
     * get请求URL，失败时尝试三次
     * @param url 请求网址
     * @return 网页内容的字符串
     */
    private String http_get(String url) {
        HttpURLConnection urlConnection = null;
        URL csdnurl;
        InputStream inputStream=null;
        String response=null;
        try{
            csdnurl = new URL(url);
            urlConnection = (HttpURLConnection) csdnurl.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MSIE 7.0; Windows NT 5.1)");
            urlConnection.setRequestProperty("Accept",
                    "image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/x-shockwave-flash, application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, */*");
            urlConnection.setRequestProperty("Accept-Language", "zh-cn");
            urlConnection.setRequestProperty("UA-CPU", "x86");
            urlConnection.setRequestProperty("Content-type", "text/html");
            urlConnection.setConnectTimeout(6 * 1000);
            urlConnection.setReadTimeout(6*1000);
            inputStream = urlConnection.getInputStream();
            byte [] bytes = new byte[1024];
//            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(inputStream));
//            String inputLine  = "";
            while(inputStream.read(bytes)!= -1){
                response += new String(bytes);
                Log.i("csdn",new String(bytes));
            }
            return response;
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            try{
                inputStream.close();
                urlConnection.disconnect();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

//    /**
//     * get请求URL，失败时尝试三次
//     * @param url 请求网址
//     * @return 网页内容的字符串
//     * @author Lai Huan
//     * @created 2013-6-20
//     */
//    private String http_get(String url) {
//        final int RETRY_TIME = 3;
//        HttpClient httpClient = null;
//        HttpGet httpGet = null;
//
//        String responseBody = "";
//        int time = 0;
//        do {
//            try {
//                httpClient = getHttpClient();
//                httpGet = new HttpGet(url);
//                HttpResponse response = httpClient.execute(httpGet);
//                if (response.getStatusLine().getStatusCode() == 200) {
//                    //用utf-8编码转化为字符串
//                    byte[] bResult = EntityUtils.toByteArray(response.getEntity());
//                    if (bResult != null) {
//                        responseBody = new String(bResult,"utf-8");
//                        Log.i("csdn",responseBody);
//                    }
//                }
//                break;
//            } catch (IOException e) {
//                time++;
//                if (time < RETRY_TIME) {
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e1) {
//                    }
//                    continue;
//                }
//                e.printStackTrace();
//            } finally {
//                httpClient = null;
//            }
//        } while (time < RETRY_TIME);
//
//        return responseBody;
//    }

    private  HttpClient getHttpClient() {
        HttpParams httpParams = new BasicHttpParams();
        //设定连接超时和读取超时时间
        HttpConnectionParams.setConnectionTimeout(httpParams, 6000);
        HttpConnectionParams.setSoTimeout(httpParams, 30000);
        return new DefaultHttpClient(httpParams);
    }
}
