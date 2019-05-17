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

import android.content.ContentValues;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Simple notes database access helper class. Defines the basic CRUD operations
 * for the notepad example, and gives the ability to list all notes as well as
 * retrieve or modify a specific note.
 * 
 * This has been improved from the first version of this tutorial through the
 * addition of better error handling and also using returning a Cursor instead
 * of using a collection of inner classes (which is less scalable and not
 * recommended).
 */
public class DbAdapter {

    public static final String KEY_ID_CATEGORIA = "Idcategoria";
    public static final String KEY_ID_PRIORITA = "Idpriorita";
    public static final String KEY_CATEGORIA = "categoria";
    public static final String KEY_PRIORITA = "priorita";
    public static final String KEY_AZIONE = "azione";
	public static final String KEY_DATA = "data";
    public static final String KEY_ROWID = "_id";
    
    
    private static final String TAG = "CoseDafareDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    
    /**
     * Database creation sql statement
     */
    private static final String TABLE_AZIONI =
    "create table azioni " +
    "(_id integer primary key autoincrement, " +
    "azione text," +
    "data bigint," +
    "Idpriorita integer not null," +
    "Idcategoria integer not null" +
    ");"; 
    private static final String TABLE_CATEGORIES=
    "create table categories " +
    "(_id integer primary key autoincrement, " +
    "categoria text not null" +
    ");";
    
    private static final String TABLE_PRIORITA=
    	    "create table priorities " +
    	    "(_id integer primary key autoincrement, " +
    	    "priorita text not null" +
    	    ");";
    	     
    

    private static final String DATABASE_NAME = "DB";
    private static final String DATABASE_TABLE_AZIONI = "azioni";
    private static final String DATABASE_TABLE_CATEGORIES = "categories";
    private static final String DATABASE_TABLE_PRIORITA= "priorities";
    
