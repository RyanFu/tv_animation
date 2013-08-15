package com.jumplife.tvanimation.adapter;

import java.util.ArrayList;

import com.jumplife.tvanimation.R;
import com.jumplife.tvanimation.entity.Animate;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import android.app.Activity;
import android.graphics.Bitmap.Config;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AnimationGridAdapter extends BaseAdapter {
	
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
    private int mWidth;
	private int mHeight;
	private int padding;
	
	private ArrayList<Animate> animations;
	private LayoutInflater myInflater;
	private class ItemView {
		RelativeLayout rlAnimationImage;
		RelativeLayout rlAnimationText;
		ImageView ivCoverPoster;
		TextView tvAnimationName;
		TextView tvAnimationSeason;
	}
	public AnimationGridAdapter(Activity mActivity, ArrayList<Animate> animations){
		this.animations = animations;
		DisplayMetrics displayMetrics = new DisplayMetrics();
		mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int screenWidth = displayMetrics.widthPixels;
		mWidth = screenWidth / mActivity.getResources().getInteger(R.integer.gridview_num_columns)
				- mActivity.getResources().getDimensionPixelSize(R.dimen.gridview_padding_rl)
				- mActivity.getResources().getDimensionPixelSize(R.dimen.gridview_item_padding)*2;
		
		mHeight = mWidth * 3 / 2;
		
		padding = (int) mActivity.getResources().getDimension(R.dimen.gridview_item_padding);
		
		myInflater = LayoutInflater.from(mActivity);
		
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.stub)
		.showImageForEmptyUri(R.drawable.stub)
		.showImageOnFail(R.drawable.stub)
		.imageScaleType(ImageScaleType.EXACTLY)
		.bitmapConfig(Config.RGB_565)
		.cacheOnDisc()
		.displayer(new SimpleBitmapDisplayer())
		.build();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return animations.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return animations.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ItemView itemView;
		if (convertView != null) {
			itemView = (ItemView) convertView.getTag();
		} else {
			
			convertView = myInflater.inflate(R.layout.grid_animation_item, null);
			itemView = new ItemView();
			
			itemView.rlAnimationImage = (RelativeLayout)convertView.findViewById(R.id.rl_animation_image);
			itemView.rlAnimationText = (RelativeLayout)convertView.findViewById(R.id.rl_animation_text);
			itemView.tvAnimationName = (TextView)convertView.findViewById(R.id.tv_animation_name);
			itemView.tvAnimationSeason = (TextView)convertView.findViewById(R.id.tv_animation_season);
			itemView.ivCoverPoster = (ImageView)convertView.findViewById(R.id.iv_animation_cover_poster);
			
			itemView.rlAnimationImage.getLayoutParams().height = mHeight + 2*padding;
			itemView.rlAnimationImage.getLayoutParams().width = mWidth + 2*padding;
			itemView.rlAnimationImage.setPadding(padding, padding, padding, padding);
			itemView.rlAnimationImage.setGravity(Gravity.CENTER);
			
			itemView.ivCoverPoster.getLayoutParams().height = mHeight;
			itemView.ivCoverPoster.getLayoutParams().width = mWidth;
			
			RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(mWidth, LayoutParams.WRAP_CONTENT);
			rlParams.addRule(RelativeLayout.ALIGN_BOTTOM, itemView.rlAnimationImage.getId());
			rlParams.setMargins(padding, 0, padding, padding);
			itemView.rlAnimationText.setLayoutParams(rlParams);
			
			convertView.setTag(itemView);
		}
		
		
		itemView.tvAnimationName.setText(animations.get(position).getName());
		if(animations.get(position).getSeason() == null || animations.get(position).getSeason().equalsIgnoreCase("null")){
			 itemView.tvAnimationSeason.setText(" ");
		}else{
    	 	 itemView.tvAnimationSeason.setText(animations.get(position).getSeason());
        }
		
		
		itemView.ivCoverPoster.setScaleType(ImageView.ScaleType.FIT_CENTER);
		imageLoader.displayImage(animations.get(position).getPosterUrl(), itemView.ivCoverPoster, options);
			
		return convertView;
	}

}
