package com.cooksnet.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.cooksnet.obj.Ingredient;
import com.cooksnet.obj.Profile;
import com.cooksnet.obj.Recipe;
import com.cooksnet.obj.Step;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by takana on 2016/04/26
 */
public class CooksNetWebAccess {

    public static final String SERVER_ERROR = "cooksnet server error. ";

    //private static final String DOMAIN = "http://54.215.20.241";
    //private static final String DOMAIN = "http://54.241.5.200";
    private static final String DOMAIN = "http://www.cooksnet.com";
    public static final String URL_SIGN_UP = DOMAIN + "/users/add";
    public static final String URL_GUIDELINE = DOMAIN + "/pages/mobile/guideline?noframe";
    public static final String URL_RULE = DOMAIN + "/pages/rule?noframe";
    public static final String URL_POST_FAQ = DOMAIN + "/pages/mobile/post-faq?noframe";

    public static final String URL_SEARCH = DOMAIN + "/recipes/index/page:{0}.xml?stdword={1}";
    public static final String URL_SEARCH_ROOT_CATEGORY = DOMAIN + "/categories/rootview.xml";
    public static final String URL_SEARCH_CATEGORY_RECIPE = DOMAIN + "/categories/view/{0}/page:{1}.xml";

    private static final String URL_LOGIN = DOMAIN + "/musers/login";
    private static final String URL_RECIPE = DOMAIN + "/recipes/{0}.xml";
    private static final String URL_MY_RECIPE = DOMAIN + "/recipes/list/{0}/{1}page:{2}.xml";
    private static final String URL_DELETE = DOMAIN + "/mrecipes/delete/{0}";
    private static final String URL_PUBLISH = DOMAIN + "/mrecipes/publish/{0}";
    private static final String URL_DRAFT = DOMAIN + "/mrecipes/draft/{0}";
    private static final String URL_CREATE_RECIPE = DOMAIN + "/mrecipes/create";
    private static final String URL_EDIT_RECIPE = DOMAIN + "/mrecipes/edit/{0}";
    private static final String URL_UPLOAD_IMAGE_RECIPE = DOMAIN + "/mrecipes/upload_image/{0}";
    private static final String URL_EDIT_INGREDIENT = DOMAIN + "/mingredients/updateAll/{0}";
    private static final String URL_EDIT_STEP = DOMAIN + "/msteps/updateAll/{0}";


    private static OkHttpClient okHttpClient;


    public CooksNetWebAccess() {
        if (okHttpClient == null) {
            generateOkHttpClient();
        }
    }


    private void generateOkHttpClient() {
        CookieJar cookieJar = new CookieJar() {
            private List<Cookie> cookies;

            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                this.cookies = cookies;
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                if (this.cookies != null) {
                    Map<String, Cookie> map = new HashMap<>();

                    // 重複消去
                    for (Cookie cookie : this.cookies) {
                        map.put(cookie.name(), cookie);
                    }

                    return new ArrayList<>(map.values());
                }
                return new ArrayList<>();
            }
        };

