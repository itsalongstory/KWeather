package com.akasuna.kweather;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class SelectArea extends ListActivity implements OnItemClickListener {

	String[] no = null;
	String[] name = null;
	String[] position_no = null;
	String Level = "";
	String NewLevel = "";

	ArrayList<String> data = new ArrayList<String>();
	ArrayList<String> newdata = new ArrayList<String>();
	ListView lv;

	String ErrMSG = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		try {
			lv = getListView();
			data.add("Loading...");
			lv.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item, data));

			lv.setTextFilterEnabled(true);
			lv.setOnItemClickListener(this);

			Intent intent = getIntent();
			Level = intent.getStringExtra("NewLevel");
			String strno = intent.getStringExtra("no");

			if (Level == null) {
				Level = "";
			}

			String strSQLCommandText = "";

			if (Level.equalsIgnoreCase("")) {
				NewLevel = "c";
				strSQLCommandText = "select no,name from province";
			} else if (Level.equalsIgnoreCase("c")) {
				NewLevel = "a";
				strSQLCommandText = "select no,name from city where no like '" + strno + "%'";
			} else if (Level.equalsIgnoreCase("a")) {
				strSQLCommandText = "select no,name,position_no from area where no like '" + strno + "%'";
			} else {
				strSQLCommandText = "";
			}

			new AsyncLoader().execute(strSQLCommandText);
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.putExtra("no", no[arg2]);

		if (Level.equalsIgnoreCase("a")) {
			new AlertDialog.Builder(this)
			.setCancelable(false)
			.setTitle("设置")
			.setMessage("将所在地区设置为 " + name[arg2] + " ？")
			.setPositiveButton("是", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					DB db = DB.GetDB();
					db.execSQL("update settings set setvalue = '" + position_no[arg2] + "' where setkey = 'position_no'");
					db.close();
					Toast.makeText(getApplicationContext(), "正在设置...", Toast.LENGTH_LONG).show();
					String areano = Func.Check_position_no();
					if(areano.length() > 0) {
						new UpdateData(SelectArea.this).execute(getApplicationContext());
					}
					
				}
			})
			.setNegativeButton("否", null)
			.show();
		} else {
			intent.putExtra("NewLevel", NewLevel);
			intent.setClass(SelectArea.this, SelectArea.class);
			startActivity(intent);
		}
	}

	class AsyncLoader extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... sql) {
			// TODO Auto-generated method stub
			try {
				DB db = DB.GetDB();
				Cursor cursor = db.rawQuery(sql[0]);
				int count = cursor.getCount();
				if (count > 0) {
					no = new String[count];
					name = new String[count];
					if (Level.equalsIgnoreCase("a")) {
						position_no = new String[count];
					}
					int i = 0;
					for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
						byte byte_no[] = cursor.getBlob(0);
						byte byte_name[] = cursor.getBlob(1);
						try {
							no[i] = new String(byte_no, "utf-8").trim();
							name[i] = new String(byte_name, "utf-8").trim();
							if (Level.equalsIgnoreCase("a")) {
								position_no[i] = new String(cursor.getBlob(2), "utf-8").trim();
							}
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						newdata.add(name[i]);
						i++;
					}
				}
				cursor.close();
				db.close();
			} catch (Exception e) {
				ErrMSG = e.toString();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			lv.setAdapter(new ArrayAdapter<String>(SelectArea.this, R.layout.list_item, newdata));
			if (ErrMSG.length() > 0) {
				Toast.makeText(getApplicationContext(), ErrMSG, Toast.LENGTH_LONG).show();
			}
		}
	}
	
	class UpdateData extends AsyncTask<android.content.Context, Void, Void> {
		SelectArea a;
		private ProgressDialog progressDlg;
		UpdateData(SelectArea a) {
			this.a = a;
		}
		
		@Override
		protected Void doInBackground(Context... c) {
			// TODO Auto-generated method stub
			Func.SaveJSON();
			Func.UpdateWidget(c[0]);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			progressDlg.cancel();
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			
			progressDlg = new ProgressDialog(a);
			progressDlg.setMessage("正在设置...");
			progressDlg.setCancelable(false);
			progressDlg.show();
		}
	}
}
