package com.cooksnet.util;

import java.io.IOException;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.cooksnet.obj.Category;
import com.cooksnet.obj.Ingredient;
import com.cooksnet.obj.Recipe;
import com.cooksnet.obj.Result;
import com.cooksnet.obj.ResultItem;
import com.cooksnet.obj.Step;

public class CooksNetParser {

	public Result parseResult(String xml) {
		Result result = new Result();

		if (null == xml) {
			return result;
		}

		XmlPullParserFactory factory;
		XmlPullParser xpp;
		try {
			factory = XmlPullParserFactory.newInstance();

			factory.setNamespaceAware(true);
			xpp = factory.newPullParser();
			xpp.setInput(new StringReader(xml));

			ResultItem item = null;
			String tag = "";
			for (int e = xpp.getEventType(); e != XmlPullParser.END_DOCUMENT; e = xpp.next()) {
				switch (e) {
				case XmlPullParser.START_TAG:
					tag = xpp.getName();
					if ("qs_record".equals(tag)) {
						item = new ResultItem();
					}
					break;
				case XmlPullParser.TEXT:
					String text = xpp.getText();
					if ("description".equals(tag)) {
						text = text.replaceAll("\t", "").replaceFirst("\n", "");
					} else {
						text = text.replaceAll("\t", "").replaceAll("\n", "");
					}
					if ("result".equals(tag)) {
						result.result = Integer.parseInt(text);
					} else if ("from".equals(tag)) {
						result.from = Integer.parseInt(text);
					} else if ("size".equals(tag)) {
						result.size = Integer.parseInt(text);
					} else if ("id".equals(tag)) {
						item.id = text;
					} else if ("title".equals(tag)) {
						item.title = text;
					} else if ("description".equals(tag)) {
						item.description = text;
					} else if ("profile_name".equals(tag)) {
						item.profileName = text;
					} else if ("photo_url".equals(tag)) {
						item.photoUrl = text;
						// item.photo = new
						// CooksNetWebAccess().getBitmapFromURL(text);
					}
					tag = "";
					break;
				case XmlPullParser.END_TAG:
					tag = xpp.getName();
					if ("qs_record".equals(tag)) {
						result.items.add(item);
						item = null;
					}
					tag = "";
					break;
				}
			}

			return result;

		} catch (XmlPullParserException xppe) {
			return result;
		} catch (IOException ioe) {
			return result;
		}
	}

	public Category parseRootCategory(String xml) {
		Category root = new Category();

		XmlPullParserFactory factory;
		XmlPullParser xpp;
		try {
			factory = XmlPullParserFactory.newInstance();

			factory.setNamespaceAware(true);
			xpp = factory.newPullParser();
			xpp.setInput(new StringReader(xml));

			Category item = null;
			boolean end = false;
			String tag = "";
			for (int e = xpp.getEventType(); e != XmlPullParser.END_DOCUMENT; e = xpp.next()) {
				switch (e) {
				case XmlPullParser.START_TAG:
					tag = xpp.getName();
					if ("category".equals(tag) && item == null) {
						item = root;
					} else if ("children".equals(tag)) {
						root = item;
					} else if ("category".equals(tag) && item != null) {
						item = new Category();
					}
					break;
				case XmlPullParser.TEXT:
					String text = xpp.getText();
					text = text.replaceAll("\t", "").replaceAll("\n", "");
					if ("id".equals(tag)) {
						item.id = text;
					} else if ("name".equals(tag)) {
						item.name = text;
					}
					if (root == item) {
						if ("child_count".equals(tag)) {
							item.childCount = Integer.parseInt(text);
						} else if ("recipe_count".equals(tag)) {
							item.recipeCount = Integer.parseInt(text);
						} else if ("page".equals(tag)) {
							item.page = Integer.parseInt(text);
						} else if ("pages".equals(tag)) {
							item.pages = Integer.parseInt(text);
						}
					} else {
						if ("hidden".equals(tag)) {
							item.hidden = new Boolean(text).booleanValue();
						}
					}
					tag = "";
					break;
				case XmlPullParser.END_TAG:
					tag = xpp.getName();
					if ("category".equals(tag) && !end) {
						root.children.add(item);
					}
					if ("children".equals(tag)) {
						end = true;
					}
					tag = "";
					break;
				}
			}

			return root;

		} catch (XmlPullParserException xppe) {
			return root;
		} catch (IOException ioe) {
			return root;
		}
	}

