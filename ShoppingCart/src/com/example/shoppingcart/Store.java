package com.example.shoppingcart;

import java.util.ArrayList;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Class for a store. Handles reading/writing to the database for a store.
 * @Author David Anderson
 */
public class Store 
{
	private SQLiteDatabase database;
	private SortList mySortList;

	/**
	 * Method handles adding the store to the database
	 * @param myDbHelper database helper
	 * @param storeName store name
	 * @param storeLocation store location
	 */
	public void addStore(DataBaseHelper myDbHelper, String storeName, String storeLocation)
	{    		
		database = myDbHelper.getWritableDatabase();	
		myDbHelper.openDataBase();

		ContentValues values = new ContentValues();
		values.put("storeName", storeName); 
		values.put("storeLocation", storeLocation);
		database.insert("StoreList", null, values);

		myDbHelper.close(); 		
	}


	/**
	 * Method handles removing the store from the database
	 * @param myDbHelper database helper
	 * @param storeName store name
	 * @param storeLocation store location
	 */
	public void deleteStore(DataBaseHelper myDbHelper, String storeName, String storeLocation)
	{
		database = myDbHelper.getWritableDatabase();

		myDbHelper.openDataBase();	
		
		
		
		database.execSQL("DELETE FROM StoreList " +
				         "WHERE storeName = " + "'" + storeName + "'" + "AND storeLocation = " + "'" + storeLocation + "';");
		
		database.execSQL("DELETE FROM StoreItemList " +
				         "WHERE storeName = " + "'" + storeName + "'" + "AND storeLocation = " + "'" + storeLocation + "';");

		myDbHelper.close();
	}


	/**
	 * Method handles renaming the store in the database
	 * @param myDbHelper database helper
	 * @param newStoreName new store name
	 * @param storeName old store name
	 * @param storeLocation store location
	 */
	public void renameStore(DataBaseHelper myDbHelper, String newStoreName, String storeName, String storeLocation)
	{	
		database = myDbHelper.getWritableDatabase();	
		myDbHelper.openDataBase();

		database.execSQL("UPDATE StoreList " +
				         "SET storeName = " + "'" + newStoreName + "'" + 
				         " WHERE storeName = " + "'" + storeName + "'" + " AND storeLocation = " + "'" + storeLocation + "';");
		
		database.execSQL("UPDATE StoreItemList " +
				         "SET storeName = " + "'" + newStoreName + "'" + 
				         " WHERE storeName = " + "'" + storeName + "'" + " AND storeLocation = " + "'" + storeLocation + "';");

		myDbHelper.close();
	}


	/**
	 * Method handles renaming the store in the database
	 * @param myDbHelper database helper
	 * @param storeName store name
	 * @param newStoreLocation new store location
	 * @param storeLocation old store location
	 */
	public void renameLocation(DataBaseHelper myDbHelper, String storeName, String newStoreLocation, String storeLocation)
	{
		database = myDbHelper.getWritableDatabase();	
		myDbHelper.openDataBase();

		database.execSQL("UPDATE StoreList " +
				         "SET storeLocation = " + "'" + newStoreLocation + "'" + 
						 " WHERE storeName = " + "'" + storeName + "'" + " AND storeLocation = " + "'" + storeLocation + "';");
		
		database.execSQL("UPDATE StoreItemList " +
						 "SET storeLocation = " + "'" + newStoreLocation + "'" + 
						 " WHERE storeName = " + "'" + storeName + "' " + "AND storeLocation = " + "'" + storeLocation + "';");	

		myDbHelper.close();
	}


	/**
	 * Method handles adding items to the stores database
	 * @param myDbHelper database helper
	 * @param storeName store name
	 * @param storeLocation store location	
	 * @param itemName item name
	 * @param itemLocation item location
	 */
	public void addItems(DataBaseHelper myDbHelper, String storeName, String storeLocation, String itemName, String itemLocation)
	{
		database = myDbHelper.getWritableDatabase();	
		myDbHelper.openDataBase();

		database.execSQL("INSERT INTO StoreItemList (storeName, storeLocation, itemName, itemLocation) " +
						 "VALUES("+ "'" + storeName + "'" + ", " + "'" + storeLocation + "'"  
						          + ", " + "'" + itemName + "'" + ", " + "'" + itemLocation + "'" + ");");

		myDbHelper.close();		
	}

	/**
	 * Method handles adding items to the stores database
	 * @param myDbHelper database helper
	 * @param storeName store name
	 * @param storeLocation store location	
	 * @param itemName item name
	 * @param itemLocation item location
	 */
	public void updateItem(DataBaseHelper myDbHelper, String storeName, String storeLocation, 
							String oldItemName, String oldItemLocation, String newItemName, String newItemLocation)
	{
		database = myDbHelper.getWritableDatabase();	
		myDbHelper.openDataBase();

		database.execSQL("UPDATE StoreItemList " +
						 "SET itemName = " + "'" + newItemName + "'" + ", " + "itemLocation = " + "'" + newItemLocation + "'" +
						 " WHERE " +
						 	"storeName = " + "'" + storeName + "'" + " AND storeLocation = " + "'" + storeLocation + "'" +
						    " AND itemName = " + "'" + oldItemName + "'" + "AND itemLocation = " + "'" + oldItemLocation + "';");

		myDbHelper.close();		
	}

