package com.cooksnet.post;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cooksnet.obj.ImageTextItem;

public class ImageTextArrayAdapter extends ArrayAdapter<ImageTextItem> {

	private LayoutInflater inflater;
	private int imageTextViewResourceId;

	public ImageTextArrayAdapter(Context context, int imageTextViewResourceId) {
		super(context, imageTextViewResourceId);
		this.imageTextViewResourceId = imageTextViewResourceId;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;

		if (convertView == null) {
			convertView = inflater.inflate(imageTextViewResourceId, parent, false);
			holder = new ViewHolder();
			holder.imageView1 = (ImageView) convertView.findViewById(R.id.imageView1);
			holder.text1 = (TextView) convertView.findViewById(android.R.id.text1);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		ImageTextItem item = getItem(position);

		if (item != null) {
			if (null != item.getImageResource()) {
				holder.imageView1.setImageResource(item.getImageResource());
			} else {
				holder.imageView1.setImageBitmap(null);
			}
			holder.text1.setText(item.getText());
		}
		return convertView;
	}

	static class ViewHolder {
		ImageView imageView1;
		TextView text1;
	}

}