package com.cooksnet.post;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cooksnet.obj.ResultItem;
import com.cooksnet.util.ImageLoadCache;
import com.cooksnet.util.ImageLoadThread;

public class ResultArrayAdapter extends ArrayAdapter<ResultItem> {

	private LayoutInflater inflater;
	private Handler handler;

	public ResultArrayAdapter(Context context, int textViewResourceId, Handler handler) {
		super(context, textViewResourceId);
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.handler = handler;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;

		ResultItem item = getItem(position);

		if (null == convertView) {
			convertView = inflater.inflate(R.layout.result_list_item, parent, false);
			holder = new ViewHolder();
			holder.photo = (ImageView) convertView.findViewById(R.id.photo);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.profileName = (TextView) convertView.findViewById(R.id.profileName);
			holder.description = (TextView) convertView.findViewById(R.id.description);
			holder.more = (TextView) convertView.findViewById(R.id.more);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (item != null) {
			if (ResultItem.MORE == item) {
				holder.photo.setVisibility(View.GONE);
				holder.title.setVisibility(View.GONE);
				holder.profileName.setVisibility(View.GONE);
				holder.description.setVisibility(View.GONE);
				holder.more.setVisibility(View.VISIBLE);
			} else if ("".equals(item.id)) {
				remove(item);
			} else {
				holder.photo.setVisibility(View.VISIBLE);
				holder.title.setVisibility(View.VISIBLE);
				holder.profileName.setVisibility(View.VISIBLE);
				holder.description.setVisibility(View.VISIBLE);
				holder.more.setVisibility(View.GONE);
				if (null != item.photoUrl) {
					Bitmap cache = ImageLoadCache.getImage(item.photoUrl);
					if (cache == null) {
						new ImageLoadThread(handler, (ArrayAdapter) this, holder.photo, false, true).execute(item.photoUrl);
						holder.photo.setImageResource(R.drawable.noimg);
					} else {
						holder.photo.setImageBitmap(cache);
					}
				}
				holder.title.setText(item.title);
				holder.profileName.setText(item.by + item.profileName);
				holder.description.setText(item.description);
			}
		}
		return convertView;
	}

	class ViewHolder {
		ImageView photo;
		TextView title;
		TextView profileName;
		TextView description;
		TextView more;
	}

}