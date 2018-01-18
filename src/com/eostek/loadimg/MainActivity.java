
package com.eostek.loadimg;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private DisplayImageOptions options;

    private GridView gridView;

    private ArrayList<ProgramInfo> mList = new ArrayList<ProgramInfo>();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {

            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.loading)
                    .showImageOnFail(R.drawable.error)
                    .showImageForEmptyUri(R.drawable.ic_launcher)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .bitmapConfig(Bitmap.Config.RGB_565) // 设置图片的解码类型
                    .build();

            gridView = (GridView) findViewById(R.id.grid);
            gridView.setAdapter(new MyAdapter());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread(getDataList).start();
    }

    Runnable getDataList = new Runnable() {
        public void run() {
            URL url = null;
            HttpURLConnection conn = null;
            InputStream is = null;
            StringBuffer sb = null;

            try {
                url = new URL(
                        "http://scifly.88popo.com:8000/eos/v2/search?channels=movie&pageIndex=1&pageCount=100&lang=CN");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                if (conn.getResponseCode() == 200) {
                    is = conn.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    sb = new StringBuffer();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }

                    JSONObject json = new JSONObject(sb.toString());

                    JSONObject content = json.getJSONObject("content");
                    JSONArray pgrpList = content.getJSONArray("pgrpList");
                    for (int i = 0; i < pgrpList.length(); i++) {
                        ProgramInfo info = new ProgramInfo();
                        JSONObject item = (JSONObject) pgrpList.get(i);
                        info.setVideoName(item.getString("pgrpName"));
                        info.setLogoUri(item.getString("pgrpLogoBak"));

                        mList.add(info);
                    }

                    Message msg = mHandler.obtainMessage();
                    msg.sendToTarget();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, e.toString());
            }
            // Log.d(TAG, "mList: " + mList);
        }
    };

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {

            final ViewHolder viewHolder;
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.item, viewGroup, false);

                viewHolder = new ViewHolder();
                viewHolder.img = (ImageView) view.findViewById(R.id.img);
                viewHolder.text = (TextView) view.findViewById(R.id.text);

                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            ProgramInfo info = mList.get(position);
            viewHolder.text.setText(info.getVideoName());
            String uri = info.getLogoUri();
            imageLoader.init(ImageLoaderConfiguration.createDefault(MainActivity.this));
            // imageLoader.displayImage(info.getLogoUri(), img, options);
            imageLoader.displayImage(uri, viewHolder.img, options, new SimpleImageLoadingListener(),
                    new ImageLoadingProgressListener() {

                        @Override
                        public void onProgressUpdate(String uri, View img, int current, int total) {

                        }
                    });

            return view;
        }

        class ViewHolder {
            private TextView text;

            private ImageView img;
        }

    }
}
