package com.example.stockmarket;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class HeadlineActivity extends Activity implements OnItemClickListener {

	private ListView listView;
	private ArrayList<String> titleList, linkList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.headline);
		listView = (ListView) findViewById(R.id.listViewHeadline);
		titleList = new ArrayList<String>();
		linkList = new ArrayList<String>();

		String json = getIntent().getExtras().getString("headline");
		try {
			JSONArray array = new JSONArray(json);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				titleList.add(obj.getString("Title"));
				linkList.add(obj.getString("Link"));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Config.toastShow("Showing " + titleList.size() + " headlines", this);
		listView.setOnItemClickListener(this);
		listView.setAdapter(new ListViewAdapter());

	}

	private class ListViewAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return titleList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			TextView text = new TextView(HeadlineActivity.this);
			text.setText(titleList.get(arg0));
			text.setMinHeight(40);
			return text;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		// call the dialog box for view and cancel
		AlertDialog.Builder alert = new AlertDialog.Builder(
				HeadlineActivity.this);
		alert.setTitle("View News");
		String[] str = new String[2];
		str[0] = "View";
		str[1] = "Cancel";
		alert.setItems(str, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if (which == 0) {
					Uri uriUrl = Uri.parse(linkList.get(arg2));
					Intent launchBrowser = new Intent(Intent.ACTION_VIEW,
							uriUrl);
					startActivity(launchBrowser);
				} else {

				}
			}
		});
		// TODO Auto-generated method stub
		// Intent myIntent = new Intent(HeadlineActivity.this,
		// WebBrowserActivity.class);
		// myIntent.putExtra("url", linkList.get(arg2));
		// startActivity(myIntent);

		alert.show();
	}
}
