package com.example.cosedafare;

import java.util.Locale;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TabHost;

public class MainActivity extends TabActivity  {
	 private static TabHost mTabHost;
	 private static Spinner spinnerCat,spinnerPrio;
	 private static ArrayAdapter <CharSequence>adapter,adapterPr;
	 private static MainActivity tabActivity;
	 private static DbAdapter mDbHelper;
	 public static int Idcategoria=2,Idpriorita;
	
	
	 public static int CATEGORIES_ALL;
	 
	 public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		tabActivity=this;
		setContentView(R.layout.activity_main);
		mDbHelper = new DbAdapter(this);
		mDbHelper.open();
		//mDbHelper.deleteAllCategories();
		fillDbCategories();
		//mDbHelper.deleteAllOperations();

		Button licenceButton = (Button) findViewById(R.id.licence);
		licenceButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				new AlertDialog.Builder(MainActivity.this)
				.setMessage(R.string.licence)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

					}})
					.show();
			}

		});



		spinnerCat = (Spinner) this.findViewById(R.id.spinner);
		spinnerPrio = (Spinner) this.findViewById(R.id.spinnerPrior);
		adapter =new ArrayAdapter<CharSequence>(
				this.getApplicationContext(), android.R.layout.simple_spinner_item);
		adapterPr =new ArrayAdapter<CharSequence>(
				this.getApplicationContext(), android.R.layout.simple_spinner_item);
		
		fillAdapter();
		adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		adapterPr.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		
		spinnerCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

			public void  onItemSelected  (AdapterView<?> parent, View view, int position, long id){
				aggiornaLista();
				mTabHost.setCurrentTab(0);
			}

			

			public void  onNothingSelected  (AdapterView<?> parent){

			}
		});

		spinnerPrio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

			public void  onItemSelected  (AdapterView<?> parent, View view, int position, long id){
				aggiornaLista();
				mTabHost.setCurrentTab(0);
			}

			

			public void  onNothingSelected  (AdapterView<?> parent){

			}
		});

		
		spinnerCat.setAdapter(adapter);
		spinnerPrio.setAdapter(adapterPr);
		CATEGORIES_ALL=new Integer(((String)spinnerCat.getItemAtPosition(0)).split("-")[0]);

		

		mTabHost = getTabHost();
		
		Intent intent =new Intent(this,ListTransaction.class);
		//intent.putExtra("tab1",true );
		mTabHost.addTab(
				mTabHost.newTabSpec("tab1")
				.setIndicator(getString(R.string.tab1),getResources().getDrawable(R.drawable.calculator))
				.setContent(intent)
		);



		intent =new Intent(this,CategoryEdit.class);
		//intent.putExtra("tab1",true );
		mTabHost.addTab(
				mTabHost.newTabSpec("tab4")
				.setIndicator(getString(R.string.category),getResources().getDrawable(R.drawable.folder))
				.setContent(intent)
		);
		mTabHost.getTabWidget().getChildAt(0).setBackgroundResource(android.R.color.darker_gray);
		mTabHost.getTabWidget().getChildAt(1).setBackgroundResource(android.R.color.darker_gray);
		spinnerCat.setSelection(0);
    }
	 
	 private void aggiornaLista() {
			String categoria=(String)spinnerCat.getSelectedItem();
			String priorita=(String)spinnerPrio.getSelectedItem();
			int idcategoria=new Integer(categoria.split("-")[0]);
			int idpriorita =new Integer(priorita.split("-")[0]);
			ListTransaction.aggiornaListOperazioni(idcategoria, idpriorita);
		}
	 
	 public static void selectSpinners(){
		String item=null;
		for (int i=0;i<spinnerCat.getCount();i++){
			item=(String)spinnerCat.getItemAtPosition(i);
			if (item.contains(String.valueOf(Idcategoria))){
				spinnerCat.setSelection(i);
				break;
			}
				
		}
		 
		for (int i=0;i<spinnerPrio.getCount();i++){
			item=(String)spinnerPrio.getItemAtPosition(i);
			if (item.contains(String.valueOf(Idpriorita))){
				spinnerPrio.setSelection(i);
				break;
			}
				
		}
	 }
	
	 public static void setProprieta(int Idcat,int Idprio){
		 Idcategoria=Idcat;
		 Idpriorita=Idprio;
	 }
	 
    public static void setCurrentTab(int tab){
    	mTabHost.setCurrentTab(tab);
    }
   
    public static int  getCategoria(){
    	return spinnerCat.getSelectedItemPosition();
    }
    
    public static CharSequence getSpinnerItem(int position){
    	return adapter.getItem(position);
    }
    
    public static int getCountCategories(){
    	return adapter.getCount();
    }
   
    
   
      
    
        
    public static void fillAdapter(){
    	Cursor cursor=mDbHelper.fetchAllCategories();
    	tabActivity.startManagingCursor(cursor);
    	adapter.clear();
    	while (cursor.moveToNext())
    		adapter.add(
    				cursor.getInt(cursor.getColumnIndexOrThrow(DbAdapter.KEY_ROWID))+"-"+
    				cursor.getString(cursor.getColumnIndexOrThrow(DbAdapter.KEY_CATEGORIA)));
    	cursor=mDbHelper.fetchAllPriorita();
    	tabActivity.startManagingCursor(cursor);
    	adapterPr.clear();
    	while (cursor.moveToNext())
    		adapterPr.add(
    				cursor.getInt(cursor.getColumnIndexOrThrow(DbAdapter.KEY_ROWID))+"-"+
    				cursor.getString(cursor.getColumnIndexOrThrow(DbAdapter.KEY_PRIORITA)));
    }
    
    private void fillDbCategories(){
    	Cursor cursor =mDbHelper.fetchAllCategories();
    	startManagingCursor(cursor);
    	if (!cursor.moveToNext()){
		    	int iLocale =0;
		    	final Locale locale =Locale.getDefault();
		    	final String [][] categories={
		    			{
		    				"All","Food","Home","Phone","Light","Petrol","Car"		
		    			},
		    			{
		    				"Tutte","Salute","Casa","Pensione","Luce","Pulizie","Condominio","Spesa"		
		    			},
		    			{
		    				"Tous","Manger","Maiso","Tel","Lumir","Auto"		
		    			}
		    	};
		    	if (locale.getLanguage().equals("it"))
		    		iLocale=1;
		    	else if (locale.getLanguage().equals("fr"))
		    		iLocale=2;
		    	
		    	for (String s:categories[iLocale])
		    		mDbHelper.createCategory(s);
    	}
    	cursor=mDbHelper.fetchAllPriorita();
    	startManagingCursor(cursor);
    	if (!cursor.moveToNext()){
		    	int iLocale =0;
		    	final Locale locale =Locale.getDefault();
		    	final String [][] priorita={
		    			{
		    				"All","None","Low","Medium","High"		
		    			},
		    			{
		    				"Tutte","Nessuna","Bassa","Media","Alta"		
		    			},
		    			{
		    				"Tous","Manger","Maiso","Tel","Lumir","Auto"		
		    			}
		    	};
		    	if (locale.getLanguage().equals("it"))
		    		iLocale=1;
		    	else if (locale.getLanguage().equals("fr"))
		    		iLocale=2;
		    	
		    	for (String s:priorita[iLocale])
		    		mDbHelper.createPriorita(s);
    	}
    }
    
	
}