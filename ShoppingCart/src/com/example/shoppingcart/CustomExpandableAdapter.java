package com.example.shoppingcart;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class CustomExpandableAdapter extends BaseExpandableListAdapter 
{
	private Typeface tfBold;
	private Context context;

	String[] locations;	
	String[][] itemNames;

	public CustomExpandableAdapter(Context context, ArrayList<String> locationsArrayList, ArrayList<ArrayList<String>> itemNamesArrayList) 
	{
		this.context = context;		
		locations = locationsArrayList.toArray(new String[locationsArrayList.size()]);	

		itemNames = new String[itemNamesArrayList.size()][];

		for (int i = 0; i < itemNamesArrayList.size(); i++) 
		{
			ArrayList<String> row = itemNamesArrayList.get(i);
			itemNames[i] = row.toArray(new String[row.size()]);
		}		
	}

	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) 
	{
		TextView tv = new TextView(context);
		tv.setText(itemNames[groupPosition][childPosition]);
		tv.setPadding(80, 0, 0, 10);
		tv.setTextSize(16);
		tv.setTextColor(Color.BLACK);
		return tv;
	}

	public View getGroupView(int groupPosition, boolean isExpanded, View conVertView, ViewGroup parent) 
	{
		TextView tv = new TextView(context);
		tv.setText(locations[groupPosition]);
		tv.setTextSize(20);
		tv.setTypeface(tfBold);
		tv.setPadding(50, 0, 0, 20);
		return tv;
	}


	public Object getChild(int arg0, int arg1) 
	{
		return null;
	}

	public long getChildId(int arg0, int arg1) 
	{	
		return 0;
	}



	public int getChildrenCount(int groupPosition) 
	{
		return itemNames[groupPosition].length;
	}

	public Object getGroup(int groupPosition) 
	{
		return groupPosition;
	}

	public int getGroupCount() 
	{
		return locations.length;
	}

	public long getGroupId(int groupPosition) 
	{
		return groupPosition;
	}

	public boolean hasStableIds() 
	{
		return false;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) 
	{
		return false;
	}
}