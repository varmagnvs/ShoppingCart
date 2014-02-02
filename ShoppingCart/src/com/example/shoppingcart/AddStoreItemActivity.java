package com.example.shoppingcart;

import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * UI Class for adding items to a store
 * @author David Anderson
 */
public class AddStoreItemActivity extends Activity
{	
	private String storeName;
	private String storeLocation;

	private Spinner categorySpinner;
	private String selectedCategory;
	
	
	/**
	 * Main method, executes on startup
	 */
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_item_store);

		// Changes the label to the store selected on previous screen
		final TextView addItemStoreLabel = (TextView) findViewById(R.id.addItemstoStoreTextView);		
		Intent myIntent = getIntent();
		String[] storeInfo = (String[]) myIntent.getSerializableExtra("key");
		storeName = storeInfo[0];
		storeLocation = storeInfo[1];
		
		addItemStoreLabel.setText("Add Items To " + storeName);
		createCategorySpinner();
		
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
	 * Method handles creating the category's for item locations
	 */
	public void createCategorySpinner()
	{	
		ArrayList<String> categoryArrayList = new ArrayList<String>();
		categoryArrayList.add("Category");
		categoryArrayList.add("Produce");	
		categoryArrayList.add("Deli");
		categoryArrayList.add("Bakery");
		categoryArrayList.add("Meat");
		categoryArrayList.add("Dairy");
		categoryArrayList.add("Frozen");				

		categorySpinner = (Spinner) findViewById(R.id.categorySpinner);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_center_item, categoryArrayList);
		adapter.setDropDownViewResource(R.layout.spinner_center_item);
		categorySpinner.setAdapter(adapter);
	
		categorySpinner.setOnItemSelectedListener(new OnItemSelectedListener() 
		{			
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) 
			{			
				if (categorySpinner.getSelectedItemPosition() != 0)					
					selectedCategory = categorySpinner.getSelectedItem().toString();	
			}

			public void onNothingSelected(AdapterView<?> parentView){} 	
		});
	}

	
	/**
	 * Method handles save item button
	 * Upon clicking save item button, gets the item name and location and puts it into the database
	 */
	public void saveItem(View v)
	{	
		final EditText itemNameEditText = (EditText) findViewById(R.id.itemNameText);
		String itemName = itemNameEditText.getText().toString().trim();
		
		EditText aisleEditText = (EditText) findViewById(R.id.aisleText);
		String aisle = aisleEditText.getText().toString().trim();
		
		Store myStore = new Store();
		DataBaseHelper myDbHelper = new DataBaseHelper(this);

		if (itemName.length() == 0)	
			itemNameEditText.setError("Please enter item name");	
		
		else if (itemName.contains("'"))	
			itemNameEditText.setError("No single quotes allowed");
		
		else if (aisle.contains("'"))
			aisleEditText.setError("No single quotes allowed");
		
		// Error checking to see if both an aisle and a category was selected
		else if (categorySpinner.getSelectedItemPosition() != 0 && aisle.length() > 0)
		{			
			new AlertDialog.Builder(AddStoreItemActivity.this)
			.setTitle("Error")
			.setMessage("You have entered an aisle and selected a category. Please choose one or the other.")
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int which) {}
			})	
			.show();
		}
		
		// Error checking for duplicate store items
		else if (myStore.hasDuplicateStoreItems(myDbHelper, storeName, storeLocation, itemName) == true)
		{
			new AlertDialog.Builder(AddStoreItemActivity.this)
			.setTitle("Error")
			.setMessage("The item you entered already exists in this store. Please enter a new item name.")
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int which) 
				{
					itemNameEditText.requestFocus();
				}
			})	
			.show();
		}			

		else
		{	
			String itemLocation;
			
			// Set location to unknown if no location for item was entered
			if (categorySpinner.getSelectedItemPosition() == 0 && aisle.length() == 0)
				itemLocation = "Unknown";
				
			// Sets location to the aisle if no category was selected
			else if (categorySpinner.getSelectedItemPosition() == 0)
				itemLocation = aisle;
			
			// Otherwise, set location to selected spinner item
			else
				itemLocation = selectedCategory;
							
			myStore.addItems(myDbHelper, storeName, storeLocation, itemName, itemLocation);			
			Toast.makeText(getApplicationContext(), itemName + " saved to " + storeName, Toast.LENGTH_SHORT).show();	
				
			// Resets screen to enable user to add more items easier					
			categorySpinner.setSelection(0);
			itemNameEditText.setText("");
			aisleEditText.setText("");			
			itemNameEditText.requestFocus();
		}
	}

	
	/**
	 * Method handles home screen button
	 * @param v view
	 */
	public void mainWindow(View v)
	{	
		startActivity(new Intent(AddStoreItemActivity.this, MainActivity.class));
		finish();
	}
}
