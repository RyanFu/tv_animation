package com.jumplife.tvanimation;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jumplife.fragment.MenuFragment;
import com.jumplife.fragment.MyFavoriteFragment;
import com.jumplife.fragment.SettingFragment;
import com.jumplife.fragment.TvAnimationGridlFragment;
import com.jumplife.tvanimation.api.TvAnimationAPI;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.SlidingMenu.OnCloseListener;
import com.slidingmenu.lib.SlidingMenu.OnOpenListener;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends SlidingFragmentActivity {
	
	private static SlidingMenu menu;
	
	private Animation animation;
	private ImageView ivDialogLoadingIcon;
	private ImageView ivDialogLoadingCircle;
	private Dialog mDialogLoader;
	
	private int typeId;
	private int sortId;
	private int openCount;
	private int version;
	
	private LoadPromoteTask loadPromoteTask;
	
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initView();
		initSlidingMenu(savedInstanceState);
		initPromote();
	}

	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
        	if(menu.isMenuShowing()) {
        		menu.showContent();
        		return true;
        	}
        }
        
        return super.onKeyDown(keyCode, event);
    }

	 @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getSupportMenuInflater().inflate(R.menu.action_bar_search, menu);
        return true;
    }
	 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	

	private void initView() {
		setActionBarListNavigation();
		getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
		
		/*
		 * Loading Animation Init
		 */
		setLoadingAnimation();
		
		mDialogLoader = new Dialog(MainActivity.this, R.style.dialogLoader);
        mDialogLoader.setContentView(R.layout.layout_loading);
        mDialogLoader.setCanceledOnTouchOutside(false);        
		ivDialogLoadingIcon = (ImageView)mDialogLoader.findViewById(R.id.iv_loading_icon);
		ivDialogLoadingCircle = (ImageView)mDialogLoader.findViewById(R.id.iv_loading_circle);		
	}
	
	private void setActionBarListNavigation() {
		Context context = getSupportActionBar().getThemedContext();
        ArrayAdapter<String> list = 
        		new ArrayAdapter<String>(context, R.layout.sherlock_spinner_item, new String[]{"依更新日期排序", "依撥放次數排序"});
        list.setDropDownViewResource(R.layout.spinner_dropdown_sort_item);

        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getSupportActionBar().setListNavigationCallbacks(list, new OnNavigationListener() {
			@Override
			public boolean onNavigationItemSelected(int itemPosition,
					long itemId) {
				TvAnimationGridlFragment fragment = TvAnimationGridlFragment.NewInstance(itemPosition, typeId);
				switchContent(fragment, typeId, false);
				return false;
			}        	
        });
	}
	
	private void initSlidingMenu(Bundle savedInstanceState) {
		
		/*
		 * Init SlidingMenu
		 */
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int screenWidth = displayMetrics.widthPixels;
		
		menu = getSlidingMenu();
	    menu.setShadowWidth(screenWidth/4);
	    menu.setShadowDrawable(R.drawable.shadow);
	    menu.setBehindOffset(screenWidth/2);
	    menu.setFadeDegree(0.35f);
	    menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
	    menu.setOnOpenListener(new OnOpenListener(){
			@Override
			public void onOpen() {
				getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			}	    	
	    });
	    menu.setOnCloseListener(new OnCloseListener(){
			@Override
			public void onClose() {
				setActionBarTitle(typeId);
			}	    	
	    });
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
				
		/*
		 *  set the Behind View
		 */
		setBehindContentView(R.layout.fragmentlayout_tvanimation_menu);
		MenuFragment menuFrag = new MenuFragment();
		if (savedInstanceState == null) {
			FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
			t.replace(R.id.menu_frame, menuFrag);
			t.commit();
		} else {
			menuFrag = (MenuFragment)this.getSupportFragmentManager().findFragmentById(R.id.menu_frame);
		}
		
		/*
		 *  set the Above View
		 */
		typeId = TvAnimationApplication.shIO.getInt("typeId", 1);
		sortId = TvAnimationApplication.shIO.getInt("sortId", 1);
		setActionBarTitle(typeId);		

		setContentView(R.layout.fragmentlayout_tvanimation_content);
		if(typeId == -1) {
			MyFavoriteFragment myFavorite = new MyFavoriteFragment();
			getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.content_frame, myFavorite)
			.commit();			
		} else if(typeId == -2) {
			SettingFragment setting = new SettingFragment();
			getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.content_frame, setting)
			.commit();
		} else {
			TvAnimationGridlFragment tvchannels = TvAnimationGridlFragment.NewInstance(sortId, typeId); 
			getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.content_frame, tvchannels)
			.commit();
		}
		
		setSlidingActionBarEnabled(false);
		
	}
	
	private void setLoadingAnimation() {
		animation = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration((long) 500);
		animation.setRepeatCount(Animation.INFINITE);
		animation.setInterpolator(AnimationUtils.loadInterpolator(MainActivity.this, android.R.anim.linear_interpolator));
		animation.setFillAfter(true);
		animation.setFillEnabled(true);
	}
	
	
	/*
	 * Sliding Menu Exchange Content Fragment
	 */
	public void switchContent(Fragment fragment, int type, boolean isAdd) {
		typeId = type;
		
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.content_frame, fragment)
		.commit();
		menu.showContent();		
	}
	
	private void setActionBarTitle(int type) {
		switch(type) {
		case MenuFragment.FLAG_FAVORITE :
			getSupportActionBar().setTitle("我的最愛");
			getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			break;
		case MenuFragment.FLAG_SETTING :
			getSupportActionBar().setTitle("設定");
			getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			break;
		case MenuFragment.FLAG_HOTHEART :
			getSupportActionBar().setTitle("熱血");
			getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
			break;
		case MenuFragment.FLAG_LOVE :
			getSupportActionBar().setTitle("戀愛");
			getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
			break;
		case MenuFragment.FLAG_HUMOR :
			getSupportActionBar().setTitle("搞笑");
			getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
			break;
		case MenuFragment.FLAG_SUSPENSE :
			getSupportActionBar().setTitle("懸疑");
			getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
			break;
		case MenuFragment.FLAG_FANTASY :
			getSupportActionBar().setTitle("奇幻");
			getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
			break;
		case MenuFragment.FLAG_OTHERS :
			getSupportActionBar().setTitle("其他");
			getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
			break;
		default:
			getSupportActionBar().setTitle("");
			getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			break;
		}
	}
	
	/*
	 * promote
	 */
	private void initPromote() {
		
        openCount = TvAnimationApplication.shIO.getInt("opencount", 0);
        version = TvAnimationApplication.shIO.getInt("version", 0);
		loadPromoteTask = new LoadPromoteTask();
    	if(openCount > 5) {
        	loadPromoteTask.execute();
        	openCount = 0;
        }
        openCount += 1;
        TvAnimationApplication.shIO.edit().putInt("opencount", openCount).commit();
		
	}
	
	class LoadPromoteTask extends AsyncTask<Integer, Integer, String>{  
	        
		private String[] promotion = null;
        private AlertDialog dialogPromotion;
        
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
			TvAnimationAPI tvAnimationAPI = new TvAnimationAPI();
			promotion = new String[5];
			promotion = tvAnimationAPI.getPromotion();
			return "progress end";  
        }  
  
        @Override  
        protected void onProgressUpdate(Integer... progress) {    
            super.onProgressUpdate(progress);  
        }  
  
        @Override  
        protected void onPostExecute(String result) {
        	mDialogLoader.cancel();
        	animation.cancel();
        	ivDialogLoadingCircle.clearAnimation();
        	ivDialogLoadingIcon.setVisibility(View.GONE);
        	ivDialogLoadingCircle.setVisibility(View.GONE);
        	
        	if(promotion != null && !promotion[1].equals("null") && Integer.valueOf(promotion[4]) > version) {
	        	View viewPromotion;
	            LayoutInflater factory = LayoutInflater.from(MainActivity.this);
	            viewPromotion = factory.inflate(R.layout.dialog_promotion,null);
	            dialogPromotion = new AlertDialog.Builder(MainActivity.this).create();
	            dialogPromotion.setView(viewPromotion);
	            ImageView imageView = (ImageView)viewPromotion.findViewById(R.id.imageView1);
	            TextView textviewTitle = (TextView)viewPromotion.findViewById(R.id.textView1);
	            TextView textviewDescription = (TextView)viewPromotion.findViewById(R.id.textView2);
				if(!promotion[0].equals("null"))
					imageLoader.displayImage(promotion[0], imageView, options);
				else
					imageView.setVisibility(View.GONE);
				if(!promotion[2].equals("null"))
					textviewTitle.setText(promotion[2]);
				else
					textviewTitle.setVisibility(View.GONE);
				if(!promotion[3].equals("null"))
					textviewDescription.setText(promotion[3]);
				else
					textviewDescription.setVisibility(View.GONE);
	            dialogPromotion.setOnKeyListener(new OnKeyListener(){
	                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
	                	//UsaDramaApplication.shIO.edit().putInt("version", Integer.valueOf(promotion[4])).commit();
	                    
	                	if(KeyEvent.KEYCODE_BACK==keyCode)
	                    	if(dialogPromotion != null && dialogPromotion.isShowing())
	                    		dialogPromotion.cancel();
	                    return false;
	                }
	            });
	            
	            ((Button)viewPromotion.findViewById(R.id.button2))
	            .setOnClickListener(
	                new OnClickListener(){
	                    public void onClick(View v) {
	                        //取得文字方塊中的關鍵字字串
	                    	TvAnimationApplication.shIO.edit().putInt("version", Integer.valueOf(promotion[4])).commit();
	                    	if(dialogPromotion != null && dialogPromotion.isShowing())
	                    		dialogPromotion.cancel();
	                    	Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(promotion[1]));
	                    	MainActivity.this.startActivity(intent);
	                    }
	                }
	            );
	            
	            dialogPromotion.setCanceledOnTouchOutside(false);
	            dialogPromotion.show();
        	}
	       	super.onPostExecute(result);  
        } 
        
         
    }
}
