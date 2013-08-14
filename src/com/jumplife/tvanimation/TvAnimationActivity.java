package com.jumplife.tvanimation;

import java.util.ArrayList;

import com.google.android.gcm.GCMRegistrar;
import com.jumplife.tvanimation.api.TvAnimationAPI;
import com.jumplife.tvanimation.entity.Animate;
import com.jumplife.tvanimation.sqlitehelper.SQLiteTvAnimationHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class TvAnimationActivity extends Activity {
	private LoadDataTask taskLoad;
	private CheckVersionTask taskVersion;
	private ImageView ivLoading;
	private TextView tvloading;
	private AnimationDrawable animationDrawable;

    private AsyncTask<Void, Void, Void> mRegisterTask;
    
	public static String TAG = "UsaDramaActivity";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_tvanimation);
        
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        
        RelativeLayout.LayoutParams ivparams = new RelativeLayout.LayoutParams(
        		displayMetrics.widthPixels / 3, displayMetrics.widthPixels / 3);
        ivparams.setMargins(0, displayMetrics.widthPixels / 6, 0, 50);
        ivparams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        ivLoading = (ImageView)findViewById(R.id.iv_load);
        ivLoading.setLayoutParams(ivparams);
        ivLoading.setScaleType(ScaleType.FIT_CENTER);
        
        RelativeLayout.LayoutParams tvparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        tvparams.setMargins(0, (int) (displayMetrics.heightPixels * 0.75), 0, (int) (displayMetrics.heightPixels - (displayMetrics.heightPixels*0.95)));
        tvparams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        tvloading = (TextView)findViewById(R.id.tv_load);
        tvloading.setText("檢查版本中");
        tvloading.setLayoutParams(tvparams);
        
        checkNotNull(CommonUtilities.SERVER_URL, "SERVER_URL");
        checkNotNull(CommonUtilities.SENDER_ID, "SENDER_ID");
        GCMRegistrar.checkDevice(getApplicationContext());
        final String regId = GCMRegistrar.getRegistrationId(getApplicationContext());
        if (regId.equals("")) {
            GCMRegistrar.register(this, CommonUtilities.SENDER_ID);
        } else {
        	if (!GCMRegistrar.isRegisteredOnServer(this)) {
                final Context context = this;
                mRegisterTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        boolean registered = ServerUtilities.register(context, regId);
                        if (!registered) {
                            GCMRegistrar.unregister(context);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        mRegisterTask = null;
                    }

                };
                mRegisterTask.execute(null, null, null);
            }
        }

        taskVersion = new CheckVersionTask();
        if(Build.VERSION.SDK_INT < 11)
        	taskVersion.execute();
        else
        	taskVersion.executeOnExecutor(CheckVersionTask.THREAD_POOL_EXECUTOR, 0);
    }
	
	private void checkNotNull(Object reference, String name) {
        if (reference == null) {
        	throw new NullPointerException("error");
        }
    }    
	
	@Override  
    public void onWindowFocusChanged(boolean hasFocus) {  
        super.onWindowFocusChanged(hasFocus);  
        ivLoading.setBackgroundResource(R.anim.landingpgaeicon);
        animationDrawable = (AnimationDrawable) ivLoading.getBackground();
        animationDrawable.start();
    } 
	
	@SuppressWarnings("unchecked")
	private String fetchData(){
		long startTime = System.currentTimeMillis();
		TvAnimationAPI dramaAPI = new TvAnimationAPI();
        
        ArrayList<Animate> dramas = dramaAPI.getTvAnimationsIdViewsEps();
        if(dramas != null && dramas.size() > 0) {
                		
        	SQLiteTvAnimationHelper instance = SQLiteTvAnimationHelper.getInstance(this);
    		instance.createDataBase();
    		SQLiteDatabase db = instance.getWritableDatabase();
    		db.beginTransaction();
    		
    		
        	ArrayList<Integer> a = new ArrayList<Integer>(100);
        	for(int i=0; i<dramas.size(); i++)
        		a.add(dramas.get(i).getId());
	        ArrayList<Integer> dramasInsertId = new ArrayList<Integer>();
	        ArrayList<Integer> dramasShowId = new ArrayList<Integer>();
	        
	        dramasInsertId = instance.findTvAnimationsIdNotInDB(db, a);
	        dramasShowId = (ArrayList<Integer>) a.clone();
	        
	        if (dramasInsertId.size() > 0){
	        	String idLst = "";
		        for(int i=0; i<dramasInsertId.size(); i++)
		           idLst = dramasInsertId.get(i) + "," +idLst;
		        dramaAPI.AddTvAnimationsFromInfo(instance, db, idLst);
	        }	        
	        
	        instance.updateTvAnimationIsShow(db, dramasShowId);
	        instance.updateTvAnimationViews(db, dramas);
	        instance.updateDramaEps(db, dramas);
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
	        instance.closeHelper();
	        
	        
	        long endTime = System.currentTimeMillis();
	        Log.e(TAG, "sample method took（movie time activity) %%%%%%%%%%%%%%%%%%%%%%%%%%%%"+(endTime-startTime)+"ms");

			return "progress end";
        } else {
        	SQLiteTvAnimationHelper instance = SQLiteTvAnimationHelper.getInstance(this);
    		instance.createDataBase();
    		SQLiteDatabase db = instance.getWritableDatabase();
            db.close();
	        instance.closeHelper();
	        
        	return "progress fail";
        }
	}
	
	private void setData(){
		
        Intent newAct = new Intent();
		newAct.setClass( TvAnimationActivity.this, MainPageActivity.class );
		startActivity(newAct);
    	finish();
	}
	
	class CheckVersionTask extends AsyncTask<Integer, Integer, String>{  
        
		int[] mVersionCode = new int[]{-1};
		String[] message = new String[]{""};
		
        @Override  
        protected void onPreExecute() {
        	super.onPreExecute();  
        }  
          
        @Override  
        protected String doInBackground(Integer... params) {
        	Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        	TvAnimationAPI api = new TvAnimationAPI();
        	Log.d("", "version code " + mVersionCode[0]);
        	api.getVersionCode(mVersionCode, message);
        	Log.d("", "version code " + mVersionCode[0]);
            return "progress end";
        }  
 

		@Override  
        protected void onProgressUpdate(Integer... progress) {    
            super.onProgressUpdate(progress);  
        }  
  
        @Override  
        protected void onPostExecute(String result) {
        	PackageInfo packageInfo = null;
        	int tmpVersionCode = -1;
			try {
				packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(packageInfo != null)
				tmpVersionCode = packageInfo.versionCode;
			
			if(tmpVersionCode > -1 && tmpVersionCode < mVersionCode[0]) {
	        	new AlertDialog.Builder(TvAnimationActivity.this).setTitle("已有新版電視連續劇")
	    		.setMessage(message[0])
	            .setPositiveButton("前往更新", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface arg0, int arg1) {	                	
	                	startActivity(new Intent(Intent.ACTION_VIEW, 
	    			    		Uri.parse("market://details?id=com.jumplife.tvdrama")));
	                	TvAnimationActivity.this.finish();
	                }
	            })
	            .setNegativeButton("下次再說", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface arg0, int arg1) {
	                    tvloading.setText("讀取中...");
	                	taskLoad = new LoadDataTask();
	                    if(Build.VERSION.SDK_INT < 11)
	                    	taskLoad.execute();
	                    else
	                    	taskLoad.executeOnExecutor(LoadDataTask.THREAD_POOL_EXECUTOR, 0);
	                }
	            })
	            .show();
		        super.onPostExecute(result);
			} else {
		        tvloading.setText("讀取中...");
				taskLoad = new LoadDataTask();
		        if(Build.VERSION.SDK_INT < 11)
		        	taskLoad.execute();
		        else
		        	taskLoad.executeOnExecutor(LoadDataTask.THREAD_POOL_EXECUTOR, 0);
			}
				
        }  
          
    }

	class LoadDataTask extends AsyncTask<Integer, Integer, String>{  
        
        @Override  
        protected void onPreExecute() {
        	super.onPreExecute();  
        }  
          
        @Override  
        protected String doInBackground(Integer... params) {
        	Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            return fetchData();
        }  
 

		@Override  
        protected void onProgressUpdate(Integer... progress) {    
            super.onProgressUpdate(progress);  
        }  
  
        @Override  
        protected void onPostExecute(String result) {
        	if(result.equals("progress fail")) {
	        	ivLoading.setVisibility(View.INVISIBLE);
	        	tvloading.setText(Html.fromHtml("網路連線不穩" + "<br>" + "更新劇集失敗"));
	        	try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        	setData();
	        super.onPostExecute(result);  
        }  
          
    }
	
	@Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
    
    @Override
    public void onDestroy() {
      super.onDestroy();
      if(animationDrawable != null && animationDrawable.isRunning())
    	  animationDrawable.stop();
      if (taskLoad!= null && taskLoad.getStatus() != AsyncTask.Status.FINISHED)
			taskLoad.cancel(true);
      if (taskVersion!= null && taskVersion.getStatus() != AsyncTask.Status.FINISHED)
    	  taskVersion.cancel(true);
    }
}
