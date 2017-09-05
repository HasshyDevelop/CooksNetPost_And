package com.cooksnet.post;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class EditRecipeGroupActivity extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.edit_recipe_group);

		setTitle(R.string.edit_recipe_textview_title);

		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.show((Fragment) fm.findFragmentById(R.id.editRecipeFragment));
		ft.hide((Fragment) fm.findFragmentById(R.id.editIngredientFragment));
		ft.hide((Fragment) fm.findFragmentById(R.id.editStepFragment));
		ft.commit();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_MENU:
			return true;
		case KeyEvent.KEYCODE_BACK:
			FragmentManager fm = getSupportFragmentManager();
			Fragment eif = (Fragment) fm.findFragmentById(R.id.editIngredientFragment);
			Fragment esf = (Fragment) fm.findFragmentById(R.id.editStepFragment);
			EditRecipeFragment erf = ((EditRecipeFragment) fm.findFragmentById(R.id.editRecipeFragment));
			if (eif.isVisible()) {
				FragmentTransaction ft = fm.beginTransaction();
				ft.show(erf);
				ft.hide(eif);
				ft.commit();
				return true;
			}
			if (esf.isVisible()) {
				FragmentTransaction ft = fm.beginTransaction();
				ft.show(erf);
				ft.hide(esf);
				ft.commit();
				return true;
			}

			SharedPreferences pref = getSharedPreferences(SHARED_NAME, MODE_PRIVATE);
			Editor e = pref.edit();
			e.putString("currentTab", "draft");
			e.commit();
			if (erf.isEditting) {
//				new AlertDialog.Builder(EditRecipeGroupActivity.this).setMessage(getText(R.string.dialog_back_alert))
//						.setPositiveButton(getText(R.string.dialog_ok), new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog, int whichButton) {
//								finish();
//							}
//						}).setNegativeButton(getText(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog, int whichButton) {
//							}
//						}).create().show();
				AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance("", getString(R.string.dialog_back_alert), "", "", 2, null, null);
				alertDialogFragment.setListener(new AlertDialogListener() {
					@Override
					public void doPositiveClick(int id) {
						finish();
					}

					@Override
					public void doNegativeClick(int id) {
					}
				});
				alertDialogFragment.show(getSupportFragmentManager(), "NoneTitle");
				return true;
			}
		default:
			return super.onKeyDown(keyCode, event);
		}
	}

	protected void onPhotoChanged(Bitmap bitmap, ImageView... photo) {
		FragmentManager fm = getSupportFragmentManager();
		EditRecipeFragment erf = ((EditRecipeFragment) fm.findFragmentById(R.id.editRecipeFragment));
		erf.setEditting(true);
		for (int i = 0; i < photo.length; i++) {
			Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);

			if (photo[i].getId() == R.id.photo) {
				int width = (int) (size.x * 0.5) - 20;
				int height = bitmap.getHeight() * width / bitmap.getWidth();
				photo[i].setLayoutParams(new LinearLayout.LayoutParams(width, height));
			} else {
				int width = (int) (size.x * 0.44f) - 20;
				int height = bitmap.getHeight() * width / bitmap.getWidth();
				photo[i].setLayoutParams(new LinearLayout.LayoutParams(width, height));
			}
		}
	}

}
