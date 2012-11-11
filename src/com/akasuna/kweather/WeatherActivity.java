package com.akasuna.kweather;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherActivity extends Activity {

	TextView tvArea = null;
	String strJSON = "";
	String strJSONExt = "";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		tvArea = (TextView) findViewById(R.id.tvArea);

		// Check if the database exists before copying
		try {
			boolean initialiseDatabase = (new File(Config.DB_PATH + Config.DB_NAME)).exists();
			if (initialiseDatabase == false) {
				CheckDBFile();
			}

			// Check_position_no();
			String areano = Func.Check_position_no();
			System.out.println("areano is " + areano);
			if (areano.length() > 0) {
				new UpdateData(this).execute(getApplicationContext());
			} else {
				Intent intent = new Intent();
				intent.setClass(WeatherActivity.this, SelectArea.class);
				startActivity(intent);
			}

		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
		}
	}

	private void CheckDBFile() {
		File f = new File(Config.DB_PATH);
		if (!f.exists()) {
			f.mkdir();
		}

		// Open the .db file in your assets directory
		try {
			InputStream is = getBaseContext().getAssets().open(Config.DB_NAME);
			// Copy the database into the destination
			OutputStream os = new FileOutputStream(Config.DB_PATH + Config.DB_NAME);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}

			os.flush();
			os.close();
			is.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);

		MenuInflater blowup = getMenuInflater();
		blowup.inflate(R.menu.menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		switch (item.getItemId()) {
		case R.id.about:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("关于").setMessage("本程序提供中国大陆及港澳台地区天气预报，数据源来自于气象局。\n\nlichao2012@gmail.com").setPositiveButton("知道了", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
				}

			}).show();
			break;
		case R.id.set:
			Intent intent = new Intent();
			intent.setClass(WeatherActivity.this, SelectArea.class);
			startActivity(intent);
			break;
		case R.id.update:
			String areano = Func.Check_position_no();
			if (areano.length() > 0) {
				new UpdateData(this).execute(getApplicationContext());
			}
		}

		return false;
	}

	private void setTextView() {
		String strJSON = "";
		String strTime = "";
		String strJSONExt = "";
		String strTimeExt = "";

		String[] s1 = Func.GetJSON(0);
		String[] s2 = Func.GetJSON(1);

		strJSON = s1[0];
		strTime = s1[1];
		strJSONExt = s2[0];
		strTimeExt = s2[1];

		tvArea.setText(strTime + "\n" + strJSON + "\n\n" + strTimeExt + "\n" + strJSONExt + "\n\n\n\n");
	}

	class UpdateData extends AsyncTask<android.content.Context, Void, Void> {
		WeatherActivity weatherActivity;
		private ProgressDialog progressDlg;

		UpdateData(WeatherActivity weatherActivity) {
			this.weatherActivity = weatherActivity;
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
			setTextView();
			progressDlg.cancel();
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			progressDlg = new ProgressDialog(weatherActivity);
			progressDlg.setMessage("正在刷新...");
			progressDlg.setCancelable(true);
			progressDlg.show();
		}
		
	}
}