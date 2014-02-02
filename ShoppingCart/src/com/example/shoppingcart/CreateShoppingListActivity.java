package com.example.shoppingcart;

/**
 * UI Class for creating shopping lists,
 * populating the main application 
 * database table and the shopping list table
 * @author Bertram Bishop
 * ITEC 4860 - Software Development Project
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class CreateShoppingListActivity extends Activity 
{
	private String selectedItems;
	private ListView mainListView;
	private String itemNames;
	private MainList mainList;
	private ArrayList<String> duplicateList;
	private DataBaseHelper myDbHelper;
	private SQLiteDatabase database;
	private ArrayList<String> itemList;
	private ArrayAdapter<String> listAdapter;
	final String SETTING_TODOLIST = "todolist";
	private SparseBooleanArray checkedPositions;
	protected int itemPosition;
	private ItemList myList;
	private int j;
	private ArrayList<String> selectedList;

	ManageShoppingListActivity mActivity = new ManageShoppingListActivity();

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_shopping_list);
		mainListView = (ListView) findViewById(R.id.ItemslistView);

		createItemList();

		listAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_multiple_choice, itemList);
		mainListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		mainListView.setAdapter(listAdapter);
		listAdapter.notifyDataSetChanged();
	}

	public void onResume() 
	{
		super.onResume();
		clearSelections();
		listAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_multiple_choice, itemList);
		mainListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		mainListView.setAdapter(listAdapter);
		listAdapter.notifyDataSetChanged();

		RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout);	
		layout.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event) 
			{
				{
					hideKeyboard(v);
					return false;
				}
			}
		});	
	}

	/**
	 * Method enables keyboard to be hidden when clicked outside of editable text box
	 * @param view
	 */
	protected void hideKeyboard(View view)
	{
		InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * Clear all selected checked items making them unchecked
	 */
	private void clearSelections() 
	{
		int count = this.mainListView.getAdapter().getCount();
		for (int i = 0; i < count; i++) 
		{
			this.mainListView.setItemChecked(i, false);

		}
	}

	/**
	 * Function that traverses and find all items in the main table for the
	 * listview to display
	 */
	public void createItemList() 
	{
		myDbHelper = new DataBaseHelper(this);
		database = myDbHelper.getWritableDatabase();
		myDbHelper.openDataBase();
		Cursor cursor = database
				.rawQuery(
						"SELECT mainItemList FROM MainShoppingList ORDER by mainItemList",
						null);
		cursor.moveToFirst();

		itemList = new ArrayList<String>();

		while (cursor.isAfterLast() == false) 
		{
			itemNames = cursor.getString(cursor.getColumnIndex("mainItemList"));
			itemList.add(itemNames);
			cursor.moveToNext();
		}
		cursor.close();
		myDbHelper.close();
		database.close();

	}

	/**
	 * Function adds new items to the database by user adding items in the add
	 * items text field and saving to the main table. Also check for duplicates
	 * in the table
	 * 
	 * @param v
	 * @throws IOException
	 */
	public void addNewItem(View v) throws IOException 
	{

		mainList = new MainList();
		DataBaseHelper myDbHelper = new DataBaseHelper(this);
		EditText newItemText = (EditText) findViewById(R.id.newItemTextV);
		String itemNames = newItemText.getText().toString().trim();

		if (isEmpty(newItemText) == true)
			newItemText.setError("Please enter an item name");

		else if (mainList.hasDuplicateItems(myDbHelper, itemNames) == true) 
		{
			new AlertDialog.Builder(this)
			.setTitle("Item Exist")
			.setMessage(
					"The item you entered already exists. Please enter a new item name")
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() 
					{
						public void onClick(DialogInterface dialog,
								int which) {
						}
					}).show();
			Toast.makeText(getApplicationContext(),
					" Please enter another item.", Toast.LENGTH_SHORT).show();
		}

		else if (mainList.hasDuplicateItems(myDbHelper, itemNames) == false) 
		{
			// Adds new item to the top of the list
			itemList.add(0, newItemText.getText().toString());
			listAdapter.notifyDataSetChanged();
			newItemText.setText("");

			// Adds list to database
			MainList mainList = new MainList();
			mainList.addList(myDbHelper, itemNames.trim());

			myDbHelper.close();
			database.close();

			// Resets fields
			newItemText.setText("");

		}

	}

	/**
	 * Method deletes items from the database and remove them from the list view
	 * 
	 * @param v
	 * @throws IOException
	 */
	public void removeItem(View v) throws IOException 
	{
		int count = mainListView.getAdapter().getCount();
		final SparseBooleanArray checkedPositionsToDelete = mainListView
				.getCheckedItemPositions();

		if (checkedPositionsToDelete.size() > 0) 
		{
			for (int j = 0; j < count; j++) 
			{
				myDbHelper = new DataBaseHelper(this);
				database = myDbHelper.getWritableDatabase();
				myDbHelper.openDataBase();
				if (mainListView.isItemChecked(j)) 
				{
					// CHECKED
					selectedItems = (String) (mainListView.getItemAtPosition(j));
					database.execSQL("DELETE FROM MainShoppingList "
							+ " WHERE mainItemList = " + "'" + selectedItems
							+ "';");
					Object toRemove = mainListView.getAdapter().getItem(j);
					listAdapter.remove((String) toRemove);
				}
				myDbHelper.close();
				database.close();
			}
			clearSelections();
			listAdapter.notifyDataSetChanged();
			Toast.makeText(getApplicationContext(),
					"Selected items have been deleted from your list.",
					Toast.LENGTH_SHORT).show();
		} 
		else 
		{

			new AlertDialog.Builder(this)
			.setTitle("No Items Selected")
			.setMessage("Please select items to delete")
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
	 * Check for checked items and only save checked items and insert all
	 * checked items into the database
	 */
	private void saveItems() 
	{
		checkedPositions = mainListView.getCheckedItemPositions();
		int count = mainListView.getAdapter().getCount();
		if (checkedPositions.size() == 0) 
		{
			new AlertDialog.Builder(this)
			.setTitle("No items selected")
			.setMessage("Please select items to save")
			.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog,
						int which) {
				}
			}).show();
			mainListView.clearChoices();
		} else if (checkedPositions.size() > 0) 
		{
			for (j = 0; j < count; j++) 
			{
				myDbHelper = new DataBaseHelper(this);
				database = myDbHelper.getWritableDatabase();
				myDbHelper.openDataBase();
				if (mainListView.isItemChecked(j)) 
				{
					// CHECKED
					selectedItems = (String) (mainListView.getItemAtPosition(j));

					database.execSQL("INSERT INTO ShoppingList (itemName) VALUES("
							+ "'" + selectedItems + "'" + ");");
					database.execSQL("INSERT INTO SortList (itemName) VALUES("
							+ "'" + selectedItems + "'" + ");");
				}
				myDbHelper.close();
				database.close();
			}
			mainListView.clearChoices();
			Toast.makeText(getApplicationContext(),
					"Items saved, but duplicate selected items was not saved.",
					Toast.LENGTH_LONG).show();
			Intent myIntent = new Intent(CreateShoppingListActivity.this,
					ManageShoppingListActivity.class);
			startActivity(myIntent);

		}
	}

	/**
	 * Function that saves the items selected in the list view and saves them in
	 * the shopping list table. Also checks for duplicate items
	 * 
	 * @param v
	 * @throws IOException
	 */
	public void saveList(View v) throws IOException 
	{
		myList = new ItemList();
		selectedList = new ArrayList<String>();
		checkedPositions = mainListView.getCheckedItemPositions();
		int count = mainListView.getAdapter().getCount();
		for (j = 0; j < count; j++) 
		{
			if (checkedPositions.get(j) == true) 
			{
				// CHECKED
				selectedItems = (String) (mainListView.getItemAtPosition(j));
				selectedList.add(selectedItems);
			}

		}
		// Check for duplicates and displays the list of duplicate items
		if (myList.hasDuplicateItems(myDbHelper, selectedItems) == true) 
		{
			AlertDialog.Builder builderSingle = new AlertDialog.Builder(
					CreateShoppingListActivity.this);
			builderSingle.setTitle("Duplicate Items");
			final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
					CreateShoppingListActivity.this,
					android.R.layout.simple_list_item_1);

			myDbHelper = new DataBaseHelper(this);
			database = myDbHelper.getWritableDatabase();
			myDbHelper.openDataBase();
			Cursor cursor = database.rawQuery(
					"SELECT itemName FROM ShoppingList", null);
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
			duplicateList = new ArrayList<String>();
			for (String duplicate : itemList) 
			{
				duplicateList.add(duplicate);
				if (selectedList.contains(duplicate)) 
				{
					arrayAdapter.add(duplicate);
				} else if (selectedList.size() > itemList.size()
						&& selectedList.contains(duplicate)) 
				{
					arrayAdapter.add(duplicate);
				}

			}

			builderSingle.setNegativeButton("cancel",
					new DialogInterface.OnClickListener() 
			{

				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					dialog.dismiss();
				}
			});

			builderSingle.setAdapter(arrayAdapter,
					new DialogInterface.OnClickListener() 
			{

				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					String strName = arrayAdapter.getItem(which);
					AlertDialog.Builder builderInner = new AlertDialog.Builder(
							CreateShoppingListActivity.this);
					builderInner.setMessage(strName);
					// builderInner.setTitle("Your Selected Item is");
					builderInner.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() 
					{

						@Override
						public void onClick(
								DialogInterface dialog,
								int which) 
						{
							dialog.dismiss();

						}
					});
					builderInner.show();
				}
			});
			builderSingle.show();
			clearSelections();
		}
		// If duplicates selected along with non duplicate items
		// only the non duplicate items are saved.
		else if (myList.hasDuplicateItems(myDbHelper, selectedItems) == false) 
		{
			saveItems();

		}

	}

	/**
	 * Function for checking if text field is empty
	 * 
	 * @param etText
	 * @return whether or not a textfield is empty
	 */
	private boolean isEmpty(EditText etText) {
		if (etText.getText().toString().trim().length() > 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Method handles home screen button
	 * @param v view
	 */
	public void mainWindow(View v)
	{	
		startActivity(new Intent(CreateShoppingListActivity.this, MainActivity.class));		
	}
}
