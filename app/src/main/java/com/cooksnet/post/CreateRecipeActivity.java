package com.cooksnet.post;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.InputFilter;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cooksnet.obj.Recipe;
import com.cooksnet.util.CooksNetWebAccess;
import com.cooksnet.util.MultibyteLengthFilter;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CreateRecipeActivity extends BaseActivity {

    private final MultibyteLengthFilter titleLengthFilter = new MultibyteLengthFilter(30, 20, 80, ExtrasData.profile.locale);

    private ProgressDialogFragment dialog;
    private Handler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.create_recipe);

        setTitle(R.string.create_recipe_title);

        handler = new Handler();

        final EditText title = (EditText) findViewById(R.id.title);
        title.setFilters(new InputFilter[]{titleLengthFilter});

        final ImageView photo = (ImageView) findViewById(R.id.photo);
//        final LinearLayout photoView = (LinearLayout) ((LayoutInflater) CreateRecipeActivity.this
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dialog_photo, null, false);
//        final ImageView photoEE = (ImageView) photoView.findViewById(R.id.photo);
//        final AlertDialog photoDialog = new AlertDialog.Builder(CreateRecipeActivity.this)
//                .setTitle(getText(R.string.dialog_photo_title)).setNegativeButton(getText(R.string.dialog_close), null)
//                .setView(photoView).create();
        photo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                photoEE.setLayoutParams(photo.getLayoutParams());
//                photoEE.setImageBitmap(((BitmapDrawable) photo.getDrawable()).getBitmap());
//                photoDialog.show();
                PhotoDialogFragment photoDialog = PhotoDialogFragment.newInstance();
                photoDialog.setImageView(photo);
                photoDialog.show(getSupportFragmentManager(), "PhotoDialog");
            }
        });
