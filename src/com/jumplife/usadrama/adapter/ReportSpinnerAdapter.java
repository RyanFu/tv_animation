package com.jumplife.usadrama.adapter;
import com.jumplife.tvanimation.R;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ReportSpinnerAdapter extends ArrayAdapter<String> {

	private Activity mActivity;
	private int layoutResourceId;
	private String[] objects;
	
	public ReportSpinnerAdapter(Activity mActivity, int layoutResourceId,
			String[] objects) {
		super(mActivity, layoutResourceId, objects);
		this.mActivity = mActivity;
		this.layoutResourceId = layoutResourceId;
		this.objects = objects;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return getCustomDropDownView(position, convertView, parent);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	public View getCustomView(int position, View convertView, ViewGroup parent) {
		
		View row = mActivity.getLayoutInflater().inflate(layoutResourceId, parent, false);
		TextView label = (TextView)row.findViewById(R.id.tv_spinner);
		label.setText(objects[position]);
	   
		return row;
  	}
	
	public View getCustomDropDownView(int position, View convertView, ViewGroup parent) {
		
		View row = mActivity.getLayoutInflater().inflate(layoutResourceId, parent, false);
		TextView label = (TextView)row.findViewById(R.id.tv_spinner);
		ImageView ivDropDown = (ImageView)row.findViewById(R.id.iv_spinner);
		
		label.setText(objects[position]);
		ivDropDown.setVisibility(View.GONE);
		
		return row;
  	}
}

