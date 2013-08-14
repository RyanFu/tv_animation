package com.jumplife.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.jumplife.tvanimation.R;
import com.jumplife.tvanimation.entity.Animate;
import com.jumplife.tvanimation.sqlitehelper.SQLiteTvAnimationHelper;
import com.jumplife.usadrama.adapter.AnimationGridAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

import android.app.Activity;
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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.AdapterView.OnItemClickListener;

public class TvAnimationGridlFragment extends Fragment {
	
	private View 			 fragmentView;
	private ImageButton      imageButtonRefresh;
	private GridView 		 varietyGridView;
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
        varietyGridView = (GridView)fragmentView.findViewById(R.id.gridview_tvchannel);
		
		imageButtonRefresh = (ImageButton)fragmentView.findViewById(R.id.refresh);
		imageButtonRefresh.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
            	loadtask = new LoadDataTask();
                if(Build.VERSION.SDK_INT < 11)
                	loadtask.execute();
                else
                	loadtask.executeOnExecutor(LoadDataTask.THREAD_POOL_EXECUTOR, 0);
            }
        });
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
        varietyGridView.setAdapter(adapter);
	}
	
	private void setListener() {
        varietyGridView.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
			}
		});
        varietyGridView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
	}
	
	class LoadDataTask extends AsyncTask<Integer, Integer, String>{  
        
    	@Override  
        protected void onPreExecute() {
    		varietyGridView.setVisibility(View.GONE);
        	imageButtonRefresh.setVisibility(View.GONE);
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
            	varietyGridView.setVisibility(View.VISIBLE);
            	imageButtonRefresh.setVisibility(View.GONE);		
    		} else {
    			varietyGridView.setVisibility(View.GONE);
                imageButtonRefresh.setVisibility(View.VISIBLE);
    		}

	        super.onPostExecute(result);  
        }
    }
}
