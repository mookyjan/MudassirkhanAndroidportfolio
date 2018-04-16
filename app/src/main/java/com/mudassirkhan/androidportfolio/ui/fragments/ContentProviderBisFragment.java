package com.mudassirkhan.androidportfolio.ui.fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import android.os.RemoteException;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.mudassirkhan.androidportfolio.R;
import com.mudassirkhan.androidportfolio.ui.activities.PortfolioItemsActivity;
import com.mudassirkhan.androidportfolio.utils.DataUtils;
import com.mudassirkhan.androidportfolio.utils.PermissionsUtils;
import com.github.clans.fab.FloatingActionButton;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ContentProviderBisFragment extends Fragment implements View.OnClickListener {

    //Declaration, as a constant String array, of the fields that we will get from the CalendarProvider
    public static final String[] CALENDAR_TABLE_COLUMNS = new String[]{
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.OWNER_ACCOUNT
    };

    //Declaration, as constant Integers, of the permission flags we will need in this fragment
    public final static int PERMISSION_WRITE_CALENDAR = 1;
    public final static int PERMISSION_WRITE_CONTACTS = 2;

    //Declaration, as constant Integers, of flags to refer to Begin or End time
    private static int BEGIN_TIME = 0;
    private static int END_TIME = 1;

    //Declaration of the different Dialog's member variables
    private DatePickerDialog mDatePickerDialog;
    private TimePickerDialog mTimePickerDialog;
    private AlertDialog mCalendarListAlertDialog;

    //Declaration of the toggling between Calendar and Contact modes related views' member variables
    public LinearLayout mCalendarLinearLayout;
    private LinearLayout mContactLinearLayout;
    private FloatingActionButton mSwitchingFab;

    //Declaration of the Calendar related views's member variables
    private TextView mBeginTimeDateTextView;
    private TextView mBeginTimeTimeTextView;
    private TextView mEndTimeDateTextView;
    private TextView mEndTimeTimeTextView;
    private TextView mCalendarTextView;
    private EditText mYourEventEditText;
    private EditText mYourEventDescriptionEditText;
    private CardView mAddEventCardViewButton;
    private CardView mGoToCalendarCardViewButton;

    //Declaration of the Calendar related objects' member variables
    private Calendar mBeginTime;
    private Calendar mEndTime;
    private GoogleCalendar mChosenCalendar;
    private List<GoogleCalendar> mCalendarList;

    //Declaration of the Contact related views's member variables
    private EditText mNameEditText;
    private EditText mPhoneNumberEditText;
    private EditText mEmailAddressEditText;
    private CardView mAddContactCardViewButton;
    private CardView mGoToContactsCardViewButton;

    //Declaration of the fragment's root view's member variable
    private View mFragmentRootView;

    //Declaration of the Shared Preferences' member variable
    private SharedPreferences mSharedPreferences;


    //Empty constructor
    public ContentProviderBisFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mFragmentRootView = inflater.inflate(R.layout.fragment_content_provider_bis, container, false);

        //Setting up Date and Time values
        setUpDateAndTimes();

        //Setting up the fragment's views
        setUpViews();

        //Setting up the activity's accent color on the fragment, so that we can paint our different buttons
        setCustomColorOnFragment();

        //Setting up the fragment's different Dialogs
        setUpRelatedDateAndTimeDialogs();

        //Getting calendar data and setting up the Calendar list
        getAndSetUpCalendarList();

        //Setting up the Shared Preferences
        setUpSharedPreferences();

        return mFragmentRootView;
    }




    /* Setting up the Fragment */


    //Setting up Date and Time values
    private void setUpDateAndTimes() {
        mBeginTime = Calendar.getInstance();
        mEndTime = Calendar.getInstance();
        mEndTime.add(Calendar.HOUR_OF_DAY, 1);
    }


    //Setting up the fragment's views
    private void setUpViews() {

        //Getting the toggling between Calendar and Contact modes related views's references
        mCalendarLinearLayout = (LinearLayout) mFragmentRootView.findViewById(R.id.calendar_content_provider_linearlayout);
        mContactLinearLayout = (LinearLayout) mFragmentRootView.findViewById(R.id.contact_content_provider_linearlayout);
        mSwitchingFab = (FloatingActionButton) mFragmentRootView.findViewById(R.id.content_provider_fab);

        //Getting the Calendar related views's references
        mBeginTimeDateTextView = (TextView) mFragmentRootView.findViewById(R.id.begin_time_date_textview);
        mBeginTimeTimeTextView = (TextView) mFragmentRootView.findViewById(R.id.begin_time_time_textview);
        mEndTimeDateTextView = (TextView) mFragmentRootView.findViewById(R.id.end_time_date_textview);
        mEndTimeTimeTextView = (TextView) mFragmentRootView.findViewById(R.id.end_time_time_textview);
        mCalendarTextView = (TextView) mFragmentRootView.findViewById(R.id.calendar_text_view);
        mYourEventEditText = (EditText) mFragmentRootView.findViewById(R.id.your_event_edittext);
        mYourEventDescriptionEditText = (EditText) mFragmentRootView.findViewById(R.id.your_event_description_edittext);
        mAddEventCardViewButton = (CardView) mFragmentRootView.findViewById(R.id.add_event_cardview_button);
        mGoToCalendarCardViewButton = (CardView) mFragmentRootView.findViewById(R.id.go_to_calendar_cardview_button);

        //Getting the Contact related views's references
        mNameEditText = (EditText) mFragmentRootView.findViewById(R.id.contact_name_edittext);
        mPhoneNumberEditText = (EditText) mFragmentRootView.findViewById(R.id.contact_number_edittext);
        mEmailAddressEditText = (EditText) mFragmentRootView.findViewById(R.id.contact_email_edittext);
        mAddContactCardViewButton = (CardView) mFragmentRootView.findViewById(R.id.add_contact_cardview_button);
        mGoToContactsCardViewButton = (CardView) mFragmentRootView.findViewById(R.id.go_to_contact_cardview_button);

        //Setting a click listener on the relevant views
        mBeginTimeDateTextView.setOnClickListener(this);
        mBeginTimeTimeTextView.setOnClickListener(this);
        mEndTimeDateTextView.setOnClickListener(this);
        mEndTimeTimeTextView.setOnClickListener(this);
        mCalendarTextView.setOnClickListener(this);
        mAddEventCardViewButton.setOnClickListener(this);
        mGoToCalendarCardViewButton.setOnClickListener(this);
        mSwitchingFab.setOnClickListener(this);
        mAddContactCardViewButton.setOnClickListener(this);
        mGoToContactsCardViewButton.setOnClickListener(this);

        //Binding initial data to Calendar related views
        setUpDateTextView(mBeginTimeDateTextView, mBeginTime);
        setUpTimeTextView(mBeginTimeTimeTextView, mBeginTime);
        setUpDateTextView(mEndTimeDateTextView, mEndTime);
        setUpTimeTextView(mEndTimeTimeTextView, mEndTime);
    }


    //Setting up the activity's accent color on the fragment, so that we can paint our different buttons
    private void setCustomColorOnFragment() {

        //Getting the color in which we will paint the different buttons
        Intent intent = getActivity().getIntent();
        int activityAccentColor = ContextCompat.getColor(getActivity(),
                intent.getIntExtra(getActivity().getString(R.string.main_from_detail_activity_background_color_tag), 0));

        //We paint the different CardViews
        mAddEventCardViewButton.setCardBackgroundColor(activityAccentColor);
        mGoToCalendarCardViewButton.setCardBackgroundColor(activityAccentColor);
        mAddContactCardViewButton.setCardBackgroundColor(activityAccentColor);
        mGoToContactsCardViewButton.setCardBackgroundColor(activityAccentColor);

        //We paint the Floating Action Button
        mSwitchingFab.setColorNormal(activityAccentColor);
        mSwitchingFab.setColorPressed(activityAccentColor);
    }


    //Setting up the fragment's different Dialogs
    private void setUpRelatedDateAndTimeDialogs() {
        //We initialize the fragment's different Dialogs
        mDatePickerDialog = new DatePickerDialog(getActivity(), null, 0, 0, 0);
        mTimePickerDialog = new TimePickerDialog(getActivity(), null, 0, 0, false);
        mCalendarListAlertDialog = new AlertDialog.Builder(getActivity()).create();
    }


    //Getting calendar data and setting up the Calendar list
    private void getAndSetUpCalendarList() {
        //If we have the permission to access to the user's Calendar, then we set up all the Calendar related stuff
        if (PermissionsUtils.checkIfHasPermission(getActivity(), Manifest.permission.WRITE_CALENDAR)) {
            setUpCalendarRelated();
        }
    }


    //Setting up the Shared Preferences
    private void setUpSharedPreferences() {
        mSharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
    }




     /* Handling events */


    //Handling click events throughout the fragment
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            //When clicked on the Begin Time Date TextView, we open the corresponding Dialog with the corresponding Date
            case R.id.begin_time_date_textview:
                if (!mDatePickerDialog.isShowing()) {
                    showDatePickerDialog(BEGIN_TIME);
                }
                break;

            //When clicked on the Begin Time Time TextView, we open the corresponding Dialog with the corresponding Time
            case R.id.begin_time_time_textview:
                if (!mTimePickerDialog.isShowing()) {
                    showTimePickerDialog(BEGIN_TIME);
                }
                break;

            //When clicked on the End Time Date TextView, we open the corresponding Dialog with the corresponding Date
            case R.id.end_time_date_textview:
                if (!mDatePickerDialog.isShowing()) {
                    showDatePickerDialog(END_TIME);
                }
                break;

            //When clicked on the End Time Time TextView, we open the corresponding Dialog with the corresponding Time
            case R.id.end_time_time_textview:
                if (!mTimePickerDialog.isShowing()) {
                    showTimePickerDialog(END_TIME);
                }
                break;

            //When clicked on the Calendar TextView, we open the List Alert Dialog that shows the list of Calendars available on the user's device
            case R.id.calendar_text_view:
                //If we have the permission, we open the List Alert Dialog
                if (PermissionsUtils.checkIfHasPermission(getActivity(), Manifest.permission.WRITE_CALENDAR)) {
                    if (!mCalendarListAlertDialog.isShowing()) {
                        showCalendarListAlertDialog();
                    }
                }
                //If not, we show a "Permission denied" toast
                else {
                    PermissionsUtils.showPermissionDeniedToast(getActivity());
                }
                break;

            //When clicked on the Add event button, we try to insert an event to the chosen Calendar
            case R.id.add_event_cardview_button:
                addEvent();
                break;

            //When clicked on the Go to Calendar button, we go to the last added event, it is exists
            case R.id.go_to_calendar_cardview_button:
                goToLastAddedEvent();
                break;

            //When clicked on the Floating Action Button, we switch between the Calendar and Contact modes
            case R.id.content_provider_fab:
                switchLinearLayouts();
                break;

            //When clicked on the Add Contact button, we try to add a contact to the user's device's repertory
            case R.id.add_contact_cardview_button:
                addContact();
                break;

            //When clicked on the Go to Contact button, we go to the last added contact, it is exists
            case R.id.go_to_contact_cardview_button:
                goToLastAddedContact();
                break;
        }

    }




    /* Calendar related helper methods */


    //Method that gets the list of calendars of the user's device
    public void setUpCalendarRelated() {

        //We get the cursor containing the list of calendars
        Cursor cursor = getCalendarListCursor();

        //We get the list of GoogleCalendar objects from the cursor
        mCalendarList = getCalendarListFromCursor(cursor);

        //If there are Calendars in the list, we initially choose the first one
        if (mCalendarList.size() != 0) {
            mCalendarTextView.setText(mCalendarList.get(0).getDisplayName());
            mChosenCalendar = mCalendarList.get(0);
        }
        //If not, we show a toast saying that we have no access to the user's Calendars
        else {
            PortfolioItemsActivity activity = (PortfolioItemsActivity) getActivity();
            if (activity.getFragmentStatePagerAdapter().getItem(activity.getViewPager().getCurrentItem()) == this) {
                Toast.makeText(getActivity(), R.string.content_provider_fragment_bis_calendar_no_access, Toast.LENGTH_SHORT).show();
            }
        }
    }


    //Method that gets the cursor containing the list of Calendars
    private Cursor getCalendarListCursor() {
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        if (!PermissionsUtils.checkIfHasPermission(getActivity(), Manifest.permission.WRITE_CALENDAR)) {
            return null;
        }
        return getActivity().getContentResolver().query(uri, CALENDAR_TABLE_COLUMNS, null, null, null);
    }


    //Method that returns a list of GoogleCalendar objects from the cursor passed in as a parameter
    private List<GoogleCalendar> getCalendarListFromCursor(Cursor cursor) {

        //We create a new GoogleCalendar object list
        List<GoogleCalendar> calendarList = new ArrayList<>();

        //We iterate through the entire cursor
        while (cursor.moveToNext()) {
            //We get the current Calendar info
            long calendarId = cursor.getLong(cursor.getColumnIndex(CALENDAR_TABLE_COLUMNS[0]));
            String calendarDisplayName = cursor.getString(cursor.getColumnIndex(CALENDAR_TABLE_COLUMNS[1]));

            //We add a new GoogleCalendar object to our list, passing in the info we just got
            calendarList.add(new GoogleCalendar(calendarId, calendarDisplayName));
        }

        //We return the list of GoogleCalendar objects
        return calendarList;
    }


    //Method used to add an event to the chosen Calendar
    private void addEvent() {

        //First of all, we check that we have the permission to write in the user's calendars, and if not, we ask for it
        if (!PermissionsUtils.checkIfHasPermission(getActivity(), Manifest.permission.WRITE_CALENDAR)) {
            PermissionsUtils.requestPermission(getActivity(), Manifest.permission.WRITE_CALENDAR, PERMISSION_WRITE_CALENDAR);
            return;
        }

        //We check that our list of GoogleCalendar objects is not empty
        if (mCalendarList != null && mCalendarList.size() != 0) {

            //We get the Begin time and the End time in milliseconds
            long beginTimeMillis = mBeginTime.getTimeInMillis();
            long endTimeMillis = mEndTime.getTimeInMillis();

            //We get the Event and Event Description EditText's values
            String event = mYourEventEditText.getText().toString();
            String eventDescription = mYourEventDescriptionEditText.getText().toString();

            //If the Event EditText is empty, then we show a toast to the user, and we stop processing the method
            if (event.isEmpty()) {
                Toast.makeText(getActivity(), R.string.content_provider_fragment_bis_validation_event_name, Toast.LENGTH_SHORT).show();
                return;
            }
            //If the Event Description EditText is empty, then we show a toast to the user, and we stop processing the method
            else if (eventDescription.isEmpty()) {
                Toast.makeText(getActivity(), R.string.content_provider_fragment_bis_validation_event_description, Toast.LENGTH_SHORT).show();
                return;
            }
            //If the End time is lower than the Begin time, then we show a toast to the user, and we stop processing the method
            else if (endTimeMillis < beginTimeMillis) {
                Toast.makeText(getActivity(), R.string.content_provider_fragment_bis_validation_time, Toast.LENGTH_SHORT).show();
                return;
            }

            //If all of the above checks are ok, we create a ContentValues object, containing all the required data
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.DTSTART, beginTimeMillis);
            values.put(CalendarContract.Events.DTEND, endTimeMillis);
            values.put(CalendarContract.Events.TITLE, event);
            values.put(CalendarContract.Events.DESCRIPTION, eventDescription);
            values.put(CalendarContract.Events.CALENDAR_ID, mChosenCalendar.getId());
            values.put(CalendarContract.Events.EVENT_TIMEZONE, String.valueOf(TimeZone.getDefault()));

            //We insert the event info in the Event Database
            Uri uri = getActivity().getContentResolver().insert(CalendarContract.Events.CONTENT_URI, values);
            long eventID = Long.parseLong(uri.getLastPathSegment());

            //If the event ID that we just got is different than 0, then it worked
            if (eventID != 0) {
                //In this case, we save the last added event ID in the Shared Preferences, and we show a toast to the user
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putLong(getString(R.string.content_provider_fragment_bis_sharedpref_last_added_event_id), eventID);
                editor.apply();
                Toast.makeText(getActivity(), R.string.content_provider_fragment_bis_event_added_to_calendar, Toast.LENGTH_SHORT).show();
            }
            //If the event ID that we just got is equal to 0, then it did not work
            else {
                //In this case, we show a toast to the user
                Toast.makeText(getActivity(), R.string.content_provider_fragment_bis_event_not_added, Toast.LENGTH_SHORT).show();
            }

        }
        //If our list of Calendars is empty, we show a toast to the user
        else {
            Toast.makeText(getActivity(), getString(R.string.content_provider_fragment_bis_calendar_no_access), Toast.LENGTH_SHORT).show();
        }

    }


    //Method that allows us to go to the last added event
    private void goToLastAddedEvent() {

        //First of all, we check that the list of calendars is not empty
        if (mCalendarList != null && mCalendarList.size() != 0) {

            //If it is not empty, we get the last added event ID from the Shared Preferences
            long lastAddedEventId = mSharedPreferences.getLong(getString(R.string.content_provider_fragment_bis_sharedpref_last_added_event_id), 0);

            //If that last added event ID is different than 0, then we go to the Calendar App, passing the event Uri to the intent
            if (lastAddedEventId != 0) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(getString(R.string.content_provider_fragment_bis_google_calendar_uri) + String.valueOf(lastAddedEventId)));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_NO_HISTORY
                        | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                getActivity().startActivity(intent);

            }
            //If it is equal to 0, then we show a toast to the user
            else {
                Toast.makeText(getActivity(), R.string.content_provider_fragment_bis_no_added_events, Toast.LENGTH_SHORT).show();
            }
        }
        //If the list of calendars is empty, then we show a toast to the user
        else {
            Toast.makeText(getActivity(), R.string.content_provider_fragment_bis_no_permission_or_calendar, Toast.LENGTH_SHORT).show();
        }

    }


    //Method used to show the CalendarListAlertDialog
    private void showCalendarListAlertDialog() {

        //First of all, we check that the list of calendars is not empty
        if (mCalendarList != null && mCalendarList.size() != 0) {

            //We pass the list's data to a CharSequence array
            CharSequence[] calendarDisplayNameList = new CharSequence[mCalendarList.size()];
            for (int i = 0; i < mCalendarList.size(); i++) {
                calendarDisplayNameList[i] = mCalendarList.get(i).getDisplayName();
            }

            //Then we create the Dialog, and pass the recently created array to it
            mCalendarListAlertDialog = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.content_provider_fragment_bis_choose_calendar)
                    .setItems(calendarDisplayNameList, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mCalendarTextView.setText(mCalendarList.get(which).getDisplayName());
                            mChosenCalendar = mCalendarList.get(which);
                        }
                    }).create();

            //We show the Dialog
            mCalendarListAlertDialog.show();
        }
        //If the list of calendars is empty, then we show a toast to the user
        else {
            Toast.makeText(getActivity(), getString(R.string.content_provider_fragment_bis_calendar_no_access), Toast.LENGTH_SHORT).show();
        }

    }


    //Method used to show the TimePickerDialog
    private void showTimePickerDialog(final int typeOfTime) {

        //We get the values of the hour and the minute to show
        int hourToShow = mBeginTime.get(Calendar.HOUR_OF_DAY);
        int minuteToShow = mBeginTime.get(Calendar.MINUTE);
        if (typeOfTime == END_TIME) {
            hourToShow = mEndTime.get(Calendar.HOUR_OF_DAY);
            minuteToShow = mEndTime.get(Calendar.MINUTE);
        }

        //We create the Dialog, and pass the hour and minute values to it
        mTimePickerDialog = new TimePickerDialog(
                getActivity(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        //We act upon the chosen time
                        if (typeOfTime == BEGIN_TIME) {
                            mBeginTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            mBeginTime.set(Calendar.MINUTE, minute);
                            setUpTimeTextView(mBeginTimeTimeTextView, mBeginTime);

                        } else if (typeOfTime == END_TIME) {
                            mEndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            mEndTime.set(Calendar.MINUTE, minute);
                            setUpTimeTextView(mEndTimeTimeTextView, mEndTime);
                        }
                    }
                },
                hourToShow,
                minuteToShow,
                true
        );

        //We show the Dialog
        mTimePickerDialog.show();
    }


    //Method used to show the DatePickerDialog
    private void showDatePickerDialog(final int typeOfTime) {

        //We get the values of the year, month and the day to show
        int yearToShow = mBeginTime.get(Calendar.YEAR);
        int monthToShow = mBeginTime.get(Calendar.MONTH);
        int dayToShow = mBeginTime.get(Calendar.DAY_OF_MONTH);
        if (typeOfTime == END_TIME) {
            yearToShow = mEndTime.get(Calendar.YEAR);
            monthToShow = mEndTime.get(Calendar.MONTH);
            dayToShow = mEndTime.get(Calendar.DAY_OF_MONTH);
        }

        //We create the Dialog, and pass the year, month, and day values to it
        mDatePickerDialog = new DatePickerDialog(
                getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        //We act upon the chosen date
                        if (typeOfTime == BEGIN_TIME) {
                            mBeginTime.set(Calendar.YEAR, year);
                            mBeginTime.set(Calendar.MONTH, month);
                            mBeginTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            setUpDateTextView(mBeginTimeDateTextView, mBeginTime);
                        } else if (typeOfTime == END_TIME) {
                            mEndTime.set(Calendar.YEAR, year);
                            mEndTime.set(Calendar.MONTH, month);
                            mEndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            setUpDateTextView(mEndTimeDateTextView, mEndTime);
                        }
                    }
                },
                yearToShow,
                monthToShow,
                dayToShow
        );

        //We show the Dialog
        mDatePickerDialog.show();
    }


    //Method that allows us to set the Time passed in as a parameter to the TextView passed in as a parameter
    private void setUpTimeTextView(TextView textView, Calendar calendar) {

        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        String minuteString = Integer.toString(minute);
        if (minuteString.length() == 1) {
            minuteString = "0" + minuteString;
        }
        textView.setText(hourOfDay + ":" + minuteString);
    }


    //Method that allows us to set the Date passed in as a parameter to the TextView passed in as a parameter
    private void setUpDateTextView(TextView textView, Calendar calendar) {
        textView.setText(getStringDate(calendar));
    }


    //Method that returns as a String format the Date passed in as a parameter
    public static String getStringDate(Calendar calendar) {

        int year = calendar.get(Calendar.YEAR);

        int month = calendar.get(Calendar.MONTH);
        Locale englishLocale = new Locale("en", "us");
        String monthName = new DateFormatSymbols(englishLocale).getMonths()[month];

        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        String dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US);

        return dayOfWeek + ", " + monthName + " " + dayOfMonth + ", " + year;
    }



    /* Contact related helper methods */


    //Method used to add a contact to the user's device's repertory
    private void addContact() {

        //First of all, we check that we have the permission to write in the user's contacts, and if not, we ask for it
        if (!PermissionsUtils.checkIfHasPermission(getActivity(), Manifest.permission.WRITE_CONTACTS)) {
            PermissionsUtils.requestPermission(getActivity(), Manifest.permission.WRITE_CONTACTS, PERMISSION_WRITE_CONTACTS);
            return;
        }

        //We get the Contact's Name, Contact's Phone Number, and Contact's Email Address EditText's values
        String contactName = mNameEditText.getText().toString();
        String contactPhoneNumber = mPhoneNumberEditText.getText().toString();
        String contactEmailAddress = mEmailAddressEditText.getText().toString();


        //If the Contact's Name EditText is empty, then we show a toast to the user, and we stop processing the method
        if (contactName.isEmpty()) {
            Toast.makeText(getActivity(), R.string.content_provider_fragment_bis_validation_contact_name, Toast.LENGTH_SHORT).show();
            return;
        }
        //If both of the other two EditTexts are empty, then we show a toast to the user, and we stop processing the method
        else if (contactPhoneNumber.isEmpty() && contactEmailAddress.isEmpty()) {
            Toast.makeText(getActivity(), R.string.content_provider_fragment_bis_validation_contact_phone_number, Toast.LENGTH_SHORT).show();
            return;
        }
        //If the email address is filled but is not valid, then we show a toast to the user, and we stop processing the method
        else if (!contactEmailAddress.isEmpty() && !DataUtils.isEmailValid(contactEmailAddress)) {
            Toast.makeText(getActivity(), R.string.content_provider_fragment_bis_validation_email_address, Toast.LENGTH_SHORT).show();
            return;
        }

        //We create a list of ContentProviderOperation objects and put the operations we want into it
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        //We add an insert operation to operations list to insert a new raw contact in the table ContactsContract.RawContacts
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        //We add an insert operation to operations list to insert the Name in the table ContactsContract.Data
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, mNameEditText.getText().toString())
                .build());

        //We add an insert operation to operations list to insert the Phone Number in the table ContactsContract.Data
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, mPhoneNumberEditText.getText().toString())
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());

        //We add an insert operation to operations list to insert the Email in the table ContactsContract.Data
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, mEmailAddressEditText.getText().toString())
                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_HOME)
                .build());


        //Then we execute all the insert operations as a single database transaction
        try {

            ContentProviderResult[] results = getActivity().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);

            //We check that we get a result array that has at least one item
            if (results != null && results.length != 0) {

                //In this case, we get the ID of the recently added contact, and store it in the Shared Preferences
                int contactId = Integer.parseInt(results[0].uri.getLastPathSegment());
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putInt(getString(R.string.content_provider_fragment_bis_sharedpref_last_created_contact_id), contactId);
                editor.apply();

                //Then we send a toast to the user to let them know that the operation has been successful
                Toast.makeText(getActivity(), R.string.content_provider_fragment_bis_contact_added, Toast.LENGTH_SHORT).show();

            }
            //If the result array is null or does not have any item, then the operation failed
            else {
                //In this case, we show a toast to the user saying that the contact has not been added
                Toast.makeText(getActivity(), R.string.content_provider_fragment_bis_contact_not_added, Toast.LENGTH_SHORT).show();
            }

        }

        //We handle possible Exceptions
        catch (RemoteException | OperationApplicationException e) {
            Toast.makeText(getActivity(), R.string.content_provider_fragment_bis_contact_not_added, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    //Method that allows us to go to the last added Contact
    public void goToLastAddedContact() {

        //We get the last added Contact ID from the Shared Preferences
        int lastAddedContactId = mSharedPreferences.getInt(getString(R.string.content_provider_fragment_bis_sharedpref_last_created_contact_id), 0);

        //If the ID of the last added contact is equal to 0, there not contact has been added yet.
        if (lastAddedContactId == 0) {
            //In this case, we show a toast to the user saying that there is not contact added yet
            Toast.makeText(getActivity(), R.string.content_provider_fragment_bis_no_added_contacts, Toast.LENGTH_SHORT).show();
            return;
        }

        //If the last added contact does not exist anymore, then we show a toast to the user saying it
        if (!checkIfContactStillExist(lastAddedContactId)) {
            Toast.makeText(getActivity(), R.string.content_provider_fragment_bis_contact_does_not_exist_anymore, Toast.LENGTH_SHORT).show();
            return;
        }


        //If that last added Contact ID is valid, then we go to the Contacts App, passing the Contact Uri to the intent
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(lastAddedContactId));
        intent.setData(uri);
        startActivity(intent);

    }


    //Method that allows us to determine if a Contact still exists in the Contact App
    private boolean checkIfContactStillExist(int lastAddedContactId) {

        //We build the selection and the selectionArgs variables, used to determine which ID we will check
        String selection = ContactsContract.Data.RAW_CONTACT_ID + " = ?";
        String[] selectionArgs = {Long.toString(lastAddedContactId)};

        //We execute the query to the Contacts Database
        Cursor cursor = getActivity().getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, selection, selectionArgs, null);

        //If the cursor does not have data, then the Contact does not exist anymore
        if (!cursor.moveToFirst()) {
            cursor.close();
            return false;
        }

        //If the cursor has data, then the Contact still exist
        cursor.close();
        return true;
    }




    /* Other helper methods */


    //Method used to toggle between the Calendar mode and the Contact mode, when clicked on the Floating Action Button
    private void switchLinearLayouts() {

        //If the Contact mode is showing, then we hide the corresponding Layout, and show the Calendar one
        if (mContactLinearLayout.getVisibility() == View.VISIBLE) {
            mContactLinearLayout.setVisibility(View.GONE);
            mCalendarLinearLayout.setVisibility(View.VISIBLE);
            //We also check that the user has the WRITE_CALENDAR permission, and ask for it if not
            if (!PermissionsUtils.checkIfHasPermission(getActivity(), Manifest.permission.WRITE_CALENDAR)) {
                PermissionsUtils.requestPermission(getActivity(), Manifest.permission.WRITE_CALENDAR, PERMISSION_WRITE_CALENDAR);
            }
        }
        //If the Calendar mode is showing, then we hide the corresponding Layout, and show the Contact one
        else if (mCalendarLinearLayout.getVisibility() == View.VISIBLE) {
            mCalendarLinearLayout.setVisibility(View.GONE);
            mContactLinearLayout.setVisibility(View.VISIBLE);
            //We also check that the user has the WRITE_CONTACTS permission, and ask for it if not
            if (!PermissionsUtils.checkIfHasPermission(getActivity(), Manifest.permission.WRITE_CONTACTS)) {
                PermissionsUtils.requestPermission(getActivity(), Manifest.permission.WRITE_CONTACTS, PERMISSION_WRITE_CONTACTS);

            }
        }
    }




    /* Fragment Lifecycle callbacks */


    //When the Fragment is stopped, we dismiss the dialogs to prevent painful crashes when the device is being rotated
    @Override
    public void onStop() {
        super.onStop();
        if (mDatePickerDialog != null) {
            mDatePickerDialog.dismiss();
        }
        if (mTimePickerDialog != null) {
            mTimePickerDialog.dismiss();
        }
        if (mCalendarListAlertDialog != null) {
            mCalendarListAlertDialog.dismiss();
        }
    }




    /* GoogleCalendar Object class */


    //POJO used for dealing with the list of Calendars in this fragment
    private class GoogleCalendar {

        private long mId;
        private String mDisplayName;

        GoogleCalendar(long id, String displayName) {
            mId = id;
            mDisplayName = displayName;
        }

        public long getId() {
            return mId;
        }

        String getDisplayName() {
            return mDisplayName;
        }
    }

}
