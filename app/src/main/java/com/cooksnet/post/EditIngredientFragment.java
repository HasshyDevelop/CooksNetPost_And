package com.cooksnet.post;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.cooksnet.util.CustomDialog;
import com.cooksnet.util.MultibyteLengthFilter;

public class EditIngredientFragment extends BaseFragment {

	public final MultibyteLengthFilter totalQuantityLengthFilter = new MultibyteLengthFilter(15, 10, 40,
			ExtrasData.profile.locale);
	public final MultibyteLengthFilter ingredientNameLengthFilter = new MultibyteLengthFilter(22, 15, 60,
			ExtrasData.profile.locale);
	public final MultibyteLengthFilter ingredientQuantityLengthFilter = new MultibyteLengthFilter(22, 15, 60,
			ExtrasData.profile.locale);

	private EditRecipeFragment erf;

	private EditText totalQuantityE;
	private TableLayout ingredientsE;
	private ImageButton iEdit, iDone;
	private ImageView iAdd;

	private TextView totalQuantity;
	private TableLayout ingredients;
	private TextView preKakko;
	private TextView suffKakko;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		v = inflater.inflate(R.layout.edit_ingredient, container, true);

		FragmentManager fm = getFragmentManager();
		erf = (EditRecipeFragment) fm.findFragmentById(R.id.editRecipeFragment);

		totalQuantityE = (EditText) v.findViewById(R.id.totalQuantity);
		totalQuantityE.setFilters(new InputFilter[] { totalQuantityLengthFilter });
		ingredientsE = (TableLayout) v.findViewById(R.id.ingredients);

		iEdit = (ImageButton) v.findViewById(R.id.edit);
		iDone = (ImageButton) v.findViewById(R.id.done);
		iAdd = (ImageView) v.findViewById(R.id.ingredientAdd);

		iEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				erf.isPushedEditButton = true;
				setLineEdit(iEdit, iDone, iAdd, ingredientsE, R.id.ingredientAdd, R.id.ingredientDel, R.id.up,
						R.id.down, true);
			}
		});
		iDone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				erf.isPushedEditButton = false;
				setLineEdit(iEdit, iDone, iAdd, ingredientsE, R.id.ingredientAdd, R.id.ingredientDel, R.id.up,
						R.id.down, false);
			}
		});
		iAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (EditRecipeFragment.MAX_COUNT_INGREDIENT == ingredientsE.getChildCount()) {
//					new AlertDialog.Builder(a).setTitle(R.string.dialog_ingredient_count_over)
//							.setPositiveButton(R.string.dialog_ok, null).create().show();
					AlertDialogFragment.newInstance("", getString(R.string.dialog_ingredient_count_over), "")
							.show(a.getSupportFragmentManager(), "NoneTitle");
					return;
				}
				ingredientsE.addView(createIngredientE(ingredientsE, !erf.isPushedEditButton));
				erf.setEditting(true);
			}
		});

		final Button end = ((Button) v.findViewById(R.id.end));
		end.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				((EditIngredientFragment) fm.findFragmentById(R.id.editIngredientFragment)).preHide();
				ft.show((Fragment) fm.findFragmentById(R.id.editRecipeFragment));
				ft.hide((Fragment) fm.findFragmentById(R.id.editIngredientFragment));
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

	public void preShow(TextView totalQuantity, TableLayout ingredients, TextView preKakko, TextView suffKakko) {
		this.totalQuantity = totalQuantity;
		this.ingredients = ingredients;
		this.preKakko = preKakko;
		this.suffKakko = suffKakko;

		totalQuantityE.setText(totalQuantity.getText().toString().trim());
		totalQuantityE.addTextChangedListener(new EdittingTextWatcher());
		ingredientsE.removeAllViews();
		for (int i = 0; i < ingredients.getChildCount(); i++) {
			View ingredient = ingredients.getChildAt(i);
			if (null == ((TextView) ingredient.findViewById(R.id.name))) {
				continue;
			}

			View ingredientE = createIngredientE(ingredientsE, true);

			EditText nameE = (EditText) ingredientE.findViewById(R.id.name);
			EditText quantityE = (EditText) ingredientE.findViewById(R.id.quantity);

			nameE.setText(((TextView) ingredient.findViewById(R.id.name)).getText().toString().trim());
			quantityE.setText(((TextView) ingredient.findViewById(R.id.quantity)).getText().toString().trim());

			nameE.addTextChangedListener(new EdittingTextWatcher());
			quantityE.addTextChangedListener(new EdittingTextWatcher());

			ingredientsE.addView(ingredientE);
		}
		if (0 == ingredientsE.getChildCount()) {
			ingredientsE.addView(createIngredientE(ingredientsE, true));
		}
		setLineEdit(iEdit, iDone, iAdd, ingredientsE, R.id.ingredientAdd, R.id.ingredientDel, R.id.up, R.id.down, false);
	}

	public void preHide() {
		totalQuantity.setText(totalQuantityE.getText());
		if (!"".equals(totalQuantityE.getText().toString().trim())) {
			preKakko.setVisibility(View.VISIBLE);
			suffKakko.setVisibility(View.VISIBLE);
		} else {
			preKakko.setVisibility(View.GONE);
			suffKakko.setVisibility(View.GONE);
		}
		ingredients.removeAllViews();
		int count = 0;
		for (int i = 0; i < ingredientsE.getChildCount(); i++) {
			View ingredientE = ingredientsE.getChildAt(i);
			EditText nameE = (EditText) ingredientE.findViewById(R.id.name);
			EditText quantityE = (EditText) ingredientE.findViewById(R.id.quantity);

			TableRow ingredient = (TableRow) a.getLayoutInflater().inflate(R.layout.ingredient_table_row, null, true);
			TextView name = (TextView) ingredient.findViewById(R.id.name);
			name.setText(nameE.getText().toString().trim());
			TextView quantity = (TextView) ingredient.findViewById(R.id.quantity);
			quantity.setText(quantityE.getText().toString().trim());
			ingredients.addView(ingredient);
			++count;

			if (i + 1 != ingredientsE.getChildCount()) {
				TableRow line = (TableRow) a.getLayoutInflater().inflate(R.layout.line_table_row, null);
				ingredients.addView(line);
			}
		}
		if (0 == count) {
			TableRow ingredient = (TableRow) a.getLayoutInflater().inflate(R.layout.ingredient_table_row, null, true);
			TextView name = (TextView) ingredient.findViewById(R.id.name);
			name.setHint(getText(R.string.edit_recipe_textview_hint_ingredient));
			ingredients.addView(ingredient);
		}
	}

	private View createIngredientE(final TableLayout ingredientsE, boolean isTop) {
		final TableRow ingredientE = (TableRow) a.getLayoutInflater().inflate(R.layout.edit_ingredient_table_row, null,
				true);
		final EditText nameE = (EditText) ingredientE.findViewById(R.id.name);
		nameE.setFilters(new InputFilter[] { ingredientNameLengthFilter });
		final EditText quantityE = (EditText) ingredientE.findViewById(R.id.quantity);
		quantityE.setFilters(new InputFilter[] { ingredientQuantityLengthFilter });

		final ImageView iDel = (ImageView) ingredientE.findViewById(R.id.ingredientDel);
		iDel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				erf.setEditting(true);
				ingredientsE.removeView(ingredientE);
			}
		});

