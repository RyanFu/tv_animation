package com.jumplife.tvanimation;


import com.jumplife.tvanimation.sqlitehelper.SQLiteTvAnimationHelper;
import com.jumplife.tvanimation.entity.Animate;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import android.app.Activity;
import android.app.Dialog;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap.Config;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class ChapterActivity extends Activity {
	private TableLayout tlChapter;
	private RelativeLayout rlChapter;
	
	private int tlColumnNum;
	private int tlMargin = 0;
	private int itemMargin = 0;
	private ImageView ivChapter;
	private ImageButton ibFavorite;
	private RelativeLayout viewFunction;
	private Animate animate;
	
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	
	private LoadDataTask loadData;
	
	private int chapterCount = 0;
	private int likeDrama = 0;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);
        
        initView();
        
        //this.setAd();
        
        loadData = new LoadDataTask();
        if(Build.VERSION.SDK_INT < 11)
        	loadData.execute();
        else
        	loadData.executeOnExecutor(LoadDataTask.THREAD_POOL_EXECUTOR, 0);
	}
	private void initView() {
		tlColumnNum = getResources().getInteger(R.integer.chapter_activity_item_num_column);
		
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		tlMargin = displayMetrics.widthPixels / 48;
		itemMargin = displayMetrics.widthPixels / 48;
		
		viewFunction = (RelativeLayout)findViewById(R.layout.chapter_intro);
		ibFavorite = (ImageButton)findViewById(R.id.ib_favorite);
		
		ivChapter = new ImageView(this);
		tlChapter = new TableLayout(this);
		rlChapter = (RelativeLayout)findViewById(R.id.rl_chapter_intro);
		
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
	private Animate fetchData() {
	/*	SQLiteTvAnimationHelper instance = SQLiteTvAnimationHelper.getInstance(this);
        SQLiteDatabase db = instance.getReadableDatabase();
        db.beginTransaction();
		drama = instance.getDrama(db, dramaId);
		likeDrama = instance.getDramaLike(db, dramaId); 
		currentChapter = instance.getDramaChapterRecord(db, dramaId);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        instance.closeHelper();
        
		chapters = drama.getEpsNumStr().split(",");
		chapterCount = chapters.length;
		
		return drama;		*/
		Animate a = new Animate();
		a.setName("巴拉巴拉");
		
		return a;
	}
	
	class LoadDataTask extends AsyncTask<Integer, Integer,String> {

		@Override  
        protected void onPreExecute() {
			
            super.onPreExecute();  
        }  
		
		@Override  
        protected String doInBackground(Integer... params) {
        	Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        	fetchData();
        	return "progress end";
        }
  
        @Override  
        protected void onPostExecute(String result) {
        	
        	if(result != null) {
        		//setChapterItem();
        		setView();
        		//setClickListener();
        	} else
    			//ibRefresh.setVisibility(View.VISIBLE);
        	
        	//animation.cancel();
        	//ivLoadingCircle.clearAnimation();
        	//ivLoadingIcon.setVisibility(View.GONE);
        	//ivLoadingCircle.setVisibility(View.GONE);
        	
	        super.onPostExecute(result);  
        }
		
	}
	private void setView() {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
 
		RelativeLayout.LayoutParams ivChapterParams = new RelativeLayout.LayoutParams(
				displayMetrics.widthPixels, displayMetrics.widthPixels * 11 / 18);
		ivChapterParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		ivChapter.setLayoutParams(ivChapterParams);
		ivChapter.setScaleType(ScaleType.FIT_CENTER);
		imageLoader.displayImage(animate.getIntroPosterUrl(), ivChapter, options);
		ivChapter.setId(chapterCount + 1);
		rlChapter.addView(ivChapter);
		
	/*	RelativeLayout.LayoutParams viewFunctionParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		viewFunctionParams.addRule(RelativeLayout.ALIGN_BOTTOM, ivChapter.getId());
		viewFunction.setLayoutParams(viewFunctionParams);
		rlChapter.addView(viewFunction);
		
		View view = new View(this);
		RelativeLayout.LayoutParams viewParams = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.seperate_height));
		viewParams.addRule(RelativeLayout.BELOW, ivChapter.getId());
		view.setId(chapterCount + 2);
		view.setLayoutParams(viewParams);
		view.setBackgroundResource(R.color.black);
		rlChapter.addView(view);
		
		RelativeLayout.LayoutParams tlChapterParams = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		tlChapterParams.addRule(RelativeLayout.BELOW, view.getId());
		tlChapterParams.setMargins(tlMargin, tlMargin, tlMargin, tlMargin);
		tlChapter.setLayoutParams(tlChapterParams);
		rlChapter.addView(tlChapter);
		setLike();*/
	}
	private void setLike() {		
		if(likeDrama == 1) {
	    	ibFavorite.setImageResource(R.drawable.favorite_press);
	    } else {
	    	ibFavorite.setImageResource(R.drawable.favorite_normal);
	    }
	}
}
