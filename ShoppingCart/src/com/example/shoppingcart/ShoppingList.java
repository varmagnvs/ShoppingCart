package com.example.shoppingcart;

import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 *  Class for a shopping list. 
 *  Handles reading/writing to the database for a shopping list.
 *  @Author David Anderson, Marin Rangelov 
 */
public class ShoppingList 
{
	private SQLiteDatabase database;


	/**
	 * Method handles deleting a shopping list
	 * @param myDbHelper database helper
	 * @param listName list name to delete
	 */
	public void deleteList(DataBaseHelper myDbHelper) 
	{
		database = myDbHelper.getWritableDatabase();
		myDbHelper.openDataBase();

		database.delete("ShoppingList", null, null);
		database.delete("ShoppingListSorted", null, null);
		myDbHelper.close();
	}	


	/**
	 * Adds items from a shopping list to a store
	 * @param myDbHelper datebase helper
	 * @param storeName store name
	 * @param storeLocation store location
	 * @param listName list name
	 */
	public void addToStore(DataBaseHelper myDbHelper, String storeName, String storeLocation)
	{
		database = myDbHelper.getWritableDatabase();
		myDbHelper.openDataBase();

		Cursor shoppingCursor = database.rawQuery("SELECT itemName FROM ShoppingList;", null); 			
		shoppingCursor.moveToFirst();

		/**
		 * Below code handles adding items from the shopping list to the store.
		 * Only shopping items that do not already exist in the store are added to the store
		 */
		while (shoppingCursor.isAfterLast() == false) 
		{			
			String itemName = shoppingCursor.getString(shoppingCursor.getColumnIndex("itemName"));
			Cursor storeCursor = database.rawQuery("SELECT itemName FROM StoreItemList " +
					"WHERE storeName = " + "'" + storeName + "' " + "AND storeLocation = " + "'" + storeLocation + "'" + 
					" AND itemName LIKE " + "'" + itemName + "';", null);

			if (storeCursor.getCount() == 0)		
				database.execSQL("INSERT INTO StoreItemList (itemName) " +
						"VALUES (" + "'" + itemName + "');");	

			shoppingCursor.moveToNext();			
			storeCursor.close();
		}	

		// Updates all the recently added items with appropriate store name, store location, and item location
		database.execSQL("UPDATE StoreItemList " +
				" SET " +
				"storeName = " + "'" + storeName + "'" + ", " + 
				"storeLocation = " + "'" + storeLocation + "'" + ", " +
				"itemLocation = " + "\"" + "Unknown" + "\"" +
				" WHERE storeName IS NULL AND StoreLocation IS NULL AND itemLocation IS NULL");

		shoppingCursor.close();
		myDbHelper.close();				
	}


	/**
	 * Sorts the shopping list by location of items
	 * @param myDbHelper database helper
	 * @param storeName store name
	 * @param storeLocation storelocation
	 */
	public void sortShoppingList(DataBaseHelper myDbHelper, String storeName, String storeLocation)
	{
		database = myDbHelper.getWritableDatabase();
		myDbHelper.openDataBase();

		// Clears any existing sorted shopping list
		database.delete("ShoppingListSorted", null, null);

		/**		
		 * If the shopping list item name exists in the store item list table,
		 * then add it to the sorted shopping list table (if it's not already in sorted shopping list)
		 * This code makes it so we don't sort all items in the store, just those in the shopping list
		 */
		Cursor storeCursor = database.rawQuery("SELECT * FROM StoreItemList " +
				"WHERE storeName = " + "'" + storeName + "'" + "AND storeLocation = " + "'" + storeLocation + "';", null);

		storeCursor.moveToFirst();	

		while (storeCursor.isAfterLast() == false) 
		{
			String itemName = storeCursor.getString(storeCursor.getColumnIndex("itemName"));
			String itemLocation = storeCursor.getString(storeCursor.getColumnIndex("itemLocation"));

			Cursor shoppingCursor = database.rawQuery("SELECT itemName FROM ShoppingList " +
													  "WHERE itemName LIKE " + "'" + itemName + "';", null); 	

			Cursor sortedCursor = database.rawQuery("SELECT itemName FROM ShoppingListSorted " +
													"WHERE itemName LIKE " + "'" + itemName + "';", null); 

			if (shoppingCursor.getCount() >= 1 && sortedCursor.getCount() == 0)	
			{
				database.execSQL("INSERT INTO ShoppingListSorted (itemName, itemLocation) " +
								 "VALUES("+ "'" + itemName + "'" + ", " + "'" + itemLocation + "'" + ");");			
				shoppingCursor.close();
			}	

			storeCursor.moveToNext();	
		}

		storeCursor.close();		
		myDbHelper.close();		
	}


