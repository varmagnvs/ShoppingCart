package com.example.shoppingcart;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * UI class for the home screen
 * ITEC 4860-01 Project 2013
 * @author David Anderson, Marin Rangelov, Bertram Bishop, Hoang Nhan
 * 
 * Shopping Cart application designed for the 
 * creating, managing, and sorting of shopping items.
 */
public class MainActivity extends Activity
{
	private ArrayList<String> listOfItems;
	private ShoppingList myShoppingList;
	private DataBaseHelper myDbHelper;
	
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);	
		
		createStartupLogo(); 
		        
        // Creates database on startup
        DataBaseHelper myDbHelper = new DataBaseHelper(this);              
        myDbHelper.createDatabase();	          
	}

	
	/**
	 * Method for creating a store
	 * Go to create a store activity upon button press
	 * @param v view
	 */
	public void createStore(View v) 
	{		
		startActivity(new Intent(MainActivity.this, CreateStoreActivity.class));                               
	}	
	

	/**
	 * Method for managing store
	 * Go to manage store activity upon button press
	 * @param v view
	 */
	public void manageStores(View v)
	{
		startActivity(new Intent(MainActivity.this, ListOfStoresActivity.class));  		
	}
	
	/**
	 * Method for managing store
	 * Go to manage store activity upon button press
	 * @param v view
	 */
	public void importExportList(View v)
	{
		startActivity(new Intent(MainActivity.this, ManageShoppingListItemsActivity.class));  		
	}
	
	
	/**
	 * Method for creating a shopping list
	 * Go to create shopping list activity upon button press
	 * @param v view
	 */
	public void createList(View v)
	{
		startActivity(new Intent(MainActivity.this, CreateShoppingListActivity.class));
	}
	
	
	/**
	 * Method for managing a shopping list
	 * Go to manage shopping list activity upon button press
	 * @param v view
	 */
	public void manageList(View v)
	{
		startActivity(new Intent(MainActivity.this, ManageShoppingListActivity.class)); 
		finish();
	}
	
	
	/**
	 * Method handles creating the logo
	 */
	public void createStartupLogo()
	{
        ImageView cartImgView = (ImageView) findViewById(R.id.shoppingCartImgView);        
        int density= getResources().getDisplayMetrics().densityDpi;
        	       
        // Below if statements handle resizing image depending on screen device
        if  (density == DisplayMetrics.DENSITY_LOW)
        {
        	cartImgView.setImageResource(R.drawable.shopping_cart_img);        	
        	cartImgView.setVisibility(View.GONE); 
        }
        
        else if  (density == DisplayMetrics.DENSITY_MEDIUM)
        {
        	cartImgView.setImageResource(R.drawable.shopping_cart_img);
        	scaleImage(cartImgView, 100);
        }
       
        else if  (density == DisplayMetrics.DENSITY_HIGH)
        {
        	cartImgView.setImageResource(R.drawable.shopping_cart_img);
        	scaleImage(cartImgView, 200);
        }
       
        else if  (density == DisplayMetrics.DENSITY_XHIGH)
        {
        	cartImgView.setImageResource(R.drawable.shopping_cart_img);
        	 scaleImage(cartImgView, 100);
        } 
	}
	
	/**
	 * Method handles scaling the image shown on the home screen
	 * @param view
	 * @param boundBoxInDp
	 * Source: http://argillander.wordpress.com/2011/11/24/scale-image-into-imageview-then-resize-imageview-to-match-the-image/
	 */
	private void scaleImage(ImageView view, int boundBoxInDp)
	{
	    // Get the ImageView and its bitmap
	    Drawable drawing = view.getDrawable();
	    Bitmap bitmap = ((BitmapDrawable)drawing).getBitmap();

	    // Get current dimensions
	    int width = bitmap.getWidth();
	    int height = bitmap.getHeight();

	    // Determine how much to scale: the dimension requiring less scaling is
	    // closer to the its side. This way the image always stays inside your
	    // bounding box AND either x/y axis touches it.
	    float xScale = ((float) boundBoxInDp) / width;
	    float yScale = ((float) boundBoxInDp) / height;
	    float scale = (xScale <= yScale) ? xScale : yScale;

	    // Create a matrix for the scaling and add the scaling data
	    Matrix matrix = new Matrix();
	    matrix.postScale(scale, scale);

	    // Create a new bitmap and convert it to a format understood by the ImageView
	    Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
	    BitmapDrawable result = new BitmapDrawable(scaledBitmap);
	    width = scaledBitmap.getWidth();
	    height = scaledBitmap.getHeight();

	    // Apply the scaled bitmap
	    view.setImageDrawable(result);

	    // Now change ImageView's dimensions to match the scaled image
	    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
	    params.width = width;
	    params.height = height;
	    view.setLayoutParams(params);
	}

	/**
	 * Method converts dp to pixels
	 * @param dp
	 * @return
	 * Source: http://argillander.wordpress.com/2011/11/24/scale-image-into-imageview-then-resize-imageview-to-match-the-image/
	 */
	private int dpToPx(int dp)
	{
	    float density = getApplicationContext().getResources().getDisplayMetrics().density;
	    return Math.round((float)dp * density);
	}	
}