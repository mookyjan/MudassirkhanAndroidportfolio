package com.mudassirkhan.androidportfolio.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mudassirkhan.androidportfolio.R;
import com.mudassirkhan.androidportfolio.models.PortfolioItem;

import java.util.List;

public class PortfolioAdapter extends RecyclerView.Adapter<PortfolioAdapter.PortfolioViewHolder> {

    //Declaration of PortfolioAdapter's member variables
    private Context mContext;
    private List<PortfolioItem> mPortfolioItems;
    private int mLayout;
    private OnItemClickListener mListener;

    //PortfolioAdapter's Constructor
    public PortfolioAdapter(List<PortfolioItem> portfolioItems, int layout, Context context, OnItemClickListener listener) {
        mPortfolioItems = portfolioItems;
        mLayout = layout;
        mContext = context;
        mListener = listener;
    }

    //Creation of the View Holder
    @Override
    public PortfolioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(mContext).inflate(mLayout, parent, false);
        return new PortfolioViewHolder(layoutView);
    }

    //Data binding
    @Override
    public void onBindViewHolder(PortfolioViewHolder holder, int position) {

        //Getting portfolio item object for the current adapter position
        PortfolioItem currentItem = mPortfolioItems.get(position);

        //Getting data out of the object
        String title = currentItem.getTitle();
        String description = currentItem.getDescription();
        int iconResource = currentItem.getIcon();
        int iconBackgroundColor = currentItem.getIconBackgroundColor();

        //Binding image resource and color to icon Image View
        ImageView iconImageView = holder.mIcon;
        int colorToUse = ContextCompat.getColor(mContext, iconBackgroundColor);
        iconImageView.setColorFilter(colorToUse, PorterDuff.Mode.MULTIPLY);
        iconImageView.setImageResource(iconResource);

        //Binding title and description to the item's layout
        holder.mTitle.setText(title);
        holder.mDescription.setText(description);
    }

    //Getting the count of the data source elements
    @Override
    public int getItemCount() {
        return mPortfolioItems.size();
    }


    //Inner class defining the Portfolio View Holder
    class PortfolioViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //Declaration of the View Holder member variables
        private ImageView mIcon;
        private TextView mTitle;
        private TextView mDescription;

        //Portfolio View Holder Constructor
        PortfolioViewHolder(View itemView) {

            super(itemView);

            //Getting references for the item views from the layout
            mIcon = (ImageView) itemView.findViewById(R.id.portfolio_item_icon);
            mTitle = (TextView) itemView.findViewById(R.id.portfolio_item_title);
            mDescription = (TextView) itemView.findViewById(R.id.portfolio_item_description);

            //Setting a Click Listener on the item layout
            itemView.setOnClickListener(this);
        }

        //Implementation of the Adapter part of the Item Click Listener
        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            PortfolioItem clickedItem = mPortfolioItems.get(clickedPosition);
            mListener.onItemClick(clickedItem, clickedPosition, itemView);
        }
    }

    //Declaration of an Item Click Listener
    public interface OnItemClickListener {
        void onItemClick(PortfolioItem portfolioItem, int position, View view);
    }

}
