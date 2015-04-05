package com.thavelka.feedme;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.List;

public class UserAdapter extends ArrayAdapter<ParseUser> {

    Context mContext;

    public UserAdapter(Context context, int resourceId,
                       List<ParseUser> items) {
        super(context, resourceId, items);
        mContext = context;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        ParseUser user = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.user_list_item, null);
            holder = new ViewHolder();
            holder.nameText = (TextView) convertView.findViewById(R.id.nameText);
            holder.scoreText = (TextView) convertView.findViewById(R.id.scoreText);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.nameText.setText(position + 1 + ". " + user.getUsername());
        holder.scoreText.setText(user.getNumber("score").toString());


        return convertView;
    }

    /*private view holder class*/
    private class ViewHolder {

        TextView nameText;
        TextView scoreText;
    }
}