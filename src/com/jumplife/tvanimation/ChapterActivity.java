package com.jumplife.tvanimation;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.jumplife.tvanimation.sqlitehelper.SQLiteTvAnimationHelper;
import com.jumplife.tvanimation.adapter.ReportSpinnerAdapter;
import com.jumplife.tvanimation.api.TvAnimationAPI;
import com.jumplife.tvanimation.entity.Animate;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import android.app.Dialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap.Config;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView.ScaleType;

public class ChapterActivity extends SherlockActivity {
	private TableLayout tlChapter;
	
	private LinearLayout llChapter;
	private LinearLayout llChapterIntro;
	private ImageView ivPoster;
	
	private LinearLayout llFavorite;
	private LinearLayout llStory;
	private LinearLayout llReport;
	private LinearLayout llCountinue;
	
	private ImageView ivFavorite;
	private ImageView ivStory;
	private ImageView ivReport;
	private ImageView ivCountinue;
	
	private TextView tvFavorite;
	private TextView tvStory;
	private TextView tvReport;
	private TextView tvCountinue;
	
	private ImageButton ibRefresh;
	private View viewFunction;
	private View vSeperate;
	private TextView[] tvChapterItem;
	private Animate animate;
	
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	
	private LoadDataTask loadData;
	private ReNewEpsNumTask reNewEpsNum;
	private UpdateViewTask updateTask;
	
	private Dialog dialogReport;
	private Spinner spinnerChapter;
	
	private int tlColumnNum;
	private int tlMargin = 0;
	private int itemMargin = 0;
	private int chapterCount = 0;
	private int currentChapter = 0;
	private int likeAnimate = 0;
	private int animateId = 0;
	private String animateName = "";
	private String[] chapters;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		
	    getSupportActionBar().setIcon(R.drawable.landingeye_3);
		getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
		
        setContentView(R.layout.activity_chapter);
        
        initView();
        
        loadData = new LoadDataTask();
        if(Build.VERSION.SDK_INT < 11)
        	loadData.execute();
        else
        	loadData.executeOnExecutor(LoadDataTask.THREAD_POOL_EXECUTOR, 0);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
        getSupportMenuInflater().inflate(R.menu.action_bar_refresh, menu);
        
