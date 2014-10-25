package com.example.stockmarket;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	private AutoCompleteTextView symbolEditText;
	private Button searchButton, facebookButton, headlineButton;
	private String symbol;
	private TextView previousCloseText, openText, bidText, asktext,
			yearTargetText, dayRangeText, yearRangeText, volText, avgvoltext,
			marketcapText, nameSymbolText, lastTradedPriceText, changeText;
	private ImageView upOrDown, stockchartImage;
	private LinearLayout linearLayoutStock;
	private AsyncHttpClient client;
	private ArrayList<String> lsStringForAutocomplete, lsSymbol;
	private boolean symbolClick = false;
	private InputMethodManager imm;
	private String headline = "", facename = "", facelasttradedprice = "",
			facechange = "", facelink = "", facestockchart = "",
			facesymbol = "";
	Bitmap bm = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		client = new AsyncHttpClient();
		lsStringForAutocomplete = new ArrayList<String>();
		lsSymbol = new ArrayList<String>();
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		nameSymbolText = (TextView) findViewById(R.id.textViewNameAndSymbol);
		changeText = (TextView) findViewById(R.id.textViewChange);
		lastTradedPriceText = (TextView) findViewById(R.id.textViewLastTradedPrice);
		linearLayoutStock = (LinearLayout) findViewById(R.id.layoutStockInfo);
		upOrDown = (ImageView) findViewById(R.id.imageViewUpOrDown);
		stockchartImage = (ImageView) findViewById(R.id.imageViewStockChart);
		previousCloseText = (TextView) findViewById(R.id.textViewPrevClose);
		openText = (TextView) findViewById(R.id.textViewOpen);
		bidText = (TextView) findViewById(R.id.textViewBid);
		asktext = (TextView) findViewById(R.id.textViewAsk);
		yearTargetText = (TextView) findViewById(R.id.textViewYearTarget);
		dayRangeText = (TextView) findViewById(R.id.textViewdayrange);
		yearRangeText = (TextView) findViewById(R.id.textView52Range);
		volText = (TextView) findViewById(R.id.textViewVolume);
		avgvoltext = (TextView) findViewById(R.id.textViewAvgVol);
		marketcapText = (TextView) findViewById(R.id.textViewMarketCap);
		symbolEditText = (AutoCompleteTextView) findViewById(R.id.autocomplete_country);
		searchButton = (Button) findViewById(R.id.buttonSearch);
		facebookButton = (Button) findViewById(R.id.buttonFacebook);
		headlineButton = (Button) findViewById(R.id.buttonHeadline);
		searchButton.setOnClickListener(this);
		facebookButton.setOnClickListener(this);
		headlineButton.setOnClickListener(this);
		symbolEditText.setTextColor(Color.BLACK);

		symbolEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (!symbolClick)
					callSymbolAutocompleteAPI(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
	}

	// ON CLICK LISTENER ON BUTTONS
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonFacebook: {
			Intent intent = new Intent(MainActivity.this,
					FacebookActivity.class);
			intent.putExtra("facename", facename);
			intent.putExtra("facesymbol", facesymbol);
			intent.putExtra("facechange", facechange);
			intent.putExtra("facelasttradedprice", facelasttradedprice);
			intent.putExtra("facestockchart", facestockchart);
			intent.putExtra("facelink", facelink);
			startActivity(intent);

			break;
		}
		case R.id.buttonHeadline: {
			Intent intent = new Intent(MainActivity.this,
					HeadlineActivity.class);
			intent.putExtra("headline", headline);
			startActivity(intent);
			break;
		}
		case R.id.buttonSearch: {
			imm.hideSoftInputFromWindow(symbolEditText.getWindowToken(), 0);
			symbolEditText.setAdapter(null);
			if (!symbolClick)
				symbol = symbolEditText.getText().toString();
			if (symbol.length() == 0 || symbol.compareTo("") == 0
					|| symbol.compareTo(" ") == 0) {
				linearLayoutStock.setVisibility(View.GONE);
				Config.alertBox("Please enter the Company Name/Symbol",
						MainActivity.this);
			} else {
				callStockMarketHeadlineApi(symbol);
			}
			symbolClick = false;
			break;
		}
		}
	}

	// PARSE JSON FROM SERVELET AND SHOW STOCK INFO
	public void fillStockInfo(String json) {

		try {
			JSONObject obj = new JSONObject(json);
			JSONObject result = new JSONObject(obj.getString("result"));
			JSONObject quote = new JSONObject(result.getString("Quote"));

			facename = URLDecoder.decode(result.getString("Name"), "UTF-8");
			facesymbol = " (" + result.getString("Symbol") + ")";
			facelasttradedprice = quote.getString("LastTradePriceOnly");
			facechange = quote.getString("Change") + "("
					+ quote.getString("ChangeInPercent") + ")";
			facestockchart = result.getString("StockChartImageURL");
			facelink = "http://finance.yahoo.com/q?s="
					+ result.getString("Symbol");

			nameSymbolText.setText(facename + " (" + result.getString("Symbol")
					+ ")");

			lastTradedPriceText.setText(quote.getString("LastTradePriceOnly"));
			changeText.setText(quote.getString("Change") + "("
					+ quote.getString("ChangeInPercent") + ")");
			if (quote.getString("ChangeType").compareTo("-") == 0) {
				changeText.setTextColor(Color.RED);
				upOrDown.setImageResource(R.drawable.down_r);
			} else {
				changeText.setTextColor(Color.GREEN);
				upOrDown.setImageResource(R.drawable.up_g);
			}

			previousCloseText.setText(quote.getString("PreviousClose"));
			openText.setText(quote.getString("Open"));
			bidText.setText(quote.getString("Bid"));
			asktext.setText(quote.getString("Ask"));
			yearTargetText.setText(quote.getString("OneYearTargetPrice"));
			dayRangeText.setText(quote.getString("DaysLow") + "-"
					+ quote.getString("DaysHigh"));
			yearRangeText.setText(quote.getString("YearLow") + "-"
					+ quote.getString("YearHigh"));
			volText.setText(quote.getString("Volume"));
			avgvoltext.setText(quote.getString("AverageDailyVolume"));
			marketcapText.setText(quote.getString("MarketCapitalization"));
			AsyncClass asyncClass = new AsyncClass();
			asyncClass.execute(result.getString("StockChartImageURL"));
			linearLayoutStock.setVisibility(View.VISIBLE);

			JSONObject news = result.getJSONObject("News");
			JSONArray array = news.getJSONArray("Item");
			JSONObject item = array.getJSONObject(0);
			if (item.getString("Title").compareTo(
					"Yahoo! Finance: RSS feed not found") == 0) {
				headlineButton.setVisibility(View.GONE);
			} else {
				headlineButton.setVisibility(View.VISIBLE);
			}
			headline = array.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block

			JSONObject obj;
			try {
				obj = new JSONObject(json);
				JSONObject result = new JSONObject(obj.getString("result"));
				String error = result.getString("Error");
				if (error.compareTo("Stock Information Not Available") == 0) {
					imm.hideSoftInputFromWindow(
							symbolEditText.getWindowToken(), 0);
					linearLayoutStock.setVisibility(View.GONE);
					Config.alertBox("Stock Information Not available",
							MainActivity.this);

				}
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}

	}

	public class AsyncClass extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			URL newurl = null;
			try {
				newurl = new URL(params[0]);
				bm = BitmapFactory.decodeStream(newurl.openConnection()
						.getInputStream());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			stockchartImage.setImageBitmap(bm);
			stockchartImage.setVisibility(View.VISIBLE);
		}
	}

	// PARSE JSON ,FROM AUTOCOMPLETE AND SET AUTOCOMPLETE ADAPTER
	public void parseAutoCompleteJson(String arg) {
		lsStringForAutocomplete.clear();
		lsSymbol.clear();
		int count = "YAHOO.Finance.SymbolSuggest.ssCallback(".length();
		String json = arg.substring(count, arg.length() - 1);
		try {
			JSONObject obj = new JSONObject(json);
			JSONObject resultSet = obj.getJSONObject("ResultSet");
			JSONArray result = resultSet.getJSONArray("Result");
			for (int i = 0; i < result.length(); i++) {
				JSONObject set = result.getJSONObject(i);
				String name = set.getString("symbol") + ", "
						+ set.getString("name") + " (" + set.getString("exch")
						+ ")";
				lsStringForAutocomplete.add(name);
				lsSymbol.add(set.getString("symbol"));
			}
			final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					R.layout.dropdown, lsStringForAutocomplete);
			symbolEditText.setAdapter(adapter);
			symbolEditText.showDropDown();
			symbolEditText.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(
							symbolEditText.getWindowToken(), 0);
					symbol = lsSymbol.get(arg2);
					symbolClick = true;
					symbolEditText.setAdapter(null);
					symbolEditText.setText(lsSymbol.get(arg2));
					symbolEditText.setAdapter(adapter);
					searchButton.performClick();
				}
			});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// CALLING AUTOCOMPLETE YAHOO API
	public void callSymbolAutocompleteAPI(String sym) {
		try {
			String url = "http://autoc.finance.yahoo.com/autoc?query="
					+ URLEncoder.encode(sym)
					+ "&callback=YAHOO.Finance.SymbolSuggest.ssCallback";
			URL url1 = new URL(url);
			client.get(url1.toString(), new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(String arg0) {
					parseAutoCompleteJson(arg0);
					super.onSuccess(arg0);
				}

				@Override
				public void onFailure(Throwable arg0, String arg1) {
					// TODO Auto-generated method stub
					super.onFailure(arg0, arg1);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// CALLING SERVELET API FOR COMPLETE STOCK INFORMATION
	public void callStockMarketHeadlineApi(String sym) {
		String url = "http://cs-server.usc.edu:26928/examples/servlet/RequestInfoExample?symbol="
				+ URLEncoder.encode(sym);
		client.get(url, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);
				fillStockInfo(arg0);
			}

			@Override
			public void onFailure(Throwable arg0, String arg1) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1);
			}
		});

	}

}
