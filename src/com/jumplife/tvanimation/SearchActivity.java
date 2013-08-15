package com.jumplife.tvanimation;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.actionbarsherlock.app.SherlockActivity;
import com.jumplife.tvanimation.entity.Animate;
import com.jumplife.tvanimation.sqlitehelper.SQLiteTvAnimationHelper;

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
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class SearchActivity extends SherlockActivity {
	private EditText etSearchInput;      
	private ListView lvSearchOutput;
	
	private ArrayList<Animate> animateList = new ArrayList<Animate>();
	private ArrayList<Animate> temps;
	private List<Map<String, Object>> items;
	private Map<String, Object> item; 
	
	private SearchListAdapter Adapter;
	
	int textlength=0;
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    getSupportActionBar().setIcon(R.drawable.loading_logo);
	    getSupportActionBar().setTitle("搜尋");
	    getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
	    
	    
	    setContentView(R.layout.activity_search);

		initViews();  
		fetchData();
		setListData();
	}

	private void initViews() {
		etSearchInput = (EditText) findViewById (R.id.et_search_input);
		lvSearchOutput = (ListView) findViewById (R.id.lv_output);
		lvSearchOutput.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent newAct = new Intent();
                newAct.putExtra("animate_id", temps.get(position).getId());
                newAct.putExtra("animate_name", temps.get(position).getName());
                newAct.setClass(SearchActivity.this, ChapterActivity.class);
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
	    	
	    	temps.clear();
	    	items.clear();
			for(int i=0; i<animateList.size(); i++) {
				if(textlength <= animateList.get(i).getName().length()) {
					for(int j=0; j<=(animateList.get(i).getName().length()-textlength); j++)
						if(etSearchInput.getText().toString().equalsIgnoreCase((String) animateList.get(i).getName().subSequence(j, j+textlength))) 
							hasItem = true;
					if(hasItem) {
						temps.add(animateList.get(i));
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
        item.put("NAME", animateList.get(i).getName());
        
        items.add(item);  
	}
	private void fetchData() {
		SQLiteTvAnimationHelper instance = SQLiteTvAnimationHelper.getInstance(this);
        SQLiteDatabase db = instance.getWritableDatabase();
        animateList = instance.getTvAnimationAllList(db);
        db.close();
        instance.closeHelper();
	}

	private void setListData() {
		/* 
		 * ListData 
		 */
		temps = new ArrayList<Animate>(animateList);  
		items = new ArrayList<Map<String,Object>>();
        for (int i = 0; i < animateList.size(); i++) {
        	fetchListData(i);  
        }
        /* 
         * Adaptersetting
         */
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
      	if(animateList == null || animateList.size() <= 0) {
      		SQLiteTvAnimationHelper instance = SQLiteTvAnimationHelper.getInstance(this);
            SQLiteDatabase db = instance.getWritableDatabase();
            animateList = instance.getTvAnimationAllList(db);
            db.close();
            instance.closeHelper();
            
      	}

    } 
}
