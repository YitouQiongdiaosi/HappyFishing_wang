package com.example.happyfishing.adapter;

import java.util.ArrayList;

import com.example.happyfishing.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FishPositionShowAdapter extends BaseAdapter{
	private int positionTotal;
	private LayoutInflater layoutInflater;
	private int fishPositionNum ;
	private ArrayList<Integer> orderedLocation;
	private int currentOrdered;
	public FishPositionShowAdapter (Context context , int positionTotal , int fishPositionNum , String []orderedPosition){
		orderedLocation = new ArrayList<Integer>();
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.positionTotal=positionTotal;
		this.fishPositionNum =fishPositionNum;
		if (orderedPosition.length>=0) {
			for (int i = 0; i < orderedPosition.length; i++) {
				orderedLocation.add(Integer.parseInt(orderedPosition[i]));
			}
		}
	}
	
	public void setCurrentOrder(int currentOrder) {
		this.currentOrdered = currentOrder;
	}
	
	public int getCurrentOrder(){
		return this.currentOrdered;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return positionTotal;
	}

	@Override
	public View getItem(int position) {
		View view = getView(position, null, null);
		view.findViewById(R.id.tv_layoutinflater_fishposition_show).setBackgroundResource(R.drawable.bgd_diaowei_default);
		return view;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
			View view ;
			view = layoutInflater.inflate(R.layout.layoutinflater_fishposition_show, null);
			view.setTag(true);
			TextView tv_fishposition_show = (TextView) view.findViewById(R.id.tv_layoutinflater_fishposition_show);
			tv_fishposition_show.setText((50*fishPositionNum+(position+1))+"");
			if ((50*fishPositionNum+(position+1)) == currentOrdered) {
				tv_fishposition_show.setBackgroundResource(R.drawable.bgd_diaowei_selected);
			}
			for (int i = 0; i < orderedLocation.size(); i++) {
				if ((50*fishPositionNum+position+1) == orderedLocation.get(i)) {
					tv_fishposition_show.setBackgroundResource(R.drawable.bgd_diaowei_noselected);
					view.setClickable(false);
					view.setTag(false);
				}
			}
			
		return view;
	}

}
