package com.example.shoppingcart;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * UI Class for managing a store
 * @author David Anderson
 */
public class ManageStoreActivity extends Activity
{
	private DataBaseHelper myDbHelper;

	private Store myStore;
	private TextView storeLabel;
	private String[] storeInfo;	
	private String storeName;
	private String storeLocation;
	private SortList mySortList;
	private SQLiteDatabase database;


	private ArrayList<String> arrayOfStoreItems;

	
	private AutoCompleteTextView textView;
	private ArrayAdapter<String> adapter;
	

	/**
	 * Method executes on screen startup
	 */
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);	   
		setContentView(R.layout.manage_store);
		
		
		myDbHelper = new DataBaseHelper(ManageStoreActivity.this);	

		
		storeLabel = (TextView) findViewById(R.id.listOfStoreTextView);			
		Intent myIntent = getIntent();		
		myStore = new Store();
		storeInfo = (String[]) myIntent.getSerializableExtra("key");

		storeName = storeInfo[0];
		storeLocation = storeInfo[1];				

		storeLabel.setText("Manage " + storeName + "\n" + "(" + storeLocation + ")" );
		
		//Instantiates AutoCompleteTextView
		textView = (AutoCompleteTextView) findViewById(R.id.autoCompleteFindStoreItem);
		ArrayList<String> listOfItemsAl = myStore.getAllItems(myDbHelper);
		String[] items = listOfItemsAl.toArray(new String[listOfItemsAl.size()]);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
		textView.setAdapter(adapter);
		
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
	
	public void onResume()
	{
		super.onResume();
		
		ArrayList<String> listOfItemsAl = myStore.getAllItems(myDbHelper);
		String[] items = listOfItemsAl.toArray(new String[listOfItemsAl.size()]);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
		textView.setAdapter(adapter);	
	}


	/**
	 * Method handles manage items button
	 * @param v view
	 */
	public void manageAddItems(View v)
	{
		
			Intent myIntent = new Intent(ManageStoreActivity.this, ManageStoreItemActivity.class);	
			myIntent.putExtra("key", storeInfo);
			startActivity(myIntent); 
		
	}


	/**
	 * Method handles deleting a store
	 * @param v view
	 */
	public void deleteStore(View v)
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(ManageStoreActivity.this);
		String storeLoc = storeLabel.getText().toString().trim();
		final String storeLocFixed = storeLoc.replace("Manage", "");
		alert.setTitle("Are you sure you want to delete store " + storeLocFixed + " ?");						

		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton) 
			{					
				myDbHelper = new DataBaseHelper(ManageStoreActivity.this);			
				database = myDbHelper.getWritableDatabase();
				myDbHelper.openDataBase();
				database.execSQL("DELETE FROM SortList WHERE itemName = itemName");
				myDbHelper.close();
				database.close();
								
				myStore.deleteStore(myDbHelper, storeName, storeLocation);
				
				
				
				Toast.makeText(getApplicationContext(), storeLocFixed + " deleted from your list of stores", Toast.LENGTH_SHORT).show();	

				startActivity(new Intent(ManageStoreActivity.this, MainActivity.class));  
			}								
		});																				

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int whichButton) {}
		});

		alert.show();	
		
	}


	/**
	 * Method handles changing a store name
	 * @param v view
	 */
	public void renameStore(View v)
	{
		// Pop-up for confirming rename
		AlertDialog.Builder alert = new AlertDialog.Builder(ManageStoreActivity.this);
		alert.setTitle("Enter new store name");						
		final EditText storeNameInput = new EditText(ManageStoreActivity.this);
		alert.setView(storeNameInput);

		alert.setPositiveButton("Rename", new DialogInterface.OnClickListener()
		{						
			public void onClick(DialogInterface dialog, int whichButton) 
			{								
				String renamedStore = storeNameInput.getText().toString().trim();												
				myDbHelper = new DataBaseHelper(ManageStoreActivity.this);	

				if (renamedStore.contains("'"))
				{
					new AlertDialog.Builder(ManageStoreActivity.this)
					.setTitle("Error")
					.setMessage("No single quotes allowed")
					.setPositiveButton("Ok", new DialogInterface.OnClickListener() 
					{
						public void onClick(DialogInterface dialog, int which) {}
					})	
					.show();
				}

				// Check for duplicate stores
				else if (myStore.hasDuplicateStores(myDbHelper, renamedStore, storeLocation) == true)
				{
					new AlertDialog.Builder(ManageStoreActivity.this)
					.setTitle("Error")
					.setMessage("The store you entered already exists. Please enter a new store name or store location.")
					.setPositiveButton("Ok", new DialogInterface.OnClickListener() 
					{
						public void onClick(DialogInterface dialog, int which) {}
					})	
					.show();
				}									

				else
				{
					myStore.renameStore(myDbHelper, renamedStore, storeName, storeLocation);			    
					Toast.makeText(getApplicationContext(), storeName + " renamed to " + renamedStore, Toast.LENGTH_SHORT).show();														
					storeLabel.setText("Manage " + renamedStore + "\n" + "(" + storeLocation + ")" );
				}
			}								
		});			

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int whichButton){}
		});

		alert.show();	
	}


	/**
	 * Method handles changing store location
	 * @param v view
	 */
	public void changeLocation(View v)
	{
		// Pop-up for confirming rename
		AlertDialog.Builder alert = new AlertDialog.Builder(ManageStoreActivity.this);
		alert.setTitle("Enter new store location");						
		final EditText storeNameInput = new EditText(ManageStoreActivity.this);
		alert.setView(storeNameInput);

		alert.setPositiveButton("Rename", new DialogInterface.OnClickListener()
		{						
			public void onClick(DialogInterface dialog, int whichButton) 
			{								
				String newStoreLocation = storeNameInput.getText().toString().trim();							
				myDbHelper = new DataBaseHelper(ManageStoreActivity.this);	

				if (newStoreLocation.contains("'"))
				{
					new AlertDialog.Builder(ManageStoreActivity.this)
					.setTitle("Error")
					.setMessage("No single quotes allowed")
					.setPositiveButton("Ok", new DialogInterface.OnClickListener() 
					{
						public void onClick(DialogInterface dialog, int which) {}
					})	
					.show();
				}

				// Check for duplicate stores
				else if (myStore.hasDuplicateStores(myDbHelper, storeName, newStoreLocation) == true)
				{
					new AlertDialog.Builder(ManageStoreActivity.this)
					.setTitle("Error")
					.setMessage("The store you entered already exists. Please enter a new store name or store location.")
					.setPositiveButton("Ok", new DialogInterface.OnClickListener() 
					{
						public void onClick(DialogInterface dialog, int which) {}
					})	
					.show();
				}	

				else
				{
					myStore.renameLocation(myDbHelper, storeName, newStoreLocation, storeLocation);
					Toast.makeText(getApplicationContext(), "Location " + storeLocation + " changed to " + newStoreLocation, Toast.LENGTH_SHORT).show();														
					storeLabel.setText("Manage " + storeName + "\n" + "(" + newStoreLocation + ")" );
				}
			}								
		});			

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int whichButton){}
		});

		alert.show();	
	}
}

	
