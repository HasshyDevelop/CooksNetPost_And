package com.cooksnet.post;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class BaseFragment extends Fragment {

	protected AppCompatActivity a;
	public View v;

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		this.a = (AppCompatActivity) context;
	}

}
