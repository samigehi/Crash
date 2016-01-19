package com.sumeet.crash;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * @author sumeet.kumar
 */
public class ErrorActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.error_activity);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.app_name);
		setSupportActionBar(toolbar);

		Button email = (Button) findViewById(R.id.mail);
		Button restart = (Button) findViewById(R.id.restart);

		email.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendEmail();
			}
		});
		restart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Crash.restart(ErrorActivity.this);
			}
		});

		ImageView image = (ImageView) findViewById(R.id.error_image);
		image.setColorFilter(Color.GRAY);
	}

	private void sendEmail() {
		try {
			Intent intent = new Intent(Intent.ACTION_SEND);
			// intent.setData(Uri.fromParts("mailto", ACRA.getConfig().mailTo(),
			// null));
			intent.setType("message/rfc822");
			// intent.setData(Uri.fromParts("mailto", Crash.EMAIL, null));
			intent.putExtra(Intent.EXTRA_EMAIL, new String[] { Crash.EMAIL });
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra(Intent.EXTRA_SUBJECT, Crash.SUBJECT);
			intent.putExtra(Intent.EXTRA_TEXT, Crash.getErrorDetails(ErrorActivity.this, getIntent()));
			startActivity(intent);
			toast("Thankyou for your feedback!");
		} catch (Exception e) {
			e.printStackTrace();
			toast("No Mail Client installed!");
			Crash.restart(this);
			finish();
		}
	}

	void toast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
}