    private static final int DATABASE_VERSION =3 ;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        	
        	db.execSQL(TABLE_AZIONI);
            db.execSQL(TABLE_CATEGORIES);
            db.execSQL(TABLE_PRIORITA);
            
            
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_AZIONI);
            db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_CATEGORIES);
            db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_PRIORITA);
            
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public DbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public DbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    
    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new note using the title and body provided. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     * 
     * @param title the title of the note
     * @param body the body of the note
     * @return rowId or -1 if failed
     */
    public long createAzione( 
    		String azione,
    		long data,
    		int Idcategoria,int Idpriorita
    		) 
    {
    	
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_AZIONE, azione);
        initialValues.put(KEY_DATA, data);
        initialValues.put(KEY_ID_PRIORITA, Idpriorita);
        initialValues.put(KEY_ID_CATEGORIA, Idcategoria);
        return mDb.insert(DATABASE_TABLE_AZIONI, null, initialValues);
    }
    
    public long createCategory(	String description) 
    {
    	ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_CATEGORIA, description);
        return mDb.insert(DATABASE_TABLE_CATEGORIES, null, initialValues);
    }
    
    public long createPriorita(	String description) 
    {
    	ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_PRIORITA, description);
        return mDb.insert(DATABASE_TABLE_PRIORITA, null, initialValues);
    }
    
    /**
     * Delete the note with the given rowId
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteAzione(long rowId) {

        return mDb.delete(DATABASE_TABLE_AZIONI, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    public boolean deleteCategory(String category) {

        return mDb.delete(DATABASE_TABLE_CATEGORIES, KEY_CATEGORIA + "='" + category+"'", null) > 0;
    }

    public boolean deletePriorita(String priorita) {

        return mDb.delete(DATABASE_TABLE_PRIORITA, KEY_PRIORITA + "='" + priorita+"'", null) > 0;
    }

    
    /**
     * Return a Cursor over the list of all notes in the database
     * 
     * @return Cursor over all notes
     */
    public Cursor fetchAllAzioni(int Idcategoria,int IDpriorita) {
    	//Log.v("DBAdapter:IDCategoria",new Integer(Idcategoria).toString());
    	if (Idcategoria==MainActivity.CATEGORIES_ALL){
    		return mDb.query(DATABASE_TABLE_AZIONI, 
            		new String[] {KEY_ROWID,KEY_AZIONE,KEY_DATA,
            					  KEY_ID_PRIORITA,	
            					  KEY_ID_CATEGORIA
            					  }, KEY_ID_PRIORITA+"="+IDpriorita, null, null, null, KEY_DATA+","+KEY_ID_PRIORITA+" DESC,"+KEY_ID_CATEGORIA );	
    		
    	} else if (IDpriorita==MainActivity.CATEGORIES_ALL){
    		return mDb.query(DATABASE_TABLE_AZIONI, 
            		new String[] {KEY_ROWID,KEY_AZIONE,KEY_DATA,
    								KEY_ID_PRIORITA,	
            					  KEY_ID_CATEGORIA
            					  }, KEY_ID_CATEGORIA+"="+Idcategoria, null, null, null,  KEY_DATA+","+KEY_ID_PRIORITA+" DESC,"+KEY_ID_CATEGORIA );	
    		
    	} 
    		
    		
    	
        return mDb.query(DATABASE_TABLE_AZIONI, 
        		new String[] {KEY_ROWID,KEY_AZIONE,KEY_DATA,
        		KEY_ID_PRIORITA,	
				  				KEY_ID_CATEGORIA  
				  			  }, KEY_ID_CATEGORIA + "=" + Idcategoria + " AND "+KEY_ID_PRIORITA+"="+IDpriorita, null, null, null,   KEY_DATA+","+KEY_ID_PRIORITA+" DESC,"+KEY_ID_CATEGORIA );	
    }
    
    public Cursor fetchAllAzioni() {
    	
    	return mDb.query(DATABASE_TABLE_AZIONI, 
            		new String[] {KEY_ROWID,KEY_AZIONE,KEY_DATA,
    			KEY_ID_PRIORITA,	
  									KEY_ID_CATEGORIA
            					  }, null, null, null, null,  KEY_DATA+","+KEY_ID_PRIORITA+" DESC,"+KEY_ID_CATEGORIA );	
        
    }
    
    public Cursor fetchAllCategories() {
    	
    	return mDb.query(DATABASE_TABLE_CATEGORIES, 
            		new String[] {KEY_ROWID,KEY_CATEGORIA,
            					  
            					  }, null, null, null, null, null);	
        
    }
    
    
    public Cursor fetchAllPriorita() {
    	
    	return mDb.query(DATABASE_TABLE_PRIORITA, 
            		new String[] {KEY_ROWID,KEY_PRIORITA,
            					  
            					  }, null, null, null, null, null);	
        
    }
    
    /**
     * Return a Cursor positioned at the note that matches the given rowId
     * 
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchAzione(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, DATABASE_TABLE_AZIONI, new String[] {
                		KEY_ROWID,KEY_AZIONE,
							KEY_DATA,KEY_ID_PRIORITA,	
							KEY_ID_CATEGORIA
					  }, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    
    public Cursor fetchCategoria(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, DATABASE_TABLE_CATEGORIES, new String[] {
                		KEY_ROWID,KEY_CATEGORIA
					  }, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        /*if (mCursor != null) {
            mCursor.moveToFirst();
        }*/
        return mCursor;

    }
    

    public String fetchPriorita(long rowId) throws SQLException {
    	
        Cursor mCursor =

                mDb.query(true, DATABASE_TABLE_PRIORITA, new String[] {
                		KEY_ROWID,KEY_PRIORITA
					  }, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
            return mCursor.getString(mCursor.getColumnIndexOrThrow(KEY_PRIORITA));
        } else
        return "Error";

    }

    
    /**
     * Update the note using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     * 
     * @param rowId id of note to update
     * @param title value to set note title to
     * @param body value to set note body to
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateAzione(long rowId, String azione,
    		long data,
    		int Idcategoria,int Idpriorita) 
    {
        ContentValues args = new ContentValues();
        args.put(KEY_AZIONE, azione);
        args.put(KEY_DATA, data);
        args.put(KEY_ID_PRIORITA, Idpriorita);
        args.put(KEY_ID_CATEGORIA, Idcategoria);
        return mDb.update(DATABASE_TABLE_AZIONI, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    
    
/*    public void deleteAllOperations() {

        Cursor cursor=mDb.query(DATABASE_TABLE, 
        		new String[] {KEY_ROWID,KEY_DESCRIPTION,
        						KEY_ROWID,KEY_DESCRIPTION,
        						KEY_DATA,KEY_SPESA,	
        						KEY_CATEGORIA
        					  }, null, null, null, null, null);
        Long rowId;
        while(cursor.moveToNext()){
        	rowId =cursor.getLong(
    				cursor.getColumnIndexOrThrow(KEY_ROWID));
        	mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) ;
        }
    }
    
    public void deleteAllCategories(){
    	Cursor cursor=mDb.query(DATABASE_TABLE_CATEGORIES, 
        		new String[] {KEY_ROWID,KEY_DESCRIPTION,
				  
		  }, null, null, null, null, null);
        Long rowId;
        while(cursor.moveToNext()){
        	rowId =cursor.getLong(
    				cursor.getColumnIndexOrThrow(KEY_ROWID));
        	mDb.delete(DATABASE_TABLE_CATEGORIES, KEY_ROWID + "=" + rowId, null) ;
        }
    }
    */
   
    
    
}
