package com.example.shoppingcart;

import java.io.IOException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * UI Class for creating a store.
 * @author David Anderson
 *
 */
public class CreateStoreActivity extends Activity
{				
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_store);	
		
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
	 * Method handles what occurs when the save store button is clicked
	 * @param v
	 * @throws IOException 
	 */
	public void saveStore(View v)
	{					
		EditText storeNameText = (EditText) findViewById(R.id.storeNameText);
		String storeName = storeNameText.getText().toString().trim();
		
		EditText storeLocationText = (EditText) findViewById(R.id.storeLocationText);		
		String storeLocation = storeLocationText.getText().toString().trim();	
		
		Store myStore = new Store();		
		DataBaseHelper myDbHelper = new DataBaseHelper(this);
		
		// Error handling for empty fields
		if (isEmpty(storeNameText) == true)
			storeNameText.setError("Please enter store name");	
		
		else if (isEmpty(storeLocationText) == true)
			storeLocationText.setError("Please enter store location");	 
		
		// Error handling for parenthesis. Used to prevent bugs since next screen puts the location in parenthesis
		else if (storeName.contains("(") || storeName.contains(")"))
			storeNameText.setError("No parentheses allowed");	 
		
		else if (storeLocation.contains("(") || storeLocation.contains(")"))
			storeLocationText.setError("No parentheses allowed");	 
				
		// Error handling for duplicate stores		
		else if (myStore.hasDuplicateStores(myDbHelper, storeName, storeLocation) == true)
		{
			new AlertDialog.Builder(this)
		    .setTitle("Error")
		    .setMessage("The store you entered already exists. Please enter a new store name or store location")
		    .setPositiveButton("Ok", new DialogInterface.OnClickListener() 
		    {
		        public void onClick(DialogInterface dialog, int which) {}
		    })	
		     .show();		
		}			
	
		else
		{								
		    myStore.addStore(myDbHelper, storeName.trim(), storeLocation.trim());
		    Toast.makeText(getApplicationContext(), storeName + " saved to your list of stores.", Toast.LENGTH_SHORT).show();			
		    
		    // Resets fields & selects store name textbox as focus
		    //storeNameText.setText("");
		    //storeLocationText.setText("");	
			//storeNameText.requestFocus();	
			startActivity(new Intent(this, ListOfStoresActivity.class));	
		}	    
	}	
	
	
	/**
	 * Method for checking if text field is empty
	 * Source: http://stackoverflow.com/questions/6290531/check-if-edittext-is-empty
	 * @param etText
	 * @return whether or not a textfield is empty
	 */
	private boolean isEmpty(EditText etText)
	{
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
		startActivity(new Intent(CreateStoreActivity.this, MainActivity.class));		
	}
}