//		final Dialog sortDialog = new CustomDialog(a, R.style.Theme_CustomDialog);
//		sortDialog.setContentView(R.layout.dialog_sort);
//		sortDialog.getWindow().getAttributes().gravity = Gravity.LEFT;
//		ImageButton up = (ImageButton) sortDialog.findViewById(R.id.up);
//		ImageButton down = (ImageButton) sortDialog.findViewById(R.id.down);
//		ImageView close = (ImageView) sortDialog.findViewById(R.id.close);
//		up.setOnClickListener(new SortOnClickListener(ingredientE, ingredientsE, -1));
//		down.setOnClickListener(new SortOnClickListener(ingredientE, ingredientsE, 1));
//		close.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				sortDialog.dismiss();
//			}
//		});
		final EditIngredientFragment.SortDialogFragment sortDialog = EditIngredientFragment.SortDialogFragment.newInstance();
		sortDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
			}
		});
		sortDialog.setUpOnClickListener(new EditIngredientFragment.SortOnClickListener(ingredientE, ingredientsE, -1));
		sortDialog.setDownOnClickListener(new EditIngredientFragment.SortOnClickListener(ingredientE, ingredientsE, 1));
		ImageView iUp = (ImageView) ingredientE.findViewById(R.id.up);
		ImageView iDown = (ImageView) ingredientE.findViewById(R.id.down);
		class UpDownOnClickListener implements OnClickListener {
			@Override
			public void onClick(View v) {
				sortDialog.show(a.getSupportFragmentManager(), "SortDialog");
			}
		}
		UpDownOnClickListener udocl = new UpDownOnClickListener();
		iUp.setOnClickListener(udocl);
		iDown.setOnClickListener(udocl);

		if (isTop) {
			iDel.setVisibility(View.GONE);
			iUp.setVisibility(View.GONE);
			iDown.setVisibility(View.GONE);
		} else {
			iDel.setVisibility(View.VISIBLE);
			iDel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					erf.setEditting(true);
					ingredientsE.removeView(ingredientE);
				}
			});
			iUp.setVisibility(View.VISIBLE);
			iDown.setVisibility(View.VISIBLE);
		}

		return ingredientE;
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

	public static class SortDialogFragment extends DialogFragment {

		private OnClickListener onUpClickListener;
		private OnClickListener onDownClickListener;
		private DialogInterface.OnDismissListener onDismissListener;

		public static EditIngredientFragment.SortDialogFragment newInstance() {
			return new EditIngredientFragment.SortDialogFragment();
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

		public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
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
