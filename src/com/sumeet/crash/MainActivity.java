package com.sumeet.crash;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.app_name);
		setSupportActionBar(toolbar);

		(findViewById(R.id.button_crash)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Integer.parseInt("sumit");
			}
		});
	}
}
