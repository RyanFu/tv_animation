package com.jumplife.fragment;

import java.util.ArrayList;
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
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class TvChannelFragment extends Fragment {
	
	/*private TextView 		 tvTopbarText;
	private View 			 fragmentView;
	private ImageButton      imageButtonRefresh;
	private ImageButton 	 ibMenu;
	private ImageButton 	 ibSearch;
	private ImageButton 	 ibAboutUs;
	private ProgressBar 	 pbInit;
	private GridView 		 varietyGridView;
	private VarietysGridAdapter adapter;
    private ArrayList<Variety> varietyList;
    
    private FragmentActivity mFragmentActivity;
    
	private LoadDataTask loadtask;
	
	public static TvChannelFragment NewInstance(int typeId, int sortId) {
		TvChannelFragment fragment = new TvChannelFragment();
	    Bundle args = new Bundle();
	    args.putInt("typeId", typeId);
	    args.putInt("sortId", sortId);
	    fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		fragmentView = inflater.inflate(R.layout.fragment_tvchannel, container, false);				
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
        pbInit = (ProgressBar)fragmentView.findViewById(R.id.pb_tvchannel);
		varietyGridView = (GridView)fragmentView.findViewById(R.id.gridview_tvchannel);
		
		tvTopbarText = (TextView)fragmentView.findViewById(R.id.topbar_text);		
		setTopBarText();
		
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

		
		ibMenu = (ImageButton)fragmentView.findViewById(R.id.iv_menu);
		ibMenu.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				TvVarietyFragmentActivity.slideMenuSwitch();
			}			
		});
        
		ibSearch = (ImageButton)fragmentView.findViewById(R.id.iv_search);
		ibSearch.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent newAct = new Intent();
	            newAct.setClass(mFragmentActivity, SearchVarietyActivity.class);
	            mFragmentActivity.startActivity(newAct);
			}			
		});
		
		ibAboutUs = (ImageButton)fragmentView.findViewById(R.id.iv_aboutus);
		ibAboutUs.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent newAct = new Intent();
	            newAct.setClass(mFragmentActivity, AboutUsActivity.class);
	            mFragmentActivity.startActivity(newAct);
			}			
		});
	}
	
	private void fetchData() {
		varietyList = new ArrayList<Variety>();
		if(getArguments().containsKey("typeId")) {
			try {
				SQLiteTvVarietyHelper instance = SQLiteTvVarietyHelper.getInstance(mFragmentActivity);
				SQLiteDatabase db = instance.getReadableDatabase();
				varietyList = instance.getVarietyList(db, getArguments().getInt("typeId", 0));
				db.close();
		        instance.closeHelper();
			} catch (Exception e) {
				
			}
		}
	}
	
	private void setGridAdatper() {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		mFragmentActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
		adapter = new VarietysGridAdapter(mFragmentActivity, varietyList, ((screenWidth / 2)), (int) (((screenWidth / 2)) * 0.6));
        varietyGridView.setAdapter(adapter);
	}
	
	private void setListener() {
        varietyGridView.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent newAct = new Intent();
	            newAct.putExtra("variety_id", varietyList.get(position).getId());
	            newAct.putExtra("variety_name", varietyList.get(position).getName());
	            newAct.putExtra("variety_poster", varietyList.get(position).getPosterUrl());
	            newAct.setClass(mFragmentActivity, VarietyChapterActivity.class);
	            mFragmentActivity.startActivity(newAct);
			}
		});
        varietyGridView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
	}
	
	private void setTopBarText() {
		int flag = 0;
		if(getArguments().containsKey("typeId"))
			flag = getArguments().getInt("typeId", 0);
		
		switch(flag) {
		case MenuFragment.FLAG_INTERVIEW:
			tvTopbarText.setText(getResources().getString(R.string.interview));
			break;
		case MenuFragment.FLAG_FOOD:
			tvTopbarText.setText(getResources().getString(R.string.food));
			break;
		case MenuFragment.FLAG_HUMOR:
			tvTopbarText.setText(getResources().getString(R.string.humor));
			break;
		case MenuFragment.FLAG_WONMAN:
			tvTopbarText.setText(getResources().getString(R.string.woman));
			break;
		case MenuFragment.FLAG_POLITICAL:
			tvTopbarText.setText(getResources().getString(R.string.political));
			break;
		case MenuFragment.FLAG_ENTERTAINMENT:
			tvTopbarText.setText(getResources().getString(R.string.entertainment));
			break;
		case MenuFragment.FLAG_MUSIC:
			tvTopbarText.setText(getResources().getString(R.string.music));
			break;
		case MenuFragment.FLAG_HEALTH:
			tvTopbarText.setText(getResources().getString(R.string.health));
			break;
		case MenuFragment.FLAG_WEDDING:
			tvTopbarText.setText(getResources().getString(R.string.wedding));
			break;
		case MenuFragment.FLAG_OTHERS:
			tvTopbarText.setText(getResources().getString(R.string.others));
			break;
		default:
			break;
		}
	}
	
	class LoadDataTask extends AsyncTask<Integer, Integer, String>{  
        
    	@Override  
        protected void onPreExecute() {
    		varietyGridView.setVisibility(View.GONE);
        	imageButtonRefresh.setVisibility(View.GONE);
    		pbInit.setVisibility(View.VISIBLE);
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
        	pbInit.setVisibility(View.GONE);
			if(varietyList != null && varietyList.size() > 0){
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
    }*/
}
