package com.database.meggnify.meggnet_users;

import java.util.ArrayList;
import java.util.List;

import com.database.meggnify.MySQLiteHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class MeggnetDataSource {
	// Database fields
	  private SQLiteDatabase database;
	  private MySQLiteHelper dbHelper;
	  private String[] allColumns = { 
		  	MySQLiteHelper.COLUMN_ID,
		  	MySQLiteHelper.COLUMN_NRIC,
		  	MySQLiteHelper.COLUMN_EMAIL,
		  	MySQLiteHelper.COLUMN_AUTH
		  };

	  public MeggnetDataSource(Context context) {
	    dbHelper = new MySQLiteHelper(context);
	  }

	  public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	  }

	  public void close() {
	    dbHelper.close();
	  }

	  public Meggnet createMeggnet(String email, String nric, String auth_token) {
	    ContentValues values = new ContentValues();
	    values.put(MySQLiteHelper.COLUMN_EMAIL, email);
	    values.put(MySQLiteHelper.COLUMN_NRIC, nric);
	    values.put(MySQLiteHelper.COLUMN_AUTH, auth_token);
	    long insertId = database.insert(MySQLiteHelper.TABLE_MEGGNETS, null,
	        values);
	    Cursor cursor = database.query(MySQLiteHelper.TABLE_MEGGNETS,
	        allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
	        null, null, null);
	    cursor.moveToFirst();
	    Meggnet newComment = cursorToMeggnet(cursor);
	    cursor.close();
	    return newComment;
	  }

	  public void deleteMeggnet(Meggnet meggnet) {
	    long id = meggnet.getId();
	    System.out.println("Comment deleted with id: " + id);
	    database.delete(MySQLiteHelper.TABLE_MEGGNETS, MySQLiteHelper.COLUMN_ID
	        + " = " + id, null);
	  }

	  public List<Meggnet> getAllMeggnets() {
	    List<Meggnet> meggnets = new ArrayList<Meggnet>();

	    Cursor cursor = database.query(MySQLiteHelper.TABLE_MEGGNETS,
	        allColumns, null, null, null, null, null);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	      Meggnet meggnet = cursorToMeggnet(cursor);
	      meggnets.add(meggnet);
	      cursor.moveToNext();
	    }
	    // make sure to close the cursor
	    cursor.close();
	    return meggnets;
	  }

	  private Meggnet cursorToMeggnet(Cursor cursor) {
	    Meggnet meggnet = new Meggnet();
	    meggnet.setID(cursor.getInt(0));
	    meggnet.setNRIC(cursor.getString(1));
	    meggnet.setEmail(cursor.getString(2));
	    meggnet.setToken(cursor.getString(3));
	    return meggnet;
	  }
}
