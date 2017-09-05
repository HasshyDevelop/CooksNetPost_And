package com.cooksnet.post;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.cooksnet.obj.Recipe;
import com.cooksnet.obj.ResultItem;

public class DraftFragment extends SearchFragment {

	private Handler handler;

	boolean alreadySearch = false;

	public static DraftFragment newInstance() {
		return new DraftFragment();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		if (null != v) {
			ViewGroup parent = (ViewGroup) v.getParent();
//			parent.removeView(v);
			return v;
		}
		v = inflater.inflate(R.layout.draft, null, true);

		handler = new Handler();

		// childLayout = (LinearLayout) findViewById(R.id.child);

		Button createRecipe = (Button) v.findViewById(R.id.createRecipe);
		createRecipe.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((BaseActivity) a).startChildActivity(new Intent(a, CreateRecipeActivity.class));
			}
		});

		ListView listView = (ListView) v.findViewById(R.id.resultList);
		arrayAdapter = new ResultArrayAdapter(a, R.layout.result_list_item, handler);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				ResultItem item = arrayAdapter.getItem(position);
				if (ResultItem.MORE == item) {
					canSearch = false;
					new SearchThread(handler, a, false).execute(false);
				} else {
					Map<String, Serializable> param = new HashMap<String, Serializable>();
					param.put("id", item.id);
					((BaseActivity) a).startChildActivity(new Intent(a, EditRecipeGroupActivity.class), param);
				}
			}
		});
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
//				new AlertDialog.Builder(a).setMessage(getText(R.string.dialog_delete_alert))
//						.setPositiveButton(getText(R.string.dialog_ok), new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog, int whichButton) {
//								final ResultItem item = arrayAdapter.getItem(position);
//								Recipe delete = new Recipe();
//								delete.id = item.id;
//
//								page = 0;
//								canSearch = false;
//								new DeleteThread(handler, a, delete).execute(false);
//							}
//						}).setNegativeButton(getText(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int whichButton) {
//					}
//				}).create().show();
				AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance("", getString(R.string.dialog_delete_alert), "", "", 2, null, null);
				alertDialogFragment.setListener(new AlertDialogListener() {
					@Override
					public void doPositiveClick(int id) {
						final ResultItem item = arrayAdapter.getItem(position);
						Recipe delete = new Recipe();
						delete.id = item.id;

						page = 0;
						canSearch = false;
						new DeleteThread(handler, a, delete).execute(false);
					}

					@Override
					public void doNegativeClick(int id) {
					}
				});
				alertDialogFragment.show(a.getSupportFragmentManager(), "NoneTitle");
				return true;
			}
		});
		listView.setAdapter(arrayAdapter);
		listView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (totalItemCount == firstVisibleItem + visibleItemCount) {
					if (existMore && canSearch) {
						canSearch = false;
						new SearchThread(handler, a, false).execute(false);
					}
				}
			}
		});

		return v;
	}

	@Override
	protected void doAfterSearch() {
		LinearLayout parent = (LinearLayout) v.findViewById(R.id.parent);
		LinearLayout child = (LinearLayout) v.findViewById(R.id.child);
		if (result.items.size() == 0) {
			parent.setVisibility(View.GONE);
			child.setVisibility(View.VISIBLE);
		} else {
			parent.setVisibility(View.VISIBLE);
			child.setVisibility(View.GONE);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		LinearLayout parent = (LinearLayout) v.findViewById(R.id.parent);
		LinearLayout child = (LinearLayout) v.findViewById(R.id.child);
		SharedPreferences pref = a.getSharedPreferences(BaseActivity.SHARED_NAME, a.MODE_PRIVATE);

		if (!pref.getBoolean("tabclick", false) || !alreadySearch) {
			parent.setVisibility(View.GONE);
			child.setVisibility(View.GONE);
			page = 0;
			canSearch = false;
			new SearchThread(handler, a, true).execute(false);
			alreadySearch = true;
		}
		Editor e = pref.edit();
		e.putBoolean("tabclick", false);
		e.commit();
	}

}