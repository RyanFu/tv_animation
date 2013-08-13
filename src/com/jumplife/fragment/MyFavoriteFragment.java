package com.jumplife.fragment;

import java.util.ArrayList;

import com.jumplife.tvanimation.R;
import com.jumplife.tvanimation.entity.Animate;
import com.jumplife.tvanimation.sqlitehelper.SQLiteTvAnimationHelper;
import com.jumplife.usadrama.adapter.AnimationGridAdapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.AdapterView.OnItemClickListener;

public class MyFavoriteFragment extends Fragment {
	
	private View 			 fragmentView;
	private GridView         gvAnimate;
    private ImageButton      imageButtonRefresh;
    private LoadDataTask     loadTask;
    private AnimationGridAdapter adapter;
    private ArrayList<Animate> animateList;
    
    private FragmentActivity mFragmentActivity;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		fragmentView = inflater.inflate(R.layout.fragment_myfavorite, container, false);				
		initView();
		
		return fragmentView;
	}
	
	@Override
    public void onAttach(Activity activity) {
    	mFragmentActivity = getActivity();
        super.onAttach(activity);
    }
	
	@Override
	public void onResume() {
    	loadTask = new LoadDataTask();
    	if(Build.VERSION.SDK_INT < 11)
        	loadTask.execute();
        else
        	loadTask.executeOnExecutor(LoadDataTask.THREAD_POOL_EXECUTOR, 0);
        
        super.onResume();
    }
	
	@Override
	public void onDestroy(){
        super.onDestroy();
        if (loadTask!= null && loadTask.getStatus() != AsyncTask.Status.FINISHED) {
        	loadTask.cancel(true);
        	loadTask.closeProgressDilog();
        }
    }
	
	private void initView() {
		
    	gvAnimate = (GridView)fragmentView.findViewById(R.id.gridview_myfavorite);
        
    	imageButtonRefresh = (ImageButton)fragmentView.findViewById(R.id.refresh);
        imageButtonRefresh.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
            	loadTask = new LoadDataTask();
            	if(Build.VERSION.SDK_INT < 11)
            		loadTask.execute();
                else
                	loadTask.executeOnExecutor(LoadDataTask.THREAD_POOL_EXECUTOR, 0);
            }
        });
        
	}
    
 // 設定畫面上的UI
    private void setViews() {
        gvAnimate.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	
            }
        });
        adapter = new AnimationGridAdapter(mFragmentActivity, animateList);        
        gvAnimate.setAdapter(adapter);
    }
    
    private void fetchData() {
    	animateList = new ArrayList<Animate>(30); 	
    	SQLiteTvAnimationHelper instance = SQLiteTvAnimationHelper.getInstance(mFragmentActivity);
		SQLiteDatabase db = instance.getReadableDatabase();
		animateList = instance.getTvAnimationLikeList(db);
		db.close();
        instance.closeHelper();
    }

    class LoadDataTask extends AsyncTask<Integer, Integer, String> {

        private ProgressDialog         progressdialogInit;
        private final OnCancelListener cancelListener = new OnCancelListener() {
		      public void onCancel(DialogInterface arg0) {
		          LoadDataTask.this.cancel(true);
		          gvAnimate.setVisibility(View.GONE);
		          imageButtonRefresh.setVisibility(View.VISIBLE);
		      }
		  };

        @Override
        protected void onPreExecute() {
            progressdialogInit = new ProgressDialog(mFragmentActivity);
            progressdialogInit.setTitle("Load");
            progressdialogInit.setMessage("Loading…");
            progressdialogInit.setOnCancelListener(cancelListener);
            progressdialogInit.setCanceledOnTouchOutside(false);
            progressdialogInit.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Integer... params) {
        	Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            fetchData();
            return "progress end";
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(String result) {
        	closeProgressDilog();

            if (animateList == null) {
            	gvAnimate.setVisibility(View.GONE);
		        imageButtonRefresh.setVisibility(View.VISIBLE);
            } else {
            	imageButtonRefresh.setVisibility(View.GONE);
            	if(animateList.size() > 0) {
            		gvAnimate.setVisibility(View.VISIBLE);
	                setViews();
            	} else {
            		gvAnimate.setVisibility(View.GONE);
            	}
            }

            super.onPostExecute(result);
        }

        public void closeProgressDilog() {
        	if(mFragmentActivity != null && !mFragmentActivity.isFinishing() 
        			&& progressdialogInit != null && progressdialogInit.isShowing())
        		progressdialogInit.dismiss();
        }

    }
}
