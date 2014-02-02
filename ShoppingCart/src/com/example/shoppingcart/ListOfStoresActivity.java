package com.example.shoppingcart;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * UI class for the list of stores
 * @author David Anderson
 */
public class ListOfStoresActivity extends Activity 
{			
	private ArrayAdapter<String> adapter;
	private ArrayList<String> listOfStores;	
	private ListView storesList;	
	private DataBaseHelper myDbHelper;
	private Store myStore;


	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.list_of_stores);
		myDbHelper = new DataBaseHelper(this);
		myStore = new Store();
		storesList = (ListView) findViewById(R.id.storeListView);
		
		createStoreList();
	}

	/**
	 * Method refreshes any changes in the database when back button pressed from ManageStoreActivity
	 */
	public void onResume() 
	{
		super.onResume();
		listOfStores = myStore.getListOfStores(myDbHelper);	

		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listOfStores);		
		storesList.setAdapter(adapter);	
	}
	
	public void createShoppingList()
	{
		Intent myIntent = new Intent(ListOfStoresActivity.this,CreateShoppingListActivity.class);
		startActivity(myIntent);	
	}


	/**
	 * Method handles creating the list of stores
	 */
	public void createStoreList()
	{
			
		listOfStores = myStore.getListOfStores(myDbHelper);

		// If no stores exist, display error
		if (listOfStores.isEmpty())
		{
			Toast.makeText(getApplicationContext(), "Currently there are no stores present. Please create a store first", Toast.LENGTH_SHORT).show();
			finish();
		}

		else
		{
			adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listOfStores);		
			storesList.setAdapter(adapter);	

			storesList.setOnItemClickListener(new OnItemClickListener() 
			{
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
				{	
					
					String selectedStore = (String) (storesList.getItemAtPosition(position));

					// Gets store name and location from selected store
					String storeName = selectedStore.substring(0, selectedStore.indexOf("(") - 1);		
					String storeLocation = selectedStore.substring(selectedStore.indexOf("(") + 1, selectedStore.length()- 1);																	
					String[] storeInfo = {storeName, storeLocation};

					Intent myIntent = new Intent(ListOfStoresActivity.this, ManageStoreActivity.class);
					myIntent.putExtra("key", storeInfo);	
					startActivity(myIntent);	
					
				}
			});			
		}
	}
	/**
	 * Method handles home screen button
	 * @param v view
	 */
	public void mainWindow(View v)
	{	
		startActivity(new Intent(ListOfStoresActivity.this, MainActivity.class));
		finish();
	}
}