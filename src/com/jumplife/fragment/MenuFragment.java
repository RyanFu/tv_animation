package com.jumplife.fragment;

import com.jumplife.tvanimation.MainPageActivity;
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
	
	private int currentProgramFlag;   
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
		
		int flag = TvAnimationApplication.shIO.getInt("tvtype_flag", FLAG_HOTHEART);
		currentProgramFlag = flag;
		switchItemState(flag);
	}
	
	private void switchItemState(int flag) {		
		setItemStateNormal();
		setItemStatePress(flag);
	}
	
	private void setItemStateNormal() {
		llFavorite.setBackgroundResource(R.color.transparent100);
		llSetting.setBackgroundResource(R.color.transparent100);
		llHotHeart.setBackgroundResource(R.color.transparent100);
		llLove.setBackgroundResource(R.color.transparent100);
		llHumor.setBackgroundResource(R.color.transparent100);
		llSuspense.setBackgroundResource(R.color.transparent100);
		llFantasy.setBackgroundResource(R.color.transparent100);
		llOthers.setBackgroundResource(R.color.transparent100);		

		vFavorite.setVisibility(View.INVISIBLE);
		vSetting.setVisibility(View.INVISIBLE);
		vHotHeart.setVisibility(View.INVISIBLE);
		vLove.setVisibility(View.INVISIBLE);
		vHumor.setVisibility(View.INVISIBLE);
		vSuspense.setVisibility(View.INVISIBLE);
		vFantasy.setVisibility(View.INVISIBLE);
		vOthers.setVisibility(View.INVISIBLE);
	}
	
	private void setItemStatePress(int flag) {
		switch(flag) {
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
		 /*llFavorite.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				TvAnimationApplication.shIO.edit().putInt("pre_flag", currentProgramFlag);
				switchItemState(FLAG_FAVORITE);
				TvAnimationApplication.shIO.edit().putInt("tvtype_flag", FLAG_FAVORITE);
				MyFavoriteFragment myFavorite = new MyFavoriteFragment();
				switchFragment(myFavorite, true);
			}			
		});*/
		llHotHeart.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				currentProgramFlag = FLAG_HOTHEART;
				switchItemState(FLAG_HOTHEART);
				TvAnimationApplication.shIO.edit().putInt("tvtype_flag", FLAG_HOTHEART).commit();
				/*TvChannelFragment tvchannels = TvChannelFragment.NewInstance(FLAG_HOTHEART);
				switchFragment(tvchannels, false);*/
			}			
		});
		llLove.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				currentProgramFlag = FLAG_LOVE;
				switchItemState(FLAG_LOVE);
				TvAnimationApplication.shIO.edit().putInt("tvtype_flag", FLAG_LOVE).commit();
				/*TvChannelFragment tvchannels = TvChannelFragment.NewInstance(FLAG_LOVE);
				switchFragment(tvchannels, false);*/
			}			
		});
		llHumor.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				currentProgramFlag = FLAG_HUMOR;
				switchItemState(FLAG_HUMOR);
				TvAnimationApplication.shIO.edit().putInt("tvtype_flag", FLAG_HUMOR);				
				/*TvChannelFragment tvchannels = TvChannelFragment.NewInstance(FLAG_HUMOR);
				switchFragment(tvchannels, false);*/
			}			
		});
		llSuspense.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				currentProgramFlag = FLAG_SUSPENSE;
				switchItemState(FLAG_SUSPENSE);
				TvAnimationApplication.shIO.edit().putInt("tvtype_flag", FLAG_SUSPENSE).commit();				
				/*TvChannelFragment tvchannels = TvChannelFragment.NewInstance(FLAG_SUSPENSE);
				switchFragment(tvchannels, false);*/
			}			
		});
		llFantasy.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				currentProgramFlag = FLAG_FANTASY;
				switchItemState(FLAG_FANTASY);
				TvAnimationApplication.shIO.edit().putInt("tvtype_flag", FLAG_FANTASY).commit();				
				/*TvChannelFragment tvchannels = TvChannelFragment.NewInstance(FLAG_FANTASY);
				switchFragment(tvchannels, false);*/
			}			
		});
		llOthers.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				currentProgramFlag = FLAG_OTHERS;
				switchItemState(FLAG_OTHERS);
				TvAnimationApplication.shIO.edit().putInt("tvtype_flag", FLAG_OTHERS);				
				/*TvChannelFragment tvchannels = TvChannelFragment.NewInstance(FLAG_OTHERS);
				switchFragment(tvchannels, false);*/
			}			
		});
	}
	
	// the meat of switching the above fragment
	private void switchFragment(Fragment fragment, boolean isAdd) {
		if (mFragmentActivity == null)
			return;
		
		if (mFragmentActivity instanceof MainPageActivity) {
			MainPageActivity tvfa = (MainPageActivity) getActivity();
			tvfa.switchContent(fragment, isAdd);
		} else 
			return;
	}
}
