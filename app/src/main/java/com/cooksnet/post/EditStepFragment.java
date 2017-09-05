package com.cooksnet.post;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.cooksnet.util.CustomDialog;
import com.cooksnet.util.MultibyteLengthFilter;

public class EditStepFragment extends BaseFragment {

	public final MultibyteLengthFilter stepTextLengthFilter = new MultibyteLengthFilter(90, 60, 240,
			ExtrasData.profile.locale);

	public EditRecipeFragment erf;

	private TableLayout stepsE;
	private ImageButton sEdit, sDone;
	private ImageView sAdd;

	private TableLayout steps;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		v = inflater.inflate(R.layout.edit_step, container, true);

		FragmentManager fm = getFragmentManager();
		erf = (EditRecipeFragment) fm.findFragmentById(R.id.editRecipeFragment);

		stepsE = (TableLayout) v.findViewById(R.id.steps);

		sEdit = (ImageButton) v.findViewById(R.id.edit);
		sDone = (ImageButton) v.findViewById(R.id.done);
		sAdd = (ImageView) v.findViewById(R.id.stepAdd);

		sEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				erf.isPushedEditButton = true;
				setLineEdit(sEdit, sDone, sAdd, stepsE, R.id.stepAdd, R.id.stepDel, R.id.up, R.id.down, true);
			}
		});
		sDone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				erf.isPushedEditButton = false;
				setLineEdit(sEdit, sDone, sAdd, stepsE, R.id.stepAdd, R.id.stepDel, R.id.up, R.id.down, false);
			}
		});
		sAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (EditRecipeFragment.MAX_COUNT_STEP == stepsE.getChildCount()) {
//					new AlertDialog.Builder(a).setTitle(R.string.dialog_step_count_over)
//							.setPositiveButton(R.string.dialog_ok, null).create().show();
					AlertDialogFragment.newInstance(getString(R.string.dialog_step_count_over), "", "")
							.show(a.getSupportFragmentManager(), "NoneTitle");
					return;
				}
				stepsE.addView(createStepE(stepsE, !erf.isPushedEditButton));
				erf.setEditting(true);
				refleshStepNumber(stepsE);
			}
		});

		final Button end = ((Button) v.findViewById(R.id.end));
		end.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				((EditStepFragment) fm.findFragmentById(R.id.editStepFragment)).preHide();
				ft.show((Fragment) fm.findFragmentById(R.id.editRecipeFragment));
				ft.hide((Fragment) fm.findFragmentById(R.id.editStepFragment));
				ft.commit();
				erf.isPushedEditButton = false;
				InputMethodManager inputMethodManager = (InputMethodManager) a.getSystemService(Context.INPUT_METHOD_SERVICE);
				if (null != inputMethodManager && null != a.getCurrentFocus()) {
					inputMethodManager.hideSoftInputFromWindow(a.getCurrentFocus().getWindowToken(), 0);
				}
			}
		});
		return v;
	}

	public void preShow(TableLayout steps) {
		this.steps = steps;

		stepsE.removeAllViews();
		int count = 0;
		for (int i = 0; i < steps.getChildCount(); i++) {
			View step = steps.getChildAt(i);
			if (null == ((TextView) step.findViewById(R.id.text))) {
				continue;
			}
			++count;

			View stepE = createStepE(stepsE, true);

			TextView numberE = (TextView) stepE.findViewById(R.id.number);
			TextView textE = (TextView) stepE.findViewById(R.id.text);
			ImageView stepPhotoE = (ImageView) stepE.findViewById(R.id.stepPhoto);
			ImageView stepPhotoDefaultE = (ImageView) stepE.findViewById(R.id.stepPhotoDefault);
			Bitmap stepPhotoDefaultEBmp = ((BitmapDrawable) stepPhotoDefaultE.getDrawable()).getBitmap();

			numberE.setText("" + count);
			textE.setText(((TextView) step.findViewById(R.id.text)).getText().toString().trim());

			Bitmap stepPhotoBmp = ((BitmapDrawable) ((ImageView) step.findViewById(R.id.stepPhoto)).getDrawable())
					.getBitmap();
			Bitmap stepPhotoDefaultBmp = ((BitmapDrawable) ((ImageView) step.findViewById(R.id.stepPhotoDefault))
					.getDrawable()).getBitmap();
			if (stepPhotoBmp != stepPhotoDefaultBmp) {
				Display display = ((WindowManager) a.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//				int width = (int) (display.getWidth() * 0.44) - 20;
				Point size = new Point();
				display.getSize(size);
				int width = (int) (size.x * 0.44f) - 20;
				int height = stepPhotoBmp.getHeight() * width / stepPhotoBmp.getWidth();
				stepPhotoE.setLayoutParams(new LinearLayout.LayoutParams(width, height));
				stepPhotoE.setImageBitmap(stepPhotoBmp);
				stepPhotoE.setTag(step.findViewById(R.id.stepPhoto).getTag());
			} else {
				stepPhotoE.setImageBitmap(stepPhotoDefaultEBmp);
				stepPhotoE.setTag(null);
			}
			stepsE.addView(stepE);
		}
		if (0 == stepsE.getChildCount()) {
			stepsE.addView(createStepE(stepsE, true));
		}
		setLineEdit(sEdit, sDone, sAdd, stepsE, R.id.stepAdd, R.id.stepDel, R.id.up, R.id.down, false);
	}

	public void preHide() {
		steps.removeAllViews();
		int count = 0;
		for (int i = 0; i < stepsE.getChildCount(); i++) {
			View stepE = stepsE.getChildAt(i);
			TextView textE = (TextView) stepE.findViewById(R.id.text);
			ImageView stepPhotoE = (ImageView) stepE.findViewById(R.id.stepPhoto);
			ImageView stepPhotoDefaultE = (ImageView) stepE.findViewById(R.id.stepPhotoDefault);

			TableRow step = (TableRow) a.getLayoutInflater().inflate(R.layout.step_table_row, null, true);
			TextView number = (TextView) step.findViewById(R.id.number);
			number.setText("" + (count + 1));
			TextView text = (TextView) step.findViewById(R.id.text);
			text.setText(trimCrlfSpace(textE.getText().toString().trim()));

			Bitmap stepPhotoBmp = ((BitmapDrawable) stepPhotoE.getDrawable()).getBitmap();
			Bitmap d = ((BitmapDrawable) stepPhotoDefaultE.getDrawable()).getBitmap();
			if (d != stepPhotoBmp) {
				ImageView stepPhoto = (ImageView) step.findViewById(R.id.stepPhoto);

				Display display = ((WindowManager) a.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//				int width = (int) (display.getWidth() * 0.44) - 20;
				Point size = new Point();
				display.getSize(size);
				int width = (int) (size.x * 0.44f) - 20;
				int height = stepPhotoBmp.getHeight() * width / stepPhotoBmp.getWidth();
				stepPhoto.setLayoutParams(new LinearLayout.LayoutParams(width, height));
				stepPhoto.setImageBitmap(stepPhotoBmp);
				stepPhoto.setTag((Uri) stepPhotoE.getTag());
				stepPhoto.setVisibility(View.VISIBLE);
			} else {
				TableRow.LayoutParams params = new TableRow.LayoutParams();
				params.span = 2;
				text.setLayoutParams(params);
			}
			steps.addView(step);
			++count;

			if (i + 1 != stepsE.getChildCount()) {
				TableRow line = (TableRow) a.getLayoutInflater().inflate(R.layout.line_table_row_3, null);
				steps.addView(line);
			}
		}
		if (0 == count) {
			TableRow step = (TableRow) a.getLayoutInflater().inflate(R.layout.step_table_row, null, true);
			TextView number = (TextView) step.findViewById(R.id.number);
			number.setHint(getText(R.string.edit_recipe_textview_hint_step));
			steps.addView(step);
		}
	}

	private View createStepE(final TableLayout stepsE, boolean isTop) {
		final TableRow stepE = (TableRow) a.getLayoutInflater().inflate(R.layout.edit_step_table_row, null, true);

//		LinearLayout stepTextView = (LinearLayout) ((LayoutInflater) a
//				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dialog_message, null, false);
//		final EditText stepTextE = (EditText) stepTextView.findViewById(R.id.message);
//		stepTextE.setFilters(new InputFilter[] { stepTextLengthFilter });
		final TextView textE = (TextView) stepE.findViewById(R.id.text);
//		final AlertDialog textDialog = new AlertDialog.Builder(a).setTitle(getText(R.string.dialog_step_title))
//				.setPositiveButton(getText(R.string.dialog_ok), new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int whichButton) {
//						textE.setText(stepTextE.getText().toString().trim());
//					}
//				}).setNegativeButton(getText(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int whichButton) {
//						if (!erf.beforeEditting) {
//							erf.setEditting(false);
//						}
//					}
//				}).setView(stepTextView).create();
		final EditTextDialogFragment stepDialog = EditTextDialogFragment.newInstance(getString(R.string.dialog_step_title), "", "", "", 2, null, null);
		stepDialog.setTextView(textE);
		stepDialog.setInputFilter(stepTextLengthFilter);
		stepDialog.setListener(new AlertDialogListener() {
			@Override
			public void doPositiveClick(int id) {
				textE.setText(stepDialog.getEditText().getText().toString().trim());
			}

			@Override
			public void doNegativeClick(int id) {
				if (!erf.beforeEditting) {
					erf.setEditting(false);
				}
			}
		});
//		textDialog.getWindow().getAttributes().gravity = Gravity.TOP;
		textE.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				erf.beforeEditting = erf.isEditting;
//				stepTextE.setText(textE.getText().toString().trim());
//				stepTextE.addTextChangedListener(new EdittingTextWatcher());
//				textDialog.show();
//				stepTextE.requestFocus();
				stepDialog.setTextWatcher(new EdittingTextWatcher());
				stepDialog.show(a.getSupportFragmentManager(), "NoneTitle");
			}
		});

		final ImageView stepPhotoE = (ImageView) stepE.findViewById(R.id.stepPhoto);
		final ImageView stepPhotoDefaultE = (ImageView) stepE.findViewById(R.id.stepPhotoDefault);
		Bitmap stepPhotoBmp = ((BitmapDrawable) stepPhotoE.getDrawable()).getBitmap();
		Display display = ((WindowManager) a.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//		int width = (int) (display.getWidth() * 0.44) - 20;
		Point size = new Point();
		display.getSize(size);
		int width = (int) (size.x * 0.44f) - 20;
		int height = stepPhotoBmp.getHeight() * width / stepPhotoBmp.getWidth();
		stepPhotoE.setLayoutParams(new LinearLayout.LayoutParams(width, height));

//		LinearLayout photoView = (LinearLayout) ((LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
//				.inflate(R.layout.dialog_photo, null, false);
//		final ImageView stepPhotoEE = (ImageView) photoView.findViewById(R.id.photo);
//		final AlertDialog photoDialog = new AlertDialog.Builder(a).setTitle(getText(R.string.dialog_photo_title))
//				.setNegativeButton(getText(R.string.dialog_close), null).setView(photoView).create();
		stepPhotoE.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				stepPhotoEE.setLayoutParams(stepPhotoE.getLayoutParams());
//				stepPhotoEE.setImageBitmap(((BitmapDrawable) stepPhotoE.getDrawable()).getBitmap());
//				photoDialog.show();
				EditStepFragment.PhotoDialogFragment photoDialog = EditStepFragment.PhotoDialogFragment.newInstance();
				photoDialog.setImageView(stepPhotoE);
				photoDialog.setDefaultImageView(stepPhotoDefaultE);
				photoDialog.show(a.getSupportFragmentManager(), "PhotoDialog");
			}
		});
