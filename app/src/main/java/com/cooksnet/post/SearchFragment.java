package com.cooksnet.post;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import com.cooksnet.obj.Recipe;
import com.cooksnet.obj.Result;
import com.cooksnet.obj.ResultItem;
import com.cooksnet.util.CooksNetParser;
import com.cooksnet.util.CooksNetWebAccess;

import java.io.IOException;

public class SearchFragment extends BaseFragment {

    private ProgressDialogFragment dialog;

    protected Activity me;

    ResultArrayAdapter arrayAdapter;

    int page = 0;
    Result result;

    boolean canSearch = false;
    boolean existMore = false;

    class DeleteThread extends AsyncTask<Boolean, Void, String> {
        private boolean successNetwork = true;
        private Handler handler;
        private Activity me;
        private Recipe recipe;
        private boolean isPublish;

        public DeleteThread(Handler handler, Activity me, Recipe recipe) {
            super();
            this.handler = handler;
            this.me = me;
            this.recipe = recipe;
        }

        protected void onPreExecute() {
            handler.postDelayed(new Runnable() {
                public void run() {
//					dialog = ProgressDialog.show(a, "", getText(R.string.dialog_progress).toString(),
//							true);
                    dialog = ProgressDialogFragment.newInstance("", getString(R.string.dialog_progress));
                    dialog.show(a.getSupportFragmentManager(), "CreateThread");
                }
            }, 0);
        }

        protected String doInBackground(Boolean... param) {
            try {
                isPublish = param[0];
                recipe.deleteStatus = new CooksNetWebAccess().delete(recipe.id);
                if (null != this.recipe && Recipe.DELETE_SUCCESS.equals(this.recipe.deleteStatus)) {
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

        protected void onPostExecute(final String res) {
            handler.post(new Runnable() {
                public void run() {
                    if (successNetwork) {
                        if ("success".equals(res)) {
                            new SearchThread(handler, me, true).execute(isPublish);
                        } else {
//                            new AlertDialog.Builder(me).setTitle(getText(R.string.dialog_system_error_title))
//                                    .setPositiveButton(R.string.dialog_ok, null).create().show();
                            AlertDialogFragment.newInstance(getString(R.string.dialog_system_error_title), "", "")
                                    .show(a.getSupportFragmentManager(), "NoneTitle");
                        }
                    }

                    if (null != dialog) {
                        dialog.dismiss();
                        dialog = null;
                    }

                    if (!successNetwork) {
                        if (null == res || "".equals(res)) {
                            Toast.makeText(
                                    me,
                                    getText(R.string.dialog_network_error_title) + "\n"
                                            + getText(R.string.dialog_network_error_message), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(
                                    me,
                                    getText(R.string.dialog_http_error_title) + "\n"
                                            + getText(R.string.dialog_input_error_message_unexpected_err) + "\n"
                                            + "Error Code " + res, Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
        }
    }

    protected void doAfterSearch() {
    }

    class SearchThread extends AsyncTask<Boolean, Void, String> {
        private boolean successNetwork = true;
        private Handler handler;
        private Activity me;
        private boolean initial;

        public SearchThread(Handler handler, Activity me, boolean initial) {
            super();
            this.initial = initial;
            this.handler = handler;
            this.me = me;
        }

        protected void onPreExecute() {
            handler.postDelayed(new Runnable() {
                public void run() {
                    if (initial) {
                        for (int i = 0; i < arrayAdapter.getCount(); i++) {
                            arrayAdapter.remove(arrayAdapter.getItem(i));
                        }
                        arrayAdapter.clear();
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            }, 0);
        }

        protected String doInBackground(Boolean... param) {
            try {
                page++;

                handler.postDelayed(new Runnable() {
                    public void run() {
                        if (!canSearch) {
//							dialog = ProgressDialog.show(a, "", getText(R.string.dialog_progress).toString(),
//									true);
                            dialog = ProgressDialogFragment.newInstance("", getString(R.string.dialog_progress));
                            dialog.show(a.getSupportFragmentManager(), "CreateThread");
                        }
                    }
                }, 200);

                String xml = new CooksNetWebAccess().searchMyRecipe(page, ExtrasData.profile.id, param[0]);
                result = new CooksNetParser().parseMyRecipeResult(xml);
                return "success";
            } catch (IOException ioe) {
                try {

                    String xml = new CooksNetWebAccess().searchMyRecipe(page, ExtrasData.profile.id, param[0]);

                    result = new CooksNetParser().parseMyRecipeResult(xml);
                    return
                            "success";

                } catch (IOException ioee) {
                    successNetwork = false;
                    if (ioe.getMessage().startsWith(CooksNetWebAccess.SERVER_ERROR)) {
                        return ioe.getMessage().replaceAll(CooksNetWebAccess.SERVER_ERROR, "");
                    }
                    return "";
                }
            }
        }

        protected void onPostExecute(final String res) {
            handler.post(new Runnable() {
                public void run() {
                    if (successNetwork) {
                        arrayAdapter.remove(ResultItem.MORE);
                        for (ResultItem item : result.items) {
                            arrayAdapter.add(item);
                        }
                        if (result.page != 0 && result.page < result.pages) {
                            existMore = true;
                            arrayAdapter.add(ResultItem.MORE);
                        } else {
                            existMore = false;
                        }
                        arrayAdapter.notifyDataSetChanged();
                        doAfterSearch();
                    }

                    if (null != dialog) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    canSearch = true;

                    if (!successNetwork) {
                        if (null == res || "".equals(res)) {
                            Toast.makeText(
                                    me,
                                    getText(R.string.dialog_network_error_title) + "\n"
                                            + getText(R.string.dialog_network_error_message), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(
                                    me,
                                    getText(R.string.dialog_http_error_title) + "\n"
                                            + getText(R.string.dialog_input_error_message_unexpected_err) + "\n"
                                            + "Error Code " + res, Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
        }
    }

}