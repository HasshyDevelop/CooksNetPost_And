package com.cooksnet.post;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

public class AccountActivity extends BaseActivity {

	// private Handler handler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.account);

		setTitle(R.string.menu_more_account);

		// handler = new Handler();

		((Button) findViewById(R.id.logout)).setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				SharedPreferences pref = getSharedPreferences(SHARED_NAME, MODE_PRIVATE);
				Editor e = pref.edit();
				e.putString("nickname", "");
				e.putString("password", "");
				e.putString("currentTab", "");
				e.commit();
				setAllFinishActivity();
				startChildActivity(new Intent(AccountActivity.this, LoginActivity.class));
				finish();
			}

		});

		((ToggleButton) findViewById(R.id.save)).setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton b, boolean check) {
				SharedPreferences pref = getSharedPreferences(SHARED_NAME, MODE_PRIVATE);
				Editor e = pref.edit();
				e.putBoolean("save", check);
				e.commit();
			}

		});

		SharedPreferences pref = getSharedPreferences(SHARED_NAME, MODE_PRIVATE);
		final String nickname = pref.getString("nickname", "");
		boolean save = pref.getBoolean("save", true);

		((TextView) findViewById(R.id.nickname)).setText(nickname);
		((ToggleButton) findViewById(R.id.save)).setChecked(save);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		return false;
	}

}
