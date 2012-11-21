package com.akasuna.kweather;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class WeatherWidget extends AppWidgetProvider {
	String strJSON = "";
	String strJSONExt = "";

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onDeleted(context, appWidgetIds);
		Toast.makeText(context, "拜拜", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onUpdate(context, appWidgetManager, appWidgetIds);

		new UpdateData(context).execute(context);
	}

	@Override
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		super.onEnabled(context);
		Toast.makeText(context, "Hello!", Toast.LENGTH_LONG).show();
		System.out.println("Widget onEnabled");
	}

	class UpdateData extends AsyncTask<android.content.Context, Void, Void> {
		android.content.Context content;

		UpdateData(android.content.Context c) {
			content = c;
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
			Toast.makeText(content, "widget updated!", Toast.LENGTH_LONG).show();
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			Toast.makeText(content, "widget updating!", Toast.LENGTH_LONG).show();
		}
	}
}
