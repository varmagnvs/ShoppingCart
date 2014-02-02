package com.example.shoppingcart;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * UI class for managing store items
 * @author David Anderson
 */
public class ManageStoreItemActivity extends Activity 
{
	private Store myStore;
	private String[] storeInfo;
	private String storeName;
	private String storeLocation;

	private ListView storeItemsList;	
	private ArrayList<String> arrayOfStoreItems;
	private ArrayAdapter<String> adapter;
	private DataBaseHelper myDbHelper;

	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manage_store_item);	

		Intent myIntent = getIntent();
		storeInfo = (String[]) myIntent.getSerializableExtra("key");
		storeName = storeInfo[0];
		storeLocation = storeInfo[1];
		myStore = new Store();		
		storeItemsList = (ListView) findViewById(R.id.storeItemsView);		

		TextView titleView = new TextView(getApplicationContext());
		titleView.setText("Tap to modify item");
		titleView.setTypeface(null, Typeface.BOLD);
		titleView.setTextSize(15);		
		storeItemsList.addHeaderView(titleView, null, false);

		myDbHelper = new DataBaseHelper(this);			
		getStoreItems();
	}


	/**
	 * Method refreshes any changes in the database when back button pressed from AddStoreItemActivity
	 */
	public void onResume() 
	{
		super.onResume();
		arrayOfStoreItems = myStore.getListOfItems(myDbHelper, storeName, storeLocation);

		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayOfStoreItems);		
		storeItemsList.setAdapter(adapter);	
	}


	/**
	 * Method handles adding a new item to store
	 * @param v view
	 */
	public void addNewItem(View v)
	{

		Intent myIntent = new Intent(ManageStoreItemActivity.this, AddStoreItemActivity.class);	
		myIntent.putExtra("key", storeInfo);
		startActivity(myIntent);  

	}


	/**
	 * Gets store items from database
	 */
	public void getStoreItems()
	{		

		storeItemsList.setOnItemClickListener(new OnItemClickListener() 
		{
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
			{	
				String selectedStore = (String) (storeItemsList.getItemAtPosition(position));

				String itemName = selectedStore.substring(0, selectedStore.indexOf("(") - 1);		
				String itemLocation = selectedStore.substring(selectedStore.indexOf("(") + 1, selectedStore.length()- 1);																	
				String[] storeAndItemInfo = {storeName, storeLocation, itemName, itemLocation};

				Intent myIntent = new Intent(ManageStoreItemActivity.this, UpdateStoreItemActivity.class);
				myIntent.putExtra("key", storeAndItemInfo);	
				startActivity(myIntent);	
				finish(); //finish here as well to end being stuck at the items list
			}
		});
	}

	/**
	 * when the back button is touched the user will be taken back to the ManageStore page
	 *to act as a main home page when managing files within the stores
	 */
	public void onBack()
	{
		Intent myIntent = new Intent(ManageStoreItemActivity.this, ManageStoreActivity.class);
		startActivity(myIntent);
		return;
	}

/**
	 * Method handles home screen button
	 * @param v view
	 */
	public void mainWindow(View v)
	{	
		startActivity(new Intent(ManageStoreItemActivity.this, MainActivity.class));		
	}
}
