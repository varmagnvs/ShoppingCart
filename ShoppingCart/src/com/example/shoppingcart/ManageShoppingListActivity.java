package com.example.shoppingcart;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * UI Class for managing shopping lists
 * 
 * @author David Anderson, Marin Rangelov, Bertram Bishop
 * 
 */
public class ManageShoppingListActivity extends Activity {
	private DataBaseHelper myDbHelper;
	private ShoppingList myShoppingList;
	private SortList mySortList;
	private Store myStore;
	private String storeName;
	private String storeLocation;
	private ListView itemListView;
	private ArrayAdapter<String> adapter;
	private SQLiteDatabase database;
	public ArrayList<String> itemList;
	private ArrayList<String> arrayOfStoreNames;
	private String itemNames;
	private SparseBooleanArray checkedPositions;
	private String selectedItems;
	private int msg = 0;
	private Spinner storeNameSpinner;
	private int i;
	

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manage_shopping_list);
		storeNameSpinner = (Spinner) findViewById(R.id.storeSpinner);
		myShoppingList = new ShoppingList();
		mySortList = new SortList();
		populateStoreSpinner();

		itemListView = (ListView) findViewById(R.id.shoppingItemsListView);	
		createItemList();
			
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_multiple_choice, itemList);
		itemListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);		
		itemListView.setAdapter(adapter);
		
		adapter.notifyDataSetChanged();
	}

	/**
	 * Clear all selected checked items making them unchecked
	 */
	private void clearSelections() 
	{
		int count = this.itemListView.getAdapter().getCount();
		for (int i = 0; i < count; i++) 
		{
			this.itemListView.setItemChecked(i, false);

		}
	}
	
	@Override
	public void onBackPressed() {
		Intent refresh = new Intent(this, MainActivity.class);
		startActivity(refresh);
		this.finish();
	}

	public void onResume() 
	{
		super.onResume();
		populateStoreSpinner();
		clearSelections();
	}

	/**
	 * Method that handles adding a shopping list to a store
	 * 
	 * @param v
	 */
	public void addList(View v) 
	{
		int count = storeNameSpinner.getAdapter().getCount();
		mySortList.addToStore(myDbHelper, storeName, storeLocation);
		myShoppingList.deleteList(myDbHelper);
		adapter.notifyDataSetChanged();

		if (itemListView.getCheckedItemPositions().size() == msg) 
		{
			new AlertDialog.Builder(this)
			.setTitle("No Items Selected")
			.setMessage(
					"No items are in the list to add to store. Please create a create a list of items")
			.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() 
			{
						public void onClick(DialogInterface dialog,
								int which) {
						}
					}).show();
		} 
			
	    else if (count == 0) 
		{
	    	new AlertDialog.Builder(this)
			.setTitle("No Stores Created")
			.setMessage(
					"You have to create a store to add a list of items")
			.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() 
			{
						public void onClick(DialogInterface dialog,
								int which) {
						}
					}).show();
	    
		} else 
		{
			adapter.notifyDataSetChanged();

			Toast.makeText(getApplicationContext(),
					"Your shopping list has been added to " + storeName,
					Toast.LENGTH_SHORT).show();
			startActivity(new Intent(ManageShoppingListActivity.this,
					ManageShoppingListItemsActivity.class));
		}

	}

	/**
	 * Method that handles deletion of shopping list
	 * 
	 * @param v
	 */
	public void delList(View v) 
	{
		if(itemListView.getCount() == 0)
		{
			new AlertDialog.Builder(this)
			.setTitle("Blank List")
			.setMessage(
					"Please create an item list")
			.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() 
			{
						public void onClick(DialogInterface dialog,
								int which) {
						}
					}).show();
		
		}
	
		
		else 
		{
			AlertDialog.Builder alert = new AlertDialog.Builder(
					ManageShoppingListActivity.this);
			alert.setTitle("Are you sure you want to delete your shopping list?");

			alert.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() 
			{
						public void onClick(DialogInterface dialog,
								int whichButton) 
						{

							myShoppingList.deleteList(myDbHelper);
							mySortList.deleteList(myDbHelper);

							startActivity(new Intent(
									ManageShoppingListActivity.this,
									MainActivity.class));
						}

					});

			alert.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() 
			{
						public void onClick(DialogInterface dialog,
								int whichButton) {
						}
					});

			alert.show();
		}
	}
	
	/**
	 * Retrieve the data from the database to be displayed in the list view
	 * and remove duplicate items from the database
	 */
	public void createItemList() 
	{		
		
		myDbHelper = new DataBaseHelper(this);
		database = myDbHelper.getWritableDatabase();
		myDbHelper.openDataBase();		
		database.execSQL("delete from ShoppingList where rowid not in (select min(rowid) from ShoppingList group by itemName);");
		database.execSQL("delete from SortList where rowid not in (select min(rowid) from SortList group by itemName);");		
		Cursor cursor = database.rawQuery(
				"SELECT itemName FROM ShoppingList ORDER by itemName", null);
		cursor.moveToFirst();

		itemList = new ArrayList<String>();

		while (cursor.isAfterLast() == false) 
		{
			itemNames = cursor.getString(cursor.getColumnIndex("itemName"));
			itemList.add(itemNames);
			cursor.moveToNext();
		}		
		cursor.close();
		myDbHelper.close();
		database.close();
	}
		
	
	
	/**
	 * Method that retrieve the items from the database and to populate the
	 * listView
	 */
	public void createStoreItemList() 
	{
		myDbHelper = new DataBaseHelper(this);
		database = myDbHelper.getWritableDatabase();
		myDbHelper.openDataBase();
		Cursor cursor = database.rawQuery(
				"SELECT itemName FROM SortList ORDER by itemName", null);
		cursor.moveToFirst();

		itemList = new ArrayList<String>();

		while (cursor.isAfterLast() == false) 
		{
			itemNames = cursor.getString(cursor.getColumnIndex("itemName"));
			itemList.add(itemNames);
			cursor.moveToNext();
		}
		cursor.close();
		myDbHelper.close();
		database.close();
	}

	/**
	 * Method populates store spinner
	 */
	public void populateStoreSpinner() 
	{
		storeNameSpinner = (Spinner) findViewById(R.id.storeSpinner);
		myDbHelper = new DataBaseHelper(ManageShoppingListActivity.this);
		myStore = new Store();

		arrayOfStoreNames = myStore.getListOfStores(myDbHelper);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				ManageShoppingListActivity.this, R.layout.spinner_center_item,
				arrayOfStoreNames);
		storeNameSpinner.setAdapter(adapter);

		storeNameSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() 
				{
					public void onItemSelected(AdapterView<?> parentView,
							View selectedItemView, int position, long id) 
					{
						String selectedStore = storeNameSpinner
								.getSelectedItem().toString();
						storeName = selectedStore.substring(0,
								selectedStore.indexOf("(") - 1);
						storeLocation = selectedStore.substring(
								selectedStore.indexOf("(") + 1,
								selectedStore.length() - 1);
					}

					public void onNothingSelected(AdapterView<?> parentView) {
					}
				});
	}

	/**
	 * Method for creating a store Go to create a store activity upon button
	 * press
	 * 
	 * @param v
	 *            view
	 */
	public void createStore(View v) 
	{
		startActivity(new Intent(ManageShoppingListActivity.this,
				CreateStoreActivity.class));
	}

	/**
	 * Method sorts shopping list based upon item locations
	 * 
	 * @param v
	 *            view
	 */
	public void sortList(View v) 
	{
		
		startActivity(new Intent(ManageShoppingListActivity.this,
				ManageShoppingListItemsActivity.class));	
	}

	/**
	 * Function that deletes the items selected in the list view
	 * 
	 * @param v
	 * @throws IOException
	 */
	public void deleteItem(View v) throws IOException 
	{
		int count = itemListView.getAdapter().getCount();
		checkedPositions = itemListView.getCheckedItemPositions();
		
		if(itemListView.isItemChecked(i) == true)
		{
			for (i = 0; i < count; i++) 
			{
				myDbHelper = new DataBaseHelper(this);
				database = myDbHelper.getWritableDatabase();
				myDbHelper.openDataBase();
				if (checkedPositions.size() > 0) 
				{
					// CHECKED
					selectedItems = (String) (itemListView.getItemAtPosition(i));
					
					database.execSQL("DELETE FROM ShoppingList "
							+ " WHERE itemName = " + "'" + selectedItems + "';");
					database.execSQL("DELETE FROM SortList "
							+ " WHERE itemName = " + "'" + selectedItems + "';");
					Object toRemove = itemListView.getAdapter().getItem(i);
					adapter.remove((String) toRemove);

				}
				myDbHelper.close();
				database.close();

			}
			clearSelections();
			adapter.notifyDataSetChanged();
			Toast.makeText(
					getApplicationContext(),
					"Selected items have been deleted from your shopping list.",
					Toast.LENGTH_SHORT).show();
		}
		else if(itemListView.isItemChecked(i) == false)
		{
			new AlertDialog.Builder(this)
			.setTitle("No Checked Items")
			.setMessage(
					"Please select items to delete")
			.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() 
			{
						public void onClick(DialogInterface dialog,
								int which) {
						}
					}).show();
		}

	}

/**
	 * Method handles home screen button
	 * @param v view
	 */
	public void mainWindow(View v)
	{	
		startActivity(new Intent(ManageShoppingListActivity.this, MainActivity.class));	
		finish();
	}
}
