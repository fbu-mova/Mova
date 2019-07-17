package com.example.mova.fragments.Personal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mova.R;
import com.example.mova.TimeUtils;
import com.example.mova.activities.JournalComposeActivity;
import com.example.mova.adapters.DatePickerAdapter;
import com.example.mova.adapters.JournalEntryAdapter;
import com.example.mova.model.Post;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.ParseException;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link JournalFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link JournalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JournalFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private DatePickerAdapter dateAdapter;
    private JournalEntryAdapter entryAdapter;

    private List<Date> dates;
    private HashMap<Date, List<Post>> entries;
    private Date currDate;

    @BindView(R.id.tvTitle)    protected TextView tvTitle;
    @BindView(R.id.rvDates)    protected RecyclerView rvDates;
    @BindView(R.id.rvEntries)  protected RecyclerView rvEntries;
    @BindView(R.id.fabCompose) protected FloatingActionButton fabCompose;

    public JournalFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment JournalFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static JournalFragment newInstance() {
        JournalFragment fragment = new JournalFragment();
        Bundle args = new Bundle();
        // TODO: Add any arguments if found to be necessary
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // TODO: Set any arguments chosen
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_journal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        dates = new ArrayList<>();
        entries = new HashMap<>();
        currDate = TimeUtils.getToday();

        // On date click, display only the entries for that date
        dateAdapter = new DatePickerAdapter(getActivity(), dates, new DatePickerAdapter.OnItemClickListener() {
            @Override
            public void onClick(View v, Date date, int position) {
                displayEntries(date);
            }
        });

        entryAdapter = new JournalEntryAdapter(getActivity(), getEntries(currDate));

        rvDates.setAdapter(dateAdapter);
        rvDates.setLayoutManager(new LinearLayoutManager(getActivity())); // TODO: Make horizontal

        rvEntries.setAdapter(entryAdapter);
        rvEntries.setLayoutManager(new LinearLayoutManager(getActivity()));

        // On fab click, open compose activity
        fabCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), JournalComposeActivity.class);
                startActivityForResult(intent, JournalComposeActivity.COMPOSE_REQUEST_CODE);
            }
        });

        loadEntries();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // TODO: Potentially uncomment and/or use onAttach
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == JournalComposeActivity.COMPOSE_REQUEST_CODE
                && resultCode == Activity.RESULT_OK) {
            Post journalEntry = data.getParcelableExtra(JournalComposeActivity.KEY_COMPOSED_POST);
            Toast.makeText(getActivity(), "Saving entry...", Toast.LENGTH_SHORT).show();
            journalEntry.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    Toast.makeText(getActivity(), "Saved entry!", Toast.LENGTH_SHORT).show();
                    Date today = TimeUtils.getToday();
                    List<Post> todayEntries = getEntries(today);
                    todayEntries.add(journalEntry);
                    if (currDate.equals(today)) {
                        entryAdapter.notifyItemInserted(todayEntries.size() - 1);
                    }
                }
            });
        }
    }

    private List<Post> getEntries(Date date) {
        List<Post> entriesFromDate = entries.get(date);
        if (entriesFromDate == null) entriesFromDate = new ArrayList<Post>();
        entries.put(date, entriesFromDate);
        // TODO: Add to date list at correct index
        return entriesFromDate;
    }

    private void displayEntries(Date date) {
        List<Post> entriesFromDate = getEntries(date);
        entryAdapter.changeSource(entriesFromDate);
    }

    private void loadEntries() {
        /* TODO:
         * - Fetch all entries
         * - For each entry...
         *   - Try adding it to the list for its date in the hashmap
         *   - If no list exists yet, create one first
         * - Display all dates
         *   - If there are no dates yet, display today unconditionally
         *     - Likely add this into the data structures, too, so that today simply exists
         * - Display all entries for the today
         *   - If there are no entries for a given day, display an empty indicator
         */
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
