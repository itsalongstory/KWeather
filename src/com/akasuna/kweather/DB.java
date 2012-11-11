package com.akasuna.kweather;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DB {
	
	private static DB db = new DB();
	
	private DB() {
		
	}
	
	public static DB GetDB() {
		return db;
	}
	
	private SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(Config.DB_PATH + Config.DB_NAME,null);
	
	// Execute SELECT statement
	public Cursor rawQuery(String SQLCommandText) {
		Cursor cursor = database.rawQuery(SQLCommandText, null);
		return cursor;
	}
	
	// Execute UPDATE statement
	public void execSQL(String SQLCommandText) {
		database.execSQL(SQLCommandText);
	}
	
	public void close() {
		//this.database.close();
	}
}
