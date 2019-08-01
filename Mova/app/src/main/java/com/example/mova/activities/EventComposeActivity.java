package com.example.mova.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.mova.R;
import com.example.mova.fragments.SocialFragment;
import com.example.mova.model.Event;
import com.example.mova.model.Group;
import com.example.mova.model.Tag;
import com.example.mova.model.User;
import com.example.mova.utils.EventUtils;
import com.example.mova.utils.GroupUtils;
import com.example.mova.utils.TextUtils;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventComposeActivity extends AppCompatActivity {

    User user;
    final Calendar myCalendar = Calendar.getInstance();
    private List<String> tags;
    private List<Group> groups;
    String[] groupArr;
    Address address;
    Event event;
    Boolean boolChangedLocation;
    Boolean boolChangeDate;


    @BindView(R.id.ibEventImage)
    ImageButton ibEventImage;

    @BindView(R.id.etTitle)
    EditText etTitle;

    @BindView(R.id.actvParentGroup)
    AutoCompleteTextView actvParentGroup;

    @BindView(R.id.etDate)
    EditText etDate;

    @BindView(R.id.etDescription)
    EditText etDescription;

    @BindView(R.id.etLocation)
    EditText etLocation;

    @BindView(R.id.etAddTag)
    EditText etAddTag;

    @BindView(R.id.btnAddTag)
    Button btnAddTag;

    @BindView(R.id.tvTags)
    TextView tvTags;

    @BindView(R.id.btnSave)
    Button btnSave;

    @BindView(R.id.btnOpenMap)
    Button btnOpenMap;

    @BindView(R.id.btnDeleteEvent)
    Button btnDeleteEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_compose);
        ButterKnife.bind(this);
        user = (User) ParseUser.getCurrentUser();
        tags = new ArrayList<>();
        groups = new ArrayList<>();

        //Intialize
        Intent intent = getIntent();
        boolChangedLocation = false;
        boolChangeDate = false;
        btnDeleteEvent.setVisibility(View.GONE);

        if(intent.getParcelableExtra("event") == null){
            event = new Event();
        }else{
            event = intent.getParcelableExtra("event");
            btnDeleteEvent.setVisibility(View.VISIBLE);

            btnDeleteEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EventComposeActivity.this);
                    builder.setMessage("Are you sure you want to delete this event?")
                    .setTitle("Confirm")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            event.deleteInBackground();
                            user.relEvents.remove(event, () -> {});
                            finish();
                            Intent intent1 = new Intent(EventComposeActivity.this, SocialFragment.class);
                            startActivity(intent1);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            });
            //Add Image
            ParseFile file = event.getEventPic();
            if(file != null){
                String imageUrl = file.getUrl();
                Glide.with(EventComposeActivity.this)
                        .load(imageUrl)
                        .into(ibEventImage);
            }

            //Add Title
            etTitle.setText(event.getTitle());

            //Add Parent Group
            event.getParentGroupName(event.getParentGroup(), (name) -> {
                actvParentGroup.setText(name);
            });

//            etDate.setText(event.getDate().toString());

            etDescription.setText(event.getDescription());

            EventUtils.getTags(event, (oldTagList) -> {
                for(Tag tag: oldTagList){
                    updateTags(tag.getName(), true);
                }
            });

        }

        //Parent Group
        GroupUtils.getUserGroups(user, (userGroups) -> {
            groupArr = new String[userGroups.size()];
            groups.addAll(userGroups);
            int i = 0;
            for(Group group: userGroups){
                groupArr[i] = group.getName();
                i++;
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, groupArr);
            actvParentGroup.setThreshold(1);
            adapter.notifyDataSetChanged();
            actvParentGroup.setAdapter(adapter);
        });

        //Calender Picking
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                boolChangeDate = true;
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        //Set location
        btnOpenMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolChangedLocation = true;
                Intent intent = new Intent(EventComposeActivity.this, MapsActivity.class);
                startActivityForResult(intent, 2);
            }
        });

        etDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(EventComposeActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        ibEventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo- open dialog to get image
            }
        });

        btnAddTag.setOnClickListener((view) -> {
            String tagName = etAddTag.getText().toString();
            if (tagName.equals("")) {
                Toast.makeText(EventComposeActivity.this, "Write a tag first!", Toast.LENGTH_LONG).show();
            } else {
                updateTags(tagName, true);
                etAddTag.setText("");
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event.saveInBackground();
                event.setTitle(etTitle.getText().toString())
                        .setDescription(etDescription.getText().toString())
                .setHost(user);

                //set parent group if the field is valid
                for(int i = 0; i < groupArr.length ; i++){
                    if(actvParentGroup.getText().toString().equals(groupArr[i].toString())){
                        Group group = groups.get(i);
                        event.relGroup.add(group);
                        event.setParentGroup(group);
                        group.relEvents.add(event);
                        group.saveInBackground();
                    }else{
                        Toast.makeText(EventComposeActivity.this, "Group is invalid, add group later", Toast.LENGTH_SHORT).show();
                    }
                }

                //Add tags
                for(String tagString: tags){
                    Tag tag = new Tag(tagString);
                    event.relTags.add(tag);
                    tag.relEvents.add(event);
                    tag.saveInBackground();
                }

                //Add Participants
                event.relParticipants.add(user);
                user.relEvents.add(event);
                user.saveInBackground();

                //Set Date
                //event.setDate(new Date(etDate.toString()));
                if(boolChangeDate) {
                    event.setDate(myCalendar.getTime());
                }

                //Set Location
                if(address != null && boolChangedLocation) {
                    ParseGeoPoint geoPoint = new ParseGeoPoint(address.getLatitude(), address.getLongitude());
                    event.setLocation(geoPoint);
                }
                event.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        finish();
                    }
                });
//                Intent intent = new Intent(EventComposeActivity.this, SocialFragment.class);
//                onActivityResult(1, RESULT_OK, intent);

            }
        });
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        etDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateTags(String tag, boolean shouldKeep) {
        if (shouldKeep && !tags.contains(tag)) {
            tags.add(tag);
        } else {
            tags.remove(tag);
        }
        TextUtils.writeCommaSeparated(tags, "No tags", tvTags, (str) -> str);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                address = data.getParcelableExtra("address");
                if(address != null){
                    etLocation.setText(address.getAddressLine(0));
                }
            }
        }
    }
}
