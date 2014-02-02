package com.example.shoppingcart;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Database helper class
 * @author http://stackoverflow.com/questions/18941404/how-to-access-and-query-the-database-that-is-copied-to-the-assets-folder
 */
public class DataBaseHelper extends SQLiteOpenHelper 
{
	private static String DB_NAME = "ShoppingCart.db";
	private String DB_PATH  = "";

	private static DataBaseHelper adb;
	private SQLiteDatabase db;
	private Context myContext;

	public DataBaseHelper(Context context)
	{   
		super(context, DB_NAME, null,1);

		this.myContext = context;
		DB_PATH = "/data/data/"+myContext.getPackageName()+"/databases/";
	}


	public static synchronized DataBaseHelper getAdapterInstance(Context context)
	{
		if(adb == null)
		{
			adb = new DataBaseHelper(context);
		}

		return adb;

	}


	public void createDatabase()
	{
		boolean dbExist = checkDataBase();
		if(dbExist)
		{


		}
		else
		{
			this.getReadableDatabase();
			try 
			{
				copyDataBase();
			} 
			catch (IOException e)
			{
				Log.v("log",e.toString());
				throw new Error("Error copying database");
			}

		}
	}

	private void copyDataBase() throws IOException
	{

		// Open your local db as the input stream
		InputStream myInput = myContext.getAssets().open(DB_NAME);

		// Path to the just created empty db
		String outFileName = DB_PATH + DB_NAME;

		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);

		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) 
		{
			myOutput.write(buffer, 0, length);
		}
		Log.v("log", "copy finish");
		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	private boolean checkDataBase() 
	{
		SQLiteDatabase checkDB = null;
		try 
		{
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY);
		}
		catch (SQLiteException e) 
		{
		}
		if (checkDB != null)
		{
			checkDB.close();
		}
		return checkDB != null ? true : false;
	}


	public SQLiteDatabase openDataBase() throws SQLException
	{
		// Open the database
		String myPath = DB_PATH + DB_NAME;

		db= SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READWRITE);

		return db;
	}



	@Override
	public void onCreate(SQLiteDatabase db)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		// TODO Auto-generated method stub

	}
	@Override
	public synchronized void close() {

		if(db != null)
			db.close();

		super.close();

	}
}