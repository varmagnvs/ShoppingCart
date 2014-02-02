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
 * UI Class for updating items in a store
 * @author David Anderson
 */
public class UpdateStoreItemActivity extends Activity
{	
	private String[] storeAndItemInfo;	
	private String storeName;
	private String storeLocation;
	private String oldItemLocation;	
	private String oldItemName;
		
	private EditText itemNameEditText;
	private EditText aisleEditText;
	
	private Spinner categorySpinner;
	private String selectedCategory;	
	
	
	/**
	 * Main method, executes on startup
	 */
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modify_item_store);

		// Changes the label to the store selected on previous screen
		final TextView addItemStoreLabel = (TextView) findViewById(R.id.addItemstoStoreTextView);		
		Intent myIntent = getIntent();
		storeAndItemInfo = (String[]) myIntent.getSerializableExtra("key");
		storeName = storeAndItemInfo[0];
		storeLocation = storeAndItemInfo[1];
		oldItemName = storeAndItemInfo[2];
		oldItemLocation = storeAndItemInfo[3];
						
		aisleEditText = (EditText) findViewById(R.id.aisleText);	
		addItemStoreLabel.setText("Add Items To " + storeName);
		createCategorySpinner();
		
		itemNameEditText = (EditText) findViewById(R.id.itemNameText);
		itemNameEditText.setText(oldItemName);
		
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
	 * Method handles creating the category's for possible items
	 */
	public void createCategorySpinner()
	{	
		ArrayList<String>  categoryArrayList = new ArrayList<String>();
		
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
		
		if (locationIsCategory(categoryArrayList) == true)
			categorySpinner.setSelection(categoryArrayList.indexOf(oldItemLocation));
		
		else
			aisleEditText.setText(oldItemLocation);
	
		// Spinner listener
		categorySpinner.setOnItemSelectedListener(new OnItemSelectedListener() 
		{			
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) 
			{
				// Set location to selected item if the position isn't 0.
				if (categorySpinner.getSelectedItemPosition() != 0)		
				{
					selectedCategory = categorySpinner.getSelectedItem().toString();
					aisleEditText.setText("");
				}
			}

			public void onNothingSelected(AdapterView<?> parentView){} 	
		});
	}

	
	/**
	 * Method handles save item button
	 */
	public void updateItem(View v)
	{		
		String newItemName = itemNameEditText.getText().toString().trim();	
		String aisle = aisleEditText.getText().toString().trim();
		
		Store myStore = new Store();
		DataBaseHelper myDbHelper = new DataBaseHelper(this);

		if (newItemName.length() == 0)	
			itemNameEditText.setError("Please enter item name");	
		
		else if (newItemName.contains("'"))	
			itemNameEditText.setError("No single quotes allowed");
		
		else if (aisle.contains("'"))
			aisleEditText.setError("No single quotes allowed");
		
		// Error checking to see if both an aisle and a category was selected
		else if (categorySpinner.getSelectedItemPosition() != 0 && aisle.length() > 0)
		{			
			new AlertDialog.Builder(UpdateStoreItemActivity.this)
			.setTitle("Error")
			.setMessage("You have entered an aisle and selected a category. Please choose one or the other.")
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int which) {}
			})	
			.show();
		}		

		else
		{
			String newItemLocation;
			
			// Set location to unknown if no location for item was entered
			if (categorySpinner.getSelectedItemPosition() == 0 && aisle.length() == 0)
				newItemLocation = "Unknown";
				
			// Sets location to the aisle if no category was selected
			else if (categorySpinner.getSelectedItemPosition() == 0)
				newItemLocation = aisle;
			
			// Otherwise, set location to selected spinner item
			else
				newItemLocation = selectedCategory;
									
			// Adds to database
			myStore.updateItem(myDbHelper, storeName, storeLocation, oldItemName, oldItemLocation, newItemName, newItemLocation);
				
			// Resets screen and takes user back to previous screen			
			categorySpinner.setSelection(0);
			itemNameEditText.setText("");
			aisleEditText.setText("");			
			itemNameEditText.requestFocus();
			
			Toast.makeText(getApplicationContext(), oldItemName + " (" + oldItemLocation + ") " + 
					   	   "updated to " + newItemName + " (" + newItemLocation + ")", Toast.LENGTH_SHORT).show();	
			
			Intent myIntent = new Intent(UpdateStoreItemActivity.this, ManageStoreItemActivity.class);
			myIntent.putExtra("key", storeAndItemInfo);	
			startActivity(myIntent);
			finish(); //finish to prevent being stuck in navigating through different update activities
		}
	}
	
	/**
	 * Method determines whether the item location is part of the category spinner, or an aisle
	 * This is done so we know whether or not to put a hint in the aisle textfield, or to select the correct category spinner
	 * @param categoryArrayList 
	 * @param categoryArrayList array list of category's
	 * @return whether or not location is a category
	 */
	public boolean locationIsCategory(ArrayList<String> categoryArrayList)
	{
		boolean locationIsCategory = false;
		
		for (int i = 0; i < categoryArrayList.size(); i++)
			if (oldItemLocation.equals(categoryArrayList.get(i)))
				locationIsCategory = true;
		
			else
				locationIsCategory = false;
		
		return locationIsCategory;		
	}

	
	/**
	 * Method handles home screen button
	 * @param v view
	 */
	public void mainWindow(View v)
	{	
		startActivity(new Intent(UpdateStoreItemActivity.this, MainActivity.class));		
	}
}
