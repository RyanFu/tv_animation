package com.jumplife.fragment;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.jumplife.tvanimation.R;
import com.jumplife.tvanimation.TvAnimationApplication;
import com.jumplife.tvanimation.api.TvAnimationAPI;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class SettingFragment extends Fragment {
	
	private View 			 fragmentView;    
    private FragmentActivity mFragmentActivity;
    
    private ImageView ivLoadingIcon;
	private ImageView ivLoadingCircle;
	private ImageView ivDialogLoadingIcon;
	private ImageView ivDialogLoadingCircle;
	private Dialog mDialogLoader;
	private Animation animation;
	
	private ImageView ivNotificationCheck;
	private RelativeLayout rlDeclare;
	private RelativeLayout rlFeedback;
	private RelativeLayout rlNotification;
	private RelativeLayout rlClean;
	private RelativeLayout rlVersionRecord;
	
    private static String updateDiary = "";
	private boolean notificationKey = true;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DiaryDataTask diaryDataTask;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		fragmentView = inflater.inflate(R.layout.fragment_setting, container, false);
        
        initView();
        setClickListener();
		
		return fragmentView;
	}
	
	@Override
    public void onAttach(Activity activity) {
    	mFragmentActivity = getActivity();
        super.onAttach(activity);
    }
	
	private void initView() {
		/*
		 * Loading Animation Init
		 */
		setLoadingAnimation();
		ivLoadingIcon = (ImageView)fragmentView.findViewById(R.id.iv_loading_icon);
		ivLoadingCircle = (ImageView)fragmentView.findViewById(R.id.iv_loading_circle);
		
		mDialogLoader = new Dialog(mFragmentActivity, R.style.dialogLoader);
        mDialogLoader.setContentView(R.layout.layout_loading);
        mDialogLoader.setCanceledOnTouchOutside(false);        
		ivDialogLoadingIcon = (ImageView)mDialogLoader.findViewById(R.id.iv_loading_icon);
		ivDialogLoadingCircle = (ImageView)mDialogLoader.findViewById(R.id.iv_loading_circle);
		
		ivLoadingIcon.setVisibility(View.VISIBLE);
		ivLoadingCircle.setVisibility(View.VISIBLE);
		ivLoadingCircle.startAnimation(animation);
		
		notificationKey = TvAnimationApplication.shIO.getBoolean("notification_key", notificationKey);		
		ivNotificationCheck = (ImageView)fragmentView.findViewById(R.id.iv_notification_check);
		setNotification();
		
		rlDeclare = (RelativeLayout)fragmentView.findViewById(R.id.rl_declare);
		rlFeedback = (RelativeLayout)fragmentView.findViewById(R.id.rl_feedback);
		rlNotification = (RelativeLayout)fragmentView.findViewById(R.id.rl_notification);
		rlClean = (RelativeLayout)fragmentView.findViewById(R.id.rl_clean);
		rlVersionRecord = (RelativeLayout)fragmentView.findViewById(R.id.rl_versionrecord);
		
	}
	
	private void setLoadingAnimation() {
		animation = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration((long) 500);
		animation.setRepeatCount(Animation.INFINITE);
		animation.setInterpolator(AnimationUtils.loadInterpolator(mFragmentActivity, android.R.anim.linear_interpolator));
		animation.setFillAfter(true);
		animation.setFillEnabled(true);
	}
	
	private void setClickListener() {
		final Tracker tracker = EasyTracker.getInstance(this.getActivity());
		tracker.set(Fields.SCREEN_NAME, "設定Fragment");
		
		rlDeclare.setOnClickListener(new OnClickListener(){
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				tracker.send(MapBuilder.createEvent("設定", "點擊", "免責聲明", null).build());
				AlertDialog dialog = new AlertDialog.Builder(mFragmentActivity).create();
		        dialog.setTitle("免責聲明");
		        dialog.setMessage(Html.fromHtml("<b>經典美劇為第三方影音共享播放清單彙整軟體，作為影音內容" +
						"的索引和影視庫的發現，影片來源取自於網路上之Youtube、DailyMotion等影音串流網站" +
        				"網址。經典美劇僅提供搜尋結果，不會上傳任何影片，也不提供任何影片下載，更不會" +
        				"鼓勵他人自行上傳影片，所有影片僅供網絡測試，個人影視製作的學習，交流之用。經典美劇" +
        				"不製播、不下載、不發布、不更改、不存儲任何節目，所有內容均由網友自行發佈" +
        				"，經典美劇不承擔網友託管在第三方網站的內容之責任，版權均為原電視台所有，請各" +
        				"位多多準時轉至各電視台收看。" +
		        		"<br/><br/>本APP所有文章、影片、圖片之著作權皆為原創作人所擁有請勿複製使用，" +
		        		"以免侵犯第三人權益，內容若有不妥，或是部分內容侵犯了您的合法權益，請洽上述節目" +
		        		"來源網站或聯繫本站。"));
		        dialog.setButton("確認", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		                // TODO Auto-generated method stub
		            }
		        });
		        dialog.show();
			}			
		});
		
		rlFeedback.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				tracker.send(MapBuilder.createEvent("設定", "點擊", "建議回饋", null).build());
				PackageInfo packageInfo = null;
            	int tmpVersionCode = -1;
    			try {
    				packageInfo = mFragmentActivity.getPackageManager().getPackageInfo(mFragmentActivity.getPackageName(), 0);
    			} catch (NameNotFoundException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    			if(packageInfo != null)
    				tmpVersionCode = packageInfo.versionCode;
    			
				//EasyTracker.getTracker().sendEvent("關於我們", "點擊", "建議回饋", (long)0);
				Uri uri = Uri.parse("mailto:jumplives@gmail.com");  
				String[] ccs={"abooyaya@gmail.com, raywu07@gmail.com, supermfb@gmail.com, form.follow.fish@gmail.com"};
				Intent it = new Intent(Intent.ACTION_SENDTO, uri);
				it.putExtra(Intent.EXTRA_CC, ccs); 
				it.putExtra(Intent.EXTRA_SUBJECT, "[經典美劇] 建議回饋");
				it.putExtra(Intent.EXTRA_TEXT, "\n\n請詳述發生情況 : " +
									"\n\n\n\nAPP版本號 : " + tmpVersionCode +
	        						"\n\nAndroid版本號 : " + Build.VERSION.RELEASE +
	        						"\n\n裝置型號 : " + Build.MANUFACTURER + " " + Build.PRODUCT + "(" + Build.MODEL + ")");
				startActivity(it);
			}			
		});
		
		rlNotification.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {	
				notificationKey = !notificationKey;
				TvAnimationApplication.shIO.edit().putBoolean("notification_key", notificationKey).commit();
				setNotification();
				String message = "";
				if(notificationKey) {
					tracker.send(MapBuilder.createEvent("設定", "開啟", "推播", null).build());
					message = "新劇推播通知[開啟]";
				} else {
					tracker.send(MapBuilder.createEvent("設定", "關閉", "推播", null).build());
					message = "新劇推播通知[關閉]";
				}
				Toast toast = Toast.makeText(mFragmentActivity, message, Toast.LENGTH_SHORT);
		        toast.setGravity(Gravity.BOTTOM, 0, 0);
		        toast.show();
			}			
		});
		
		rlClean.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				tracker.send(MapBuilder.createEvent("設定", "點擊", "清除緩衝", null).build());
				imageLoader.clearMemoryCache();
				imageLoader.clearDiscCache();
				Toast toast = Toast.makeText(mFragmentActivity, "清除圖片緩衝完成", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();
			}			
		});
		
		rlVersionRecord.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				tracker.send(MapBuilder.createEvent("設定", "點擊", "更新日誌", null).build());
				diaryDataTask = new DiaryDataTask();
        	    if(Build.VERSION.SDK_INT < 11)
        	    	diaryDataTask.execute();
                else
                	diaryDataTask.executeOnExecutor(DiaryDataTask.THREAD_POOL_EXECUTOR, 0);
			}			
		});
		
		animation.cancel();
    	ivLoadingCircle.clearAnimation();
    	ivLoadingIcon.setVisibility(View.GONE);
    	ivLoadingCircle.setVisibility(View.GONE);
	}
	
	private void setNotification() {
		if(notificationKey) {
			ivNotificationCheck.setImageResource(R.drawable.checkbox_press);
		} else {
        	ivNotificationCheck.setImageResource(R.drawable.checkbox_normal);
		}
	}
	
	class DiaryDataTask extends AsyncTask<Integer, Integer, String> {

        @Override
        protected void onPreExecute() {
        	ivDialogLoadingIcon.setVisibility(View.VISIBLE);
			ivDialogLoadingCircle.setVisibility(View.VISIBLE);
			ivDialogLoadingCircle.startAnimation(animation);
			
			mDialogLoader.show();
			super.onPreExecute();
        }

        @Override
        protected String doInBackground(Integer... params) {
        	Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        	if(updateDiary == null) {
        		TvAnimationAPI tvAnimationAPI = new TvAnimationAPI();
        		updateDiary = tvAnimationAPI.getTvAnimationsHistory();
        	} else {
        		if(updateDiary.equals("")) {
        			TvAnimationAPI tvAnimationAPI = new TvAnimationAPI();
        			updateDiary = tvAnimationAPI.getTvAnimationsHistory();
        		}
        	}
            return "progress end";
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(String result) {
        	String message = "";
        	if (updateDiary != null && !updateDiary.equals("")) {
        		message = updateDiary;
        		TvAnimationApplication.shIO.edit().putString("UpdateDiary", updateDiary).commit();
            } else {
            	message = TvAnimationApplication.shIO.getString("UpdateDiary", "尚未有更新");
            }
        	new AlertDialog.Builder(mFragmentActivity).setTitle("更新日誌")
    		.setMessage(Html.fromHtml(message))
            .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {	                	
                }
            })
            .show();

        	mDialogLoader.cancel();
        	animation.cancel();
        	ivDialogLoadingCircle.clearAnimation();
        	ivDialogLoadingIcon.setVisibility(View.GONE);
        	ivDialogLoadingCircle.setVisibility(View.GONE);
        	
			super.onPostExecute(result);
        }
    }
	
	@Override
	public void onDestroy() {	
	    super.onDestroy();
	}
	
	@Override
	public void onStart() {
        super.onStart();
        
        EasyTracker.getInstance(this.getActivity()).activityStart(this.getActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        
        EasyTracker.getInstance(this.getActivity()).activityStop(this.getActivity());
    }
}
