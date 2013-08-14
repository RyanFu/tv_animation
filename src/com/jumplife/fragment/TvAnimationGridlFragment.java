package com.jumplife.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.jumplife.tvanimation.ChapterActivity;
import com.jumplife.tvanimation.R;
import com.jumplife.tvanimation.adapter.AnimationGridAdapter;
import com.jumplife.tvanimation.entity.Animate;
import com.jumplife.tvanimation.sqlitehelper.SQLiteTvAnimationHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

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
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;

public class TvAnimationGridlFragment extends Fragment {
	
	private View 			fragmentView;
    private ImageButton		ibRefresh;
	
	private Animation animation;
	private ImageView ivLoadingIcon;
	private ImageView ivLoadingCircle;
	
	private GridView 		 gvAnimation;
	private AnimationGridAdapter adapter;
    private ArrayList<Animate> animateList;    
    private FragmentActivity mFragmentActivity;    
	private LoadDataTask loadtask;
	
	public static TvAnimationGridlFragment NewInstance(int sortId, int typeId) {
		TvAnimationGridlFragment fragment = new TvAnimationGridlFragment();
	    Bundle args = new Bundle();
	    args.putInt("sortId", sortId);
	    args.putInt("typeId", typeId);
	    fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		fragmentView = inflater.inflate(R.layout.fragment_tvanimation_grid, container, false);
		
		initView();
		
        loadtask = new LoadDataTask();
	    if(Build.VERSION.SDK_INT < 11)
	    	loadtask.execute();
        else
        	loadtask.executeOnExecutor(LoadDataTask.THREAD_POOL_EXECUTOR, 0);
	    
		return fragmentView;
	}
	
	@Override
    public void onAttach(Activity activity) {
    	mFragmentActivity = getActivity();
        super.onAttach(activity);
    }
	
	private void initView() {
        gvAnimation = (GridView)fragmentView.findViewById(R.id.gridview_tvchannel);
	    
	    /*
		 * Loading Animation Init
		 */
		setLoadingAnimation();
		ivLoadingIcon = (ImageView)fragmentView.findViewById(R.id.iv_loading_icon);
		ivLoadingCircle = (ImageView)fragmentView.findViewById(R.id.iv_loading_circle);
		
		ibRefresh = (ImageButton)fragmentView.findViewById(R.id.refresh);
		ibRefresh.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
            	loadtask = new LoadDataTask();
                if(Build.VERSION.SDK_INT < 11)
                	loadtask.execute();
                else
                	loadtask.executeOnExecutor(LoadDataTask.THREAD_POOL_EXECUTOR, 0);
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
	
	private void newSort(ArrayList<Animate> AnimateList) {
		Collections.sort(AnimateList, new Comparator<Animate>(){
			public int compare(Animate obj1,Animate obj2){
	    		if(obj1.getId() < obj2.getId()) 
	    			return 1;   
	    		else if(obj1.getId() == obj2.getId()) 
	    			return 0;
	    		else
	    			return -1;
			}
		});
	}
	
	private void hotSort(ArrayList<Animate> AnimateList) {
		Collections.sort(AnimateList, new Comparator<Animate>(){
			public int compare(Animate obj1,Animate obj2){
				if(obj1.getViews()< obj2.getViews()) 
	    			return 1;   
	    		else if(obj1.getViews() == obj2.getViews()) 
	    			return 0;
	    		else
	    			return -1;
			}
		});
	}
	
	private void fetchData() {
		animateList = new ArrayList<Animate>();
		if(getArguments().containsKey("typeId")) {
			try {
				SQLiteTvAnimationHelper instance = SQLiteTvAnimationHelper.getInstance(mFragmentActivity);
				SQLiteDatabase db = instance.getReadableDatabase();
				animateList = instance.getTvAnimationTypeList(db, getArguments().getInt("typeId", 0));
				db.close();
		        instance.closeHelper();
			} catch (Exception e) {
				
			}
		}
		
		if(getArguments().getInt("sortId", 0) == 0)
			newSort(animateList);
		else
			hotSort(animateList);
	}
	
	private void setGridAdatper() {
		adapter = new AnimationGridAdapter(mFragmentActivity, animateList);
        gvAnimation.setAdapter(adapter);
	}
	
	private void setListener() {
        gvAnimation.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent newAct = new Intent();
                newAct.putExtra("animate_id", animateList.get(position).getId());
                newAct.putExtra("animate_name", animateList.get(position).getName());
				newAct.setClass(mFragmentActivity, ChapterActivity.class );
				mFragmentActivity.startActivity(newAct);				
			}
		});
        gvAnimation.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
	}
	
	class LoadDataTask extends AsyncTask<Integer, Integer, String>{  
        
    	@Override  
        protected void onPreExecute() {
			ivLoadingIcon.setVisibility(View.VISIBLE);
			ivLoadingCircle.setVisibility(View.VISIBLE);
			ivLoadingCircle.startAnimation(animation);
			
			ibRefresh.setVisibility(View.GONE);
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
        	if(animateList != null && animateList.size() > 0){
        		setGridAdatper();
        		setListener();
            	gvAnimation.setVisibility(View.VISIBLE);
            	ibRefresh.setVisibility(View.GONE);		
    		} else {
    			gvAnimation.setVisibility(View.GONE);
    			ibRefresh.setVisibility(View.VISIBLE);
    		}
        	
        	animation.cancel();
        	ivLoadingCircle.clearAnimation();
        	ivLoadingIcon.setVisibility(View.GONE);
        	ivLoadingCircle.setVisibility(View.GONE);

	        super.onPostExecute(result);  
        }
    }
}
