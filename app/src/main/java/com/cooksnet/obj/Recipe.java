package com.cooksnet.obj;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;

public class Recipe {

	public static final String CREATE_SUCCESS = "create=succeeded";
	public static final String CREATE_FAILED = "create=failed";
	public static final String EDIT_SUCCESS = "recipeEdit=succeeded";
	public static final String EDIT_FAILED = "recipeEdit=failed";
	public static final String UPLOAD_IMAGE_SUCCESS = "recipeUploadImage=succeeded";
	public static final String UPLOAD_IMAGE_FAILED = "recipeUploadImage=failed";
	public static final String EDIT_INGREDIENT_SUCCESS = "recipeEdit=succeeded";
	public static final String EDIT_INGREDIENT_FAILED = "recipeEdit=failed";
	public static final String EDIT_STEP_SUCCESS = "recipeEdit=succeeded";
	public static final String EDIT_STEP_FAILED = "recipeEdit=failed";
	public static final String PUBLISH_SUCCESS = "recipePublishResult=succeeded";
	public static final String PUBLISH_FAILED = "recipePublishResult=failed";
	public static final String DELETE_SUCCESS = "recipeDeleteResult=succeeded";
	public static final String DELETE_FAILED = "recipeDeleteResult=failed";
	public static final String DRAFT_SUCCESS = "recipeDraftResult=succeeded";
	public static final String DRAFT_FAILED = "recipeDraftResult=failed";

	public String createStatus = "";
	public String createErrMsg = "";
	public String editStatus = "";
	public String uploadImageStatus = "";
	public String editIngredientStatus = "";
	public String editStepStatus = "";
	public String publishStatus = "";
	public String deleteStatus = "";
	public String draftStatus = "";

	public String xml = "";
	public String id = "";
	public String lang = "";
	public String title = "";
	public String description = "";
	public String totalQuantity = "";
	public String advice = "";
	public String profileName = "";
	public String photoId = "";
	public String photoUrl = "";
	public Bitmap photo;
	public File photoFile;
	public boolean isModifiedPhoto;
	public String userIconUrl = "";
	public Bitmap userIcon;
	public boolean isFavorite = false;

	public List<Ingredient> ingredients = new ArrayList<Ingredient>();
	public List<Step> steps = new ArrayList<Step>();

	public Recipe() {
	}

}