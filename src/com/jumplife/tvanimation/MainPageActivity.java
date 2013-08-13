package com.jumplife.tvanimation;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jumplife.fragment.MenuFragment;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;


public class MainPageActivity extends SlidingFragmentActivity {
	
	private static SlidingMenu menu;
	private int openCount;
	private int version;
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
		menu = getSlidingMenu();
	    menu.setShadowWidth(80);
	    menu.setShadowDrawable(R.drawable.shadow);
	    menu.setBehindOffset(160);
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
				getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
			}	    	
	    });
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
				
		// set the Above View
		setContentView(R.layout.fragmentlayout_tvanimation_content);
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.content_frame, new MenuFragment())
		.commit();
		
		setSlidingActionBarEnabled(false);
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
        		new ArrayAdapter<String>(context, R.layout.sherlock_spinner_item, new String[]{"依更新日期排序", "依撥放日期排序"});
        list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getSupportActionBar().setListNavigationCallbacks(list, new OnNavigationListener() {
			@Override
			public boolean onNavigationItemSelected(int itemPosition,
					long itemId) {
				// TODO Auto-generated method stub
				return false;
			}        	
        });
	}
	
	public void switchContent(Fragment fragment, boolean isAdd) {
		getSupportActionBar().setTitle("");
		
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.content_frame, fragment)
		.commit();
		menu.showContent();		
	}

}
