package com.thavelka.feedme;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseObject;

import java.text.ParseException;
import java.util.List;

/**
 * Created by tim on 3/13/15.
 */
public class ParseAdapter extends RecyclerView.Adapter<ParseAdapter.ParseViewHolder> {

    private List<ParseObject> mObjects;

    public ParseAdapter(FragmentActivity activity, List<ParseObject> objects) {
        mObjects = objects;

    }


    @Override
    public ParseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item, viewGroup, false);

        ParseViewHolder viewHolder = new ParseViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ParseViewHolder parseViewHolder, int i) {
        parseViewHolder.bindObject(mObjects.get(i));
    }

    @Override
    public int getItemCount() {
        return mObjects.size();
    }

    public class ParseViewHolder extends RecyclerView.ViewHolder {


        public TextView mNameLabel;
        public TextView mAddressLabel;
        public TextView mDescriptionLabel;


        public ParseViewHolder(View itemView) {
            super(itemView);

            mNameLabel = (TextView) itemView.findViewById(R.id.name);
            mAddressLabel = (TextView) itemView.findViewById(R.id.address);
            mDescriptionLabel = (TextView) itemView.findViewById(R.id.description);
        }

        public void bindObject (ParseObject object) {

            object.getParseObject("restaurant")
                    .fetchIfNeededInBackground(new GetCallback<ParseObject>() {

                        @Override
                        public void done(ParseObject object, com.parse.ParseException e) {
                            mNameLabel.setText(object.getString("name"));
                        }
                    });

            object.getParseObject("restaurant")
                    .fetchIfNeededInBackground(new GetCallback<ParseObject>() {

                        @Override
                        public void done(ParseObject object, com.parse.ParseException e) {
                            mAddressLabel.setText(object.getString("address"));
                        }
                    });


            mDescriptionLabel.setText(object.getString("description"));
        }

    }


}
