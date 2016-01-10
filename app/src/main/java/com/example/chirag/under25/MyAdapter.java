package com.example.chirag.under25;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Chirag on 1/9/2016.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private ArrayList<Item> mDataset;
    private Context context;
    private int lastPosition = -1;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView tvHeader;
        public TextView tvDescription;
        public ImageView circleImageView;
        public TextView tvOldPrice;
        public TextView tvNewPrice;
        public CardView cardView;
        Typeface bold;
        Typeface light;
        Context c;


        public ViewHolder(View v, Context context) {
            super(v);
            tvHeader = (TextView) v.findViewById(R.id.item_header);
            tvDescription = (TextView) v.findViewById(R.id.item_description);
            tvOldPrice = (TextView) v.findViewById(R.id.item_old_price);
            tvNewPrice = (TextView) v.findViewById(R.id.item_new_price);
            circleImageView = (ImageView) v.findViewById(R.id.item_image);
            cardView = (CardView) v.findViewById(R.id.card_view);
            bold = Typeface.createFromAsset(context.getAssets(), "fonts/opensans.ttf");
            light = Typeface.createFromAsset(context.getAssets(), "fonts/opensanslight.ttf");


            tvOldPrice.setPaintFlags(tvOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            tvHeader.setTypeface(bold);
            tvDescription.setTypeface(light);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(Context c, ArrayList<Item> myDataset) {
        mDataset = myDataset;
        this.context = c;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v, context);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.tvHeader.setText(mDataset.get(position).getHeader());
        holder.tvDescription.setText(mDataset.get(position).getDescription());
        holder.tvNewPrice.setText(context.getResources().getString(R.string.rupee) + mDataset.get(position).getNew_price());
        holder.tvOldPrice.setText(context.getResources().getString(R.string.rupee) + mDataset.get(position).getold_price());

        setAnimation(holder.cardView, position);


        Picasso.with(context).load(mDataset.get(position).getImage())
                .transform(new CircleTransform())
                .into(holder.circleImageView);

    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            animation.setStartOffset(position * 80);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}