	public Category parseCategoryRecipe(String xml) {
		Category category = new Category();
		XmlPullParserFactory factory;
		XmlPullParser xpp;
		try {
			factory = XmlPullParserFactory.newInstance();

			factory.setNamespaceAware(true);
			xpp = factory.newPullParser();
			xpp.setInput(new StringReader(xml));

			Category item = null;
			ResultItem resultItem = null;
			boolean end = false;
			String tag = "";
			for (int e = xpp.getEventType(); e != XmlPullParser.END_DOCUMENT; e = xpp.next()) {
				switch (e) {
				case XmlPullParser.START_TAG:
					tag = xpp.getName();
					if ("basic_info".equals(tag) && item == null) {
						item = category;
					} else if ("children".equals(tag)) {
						category = item;
					} else if ("category".equals(tag) && item != null) {
						item = new Category();
					} else if ("recipes".equals(tag)) {
						end = true;
					} else if ("recipe".equals(tag) && item != null) {
						resultItem = new ResultItem();
						end = true;
					}
					break;
				case XmlPullParser.TEXT:
					String text = xpp.getText();
					if ("description".equals(tag)) {
						text = text.replaceAll("\t", "").replaceFirst("\n", "");
					} else {
						text = text.replaceAll("\t", "").replaceAll("\n", "");
					}
					if (!end) {
						if ("id".equals(tag)) {
							item.id = text;
						} else if ("name".equals(tag)) {
							item.name = text;
						}
						if (category == item) {
							if ("child_count".equals(tag)) {
								item.childCount = Integer.parseInt(text);
							} else if ("recipe_count".equals(tag)) {
								item.recipeCount = Integer.parseInt(text);
							} else if ("page".equals(tag)) {
								item.page = Integer.parseInt(text);
							} else if ("pages".equals(tag)) {
								item.pages = Integer.parseInt(text);
							}
						} else {
							if ("hidden".equals(tag)) {
								item.hidden = new Boolean(text).booleanValue();
							}
						}
					} else {
						if ("id".equals(tag)) {
							resultItem.id = text;
						} else if ("title".equals(tag)) {
							resultItem.title = text;
						} else if ("description".equals(tag)) {
							resultItem.description = text;
						} else if ("profile_name".equals(tag)) {
							resultItem.profileName = text;
						} else if ("photo_url".equals(tag)) {
							resultItem.photoUrl = text;
							// item.photo = new
							// CooksNetWebAccess().getBitmapFromURL(text);
						}
					}
					tag = "";
					break;
				case XmlPullParser.END_TAG:
					tag = xpp.getName();
					if ("category".equals(tag) && !end) {
						category.children.add(item);
					}
					if ("children".equals(tag)) {
						end = true;
					}
					if ("recipe".equals(tag)) {
						category.recipes.add(resultItem);
					}
					tag = "";
					break;
				}
			}

			return category;

		} catch (XmlPullParserException xppe) {
			return category;
		} catch (IOException ioe) {
			return category;
		}
	}

