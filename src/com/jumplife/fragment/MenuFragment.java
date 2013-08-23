package com.jumplife.fragment;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.jumplife.tvanimation.MainActivity;
import com.jumplife.tvanimation.R;
import com.jumplife.tvanimation.TvAnimationApplication;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class MenuFragment extends Fragment {
	
	private View fragmentView;
	private LinearLayout llFavorite,
					llSetting,
					llHotHeart,
					llLove,
					llHumor,
					llSuspense,
					llFantasy,
					llOthers;
	private View vFavorite,
					vSetting,
					vHotHeart,
					vLove,
					vHumor,
					vSuspense,
					vFantasy,
					vOthers;
	public final static int FLAG_FAVORITE = -1,
					FLAG_SETTING = -2,
					FLAG_HOTHEART = 1,
					FLAG_LOVE = 2,
					FLAG_HUMOR = 3,
					FLAG_SUSPENSE = 4,
					FLAG_FANTASY = 5,
					FLAG_OTHERS = 6;
	
	private int typeId;
	private int sortId;
	
	private FragmentActivity mFragmentActivity;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		fragmentView = inflater.inflate(R.layout.fragment_menu, container, false);				
		initView();
		setListener();
		
		return fragmentView;
	}
	
	@Override
    public void onAttach(Activity activity) {
    	mFragmentActivity = getActivity();
        super.onAttach(activity);
    }
	
	private void initView() {
		llFavorite = (LinearLayout)fragmentView.findViewById(R.id.ll_myfavorite);
		llSetting = (LinearLayout)fragmentView.findViewById(R.id.ll_setting);
		llHotHeart = (LinearLayout)fragmentView.findViewById(R.id.ll_hotheart);
		llLove = (LinearLayout)fragmentView.findViewById(R.id.ll_love);
		llHumor = (LinearLayout)fragmentView.findViewById(R.id.ll_humor);
		llSuspense = (LinearLayout)fragmentView.findViewById(R.id.ll_suspense);
		llFantasy = (LinearLayout)fragmentView.findViewById(R.id.ll_fantasy);
		llOthers = (LinearLayout)fragmentView.findViewById(R.id.ll_other);
		
		vFavorite = (View)fragmentView.findViewById(R.id.v_myfavorite);
		vSetting = (View)fragmentView.findViewById(R.id.v_setting);
		vHotHeart = (View)fragmentView.findViewById(R.id.v_hotheart);
		vLove = (View)fragmentView.findViewById(R.id.v_love);
		vHumor = (View)fragmentView.findViewById(R.id.v_humor);
		vSuspense = (View)fragmentView.findViewById(R.id.v_suspense);
		vFantasy = (View)fragmentView.findViewById(R.id.v_fantasy);
		vOthers = (View)fragmentView.findViewById(R.id.v_other);
		
		typeId = TvAnimationApplication.shIO.getInt("typeId", 1);
		sortId = TvAnimationApplication.shIO.getInt("sortId", 0);
		
		switchItemState();
	}
	
	private void switchItemState() {		
		setItemStateNormal();
		setItemStatePress();
	}
	
	private void setItemStateNormal() {
		llFavorite.setBackgroundResource(R.drawable.fragement_menu_item_bg_normal);
		llSetting.setBackgroundResource(R.drawable.fragement_menu_item_bg_normal);
		llHotHeart.setBackgroundResource(R.drawable.fragement_menu_item_bg_normal);
		llLove.setBackgroundResource(R.drawable.fragement_menu_item_bg_normal);
		llHumor.setBackgroundResource(R.drawable.fragement_menu_item_bg_normal);
		llSuspense.setBackgroundResource(R.drawable.fragement_menu_item_bg_normal);
		llFantasy.setBackgroundResource(R.drawable.fragement_menu_item_bg_normal);
		llOthers.setBackgroundResource(R.drawable.fragement_menu_item_bg_normal);		

		vFavorite.setVisibility(View.INVISIBLE);
		vSetting.setVisibility(View.INVISIBLE);
		vHotHeart.setVisibility(View.INVISIBLE);
		vLove.setVisibility(View.INVISIBLE);
		vHumor.setVisibility(View.INVISIBLE);
		vSuspense.setVisibility(View.INVISIBLE);
		vFantasy.setVisibility(View.INVISIBLE);
		vOthers.setVisibility(View.INVISIBLE);
	}
	
	private void setItemStatePress() {
		switch(typeId) {
		case FLAG_FAVORITE:
			llFavorite.setBackgroundResource(R.color.background_item);
			vFavorite.setVisibility(View.VISIBLE);
			break;
		case FLAG_SETTING:
			llSetting.setBackgroundResource(R.color.background_item);
			vSetting.setVisibility(View.VISIBLE);
			break;
		case FLAG_HOTHEART:
			llHotHeart.setBackgroundResource(R.color.background_item);
			vHotHeart.setVisibility(View.VISIBLE);
			break;
		case FLAG_LOVE:
			llLove.setBackgroundResource(R.color.background_item);
			vLove.setVisibility(View.VISIBLE);
			break;
		case FLAG_HUMOR:
			llHumor.setBackgroundResource(R.color.background_item);
			vHumor.setVisibility(View.VISIBLE);
			break;
		case FLAG_SUSPENSE:
			llSuspense.setBackgroundResource(R.color.background_item);
			vSuspense.setVisibility(View.VISIBLE);
			break;
		case FLAG_FANTASY:
			llFantasy.setBackgroundResource(R.color.background_item);
			vFantasy.setVisibility(View.VISIBLE);
			break;
		case FLAG_OTHERS:
			llOthers.setBackgroundResource(R.color.background_item);
			vOthers.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}
	
	private void setListener() {
		final Tracker tracker = EasyTracker.getInstance(this.getActivity());
		tracker.set(Fields.SCREEN_NAME, "MenuFragment");
		
		llFavorite.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				tracker.send(MapBuilder.createEvent("設定", "點擊", "我的最愛", null).build());
				typeId = FLAG_FAVORITE;
				switchItemState();
				TvAnimationApplication.shIO.edit().putInt("typeId", FLAG_FAVORITE).commit();
				MyFavoriteFragment myFavorite = new MyFavoriteFragment();
				switchFragment(myFavorite, true);
			}			
		 });		
		 llSetting.setOnClickListener(new OnClickListener() {
			 public void onClick(View arg0) {
				tracker.send(MapBuilder.createEvent("設定", "點擊", "設定", null).build());
				typeId = FLAG_SETTING;
				switchItemState();
				TvAnimationApplication.shIO.edit().putInt("typeId", FLAG_SETTING).commit();
				SettingFragment setting = new SettingFragment();
				switchFragment(setting, true);
			 }			
		 });
		
		 llHotHeart.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				tracker.send(MapBuilder.createEvent("設定", "點擊", "熱血", null).build());
				typeId = FLAG_HOTHEART;
				switchItemState();
				TvAnimationApplication.shIO.edit().putInt("typeId", FLAG_HOTHEART).commit();
				TvAnimationGridlFragment tvTypes = TvAnimationGridlFragment.NewInstance(sortId, FLAG_HOTHEART);
				switchFragment(tvTypes, false);
			}			
		 });
		 llLove.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				tracker.send(MapBuilder.createEvent("設定", "點擊", "戀愛", null).build());
				typeId = FLAG_LOVE;
				switchItemState();
				TvAnimationApplication.shIO.edit().putInt("typeId", FLAG_LOVE).commit();
				TvAnimationGridlFragment tvTypes = TvAnimationGridlFragment.NewInstance(sortId, FLAG_LOVE);
				switchFragment(tvTypes, false);
			}			
		});
		llHumor.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				tracker.send(MapBuilder.createEvent("設定", "點擊", "搞笑", null).build());
				typeId = FLAG_HUMOR;
				switchItemState();
				TvAnimationApplication.shIO.edit().putInt("typeId", FLAG_HUMOR);				
				TvAnimationGridlFragment tvTypes = TvAnimationGridlFragment.NewInstance(sortId, FLAG_HUMOR);
				switchFragment(tvTypes, false);
			}			
		});
		llSuspense.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				tracker.send(MapBuilder.createEvent("設定", "點擊", "懸疑", null).build());
				typeId = FLAG_SUSPENSE;
				switchItemState();
				TvAnimationApplication.shIO.edit().putInt("typeId", FLAG_SUSPENSE).commit();				
				TvAnimationGridlFragment tvTypes = TvAnimationGridlFragment.NewInstance(sortId, FLAG_SUSPENSE);
				switchFragment(tvTypes, false);
			}			
		});
		llFantasy.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				tracker.send(MapBuilder.createEvent("設定", "點擊", "奇幻", null).build());
				typeId = FLAG_FANTASY;
				switchItemState();
				TvAnimationApplication.shIO.edit().putInt("typeId", FLAG_FANTASY).commit();				
				TvAnimationGridlFragment tvTypes = TvAnimationGridlFragment.NewInstance(sortId, FLAG_FANTASY);
				switchFragment(tvTypes, false);
			}			
		});
		llOthers.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				tracker.send(MapBuilder.createEvent("設定", "點擊", "其他", null).build());
				typeId = FLAG_OTHERS;
				switchItemState();
				TvAnimationApplication.shIO.edit().putInt("typeId", FLAG_OTHERS);				
				TvAnimationGridlFragment tvTypes = TvAnimationGridlFragment.NewInstance(sortId, FLAG_OTHERS);
				switchFragment(tvTypes, false);
			}			
		});
	}
	
	// the meat of switching the above fragment
	private void switchFragment(Fragment fragment, boolean isAdd) {
		if (mFragmentActivity == null)
			return;
		
		if (mFragmentActivity instanceof MainActivity) {
			MainActivity tvfa = (MainActivity) getActivity();
			tvfa.switchContent(fragment, typeId, isAdd);
		} else 
			return;
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
