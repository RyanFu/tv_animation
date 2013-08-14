package com.jumplife.actionprovider;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import com.actionbarsherlock.view.ActionProvider;
import com.jumplife.tvanimation.R;
import com.jumplife.tvanimation.SearchActivity;

public class SearchActionProvider extends ActionProvider {

	private final Context mContext;

    public SearchActionProvider(Context context) {
        super(context);
        mContext = context;
    }

	@Override
	public View onCreateActionView() {
        View vSearch = LayoutInflater.from(mContext).inflate(R.layout.action_bar_search, null);

        //Bind to its state change
        ImageButton ibSearch = (ImageButton)vSearch.findViewById(R.id.ib_search);
        ibSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Log.d(null, "click search");
				Intent newAct = new Intent();
				newAct.setClass(mContext, SearchActivity.class );
				mContext.startActivity(newAct);
			}        	
        });
        
        return vSearch;
	}

}