	public Recipe parseRecipe(String xml) {
		Recipe recipe = new Recipe();
		recipe.xml = xml;

		XmlPullParserFactory factory;
		XmlPullParser xpp;
		try {
			factory = XmlPullParserFactory.newInstance();

			factory.setNamespaceAware(true);
			xpp = factory.newPullParser();
			xpp.setInput(new StringReader(xml));

			Ingredient ingredient = null;
			Step step = null;
			String tag = "";
			int mode = 0;
			for (int e = xpp.getEventType(); e != XmlPullParser.END_DOCUMENT; e = xpp.next()) {
				switch (e) {
				case XmlPullParser.START_TAG:
					tag = xpp.getName();
					if ("ingredient".equals(tag)) {
						ingredient = new Ingredient();
						mode = 1;
					}
					if ("step".equals(tag)) {
						step = new Step();
						mode = 2;
					}
					break;
				case XmlPullParser.TEXT:
					String text = xpp.getText();
					if ("description".equals(tag) || "text".equals(tag) || "advice".equals(tag)) {
						text = text.replaceAll("\t", "").replaceFirst("\n", "");
						if (0 < text.lastIndexOf("\n")) {
							text = text.substring(0, text.lastIndexOf("\n"));
						}

					} else {
						text = text.replaceAll("\t", "").replaceAll("\n", "");
					}
					if ("id".equals(tag)) {
						if (0 == mode) {
							recipe.id = text;
						} else if (1 == mode) {
							ingredient.id = text;
						} else if (2 == mode) {
							step.id = text;
						}
					} else if ("title".equals(tag)) {
						recipe.title = text;
					} else if ("description".equals(tag)) {
						recipe.description = text;
					} else if ("total_quantity".equals(tag)) {
						recipe.totalQuantity = text;
					} else if ("advice".equals(tag)) {
						recipe.advice = text;
					} else if ("profile_name".equals(tag)) {
						recipe.profileName = text;
					} else if ("photo_id".equals(tag) && null == step) {
						recipe.photoId = text;
					} else if ("photo_url".equals(tag) && null == step) {
						recipe.photoUrl = text;
						// recipe.photo = new
						// CooksNetWebAccess().getBitmapFromURL(text);
					} else if ("user_icon".equals(tag)) {
						recipe.userIconUrl = text;
						// recipe.userIcon = new
						// CooksNetWebAccess().getBitmapFromURL(text);
					} else if ("name".equals(tag)) {
						ingredient.name = text;
					} else if ("quantity".equals(tag)) {
						ingredient.quantity = text;
					} else if ("text".equals(tag)) {
						step.text = text;
					} else if ("photo_id".equals(tag) && null != step) {
						step.photoId = text;
					} else if ("photo_url".equals(tag) && null != step) {
						step.photoUrl = text;
						// step.photo = new
						// CooksNetWebAccess().getBitmapFromURL(text);
					}
					tag = "";
					break;
				case XmlPullParser.END_TAG:
					tag = xpp.getName();
					if ("ingredient".equals(tag)) {
						recipe.ingredients.add(ingredient);
						ingredient = null;
					}
					if ("step".equals(tag)) {
						recipe.steps.add(step);
						step = null;
					}
					tag = "";
					break;
				}
			}

			return recipe;

		} catch (XmlPullParserException xppe) {
			return recipe;
		} catch (IOException ioe) {
			return recipe;
		}
	}

	public Result parseMyRecipeResult(String xml) {
		Result result = new Result();

		if (null == xml) {
			return result;
		}

		XmlPullParserFactory factory;
		XmlPullParser xpp;
		try {
			factory = XmlPullParserFactory.newInstance();

			factory.setNamespaceAware(true);
			xpp = factory.newPullParser();
			xpp.setInput(new StringReader(xml));

			ResultItem item = null;
			String tag = "";
			for (int e = xpp.getEventType(); e != XmlPullParser.END_DOCUMENT; e = xpp.next()) {
				switch (e) {
				case XmlPullParser.START_TAG:
					tag = xpp.getName();
					if ("record".equals(tag)) {
						item = new ResultItem();
					}
					break;
				case XmlPullParser.TEXT:
					String text = xpp.getText();
					if ("description".equals(tag)) {
						text = text.replaceAll("\t", "").replaceFirst("\n", "");
					} else {
						text = text.replaceAll("\t", "").replaceAll("\n", "");
					}
					if ("result".equals(tag)) {
						result.result = Integer.parseInt(text);
					} else if ("page".equals(tag)) {
						result.page = Integer.parseInt(text);
					} else if ("pages".equals(tag)) {
						result.pages = Integer.parseInt(text);
					} else if ("id".equals(tag)) {
						item.id = text;
					} else if ("title".equals(tag)) {
						item.title = text;
					} else if ("description".equals(tag)) {
						item.description = text;
					} else if ("photo_url".equals(tag)) {
						item.photoUrl = text;
						// item.photo = new
						// CooksNetWebAccess().getBitmapFromURL(text);
					}
					tag = "";
					break;
				case XmlPullParser.END_TAG:
					tag = xpp.getName();
					if ("record".equals(tag)) {
						result.items.add(item);
						item = null;
					}
					tag = "";
					break;
				}
			}

			return result;

		} catch (XmlPullParserException xppe) {
			return result;
		} catch (IOException ioe) {
			return result;
		}
	}

}
