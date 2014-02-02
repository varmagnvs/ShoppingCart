package com.example.shoppingcart;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * MainList Class
 * @author Bertram Bishop
 */
public class MainList 
{
	private SQLiteDatabase database;

	
	/**
	 * Method handles adding the items to the database
	 * @param myDbHelper database helper
	 * @param itemName item name
	 * @param shopperLits shopper List
	 */
	public void addList(DataBaseHelper myDbHelper, String mainItemList)
	{    		
		database = myDbHelper.getWritableDatabase();	
		myDbHelper.openDataBase();

		ContentValues values = new ContentValues();
		values.put("mainItemList", mainItemList); 
		database.insert("MainShoppingList", null, values);

		myDbHelper.close(); 		
	}

	
	/**
	 * Method handles adding items to database
	 * @param myDbHelper database helper
	 * @param itemName item name
	 */
	public void addItems(DataBaseHelper myDbHelper, String mainItemList)
	{
		database = myDbHelper.getWritableDatabase();	
		myDbHelper.openDataBase();

		database.execSQL("INSERT INTO MainShoppingList (mainItemList) " +
						 "VALUES("+ "'" + mainItemList + "'" + ");"); 
		

		myDbHelper.close();		
	}
	
	
	/**
	 * Method checks database for duplicate items
	 * @param myDbHelper database helper
	 * @param itemName item name
	 * @return whether or not duplicate stores exist
	 */
	public boolean hasDuplicateItems(DataBaseHelper myDbHelper, String mainItemList)
	
	{
		boolean hasDuplicateStores;
		Cursor cursor;
		
		SQLiteDatabase database = myDbHelper.getWritableDatabase();		 
		myDbHelper.openDataBase();	

		cursor = database.rawQuery("SELECT (mainItemList) FROM MainShoppingList " +
										  "WHERE mainItemList = " +  "'" + mainItemList + "';", null); 	
				
		// If 1 or more results are in cursor, duplicates exist
		if(cursor.getCount() >= 1)				
			hasDuplicateStores = true;     
			
		else							
			hasDuplicateStores = false;
				
		cursor.close();
		myDbHelper.close();	
		
		return hasDuplicateStores;
	}
}