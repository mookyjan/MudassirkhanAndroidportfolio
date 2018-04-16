package com.mudassirkhan.androidportfolio.ui.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mudassirkhan.androidportfolio.R;
import com.mudassirkhan.androidportfolio.adapters.ContactAdapter;
import com.mudassirkhan.androidportfolio.background.ContactLoader;
import com.mudassirkhan.androidportfolio.data.ContactContract;
import com.mudassirkhan.androidportfolio.data.ContactDbHelper;
import com.mudassirkhan.androidportfolio.models.Contact;
import com.mudassirkhan.androidportfolio.ui.uielements.RecyclerViewItemDecoration;
import com.mudassirkhan.androidportfolio.utils.DataUtils;

import com.github.clans.fab.FloatingActionButton;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public abstract class BaseDatabaseFragment extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<List<Contact>>, ContactAdapter.OnContactItemClickListener {

    //Declaration of Flag constants in order to know for the AlertDialog if we are in Insert or Update mode
    private static final int ALERT_DIALOG_INSERT_MODE = 0;
    private static final int ALERT_DIALOG_UPDATE_MODE = 1;

    //Declaration of a constant String in order to make queries not take case into account for sorting values
    protected static final String COLLATE_NO_CASE = " COLLATE NOCASE";

    //Declaration of the RecyclerView's member variables
    private RecyclerView mDataPersistenceRecyclerView;
    private List<Contact> mContactList;
    private ContactAdapter mDataPersistenceAdapter;

    //Declaration of the Insert/Edit Contact Dialog's member variables
    private AlertDialog mContactAlertDialog;
    private int mInsertOrEditAlertDialogMode;
    private LinearLayout mDialogLinearLayout;
    private EditText mDialogNameEditText;
    private EditText mDialogNumberEditText;
    private EditText mDialogEmailEditText;
    private EditText mDialogCityEditText;
    private Button mDialogChoosePictureButton;
    private Button mDialogRemovePictureButton;
    private TextView mDialogPictureFileName;

    //Declaration of the Delete Contact Dialog's member variable
    private AlertDialog mDeleteAlertDialog;

    //Declaration of the databases related member variables
    private SQLiteDatabase mWritableDbAccess;
    private SQLiteDatabase mReadableDbAccess;
    private long mDatabaseLastInsertedOrUpdatedRowId;

    //Declaration the Loader's ID
    private int mLoaderId;

    //Declaration of other views' member variables
    private View mFragmentRootView;
    private LinearLayout mEmptyStateLinearLayout;
    private TextView mEmptyStateTextView;
    private ImageView mEmptyStateImageView;


    //Empty constructor
    public BaseDatabaseFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mFragmentRootView = inflater.inflate(R.layout.fragment_recyclerview, container, false);

        //Setting up the Floating Action Button
        setUpFloatingActionButton();

        //Setting up the Loading Indicator and the Empty View
        setUpLoadingIndicatorAndEmptyView();

        //Initializing the Contact Alert Dialog
        initializeContactAlertDialog();

        //Setting up the RecyclerView
        setUpRecyclerView();

        //Initializing databases related stuff
        initializeDatabases();

        //Getting and showing the data in the RecyclerView
        getAndShowData();

        return mFragmentRootView;
    }



    /* Setting up the Fragment */


    //Setting up Floating Action Button
    private void setUpFloatingActionButton() {
        //Getting the color in which we will paint the Floating Action Button
        Intent intent = getActivity().getIntent();
        int activityAccentColor = ContextCompat.getColor(getActivity(), intent.getIntExtra(
                getActivity().getString(R.string.main_from_detail_activity_background_color_tag), 0));

        //Getting the Floating Action Button's view reference
        FloatingActionButton floatingActionButton = (FloatingActionButton) mFragmentRootView.findViewById(R.id.recyclerview_fragments_fab);

        //Setting the Floating Action Button's click listener
        floatingActionButton.setOnClickListener(this);

        //Painting the Floating Action Button
        floatingActionButton.setColorNormal(activityAccentColor);
        floatingActionButton.setColorPressed(activityAccentColor);
    }


    //Setting up the Loading Indicator and the Empty View
    private void setUpLoadingIndicatorAndEmptyView() {

        //Getting the Loading Indicator's and the Empty View's references
        ProgressBar loadingIndicator = (ProgressBar) mFragmentRootView.findViewById(R.id.downloading_indicator);
        mEmptyStateLinearLayout = (LinearLayout) mFragmentRootView.findViewById(R.id.empty_state_linear_layout);
        mEmptyStateTextView = (TextView) mFragmentRootView.findViewById(R.id.empty_state_textview);
        mEmptyStateImageView = (ImageView) mFragmentRootView.findViewById(R.id.empty_state_imageview);

        //Hiding the Loading Indicator
        loadingIndicator.setVisibility(View.GONE);
    }


    //Initializing the Contact Alert Dialog
    private void initializeContactAlertDialog() {
        mContactAlertDialog = new AlertDialog.Builder(getActivity()).create();
    }


    //Setting up the RecyclerView
    private void setUpRecyclerView() {
        //Getting the RecyclerView's reference
        mDataPersistenceRecyclerView = (RecyclerView) mFragmentRootView.findViewById(R.id.base_fragment_recyclerview);

        //Instantiating data set with empty list
        mContactList = new ArrayList<>();

        //Instantiating the DataPersistenceAdapter
        mDataPersistenceAdapter = new ContactAdapter(getActivity(), mContactList, R.layout.contact_item, this);

        //Instantiating the LayoutManager
        RecyclerView.LayoutManager dataPersistenceLayoutManager = new LinearLayoutManager(getContext());

        //Wiring up the RecyclerView
        mDataPersistenceRecyclerView.setHasFixedSize(true);
        mDataPersistenceRecyclerView.setLayoutManager(dataPersistenceLayoutManager);
        mDataPersistenceRecyclerView.setAdapter(mDataPersistenceAdapter);
        mDataPersistenceRecyclerView.addItemDecoration(new RecyclerViewItemDecoration(getActivity(), R.drawable.recyclerviews_line_divider));
    }


    //Initializing databases related stuff
    private void initializeDatabases() {
        ContactDbHelper dbHelper = new ContactDbHelper(getContext());
        mWritableDbAccess = dbHelper.getWritableDatabase();
        mReadableDbAccess = dbHelper.getReadableDatabase();
        mDatabaseLastInsertedOrUpdatedRowId = -1;
    }


    //Getting and showing the data in the RecyclerView
    private void getAndShowData() {
        if (this.getClass().getSimpleName().equals(getString(R.string.realm_fragment_name))) {
            //If we are in the Realm fragment, we use the startQueryUsingRealm() method
            startQueryUsingRealm();
        } else {
            //If not, we start the Loader
            startLoader();
        }
    }



    /* Handling events */


    //Handling click events throughout the fragment
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            //If clicked on the fragment's fab, then we start the Contact Dialog in insert mode
            case R.id.recyclerview_fragments_fab:
                startDialogToInsert();
                break;

            //If clicked on the Insert/Update Contact Dialog's positive button, we insert in or update the database
            case R.id.data_persistence_alert_dialog_positive_button_id:
                if (!areDialogFieldsAllFilled()) {
                    Toast.makeText(getActivity(), R.string.base_database_fragment_form_validation_checking_all_fields, Toast.LENGTH_SHORT).show();
                } else if (!DataUtils.isEmailValid(mDialogEmailEditText.getText().toString())) {
                    Toast.makeText(getActivity(), R.string.base_database_fragment_form_validation_checking_email, Toast.LENGTH_SHORT).show();
                } else {
                    performInsertOrEditActionOnDatabase();
                    mContactAlertDialog.dismiss();
                }
                break;

            //If clicked on the Contact Dialog's "Choose picture" button, we launch the Photo Picker Activity, to choose a picture
            case R.id.contact_alert_dialog_choose_picture_button:
                startPickerAndCroppingActivity();
                break;

            //If clicked on the the Contact Dialog's remove button, then the picture is removed
            case R.id.contact_alert_dialog_remove_picture_button:
                mDialogPictureFileName.setTag("");
                mDialogPictureFileName.setText("");
                mDialogRemovePictureButton.setVisibility(View.GONE);
                break;
        }
    }


    //Handling results sent back from the Photo Picker
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //We check that the result we get is actually from the Photo Picker Activity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            //We check that the result is OK
            if (resultCode == RESULT_OK) {

                //Then, we set the picture's info on the Dialog
                if (result != null) {
                    Uri resultUri = result.getUri();
                    String uriString = resultUri.toString();
                    mDialogPictureFileName.setTag(uriString);
                    String pictureName = resultUri.getLastPathSegment();
                    mDialogPictureFileName.setText(pictureName);
                    mDialogRemovePictureButton.setVisibility(View.VISIBLE);
                }
            }
            //We handle possible errors
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                try {
                    throw error;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    //Handling when a Contact item is clicked
    @Override
    public void onContactItemClick(Contact contact, int position) {
        //In this case, we start the Contact Dialog in Edit mode
        startDialogToEdit(contact);
    }


    //Handling when a Contact item is long clicked
    @Override
    public void onContactItemLongClick(Contact contact, int position) {
        //In this case, we start the Delete Dialog
        showDeleteAlertDialog(contact, position);
    }



    /* CRUD methods */


    //Abstract method implemented in the inheriting fragments, where the way of querying data will be defined
    public abstract List<Contact> executeQuery(SQLiteDatabase readableDbAccess, String tabName);


    //Method triggered when the user confirms to insert data in the database
    private void insertContactIntoDatabase() {

        //We get the values from the Contact Dialog, and put them in a ContentValues object
        ContentValues values = getContentValuesFromAlertDialog();

        //We execute the executeInsert abstract method, which is the one defined right below
        long newRowId = executeInsert(mWritableDbAccess, ContactContract.ContactEntry.TABLE_NAME, values);

        //If the newRowId long value is equal to -1 or 0, then the insertion did not work
        if (newRowId == -1 || newRowId == 0) {
            Toast.makeText(getActivity(), R.string.base_database_fragment_database_error, Toast.LENGTH_SHORT).show();
            mDatabaseLastInsertedOrUpdatedRowId = -1;
        }
        //Otherwise, it did work!
        else {
            Toast.makeText(getActivity(), mDialogNameEditText.getText().toString().trim() + " " + getString(R.string.base_database_fragment_item_added_correctly), Toast.LENGTH_SHORT).show();
            mDatabaseLastInsertedOrUpdatedRowId = newRowId;
        }
    }


    //Abstract method implemented in the inheriting fragments, where the way of inserting data into the database will be defined
    protected abstract long executeInsert(SQLiteDatabase writableDbAccess, String tabName, ContentValues values);


    //Method triggered when the user confirms to update data in the database
    private void updateContactInDatabase() {

        //We get the values from the Contact Dialog, and put them in a ContentValues object
        ContentValues values = getContentValuesFromAlertDialog();

        //We build the selection and the selectionArgs variables, used to determine which item will be updated
        String selection = ContactContract.ContactEntry._ID + " = ?";
        long contactRowIdFromDatabase = (long) mDialogLinearLayout.getTag();
        String[] selectionArgs = {Long.toString(contactRowIdFromDatabase)};

        //We execute the executeUpdate abstract method, which is the one defined right below
        int nbRowsAffected = executeUpdate(mWritableDbAccess, ContactContract.ContactEntry.TABLE_NAME, values, selection, selectionArgs, contactRowIdFromDatabase);

        //If the nbRowsAffected int value is equal to 0, then the update did not work
        if (nbRowsAffected == 0) {
            Toast.makeText(getActivity(), R.string.base_database_fragment_database_error, Toast.LENGTH_SHORT).show();
            mDatabaseLastInsertedOrUpdatedRowId = -1;
        }
        //Otherwise, it did work!
        else {
            Toast.makeText(getActivity(), mDialogNameEditText.getText().toString().trim() + " " + getString(R.string.base_database_fragment_item_updated_correctly), Toast.LENGTH_SHORT).show();
            mDatabaseLastInsertedOrUpdatedRowId = contactRowIdFromDatabase;
        }
    }


    //Abstract method implemented in the inheriting fragments, where the way of updating data in the database will be defined
    protected abstract int executeUpdate(SQLiteDatabase writableDbAccess, String tabName, ContentValues values, String selection, String[] selectionArgs, long id);


    //Method triggered when the user confirms to delete a Contact item from the database
    public void deleteContactFromDatabase(Contact contact) {

        //We build the selection and the selectionArgs variables, used to determine which contact item will be deleted from the database
        long contactId = contact.getId();
        String contactName = contact.getName();
        String selection = ContactContract.ContactEntry._ID + " = ?";
        String[] selectionArgs = {Long.toString(contactId)};

        //We execute the executeDelete abstract method, which is the one defined right below
        int nbRowsDeleted = executeDelete(mWritableDbAccess, ContactContract.ContactEntry.TABLE_NAME, selection, selectionArgs, contactId);

        //If the nbRowsDeleted int value is equal to 1, then it worked!
        if (nbRowsDeleted == 1) {
            Toast.makeText(getActivity(), contactName + " " + getString(R.string.base_database_fragment_item_deleted_correctly), Toast.LENGTH_SHORT).show();
        }
        //Otherwise, it dit not work!
        else {
            Toast.makeText(getActivity(), getString(R.string.base_database_fragment_database_error), Toast.LENGTH_SHORT).show();
        }

        //Then, once the Contact item is deleted, we query the database again, to update the RecyclerView
        if (this.getClass().getSimpleName().equals(getString(R.string.realm_fragment_name))) {
            startQueryUsingRealm();
        } else {
            startQueryFromContactTable();
        }
    }


    //Abstract method implemented in the inheriting fragments, where the way of deleting data from the database will be defined
    protected abstract int executeDelete(SQLiteDatabase writableDbAccess, String tabName, String selection, String[] selectionArgs, long id);



   /* Dialog related methods */


    //Method that builds the Contact Dialog and returns it
    private AlertDialog getContactAlertDialog(String title) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setView(getContactAlertDialogCustomLayout())
                .setPositiveButton(android.R.string.yes, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }


    //Method that starts the Contact Dialog in order to insert new data into the database
    private void startDialogToInsert() {
        if (!mContactAlertDialog.isShowing()) {
            //In case it is not already showing, we show it and set its flag to the insert mode
            showContactAlertDialog(getString(R.string.base_database_fragment_add_contact_dialog_title));
            mInsertOrEditAlertDialogMode = ALERT_DIALOG_INSERT_MODE;
        }
    }


    //Method that starts the Contact Dialog in order to edit data in the database
    private void startDialogToEdit(Contact contact) {
        if (!mContactAlertDialog.isShowing()) {

            //In case it is not already showing, we show it and set its flag to the edit mode
            showContactAlertDialog(getString(R.string.base_database_fragment_update_contact_dialog_title));
            mInsertOrEditAlertDialogMode = ALERT_DIALOG_UPDATE_MODE;

            //We bind the Contact Dialog's views with the Contact's data
            mDialogLinearLayout.setTag(contact.getId());
            mDialogNameEditText.setText(contact.getName());
            mDialogNumberEditText.setText(contact.getPhoneNumber());
            mDialogEmailEditText.setText(contact.getEmailAddress());
            mDialogCityEditText.setText(contact.getCity());

            //We do the same with the picture info and Uri
            String pictureUri = contact.getPictureUri();
            if (pictureUri != null && !pictureUri.isEmpty()) {
                mDialogPictureFileName.setText(pictureUri.substring(pictureUri.lastIndexOf('/') + 1));
                mDialogPictureFileName.setTag(pictureUri);
                mDialogRemovePictureButton.setVisibility(View.VISIBLE);
            }
        }
    }


    //Method called by the startDialogToInsert() and startDialogToEdit() methods to show the Contact Dialog
    private void showContactAlertDialog(String title) {
        mContactAlertDialog = getContactAlertDialog(title);
        mContactAlertDialog.show();
        initializeContactAlertDialogButtons();
    }


    //Method that initializes the Contact Dialog's buttons
    private void initializeContactAlertDialogButtons() {
        Button dialogPositiveButton = mContactAlertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        if (dialogPositiveButton != null) {
            dialogPositiveButton.setId(R.id.data_persistence_alert_dialog_positive_button_id);
            dialogPositiveButton.setOnClickListener(this);
        }
        mDialogChoosePictureButton.setOnClickListener(this);
        mDialogRemovePictureButton.setOnClickListener(this);
    }


    //Method that builds the Delete Contact Dialog and returns it
    private AlertDialog getDeleteAlertDialog(Context context, final Fragment fragment, final Contact contact) {
        return new AlertDialog.Builder(context)
                .setTitle(contact.getName())
                .setMessage(R.string.various_fragments_delete_dialog_delete_confirmation)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ((BaseDatabaseFragment) fragment).deleteContactFromDatabase(contact);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                }).create();
    }


    //Method that shows the Delete Contact Dialog
    private void showDeleteAlertDialog(Contact contact, int position) {
        mDeleteAlertDialog = getDeleteAlertDialog(getActivity(), this, contact);
        mDeleteAlertDialog.show();
    }



    /* Helper methods */


    //Method used to start the Loader, when used by the fragment
    public void startLoader() {
        mLoaderId = getLoaderId();
        startQueryFromContactTable();
    }


    //Abstract method implemented in the inheriting fragments, that returns the Loader's ID
    protected abstract int getLoaderId();


    //Method that will start the Loader, when the Loader is used
    private void startQueryFromContactTable() {
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.restartLoader(mLoaderId, null, this);
    }


    //Method that will start a query from the Realm database, when Realm is used
    private void startQueryUsingRealm() {
        List<Contact> contacts = executeQuery(null, null);
        showDataFromDatabase(contacts);
    }


    //Method used to start the Activity that will allow us choose a picture for the contact items
    private void startPickerAndCroppingActivity() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(getContext(), this);
    }


    //Method that will be triggered when the user clicks on the "OK" button in the Contact Dialog
    private void performInsertOrEditActionOnDatabase() {
        if (mInsertOrEditAlertDialogMode == ALERT_DIALOG_INSERT_MODE) {
            //If the Contact Dialog was opened in insert mode, then the data will be inserted into the database
            insertContactIntoDatabase();
        } else if (mInsertOrEditAlertDialogMode == ALERT_DIALOG_UPDATE_MODE) {
            //If the Contact Dialog was opened in edit mode, then the contact's data will be updated in the database
            updateContactInDatabase();
        }

        //Then, after inserting or updating, we query again the database to show updated data to the user
        if (this.getClass().getSimpleName().equals(getString(R.string.realm_fragment_name))) {
            startQueryUsingRealm();
        } else {
            startQueryFromContactTable();
        }
    }


    //Method that allows us to determine whether all the mandatory fields in the Contact Dialog are filled
    private boolean areDialogFieldsAllFilled() {
        return !(mDialogNameEditText.getText().toString().isEmpty()
                || mDialogNumberEditText.getText().toString().isEmpty()
                || mDialogEmailEditText.getText().toString().isEmpty()
                || mDialogCityEditText.getText().toString().isEmpty());
    }


    //Method that initializes all the Contact Dialog's views' references and returns the Root Layout from the Dialog
    private ViewGroup getContactAlertDialogCustomLayout() {
        ViewGroup rootLayout = (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.contact_alert_dialog_layout, null);
        mDialogLinearLayout = (LinearLayout) rootLayout.findViewById(R.id.contact_alert_dialog_linear_layout);
        mDialogNameEditText = (EditText) rootLayout.findViewById(R.id.contact_alert_dialog_name_edit_text);
        mDialogNumberEditText = (EditText) rootLayout.findViewById(R.id.contact_alert_dialog_phone_number_edit_text);
        mDialogEmailEditText = (EditText) rootLayout.findViewById(R.id.contact_alert_dialog_email_address_edit_text);
        mDialogCityEditText = (EditText) rootLayout.findViewById(R.id.contact_alert_dialog_city_edit_text);
        mDialogChoosePictureButton = (Button) rootLayout.findViewById(R.id.contact_alert_dialog_choose_picture_button);
        mDialogRemovePictureButton = (Button) rootLayout.findViewById(R.id.contact_alert_dialog_remove_picture_button);
        mDialogPictureFileName = (TextView) rootLayout.findViewById(R.id.contact_alert_dialog_picture_file_name);
        return rootLayout;
    }


    //Method that returns a ContentValues object that contains the data from the Contact Dialog
    private ContentValues getContentValuesFromAlertDialog() {
        ContentValues values = new ContentValues();
        values.put(ContactContract.ContactEntry.COLUMN_NAME_NAME, mDialogNameEditText.getText().toString().trim());
        values.put(ContactContract.ContactEntry.COLUMN_NAME_PHONE_NUMBER, mDialogNumberEditText.getText().toString().trim());
        values.put(ContactContract.ContactEntry.COLUMN_NAME_EMAIL_ADDRESS, mDialogEmailEditText.getText().toString().trim());
        values.put(ContactContract.ContactEntry.COLUMN_NAME_CITY, mDialogCityEditText.getText().toString().trim());
        values.put(ContactContract.ContactEntry.COLUMN_NAME_PICTURE_URI, (String) mDialogPictureFileName.getTag());
        return values;
    }


    //Method used to check if we got data from the query, and show it if that was the case
    private void showDataFromDatabase(List<Contact> contacts) {

        //If the List of Contacts is null, then we show the empty view
        if (contacts == null) {
            isNotShowingData(getString(R.string.base_database_fragment_no_data_in_database), R.mipmap.empty_folder_icon);
            return;
        }
        //If the List of Contacts is empty, then we show the empty view
        else if (contacts.size() == 0) {
            isNotShowingData(getString(R.string.base_database_fragment_no_data_in_database), R.mipmap.empty_folder_icon);
        }
        //If there is data in the list, we show it
        else {
            isShowingData();
        }

        //We update the RecyclerView's adapter
        mContactList = contacts;
        mDataPersistenceAdapter.swapData(mContactList);

        //In case it was an update or an insertion, then we get the position of the item in the list and smoothly scroll to it
        if (mDatabaseLastInsertedOrUpdatedRowId != -1) {
            int insertedOrModifiedRowInRecyclerView = findInsertedOrModifiedRowInRecyclerView(mDatabaseLastInsertedOrUpdatedRowId);
            if (insertedOrModifiedRowInRecyclerView != -1) {
                mDataPersistenceRecyclerView.smoothScrollToPosition(insertedOrModifiedRowInRecyclerView);
            }
            mDatabaseLastInsertedOrUpdatedRowId = -1;
        }

    }


    //Method that returns a list of Contacts from the cursor passed in as a parameter
    protected List<Contact> getContactListFromCursor(Cursor cursor) {
        List<Contact> contactList = new ArrayList<>();
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndex(ContactContract.ContactEntry._ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactContract.ContactEntry.COLUMN_NAME_NAME));
            String number = cursor.getString(cursor.getColumnIndex(ContactContract.ContactEntry.COLUMN_NAME_PHONE_NUMBER));
            String email = cursor.getString(cursor.getColumnIndex(ContactContract.ContactEntry.COLUMN_NAME_EMAIL_ADDRESS));
            String city = cursor.getString(cursor.getColumnIndex(ContactContract.ContactEntry.COLUMN_NAME_CITY));
            String pictureUri = cursor.getString(cursor.getColumnIndex(ContactContract.ContactEntry.COLUMN_NAME_PICTURE_URI));
            contactList.add(new Contact(id, name, number, email, city, pictureUri));
        }
        return contactList;
    }


    //Methods that returns the position of the last inserted or last updated item in the RecyclerView
    private int findInsertedOrModifiedRowInRecyclerView(long insertedOrModifiedId) {
        int insertedOrModifiedRowInRecyclerView = -1;
        for (int i = 0; i < mContactList.size(); i++) {
            if (mContactList.get(i).getId() == insertedOrModifiedId) {
                insertedOrModifiedRowInRecyclerView = i;
                break;
            }
        }
        return insertedOrModifiedRowInRecyclerView;
    }


    //Method that hides the empty view
    private void isShowingData() {
        if (mEmptyStateLinearLayout.getVisibility() == View.VISIBLE) {
            mEmptyStateLinearLayout.setVisibility(View.INVISIBLE);
        }
    }


    //Method that shows the empty view and bind data to it
    protected void isNotShowingData(String emptyTextViewContent, int emptyViewIconImage) {
        Picasso.with(getActivity()).load(emptyViewIconImage).into(mEmptyStateImageView);
        mEmptyStateTextView.setText(emptyTextViewContent);
        if (mEmptyStateLinearLayout.getVisibility() != View.VISIBLE) {
            mEmptyStateLinearLayout.setVisibility(View.VISIBLE);
        }
    }




    /* Loader callbacks */


    //When the Loader is created, this method returns a ContactLoader instance
    @Override
    public Loader<List<Contact>> onCreateLoader(int id, Bundle bundle) {
        return new ContactLoader(getActivity(), mReadableDbAccess, this);
    }


    //Triggered when Loader is reset
    @Override
    public void onLoaderReset(Loader<List<Contact>> loader) {
        mDataPersistenceAdapter.swapData(null);
    }


    //Triggered when loading is finished
    @Override
    public void onLoadFinished(Loader<List<Contact>> loader, List<Contact> contacts) {
        showDataFromDatabase(contacts);
    }




    /* Fragment Lifecycle callbacks */


    //When the Fragment is stopped, we dismiss the dialogs to prevent painful crashes when the device is being rotated
    @Override
    public void onStop() {
        super.onStop();
        if (mDeleteAlertDialog != null) {
            mDeleteAlertDialog.dismiss();
        }
    }

}

