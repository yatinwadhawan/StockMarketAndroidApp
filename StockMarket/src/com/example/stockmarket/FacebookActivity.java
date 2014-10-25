package com.example.stockmarket;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class FacebookActivity extends Activity {
	private static final String URL_PREFIX_FRIENDS = "https://graph.facebook.com/me/friends?access_token=";

	private TextView textInstructionsOrLink;
	private Button buttonLoginLogout;
	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	private UiLifecycleHelper uiHelper;
	private String facename = "", facelasttradedprice = "", facechange = "",
			facelink = "", facestockchart = "", facesymbol = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(this, new SessionStatusCallback());
		uiHelper.onCreate(savedInstanceState);
		setContentView(R.layout.activity);
		buttonLoginLogout = (Button) findViewById(R.id.buttonLoginLogout);
		textInstructionsOrLink = (TextView) findViewById(R.id.instructionsOrLink);

		facename = getIntent().getExtras().getString("facename");
		facelasttradedprice = getIntent().getExtras().getString(
				"facelasttradedprice");
		facechange = getIntent().getExtras().getString("facechange");
		facesymbol = getIntent().getExtras().getString("facesymbol");
		facestockchart = getIntent().getExtras().getString("facestockchart");
		facelink = getIntent().getExtras().getString("facelink");

		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

		Session session = Session.getActiveSession();
		if (session == null) {
			if (savedInstanceState != null) {
				session = Session.restoreSession(this, null, statusCallback,
						savedInstanceState);
			}
			if (session == null) {
				session = new Session(this);
			}
			Session.setActiveSession(session);
			if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
				session.openForRead(new Session.OpenRequest(this)
						.setCallback(statusCallback));
			}
		}
		// updateView();
		onClickLogin();

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onStart() {
		super.onStart();
		Session.getActiveSession().addCallback(statusCallback);
	}

	@Override
	public void onStop() {
		super.onStop();
		Session.getActiveSession().removeCallback(statusCallback);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
		publishFeedDialog();
		uiHelper.onActivityResult(requestCode, resultCode, data,
				new FacebookDialog.Callback() {
					@Override
					public void onError(FacebookDialog.PendingCall pendingCall,
							Exception error, Bundle data) {

					}

					@Override
					public void onComplete(
							FacebookDialog.PendingCall pendingCall, Bundle data) {
						publishFeedDialog();

					}
				});
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
		Session session = Session.getActiveSession();
		Session.saveSession(session, outState);

	}

	private void updateView() {
		Session session = Session.getActiveSession();
		if (session.isOpened()) {
			publishFeedDialog();
			// textInstructionsOrLink.setText(URL_PREFIX_FRIENDS
			// + session.getAccessToken());
		} else {
			// buttonLoginLogout.setText("Login");
			// buttonLoginLogout.setOnClickListener(new OnClickListener() {
			// public void onClick(View view) {
			// onClickLogin();
			// }
			// });
		}
	}

	private void onClickLogin() {
		Session session = Session.getActiveSession();
		if (!session.isOpened() && !session.isClosed()) {

			session.openForRead(new Session.OpenRequest(this)
					.setCallback(statusCallback));
			if (session.isOpened()) {
				publishFeedDialog();

			}
		} else {
			Session.openActiveSession(this, true, statusCallback);
			if (session.isOpened()) {
				publishFeedDialog();
			}
		}
	}

	private void onClickLogout() {
		Session session = Session.getActiveSession();
		if (!session.isClosed()) {
			session.closeAndClearTokenInformation();
		}
	}

	private class SessionStatusCallback implements Session.StatusCallback {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			updateView();
		}
	}

	private void publishFeedDialog() {
		Bundle params = new Bundle();
		params.putString("name", facename);
		params.putString("caption", "Stock Information of " + facename + " "
				+ facesymbol);
		params.putString("description", "Last Trade Price:"
				+ facelasttradedprice + ",Change:" + facechange);
		params.putString("link", facelink);
		params.putString("picture", facestockchart);

		WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(
				FacebookActivity.this, Session.getActiveSession(), params))
				.setOnCompleteListener(new OnCompleteListener() {

					public void onComplete(Bundle values,
							FacebookException error) {
						// TODO Auto-generated method stub
						if (error == null) {
							// When the story is posted, echo the success
							// and the post Id.
							final String postId = values.getString("post_id");
							if (postId != null) {
								Toast.makeText(FacebookActivity.this,
										"Posted story, id: " + postId,
										Toast.LENGTH_SHORT).show();
								finish();
							} else {
								// User clicked the Cancel button
								Toast.makeText(FacebookActivity.this,
										"Publish cancelled", Toast.LENGTH_SHORT)
										.show();
								finish();
							}
						} else if (error instanceof FacebookOperationCanceledException) {
							// User clicked the "x" button
							Toast.makeText(FacebookActivity.this,
									"Publish cancelled", Toast.LENGTH_SHORT)
									.show();
							finish();
						} else {
							// Generic, ex: network error
							Toast.makeText(FacebookActivity.this,
									"Error posting story", Toast.LENGTH_SHORT)
									.show();
							finish();
						}

					}

				}).build();
		feedDialog.show();
	}

}