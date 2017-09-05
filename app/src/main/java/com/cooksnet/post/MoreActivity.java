package com.cooksnet.post;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.cooksnet.obj.ImageTextItem;
import com.cooksnet.util.CooksNetWebAccess;

public class MoreActivity extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.more);

		setTitle(R.string.menu_more);

		ListView listView = (ListView) findViewById(R.id.moreMenu);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				Object obj = adapter.getItemAtPosition(position);
				if (obj instanceof ImageTextItem) {
					Intent i = new Intent(MoreActivity.this, HtmlWebViewActivity.class);
					switch (position) {
					case 0:
						startChildActivity(new Intent(MoreActivity.this, AccountActivity.class));
						break;
					case 1:
						i.putExtra("title", getText(R.string.menu_more_rule));
						i.putExtra("url", CooksNetWebAccess.URL_RULE);
						startChildActivity(i);
						break;
					case 2:
						i.putExtra("title", getText(R.string.menu_more_faq));
						i.putExtra("url", CooksNetWebAccess.URL_POST_FAQ);
						startChildActivity(i);
						break;
					case 3:
						startChildActivity(new Intent(MoreActivity.this, AboutActivity.class));
						break;
					case 4:
						setAllFinishActivity();
						finish();
						break;
					}
				}
			}
		});
		ImageTextArrayAdapter arrayAdapter = new ImageTextArrayAdapter(this, R.layout.more_list_item);
		arrayAdapter.add(new ImageTextItem(R.drawable.icon, getText(R.string.menu_more_account).toString()));
		arrayAdapter.add(new ImageTextItem(R.drawable.icon, getText(R.string.menu_more_rule).toString()));
		arrayAdapter.add(new ImageTextItem(R.drawable.icon, getText(R.string.menu_more_faq).toString()));
		arrayAdapter.add(new ImageTextItem(R.drawable.icon, getText(R.string.menu_more_about).toString()));
//		arrayAdapter.add(new ImageTextItem(R.drawable.icon, getText(R.string.menu_more_exit).toString()));
		listView.setAdapter(arrayAdapter);
	}

}
