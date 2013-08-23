package com.jumplife.fragment;

import java.util.ArrayList;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.jumplife.tvanimation.ChapterActivity;
import com.jumplife.tvanimation.R;
import com.jumplife.tvanimation.adapter.AnimationGridAdapter;
import com.jumplife.tvanimation.entity.Animate;
import com.jumplife.tvanimation.sqlitehelper.SQLiteTvAnimationHelper;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MyFavoriteFragment extends Fragment {
	
	private View 				fragmentView;
	private GridView        	gvAnimate;
	private TextView 			tvNoDrama;
    private ImageButton     	ibRefresh;
    private AnimationGridAdapter adapter;
    private ArrayList<Animate>	animateList;
    
    private Animation animation;
	private ImageView ivLoadingIcon;
	private ImageView ivLoadingCircle;

    private LoadDataTask     loadTask;
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
	
	private void initView() {
		
    	gvAnimate = (GridView)fragmentView.findViewById(R.id.gridview_myfavorite);
	    tvNoDrama=(TextView)fragmentView.findViewById(R.id.textview_myfavorite);
	    ibRefresh = (ImageButton)fragmentView.findViewById(R.id.refresh);
		
	    /*
		 * Loading Animation Init
		 */
		setLoadingAnimation();
		ivLoadingIcon = (ImageView)fragmentView.findViewById(R.id.iv_loading_icon);
		ivLoadingCircle = (ImageView)fragmentView.findViewById(R.id.iv_loading_circle);
        
		ibRefresh.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
            	loadTask = new LoadDataTask();
            	if(Build.VERSION.SDK_INT < 11)
            		loadTask.execute();
                else
                	loadTask.executeOnExecutor(LoadDataTask.THREAD_POOL_EXECUTOR, 0);
            }
        });
        
	}
	
	private void setLoadingAnimation() {
		animation = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration((long) 500);
		animation.setRepeatCount(Animation.INFINITE);
		animation.setInterpolator(AnimationUtils.loadInterpolator(mFragmentActivity, android.R.anim.linear_interpolator));
		animation.setFillAfter(true);
		animation.setFillEnabled(true);
	}
    
	private void setViews() {
        gvAnimate.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent newAct = new Intent();
                newAct.putExtra("animate_id", animateList.get(position).getId());
                newAct.putExtra("animate_name", animateList.get(position).getName());
				newAct.setClass(mFragmentActivity, ChapterActivity.class );
				
				Tracker tracker = EasyTracker.getInstance(MyFavoriteFragment.this.getActivity());
				tracker.set(Fields.SCREEN_NAME, "我的最愛Fragment");
				 
				tracker.send(MapBuilder.createEvent("我的最愛Fragment", "點擊", animateList.get(position).getName(), null).build());
				
				mFragmentActivity.startActivity(newAct);
            }
        });
        adapter = new AnimationGridAdapter(mFragmentActivity, animateList);        
        gvAnimate.setAdapter(adapter);
    }
    
    private ArrayList<Animate> fetchData() {
    	animateList = new ArrayList<Animate>(30); 	
    	SQLiteTvAnimationHelper instance = SQLiteTvAnimationHelper.getInstance(mFragmentActivity);
		SQLiteDatabase db = instance.getReadableDatabase();
		animateList = instance.getTvAnimationLikeList(db);
		db.close();
        instance.closeHelper();
        
        return animateList;
    }

	
	class LoadDataTask extends AsyncTask<Integer, Integer, ArrayList<Animate>> {
		
        @Override
        protected void onPreExecute() {
			ivLoadingIcon.setVisibility(View.VISIBLE);
			ivLoadingCircle.setVisibility(View.VISIBLE);
			ivLoadingCircle.startAnimation(animation);
			
			ibRefresh.setVisibility(View.GONE);
	        super.onPreExecute();
        }
    
		@Override
		protected ArrayList<Animate> doInBackground(Integer... params) {
			Thread.currentThread().setPriority(Thread.MAX_PRIORITY);	
			return fetchData();
		}
		
	   @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(ArrayList<Animate> animate) {
        	
        	if (animateList == null) {
        		gvAnimate.setVisibility(View.GONE);
    			tvNoDrama.setVisibility(View.GONE);
    			ibRefresh.setVisibility(View.VISIBLE);
			} else {
				ibRefresh.setVisibility(View.GONE);
			       if(animateList.size() > 0) {
			    	   gvAnimate.setVisibility(View.VISIBLE);
			    	   tvNoDrama.setVisibility(View.GONE);
			    	   setViews();
			       } else {
			    	   gvAnimate.setVisibility(View.GONE);
			    	   tvNoDrama.setVisibility(View.VISIBLE);
			       }
    		}
        	
        	animation.cancel();
        	ivLoadingCircle.clearAnimation();
        	ivLoadingIcon.setVisibility(View.GONE);
        	ivLoadingCircle.setVisibility(View.GONE);
        	
        	super.onPostExecute(animate);
        }
        
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
        if (loadTask!= null && loadTask.getStatus() != AsyncTask.Status.FINISHED) {
        	loadTask.cancel(true);
        	
        }
        
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
