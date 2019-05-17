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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ListTransaction extends ListActivity{
	private static final int ACTIVITY_EDIT=1;
	private static class Row {
        private String azione;
        private Long data;
        private int IDpriorita;
        private Long ID;
        private int IDcategoria;
        
    }
	private static List<Row> listOperation;
	private static Map<Integer,String> categories= new HashMap<Integer,String>(); 
	private static Map<Integer,String> priorita= new HashMap<Integer,String>(); 
	
	
	private  DbAdapter mDbHelper;
	private static ListTransaction listActivity;
	private static  boolean isBegin=true;
	final DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);    					
     
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		listActivity=this;
		mDbHelper = new DbAdapter(this);
		mDbHelper.open();
		createMaps();
		//mDbHelper.deleteAllOperations();
        //mDbHelper.deleteDatabase();
        //fillData();
		
		
		
		
	}
	public static void aggiornaListOperazioni(int IdCategoria,int priorita){
		if (isBegin){
			listActivity.setListAdapter( listActivity.new EfficientAdapter(listActivity,listOperation=listActivity.getData(listOperation,IdCategoria,priorita)));
			isBegin=false;
			MainActivity.setCurrentTab(1);
			MainActivity.setCurrentTab(2);
			MainActivity.setCurrentTab(3);
			MainActivity.setCurrentTab(0);
		}else {
			listActivity.setListAdapter( listActivity.new EfficientAdapter(listActivity,listOperation=listActivity.getData(listOperation,IdCategoria,priorita)));
			
		}
		
		
	}
	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Row r;
        Intent i = new Intent(this, TransactionEdit.class);
        r =listOperation.get(position);
       
    	if(position==0 && !r.azione.equals(getString(R.string.no_transaction)))
        {
	    	i.putExtra(DbAdapter.KEY_ROWID, r.ID);
	        startActivityForResult(i, ACTIVITY_EDIT);
        }
    	if(position>0){
    		i.putExtra(DbAdapter.KEY_ROWID, r.ID);
	        startActivityForResult(i, ACTIVITY_EDIT);
    	}
        
    }
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, 
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
       
        MainActivity.setCurrentTab(0);
        MainActivity.selectSpinners();
        listActivity.setListAdapter( listActivity.new  EfficientAdapter(listActivity,listOperation=getData(listOperation,MainActivity.Idcategoria,MainActivity.Idpriorita)));
        
    }
	
	private  class EfficientAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private List<Row> DATA;
        private Context context;
       
        public EfficientAdapter(Context context,List<Row>DATA) {
            // Cache the LayoutInflate to avoid asking for a new one each time.
            mInflater = LayoutInflater.from(context);
            // Icons bound to the rows.
            this.context=context;
            this.DATA=DATA;
        }

        /**
         * The number of items in the list is determined by the number of speeches
         * in our array.
         *
         * @see android.widget.ListAdapter#getCount()
         */
        public int getCount() {
        	 return DATA.size();
        }

        /**
         * Since the data comes from an array, just returning the index is
         * sufficent to get at the data. If we were using a more complex data
         * structure, we would return whatever object represents one row in the
         * list.
         *
         * @see android.widget.ListAdapter#getItem(int)
         */
        public Object getItem(int position) {
            return position;
        }

        /**
         * Use the array index as a unique id.
         *
         * @see android.widget.ListAdapter#getItemId(int)
         */
        public long getItemId(int position) {
            return position;
        }

        /**
         * Make a view to hold each row.
         *
         * @see android.widget.ListAdapter#getView(int, android.view.View,
         *      android.view.ViewGroup)
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            // A ViewHolder keeps references to children views to avoid unneccessary calls
            // to findViewById() on each row.
        	/*if (extras!=null && extras.getBoolean("tab1") && position==0){
        		Log.v("position",Integer.toString(position));
        		return createSpinner();
        		
        	}*/
            ViewHolder holder;
            
            
            // When convertView is not null, we can reuse it directly, there is no need
            // to reinflate it. We only inflate a new View when the convertView supplied
            // by ListView is null.
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.row, null);
                //convertView.setBackgroundResource(android.R.color.background_light);

                // Creates a ViewHolder and store references to the two children views
                // we want to bind data to.
                holder = new ViewHolder();
                
                holder.data = (TextView) convertView.findViewById(R.id.data);
                holder.categoria=(TextView) convertView.findViewById(R.id.categoria);
                holder.azione = (TextView) convertView.findViewById(R.id.azione);
               
                convertView.setTag(holder);
            } else {
                // Get the ViewHolder back to get fast access to the TextView
                // and the ImageView.
                holder = (ViewHolder) convertView.getTag();
            }

            // Bind the data efficiently with the holder.
            Row r = DATA.get(position);
            int nums=R.string.no_transaction;
            if(position==0 && r.azione.equals(getString(nums))){
            	holder.categoria.setText(nums);
            	holder.data.setText("");
            	holder.azione.setText("");
            	
            }else{
            	
	            try{
	            	
		            holder.data.setText(df.format(new Date(r.data)));
		            holder.categoria.setText("Cat:"+categories.get(r.IDcategoria)+" P:"+priorita.get(r.IDpriorita));
		            holder.azione.setText(r.azione);
		            //holder.priorita.setText(priorita.get(r.IDpriorita));
	            } catch(Exception e){
	            	Log.getStackTraceString(e); }
            }
            return convertView;
        }
        private  class ViewHolder {
            TextView categoria;
            TextView data;
            TextView azione;
            
           
        }
        
    }
	
	
	private  List<Row> getData(List<Row>list,int IDcategoria,int Idpriorita){
		list= new ArrayList<Row>();
		Row r=null; 
		fillData(r,list,IDcategoria,Idpriorita);
		return list;
	}
	
	private void fillData(Row r,List<Row> list,int Idcategoria,int Idpriorita) {
		//Log.v("LIST TRANSACTION", Idcategoria +"-"+Idpriorita);
		Cursor operations = Idcategoria==1 & Idpriorita==1?mDbHelper.fetchAllAzioni():mDbHelper.fetchAllAzioni(Idcategoria,Idpriorita);
		listActivity.startManagingCursor(operations);
		if (!operations.moveToNext()){
			r=new Row();
			r.azione=listActivity.getString(R.string.no_transaction);
			list.add(r);
		}
		else {
			
			do{
				r=new Row();
				r.ID=operations.getLong(
						operations.getColumnIndexOrThrow(DbAdapter.KEY_ROWID));
				r.IDpriorita=operations.getInt(
						operations.getColumnIndexOrThrow(DbAdapter.KEY_ID_PRIORITA));
				r.data=operations.getLong(
						operations.getColumnIndexOrThrow(DbAdapter.KEY_DATA));
				r.azione=operations.getString(
						operations.getColumnIndexOrThrow(DbAdapter.KEY_AZIONE));
				r.IDcategoria=operations.getInt(
						operations.getColumnIndexOrThrow(DbAdapter.KEY_ID_CATEGORIA));
				list.add(r);
			
				
			}while(operations.moveToNext());
			
			
		}
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
       
        menu.add(0, TransactionEdit.INSERT_ID, 0, R.string.create_azione).setIcon(R.drawable.db_add);
        //menu.add(0, TransactionEdit.DELETE_ID, 0, R.string.delete).setIcon(R.drawable.db_remove);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        
        case TransactionEdit.INSERT_ID:
        	Intent i = new Intent(this, TransactionEdit.class);
        	startActivityForResult(i, ACTIVITY_EDIT);
			break;
        
        }
        
        return super.onMenuItemSelected(featureId, item);
    }
    
    public static  void createMaps(){
    	Cursor cursor=listActivity.mDbHelper.fetchAllCategories();
    	listActivity.startManagingCursor(cursor);
    	while (cursor.moveToNext())
    		categories.put(
    				cursor.getInt(cursor.getColumnIndexOrThrow(DbAdapter.KEY_ROWID)),
    				cursor.getString(cursor.getColumnIndexOrThrow(DbAdapter.KEY_CATEGORIA)));
    	
    	cursor=listActivity.mDbHelper.fetchAllPriorita();
    	listActivity.startManagingCursor(cursor);
    	while (cursor.moveToNext())
    		priorita.put(
    				cursor.getInt(cursor.getColumnIndexOrThrow(DbAdapter.KEY_ROWID)),
    				cursor.getString(cursor.getColumnIndexOrThrow(DbAdapter.KEY_PRIORITA)));
    	
    	
    	
    	
    	
    }
	
    
}
