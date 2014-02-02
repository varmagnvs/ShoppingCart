package com.example.shoppingcart;

import java.util.ArrayList;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

/**
 * UI Class for Sorting Items List.
 * @author David Anderson, Hoang Nhan
 *
 */
public class SortedItemsActivity extends Activity
{			
	private ExpandableListView sortedItems;
	
	
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sorted_items);
				
		sortedItems = (ExpandableListView) findViewById(R.id.sortedExpandableListView);
		TextView textView = new TextView(SortedItemsActivity.this);
		textView.setTypeface(null, Typeface.BOLD);
		textView.setText("Sorted List");
		textView.setTextSize(15);
		sortedItems.addHeaderView(textView, null, false);
		
		getSortedList();		
	}	
		
	public void onResume()
	{
		super.onResume();
		getSortedList();
	}	
	
	/**
	 * Method handles home screen button
	 * @param v view
	 */
	public void mainWindow(View v)
	{	
		startActivity(new Intent(SortedItemsActivity.this, MainActivity.class));		
	}
	

	/**
	 * Sorts items in shopping list
	 */
	public void getSortedList()
	{			
		SortList mySortList = new SortList();
		DataBaseHelper myDbHelper = new DataBaseHelper(this);
		
		ArrayList<String> locations = mySortList.getLocations(myDbHelper);	
		
		ArrayList<ArrayList<String>> itemNames2D = mySortList.getItemNames2D(myDbHelper);
		
		CustomExpandableAdapter myAdapter = new CustomExpandableAdapter(this, locations, itemNames2D);		
		sortedItems.setAdapter(myAdapter);
		
		// Expands children by default
		int count = myAdapter.getGroupCount();
		for (int i = 1; i <= count; i++)
			sortedItems.expandGroup(i - 1);		
	}	
}