//        ImageView camera = (ImageView) photoView.findViewById(R.id.camera);
//        camera.setOnClickListener(new CameraOnClickListener(EditRecipeFragment.PHOTO_SIZE, photoEE, photo));
//        ImageView gallery = (ImageView) photoView.findViewById(R.id.gallery);
//        gallery.setOnClickListener(new GralleryOnClickListener(EditRecipeFragment.PHOTO_SIZE, photoEE, photo));
//        ImageView rotate = (ImageView) photoView.findViewById(R.id.rotate);
//        rotate.setOnClickListener(new RotateOnClickListener(EditRecipeFragment.PHOTO_SIZE, photoEE, photo));
//        ImageView crop = (ImageView) photoView.findViewById(R.id.crop);
//        crop.setOnClickListener(new CropOnClickListener(EditRecipeFragment.PHOTO_CROP_SIZE, photoEE, photo));

        final TextView guideline = (TextView) findViewById(R.id.guideline);
        guideline.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CreateRecipeActivity.this, HtmlWebViewActivity.class);
                i.putExtra("title", getText(R.string.guideline_title));
                i.putExtra("url", CooksNetWebAccess.URL_GUIDELINE);
                startChildActivity(i);
            }
        });

        final CheckBox checkGuideline = (CheckBox) findViewById(R.id.checkGuideline);

        Button create = (Button) findViewById(R.id.create);
        create.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(title.getText().toString().trim())) {
//                    new AlertDialog.Builder(CreateRecipeActivity.this).setMessage(R.string.dialog_none_title)
//                            .setPositiveButton(R.string.dialog_ok, null).create().show();
                    AlertDialogFragment.newInstance("", getString(R.string.dialog_none_title), "")
                            .show(getSupportFragmentManager(), "NoneTitle");
                    return;
                }
                if (null == photo.getTag()) {
//                    new AlertDialog.Builder(CreateRecipeActivity.this).setMessage(R.string.dialog_none_photo)
//                            .setPositiveButton(R.string.dialog_ok, null).create().show();
                    AlertDialogFragment.newInstance("", getString(R.string.dialog_none_photo), "")
                            .show(getSupportFragmentManager(), "NoneTitle");
                    return;
                }
                if (!checkGuideline.isChecked()) {
//                    new AlertDialog.Builder(CreateRecipeActivity.this).setMessage(R.string.dialog_accept_guideline)
//                            .setPositiveButton(R.string.dialog_ok, null).create().show();
                    AlertDialogFragment.newInstance("", getString(R.string.dialog_accept_guideline), "")
                            .show(getSupportFragmentManager(), "NoneTitle");
                    return;
                }

                Bitmap bitmap = ((BitmapDrawable) photo.getDrawable()).getBitmap();

                File photo = null;
                try {
                    photo = saveBitmapToPrivateStrage(bitmap, EditRecipeFragment.PHOTO_NAME,
                            Context.MODE_PRIVATE);
                } catch (IOException ioe) {
                }

                Recipe recipe = new Recipe();
                recipe.title = title.getText().toString().trim();
                recipe.photoFile = photo;

                new CreateThread().execute(recipe);
            }
        });
    }

    class CreateThread extends AsyncTask<Recipe, Void, String> {
        private boolean successNetwork = true;

        private Recipe recipe;

        public CreateThread() {
            super();
        }

        protected void onPreExecute() {
            handler.postDelayed(new Runnable() {
                public void run() {
//					dialog = ProgressDialog.show(CreateRecipeActivity.this, "", getText(R.string.dialog_progress)
//							.toString(), true);
                    dialog = ProgressDialogFragment.newInstance("", getString(R.string.dialog_progress));
                    dialog.show(getSupportFragmentManager(), "CreateThread");
                }
            }, 0);
        }

        protected String doInBackground(Recipe... recipe) {
            try {
                this.recipe = new CooksNetWebAccess().createRecipe(recipe[0]);
                if (null != this.recipe && Recipe.CREATE_SUCCESS.equals(this.recipe.createStatus)) {
                    return "success";
                } else {
                    return this.recipe.createErrMsg;
                }
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
                        if ("success".equals(res)) {
                            Map<String, Serializable> param = new HashMap<String, Serializable>();
                            param.put("id", CreateThread.this.recipe.id);
                            startChildActivity(new Intent(CreateRecipeActivity.this, EditRecipeGroupActivity.class), param);
                            finish();
                        } else {
//                            new AlertDialog.Builder(CreateRecipeActivity.this).setMessage(res)
//                                    .setPositiveButton(R.string.dialog_ok, null).create().show();
                            AlertDialogFragment.newInstance("", res, "")
                                    .show(getSupportFragmentManager(), "NoneTitle");
                        }
                    }

                    if (null != dialog) {
                        dialog.dismiss();
                        dialog = null;
                    }

                    if (!successNetwork) {
                        if (null == res || "".equals(res)) {
                            Toast.makeText(
                                    CreateRecipeActivity.this,
                                    getText(R.string.dialog_network_error_title) + "\n"
                                            + getText(R.string.dialog_network_error_message), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(
                                    CreateRecipeActivity.this,
                                    getText(R.string.dialog_http_error_title) + "\n"
                                            + getText(R.string.dialog_input_error_message_unexpected_err) + "\n"
                                            + "Error Code " + res, Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
        }
    }

    protected void onPhotoChanged(Bitmap bitmap, ImageView... photo) {
        Display display = ((WindowManager) CreateRecipeActivity.this.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay();
        for (ImageView aPhoto : photo) {
//            int width = (int) (display.getWidth() * 0.5) - 20;
            Point size = new Point();
            display.getSize(size);
            int width = (int) (size.x * 0.5) - 20;
            int height = bitmap.getHeight() * width / bitmap.getWidth();
            aPhoto.setLayoutParams(new LinearLayout.LayoutParams(width, height));
        }
    }

    public static class PhotoDialogFragment extends DialogFragment {
        private static final String BITMAP_PHOTO_EE = "bitmapPhotoEE";

        ImageView photoEE;
        static ImageView photo;

        public static PhotoDialogFragment newInstance() {
            return new PhotoDialogFragment();
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
            CreateRecipeActivity createRecipeActivity = (CreateRecipeActivity) getActivity();
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
            camera.setOnClickListener(createRecipeActivity.new CameraOnClickListener(EditRecipeFragment.PHOTO_SIZE, photoEE, photo));
            ImageView gallery = (ImageView) photoView.findViewById(R.id.gallery);
            gallery.setOnClickListener(createRecipeActivity.new GralleryOnClickListener(EditRecipeFragment.PHOTO_SIZE, photoEE, photo));
            ImageView rotate = (ImageView) photoView.findViewById(R.id.rotate);
            rotate.setOnClickListener(createRecipeActivity.new RotateOnClickListener(EditRecipeFragment.PHOTO_SIZE, photoEE, photo));
            ImageView crop = (ImageView) photoView.findViewById(R.id.crop);
            crop.setOnClickListener(createRecipeActivity.new CropOnClickListener(EditRecipeFragment.PHOTO_CROP_SIZE, photoEE, photo));

            return photoDialog;
        }
    }
}