        okHttpClient = new OkHttpClient.Builder()
                .readTimeout(120 * 1000, TimeUnit.MILLISECONDS)
                .writeTimeout(120 * 1000, TimeUnit.MILLISECONDS)
                .connectTimeout(120 * 1000, TimeUnit.MILLISECONDS)
                .cookieJar(cookieJar)
                .build();
    }


    public String search(int page, String query) throws IOException {
        try {
            String url = URL_SEARCH.replaceAll("\\{0\\}", "" + page);
            url = url.replaceAll("\\{1\\}", "" + URLEncoder.encode(query, "UTF-8"));
            Response response = get(url);
            return response.body().string();
        } catch (IOException ioe) {
            throw ioe;
        }
    }

    public String searchRootCategory() throws IOException {
        try {
            Response response = get(URL_SEARCH_ROOT_CATEGORY);
            return response.body().string();
        } catch (IOException ioe) {
            throw ioe;
        }
    }

    public String searchCategoryResult(int page, String id) throws IOException {
        try {
            String url = URL_SEARCH_CATEGORY_RECIPE.replaceAll("\\{0\\}", "" + id);
            url = url.replaceAll("\\{1\\}", "" + page);
            Response response = get(url);
            return response.body().string();
        } catch (IOException ioe) {
            throw ioe;
        }
    }

    public String getRecipe(String id) throws IOException {
        try {
            return getMyRecipe(id);
        } catch (IOException ioe) {
            throw ioe;
        }
    }

    public Profile login(String nickname, String password) throws IOException {
        RequestBody body = new FormBody.Builder()
                .add("data[User][nickname]", nickname)
                .add("data[User][password]", password)
                .build();

        Response response = post(URL_LOGIN, body);

        if (!response.isSuccessful()) {
            throw new IOException(SERVER_ERROR + response.code());
        }

        String[] resLines = response.body().string().split("\n");
        List<String> lines = new ArrayList<>(Arrays.asList(resLines));

        Profile profile = new Profile();
        profile.loginState = lines.get(0);

        if (Profile.LOGIN_SUCCESS.equals(profile.loginState)) {
            profile.nickname = nickname;
            profile.password = password;
            profile.id = lines.get(1);
            profile.name = lines.get(2);
            profile.lang = lines.get(3);

            if (profile.lang.startsWith("ja")) {
                profile.locale = Locale.JAPANESE;
            } else if (profile.lang.startsWith("zh")) {
                profile.locale = Locale.CHINESE;
            } else {
                profile.locale = Locale.ENGLISH;
            }
        }

        return profile;
    }


    public String searchMyRecipe(int page, String profileId, boolean isPublish) throws IOException {
        try {
            String url = URL_MY_RECIPE.replaceAll("\\{0\\}", profileId);

            if (isPublish) {
                url = url.replaceAll("\\{1\\}", "");
            } else {
                url = url.replaceAll("\\{1\\}", "draft/");
            }

            url = url.replaceAll("\\{2\\}", "" + page);
            Response response = get(url);

            return response.body().string();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            throw ioe;
        }
    }


    public Bitmap getBitmapFromURL(String src) throws IOException {
        if ("".equals(src)) {
            return null;
        }

        Response response = get(src);
        InputStream inputStream = response.body().byteStream();

        return BitmapFactory.decodeStream(inputStream);
    }


    public Recipe createRecipe(Recipe recipe) throws IOException {
        String filename = recipe.photoFile.getName();
        MediaType mediaType = getMediaType(filename);

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("data[Recipe][title]", recipe.title)
                .addFormDataPart("data[Recipe][lang]", recipe.lang)
                .addFormDataPart("data[Attachment][0][model]", "Recipe")
                .addFormDataPart("data[Attachment][0][group]", "attachment")
                .addFormDataPart("data[Attachment][0][file]", filename,
                        RequestBody.create(mediaType, recipe.photoFile))
                .build();

        String res = post(URL_CREATE_RECIPE, body).body().
                string();
//        Map<String, String> params = new HashMap<>();
//        params.put("data[Recipe][title]", recipe.title);
//        params.put("data[Recipe][lang]", recipe.lang);
//        params.put("data[Attachment][0][model]", "Recipe");
//        params.put("data[Attachment][0][group]", "attachment");
//        Map<String, File> files = new HashMap<>();
//        files.put("data[Attachment][0][file]", recipe.photoFile);

//        String res = postMultipart(URL_CREATE_RECIPE, params, null, files, null, true);

        List<String> lines = new ArrayList<>(Arrays.asList(res.split("\n")));

        if (!Recipe.CREATE_SUCCESS.equals(lines.get(0))) {
            recipe.createErrMsg = lines.get(1);
        } else {
            recipe.id = lines.get(1);
        }

        recipe.createStatus = lines.get(0);

        return recipe;
    }


    public Recipe editRecipe(Recipe recipe) throws IOException {
        RequestBody body = new FormBody.Builder()
                .add("data[Recipe][description]", recipe.description)
                .add("data[Recipe][total_quantity]", recipe.totalQuantity)
                .add("data[Recipe][advice]", recipe.advice)
                .build();

        String url = replaceUrl(URL_EDIT_RECIPE, recipe.id);

        String res = post(url, body)
                .body()
                .string();

        recipe.editStatus = getFirstLine(res);

        return recipe;
    }


    public Recipe uploadPhoto(Recipe recipe, Recipe oldRecipe) throws IOException {
        String filename = recipe.photoFile.getName();
        MediaType mediaType = getMediaType(filename);

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("data[Attachment][0][model]", "Recipe")
                .addFormDataPart("data[Attachment][0][group]", "attachment")
                .addFormDataPart("data[Attachment][1][id]", oldRecipe.photoId)
                .addFormDataPart("data[Attachment][1][delete]", "1")
                .addFormDataPart("data[Attachment][0][file]", filename,
                        RequestBody.create(mediaType, recipe.photoFile))
                .build();

        String url = replaceUrl(URL_UPLOAD_IMAGE_RECIPE, recipe.id);
        Response response = post(url, body);
        recipe.uploadImageStatus = getFirstLine(response.body().string());

        return recipe;
    }


    public Recipe editIngredient(Recipe recipe, Recipe oldRecipe) throws IOException {
        FormBody.Builder builder = new FormBody.Builder();

        for (int i = 0; i < recipe.ingredients.size(); i++) {
            Ingredient ingredient = recipe.ingredients.get(i);

            if ((i + 1) <= oldRecipe.ingredients.size()) {
                Ingredient oldIngredient = oldRecipe.ingredients.get(i);
                builder.add("data[" + i + "][Ingredient][id]=", oldIngredient.id);
            }

            builder.add("data[" + i + "][Ingredient][name]=", ingredient.name)
                    .add("data[" + i + "][Ingredient][quantity]=", ingredient.quantity)
                    .add("data[" + i + "][Ingredient][recipe_id]=", recipe.id)
                    .add("data[" + i + "][Ingredient][order]=", String.valueOf(i + 1));
        }

        String url = replaceUrl(URL_EDIT_INGREDIENT, recipe.id);
        Response response = post(url, builder.build());
        recipe.editIngredientStatus = getFirstLine(response.body().string());

        return recipe;
    }


    public Recipe editStep(Recipe recipe, Recipe oldRecipe) throws IOException {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        for (int i = 0; i < recipe.steps.size(); i++) {
            Step step = recipe.steps.get(i);

            if ((i + 1) <= oldRecipe.steps.size()) {
                Step oldStep = oldRecipe.steps.get(i);
                builder.addFormDataPart("data[" + i + "][Step][id]", oldStep.id);
            }

            builder.addFormDataPart("data[" + i + "][Step][text]", step.text)
                    .addFormDataPart("data[" + i + "][Step][recipe_id]", recipe.id)
                    .addFormDataPart("data[" + i + "][Step][order]", String.valueOf((i + 1)));

            if ((i + 1) <= oldRecipe.steps.size()) {
                Step oldI = oldRecipe.steps.get(i);
                if ("".equals(oldI.photoUrl) && null != step.photoFile) {
                    builder.addFormDataPart("data[" + i + "][Attachment][0][model]", "Step")
                            .addFormDataPart("data[" + i + "][Attachment][0][group]", "attachment")
                            .addFormDataPart("data[" + i + "][Attachment][0][file]",
                                    step.photoFile.getName(),
                                    RequestBody.create(getMediaType(step.photoFile.getName()), step.photoFile));
                } else if (!"".equals(oldI.photoUrl) && null != step.photoFile && step.isModifiedPhoto) {
                    builder.addFormDataPart("data[" + i + "][Attachment][0][model]", "Step")
                            .addFormDataPart("data[" + i + "][Attachment][0][group]", "attachment")
                            .addFormDataPart("data[" + i + "][Attachment][0][file]",
                                    step.photoFile.getName(),
                                    FormBody.create(getMediaType(step.photoFile.getName()), step.photoFile))
                            .addFormDataPart("data[" + i + "][Attachment][1][id]", oldI.photoId)
                            .addFormDataPart("data[" + i + "][Attachment][1][delete]", "1");
                } else if (!"".equals(oldI.photoUrl) && null == step.photoFile) {
                    builder.addFormDataPart("data[" + i + "][Attachment][1][id]", oldI.photoId)
                            .addFormDataPart("data[" + i + "][Attachment][1][delete]", "1");
                }
            } else {
                if (null != step.photoFile) {
                    builder.addFormDataPart("data[" + i + "][Attachment][0][model]", "Step")
                            .addFormDataPart("data[" + i + "][Attachment][0][group]", "attachment")
                            .addFormDataPart("data[" + i + "][Attachment][0][file]",
                                    step.photoFile.getName(),
                                    FormBody.create(getMediaType(step.photoFile.getName()), step.photoFile));
                }
            }
        }

        String url = replaceUrl(URL_EDIT_STEP, recipe.id);
        Response response = post(url, builder.build());
        recipe.editStepStatus = getFirstLine(response.body().string());

        return recipe;
    }


    public String publish(String recipeId) throws IOException {
        Response response = get(replaceUrl(URL_PUBLISH, recipeId));
        return response.body().string();
    }


    public String draft(String recipeId) throws IOException {
        Response response = get(replaceUrl(URL_DRAFT, recipeId));
        return response.body().string();
    }


    public String delete(String recipeId) throws IOException {
        Response response = get(replaceUrl(URL_DELETE, recipeId));
        return response.body().string();
    }


    public String getMyRecipe(String id) throws IOException {
        Response response = get(replaceUrl(URL_RECIPE, id));
        return response.body().string();
    }


    private static String replaceUrl(String url, String recipeId) {
        return url.replaceAll("\\{0\\}", recipeId);
    }


    private static String getFirstLine(String res) {
        List<String> lines = new ArrayList<>(Arrays.asList(res.split("\n")));
        return lines.get(0);
    }


    private static MediaType getMediaType(String filename) {
        return filename.endsWith("png")
                ? MediaType.parse("image/png")
                : MediaType.parse("image/jpeg");
    }


    private Response get(String url) throws IOException {
        Request request = new Request.Builder()
                .addHeader("Accept-Language", Locale.getDefault().toString())
                .url(url)
                .get()
                .build();

        return okHttpClient
                .newCall(request)
                .execute();
    }


    private Response post(String url, RequestBody body) throws IOException {
        Request request = new Request.Builder()
                .addHeader("Accept-Language", Locale.getDefault().toString())
                .url(url)
                .post(body)
                .build();

        return okHttpClient
                .newCall(request)
                .execute();
    }
}