        MenuItem item = menu.findItem(R.id.menu_item_action_provider_refresh);
        View actionView = item.getActionView();
        ImageButton ibRefresh = (ImageButton) actionView.findViewById(R.id.ib_actionbar_refresh);
        if (ibRefresh != null) {
        	ibRefresh.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					reNewEpsNum = new ReNewEpsNumTask();
			        if(Build.VERSION.SDK_INT < 11)
			        	reNewEpsNum.execute();
			        else
			        	reNewEpsNum.executeOnExecutor(LoadDataTask.THREAD_POOL_EXECUTOR, 0);
				}            	
            });
        }
        
        return true;
    }
	 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_item_action_provider_refresh:
			reNewEpsNum = new ReNewEpsNumTask();
	        if(Build.VERSION.SDK_INT < 11)
	        	reNewEpsNum.execute();
	        else
	        	reNewEpsNum.executeOnExecutor(LoadDataTask.THREAD_POOL_EXECUTOR, 0);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void initView() {
		Bundle extras = getIntent().getExtras();
		if(extras != null) {
        	animateId = extras.getInt("animate_id");
        	animateName = extras.getString("animate_name");
        } else {
        	animateId = 1;
        	animateName = "測試";
        }
		getSupportActionBar().setTitle(animateName);
		
		tlColumnNum = getResources().getInteger(R.integer.chapter_activity_item_num_column);
		
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		tlMargin = displayMetrics.widthPixels / 48;
		itemMargin = displayMetrics.widthPixels / 48;
		
		viewFunction = (View)LayoutInflater.from(this).inflate(R.layout.chapter_intro,null);
		
		llFavorite = (LinearLayout)viewFunction.findViewById(R.id.ll_favorite);
		llStory = (LinearLayout)viewFunction.findViewById(R.id.ll_story);
		llReport = (LinearLayout)viewFunction.findViewById(R.id.ll_report);
		llCountinue = (LinearLayout)viewFunction.findViewById(R.id.ll_countinue);
		
		
		tvFavorite = (TextView)viewFunction.findViewById(R.id.tv_favorite);
		tvStory = (TextView)viewFunction.findViewById(R.id.tv_story);
		tvReport = (TextView)viewFunction.findViewById(R.id.tv_report);
		tvCountinue = (TextView)viewFunction.findViewById(R.id.tv_countinue);
		
		ivFavorite = (ImageView)viewFunction.findViewById(R.id.iv_favorite);
		ivStory = (ImageView)viewFunction.findViewById(R.id.iv_story);
		ivReport = (ImageView)viewFunction.findViewById(R.id.iv_report);
		ivCountinue = (ImageView)viewFunction.findViewById(R.id.iv_countinue);
		
		vSeperate =(View)viewFunction.findViewById(R.id.v_seperate);
		
		ivPoster = new ImageView(this);
		tlChapter = new TableLayout(this);
		llChapter = (LinearLayout)findViewById(R.id.ll_chapter);
		llChapterIntro = (LinearLayout)findViewById(R.id.ll_chapter_intro);
		
		/*
		 * Refresh Button Init
		 */
		ibRefresh = (ImageButton)findViewById(R.id.ib_refresh);
		ibRefresh.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				loadData = new LoadDataTask();
		        if(Build.VERSION.SDK_INT < 11)
		        	loadData.execute();
		        else
		        	loadData.executeOnExecutor(LoadDataTask.THREAD_POOL_EXECUTOR, 0);
			}
			
		});
		
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
	private void setClickListener() {
		final Tracker tracker = EasyTracker.getInstance(this);
		tracker.set(Fields.SCREEN_NAME, "劇集列表");
		
		llStory.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				tracker.send(MapBuilder.createEvent("劇集列表", "點擊", "戲劇簡介", null).build());
				setDialogIntroduction();
			}
			
		});
		
		llFavorite.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(likeAnimate == 1) {
					tracker.send(MapBuilder.createEvent("劇集列表", "取消", "我的最愛", (long) animateId).build());
					likeAnimate = 0;
			    } else {
			    	tracker.send(MapBuilder.createEvent("劇集列表", "加入", "我的最愛", (long) animateId).build());
			    	likeAnimate = 1;
			    }
				
				SQLiteTvAnimationHelper instance = SQLiteTvAnimationHelper.getInstance(ChapterActivity.this);
		        SQLiteDatabase db = instance.getWritableDatabase();
		        db.beginTransaction();
		        instance.updateTvAnimationLike(db, animateId, likeAnimate);
				
				instance.updateTvAnimationChapterRecord(db, animate.getId(), currentChapter);
		        db.setTransactionSuccessful();
		        db.endTransaction();
		        db.close();
		        instance.closeHelper();
				
		        if(likeAnimate == 1) {
			    	setToast(true, "已加入至收藏清單", "已從收藏清單移除");
			    } else {
			    	setToast(false, "已加入至收藏清單", "已從收藏清單移除");
			    }
		        setLike();
			}
			
		});
		
		
		llReport.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				tracker.send(MapBuilder.createEvent("劇集列表", "點擊", "問題回報", null).build());
				setDialogReport();
			}
			
		});
		llCountinue.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				tracker.send(MapBuilder.createEvent("劇集列表", "點擊", "續看", null).build());
				Intent newAct = new Intent();
				newAct.putExtra("animate_id", animate.getId());
				newAct.putExtra("eps_num", Integer.parseInt(chapters[currentChapter]));
                newAct.setClass(ChapterActivity.this, PlayerActivity.class);
                ChapterActivity.this.startActivity(newAct);
                
			}
			
		});
		/*
		ibRenew.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				EasyTracker.getTracker().trackEvent("戲劇集數", "點擊", "集數更新", (long)0);
				
		        reNewEpsNum = new ReNewEpsNumTask();
		        if(Build.VERSION.SDK_INT < 11)
		        	reNewEpsNum.execute();
		        else
		        	reNewEpsNum.executeOnExecutor(LoadDataTask.THREAD_POOL_EXECUTOR, 0);
			}
			
		});*/
		
		
	}
	
	private Animate loadEpsNum() {
		TvAnimationAPI tvAnimationAPI = new TvAnimationAPI();
		animate = tvAnimationAPI.getTvAnimationEpsNumViews(animate.getId(), animate);
		
		return animate;
	}
	
	private void setToast(boolean success, String succeeStr, String failStr) {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int wh = (displayMetrics.widthPixels - 2 * tlMargin) / tlColumnNum - 2 * itemMargin;
		
        LinearLayout layout = new LinearLayout(ChapterActivity.this);
        layout.setBackgroundResource(R.color.main_color_green);
        TextView tv = new TextView(ChapterActivity.this);
        tv.setTextColor(getResources().getColor(R.color.white));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, wh / 3);
		tv.setPadding(wh / 2, 
				(int) ChapterActivity.this.getResources().getDimension(R.dimen.chapter_activity_toast_padding), 
				wh / 2, 
				(int) ChapterActivity.this.getResources().getDimension(R.dimen.chapter_activity_toast_padding)
				);
        
        if(success) {
	    	tv.setText(succeeStr);
	    } else {
	    	tv.setText(failStr);
	    }
        layout.addView(tv);
		Toast toast = new Toast(ChapterActivity.this);
		toast.setView(layout);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, (int)(displayMetrics.heightPixels * 1 / 4));
        toast.show();
	}
	private void setDialogIntroduction() {

		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int width = displayMetrics.widthPixels * 9 / 10;
		int height = displayMetrics.heightPixels * 11 / 15;
		
		Dialog dialogIntroduction = new Dialog(this, R.style.dialogBasic);
		dialogIntroduction.setContentView(R.layout.dialog_chapter_introdution);
		dialogIntroduction.getWindow().setLayout(width, height);
		
		TextView tvViews = (TextView)dialogIntroduction.findViewById(R.id.tv_views);
		TextView tvActors = (TextView)dialogIntroduction.findViewById(R.id.tv_actors);
		TextView tvIntro = (TextView)dialogIntroduction.findViewById(R.id.tv_intro);				
		tvViews.setText("觀看次數 : " + animate.getViews());
		tvActors.setText("更新集數 : " + chapterCount);
		tvIntro.setText("簡介 : " + animate.getIntroduction());
		
		dialogIntroduction.show();
		
	}
	private void setDialogReport() {

		dialogReport = new Dialog(this, R.style.dialogBasic);
		dialogReport.setContentView(R.layout.dialog_chapter_report);
		
		spinnerChapter = (Spinner)dialogReport.findViewById(R.id.sp_chapter);
		Button buttonSender = (Button)dialogReport.findViewById(R.id.button_send);
		
		String[] chaptersZHs = new String[chapters.length];
		for(int i=0; i<chapters.length; i++) {
			chaptersZHs[i] = "第" + chapters[i] + "集";
		}
		
		spinnerChapter.setAdapter(new ReportSpinnerAdapter(this, R.layout.spinner_chapter_report, chaptersZHs));
		spinnerChapter.setSelection(currentChapter);
		spinnerChapter.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}			
		});
		
		
		buttonSender.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new ReportTask().execute();
			}			
		});
		
		dialogReport.show();
		
	}
	class ReportTask extends AsyncTask<Integer, Integer, Boolean> {
		
		@Override  
        protected void onPreExecute() {
			/*ivDialogLoadingIcon.setVisibility(View.VISIBLE);
			ivDialogLoadingCircle.setVisibility(View.VISIBLE);
			ivDialogLoadingCircle.startAnimation(animation);
			
			mDialogLoader.show();*/
			
            super.onPreExecute();  
        }  
		
		@Override  
        protected Boolean doInBackground(Integer... params) {
        	Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        	TvAnimationAPI tvAnimateAPI = new TvAnimationAPI();
        	return tvAnimateAPI.report(animate.getId(), Integer.parseInt(chapters[spinnerChapter.getSelectedItemPosition()]));
        }
  
        @Override  
        protected void onPostExecute(Boolean success) {
        	
           	if(success) {        		
        		setToast(true, "回報成功", "回報失敗");
        	} else {
        		setToast(false, "回報成功", "回報失敗");
        	}

        	/*mDialogLoader.cancel();
        	animation.cancel();
        	ivDialogLoadingCircle.clearAnimation();
        	ivDialogLoadingIcon.setVisibility(View.GONE);
        	ivDialogLoadingCircle.setVisibility(View.GONE);*/
        	
        	
        	if(dialogReport != null && dialogReport.isShowing())
        		dialogReport.cancel();
        	
	        super.onPostExecute(success);  
        }
	}
	private Animate fetchData() {
		
		SQLiteTvAnimationHelper instance = SQLiteTvAnimationHelper.getInstance(this);
        SQLiteDatabase db = instance.getReadableDatabase();
        db.beginTransaction();
        animate = instance.getTvAnimation(db, animateId);
		likeAnimate = instance.getTvAnimationLike(db, animateId); 
		currentChapter = instance.getTvAnimationChapterRecord(db, animateId);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        instance.closeHelper();
        chapters = animate.getEpsNumStr().split(",");
		chapterCount = chapters.length;
		Log.d("chapters", ""+animate.getEpsNumStr());
		return animate;		
		
	}
	
	class LoadDataTask extends AsyncTask<Integer, Integer, Animate> {

		@Override  
        protected void onPreExecute() {
			/*ivLoadingIcon.setVisibility(View.VISIBLE);
			ivLoadingCircle.setVisibility(View.VISIBLE);
			ivLoadingCircle.startAnimation(animation);*/
			
			//ibRefresh.setVisibility(View.GONE);
			
            super.onPreExecute();  
        }  
		
		@Override  
        protected Animate doInBackground(Integer... params) {
        	Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        	
        	return fetchData();
        }
  
        @Override  
        protected void onPostExecute(Animate animate) {
        
        	if(animate != null) {
        		
        		setChapterItem();
        		setView();
        		setClickListener();
        	} else
        		ibRefresh.setVisibility(View.VISIBLE);
        	
        	/*animation.cancel();
        	ivLoadingCircle.clearAnimation();
        	ivLoadingIcon.setVisibility(View.GONE);
        	ivLoadingCircle.setVisibility(View.GONE);*/
        	
	        super.onPostExecute(animate);  
        }
		
	}
	
	class ReNewEpsNumTask extends AsyncTask<Integer, Integer, Animate> {
		
		@Override  
        protected void onPreExecute() {
			/*ivDialogLoadingIcon.setVisibility(View.VISIBLE);
			ivDialogLoadingCircle.setVisibility(View.VISIBLE);
			ivDialogLoadingCircle.startAnimation(animation);
			
			mDialogLoader.show();*/
            super.onPreExecute();  
        }  
		
		@Override  
        protected Animate doInBackground(Integer... params) {
        	Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        	return loadEpsNum();
        }
  
        @Override  
        protected void onPostExecute(Animate animate) {
        	
        	if(animate != null) {
        		
        		SQLiteTvAnimationHelper instance = SQLiteTvAnimationHelper.getInstance(ChapterActivity.this);
                SQLiteDatabase db = instance.getWritableDatabase();
                instance.updateTvAnimationEpsNumViews(db, animate);
                db.close();
                instance.closeHelper();
        		
        		chapters = animate.getEpsNumStr().split(",");
        		chapterCount = chapters.length;
        		
        		tlChapter.removeAllViews();
        		setChapterItem();
        		
        		setToast(true, "劇集更新成功", "劇集更新失敗，請在更新一次");
        	} else {
        		setToast(false, "劇集更新成功", "劇集更新失敗，請在更新一次");
        	}
        	
        	/*mDialogLoader.cancel();
        	animation.cancel();
        	ivDialogLoadingCircle.clearAnimation();
        	ivDialogLoadingIcon.setVisibility(View.GONE);
        	ivDialogLoadingCircle.setVisibility(View.GONE);*/
        	
	        super.onPostExecute(animate);  
        }
	}

	private void setView() {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		
		LinearLayout.LayoutParams llChapterIntroParams = new LinearLayout.LayoutParams(
				displayMetrics.widthPixels , displayMetrics.widthPixels * 5/8);
		llChapterIntro.setLayoutParams(llChapterIntroParams);
		llChapterIntro.setGravity(Gravity.CENTER);
		llChapterIntro.setPadding(displayMetrics.widthPixels * 3/40,displayMetrics.widthPixels * 1/16,
				 displayMetrics.widthPixels * 3/40,displayMetrics.widthPixels * 1/16);
		
		LinearLayout.LayoutParams ivChapterParams = new LinearLayout.LayoutParams(
				displayMetrics.widthPixels * 1/3 , displayMetrics.widthPixels * 1/2);
		
		ivPoster.setLayoutParams(ivChapterParams);
		ivPoster.setScaleType(ScaleType.FIT_CENTER);
		
		imageLoader.displayImage(animate.getPosterUrl(), ivPoster, options);
		llChapterIntro.addView(ivPoster);
		
		LinearLayout.LayoutParams viewFunctionParams = new LinearLayout.LayoutParams(
				displayMetrics.widthPixels * 53/120 ,LayoutParams.WRAP_CONTENT);
		viewFunctionParams.setMargins(displayMetrics.widthPixels * 3/40, 0, 0, 0);
		viewFunction.setLayoutParams(viewFunctionParams);
		
		llFavorite.setPadding(0, displayMetrics.widthPixels*1/36, 0, displayMetrics.widthPixels*1/36);
		llStory.setPadding(0, displayMetrics.widthPixels*1/36, 0, displayMetrics.widthPixels*1/36);
		llReport.setPadding(0, displayMetrics.widthPixels*1/36, 0, displayMetrics.widthPixels*1/36);
		llCountinue.setPadding(0, displayMetrics.widthPixels*1/36, 0, displayMetrics.widthPixels*1/36);
		
		LinearLayout.LayoutParams ivPicItemParams = new LinearLayout.LayoutParams(
				displayMetrics.widthPixels*1/12 ,displayMetrics.widthPixels*1/12);
		ivPicItemParams.setMargins(0, 0, 0, displayMetrics.widthPixels*1/36);
		ivFavorite.setLayoutParams(ivPicItemParams);
		ivStory.setLayoutParams(ivPicItemParams);
		ivReport.setLayoutParams(ivPicItemParams);
		ivCountinue.setLayoutParams(ivPicItemParams);
		
		tvFavorite.setTextSize(TypedValue.COMPLEX_UNIT_PX, displayMetrics.widthPixels * 1 / 28);
		tvStory.setTextSize(TypedValue.COMPLEX_UNIT_PX, displayMetrics.widthPixels * 1 / 28);
		tvReport.setTextSize(TypedValue.COMPLEX_UNIT_PX, displayMetrics.widthPixels * 1 / 28);
		tvCountinue.setTextSize(TypedValue.COMPLEX_UNIT_PX, displayMetrics.widthPixels * 1 / 28);
		
		
		
		vSeperate.getLayoutParams().height = displayMetrics.widthPixels * 1/2;
		
		llChapterIntro.addView(viewFunction);
		
		LinearLayout.LayoutParams tlChapterParams = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		tlChapterParams.setMargins(tlMargin, tlMargin, tlMargin, tlMargin);
		tlChapter.setLayoutParams(tlChapterParams);
		llChapter.addView(tlChapter);
		setLike();
		 tvCountinue.setText("續看"+Integer.parseInt(chapters[currentChapter])+"話");
	}
	private void setLike() {		
		if(likeAnimate == 1) {
			ivFavorite.setImageResource(R.drawable.favorite_press);
			tvFavorite.setText("移除收藏");
	    } else {
	    	ivFavorite.setImageResource(R.drawable.favorite_normal);
	    	tvFavorite.setText("加入收藏");
	    }
	}
	private void setChapterItem() {
		
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int wh = (displayMetrics.widthPixels - 2 * tlMargin) / tlColumnNum - 2 * itemMargin;
		
		tvChapterItem = new TextView[chapterCount];
		for(int i=chapterCount-1; i>=0; i-=tlColumnNum) {
			TableRow trChapter = new TableRow(this);
			for(int j=0; j<tlColumnNum; j++) {
				int index = i - j;
				RelativeLayout rl = new RelativeLayout(this);
				
				if(index > -1 && index < chapterCount) {
					tvChapterItem[index] = new TextView(this);
					tvChapterItem[index].setText(chapters[index]);	
					tvChapterItem[index].setId(index);
					tvChapterItem[index].setBackgroundResource(R.drawable.activity_chapter_item_shape);
					tvChapterItem[index].setTextSize(TypedValue.COMPLEX_UNIT_PX, wh * 4 / 9);
					tvChapterItem[index].setTextColor(getResources().getColor(R.color.white));
					tvChapterItem[index].setGravity(Gravity.CENTER);
					tvChapterItem[index].setOnClickListener(new OnClickListener() {
						public void onClick(View arg0) {
							
							int position = arg0.getId();
							
							SQLiteTvAnimationHelper instance = SQLiteTvAnimationHelper.getInstance(ChapterActivity.this);
					        SQLiteDatabase db = instance.getWritableDatabase();
					        db.beginTransaction();
					        
					        if(currentChapter != position) {
					        	instance.updateTvAnimationTimeRecord(db,animate.getId(), 0);
							}								
							currentChapter = position;
				        
							instance.updateTvAnimationChapterRecord(db, animate.getId(), currentChapter);							
							db.setTransactionSuccessful();
							db.endTransaction();
					        db.close();
					        instance.closeHelper();
					        
					        tvCountinue.setText("續看第"+Integer.parseInt(chapters[currentChapter])+"話");
							setItemMark();  
							
							Intent newAct = new Intent();
							newAct.putExtra("animate_id", animate.getId());
							newAct.putExtra("eps_num", Integer.parseInt(chapters[position]));
			                newAct.setClass(ChapterActivity.this, PlayerActivity.class);
			                ChapterActivity.this.startActivity(newAct);
			                
							
			                /*mDialogLoader.cancel();
			            	animation.cancel();
			            	ivDialogLoadingCircle.clearAnimation();
			            	ivDialogLoadingIcon.setVisibility(View.GONE);
			            	ivDialogLoadingCircle.setVisibility(View.GONE);*/
			                
			                updateTask = new UpdateViewTask();
			                updateTask.execute();
			            	
						}					
					});
					RelativeLayout.LayoutParams rlTextParams = new RelativeLayout.LayoutParams(wh, wh);
					tvChapterItem[index].setLayoutParams(rlTextParams);
					rl.addView(tvChapterItem[index]);
				} else {
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(wh, wh);
					View view = new View(this);
					view.setLayoutParams(params);
					rl.addView(view);
				}
				TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1.0f / tlColumnNum);
				tableRowParams.setMargins(itemMargin, itemMargin, itemMargin, itemMargin);
				rl.setLayoutParams(tableRowParams);				
				trChapter.addView(rl);				
			}
			
			trChapter.setLayoutParams(new LayoutParams
					(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));  
			tlChapter.addView(trChapter);
		}
		setItemMark();
	}
	class UpdateViewTask extends AsyncTask<Integer, Integer, String> {
	    @Override
	    protected String doInBackground(Integer... params) {
			TvAnimationAPI tvAnimateAPI = new TvAnimationAPI();
			tvAnimateAPI.updateViews(animateId);
	        return "progress end";
	    }
	}
	private void setItemMark() {
		for(int i=0; i<chapterCount; i+=1) {
			if(currentChapter < 0) {				
				if(i == 0) {
					tvChapterItem[i].setBackgroundResource(R.drawable.activity_chapter_item_select);
					tvChapterItem[i].setTextColor(getResources().getColor(R.color.white));
				} else {
					tvChapterItem[i].setBackgroundResource(R.drawable.activity_chapter_item_shape);
					tvChapterItem[i].setTextColor(getResources().getColor(R.color.background_item));
				}
			} else {
				if(i == currentChapter) {
					tvChapterItem[i].setBackgroundResource(R.drawable.activity_chapter_item_select);
					tvChapterItem[i].setTextColor(getResources().getColor(R.color.white));
				} else {
					tvChapterItem[i].setBackgroundResource(R.drawable.activity_chapter_item_shape);
					tvChapterItem[i].setTextColor(getResources().getColor(R.color.background_item));
				}
			}
		}
	}
	
	@Override
    protected void onStart() {
        super.onStart();
        
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        
        EasyTracker.getInstance(this).activityStop(this);
    }
	
}
