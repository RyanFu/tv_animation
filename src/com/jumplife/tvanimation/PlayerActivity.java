package com.jumplife.tvanimation;

import com.jumplife.customPlayer.VideoControllerView;
import com.jumplife.tvanimation.api.TvAnimationAPI;
import com.jumplife.tvanimation.sqlitehelper.SQLiteTvAnimationHelper;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;

import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnErrorListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;

import io.vov.vitamio.widget.VideoView;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

public class PlayerActivity extends Activity implements VideoControllerView.MediaPlayerControl {

	private ImageView ivDialogLoadingIcon;
	private ImageView ivDialogLoadingCircle;
	private Dialog mDialogLoader;
	private Dialog mDialogWifi;
	private Animation animation;

    private VideoView mVideoView;
    private VideoControllerView controller;

    private int dramaId = 0;
    private int epsNum = 0;
    private String videoLink = "";
    private static int stopPosition = 0;
    
    private QueryVideoTask mQueryVideoTask;
    
    private final static int filter = 30000; 
    
    private final static int SETTING_WIFI_REQUESTCODE = 100;
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);        
        if(mVideoView != null) {
        	float aspectRatio = mVideoView.getVideoAspectRatio();
        	mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, aspectRatio);
        }
    }
    
    @Override
    protected void onCreate(Bundle pSavedInstanceState) {
        super.onCreate(pSavedInstanceState);
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	
        if (!LibsChecker.checkVitamioLibs(this))
			return;        
        
        setContentView(R.layout.activity_player);
        
        boolean wifiRemind = TvAnimationApplication.shIO.getBoolean("wifi_remind", true);
        if(wifiRemind && !((ConnectivityManager)getSystemService(PlayerActivity.CONNECTIVITY_SERVICE))
        		.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting()) {
        	setDialogWifi();
        	TvAnimationApplication.shIO.edit().putBoolean("wifi_remind", false).commit();
        } else {
	        initView();
	                
	        mQueryVideoTask = new QueryVideoTask();
	        if(Build.VERSION.SDK_INT < 11)
	        	mQueryVideoTask.execute();
	        else
	        	mQueryVideoTask.executeOnExecutor(QueryVideoTask.THREAD_POOL_EXECUTOR);
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        switch (requestCode) {
        case SETTING_WIFI_REQUESTCODE :			
			initView();
	        		        
	        mQueryVideoTask = new QueryVideoTask();
	        if(Build.VERSION.SDK_INT < 11)
	        	mQueryVideoTask.execute();
	        else
	        	mQueryVideoTask.executeOnExecutor(QueryVideoTask.THREAD_POOL_EXECUTOR);
            break;
        }
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	if(event.getAction() == MotionEvent.ACTION_UP) {
    		if(!controller.isShowing()) {
	    		controller.show();
	    	} else {
	    		controller.hide();
	    	}
	    	return true;
    	}
        return false;
    }

    private void initView() {
    	
    	Bundle extras = getIntent().getExtras();
		if(extras != null) {
        	dramaId = extras.getInt("animate_id");
        	epsNum = extras.getInt("eps_num");
        }
    	  
		SQLiteTvAnimationHelper instance = SQLiteTvAnimationHelper.getInstance(PlayerActivity.this);
        SQLiteDatabase db = instance.getWritableDatabase();
        stopPosition = instance.getTvAnimationTimeRecord(db, dramaId);
		db.close();
        instance.closeHelper();
		
        controller = new VideoControllerView(this);
        mVideoView = (VideoView)findViewById(R.id.videoview);
        
        controller.setMediaPlayer(mVideoView);
        controller.setAnchorView((FrameLayout)findViewById(R.id.videoSurfaceContainer));
        
        
        mVideoView.setOnInfoListener(new OnInfoListener(){
			@Override
			public boolean onInfo(MediaPlayer mp, int what, int extra) {
				stopPosition = (int) mp.getCurrentPosition();
				return false;

			}        	
        });        

        mVideoView.setOnCompletionListener(new OnCompletionListener() {	
			@Override
			public void onCompletion(MediaPlayer mp) {
				if(stopPosition < mVideoView.getDuration() - filter){
					mQueryVideoTask = new QueryVideoTask();
			        if(Build.VERSION.SDK_INT < 11)
			        	mQueryVideoTask.execute();
			        else
			        	mQueryVideoTask.executeOnExecutor(QueryVideoTask.THREAD_POOL_EXECUTOR);
				}else{
					stopPosition = 0;
					Toast.makeText(PlayerActivity.this, "本集已撥放完畢",  Toast.LENGTH_SHORT).show();
		        	PlayerActivity.this.finish();
		        }
			}	
        });
        mVideoView.setOnPreparedListener(new OnPreparedListener() {	
        	@Override
        	public void onPrepared(MediaPlayer pMp) {
            	mDialogLoader.cancel();
            	animation.cancel();
            	ivDialogLoadingCircle.clearAnimation();
            	ivDialogLoadingIcon.setVisibility(View.GONE);
            	ivDialogLoadingCircle.setVisibility(View.GONE);
            }

        });
        mVideoView.setOnErrorListener(new OnErrorListener() {
        	@Override
        	public boolean onError(MediaPlayer mp, int what, int extra) {
        		if(mVideoView.isPlaying())
        		   mVideoView.stopPlayback();
        		
        		ConnectivityManager connManager = (ConnectivityManager) getSystemService(PlayerActivity.CONNECTIVITY_SERVICE); 
            	NetworkInfo info = connManager.getActiveNetworkInfo();
        		
        		if(info==null || !info.isConnected()){
        			setDialogWifi();
        			TvAnimationApplication.shIO.edit().putBoolean("wifi_remind", false).commit();
                	
        		}else {
        			
        			if(videoLink != null) {
    	        		Uri uri = Uri.parse(videoLink);
    	        		Intent it = new Intent(Intent.ACTION_VIEW, uri);
    	        		startActivity(it); 
    	        	}
        		
    			PlayerActivity.this.finish(); 
        		}
        		
	        	Toast.makeText(PlayerActivity.this, "影片撥放發生錯誤，請再嘗試一次",  Toast.LENGTH_SHORT).show();	        	
                return true;
        	}
        });
        
        setLoadingAnimation();
        mDialogLoader = new Dialog(this, R.style.dialogLoader);
        mDialogLoader.setContentView(R.layout.dialog_player_loader);
        mDialogLoader.setCanceledOnTouchOutside(false);
        mDialogLoader.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_BACK
						&& event.getAction() == KeyEvent.ACTION_DOWN){
					if(mVideoView.isPlaying())
						mVideoView.stopPlayback();
					if(mDialogLoader != null && mDialogLoader.isShowing())
						mDialogLoader.cancel();
					PlayerActivity.this.finish();
					return true;
				}
				return false;
			}			    	
        });

		ivDialogLoadingIcon = (ImageView)mDialogLoader.findViewById(R.id.iv_loading_icon);
		ivDialogLoadingCircle = (ImageView)mDialogLoader.findViewById(R.id.iv_loading_circle);
    }
	
	private void setLoadingAnimation() {
		animation = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration((long) 500);
		animation.setRepeatCount(Animation.INFINITE);
		animation.setInterpolator(AnimationUtils.loadInterpolator(this, android.R.anim.linear_interpolator));
		animation.setFillAfter(true);
		animation.setFillEnabled(true);
	}
    
    private class QueryVideoTask extends AsyncTask<String, String, Uri> {

        @Override
        protected void onPreExecute() {        	
        	ivDialogLoadingIcon.setVisibility(View.VISIBLE);
    		ivDialogLoadingCircle.setVisibility(View.VISIBLE);
    		ivDialogLoadingCircle.startAnimation(animation);    		
    		mDialogLoader.show();        	
        }
        
        @Override
        protected Uri doInBackground(String... pParams) {
        	Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            
        	TvAnimationAPI tvAnimationAPI = new TvAnimationAPI();
        	
        	videoLink = tvAnimationAPI.getVideoLink(dramaId, epsNum);            
            
            if (videoLink != null) {            	
            	return Uri.parse(videoLink);
            } else {            	
                return null;
            }            
        }

        @Override
        protected void onPostExecute(Uri pResult) {
            super.onPostExecute(pResult);
            
            if (pResult == null) {
            	PlayerActivity.this.finish();
            	Toast.makeText(PlayerActivity.this, "無法取得影片連結，請再嘗試一次",  Toast.LENGTH_SHORT).show();
                mDialogLoader.cancel();
            	animation.cancel();
            	ivDialogLoadingCircle.clearAnimation();
            	ivDialogLoadingIcon.setVisibility(View.GONE);
            	ivDialogLoadingCircle.setVisibility(View.GONE);
            } else {
                controller.mFullscreenButton.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						toggleFullScreen();
					}
                });
                playVideo(pResult);
                timeToast();
            }
        }
    }

    private void playVideo(Uri uri) {
    	if(mVideoView != null && mVideoView.isPlaying())
    		mVideoView.stopPlayback();
    	mVideoView.clearFocus();
    	mVideoView.setVideoURI(uri);
        mVideoView.requestFocus();
        mVideoView.start();
        mVideoView.seekTo(stopPosition);
    }
    
    private void timeToast() {
    	String timeStr = "";
    	int hou = stopPosition / (1000 * 60 * 60);    	
    	if(hou != 0)
    		timeStr = timeStr + hou + "時";
    	
    	int min = (stopPosition - hou * (1000 * 60 * 60)) / (1000 * 60);
    	if(min != 0)
    		timeStr = timeStr + min + "分";
    	
    	int sec = (stopPosition - hou * (1000 * 60 * 60) - min * (1000 * 60)) / 1000;
    	if(sec != 0)
    		timeStr = timeStr + sec + "秒";
    	
    	if(timeStr == "")
    		timeStr = "頭";
    		
    	String message = "影片將從 " + timeStr + " 開始撥放";
    	Toast.makeText(PlayerActivity.this, message,  Toast.LENGTH_SHORT).show();
    }
    
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
    
    protected void onPause() {
    	if(mVideoView != null) {
    		mVideoView.pause();
    	}
    	super.onPause();
    }

    protected void onResume() {
    	if(mVideoView != null) {
    	    mVideoView.seekTo(stopPosition);    
    	    mVideoView.start();
    	}
    	super.onResume();
    }

    @Override
    protected void onDestroy() { 
        super.onDestroy();

    	SQLiteTvAnimationHelper instance = SQLiteTvAnimationHelper.getInstance(PlayerActivity.this);
        SQLiteDatabase db = instance.getWritableDatabase();
        instance.updateTvAnimationTimeRecord(db, dramaId, stopPosition);
		db.close();
        instance.closeHelper();

    	if (mQueryVideoTask!= null && mQueryVideoTask.getStatus() != AsyncTask.Status.FINISHED) {
    		mQueryVideoTask.cancel(true);
	     }
    	
        if(animation != null)
        	animation.cancel();
        
        if (mVideoView != null && mVideoView.isPlaying()) {
            mVideoView.stopPlayback();
        }

        /*
         *  clear the flag that keeps the screen ON
         */
        getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        this.mVideoView = null;
    }

    @Override
	public void start() {
		mVideoView.start();		
	}

    @Override
	public void pause() {
		mVideoView.pause();
	}

    @Override
	public int getDuration() {
        return (int) mVideoView.getDuration();
	}

    @Override
	public int getCurrentPosition() {
		return (int) mVideoView.getCurrentPosition();
	}

	@Override
	public void seekTo(int pos) {
		mVideoView.seekTo(pos);
	}

	@Override
	public boolean isPlaying() {
		return mVideoView.isPlaying();
	}

	@Override
	public int getBufferPercentage() {
		return mVideoView.getBufferPercentage();
	}

	@Override
	public boolean canPause() {
		return true;
	}

	@Override
	public boolean canSeekBackward() {
		return true;
	}

	@Override
	public boolean canSeekForward() {
		return true;
	}

	@Override
	public boolean isFullScreen() {
		if(PlayerActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
    		return false;
		} else {
			return true;
		}
	}

	boolean quality = true;
	@Override
	public void toggleFullScreen() {
		if(PlayerActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			PlayerActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			PlayerActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				PlayerActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			}
		}, 2000);
	}
	
	private void setDialogWifi() {

		mDialogWifi = new Dialog(this, R.style.dialogBasic);
		mDialogWifi.setContentView(R.layout.dialog_player_wifi);
		
		
		Button buttonPlay = (Button)mDialogWifi.findViewById(R.id.button_play);
		Button buttonWifi = (Button)mDialogWifi.findViewById(R.id.button_wifi);
		
		buttonPlay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mDialogWifi.cancel();
				
				initView();
		        		        
		        mQueryVideoTask = new QueryVideoTask();
		        if(Build.VERSION.SDK_INT < 11)
		        	mQueryVideoTask.execute();
		        else
		        	mQueryVideoTask.executeOnExecutor(QueryVideoTask.THREAD_POOL_EXECUTOR);
			}			
		});
		buttonWifi.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mDialogWifi.cancel();
				startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), SETTING_WIFI_REQUESTCODE);
			}			
		});
		mDialogWifi.setCancelable(false);
		mDialogWifi.setCanceledOnTouchOutside(false);
		mDialogWifi.show();
	}
}