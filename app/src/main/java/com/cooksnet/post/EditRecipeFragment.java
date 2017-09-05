package com.cooksnet.post;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.cooksnet.obj.Ingredient;
import com.cooksnet.obj.Recipe;
import com.cooksnet.obj.Step;
import com.cooksnet.util.CooksNetParser;
import com.cooksnet.util.CooksNetWebAccess;
import com.cooksnet.util.ImageLoadCache;
import com.cooksnet.util.ImageLoadThread;
import com.cooksnet.util.MultibyteLengthFilter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditRecipeFragment extends BaseFragment {

    public static final int MAX_COUNT_INGREDIENT = 20;
    public static final int MAX_COUNT_STEP = 20;

    public static final String PHOTO_NAME = "photo.jpg";
    public static final int[] PHOTO_SIZE = new int[]{400, 400, 400, 400, 400, 400};
    public static final int[] PHOTO_CROP_SIZE = new int[]{400, 400, 400, 400, 400, 400};
    public static final int[] PHOTO_STEP_SIZE = new int[]{160, 213, 200, 150, 160, 120};
    public static final int[] PHOTO_STEP_CROP_SIZE = new int[]{160, 213, 200, 150, 160, 120};

    public final MultibyteLengthFilter descriptionLengthFilter = new MultibyteLengthFilter(90, 60, 240,
            ExtrasData.profile.locale);
    public final MultibyteLengthFilter adviceLengthFilter = new MultibyteLengthFilter(180, 120, 480,
            ExtrasData.profile.locale);

    private ProgressDialogFragment dialog;
    private Handler handler;

    private LinearLayout down;

    private boolean isPublish;

    boolean isPushedEditButton = false;
    boolean isEditting = false;
    boolean beforeEditting = false;

    Recipe recipe;
    public List<Step> stepsTmp = new ArrayList<Step>();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        v = inflater.inflate(R.layout.edit_recipe, null, true);

        handler = new Handler();

        isPublish = a.getIntent().getExtras().getBoolean("isPublish");

        final String id = (String) a.getIntent().getExtras().getSerializable("id");

        down = (LinearLayout) v.findViewById(R.id.down);

        final TextView title = (TextView) v.findViewById(R.id.title);

        final ImageView photo = (ImageView) v.findViewById(R.id.photo);
//        final LinearLayout photoView = (LinearLayout) ((LayoutInflater) a
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dialog_photo, null, false);
//        final ImageView photoEE = (ImageView) photoView.findViewById(R.id.photo);
//        final AlertDialog photoDialog = new AlertDialog.Builder(a).setTitle(getText(R.string.dialog_photo_title))
//                .setNegativeButton(getText(R.string.dialog_close), null).setView(photoView).create();
        photo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPublish) {
                    return;
                }
//                photoEE.setLayoutParams(photo.getLayoutParams());
//                photoEE.setImageBitmap(((BitmapDrawable) photo.getDrawable()).getBitmap());
//                photoDialog.show();
                EditRecipeFragment.PhotoDialogFragment photoDialog = EditRecipeFragment.PhotoDialogFragment.newInstance();
                photoDialog.setImageView(photo);
                photoDialog.show(a.getSupportFragmentManager(), "PhotoDialog");
            }
        });
//        ImageView camera = (ImageView) photoView.findViewById(R.id.camera);
//        camera.setOnClickListener(((BaseActivity) a).new CameraOnClickListener(PHOTO_SIZE, photoEE, photo));
//        ImageView gallery = (ImageView) photoView.findViewById(R.id.gallery);
//        gallery.setOnClickListener(((BaseActivity) a).new GralleryOnClickListener(PHOTO_SIZE, photoEE, photo));
//        ImageView rotate = (ImageView) photoView.findViewById(R.id.rotate);
//        rotate.setOnClickListener(((BaseActivity) a).new RotateOnClickListener(PHOTO_SIZE, photoEE, photo));
//        ImageView crop = (ImageView) photoView.findViewById(R.id.crop);
//        crop.setOnClickListener(((BaseActivity) a).new CropOnClickListener(PHOTO_SIZE, photoEE, photo));

        final TextView description = (TextView) v.findViewById(R.id.description);
