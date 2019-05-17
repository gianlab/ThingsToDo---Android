/*
 *  Copyright (C) 2009 gian
   
 This program is licensed under the Apache License, Version 2.0 (the "License");
 You may obtain a copy of the License at
 
       http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 
 Thanks to Google for code in ApiDemos and Notepads
 
 Thanks to Abdylas Tynyshov - userinterfaceicons.com for icons released
 under  Creative Commons Attribution 3.0 United States License:
 http://creativecommons.org/licenses/by/3.0/us/ .*/
package com.example.cosedafare;

import java.util.Calendar;
import java.util.Comparator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class CategoryEdit extends Activity {

	
	private Spinner mSpinner;
	private EditText mCategory;
	private DbAdapter mDbHelper;
	public static final int INSERT_ID =  Menu.FIRST ;
    public static final int DELETE_ID =  Menu.FIRST + 1;
    private ArrayAdapter <CharSequence>adapter;

    //private Long IDoperazione;


	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		/*if(extras!=null){
			mRowId=extras.getLong(DbAdapter.KEY_ROWID);
		}*/	
			mDbHelper = new DbAdapter(this);
			mDbHelper.open();
			
			
			setContentView(R.layout.category_edit);
		    mSpinner=(Spinner)findViewById(R.id.ListCategories); 
		    mSpinner.setAdapter(getAdapter());
		    mCategory=(EditText) findViewById(R.id.entry_category);
		    createButtons();
		
	}
	
		
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		//outState.putLong(DbAdapter.KEY_ROWID, mRowId);
	}
	@Override
	protected void onPause() {
		super.onPause();
		//finish();
		//saveState();
	}

	@Override
	protected void onResume() {
		super.onResume();
		//populateFields();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
       
        menu.add(0, INSERT_ID, 0, R.string.add_category).setIcon(R.drawable.db_add);
        menu.add(0, DELETE_ID, 0, R.string.delete).setIcon(R.drawable.db_remove);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

    	try{
    		switch(item.getItemId()) {

    		case INSERT_ID:
    			
    			
    			if(adapter.getCount()==10)
    				new AlertDialog.Builder(this)
    			.setMessage(R.string.max_categories)
    			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int whichButton) {

    				}})
    				.show();
    			else addDb();
    			break;
    		case DELETE_ID:
    			deleteFromDb();
    			break;
    		}
    	} catch (Exception ex){
    		Log.v("OnMenuItemSelected:",ex.toString());
    	}
    	return super.onMenuItemSelected(featureId, item);
    }

    private ArrayAdapter <CharSequence>getAdapter(){
    	adapter =new ArrayAdapter<CharSequence>(
    			this.getApplicationContext(), android.R.layout.simple_spinner_item);
    	adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
    	final class CategorySort implements Comparator<CharSequence>{

    		@Override
    		public int compare(CharSequence arg0, CharSequence arg1) {
    			return arg0.toString().compareToIgnoreCase(arg1.toString());
    		}

    	}
    	Cursor cursor=mDbHelper.fetchAllCategories();
    	startManagingCursor(cursor);
    	adapter.clear();
    	cursor.moveToNext();
    	while (cursor.moveToNext())
    		adapter.add(cursor.getString(
    				cursor.getColumnIndexOrThrow(DbAdapter.KEY_CATEGORIA)));
    	adapter.sort(new CategorySort());
    	return adapter;
    }
    
    private void createButtons(){
		
		Button deleteButton = (Button) findViewById(R.id.delete);
		Button saveButton = (Button) findViewById(R.id.save);
		
		
		saveButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				addDb();
				new AlertDialog.Builder(CategoryEdit.this)
    			.setMessage("Categoria salvata!")
    			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int whichButton) {

    				}})
    				.show();
			}

		});

		deleteButton = (Button) findViewById(R.id.delete);
		deleteButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				deleteFromDb();
				new AlertDialog.Builder(CategoryEdit.this)
    			.setMessage("Categoria cancellata!")
    			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int whichButton) {

    				}})
    				.show();
			}

		});	
	}
    
    private void addDb(){
    	String category=mCategory.getText().toString();
		if (!category.equals("") ) {
			
			mDbHelper.createCategory(category);
			mSpinner.setAdapter(getAdapter());
			MainActivity.fillAdapter();
			ListTransaction.createMaps();
			
		}
    }
    
    private void deleteFromDb(){
    	String category=mSpinner.getSelectedItem().toString();
		mDbHelper.deleteCategory(category);
		mSpinner.setAdapter(getAdapter());
		MainActivity.fillAdapter();
		ListTransaction.createMaps();
		
	}
 
}