	/**
	 * Method checks database for duplicate stores
	 * @param myDbHelper database helper
	 * @param storeName store name
	 * @param storeLocation store location
	 * @return whether or not duplicate stores exist
	 */
	public boolean hasDuplicateStores(DataBaseHelper myDbHelper, String storeName, String storeLocation)
	{
		boolean hasDuplicateStores;
		
		SQLiteDatabase database = myDbHelper.getWritableDatabase();		 
		myDbHelper.openDataBase();	

		Cursor cursor = database.rawQuery("SELECT storeName FROM StoreList " +
										  "WHERE storeName LIKE " + "'" + storeName + "'" + " AND storeLocation LIKE " + "'" + storeLocation + "';", null); 					
	   			
		// If 1 or more results are in cursor, duplicates exist
		if(cursor.getCount() >= 1)				
			hasDuplicateStores = true;     
			
		else							
			hasDuplicateStores = false;
				
		cursor.close();
		myDbHelper.close();	
		
		return hasDuplicateStores;
	}
	
	
	/**
	 * Method checks database for duplicate items
	 * @param MyDbHelper database helper
	 * @param storeName store name
	 * @param storeLocation store location
	 * @param itemName item name
	 * @return whether or not duplicate items exist for specified store
	 */
	public boolean hasDuplicateStoreItems(DataBaseHelper myDbHelper, String storeName, String storeLocation, String itemName)
	{
		boolean hasDuplicateStoreItems;
		
		SQLiteDatabase database = myDbHelper.getWritableDatabase();		 
		myDbHelper.openDataBase();	

		Cursor cursor = database.rawQuery("SELECT itemName FROM StoreItemList " +
										  "WHERE storeName LIKE " + "'" + storeName + "' " + "AND storeLocation LIKE " + "'" + storeLocation + "' " + 
										  "AND itemName LIKE " + "'" + itemName + "';", null); 					
	   		
		// If 1 or more results are in cursor, duplicate items exist
		if(cursor.getCount() >= 1)		
			hasDuplicateStoreItems = true;
			
		else
			hasDuplicateStoreItems = false;
		
		cursor.close();
		myDbHelper.close();	
		
		return hasDuplicateStoreItems;
			
	}
	

	/**
	 * Gets a list of stores currently in the database
	 * @param myDbHelper database helper
	 * @return array of all of the stores in database
	 */
	public ArrayList<String> getListOfStores(DataBaseHelper myDbHelper)
	{	
		ArrayList<String> listOfStores = new ArrayList<String>();	

		database = myDbHelper.getWritableDatabase();	
		myDbHelper.openDataBase();	

		Cursor cursor = database.rawQuery("SELECT storeName, storeLocation FROM StoreList " +
										  "ORDER BY storeName, storeLocation;", null); 					
		cursor.moveToFirst();

		while (cursor.isAfterLast() == false) 
		{					
			String storeName = cursor.getString(cursor.getColumnIndex("storeName"));
			String storeLocation = cursor.getString(cursor.getColumnIndex("storeLocation"));
			listOfStores.add(storeName + "\n" + "(" + storeLocation + ")");	          
			cursor.moveToNext();
		}	 

		cursor.close();	
		myDbHelper.close();

		return listOfStores;	
	}
	
	
/**
	 * Get list of items from database 
	 * @param myDbHelper database helper
	 * @param storeName store name
	 * @param storeLocation store location
	 * @return array of items in a store
	 */
	public ArrayList<String> getListOfItems(DataBaseHelper myDbHelper, String storeName, String storeLocation) 
	{
		ArrayList<String> listOfItems = new ArrayList<String>();	

		database = myDbHelper.getWritableDatabase();	
		myDbHelper.openDataBase();	

		Cursor cursor = database.rawQuery("SELECT * FROM StoreItemList " +
										  "WHERE storeName = " + "'" + storeName + "' " + "AND storeLocation = " + "'" + storeLocation + "'" +
										   "ORDER BY itemLocation, itemName;", null); 					
		cursor.moveToFirst();
		
		while (cursor.isAfterLast() == false) 
		{					
			String itemName = cursor.getString(cursor.getColumnIndex("itemName"));
			String itemLocation = cursor.getString(cursor.getColumnIndex("itemLocation"));
			listOfItems.add(itemName + "\n" + "(" + itemLocation + ")");	          
			cursor.moveToNext();
		}	 

		cursor.close();	
		myDbHelper.close();

		return listOfItems;
	}
	
/**
	 * Method returns all of the items in the database, used for finding item locations
	 * @param myDbHelper database helper
	 * @return arraylist of item names with their locations
	 */
	public ArrayList<String> getAllItems(DataBaseHelper myDbHelper) 
	{
		ArrayList<String> listOfItems = new ArrayList<String>();	

		database = myDbHelper.getWritableDatabase();	
		myDbHelper.openDataBase();	

		Cursor storeCursor = database.rawQuery("SELECT * FROM StoreItemList;", null); 					
		storeCursor.moveToFirst();
					
		while (storeCursor.isAfterLast() == false) 
		{					
			String itemName = storeCursor.getString(storeCursor.getColumnIndex("itemName"));
			String itemLocation = storeCursor.getString(storeCursor.getColumnIndex("itemLocation"));
			listOfItems.add(itemName + " is located at " + itemLocation);	          
			storeCursor.moveToNext();
		}		
		
		if (listOfItems.size() == 0)	
			listOfItems.add("Item could not be found");	
	
		storeCursor.close();
		myDbHelper.close();

		return listOfItems;
	}
}