//        final AlertDialog descriptionDialog = new AlertDialog.Builder(a)
//                .setTitle(getText(R.string.dialog_description_title))
//                .setPositiveButton(getText(R.string.dialog_ok), new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        description.setText(trimCrlfSpace(descriptionE.getText().toString()));
//                    }
//                }).setNegativeButton(getText(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        if (!beforeEditting) {
//                            setEditting(false);
//                        }
//                    }
//                }).setView(descriptionView).create();
        final EditTextDialogFragment descriptionDialog = EditTextDialogFragment.newInstance(getString(R.string.dialog_description_title), "", "", "", 2, null, null);
        descriptionDialog.setTextView(description);
        descriptionDialog.setInputFilter(descriptionLengthFilter);
        descriptionDialog.setListener(new AlertDialogListener() {
            @Override
            public void doPositiveClick(int id) {
                description.setText(trimCrlfSpace(descriptionDialog.getEditText().getText().toString()));
                down.setVisibility(View.VISIBLE);
            }

            @Override
            public void doNegativeClick(int id) {
                if (!beforeEditting) {
                    setEditting(false);
                }
                down.setVisibility(View.VISIBLE);
            }
        });
//        descriptionDialog.setOnDismissListener(new OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                down.setVisibility(View.VISIBLE);
//            }
//        });
//        descriptionDialog.getWindow().getAttributes().gravity = Gravity.TOP;
        description.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPublish) {
                    return;
                }
                beforeEditting = isEditting;
                down.setVisibility(View.GONE);
//                descriptionE.setText(description.getText());
//                descriptionE.addTextChangedListener(new EdittingTextWatcher());
//                descriptionDialog.show();
//                descriptionE.requestFocus();
                descriptionDialog.setTextWatcher(new EdittingTextWatcher());
                descriptionDialog.show(a.getSupportFragmentManager(), "NoneTitle");

                // ダイアログ幅を広げるおまじない
//                Window win = descriptionDialog.getWindow();
//                WindowManager.LayoutParams lpCur = win.getAttributes();
//                WindowManager.LayoutParams lpNew = new WindowManager.LayoutParams();
//                lpNew.copyFrom(lpCur);
//                lpNew.width = WindowManager.LayoutParams.FILL_PARENT;
//                lpNew.height = WindowManager.LayoutParams.WRAP_CONTENT;
//                win.setAttributes(lpNew);
            }
        });

        final ImageView userIcon = (ImageView) v.findViewById(R.id.userIcon);
        final TextView profileName = (TextView) v.findViewById(R.id.profileName);

        final TextView totalQuantity = (TextView) v.findViewById(R.id.totalQuantity);
        final TextView preKakko = (TextView) v.findViewById(R.id.preKakko);
        final TextView suffKakko = (TextView) v.findViewById(R.id.suffKakko);
        final LinearLayout ingredientLabel = (LinearLayout) v.findViewById(R.id.ingredientLabel);
        final TableLayout ingredients = (TableLayout) v.findViewById(R.id.ingredients);

        class IngradientOnClickListner implements OnClickListener {
            @Override
            public void onClick(View v) {
                if (isPublish) {
                    return;
                }
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ((EditIngredientFragment) fm.findFragmentById(R.id.editIngredientFragment)).preShow(totalQuantity,
                        ingredients, preKakko, suffKakko);
                ft.show((Fragment) fm.findFragmentById(R.id.editIngredientFragment));
                ft.hide((Fragment) fm.findFragmentById(R.id.editRecipeFragment));
                ft.commit();
            }
        }
        ingredients.setOnClickListener(new IngradientOnClickListner());
        ingredientLabel.setOnClickListener(new IngradientOnClickListner());

        final LinearLayout stepLabel = (LinearLayout) v.findViewById(R.id.stepLabel);
        final TableLayout steps = (TableLayout) v.findViewById(R.id.steps);

        class StepOnClickListner implements OnClickListener {
            @Override
            public void onClick(View v) {
                if (isPublish) {
                    return;
                }
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ((EditStepFragment) fm.findFragmentById(R.id.editStepFragment)).preShow(steps);
                ft.show((Fragment) fm.findFragmentById(R.id.editStepFragment));
                ft.hide((Fragment) fm.findFragmentById(R.id.editRecipeFragment));
                ft.commit();
            }
        }
        steps.setOnClickListener(new StepOnClickListner());
        stepLabel.setOnClickListener(new StepOnClickListner());

        final LinearLayout adviceLabel = (LinearLayout) v.findViewById(R.id.adviceLabel);
        final TextView advice = (TextView) v.findViewById(R.id.advice);
