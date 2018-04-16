package com.mudassirkhan.androidportfolio.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mudassirkhan.androidportfolio.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class PicassoAdapter extends RecyclerView.Adapter<PicassoAdapter.PicassoViewHolder> {

    //Declaration of different types of modes in the Picasso Fragment as constant variables
    public final static int NETWORK_MODE = 10;
    public final static int RESOURCES_MODE = 20;
    public final static int FILES_MODE = 30;

    //Declaration of PicassoAdapter's member variables
    private Context mContext;
    private String[] mNetworkImagePathsArray;
    private List<Integer> mResourceImagesIdList;
    private List<String> mFileImagesPathArray;
    private int mChoosenMode;
    private int mLayout;

    //PicassoAdapter's Constructor
    public PicassoAdapter(Context context, int layout) {
        mContext = context;
        mLayout = layout;
    }

    //Creation of the View Holder
    @Override
    public PicassoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(mLayout, parent, false);
        return new PicassoViewHolder(view);
    }

    //Data binding
    @Override
    public void onBindViewHolder(PicassoViewHolder holder, int position) {

        //Binding pictures according to the mode in which we are
        if (mChoosenMode == NETWORK_MODE) {
            String imageUrl = mContext.getString(R.string.picasso_network_base_url) + mNetworkImagePathsArray[position];
            Picasso.with(mContext).load(imageUrl)
                    .fit().centerCrop()
                    .placeholder(R.mipmap.loading_icon)
                    .into(holder.mPicassoImageView);
        } else if (mChoosenMode == RESOURCES_MODE) {
            int imageResourceId = mResourceImagesIdList.get(position);
            Picasso.with(mContext).load(imageResourceId)
                    .fit().centerCrop()
                    .placeholder(R.mipmap.loading_icon)
                    .into(holder.mPicassoImageView);
        } else if (mChoosenMode == FILES_MODE) {
            String imageFilePath = mFileImagesPathArray.get(position);
            Picasso.with(mContext).load(new File(imageFilePath))
                    .fit().centerCrop()
                    .placeholder(R.mipmap.loading_icon)
                    .into(holder.mPicassoImageView);
        }
    }

    //Getting the count of the data source elements
    @Override
    public int getItemCount() {

        int count = 0;
        if (mChoosenMode == NETWORK_MODE) {
            count = mNetworkImagePathsArray.length;
        } else if (mChoosenMode == RESOURCES_MODE) {
            count = mResourceImagesIdList.size();
        } else if (mChoosenMode == FILES_MODE) {
            count = mFileImagesPathArray.size();
        }
        return count;
    }

    //Method that allows us to refresh the Network data source from outside of the adapter
    public void setNetworkMode(String[] networkImagePathsArray, int mode) {
        if (networkImagePathsArray != null) {
            mNetworkImagePathsArray = networkImagePathsArray;
            mChoosenMode = mode;
            notifyDataSetChanged();

        }
    }

    //Method that allows us to refresh the Resources data source from outside of the adapter
    public void setResourceMode(List<Integer> resourceImagesIdList, int mode) {
        if (resourceImagesIdList != null) {
            mResourceImagesIdList = resourceImagesIdList;
            mChoosenMode = mode;
            notifyDataSetChanged();

        }
    }

    //Method that allows us to refresh the Files data source from outside of the adapter
    public void setFileMode(List<String> fileImagesPathArray, int mode) {
        if (fileImagesPathArray != null) {
            mFileImagesPathArray = fileImagesPathArray;
            mChoosenMode = mode;
            notifyDataSetChanged();
        }
    }

    //Inner class defining the PicassoViewHolder
    static class PicassoViewHolder extends RecyclerView.ViewHolder {

        //Declaration of the View Holder member variable
        private ImageView mPicassoImageView;

        PicassoViewHolder(View itemView) {
            super(itemView);

            //Getting references for the view from the layout
            mPicassoImageView = (ImageView) itemView.findViewById(R.id.picasso_imageview);
        }
    }

}


