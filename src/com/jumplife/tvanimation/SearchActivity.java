package com.jumplife.tvanimation;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.actionbarsherlock.app.SherlockActivity;
import com.jumplife.tvanimation.entity.Animate;
import com.jumplife.tvanimation.sqlitehelper.SQLiteTvAnimationHelper;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class SearchActivity extends SherlockActivity {
	private ImageButton ibSearch;     	
	private EditText etSearchInput;      
	private ListView lvSearchOutput;
	
	private ArrayList<Animate> dramas = new ArrayList<Animate>();
	private ArrayList<Animate> temps;
	private List<Map<String, Object>> items;
	private Map<String, Object> item; 
	
	private SearchListAdapter Adapter;
	
	int textlength=0;
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
	    setContentView(R.layout.acticity_search);

		initViews();  
		fetchData();
		setListData();
		//this.setAd();


	}

	private void initViews() {
		etSearchInput = (EditText) findViewById (R.id.et_search_input);
		lvSearchOutput = (ListView) findViewById (R.id.lv_output);
		ibSearch = (ImageButton) findViewById(R.id.ib_search_icon);
		lvSearchOutput.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent newAct = new Intent();
              //  newAct.putExtra("drama_id", temps.get(position).getId());
              //  newAct.putExtra("drama_name", temps.get(position).getName());
              //  newAct.setClass(SearchActivity.this, ChapterActivity.class);
                startActivity(newAct);
            }
        });
		
		etSearchInput.addTextChangedListener(filterTextWatcher);
	}
	private TextWatcher filterTextWatcher = new TextWatcher() {

	    public void afterTextChanged(Editable s) {
	    
	    }

	    public void beforeTextChanged(CharSequence s, int start, int count,
	            int after) {
	    }

	    public void onTextChanged(CharSequence s, int start, int before,int count) 
	    {
	    	boolean hasItem = false;
	    	textlength = etSearchInput.getText().length();
	    	
	    	//EasyTracker.getTracker().trackEvent("搜尋", "打字", etSearchInput.getText().toString(), (long)0);
	    	
	    	temps.clear();
	    	items.clear();
			for(int i=0; i<dramas.size(); i++) {
				if(textlength <= dramas.get(i).getName().length()) {
					for(int j=0; j<=(dramas.get(i).getName().length()-textlength); j++)
						if(etSearchInput.getText().toString().equalsIgnoreCase((String) dramas.get(i).getName().subSequence(j, j+textlength))) 
							hasItem = true;
					if(hasItem) {
						temps.add(dramas.get(i));
						fetchListData(i); 
						hasItem = false;	
					}//if
				}//if
			}//for
		
			Adapter.notifyDataSetChanged();
	    }//onTextChanged


	};//TextWatcher
	private void fetchListData(int i) {
		item = new HashMap<String, Object>();
        item.put("NAME", dramas.get(i).getName());
        
        items.add(item);  
	}
	private void fetchData() {
		SQLiteTvAnimationHelper instance = SQLiteTvAnimationHelper.getInstance(this);
        SQLiteDatabase db = instance.getWritableDatabase();
       //dramas = instance.getTvAnimationAllList(db);
        db.close();
        instance.closeHelper();
        Animate a = new Animate();
        for(int x=0 ; x<20 ; x++){
        	a.setName("一二三四五六七八九"+x);
        	dramas.add(a);
        }
        		
	}

	private void setListData() {
		/* ListData */
		temps = new ArrayList<Animate>(dramas);  
		items = new ArrayList<Map<String,Object>>();
        for (int i = 0; i < dramas.size(); i++) {
        	fetchListData(i);  
        }
        /* Adaptersetting */
		Adapter =new  SearchListAdapter(SearchActivity.this,
										items,
										R.layout.listview_item_search,
										new String[]{"NAME"},
										new int[] {R.id.tv_listitem_drama});
		lvSearchOutput.setAdapter(Adapter);
	}
	public class SearchListAdapter extends SimpleAdapter implements Filterable {
		public SearchListAdapter(Context context, List<? extends Map<String, ?>> data,int resource, String[] from, int[] to) {
			super(context, data, resource, from, to);
		}
      	
    }
	@Override
    public void onStart() {
    	super.onStart();
      	if(dramas == null || dramas.size() <= 0) {
      		SQLiteTvAnimationHelper instance = SQLiteTvAnimationHelper.getInstance(this);
            SQLiteDatabase db = instance.getWritableDatabase();
            //dramas = instance.getTvAnimationAllList(db);
            db.close();
            instance.closeHelper();
            
      	}

    } 
}