//		ImageView camera = (ImageView) photoView.findViewById(R.id.camera);
//		camera.setOnClickListener(((BaseActivity) a).new CameraOnClickListener(EditRecipeFragment.PHOTO_STEP_SIZE,
//				stepPhotoEE, stepPhotoE));
//		ImageView gallery = (ImageView) photoView.findViewById(R.id.gallery);
//		gallery.setOnClickListener(((BaseActivity) a).new GralleryOnClickListener(EditRecipeFragment.PHOTO_STEP_SIZE,
//				stepPhotoEE, stepPhotoE));
//		ImageView rotate = (ImageView) photoView.findViewById(R.id.rotate);
//		rotate.setOnClickListener(((BaseActivity) a).new RotateOnClickListener(EditRecipeFragment.PHOTO_STEP_SIZE,
//				stepPhotoEE, stepPhotoE));
//		ImageView crop = (ImageView) photoView.findViewById(R.id.crop);
//		crop.setOnClickListener(((BaseActivity) a).new CropOnClickListener(EditRecipeFragment.PHOTO_STEP_CROP_SIZE,
//				stepPhotoEE, stepPhotoE));
//		ImageView delete = (ImageView) photoView.findViewById(R.id.delete);
//		delete.setVisibility(View.VISIBLE);
//		delete.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Bitmap before = ((BitmapDrawable) stepPhotoE.getDrawable()).getBitmap();
//				Bitmap bitmap = ((BitmapDrawable) stepPhotoDefaultE.getDrawable()).getBitmap();
//				if (before != bitmap) {
//					erf.setEditting(true);
//				}
//				stepPhotoE.setImageBitmap(bitmap);
//				stepPhotoEE.setImageBitmap(bitmap);
//				Display display = ((WindowManager) a.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
////				int width = (int) (display.getWidth() * 0.44) - 20;
//				Point size = new Point();
//				display.getSize(size);
//				int width = (int) (size.x * 0.44f) - 20;
//				int height = bitmap.getHeight() * width / bitmap.getWidth();
//				stepPhotoE.setLayoutParams(new LinearLayout.LayoutParams(width, height));
//				stepPhotoEE.setLayoutParams(new LinearLayout.LayoutParams(width, height));
//				stepPhotoE.setTag(null);
//				stepPhotoEE.setTag(null);
//			}
//		});

		ImageView sDel = (ImageView) stepE.findViewById(R.id.stepDel);
		sDel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				erf.setEditting(true);
				stepsE.removeView(stepE);
				refleshStepNumber(stepsE);
			}
		});

		// TODO ダイアログの幅が変わらないので透明で対処
