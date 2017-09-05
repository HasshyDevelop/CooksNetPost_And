package com.cooksnet.post;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;

public abstract class BaseActivity extends AppCompatActivity {

    public static final int PERMISSION_REQUEST_CODE = 111;

    public static final String SHARED_NAME = "com.cooksnet.post";
    public static final String PREF_SAVE = "save";
    public static final String PREF_NICKNAME = "nickname";
    public static final String PREF_PASSWORD = "password";
    public static final String PREF_CURRENT_TAB = "currentTab";
    public static final String TAB_PUBLISH = "publish";
    public static final String TAB_DRAFT = "draft";

    public static final int REQUEST_CAMERA = 1;
    public static final int REQUEST_GALLERY = 2;
    public static final int REQUEST_CROP = 3;

    public static final String TEMP_PHOTO_NAME_FOR_CAMERA = "temp_photo.jpg";

    String fromActivity;

    private File photoFileForCamera;

    private ImageView[] photo;
    private int[] resize;
    private int[] resizeCrop;
    private int rotate = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionbar = getSupportActionBar();
        int color = R.drawable.search_grad;
//        Drawable backgroundDrawable = getApplicationContext().getResources().getDrawable(color);
        Drawable backgroundDrawable = ContextCompat.getDrawable(this, color);
        actionbar.setBackgroundDrawable(backgroundDrawable);
        fromActivity = getIntent().getStringExtra("fromActivity");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        if (this.getClass().getName().equals(EditRecipeGroupActivity.class.getName()) ||
                this.getClass().getName().equals(LoginActivity.class.getName())) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {
        super.onOptionsItemSelected(menu);
        String myname = this.getClass().getName();
        if (CreateRecipeActivity.class.getName().equals(myname)) {
            switch (menu.getItemId()) {
                case R.id.menu_create_recipe:
                    break;
                case R.id.menu_my_recipe:
                    if (MoreActivity.class.getName().equals(fromActivity)) {
                        setAllFinishActivity();
                        startActivity(new Intent(this, MyRecipeActivity.class));
                        finish();
                    } else {
                        finish();
                    }
                    break;
                case R.id.menu_more:
                    if (MyRecipeActivity.class.getName().equals(fromActivity)) {
                        startChildActivity(new Intent(this, MoreActivity.class));
                    } else {
                        finish();
                    }
                    break;
                case R.id.menu_exit:
//                    new AlertDialog.Builder(BaseActivity.this).setTitle(getText(R.string.dialog_exit))
//                            .setPositiveButton(getText(R.string.dialog_ok), new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int whichButton) {
//                                    setAllFinishActivity();
//                                    finish();
//                                }
//                            }).setNeutralButton(getText(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int whichButton) {
//                        }
//                    }).show();
                    AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(getString(R.string.dialog_exit), "", "", "", 2, null, null);
                    alertDialogFragment.setListener(new AlertDialogListener() {
                        @Override
                        public void doPositiveClick(int id) {
                            setAllFinishActivity();
                            finish();
                        }

                        @Override
                        public void doNegativeClick(int id) {
                        }
                    });
                    alertDialogFragment.show(getSupportFragmentManager(), "NoneTitle");
                    break;
            }
        }
        if (MyRecipeActivity.class.getName().equals(myname)) {
            switch (menu.getItemId()) {
                case R.id.menu_create_recipe:
                    startChildActivity(new Intent(this, CreateRecipeActivity.class));
                    break;
                case R.id.menu_my_recipe:
                    break;
                case R.id.menu_more:
                    startChildActivity(new Intent(this, MoreActivity.class));
                    break;
                case R.id.menu_exit:
//                    new AlertDialog.Builder(BaseActivity.this).setTitle(getText(R.string.dialog_exit))
//                            .setPositiveButton(getText(R.string.dialog_ok), new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int whichButton) {
//                                    setAllFinishActivity();
//                                    finish();
//                                }
//                            }).setNeutralButton(getText(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int whichButton) {
//                        }
//                    }).show();
                    AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(getString(R.string.dialog_exit), "", "", "", 2, null, null);
                    alertDialogFragment.setListener(new AlertDialogListener() {
                        @Override
                        public void doPositiveClick(int id) {
                            setAllFinishActivity();
                            finish();
                        }

                        @Override
                        public void doNegativeClick(int id) {
                        }
                    });
                    alertDialogFragment.show(getSupportFragmentManager(), "NoneTitle");
                    break;
            }
        }
        if (MoreActivity.class.getName().equals(myname)) {
            switch (menu.getItemId()) {
                case R.id.menu_create_recipe:
                    if (MyRecipeActivity.class.getName().equals(fromActivity)) {
                        startChildActivity(new Intent(this, CreateRecipeActivity.class));
                    } else {
                        finish();
                    }
                    break;
                case R.id.menu_my_recipe:
                    if (CreateRecipeActivity.class.getName().equals(fromActivity)) {
                        setAllFinishActivity();
                        startActivity(new Intent(this, MyRecipeActivity.class));
                        finish();
                    } else {
                        finish();
                    }
                    break;
                case R.id.menu_more:
                    break;
                case R.id.menu_exit:
//                    new AlertDialog.Builder(BaseActivity.this).setTitle(getText(R.string.dialog_exit))
//                            .setPositiveButton(getText(R.string.dialog_ok), new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int whichButton) {
//                                    setAllFinishActivity();
//                                    finish();
//                                }
//                            }).setNeutralButton(getText(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int whichButton) {
//                        }
//                    }).show();
                    AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(getString(R.string.dialog_exit), "", "", "", 2, null, null);
                    alertDialogFragment.setListener(new AlertDialogListener() {
                        @Override
                        public void doPositiveClick(int id) {
                            setAllFinishActivity();
                            finish();
                        }

                        @Override
                        public void doNegativeClick(int id) {
                        }
                    });
                    alertDialogFragment.show(getSupportFragmentManager(), "NoneTitle");
                    break;
            }
        }
        if (AboutActivity.class.getName().equals(myname) ||
                AccountActivity.class.getName().equals(myname) || HtmlWebViewActivity.class.getName().equals(myname)) {
            switch (menu.getItemId()) {
                case R.id.menu_create_recipe:
                    setAllFinishActivity();
                    startActivity(new Intent(this, CreateRecipeActivity.class));
                    finish();
                    break;
                case R.id.menu_my_recipe:
                    setAllFinishActivity();
                    startActivity(new Intent(this, MyRecipeActivity.class));
                    finish();
                    break;
                case R.id.menu_more:
                    setAllFinishActivity();
                    startActivity(new Intent(this, MoreActivity.class));
                    finish();
                    break;
                case R.id.menu_exit:
//                    new AlertDialog.Builder(BaseActivity.this).setTitle(getText(R.string.dialog_exit))
//                            .setPositiveButton(getText(R.string.dialog_ok), new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int whichButton) {
//                                    setAllFinishActivity();
//                                    finish();
//                                }
//                            }).setNeutralButton(getText(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int whichButton) {
//                        }
//                    }).show();
                    AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(getString(R.string.dialog_exit), "", "", "", 2, null, null);
                    alertDialogFragment.setListener(new AlertDialogListener() {
                        @Override
                        public void doPositiveClick(int id) {
                            setAllFinishActivity();
                            finish();
                        }

                        @Override
                        public void doNegativeClick(int id) {
                        }
                    });
                    alertDialogFragment.show(getSupportFragmentManager(), "NoneTitle");
                    break;
            }
        }
        return true;
    }

