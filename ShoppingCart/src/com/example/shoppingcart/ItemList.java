package com.example.shoppingcart;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Class for Item List
 * @author Bertram Bishop
 */
public class ItemList 
{
	private SQLiteDatabase database;

	/**
	 * Method handles adding the items to the database
	 * @param myDbHelper database helper
	 * @param itemName item name
	 * @param shopperLits shopper List
	 */
	public void addList(DataBaseHelper myDbHelper, String itemName)
	{    		
		database = myDbHelper.getWritableDatabase();	
		myDbHelper.openDataBase();

		ContentValues values = new ContentValues();
		values.put("itemName", itemName); 
		database.insert("ShoppingList", null, values);

		myDbHelper.close(); 		
	}


	/**
	 * Method handles adding items to database
	 * @param myDbHelper database helper
	 * @param itemName item name
	 */
	public void addItems(DataBaseHelper myDbHelper, String itemName)
	{
		database = myDbHelper.getWritableDatabase();	
		myDbHelper.openDataBase();

		database.execSQL("INSERT INTO ShoppingList (itemName) " +
						 "VALUES("+ "'" + itemName + "'" + ");"); 
		database.execSQL("INSERT INTO SortList (itemName) " +
				 "VALUES("+ "'" + itemName + "'" + ");"); 


		myDbHelper.close();		
	}

	
	/**
	 * Method checks database for duplicate items
	 * @param myDbHelper database helper
	 * @param itemName item name
	 * @return whether or not duplicate stores exist
	 */
	public boolean hasDuplicateItems(DataBaseHelper myDbHelper, String itemName)
	
	{
		boolean hasDuplicateStores;
		Cursor cursor;
		
		SQLiteDatabase database = myDbHelper.getWritableDatabase();		 
		myDbHelper.openDataBase();	

		cursor = database.rawQuery("SELECT itemName FROM ShoppingList " +
										  "WHERE itemName = " + "'" + itemName + "';", null); 	
		cursor = database.rawQuery("SELECT itemName FROM SortList " +
				  						  "WHERE itemName = " + "'" + itemName + "';", null); 	

		
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