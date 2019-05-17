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

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class TransactionEdit extends Activity {

	
	private Long mRowId;
	
	private TextView mDateDisplay;
	private EditText mDescription;
	private DbAdapter mDbHelper;
	
	private ArrayList <Integer>listPrior = new ArrayList<Integer>();
	private Spinner mSpinner,mSpinnerPrior;
	private Bundle extras;
	private boolean isDeleting=false;
	private int mYear;
    private int mMonth;
    private int mDay;

    final DateFormat df = DateFormat.getDateInstance(DateFormat.FULL);  
	public static final int INSERT_ID =  Menu.FIRST ;
    public static final int DELETE_ID =  Menu.FIRST + 1;
    private static final int DATE_DIALOG_ID = Menu.FIRST + 2;
    //private Long IDoperazione;
 // the callback received when the user "sets" the date in the dialog
    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year, 
                                      int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    updateDisplay();
                }
            };


	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		/*if(extras!=null){
			mRowId=extras.getLong(DbAdapter.KEY_ROWID);
		}*/	
			mDbHelper = new DbAdapter(this);
			mDbHelper.open();
			setContentView(R.layout.transaction_edit);
			mDescription=(EditText) findViewById(R.id.entry_azione);
			mSpinner=(Spinner)findViewById(R.id.spinnerEdit);
    		mSpinner.setAdapter(getAdapter(true));
    		
    		mSpinnerPrior=(Spinner)findViewById(R.id.spinnerPriorEdit);
    		mSpinnerPrior.setAdapter(getAdapter(false));
    		
    		// capture our View elements
            mDateDisplay = (TextView) findViewById(R.id.dateDisplay);
            //testDateFormat();

            // get the current date
            final Calendar c = Calendar.getInstance();
           
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            // display the current date
            updateDisplay();

			
			
		       
		createButtons();
		
		//if (!extras.getBoolean("tab1")){
			mRowId = 
				(icicle != null ? icicle.getLong(DbAdapter.KEY_ROWID): null);
			if (mRowId == null) {
				extras = getIntent().getExtras();
				mRowId = extras != null ? extras.getLong(DbAdapter.KEY_ROWID) 
						: null;

			}
			mYear=(mRowId==null)?c.get(Calendar.YEAR)+2:c.get(Calendar.YEAR);
			populateFields();
		//}
	}
	
	private void populateFields() {
		
		if (mRowId != null) {
			//Log.v("mRowID",mRowId.toString());
			//Log.v("IDoperazione",IDoperazione.toString());
			Cursor operation = mDbHelper.fetchAzione(mRowId);
						
				mDateDisplay.setText(df.format(new Date(operation.getLong(
						operation.getColumnIndexOrThrow(DbAdapter.KEY_DATA)))));
				mDescription.setText(operation.getString(
						operation.getColumnIndexOrThrow(DbAdapter.KEY_AZIONE)));
				
				
				selectSpinner(operation.getInt(operation.getColumnIndexOrThrow(DbAdapter.KEY_ID_CATEGORIA)));
				int position=listPrior.indexOf(operation.getInt(operation.getColumnIndexOrThrow(DbAdapter.KEY_ID_PRIORITA)));
				mSpinnerPrior.setSelection(position);
			
			
		}
		else {
			selectSpinner(MainActivity.Idcategoria);
			updateDisplay();
		}
			
	}
	

	
	private void saveState() {
		if (!isDeleting){
			String data = mDateDisplay.getText().toString();
			
			
			try {
				long d = df.parse(data).getTime();


				String description=mDescription.getText().toString();
				String categoria=(String)mSpinner.getSelectedItem();
				int idcategoria=new Integer(categoria.split("-")[0]);
				String priorita=(String)mSpinnerPrior.getSelectedItem();
				int idpriorita=new Integer(priorita.split("-")[0]);

				if(description.equals("")) return; 
				
				if (mRowId == null) {
					long id = mDbHelper.createAzione(description, d, idcategoria, idpriorita); 
					if (id > 0) {
						mRowId = id;
					}

				} else {
					mDbHelper.updateAzione(mRowId, description, d, idcategoria, idpriorita);
				}

				MainActivity.setProprieta(idcategoria, idpriorita);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				Log.getStackTraceString(e);
				
			}
		}
		isDeleting=false;
	}
	
	private void createButtons(){
		Button deleteButton = (Button) findViewById(R.id.delete);
		Button saveButton = (Button) findViewById(R.id.save);
		Button mPickDate = (Button) findViewById(R.id.pickDate);

		
		saveButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				addDb();
			}

		});
		deleteButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				deleteFromDb();
			}

		});	

         mPickDate.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
                 showDialogNew(DATE_DIALOG_ID);
             }
         });
	}
	
	
	
	
	
	private void deleteFromDb(){
		if (mRowId==null)
			showDialogNew(DELETE_ID);
		else{	
			isDeleting=true;
			mDbHelper.deleteAzione(mRowId);
			setResult(RESULT_OK);
			finish();
		}
	}
	
	private void addDb(){
		
			setResult(RESULT_OK);
			finish();
		
	}
	
	private void showDialogNew(int option,String...number){
		switch(option){
		case INSERT_ID:
			/*number[0]=(number[0].equals("") ? getString(R.string.string_empty) : number[0]);
			new AlertDialog.Builder(TransactionEdit.this)
			.setMessage(number[0]+" "+getString(R.string.is_not_number))
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

				}})
				.show();*/
			break;
		case DELETE_ID:
			new AlertDialog.Builder(TransactionEdit.this)
			.setMessage(R.string.no_deleting)
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

				}})
				.show();
			break;
		case DATE_DIALOG_ID:
	        new DatePickerDialog(this,
	                    mDateSetListener,
	                    mYear, mMonth, mDay).show();
	        break;
	    //return null;

		
		}
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(DbAdapter.KEY_ROWID, mRowId);
	}
	@Override
	protected void onPause() {
		super.onPause();
		saveState();
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDbHelper.close();
	}

	@Override
	protected void onResume() {
		super.onResume();
		populateFields();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
       
        menu.add(0, INSERT_ID, 0, R.string.save).setIcon(R.drawable.db_add);
        menu.add(0, DELETE_ID, 0, R.string.delete).setIcon(R.drawable.db_remove);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        
        case INSERT_ID:
        	addDb();
			break;
        case DELETE_ID:
        	deleteFromDb();
			break;
        }
        
        return super.onMenuItemSelected(featureId, item);
    }

 // updates the date we display in the TextView
    private void updateDisplay() {
    	Calendar calendar = Calendar.getInstance();
    	calendar.set(mYear, mMonth, mDay);
    	  					
        mDateDisplay.setText(df.format(calendar.getTime()));
       

    }
    private ArrayAdapter <CharSequence>getAdapter(boolean isCategory){
    	Integer id=0;
    	String categoria;
    	ArrayAdapter <CharSequence>adapter =new ArrayAdapter<CharSequence>(
    			this.getApplicationContext(), android.R.layout.simple_spinner_item);
    	adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
    	if (isCategory){
    		Cursor cursor=mDbHelper.fetchAllCategories();
        	startManagingCursor(cursor);
        	adapter.clear();
        	cursor.moveToNext();
        	while (cursor.moveToNext()){
        		id=cursor.getInt(cursor.getColumnIndexOrThrow(DbAdapter.KEY_ROWID));
        		categoria=cursor.getString(cursor.getColumnIndexOrThrow(DbAdapter.KEY_CATEGORIA));
        		adapter.add(id+"-"+categoria);
        		
        		
        	}
        	cursor.close();
    	} else {
    		Cursor cursor=mDbHelper.fetchAllPriorita();
        	startManagingCursor(cursor);
        	adapter.clear();
        	cursor.moveToNext();
        	while (cursor.moveToNext()){
        		id=cursor.getInt(cursor.getColumnIndexOrThrow(DbAdapter.KEY_ROWID));
        		categoria=cursor.getString(cursor.getColumnIndexOrThrow(DbAdapter.KEY_PRIORITA));
        		adapter.add(id+"-"+categoria);
        		listPrior.add(id);
        		
        	}
        	cursor.close();
    	}	
    	return adapter;
    }
   
    
 private void selectSpinner(int idcategoria){
		String item=null;
		for (int i=0;i<mSpinner.getCount();i++){
			item=(String)mSpinner.getItemAtPosition(i);
			if (item.contains(String.valueOf(idcategoria))){
				mSpinner.setSelection(i);
				break;
			}
				
		}
		 
		
	 }
    	

   
}