    public void startChildActivity(Intent i) {
        i.putExtra("fromActivity", this.getClass().getName());
        startActivityForResult(i, 999);
    }

    public void startChildActivity(Intent i, Map<String, Serializable> param) {
        for (Map.Entry<String, Serializable> e : param.entrySet()) {
            i.putExtra(e.getKey(), e.getValue());
        }
        startChildActivity(i);
    }

    protected void setAllFinishActivity() {
        Intent intent = new Intent();
        intent.putExtra("isFinish", "all");
        setResult(Activity.RESULT_OK, intent);
    }

    class CameraOnClickListener implements OnClickListener {
        private ImageView[] photo;
        private int[] resize;

        public CameraOnClickListener(int[] resize, ImageView... photo) {
            BaseActivity.this.photo = photo;
            BaseActivity.this.resize = resize;
            this.photo = photo;
            this.resize = resize;
        }

        @Override
        public void onClick(View v) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                if (RuntimePermissionUtils.hasSelfPermissions(BaseActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    photoFileForCamera = initialPublicStoragePhotoFile(TEMP_PHOTO_NAME_FOR_CAMERA);
                    Intent intent = new Intent();
                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFileForCamera));
                    startActivityForResult(intent, REQUEST_CAMERA);
                    BaseActivity.this.photo = this.photo;
                    BaseActivity.this.resize = this.resize;
                } else {
                    ActivityCompat.requestPermissions(BaseActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,}, PERMISSION_REQUEST_CODE);
                    return;
                }
            } else {
                photoFileForCamera = initialPublicStoragePhotoFile(TEMP_PHOTO_NAME_FOR_CAMERA);
                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFileForCamera));
                startActivityForResult(intent, REQUEST_CAMERA);
                BaseActivity.this.photo = this.photo;
                BaseActivity.this.resize = this.resize;
            }
        }
    }

    class GralleryOnClickListener implements OnClickListener {
        private ImageView[] photo;
        private int[] resize;

        public GralleryOnClickListener(int[] resize, ImageView... photo) {
            BaseActivity.this.photo = photo;
            BaseActivity.this.resize = resize;
            this.photo = photo;
            this.resize = resize;
        }

        @Override
        public void onClick(View v) {
            // photoFileForCamera =
            // initialPrivateStoragePhotoFile(TEMP_PHOTO_NAME_FOR_CAMERA);
            // Intent intent = new Intent(Intent.ACTION_PICK,
            // android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            // intent.addCategory(Intent.CATEGORY_OPENABLE);
            // intent.setDataAndType(Uri.parse(photoFileForCamera.getAbsolutePath()),
            // "image/*");

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                if (RuntimePermissionUtils.hasSelfPermissions(BaseActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, REQUEST_GALLERY);
                    BaseActivity.this.photo = this.photo;
                    BaseActivity.this.resize = this.resize;
                } else {
                    ActivityCompat.requestPermissions(BaseActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,}, PERMISSION_REQUEST_CODE);
                    return;
                }
            } else {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_GALLERY);
                BaseActivity.this.photo = this.photo;
                BaseActivity.this.resize = this.resize;
            }
        }
    }

    class RotateOnClickListener implements OnClickListener {
        private ImageView[] photo;
        private int[] resize;

        public RotateOnClickListener(int[] resize, ImageView... photo) {
            BaseActivity.this.photo = photo;
            BaseActivity.this.resize = resize;
            this.photo = photo;
            this.resize = resize;
        }

        @Override
        public void onClick(View v) {
            try {
                Uri photoUri = (Uri) photo[0].getTag();
                if (null == photoUri) {
                    return;
                }

                rotate += 90;

                InputStream is;
                if (null != photoFileForCamera) {
                    is = new FileInputStream(photoFileForCamera);
                } else {
                    is = getContentResolver().openInputStream(photoUri);
                }
                BitmapFactory.Options op = new BitmapFactory.Options();
                op.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(is, null, op);
                is.close();

                if (null != photoFileForCamera) {
                    is = new FileInputStream(photoFileForCamera);
                } else {
                    is = getContentResolver().openInputStream(photoUri);
                }

                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = false;
                if (4500 < op.outWidth || 4500 < op.outHeight) {
                    o.inSampleSize = 8;
                } else if (3500 < op.outWidth || 3500 < op.outHeight) {
                    o.inSampleSize = 6;
                } else if (2500 < op.outWidth || 2500 < op.outHeight) {
                    o.inSampleSize = 4;
                } else if (1500 < op.outWidth || 1500 < op.outHeight) {
                    o.inSampleSize = 2;
                }
                Bitmap src = BitmapFactory.decodeStream(is, null, o);

                is.close();

                Bitmap rotateBitmap = null;
                if (rotate == 360) {
                    rotateBitmap = src;
                    rotate = 0;
                } else {
                    Matrix matrix = new Matrix();
                    matrix.postScale(1, 1);
                    matrix.postRotate(rotate);
                    rotateBitmap = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
                    src.recycle();
                }

                Bitmap bitmap = resizeBitmap(rotateBitmap, resize[0], resize[1], resize[2], resize[3], resize[4],
                        resize[5]);
                rotateBitmap.recycle();

                for (int i = 0; i < photo.length; i++) {
                    photo[i].setImageBitmap(bitmap);
                    photo[i].setTag(photoUri);
                }

                onPhotoChanged(bitmap, photo);

            } catch (FileNotFoundException fnfe) {
                fnfe.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    class CropOnClickListener implements OnClickListener {
        private ImageView[] photo;
        private int[] resizeCrop;

        public CropOnClickListener(int[] resizeCrop, ImageView... photo) {
            BaseActivity.this.photo = photo;
            BaseActivity.this.resizeCrop = resizeCrop;
            this.photo = photo;
            this.resizeCrop = resizeCrop;
        }

        @Override
        public void onClick(View v) {
            Log.d("", "crop on click");
            for (int i = 0; i < photo.length; i++) {
                Uri photoUri = (Uri) photo[i].getTag();
                if (null == photoUri) {
                    continue;
                }
                Log.d("", photoUri.toString());

                // Google DriveからはCROPでエラーになるのでプライベートストレージのを登録する
                if (photoUri.toString().startsWith("content://com.google.android.apps.docs")) {
                    Bitmap bitmap = BitmapFactory.decodeFile(photoFileForCamera.getPath());
                    Log.d("", "change" + photoFileForCamera.getPath());
                    photoUri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap,
                            photoFileForCamera.toString(), null));

                    for (int j = 0; j < photo.length; j++) {
                        photo[j].setImageBitmap(bitmap);
                        photo[j].setTag(photoUri);
                    }

                    onPhotoChanged(bitmap, photo);
                }
                Log.d("", "path" + photoUri.getPath());

                if (photoUri.toString().startsWith("file")) {
                    /*
                     * Cursor ce =
					 * getContentResolver().query(MediaStore.Images.Media
					 * .EXTERNAL_CONTENT_URI, null, null, null, null); Log.d("",
					 * "edata" + ce.getCount()); ce.moveToFirst(); if (0 !=
					 * ce.getCount() && !ce.isLast()) { while (!ce.isLast()) {
					 * Log.d("", "edata" +
					 * ce.getString(ce.getColumnIndex(MediaStore
					 * .MediaColumns.DATA))); Log.d("", "edata" +
					 * ce.getString(ce
					 * .getColumnIndex(MediaStore.MediaColumns.TITLE)));
					 * ce.moveToNext(); } Log.d("", "edata" +
					 * ce.getString(ce.getColumnIndex
					 * (MediaStore.MediaColumns.DATA))); } ce.close(); Cursor ci
					 * = getContentResolver().query(MediaStore.Images.Media.
					 * INTERNAL_CONTENT_URI, null, null, null, null); Log.d("",
					 * "idata" + ci.getCount()); ci.moveToFirst(); if (0 !=
					 * ci.getCount() && !ci.isLast()) { while (!ci.isLast()) {
					 * Log.d("", "idata" +
					 * ci.getString(ci.getColumnIndex(MediaStore
					 * .MediaColumns.DATA))); ci.moveToNext(); } Log.d("",
					 * "idata" +
					 * ci.getString(ce.getColumnIndex(MediaStore.MediaColumns
					 * .DATA))); } ci.close();
					 */
                    Cursor c = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                            MediaStore.Images.ImageColumns.TITLE + " = ?", new String[]{photoUri.getPath()}, null);
                    if (0 < c.getCount()) {
                        Log.d("", "content");
                        c.moveToFirst();
                        photoUri = Uri.parse("content://media/external/images/media/"
                                + c.getInt(c.getColumnIndex(MediaStore.MediaColumns._ID)));
                        c.close();
                    } else {
                        c.close();
                        Bitmap src = BitmapFactory.decodeFile(photoUri.getPath());
                        Log.d("", "src" + photoUri.getPath());
                        photoUri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), src,
                                photoUri.getPath(), null));
                    }
                }

                Log.d("", photoUri.getPath());
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setData(photoUri);
                intent.putExtra("outputX", 300);
                intent.putExtra("outputY", 300);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, REQUEST_CROP);
                BaseActivity.this.photo = this.photo;
                BaseActivity.this.resizeCrop = this.resizeCrop;
                break;
            }
        }
    }

    private File initialPrivateStoragePhotoFile(String photoName) {

        FileOutputStream fos = null;
        try {
            Resources res = this.getApplicationContext().getResources();
            Bitmap defalutRecipesPhoto = BitmapFactory.decodeResource(res, R.drawable.default_photo);

            fos = openFileOutput(photoName, Context.MODE_PRIVATE);
            defalutRecipesPhoto.compress(CompressFormat.JPEG, 100, fos);
            fos.flush();

            return getFileStreamPath(photoName);

        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private File initialPublicStoragePhotoFile(String photoName) {

        FileOutputStream fos = null;
        try {
            File photoPublic = new File(getExternalFilesDir(null), photoName);
            Resources res = this.getApplicationContext().getResources();
            Bitmap defalutRecipesPhoto = BitmapFactory.decodeResource(res, R.drawable.default_photo);

            photoPublic.createNewFile();
            fos = new FileOutputStream(photoPublic);
            defalutRecipesPhoto.compress(CompressFormat.JPEG, 100, fos);
            fos.flush();

            return photoPublic;

        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 999:
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        String text = extras.getString("isFinish");
                        if ("all".equals(text)) {
                            setAllFinishActivity();
                            finish();
                        }
                    }
                    break;

                case REQUEST_CAMERA:
                    try {
                        // カメラからは既に保存されている
                        InputStream is = new FileInputStream(photoFileForCamera);

                        BitmapFactory.Options op = new BitmapFactory.Options();
                        op.inJustDecodeBounds = true;
                        BitmapFactory.decodeStream(is, null, op);
                        is.close();

                        is = new FileInputStream(photoFileForCamera);
                        BitmapFactory.Options o = new BitmapFactory.Options();
                        o.inJustDecodeBounds = false;
                        if (4500 < op.outWidth || 4500 < op.outHeight) {
                            o.inSampleSize = 8;
                        } else if (3500 < op.outWidth || 3500 < op.outHeight) {
                            o.inSampleSize = 6;
                        } else if (2500 < op.outWidth || 2500 < op.outHeight) {
                            o.inSampleSize = 4;
                        } else if (1500 < op.outWidth || 1500 < op.outHeight) {
                            o.inSampleSize = 2;
                        }
                        Bitmap src = BitmapFactory.decodeStream(is, null, o);

                        is.close();

                        // MediaStoreへ追加しないとクロップできない
                        String uri = MediaStore.Images.Media.insertImage(getContentResolver(), src,
                                photoFileForCamera.getName(), null);

                        if (null != uri) {
                            Uri photoUri = Uri.parse(uri);

                            Bitmap bitmap = resizeBitmap(src, resize[0], resize[1], resize[2], resize[3], resize[4], resize[5]);
                            src.recycle();

                            for (int i = 0; i < photo.length; i++) {
                                photo[i].setImageBitmap(bitmap);
                                photo[i].setTag(photoUri);
                            }

                            onPhotoChanged(bitmap, photo);
                        }

                    } catch (FileNotFoundException fnfe) {
                        fnfe.printStackTrace();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }

                    rotate = 0;
                    break;

                case REQUEST_GALLERY:
                    try {
                        // ギャラリーからはUriで返ってくるGoogle Driveなんかも増えた
                        Uri uri = data.getData();
                        Log.d("", "gallery " + uri.toString());
                        InputStream is = getContentResolver().openInputStream(uri);

                        BitmapFactory.Options op = new BitmapFactory.Options();
                        op.inJustDecodeBounds = true;
                        Bitmap ss = BitmapFactory.decodeStream(is, null, op);
                        is.close();

                        is = getContentResolver().openInputStream(uri);
                        BitmapFactory.Options o = new BitmapFactory.Options();
                        o.inJustDecodeBounds = false;
                        if (4500 < op.outWidth || 4500 < op.outHeight) {
                            o.inSampleSize = 8;
                        } else if (3500 < op.outWidth || 3500 < op.outHeight) {
                            o.inSampleSize = 6;
                        } else if (2500 < op.outWidth || 2500 < op.outHeight) {
                            o.inSampleSize = 4;
                        } else if (1500 < op.outWidth || 1500 < op.outHeight) {
                            o.inSampleSize = 2;
                        }
                        Bitmap src = BitmapFactory.decodeStream(is, null, o);
                        is.close();

                        if (null == src) {
//                            new AlertDialog.Builder(this).setMessage(uri.toString() + " \nis not supported.")
//                                    .setPositiveButton(getText(R.string.dialog_ok), new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int whichButton) {
//                                        }
//                                    }).create().show();
                            AlertDialogFragment.newInstance("", uri.toString() + " \nis not supported.", "")
                                    .show(getSupportFragmentManager(), "NoneTitle");
                            break;
                        }

                        Uri photoUri = uri;

                        photoFileForCamera = saveBitmapToPrivateStrage(src, TEMP_PHOTO_NAME_FOR_CAMERA,
                                Context.MODE_PRIVATE);

                        Bitmap bitmap = resizeBitmap(src, resize[0], resize[1], resize[2], resize[3], resize[4], resize[5]);
                        src.recycle();

                        Log.d("", "" + photoUri.toString());
                        Log.d("", "src " + src);
                        for (int i = 0; i < photo.length; i++) {
                            photo[i].setImageBitmap(bitmap);
                            photo[i].setTag(photoUri);
                        }

                        onPhotoChanged(bitmap, photo);

                    } catch (FileNotFoundException fnfe) {
                        fnfe.printStackTrace();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }

                    rotate = 0;
                    break;

                case REQUEST_CROP:
                    try {
                        // クロップからはBitmapデータで返ってくる
                        Bitmap src = data.getExtras().getParcelable("data");

                        photoFileForCamera = saveBitmapToPrivateStrage(src, TEMP_PHOTO_NAME_FOR_CAMERA,
                                Context.MODE_PRIVATE);

                        Bitmap bitmap = resizeBitmap(src, resizeCrop[0], resizeCrop[1], resizeCrop[2], resizeCrop[3],
                                resizeCrop[4], resizeCrop[5]);
                        src.recycle();

                        for (int i = 0; i < photo.length; i++) {
                            photo[i].setImageBitmap(bitmap);
                        }

                        onPhotoChanged(bitmap, photo);

                    } catch (FileNotFoundException fnfe) {
                        fnfe.printStackTrace();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }

                    rotate = 0;
                    break;
            }
        }
    }

    protected void onPhotoChanged(Bitmap bitmap, ImageView... photo) {
    }

    public File saveBitmapToPrivateStrage(Bitmap bitmap, String fileName, int mode) throws IOException {
        File photo = null;
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(fileName, mode);
            bitmap.compress(CompressFormat.JPEG, 100, fos);
            fos.flush();

            photo = getFileStreamPath(fileName);

            return photo;

        } catch (IOException ioe) {
            throw ioe;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    throw e;
                }
            }
        }
    }

    public Bitmap resizeBitmap(Bitmap src, int vw, int vh, int hw, int hh, int dw, int dh) {
        // カット
        if (vw < dw || vh < dh || hw < dw || hh < dh) {
            throw new IllegalArgumentException("vw < dw || vh < dh || hw < dw || hh < dh");
        }
        Bitmap dst = null;
        int destWidth = 0;
        int destHeight = 0;
        if (src.getWidth() < src.getHeight()) {
            // destHeight = vh;
            // int hoseiW = (vh * src.getWidth()) / src.getHeight();
            // if (hoseiW >= vw) {
            // vw = hoseiW;
            // }
            // destWidth = vw;
            destWidth = vw;
            int hoseiH = (vw * src.getHeight()) / src.getWidth();
            if (hoseiH >= vh) {
                vh = hoseiH;
            }
            destHeight = vh;
        } else if (src.getWidth() > src.getHeight()) {
            // destWidth = hw;
            // int hoseiH = (hw * src.getHeight()) / src.getWidth();
            // if (hoseiH >= hh) {
            // hh = hoseiH;
            // }
            // destHeight = hh;
            destHeight = hh;
            int hoseiW = (hh * src.getWidth()) / src.getHeight();
            if (hoseiW >= hw) {
                hw = hoseiW;
            }
            destWidth = hw;
        } else {
            int mw = vw > hw ? vw : hw;
            int mh = vh > hh ? vh : hh;
            destWidth = mw;
            destHeight = mh;
        }
        Matrix matrix = new Matrix();
        float widthScale = ((float) destWidth) / ((float) src.getWidth());
        float heightScale = ((float) destHeight) / ((float) src.getHeight());
        Log.d("src", "w" + src.getWidth() + "h" + src.getHeight());
        Log.d("dst", "w" + destWidth + "h" + destHeight);
        Log.d("scale", "w" + widthScale + "h" + heightScale);
        matrix.postScale(widthScale, heightScale);
        Bitmap hosei = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        if ((dw == hosei.getWidth() && dh == hosei.getHeight()) || dw == 0 || dh == 0) {
            Log.d("res", "w" + hosei.getWidth() + "h" + hosei.getHeight());
            return hosei;
        }
        Log.d("d", "w" + dw + "h" + dh);
        // if (dst.getWidth() != dst.getHeight()) {
        int cutW = (hosei.getWidth() - dw) / 2;
        int cutH = (hosei.getHeight() - dh) / 2;
        Log.d("cut", "w" + cutW + "h" + cutH);
        dst = Bitmap.createBitmap(hosei, cutW, cutH, hosei.getWidth() - (cutW * 2), hosei.getHeight() - (cutH * 2));
        hosei.recycle();
        Log.d("cutres", "w" + dst.getWidth() + "h" + dst.getHeight());
        // }
        if (dst.getWidth() != dw || dst.getHeight() != dh) {
            Bitmap ddst = Bitmap.createBitmap(dst, 0, 0, dw, dh);
            dst.recycle();
            Log.d("dst", "w" + ddst.getWidth() + "h" + ddst.getHeight());
            return ddst;
        }
        Log.d("res", "w" + dst.getWidth() + "h" + dst.getHeight());
        return dst;
    }

    public Bitmap resizeBitmap2(Bitmap src, int vw, int vh, int hw, int hh, int dw, int dh) {
        // 収縮
        if (vw < dw || vh < dh || hw < dw || hh < dh) {
            throw new IllegalArgumentException("vw < dw || vh < dh || hw < dw || hh < dh");
        }
        Bitmap dst = null;
        int destWidth = 0;
        int destHeight = 0;
        if (src.getWidth() < src.getHeight()) {
            destHeight = vh;
            int hoseiW = (vh * src.getWidth()) / src.getHeight();
            if (hoseiW >= vw) {
                vw = hoseiW;
            }
            destWidth = vw;
        } else if (src.getWidth() > src.getHeight()) {
            destWidth = hw;
            int hoseiH = (hw * src.getHeight()) / src.getWidth();
            if (hoseiH >= hh) {
                hh = hoseiH;
            }
            destHeight = hh;
        } else {
            int mw = vw > hw ? vw : hw;
            int mh = vh > hh ? vh : hh;
            destWidth = mw;
            destHeight = mh;
        }
        Matrix matrix = new Matrix();
        float widthScale = ((float) destWidth) / ((float) src.getWidth());
        float heightScale = ((float) destHeight) / ((float) src.getHeight());
        Log.d("scale", "w" + widthScale + "h" + heightScale);
        matrix.postScale(widthScale, heightScale);
        Bitmap hosei = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        Log.d("src", "w" + src.getWidth() + "h" + src.getHeight());
        if ((dw == hosei.getWidth() && dh == hosei.getHeight()) || dw == 0 || dh == 0) {
            Log.d("dst", "w" + hosei.getWidth() + "h" + hosei.getHeight());
            return hosei;
        }
        if (hosei.getWidth() != hosei.getHeight()) {
            int cutW = (hosei.getWidth() - dw) / 2;
            int cutH = (hosei.getHeight() - dh) / 2;
            Log.d("cut", "w" + cutW + "h" + cutH);
            dst = Bitmap.createBitmap(hosei, cutW, cutH, hosei.getWidth() - (cutW * 2), hosei.getHeight() - (cutH * 2));
            hosei.recycle();
            Log.d("cutdst", "w" + dst.getWidth() + "h" + dst.getHeight());
        }
        if (dst.getWidth() != dw || dst.getHeight() != dh) {
            Bitmap ddst = Bitmap.createBitmap(dst, 0, 0, dw, dh);
            dst.recycle();
            Log.d("dst", "w" + ddst.getWidth() + "h" + ddst.getHeight());
            return ddst;
        }
        Log.d("dst", "w" + dst.getWidth() + "h" + dst.getHeight());
        return dst;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0) {
            if (!RuntimePermissionUtils.checkGrantResults(grantResults)) {
//                Toast.makeText(this, "権限ないです", Toast.LENGTH_SHORT).show();
            } else {
//                Toast.makeText(this, "権限ありです", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static class RuntimePermissionUtils {
        private RuntimePermissionUtils() {
        }

        public static boolean hasSelfPermissions(@NonNull Context context, @NonNull String... permissions) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                return true;
            }
            for (String permission : permissions) {
                if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
            return true;
        }

        public static boolean checkGrantResults(@NonNull int... grantResults) {
            if (grantResults.length == 0)
                throw new IllegalArgumentException("grantResults is empty");
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
            return true;
        }
    }

    private void showExifInfo(String filename) {
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(filename);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        String TAG = "EXIF";
        if (exifInterface != null) {
            // get latitude and longitude
            float[] latlong = new float[2];
            exifInterface.getLatLong(latlong);

            //String aperture = exifInterface.getAttribute (ExifInterface.TAG_APERTURE); // since API Level 11
            String datetime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
            //String exposure = exifInterface.getAttribute (ExifInterface.TAG_EXPOSURE_TIME); // since API Level 11
            int flash = exifInterface.getAttributeInt(ExifInterface.TAG_FLASH, 0);
            double focalLength = exifInterface.getAttributeDouble(ExifInterface.TAG_FOCAL_LENGTH, 0);
            double altitude = exifInterface.getAttributeDouble(ExifInterface.TAG_GPS_ALTITUDE, 0); // since API Level 9
            double altitudeRef = exifInterface.getAttributeDouble(ExifInterface.TAG_GPS_ALTITUDE_REF, 0); // since API Level 9
            String datestamp = exifInterface.getAttribute(ExifInterface.TAG_GPS_DATESTAMP);
            String latitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            String latitudeRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
            String longitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            String longitudeRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
            String processing = exifInterface.getAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD);
            String timestamp = exifInterface.getAttribute(ExifInterface.TAG_GPS_TIMESTAMP);
            int imageLength = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0);
            int imageWidth = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0);
            //String iso = exifInterface.getAttribute (ExifInterface.TAG_ISO); // since API Level 11
            String make = exifInterface.getAttribute(ExifInterface.TAG_MAKE);
            String model = exifInterface.getAttribute(ExifInterface.TAG_MODEL);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            int whitebalance = exifInterface.getAttributeInt(ExifInterface.TAG_WHITE_BALANCE, ExifInterface.WHITEBALANCE_AUTO);

            String orientationInfo = "";
            switch (orientation) {
                case ExifInterface.ORIENTATION_UNDEFINED:
                    orientationInfo = "UNDEFINED";
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                    orientationInfo = "NORMAL";
                    break;
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                    orientationInfo = "FLIP_HORIZONTAL";
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    orientationInfo = "ROTATE_180";
                    break;
                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                    orientationInfo = "FLIP_VERTICAL";
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    orientationInfo = "ROTATE_90";
                    break;
                case ExifInterface.ORIENTATION_TRANSVERSE:
                    orientationInfo = "TRANSVERSE";
                    break;
                case ExifInterface.ORIENTATION_TRANSPOSE:
                    orientationInfo = "TRANSPOSE";
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    orientationInfo = "ROTATE_270";
                    break;
            }
            Log.d(TAG, "latlong : " + latlong[0] + ", " + latlong[1]);
            Log.d(TAG, "datetime : " + datetime);
            Log.d(TAG, "flash : " + flash + "  (" + (flash == 1 ? "on" : "off") + ")");
            Log.d(TAG, "focalLength : " + focalLength + "");
            Log.d(TAG, "datestamp : " + datestamp);
            Log.d(TAG, "altitude : " + altitude);
            Log.d(TAG, "altitudeRef : " + altitudeRef);
            Log.d(TAG, "latitude : " + latitude);
            Log.d(TAG, "latitudeRef : " + latitudeRef);
            Log.d(TAG, "longitude : " + longitude);
            Log.d(TAG, "longitudeRef : " + longitudeRef);
            Log.d(TAG, "processing : " + processing);
            Log.d(TAG, "timestamp : " + timestamp);
            Log.d(TAG, "imageLength : " + imageLength + "");
            Log.d(TAG, "imageWidth : " + imageWidth + "");
            Log.d(TAG, "make : " + make);
            Log.d(TAG, "model : " + model);
            Log.d(TAG, "orientation : " + orientation + "  (" + orientationInfo + ")");
            Log.d(TAG, "whitebalance : " + whitebalance + "  " + (whitebalance == 1 ? "manual" : "auto"));
        } else {
            Log.d(TAG, "NONE");
        }
    }
}