//        final LinearLayout adviceView = (LinearLayout) ((LayoutInflater) a
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dialog_message, null, false);
//        final EditText adviceE = (EditText) adviceView.findViewById(R.id.message);
//        adviceE.setFilters(new InputFilter[]{adviceLengthFilter});
//        final AlertDialog adviceDialog = new AlertDialog.Builder(a).setTitle(getText(R.string.dialog_advice_title))
//                .setPositiveButton(getText(R.string.dialog_ok), new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        advice.setText(trimCrlfSpace(adviceE.getText().toString()));
//                    }
//                }).setNegativeButton(getText(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        if (!beforeEditting) {
//                            setEditting(false);
//                        }
//                    }
//                }).setView(adviceView).create();
        final EditTextDialogFragment adviceDialog = EditTextDialogFragment.newInstance(getString(R.string.dialog_advice_title), "", "", "", 2, null, null);
        adviceDialog.setTextView(advice);
        adviceDialog.setInputFilter(adviceLengthFilter);
        adviceDialog.setListener(new AlertDialogListener() {
            @Override
            public void doPositiveClick(int id) {
                advice.setText(trimCrlfSpace(adviceDialog.getEditText().getText().toString()));
                down.setVisibility(View.VISIBLE);
            }

            @Override
            public void doNegativeClick(int id) {
                if (!beforeEditting) {
                    setEditting(false);
                }
                down.setVisibility(View.VISIBLE);
            }
        });
