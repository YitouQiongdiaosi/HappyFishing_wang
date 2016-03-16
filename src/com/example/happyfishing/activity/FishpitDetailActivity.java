package com.example.happyfishing.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.amap.api.services.core.LatLonPoint;
import com.example.happyfishing.R;

import com.example.happyfishing.R.color;
import com.example.happyfishing.adapter.ImagePagerAdapter;
import com.example.happyfishing.bannerview.CircleFlowIndicator;
import com.example.happyfishing.bannerview.ViewFlow;
import com.example.happyfishing.mapManager.Locating;
import com.example.happyfishing.tool.HttpAddress;
import com.example.happyfishing.tool.HttpCallbackListener;
import com.example.happyfishing.tool.HttpUtil;
import com.example.happyfishing.view.ActionBarView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.R.bool;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FishpitDetailActivity extends Activity implements OnClickListener {

	private ActionBarView actionBarView_fishipitdetail;
	private TextView tv_fishpitdetail_title;
	private ViewFlow mViewFlow;
	private CircleFlowIndicator mFlowIndicator;
	private ArrayList<String> imageUrlList;
	private ArrayList<String> linkUrlArray;
	// private TextView tv_envirScore;
	private TextView tv_address;
	private TextView tv_fishpitDetail;
	private double longitude2;
	private double latitude2;

	private String idString;
	private String nameString;
	private Locating locating;
	private int fishPositionTotal;
	private String cityName;
	private Handler mainHandler;
	private ImageView img_collected;
	private boolean isCollected;
	private String collectedId;

	// private ArrayList<String> titleList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fishpit_detail);

		mainHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					String statusString = (String) msg.obj;
					Bundle bundle = msg.getData();
					boolean collecting = bundle.getBoolean("isCollected");
					if (collecting) {
						img_collected.setImageResource(R.drawable.ic_collection_collected);
					}else {
						img_collected.setImageResource(R.drawable.ic_collection_default);
					}
					Toast.makeText(FishpitDetailActivity.this, statusString, Toast.LENGTH_SHORT).show();
					break;
				case 2:
					Toast.makeText(FishpitDetailActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
					break;
				case 5:
					Toast.makeText(FishpitDetailActivity.this, "网络连接错误", Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
				}
				super.handleMessage(msg);
			}
		};

		initView();

		initBannerList();

		initBanner(imageUrlList);

		loadData();

	}

	private void initBanner(ArrayList<String> imageUrlList2) {

		mViewFlow.setAdapter(new ImagePagerAdapter(this, imageUrlList, linkUrlArray).setInfiniteLoop(true));
		mViewFlow.setmSideBuffer(imageUrlList.size()); // 实际图片张数，

		mViewFlow.setFlowIndicator(mFlowIndicator);
		mViewFlow.setTimeSpan(4500);
		mViewFlow.setSelection(imageUrlList.size() * 1000); // 设置初始位置
		mViewFlow.startAutoFlowTimer(); // 启动自动播放
	}

	private void initBannerList() {
		imageUrlList = new ArrayList<String>();
		linkUrlArray = new ArrayList<String>();
		imageUrlList.add("http://b.hiphotos.baidu.com/image/pic/item/d01373f082025aaf95bdf7e4f8edab64034f1a15.jpg");
		imageUrlList.add("http://g.hiphotos.baidu.com/image/pic/item/6159252dd42a2834da6660c459b5c9ea14cebf39.jpg");
		imageUrlList.add("http://d.hiphotos.baidu.com/image/pic/item/adaf2edda3cc7cd976427f6c3901213fb80e911c.jpg");
		imageUrlList.add("http://g.hiphotos.baidu.com/image/pic/item/b3119313b07eca80131de3e6932397dda1448393.jpg");

		linkUrlArray.add("http://blog.csdn.net/finddreams/article/details/44301359");
		linkUrlArray.add("http://blog.csdn.net/finddreams/article/details/43486527");
		linkUrlArray.add("http://blog.csdn.net/finddreams/article/details/44648121");
		linkUrlArray.add("http://blog.csdn.net/finddreams/article/details/44619589");
	}

	private void initView() {
		img_collected = (ImageView) findViewById(R.id.img_actionbar_collection);
		img_collected.setOnClickListener(this);

		mViewFlow = (ViewFlow) findViewById(R.id.viewflow);
		mFlowIndicator = (CircleFlowIndicator) findViewById(R.id.viewflowindic);
		findViewById(R.id.tv_fishpitdetail_order).setOnClickListener(this);
		findViewById(R.id.ll_fishpitlocation).setOnClickListener(this);
		// 获取意图
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		idString = bundle.getString("id");
		nameString = bundle.getString("title");

		tv_fishpitdetail_title = (TextView) findViewById(R.id.tv_actionbar_title);
		actionBarView_fishipitdetail = (ActionBarView) findViewById(R.id.actionbar_fishipitdetail);
		actionBarView_fishipitdetail.setActionBar(R.string.back, -1, R.string.action_settings, this);
		tv_fishpitdetail_title.setText(bundle.getString("title"));

		// tv_envirScore = (TextView)
		// findViewById(R.id.tv_fishpitdetail_envirScore);
		tv_address = (TextView) findViewById(R.id.tv_fishpitdetail_address);
		tv_fishpitDetail = (TextView) findViewById(R.id.tv_fishipitdetail_detail);
		initData(idString);
	}

	private void initData(String id) {

		SharedPreferences sp = getSharedPreferences("user", Context.MODE_PRIVATE);
		String token = sp.getString("token", null);
		HashMap<String, String> params = new HashMap<String, String>();
		if (token == null) {
		} else {
			params.put("token", token);
		}
		params.put("id", id);
		HttpUtil.getJSON(HttpAddress.ADDRESS + HttpAddress.PROJECT + HttpAddress.CLASS_MERCHANT + HttpAddress.METHOD_DETAIL, params, new HttpCallbackListener() {

			@Override
			public void onFinish(Object response) {
				Log.d("detail", response.toString());
				JSONObject jsonObject = (JSONObject) response;
				try {
					// 缺经纬度 imageurls haveWIFI haveParking
					JSONObject jsonObject2 = jsonObject.getJSONObject("merchant");
					fishPositionTotal = jsonObject2.getInt("locationTotal"); // 钓位总数
					final String location = jsonObject2.getString("address");// 鱼坑位置
					nameString = jsonObject2.getString("name");// 鱼坑名字
					final String envirScore = jsonObject2.getString("envirScore");// 环境指数
					final String fishpitDetail = jsonObject2.getString("introduction");// 鱼坑介绍
					String ADBannerURLS = jsonObject2.getString("imageUrls");// 鱼坑的banner图片
					String longitude = jsonObject2.getString("longitude");// 鱼坑所在的经度
					final String collected = jsonObject2.getString("collected");
					longitude2 = Double.parseDouble(longitude);
					String latitude = jsonObject2.getString("latitude");// 鱼坑所在的纬度
					latitude2 = Double.parseDouble(latitude);
					boolean hasWifi = jsonObject2.getBoolean("hasWifi");// 鱼坑是否有wifi
					boolean hasPark = jsonObject2.getBoolean("hasPark");// 鱼坑是否有停车场
					String idString = jsonObject2.getString("id");
					collectedId = jsonObject2.getString("collectId");

					runOnUiThread(new Runnable() {
						public void run() {
							// tv_envirScore.setText("环境："+envirScore);
							tv_address.setText(location);
							tv_fishpitDetail.setText(fishpitDetail);
							if (collected.equals("1")) {
								img_collected.setImageResource(R.drawable.ic_collection_collected);
								FishpitDetailActivity.this.isCollected = true;
							}else {
								FishpitDetailActivity.this.isCollected = false;
							}
						}
					});
				} catch (JSONException e) {
					mainHandler.sendEmptyMessage(5);
				}

			}

			@Override
			public void onError(Exception e) {
				mainHandler.sendEmptyMessage(5);
			}
		});
	}

	private void loadData() {
		locating = Locating.getInstance(FishpitDetailActivity.this);
		locating.locate();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.fishpit_detail, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_fishpitdetail_order:
			SharedPreferences sp2 = getSharedPreferences("user", Context.MODE_PRIVATE);
			String token2 = sp2.getString("token", "");
			if (token2.equals("")) {
				Toast.makeText(FishpitDetailActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
			} else {
				Intent intent1 = new Intent(FishpitDetailActivity.this, FishpitConfirmOrderActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("id", idString);
				bundle.putString("name", nameString);
				bundle.putInt("fishPositionTotal", fishPositionTotal);
				intent1.putExtras(bundle);
				startActivity(intent1);
			}
			break;
		case R.id.ll_fishpitlocation:
			Intent intent2 = new Intent(FishpitDetailActivity.this, LocationShowActivity.class);
			Bundle bundle2 = new Bundle();
			bundle2.putDouble("target_lat", latitude2);
			bundle2.putDouble("target_lon", longitude2);
			bundle2.putDouble("my_lat", locating.latitude);
			bundle2.putDouble("my_lon", locating.longitude);
			bundle2.putString("city", locating.cityName);
			intent2.putExtras(bundle2);
			startActivity(intent2);
			break;
		case R.id.tv_actionbar_left:
			FishpitDetailActivity.this.finish();
			break;
		case R.id.img_actionbar_collection:
			if (isCollected) {
				SharedPreferences sp3 = getSharedPreferences("user", Context.MODE_PRIVATE);
				String token = sp3.getString("token", "");
				HashMap<String, String> params2 = new HashMap<String, String>();
				params2.put("token", token);
				params2.put("collectId", collectedId);
				
				HttpUtil.getJSON(HttpAddress.ADDRESS+HttpAddress.PROJECT+
						HttpAddress.CLASS_USERCOLLECT+HttpAddress.METHOD_DELCOLLET, 
						params2, 
						new HttpCallbackListener() {
							
							@Override
							public void onFinish(Object response) {
								Log.d("delCollect", response.toString());
								JSONObject jsonObject = (JSONObject) response;
								String text = "网络错误，请重试";
								try {
									text = jsonObject.getString("text");
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								Message msg = new Message();
								isCollected = false;
								Bundle bundle = new Bundle();
								bundle.putBoolean("isCollected", isCollected);
								msg.setData(bundle);
								msg.obj = text;
								msg.what=1;
								mainHandler.sendMessage(msg);
							}
							
							@Override
							public void onError(Exception e) {
								
							}
						});
			}else {
				SharedPreferences sp = getSharedPreferences("user", Context.MODE_PRIVATE);
				String token = sp.getString("token", "");
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("token", token);
				params.put("merchantId", idString);
				
				if (token.equals("")) {
					Toast.makeText(FishpitDetailActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
					
				} else {
					HttpUtil.getJSON(HttpAddress.ADDRESS + HttpAddress.PROJECT + HttpAddress.CLASS_USERCOLLECT + HttpAddress.METHOD_ADDCOLLET, params, new HttpCallbackListener() {
						
						@Override
						public void onFinish(Object response) {
							JSONObject jsonObject = (JSONObject) response;
							Log.d("addCollect", response.toString());
							String statusString = "收藏失败";
							try {
								statusString = jsonObject.getString("text");
								collectedId = jsonObject.getString("collectId");
							} catch (JSONException e) {
								mainHandler.sendEmptyMessage(5);
								e.printStackTrace();
							}
							isCollected = true;
							Message message = new Message();
							message.what = 1;
							message.obj = statusString;
							Bundle bundle = new Bundle();
							bundle.putBoolean("isCollected", isCollected);
							message.setData(bundle);
							mainHandler.sendMessage(message);
						}
						
						@Override
						public void onError(Exception e) {
							mainHandler.sendEmptyMessage(5);
						}
					});
				}
			}
			break;
		default:
			break;
		}
	}

}
