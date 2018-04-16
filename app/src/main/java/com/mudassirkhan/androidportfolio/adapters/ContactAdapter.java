package com.mudassirkhan.androidportfolio.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mudassirkhan.androidportfolio.R;
import com.mudassirkhan.androidportfolio.models.Contact;
import com.mudassirkhan.androidportfolio.ui.uielements.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    //Declaration of ContactAdapter's member variables
    private Context mContext;
    private List<Contact> mContactList;
    private int mLayout;
    private OnContactItemClickListener mListener;

    //ContactAdapter's Constructor
    public ContactAdapter(Context context, List<Contact> contactList, int layout, OnContactItemClickListener listener) {
        mContext = context;
        mContactList = contactList;
        mLayout = layout;
        mListener = listener;
    }

    //Creation of the View Holder
    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(mContext).inflate(mLayout, parent, false);
        return new ContactViewHolder(layoutView);
    }

    //Data binding
    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {

        //Getting Contact object for the current adapter position
        Contact contact = mContactList.get(position);

        //Getting data out of the object
        String name = contact.getName();
        String phoneNumber = contact.getPhoneNumber();
        String emailAddress = contact.getEmailAddress();
        String city = contact.getCity();
        String pictureUri = contact.getPictureUri();

        //Binding contact details to the item's layout
        holder.mName.setText(name);
        holder.mPhoneNumber.setText(phoneNumber);
        holder.mEmailAddress.setText(emailAddress);
        holder.mCity.setText(city);

        //Binding image to the Image View
        if (pictureUri != null && !pictureUri.isEmpty()) {
            Picasso.with(mContext).load(pictureUri).transform(new CircleTransform()).into(holder.mPicture);
        } else {
            Picasso.with(mContext).load(R.drawable.empty_picture).transform(new CircleTransform()).into(holder.mPicture);
        }

    }

    //Getting the count of the data source elements
    @Override
    public int getItemCount() {
        if (mContactList == null) {
            return 0;
        }
        return mContactList.size();
    }

    //Method that allows us to refresh the data source from outside of the adapter
    public void swapData(List<Contact> contacts) {
        if (contacts != null) {
            mContactList = contacts;
            notifyDataSetChanged();
        }
    }

    //Inner class defining the Contact View Holder
    class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        //Declaration of the View Holder member variables
        private TextView mName;
        private TextView mPhoneNumber;
        private TextView mEmailAddress;
        private TextView mCity;
        private ImageView mPicture;

        //ContactViewHolder's Constructor
        ContactViewHolder(View itemView) {
            super(itemView);

            //Getting references for the views from the layout
            mName = (TextView) itemView.findViewById(R.id.contact_item_name);
            mPhoneNumber = (TextView) itemView.findViewById(R.id.contact_item_phone_number);
            mEmailAddress = (TextView) itemView.findViewById(R.id.contact_item_email_address);
            mCity = (TextView) itemView.findViewById(R.id.contact_item_city);
            mPicture = (ImageView) itemView.findViewById(R.id.contact_item_picture);

            //Setting Click Listeners on the item layout
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        //Implementation of the Adapter part of the Item Click Listener methods
        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            Contact contact = mContactList.get(clickedPosition);
            mListener.onContactItemClick(contact, clickedPosition);
        }

        @Override
        public boolean onLongClick(View v) {
            int clickedPosition = getAdapterPosition();
            Contact contact = mContactList.get(clickedPosition);
            mListener.onContactItemLongClick(contact, clickedPosition);
            return true;
        }
    }

    //Declaration of an Item Click Listener
    public interface OnContactItemClickListener {
        void onContactItemClick(Contact contact, int position);
        void onContactItemLongClick(Contact contact, int position);
    }

}