//		final Dialog sortDialog = new CustomDialog(a, R.style.Theme_CustomDialog);
//		sortDialog.setContentView(R.layout.dialog_sort);
//		sortDialog.getWindow().getAttributes().gravity = Gravity.LEFT;
//		ImageButton up = (ImageButton) sortDialog.findViewById(R.id.up);
//		ImageButton down = (ImageButton) sortDialog.findViewById(R.id.down);
//		ImageView close = (ImageView) sortDialog.findViewById(R.id.close);
//		sortDialog.setOnDismissListener(new OnDismissListener() {
//			@Override
//			public void onDismiss(DialogInterface dialog) {
//				refleshStepNumber(stepsE);
//			}
//		});
//		up.setOnClickListener(new SortOnClickListener(stepE, stepsE, -1));
//		down.setOnClickListener(new SortOnClickListener(stepE, stepsE, 1));
//		close.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				sortDialog.dismiss();
//			}
//		});
		final EditStepFragment.SortDialogFragment sortDialog = EditStepFragment.SortDialogFragment.newInstance();
		sortDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				refleshStepNumber(stepsE);
			}
		});
		sortDialog.setUpOnClickListener(new SortOnClickListener(stepE, stepsE, -1));
		sortDialog.setDownOnClickListener(new SortOnClickListener(stepE, stepsE, 1));
		ImageView sUp = (ImageView) stepE.findViewById(R.id.up);
		ImageView sDown = (ImageView) stepE.findViewById(R.id.down);
		class UpDownOnClickListener implements OnClickListener {
			@Override
			public void onClick(View v) {
				sortDialog.show(a.getSupportFragmentManager(), "SortDialog");
			}
		}
		UpDownOnClickListener udocl = new UpDownOnClickListener();
		sUp.setOnClickListener(udocl);
		sDown.setOnClickListener(udocl);

		if (isTop) {
			sDel.setVisibility(View.GONE);
			sUp.setVisibility(View.GONE);
			sDown.setVisibility(View.GONE);
		} else {
			sDel.setVisibility(View.VISIBLE);
			sDel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					erf.setEditting(true);
					stepsE.removeView(stepE);
					refleshStepNumber(stepsE);
				}
			});
			sUp.setVisibility(View.VISIBLE);
			sDown.setVisibility(View.VISIBLE);
		}

		return stepE;
	}

	class SortOnClickListener implements OnClickListener {
		View current;
		TableLayout parent;
		int sort = 0;

		public SortOnClickListener(View current, TableLayout parent, int sort) {
			this.current = current;
			this.parent = parent;
			this.sort = sort;
		}

		@Override
		public void onClick(View v) {
			erf.setEditting(true);
			int index = parent.indexOfChild(current);
			if ((index + sort) >= parent.getChildCount()) {
				index = -1;
			}
			if ((index + sort) < 0) {
				index = parent.getChildCount();
			}
			parent.removeView(current);
			parent.addView(current, index + sort);
		}
	}

	private void setLineEdit(ImageView tEdit, ImageView tDone, ImageView tAdd, TableLayout tsE, int addId, int delId,
			int upId, int downId, boolean isEdit) {
		for (int i = 0; i < tsE.getChildCount(); i++) {
			View tE = tsE.getChildAt(i);
			ImageView tDel = (ImageView) tE.findViewById(delId);
			tDel.setVisibility(isEdit ? View.VISIBLE : View.GONE);
			ImageView tUp = (ImageView) tE.findViewById(upId);
			tUp.setVisibility(isEdit ? View.VISIBLE : View.GONE);
			ImageView tDown = (ImageView) tE.findViewById(downId);
			tDown.setVisibility(isEdit ? View.VISIBLE : View.GONE);
		}
		tEdit.setVisibility(!isEdit ? View.VISIBLE : View.GONE);
		tDone.setVisibility(isEdit ? View.VISIBLE : View.GONE);
	}

	class EdittingTextWatcher implements TextWatcher {
		String pre;

		public void afterTextChanged(Editable arg) {
		}

		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			pre = s.toString();
		}

		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if (!pre.equals(s.toString())) {
				erf.setEditting(true);
			}
		}
	}

	private void refleshStepNumber(TableLayout stepsE) {
		for (int i = 0; i < stepsE.getChildCount(); i++) {
			View stepE = stepsE.getChildAt(i);
			TextView numberE = (TextView) stepE.findViewById(R.id.number);
			numberE.setText("" + (i + 1));
		}
	}

	private String trimCrlfSpace(String a) {
		a = a.trim();
		while (a.startsWith("\r\n")) {
			a = a.substring(1, a.length());
			a = a.trim();
		}
		while (a.endsWith("\r\n")) {
			a = a.substring(0, a.length() - 1);
			a = a.trim();
		}
		return a;
	}

	public static class PhotoDialogFragment extends DialogFragment {
		private static final String BITMAP_PHOTO_EE = "bitmapPhotoEE";

		ImageView photoEE;
		static ImageView photo;
		static ImageView defaultPhoto;

		public static EditStepFragment.PhotoDialogFragment newInstance() {
			return new EditStepFragment.PhotoDialogFragment();
		}

		@Override
		public void onSaveInstanceState(Bundle outState) {
			super.onSaveInstanceState(outState);
			outState.putParcelable(BITMAP_PHOTO_EE, ((BitmapDrawable)photoEE.getDrawable()).getBitmap());
		}

		public void setImageView(ImageView photo) {
			this.photo = photo;
		}

		public void setDefaultImageView(ImageView defaultPhoto) {
			this.defaultPhoto = defaultPhoto;
		}

		@NonNull
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final EditRecipeGroupActivity editRecipeActivity = (EditRecipeGroupActivity) getActivity();
//            ImageView photo = createRecipeActivity.photo;

			final LinearLayout photoView = (LinearLayout) View.inflate(getContext(), R.layout.dialog_photo, null);
			photoEE = (ImageView) photoView.findViewById(R.id.photo);

			photoEE.setLayoutParams(photo.getLayoutParams());
			if (savedInstanceState != null) {
				photoEE.setImageBitmap((Bitmap) savedInstanceState.getParcelable(BITMAP_PHOTO_EE));
			}
			else {
				photoEE.setImageBitmap(((BitmapDrawable) photo.getDrawable()).getBitmap());
			}

			final AlertDialog photoDialog = new AlertDialog.Builder(getContext())
					.setTitle(getText(R.string.dialog_photo_title))
					.setNegativeButton(getText(R.string.dialog_close), null)
					.setView(photoView).create();

			ImageView camera = (ImageView) photoView.findViewById(R.id.camera);
			camera.setOnClickListener(editRecipeActivity.new CameraOnClickListener(EditRecipeFragment.PHOTO_SIZE, photoEE, photo));
			ImageView gallery = (ImageView) photoView.findViewById(R.id.gallery);
			gallery.setOnClickListener(editRecipeActivity.new GralleryOnClickListener(EditRecipeFragment.PHOTO_SIZE, photoEE, photo));
			ImageView rotate = (ImageView) photoView.findViewById(R.id.rotate);
			rotate.setOnClickListener(editRecipeActivity.new RotateOnClickListener(EditRecipeFragment.PHOTO_SIZE, photoEE, photo));
			ImageView crop = (ImageView) photoView.findViewById(R.id.crop);
			crop.setOnClickListener(editRecipeActivity.new CropOnClickListener(EditRecipeFragment.PHOTO_CROP_SIZE, photoEE, photo));
			ImageView delete = (ImageView) photoView.findViewById(R.id.delete);
			delete.setVisibility(View.VISIBLE);
			delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Bitmap before = ((BitmapDrawable) photo.getDrawable()).getBitmap();
					Bitmap bitmap = ((BitmapDrawable) defaultPhoto.getDrawable()).getBitmap();
					if (before != bitmap) {
						FragmentManager fm = getFragmentManager();
						EditRecipeFragment erf = (EditRecipeFragment) fm.findFragmentById(R.id.editRecipeFragment);
						erf.setEditting(true);
					}
					photo.setImageBitmap(bitmap);
					photoEE.setImageBitmap(bitmap);
					Display display = ((WindowManager) editRecipeActivity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//				int width = (int) (display.getWidth() * 0.44) - 20;
					Point size = new Point();
					display.getSize(size);
					int width = (int) (size.x * 0.44f) - 20;
					int height = bitmap.getHeight() * width / bitmap.getWidth();
					photo.setLayoutParams(new LinearLayout.LayoutParams(width, height));
					photoEE.setLayoutParams(new LinearLayout.LayoutParams(width, height));
					photo.setTag(null);
					photoEE.setTag(null);
				}
			});

			return photoDialog;
		}
	}

	public static class SortDialogFragment extends DialogFragment {

		private OnClickListener onUpClickListener;
		private OnClickListener onDownClickListener;
		private OnDismissListener onDismissListener;

		public static EditStepFragment.SortDialogFragment newInstance() {
			return new EditStepFragment.SortDialogFragment();
		}

		@Override
		public void onSaveInstanceState(Bundle outState) {
			super.onSaveInstanceState(outState);
		}

		public void setUpOnClickListener(OnClickListener onUpClickListener) {
			this.onUpClickListener = onUpClickListener;
		}

		public void setDownOnClickListener(OnClickListener onDownClickListener) {
			this.onDownClickListener = onDownClickListener;
		}

		public void setOnDismissListener(OnDismissListener onDismissListener) {
			this.onDismissListener = onDismissListener;
		}

		@NonNull
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final EditRecipeGroupActivity a = (EditRecipeGroupActivity) getActivity();
			final Dialog sortDialog = new CustomDialog(a, R.style.Theme_CustomDialog);
			sortDialog.setContentView(R.layout.dialog_sort);
			sortDialog.getWindow().getAttributes().gravity = Gravity.LEFT;
			ImageButton up = (ImageButton) sortDialog.findViewById(R.id.up);
			ImageButton down = (ImageButton) sortDialog.findViewById(R.id.down);
			ImageView close = (ImageView) sortDialog.findViewById(R.id.close);
			sortDialog.setOnDismissListener(onDismissListener);
			up.setOnClickListener(this.onUpClickListener);
			down.setOnClickListener(this.onDownClickListener);
			close.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					sortDialog.dismiss();
				}
			});
			return sortDialog;
		}
	}
}
