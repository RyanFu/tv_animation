package com.jumplife.tvanimation;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jumplife.fragment.MenuFragment;
import com.jumplife.fragment.MyFavoriteFragment;
import com.jumplife.fragment.TvAnimationGridlFragment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.SlidingMenu.OnCloseListener;
import com.slidingmenu.lib.SlidingMenu.OnOpenListener;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;


public class MainPageActivity extends SlidingFragmentActivity {
	
	private static SlidingMenu menu;
	private int typeId;
	private int sortId;
	private int openCount;
	private int version;
	//private LoadPromoteTask loadPromoteTask;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setActionBarListNavigation();
		getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
		
		// set the Behind View
		setBehindContentView(R.layout.fragmentlayout_tvanimation_menu);
		MenuFragment menuFrag = new MenuFragment();
		if (savedInstanceState == null) {
			FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
			t.replace(R.id.menu_frame, menuFrag);
			t.commit();
		} else {
			menuFrag = (MenuFragment)this.getSupportFragmentManager().findFragmentById(R.id.menu_frame);
		}
		
		// customize the SlidingMenu
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
		 *  set the Above View
		 */
		typeId = TvAnimationApplication.shIO.getInt("typeId", 1);
		sortId = TvAnimationApplication.shIO.getInt("sortId", 1);
		setActionBarTitle(typeId);
		
		if(typeId < 0) {
			MyFavoriteFragment myFavorite = new MyFavoriteFragment();
			getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.content_frame, myFavorite)
			.commit();			

			setContentView(R.layout.fragmentlayout_tvanimation_content);
			getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.content_frame, myFavorite)
			.commit();
			
		} else {
			TvAnimationGridlFragment tvchannels = TvAnimationGridlFragment.NewInstance(sortId, typeId); 
			getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.content_frame, tvchannels)
			.commit();

			setContentView(R.layout.fragmentlayout_tvanimation_content);
			getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.content_frame, tvchannels)
			.commit();
		}
		
		setSlidingActionBarEnabled(false);
		
		
		/*
		 * Promote
		 */
        openCount = TvAnimationApplication.shIO.getInt("opencount", 0);
        version = TvAnimationApplication.shIO.getInt("version", 0);
		/*loadPromoteTask = new LoadPromoteTask();
    	if(openCount > 5) {
        	loadPromoteTask.execute();
        	openCount = 0;
        }*/
        openCount += 1;
        TvAnimationApplication.shIO.edit().putInt("opencount", openCount).commit();
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
	
	private void setActionBarListNavigation() {
		Context context = getSupportActionBar().getThemedContext();
        ArrayAdapter<String> list = 
        		new ArrayAdapter<String>(context, R.layout.sherlock_spinner_item, new String[]{"依更新日期排序", "依撥放次數排序"});
        list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

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

	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

        	if(!menu.isMenuShowing()) {
        		/*PromoteAPP promoteAPP = new PromoteAPP(TvVarietyFragmentActivity.this);
	        	if(!promoteAPP.isPromote) {
		        	new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.leave_app))
		            .setPositiveButton(getResources().getString(R.string.leave), new DialogInterface.OnClickListener() {
		                // do something when the button is clicked
		                public void onClick(DialogInterface arg0, int arg1) {
		                	MainPageActivity.this.finish();
		                }
		            }).setNegativeButton(getResources().getString(R.string.cancel), null)
		            .show();
			    } else
			    	promoteAPP.promoteAPPExe();*/
        		return super.onKeyDown(keyCode, event);
        	} else {
        		menu.showContent();
        		return true;
        	}
        } else
            return super.onKeyDown(keyCode, event);
    }
	
    /*class LoadPromoteTask extends AsyncTask<Integer, Integer, String>{  
        
		private String[] promotion = null;
        private ProgressDialog progressdialogInit;
        private AlertDialog dialogPromotion;
        
        private OnCancelListener cancelListener = new OnCancelListener(){
		    public void onCancel(DialogInterface arg0){
		    	LoadPromoteTask.this.cancel(true);
		    }
    	};

    	@Override  
        protected void onPreExecute() {
    		progressdialogInit= new ProgressDialog(MainPageActivity.this);
        	progressdialogInit.setTitle("Load");
        	progressdialogInit.setMessage("Loading…");
        	progressdialogInit.setOnCancelListener(cancelListener);
        	progressdialogInit.setCanceledOnTouchOutside(false);
        	if(progressdialogInit != null && !progressdialogInit.isShowing())
        		progressdialogInit.show();
			super.onPreExecute();  
        }  
    	
		@Override  
        protected String doInBackground(Integer... params) {
			VarietyAPI varietyAPI = new VarietyAPI();
			promotion = new String[5];
			promotion = varietyAPI.getPromotion();
			return "progress end";  
        }  
  
        @Override  
        protected void onProgressUpdate(Integer... progress) {    
            super.onProgressUpdate(progress);  
        }  
  
        @Override  
        protected void onPostExecute(String result) {
        	closeProgressDilog();
        	
        	if(promotion != null && !promotion[1].equals("null") && Integer.valueOf(promotion[4]) > version) {
	        	View viewPromotion;
	            LayoutInflater factory = LayoutInflater.from(MainPageActivity.this);
	            viewPromotion = factory.inflate(R.layout.dialog_promotion,null);
	            dialogPromotion = new AlertDialog.Builder(MainPageActivity.this).create();
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
	                public boolean onKey(DialogInterface dialog, int keyCode,
	                        KeyEvent event) {
	                	shIO.SharePreferenceI("version", Integer.valueOf(promotion[4]));
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
	                    	
	                    	HashMap<String, String> parameters = new HashMap<String, String>();
	                    	parameters.put("LINK", promotion[1]);
	    					
	                    	Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(promotion[1]));
	                    	MainPageActivity.this.startActivity(intent);
	                    }
	                }
	            );
	            dialogPromotion.setCanceledOnTouchOutside(false);
	            dialogPromotion.show();
        	}
	       	super.onPostExecute(result);  
        } 
        
        public void closeProgressDilog() {
        	if(MainPageActivity.this != null && !MainPageActivity.this.isFinishing() 
        			&& progressdialogInit != null && progressdialogInit.isShowing())
        		progressdialogInit.dismiss();
        }   
    }*/
	
	@Override
	protected void onDestroy(){
		/*if (loadPromoteTask!= null && loadPromoteTask.getStatus() != AsyncTask.Status.FINISHED) {
        	loadPromoteTask.closeProgressDilog();
        	loadPromoteTask.cancel(true);
        }*/
        super.onDestroy();
	}
}