	/**
	 * Gets the sorted shopping list to display it
	 * @param myDbHelper database helper
	 * @return sorted shopping list as an array
	 */
	public ArrayList<ArrayList<String>> getItemNames2D(DataBaseHelper myDbHelper)
	{	
		database = myDbHelper.getWritableDatabase();	
		myDbHelper.openDataBase();	
		
		/**
		 * For each element in the locations array, loop through the table and see if the location in the array
		 * is equal to the location in the table. If so, add that item to a new array. 
		 * At the end of the table looping, add those array of items into the 2D array
		 */
		ArrayList<ArrayList<String>> itemNames2D = new ArrayList<ArrayList<String>>();		
		ArrayList<String> locations = getLocations(myDbHelper);				

		Cursor cursor = database.rawQuery("SELECT itemName, itemLocation FROM ShoppingListSorted " +
															 "ORDER BY itemLocation;", null); 					
		cursor.moveToFirst();
		
		ArrayList<String> itemNames = new ArrayList<String>();
		for (int i = 0; i < locations.size(); i++)
		{
			while (cursor.isAfterLast() == false) 
			{		
				String itemName = cursor.getString(cursor.getColumnIndex("itemName"));
				String itemLocation = cursor.getString(cursor.getColumnIndex("itemLocation"));
				
				if (locations.get(i).equals(itemLocation))	
					itemNames.add(itemName);					
				
				cursor.moveToNext();
			}	
			
			itemNames2D.add(itemNames);
			itemNames = new ArrayList<String>();
			cursor.moveToFirst();
		}		

		cursor.close();	
		myDbHelper.close();

		return itemNames2D;	
	}


	/**
	 * Gets list of non duplicate locations from the sorted shopping list
	 * @param myDbHelper database helper
	 * @return array of unique item locations in sorted shopping list
	 */
	public ArrayList<String> getLocations(DataBaseHelper myDbHelper)
	{
		ArrayList<String> locations = new ArrayList<String>();

		database = myDbHelper.getWritableDatabase();	
		myDbHelper.openDataBase();	

		Cursor cursor = database.rawQuery("SELECT DISTINCT itemLocation FROM ShoppingListSorted;", null); 		
		cursor.moveToFirst();

		while (cursor.isAfterLast() == false) 	
		{
			locations.add(cursor.getString(cursor.getColumnIndex("itemLocation")));
			cursor.moveToNext();
		}

		return locations;
	}	
	
	
	/**
	 * Gets a list of items for the listview in ManageShoppingListActivity
	 * @param myDbHelper database helper
	 * @return array of list items
	 */
	public ArrayList<String> getItemNames(DataBaseHelper myDbHelper)
	{
		ArrayList<String> locations = new ArrayList<String>();

		database = myDbHelper.getWritableDatabase();	
		myDbHelper.openDataBase();	

		Cursor cursor = database.rawQuery("SELECT itemName FROM ShoppingList ORDER BY itemName;", null); 		
		cursor.moveToFirst();

		while (cursor.isAfterLast() == false) 	
		{
			locations.add(cursor.getString(cursor.getColumnIndex("itemName")));
			cursor.moveToNext();
		}
		
		cursor.close();
		myDbHelper.close();

		return locations;
	}
}