//        adviceDialog.setOnDismissListener(new OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                down.setVisibility(View.VISIBLE);
//            }
//        });
//        adviceDialog.getWindow().getAttributes().gravity = Gravity.TOP;
        class AdviceOnClickListner implements OnClickListener {
            @Override
            public void onClick(View v) {
                if (isPublish) {
                    return;
                }
                beforeEditting = isEditting;
                down.setVisibility(View.GONE);
//                adviceE.setText(advice.getText());
//                adviceE.addTextChangedListener(new EdittingTextWatcher());
//                adviceDialog.show();
//                adviceE.requestFocus();
                adviceDialog.setTextWatcher(new EdittingTextWatcher());
                adviceDialog.show(a.getSupportFragmentManager(), "NoneTitle");

                // ダイアログ幅を広げるおまじない
//                Window win = adviceDialog.getWindow();
//                WindowManager.LayoutParams lpCur = win.getAttributes();
//                WindowManager.LayoutParams lpNew = new WindowManager.LayoutParams();
//                lpNew.copyFrom(lpCur);
//                lpNew.width = WindowManager.LayoutParams.MATCH_PARENT;
//                lpNew.height = WindowManager.LayoutParams.WRAP_CONTENT;
//                win.setAttributes(lpNew);
            }
        }

        adviceLabel.setOnClickListener(new AdviceOnClickListner());
        advice.setOnClickListener(new AdviceOnClickListner());

        final TextView recipeId = (TextView) v.findViewById(R.id.recipeId);

        class SaveOrPublishOrDraftOnClickListener implements OnClickListener {
            Recipe edit = new Recipe();

            public void onClick(View v) {
            }

            protected void set() {
                edit = new Recipe();
                edit.id = recipe.id;
                edit.description = description.getText().toString().trim();
                edit.totalQuantity = totalQuantity.getText().toString().trim();
                edit.advice = advice.getText().toString().trim();

                Bitmap bitmap = ((BitmapDrawable) photo.getDrawable()).getBitmap();
                File photoFile = null;
                if (null != photo.getTag()) {
                    try {
                        photoFile = ((BaseActivity) a).saveBitmapToPrivateStrage(bitmap, PHOTO_NAME,
                                Context.MODE_PRIVATE);
                        edit.isModifiedPhoto = true;
                    } catch (IOException ioe) {
                    }
                }
                edit.photo = bitmap;
                edit.photoFile = photoFile;

                for (int i = 0; i < ingredients.getChildCount(); i++) {
                    View tableRow = ingredients.getChildAt(i);
                    if (null == (TextView) tableRow.findViewById(R.id.name)) {
                        continue;
                    }
                    TextView name = (TextView) tableRow.findViewById(R.id.name);
                    TextView quantity = (TextView) tableRow.findViewById(R.id.quantity);

                    Ingredient ingredient = new Ingredient();
                    ingredient.name = name.getText().toString().trim();
                    ingredient.quantity = quantity.getText().toString().trim();
                    edit.ingredients.add(ingredient);
                }

                for (int i = 0; i < steps.getChildCount(); i++) {
                    View tableRow = steps.getChildAt(i);
                    if (null == (TextView) tableRow.findViewById(R.id.text)) {
                        continue;
                    }
                    TextView text = (TextView) tableRow.findViewById(R.id.text);
                    ImageView stepPhoto = (ImageView) tableRow.findViewById(R.id.stepPhoto);
                    ImageView stepPhotoDefault = (ImageView) tableRow.findViewById(R.id.stepPhotoDefault);

                    Step step = new Step();
                    step.text = text.getText().toString().trim();

                    Bitmap stepBitmap = ((BitmapDrawable) stepPhoto.getDrawable()).getBitmap();
                    Bitmap defaultBitmap = ((BitmapDrawable) stepPhotoDefault.getDrawable()).getBitmap();
                    File stepPhotoFile = null;
                    if (stepBitmap != defaultBitmap) {
                        try {
                            stepPhotoFile = ((BaseActivity) a).saveBitmapToPrivateStrage(stepBitmap, "" + i
                                    + PHOTO_NAME, Context.MODE_PRIVATE);
                            if (null != stepPhoto.getTag()) {
                                step.isModifiedPhoto = true;
                            }
                        } catch (IOException ioe) {
                        }
                    }
                    step.photo = stepBitmap;
                    step.photoFile = stepPhotoFile;
                    edit.steps.add(step);
                }
            }

            protected boolean validate() {
                if (0 == edit.ingredients.size()) {
//                    new AlertDialog.Builder(a).setTitle(getText(R.string.dialog_least_ingredient))
//                            .setPositiveButton(R.string.dialog_ok, null).create().show();
                    AlertDialogFragment.newInstance(getString(R.string.dialog_least_ingredient), "", "")
                            .show(a.getSupportFragmentManager(), "NoneTitle");
                    return false;
                }
                if (0 == edit.steps.size()) {
//                    new AlertDialog.Builder(a).setTitle(getText(R.string.dialog_least_step))
//                            .setPositiveButton(R.string.dialog_ok, null).create().show();
                    AlertDialogFragment.newInstance(getString(R.string.dialog_least_step), "", "")
                            .show(a.getSupportFragmentManager(), "NoneTitle");
                    return false;
                }
                boolean requiredDescription = true;
                if ("".equals(edit.description.trim())) {
                    requiredDescription = false;
                }
                boolean requiredIngredients = true;
                for (Ingredient i : edit.ingredients) {
                    if ("".equals(i.name.trim())) {
                        requiredIngredients = false;
                        break;
                    }
                }
                boolean requiredSteps = true;
                for (Step s : edit.steps) {
                    if ("".equals(s.text.trim())) {
                        requiredSteps = false;
                        break;
                    }
                }
                if (!requiredDescription || !requiredIngredients || !requiredSteps) {
//                    new AlertDialog.Builder(a)
//                            .setMessage(
//                                    getText(R.string.dialog_required)
//                                            + "\n"
//                                            + ((!requiredDescription) ? "・"
//                                            + getText(R.string.dialog_description_title) + "\n" : "")
//                                            + ((!requiredIngredients) ? "・"
//                                            + getText(R.string.dialog_ingredient_ingredient_name_hint) + "\n"
//                                            : "")
//                                            + ((!requiredSteps) ? "・" + getText(R.string.edit_recipe_textview_step)
//                                            : "")).setPositiveButton(R.string.dialog_ok, null).create().show();
                    AlertDialogFragment.newInstance("", getText(R.string.dialog_required)
                            + "\n"
                            + ((!requiredDescription) ? "・"
                            + getText(R.string.dialog_description_title) + "\n" : "")
                            + ((!requiredIngredients) ? "・"
                            + getText(R.string.dialog_ingredient_ingredient_name_hint) + "\n"
                            : "")
                            + ((!requiredSteps) ? "・" + getText(R.string.edit_recipe_textview_step)
                            : ""), "")
                            .show(a.getSupportFragmentManager(), "NoneTitle");
                    return false;
                }
                return true;
            }
        }

        class SaveThread extends SaveOrPublishOrDraftThread {
            protected String doInBackground(Recipe... recipe) {
                try {
                    this.recipe = new CooksNetWebAccess().editRecipe(recipe[0]);
                    if (null != this.recipe && Recipe.EDIT_SUCCESS.equals(this.recipe.editStatus)) {
                    } else {
                        return this.recipe.editStatus;
                    }

                    if (recipe[0].isModifiedPhoto) {
                        this.recipe = new CooksNetWebAccess().uploadPhoto(recipe[0], EditRecipeFragment.this.recipe);
                        if (null != this.recipe && Recipe.UPLOAD_IMAGE_SUCCESS.equals(this.recipe.uploadImageStatus)) {
                        } else {
                            return this.recipe.uploadImageStatus;
                        }
                    }

                    this.recipe = new CooksNetWebAccess().editIngredient(recipe[0], EditRecipeFragment.this.recipe);
                    if (null != this.recipe && Recipe.EDIT_INGREDIENT_SUCCESS.equals(this.recipe.editIngredientStatus)) {
                    } else {
                        return this.recipe.editIngredientStatus;
                    }

                    this.recipe = new CooksNetWebAccess().editStep(recipe[0], EditRecipeFragment.this.recipe);
                    if (null != this.recipe && Recipe.EDIT_STEP_SUCCESS.equals(this.recipe.editStepStatus)) {
                    } else {
                        return this.recipe.editStepStatus;
                    }

                    return "success";
                } catch (IOException ioe) {
                    successNetwork = false;
                    if (ioe.getMessage() != null && ioe.getMessage().startsWith(CooksNetWebAccess.SERVER_ERROR)) {
                        return ioe.getMessage().replaceAll(CooksNetWebAccess.SERVER_ERROR, "");
                    }
                    return "";
                }
            }

            protected void doSuccess(String res) {
                setEditting(false);
            }

            protected void doFailed(String res) {
//                new AlertDialog.Builder(a).setMessage(res).setPositiveButton(R.string.dialog_ok, null).create().show();
                AlertDialogFragment.newInstance("", res, "")
                        .show(a.getSupportFragmentManager(), "NoneTitle");
            }
        }

        final Button save = (Button) v.findViewById(R.id.save);
        save.setOnClickListener(new SaveOrPublishOrDraftOnClickListener() {
            public void onClick(View v) {
                set();
                new SaveThread().execute(edit);
            }
        });

        class PublishThread extends SaveOrPublishOrDraftThread {
            protected String doInBackground(Recipe... recipe) {
                try {
                    recipe[0].publishStatus = new CooksNetWebAccess().publish(recipe[0].id);
                    this.recipe = recipe[0];
                    if (null != this.recipe && Recipe.PUBLISH_SUCCESS.equals(this.recipe.publishStatus)) {
                        return "success";
                    } else {
                        return "";
                    }

                } catch (IOException ioe) {
                    successNetwork = false;
                    if (ioe.getMessage().startsWith(CooksNetWebAccess.SERVER_ERROR)) {
                        return ioe.getMessage().replaceAll(CooksNetWebAccess.SERVER_ERROR, "");
                    }
                    return "";
                }
            }

            protected void doSuccess(String res) {
//                new AlertDialog.Builder(a).setMessage(getText(R.string.dialog_publish_readme))
//                        .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                SharedPreferences pref = a.getSharedPreferences(BaseActivity.SHARED_NAME,
//                                        a.MODE_PRIVATE);
//                                Editor e = pref.edit();
//                                e.putString("currentTab", "draft");
//                                e.commit();
//                                a.finish();
//                            }
//                        }).create().show();
                AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance("", getString(R.string.dialog_publish_readme), "", "", 1, null, null);
                alertDialogFragment.setListener(new AlertDialogListener() {
                    @Override
                    public void doPositiveClick(int id) {
                        SharedPreferences pref = a.getSharedPreferences(BaseActivity.SHARED_NAME,
                                a.MODE_PRIVATE);
                        Editor e = pref.edit();
                        e.putString("currentTab", "draft");
                        e.commit();
                        a.finish();
                    }

                    @Override
                    public void doNegativeClick(int id) {
                    }
                });
                alertDialogFragment.show(a.getSupportFragmentManager(), "NoneTitle");
            }

            protected void doFailed(String res) {
//                new AlertDialog.Builder(a).setTitle(getText(R.string.dialog_system_error_title))
//                        .setPositiveButton(R.string.dialog_ok, null).create().show();
                AlertDialogFragment.newInstance(getString(R.string.dialog_system_error_title), "", "")
                        .show(a.getSupportFragmentManager(), "NoneTitle");
            }
        }

        final Button publish = (Button) v.findViewById(R.id.publish);
        publish.setOnClickListener(new SaveOrPublishOrDraftOnClickListener() {
            public void onClick(View v) {
                set();
                if (validate()) {
//                    new AlertDialog.Builder(a).setMessage(getText(R.string.dialog_publish_question))
//                            .setPositiveButton(getText(R.string.dialog_ok), new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int whichButton) {
//                                    new PublishThread().execute(recipe);
//                                }
//                            })
//                            .setNegativeButton(getText(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int whichButton) {
//                                }
//                            }).create().show();
                    AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance("", getString(R.string.dialog_publish_question), "", "", 2, null, null);
                    alertDialogFragment.setListener(new AlertDialogListener() {
                        @Override
                        public void doPositiveClick(int id) {
                            new PublishThread().execute(recipe);
                        }

                        @Override
                        public void doNegativeClick(int id) {
                        }
                    });
                    alertDialogFragment.show(a.getSupportFragmentManager(), "NoneTitle");
                }
            }
        });

        class DraftThread extends SaveOrPublishOrDraftThread {
            protected String doInBackground(Recipe... recipe) {
                try {
                    recipe[0].draftStatus = new CooksNetWebAccess().draft(recipe[0].id);
                    this.recipe = recipe[0];
                    if (null != this.recipe && Recipe.DRAFT_SUCCESS.equals(this.recipe.draftStatus)) {
                        return "success";
                    } else {
                        return "";
                    }

                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    successNetwork = false;
                    if (ioe.getMessage().startsWith(CooksNetWebAccess.SERVER_ERROR)) {
                        return ioe.getMessage().replaceAll(CooksNetWebAccess.SERVER_ERROR, "");
                    }
                    return "";
                }
            }

            @Override
            protected void doSuccess(String res) {
//                new AlertDialog.Builder(a).setMessage(getText(R.string.dialog_draft_readme))
//                        .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                isPublish = false;
//                                setEditting(false);
//                            }
//                        }).create().show();
                AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance("", getString(R.string.dialog_draft_readme), "", "", 1, null, null);
                alertDialogFragment.setListener(new AlertDialogListener() {
                    @Override
                    public void doPositiveClick(int id) {
                        isPublish = false;
                        setEditting(false);
                    }

                    @Override
                    public void doNegativeClick(int id) {
                    }
                });
                alertDialogFragment.show(a.getSupportFragmentManager(), "NoneTitle");
            }

            @Override
            protected void doFailed(String res) {
//                new AlertDialog.Builder(a).setTitle(getText(R.string.dialog_system_error_title))
//                        .setPositiveButton(R.string.dialog_ok, null).create().show();
                AlertDialogFragment.newInstance(getString(R.string.dialog_system_error_title), "", "")
                        .show(a.getSupportFragmentManager(), "NoneTitle");
            }
        }

        final Button draft = (Button) v.findViewById(R.id.draft);
        draft.setOnClickListener(new SaveOrPublishOrDraftOnClickListener() {
            public void onClick(View v) {
//                new AlertDialog.Builder(a).setMessage(getText(R.string.dialog_draft_question))
//                        .setPositiveButton(getText(R.string.dialog_ok), new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int whichButton) {
//                                new DraftThread().execute(recipe);
//                            }
//                        }).setNegativeButton(getText(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                    }
//                }).create().show();
                AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance("", getString(R.string.dialog_draft_question), "", "", 2, null, null);
                alertDialogFragment.setListener(new AlertDialogListener() {
                    @Override
                    public void doPositiveClick(int id) {
                        new DraftThread().execute(recipe);
                    }

                    @Override
                    public void doNegativeClick(int id) {
                    }
                });
                alertDialogFragment.show(a.getSupportFragmentManager(), "NoneTitle");
            }
        });

        class GetRecipeThread extends AsyncTask<String, Void, String> {
            private boolean successNetwork = true;

            protected void onPreExecute() {
            }

            protected String doInBackground(String... id) {
                try {
                    // Recipe cache = null;
                    // Recipe cache = RecipeCache.getRecipe(id[0]);
                    // if (null == cache) {
                    handler.postDelayed(new Runnable() {
                        public void run() {
//							dialog = ProgressDialog.show(a, "", getText(R.string.dialog_progress).toString(), true);
                            dialog = ProgressDialogFragment.newInstance("", getString(R.string.dialog_progress));
                            dialog.show(a.getSupportFragmentManager(), "CreateThread");
                        }
                    }, 100);
                    String xml = new CooksNetWebAccess().getMyRecipe(id[0]);
                    recipe = new CooksNetParser().parseRecipe(xml);
                    RecipeCache.putRecipe(id[0], recipe);
                    // } else {
                    // recipe = cache;
                    // }
                    RecipeCache.lookedRecipe(recipe);
                    return "success";
                } catch (IOException ioe) {
                    successNetwork = false;
                    if (ioe.getMessage().startsWith(CooksNetWebAccess.SERVER_ERROR)) {
                        return ioe.getMessage().replaceAll(CooksNetWebAccess.SERVER_ERROR, "");
                    }
                    return "";
                }
            }

            protected void onPostExecute(final String res) {
                handler.post(new Runnable() {
                    public void run() {
                        if (successNetwork) {
                            Display display = ((WindowManager) a.getSystemService(Context.WINDOW_SERVICE))
                                    .getDefaultDisplay();

                            title.setText(recipe.title);
                            if (null != recipe.photoUrl) {
                                if (null == recipe.photo) {
                                    recipe.photo = ImageLoadCache.getImage(recipe.photoUrl);
                                }
                                if (null == recipe.photo) {
                                    new ImageLoadThread(handler, photo, true, 0.5f, 20).execute(recipe.photoUrl);
                                } else {
//                                    int width = (int) (display.getWidth() * 0.5f) - 20;
                                    Point size = new Point();
                                    display.getSize(size);
                                    int width = (int) (size.x * 0.5f) - 20;
                                    int height = recipe.photo.getHeight() * width / recipe.photo.getWidth();
                                    photo.setLayoutParams(new LinearLayout.LayoutParams(width, height));
                                    photo.setImageBitmap(recipe.photo);
                                    photo.setVisibility(View.VISIBLE);
                                }
                                // photo.setImageBitmap(recipe.photo);
                            } else {
                                photo.setVisibility(View.GONE);
                            }
                            description.setText(recipe.description);
                            description.setVisibility(View.VISIBLE);
                            // userIcon.setImageBitmap(recipe.userIcon);
                            new ImageLoadThread(handler, userIcon, true).execute(recipe.userIconUrl);
                            profileName.setText(recipe.profileName);
                            if (null != recipe.totalQuantity && !"".equals(recipe.totalQuantity)) {
                                totalQuantity.setText(recipe.totalQuantity);
                                preKakko.setVisibility(View.VISIBLE);
                                suffKakko.setVisibility(View.VISIBLE);
                            }
                            ingredientLabel.setVisibility(View.VISIBLE);
                            for (int i = 0; i < recipe.ingredients.size(); i++) {
                                Ingredient in = recipe.ingredients.get(i);
                                TableRow ingredient = (TableRow) a.getLayoutInflater().inflate(
                                        R.layout.ingredient_table_row, null, true);
                                TextView name = (TextView) ingredient.findViewById(R.id.name);
                                name.setText(in.name);
                                TextView quantity = (TextView) ingredient.findViewById(R.id.quantity);
                                quantity.setText(in.quantity);
                                ingredients.addView(ingredient);
                                if (i + 1 != recipe.ingredients.size()) {
                                    TableRow line = (TableRow) a.getLayoutInflater().inflate(R.layout.line_table_row,
                                            null);
                                    ingredients.addView(line);
                                }
                            }
                            if (1 == ingredients.getChildCount() && "".equals(recipe.ingredients.get(0).name.trim())
                                    && "".equals(recipe.ingredients.get(0).quantity.trim())) {
                                ingredients.removeViewAt(0);
                            }
                            if (0 == ingredients.getChildCount()) {
                                TableRow ingredient = (TableRow) a.getLayoutInflater().inflate(
                                        R.layout.ingredient_table_row, null, true);
                                TextView name = (TextView) ingredient.findViewById(R.id.name);
                                name.setHint(getText(R.string.edit_recipe_textview_hint_ingredient));
                                ingredients.addView(ingredient);
                            }
                            stepLabel.setVisibility(View.VISIBLE);
                            for (int i = 0; i < recipe.steps.size(); i++) {
                                Step s = recipe.steps.get(i);
                                TableRow step = (TableRow) a.getLayoutInflater().inflate(R.layout.step_table_row, null);
                                TextView number = (TextView) step.findViewById(R.id.number);
                                number.setText("" + (i + 1));
                                TextView text = (TextView) step.findViewById(R.id.text);
                                text.setText(s.text);
                                ImageView stepPhoto = (ImageView) step.findViewById(R.id.stepPhoto);
                                if (null != s.photoUrl && !"".equals(s.photoUrl)) {
                                    if (null == s.photo) {
                                        s.photo = ImageLoadCache.getImage(s.photoUrl);
                                    }
                                    if (null == s.photo) {
                                        new ImageLoadThread(handler, stepPhoto, true, 0.44f, 20).execute(s.photoUrl);
                                    } else {
//                                        int width = (int) (display.getWidth() * 0.44f) - 20;
                                        Point size = new Point();
                                        display.getSize(size);
                                        int width = (int) (size.x * 0.44f) - 20;
                                        int height = s.photo.getHeight() * width / s.photo.getWidth();
                                        stepPhoto.setLayoutParams(new LinearLayout.LayoutParams(width, height));
                                        stepPhoto.setImageBitmap(s.photo);
                                        stepPhoto.setVisibility(View.VISIBLE);
                                    }
                                    // stepPhoto.setImageBitmap(s.photo);
                                } else {
                                    TableRow.LayoutParams params = new TableRow.LayoutParams();
                                    params.span = 2;
                                    text.setLayoutParams(params);
                                }
                                steps.addView(step);
                                if (i + 1 != recipe.steps.size()) {
                                    TableRow line = (TableRow) a.getLayoutInflater().inflate(R.layout.line_table_row_3,
                                            null);
                                    steps.addView(line);
                                }
                            }
                            if (1 == steps.getChildCount() && "".equals(recipe.steps.get(0).text.trim())
                                    && "".equals(recipe.steps.get(0).photoUrl)) {
                                steps.removeViewAt(0);
                            }
                            if (0 == steps.getChildCount()) {
                                TableRow step = (TableRow) a.getLayoutInflater().inflate(R.layout.step_table_row, null,
                                        true);
                                TextView number = (TextView) step.findViewById(R.id.number);
                                number.setHint(getText(R.string.edit_recipe_textview_hint_step));
                                steps.addView(step);
                            }
                            adviceLabel.setVisibility(View.VISIBLE);
                            advice.setText(recipe.advice);
                            advice.setVisibility(View.VISIBLE);
                            recipeId.setVisibility(View.VISIBLE);
                            recipeId.setText(recipeId.getText() + recipe.id);
                        }

                        if (null != dialog) {
                            dialog.dismiss();
                        }

                        if (!successNetwork) {
                            if (null == res || "".equals(res)) {
                                Toast.makeText(
                                        a,
                                        getText(R.string.dialog_network_error_title) + "\n"
                                                + getText(R.string.dialog_network_error_message), Toast.LENGTH_LONG)
                                        .show();
                            } else {
                                Toast.makeText(
                                        a,
                                        getText(R.string.dialog_http_error_title) + "\n"
                                                + getText(R.string.dialog_input_error_message_unexpected_err) + "\n"
                                                + "Error Code " + res, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
            }
        }

        new GetRecipeThread().execute(id);

        setEditting(false);

        return v;
    }

    class SaveOrPublishOrDraftThread extends AsyncTask<Recipe, Void, String> {
        boolean successNetwork = true;

        Recipe recipe;

        public SaveOrPublishOrDraftThread() {
            super();
        }

        protected void onPreExecute() {
            handler.postDelayed(new Runnable() {
                public void run() {
//					dialog = ProgressDialog.show(a, "", getText(R.string.dialog_progress).toString(), true);
                    dialog = ProgressDialogFragment.newInstance("", getString(R.string.dialog_progress));
                    dialog.show(a.getSupportFragmentManager(), "CreateThread");
                }
            }, 0);
        }

        protected String doInBackground(Recipe... recipe) {
            return "";
        }

        protected void onPostExecute(final String res) {
            handler.post(new Runnable() {
                public void run() {
                    if (successNetwork) {
                        if ("success".equals(res)) {
                            SaveOrPublishOrDraftThread.this.doSuccess(res);
                        } else {
                            SaveOrPublishOrDraftThread.this.doFailed(res);
                        }
                    }

                    if (null != dialog) {
                        dialog.dismiss();
                        dialog = null;
                    }

                    if (!successNetwork) {
                        if (null == res || "".equals(res)) {
                            Toast.makeText(
                                    a,
                                    getText(R.string.dialog_network_error_title) + "\n"
                                            + getText(R.string.dialog_network_error_message), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(
                                    a,
                                    getText(R.string.dialog_http_error_title) + "\n"
                                            + getText(R.string.dialog_input_error_message_unexpected_err) + "\n"
                                            + "Error Code " + res, Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
        }

        protected void doSuccess(String res) {
        }

        protected void doFailed(String res) {
        }
    }

    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            this.down.setVisibility(View.GONE);
        } else {
            this.down.setVisibility(View.VISIBLE);
        }
    }

    public void setEditting(boolean editting) {
        TextView saveText = (TextView) v.findViewById(R.id.saveText);
        TextView publishText = (TextView) v.findViewById(R.id.publishText);
        TextView draftText = (TextView) v.findViewById(R.id.draftText);
        Button save = (Button) v.findViewById(R.id.save);
        Button publish = (Button) v.findViewById(R.id.publish);
        Button draft = (Button) v.findViewById(R.id.draft);
        if (isPublish) {
            saveText.setVisibility(View.GONE);
            save.setVisibility(View.GONE);
            publishText.setVisibility(View.GONE);
            publish.setVisibility(View.GONE);
            draftText.setVisibility(View.VISIBLE);
            draft.setVisibility(View.VISIBLE);
        } else {
            if (editting) {
                saveText.setVisibility(View.VISIBLE);
                save.setVisibility(View.VISIBLE);
                publishText.setVisibility(View.GONE);
                publish.setVisibility(View.GONE);
                draftText.setVisibility(View.GONE);
                draft.setVisibility(View.GONE);
            } else {
                saveText.setVisibility(View.GONE);
                save.setVisibility(View.GONE);
                publishText.setVisibility(View.VISIBLE);
                publish.setVisibility(View.VISIBLE);
                draftText.setVisibility(View.GONE);
                draft.setVisibility(View.GONE);
            }
            isEditting = editting;
        }
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
                setEditting(true);
            }
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

        public static EditRecipeFragment.PhotoDialogFragment newInstance() {
            return new EditRecipeFragment.PhotoDialogFragment();
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putParcelable(BITMAP_PHOTO_EE, ((BitmapDrawable)photoEE.getDrawable()).getBitmap());
        }

        public void setImageView(ImageView photo) {
            this.photo = photo;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            EditRecipeGroupActivity editRecipeActivity = (EditRecipeGroupActivity) getActivity();
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

            return photoDialog;
        }
    }
}
