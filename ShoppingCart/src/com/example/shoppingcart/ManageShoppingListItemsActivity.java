package com.example.shoppingcart;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
/**
 * UI Class for managing shopping list items.
 * 
 * @author Bertram Bishop
 */
public class ManageShoppingListItemsActivity extends Activity {

	private static final String TAG="ManageShoppingListItemsActivity";
	public ListView storeItemsListView;
	private Store myStore;
	private SortList mySortList;
	private String storeName;
	private String storeLocation;
	private String itemNames;
	private DataBaseHelper myDbHelper;
	private SQLiteDatabase database;
	private ArrayList<String> itemList;
	private ArrayAdapter<String> listAdapter;
	private Button importBtn,exportBtn,sendMailBtn;	
	private String iName;
	private String[] nextLine;
	private String sName;
	private String lName;
	String exportFileName="ExportExcel.csv";
	Context context;
	File file=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manage_shopping_list_items);
		mySortList = new SortList();
		storeItemsListView = (ListView) findViewById(R.id.storeItemslistView);

		itemList = new ArrayList<String>();
		listAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, itemList);

		populateStoreSpinner();
		createItemList();

		storeItemsListView.setAdapter(listAdapter);
		listAdapter.notifyDataSetChanged();
		
		importBtn=(Button) findViewById(R.id.importDataBtn);
		exportBtn=(Button) findViewById(R.id.exportBtn);
		sendMailBtn = (Button) findViewById(R.id.sendMailBtn);	
	}

	/**
	 * Function that traverses and find all items in the main table for the
	 * listview to display
	 */
	public void createItemList() 
	{
		listAdapter.notifyDataSetChanged();
		myDbHelper = new DataBaseHelper(this);
		database = myDbHelper.getWritableDatabase();
		database.execSQL("delete from ShoppingList where rowid not in (select min(rowid) from ShoppingList group by itemName);");
		database.execSQL("delete from SortList where rowid not in (select min(rowid) from SortList group by itemName);");
		myDbHelper.openDataBase();
		Cursor cursor = database.rawQuery(
				"SELECT itemName FROM SortList ORDER by itemName", null);
		cursor.moveToFirst();

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
		final Spinner storeNameSpinner = (Spinner) findViewById(R.id.storeItemsSpinner1);
		myDbHelper = new DataBaseHelper(ManageShoppingListItemsActivity.this);
		myStore = new Store();

		ArrayList<String> arrayOfStoreNames = myStore
				.getListOfStores(myDbHelper);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				ManageShoppingListItemsActivity.this,
				R.layout.spinner_center_item, arrayOfStoreNames);
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
	 * Method sort the shopping list
	 * 
	 * @param v
	 */
	public void sortListItems(View v) 
	{
		mySortList.sortShoppingList(myDbHelper, storeName, storeLocation);
		startActivity(new Intent(ManageShoppingListItemsActivity.this,
				SortedItemsActivity.class));
	}

	/**
	 * Method that handles the deletion of the shopping list
	 * 
	 * @param v
	 */
	public void delList(View v) 
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(
				ManageShoppingListItemsActivity.this);
		if(storeItemsListView.getAdapter().getCount() == 0)
		{
			new AlertDialog.Builder(this)
			.setTitle("Empty List")
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
		else
		{	
		alert.setTitle("Are you sure you want to delete your shopping list?");

		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int whichButton) 
			{
				mySortList.deleteList(myDbHelper);
				Toast.makeText(getApplicationContext(),
						"Your shopping list was deleted", Toast.LENGTH_LONG)
						.show();
				finish(); //finished here
				startActivity(new Intent(ManageShoppingListItemsActivity.this,
						MainActivity.class));
				finish();
			}
		});
		

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() 
		{
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				});

		alert.show();
	}
	}
	
	/**
	 * Method that handles the importing, exporting, and emailing of the shopping list
	 * 
	 * @param v
	 * @throws IOException 
	 */
	public void emailList(View v) throws IOException
	{
		Log.v(TAG, "onClick called");
		// Export the list contents from the SortList table to the ImportExport.csv file
		if(v==exportBtn){
			
			ExportDatabaseCSVTask task=new ExportDatabaseCSVTask();
			task.execute();
		}
		// Imports the contents of ImportExport.csv to the Shopping and SortList table
		// and populate the listview
		else if(v==importBtn){
	        File exportDir = new File(Environment.getExternalStorageDirectory(), "");
	        if (!exportDir.exists()) {
	            exportDir.mkdirs();
	        }
	        file = new File(exportDir, "ImportExport.csv");		
			try {
				CSVReader reader = new CSVReader(new FileReader(file));				
				try {
					while ((nextLine = reader.readNext()) != null) {						
					    // nextLine[] is an array of values from the line				   
					    iName=nextLine[0];
					    String read = nextLine[0].toString();
					    int len = read.length();
					    
					    // extracting the store name and location from a string
					    for(int i = 0; i < len; i++)
				    	{
				    		if(read.charAt(i) == '(')
				    		{
				    			sName = read.substring(0, i);
				    			lName = read.substring(i+1, len-2);
				    		}
				    	}
					    // inserting imported store name and location into database
					    if(importBtn.isPressed())
					    {					    		
					    	myDbHelper = new DataBaseHelper(this);
							database = myDbHelper.getWritableDatabase();
							myDbHelper.openDataBase();						
					    	database.execSQL("INSERT INTO StoreList (storeName, storeLocation) VALUES("
									+ "'" + sName + "'" + "," + "'" + lName + "'" + ");");	
					    	database.close();
					    	myDbHelper.close();					    	
					    	populateStoreSpinner();						    	
					    }
					    
					    if(iName.equalsIgnoreCase("Item Name"))
					    {
					    	
					    }
					    // inserts ImportExport.csv contents to Shopping and SortList tables
					    else
					    {
					    	listAdapter.notifyDataSetChanged();
					    	myDbHelper = new DataBaseHelper(this);
							database = myDbHelper.getWritableDatabase();
							myDbHelper.openDataBase();	
							database.execSQL("INSERT INTO SortList (itemName) VALUES("
										+ "'" + iName + "'" + ");");				
							database.execSQL("DELETE FROM SortList WHERE itemName Like '%(%'");	
					    	database.close();
					    	myDbHelper.close();						    	
					    }					    
					}
					startActivity(new Intent(this,
							ManageShoppingListItemsActivity.class));
					finish();
					storeItemsListView.setAdapter(listAdapter);
					createItemList();	
					Toast.makeText(getApplicationContext(), "Data inerted into table", Toast.LENGTH_LONG).show();
					// Add list of imported items to the store
					if(!itemList.isEmpty())
					{
						mySortList.addToStore(myDbHelper, storeName, storeLocation);
						storeItemsListView.setAdapter(listAdapter);
						listAdapter.notifyDataSetChanged();		
					}
								
				} catch (IOException e) {
					e.printStackTrace();
				}				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		// email ImportExport.csv file
		else if (v==sendMailBtn) {			
			
		}
	}
	/**
	 * ExportDatabaseCSVTask class which handles the exporting of the 
	 * database contents to the ImportExport.csv file
	 * @author Bertram Bishop
	 *
	 */
	private class ExportDatabaseCSVTask extends AsyncTask<String, Void, Boolean>{
	    private final ProgressDialog dialog = new ProgressDialog(ManageShoppingListItemsActivity.this);
	    
	    @Override
	    protected void onPreExecute() {
	        this.dialog.setMessage("Exporting database...");
	        this.dialog.show();
	    }
	    protected Boolean doInBackground(final String... args){
	      File dbFile=getDatabasePath("ShoppingCart.db");
	      Log.v(TAG, "Db path is: "+dbFile);  //get the path of db
	  
	        File exportDir = new File(Environment.getExternalStorageDirectory(), "");
	        if (!exportDir.exists()) {
	            exportDir.mkdirs();
	        }

	        file = new File(exportDir, "ImportExport.csv");
	        try {
	        	
	            file.createNewFile();
	            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
	            
	            // this is the Column of the table and same for Header of CSV file
	            //String arrStr1[] ={"Item Name","Store Name (Location)"};
	            String arrStr1[] ={storeName + "(" + storeLocation + ") "};
	            
           	    csvWrite.writeNext(arrStr1);
           	             	    
	            
	          
	            	for(int i=0; i < itemList.size(); i++)
	            	{
		            	 String item=itemList.get(i);
		            	 String arrStr[] ={item};
		            	 csvWrite.writeNext(arrStr);
	            	}
	            
	            
	            csvWrite.close();
	            return true;
	        }
	        catch (IOException e){
	            Log.e("ManageShoppingListItemsActivity", e.getMessage(), e);
	            return false;
	        }
	    }
	    /**
	     * Method handles the messages of the success or failure of the
	     * exporting process
	     */
	    @Override
	    protected void onPostExecute(final Boolean success)	{
	    	
	        if (this.dialog.isShowing()){
	            this.dialog.dismiss();
	        }
	        if (success && itemList.size() > 0){
	            Toast.makeText(ManageShoppingListItemsActivity.this, "Export successful!", Toast.LENGTH_SHORT).show();
	        }
	        else {
	            Toast.makeText(ManageShoppingListItemsActivity.this, "Export failed!", Toast.LENGTH_SHORT).show();
	        }
	    }
	}
	/**
	 * Method that handles going back to the home screen
	 * @param view
	 */
	public void homeButton(View view)
	{
		Intent refresh = new Intent(this, MainActivity.class);
		startActivity(refresh);
		this.finish();
	}
	
	/**
	 * Method that handles going back to the main screen
	 * when the back button is pressed
	 */
	public void onBack()
	{
		Intent myIntent = new Intent(ManageShoppingListItemsActivity.this, MainActivity.class);
		startActivity(myIntent);
		finish();
		return;
	}
}
