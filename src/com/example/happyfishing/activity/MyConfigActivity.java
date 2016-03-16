package com.example.happyfishing.activity;

import com.example.happyfishing.R;
import com.example.happyfishing.R.layout;
import com.example.happyfishing.R.menu;
import com.example.happyfishing.view.ActionBarView;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MyConfigActivity extends Activity implements OnClickListener {

	private ActionBarView actionBar_config;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_config);

		findViewById(R.id.ll_myconfig_loginpassword).setOnClickListener(this);
		findViewById(R.id.btn_logout).setOnClickListener(this);
		
		
		initView();
		
		loadData();

	}

	private void initView() {
		actionBar_config = (ActionBarView) findViewById(R.id.actionBar_config);
		actionBar_config.setActionBar(R.string.back, -1, R.string.title_actionbar_myconfig, this);
	}

	private void loadData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_config, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_myconfig_loginpassword:
			Intent intent1 = new  Intent(MyConfigActivity.this, PasswordAlterActivity.class);
			
//			int TYPE_LOGIN = 0;
//			if (TYPE_LOGIN == 1) {
				
//			} else {
//				intent1 = new Intent(MyConfigActivity.this, PasswordCreatActivity.class);
//			}
			startActivity(intent1);
			break;
			
		case R.id.tv_actionbar_left:
			MyConfigActivity.this.finish();
			break;
		case R.id.ll_myconfig_paypassword:
			Intent intent2;
			int TYPE_PAY = 1;
			if (TYPE_PAY == 1) {
				intent2 = new Intent(MyConfigActivity.this, PasswordAlterActivity.class);
			} else {
				intent2 = new Intent(MyConfigActivity.this, PasswordCreatActivity.class);
			}
			startActivity(intent2);
			break;
		case R.id.btn_logout:
			SharedPreferences sp = getSharedPreferences("user", Context.MODE_PRIVATE);
			if (sp.getString("token", null) == null) {
				Toast.makeText(MyConfigActivity.this, "您还尚未登录", Toast.LENGTH_SHORT).show();
				break;
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(MyConfigActivity.this);
			builder.setTitle("是否退出登录？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
		           @Override
		           public void onClick(DialogInterface dialog, int which) {
		        	   SharedPreferences sp1 =getSharedPreferences("user", Context.MODE_PRIVATE);
		        	   Editor editor = sp1.edit();
		        	   editor.clear();
		        	   editor.commit();
		        	   Toast.makeText(MyConfigActivity.this, "您已退出登录", Toast.LENGTH_SHORT).show();
		        	   Intent intent = new Intent(MyConfigActivity.this, HomeActivity.class);
		        	   startActivity(intent);
		           }
		       }).setNegativeButton("取消",
		       new DialogInterface.OnClickListener() {
		           @Override
		           public void onClick(DialogInterface dialog, int which) {
		                
		           }
		       }).show();
			break;
		default:
			break;
		}
	}

}
