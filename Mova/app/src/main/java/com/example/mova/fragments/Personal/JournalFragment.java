package com.example.mova.fragments.Personal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mova.R;
import com.example.mova.model.Journal;
import com.example.mova.model.Tag;
import com.example.mova.model.User;
import com.example.mova.scrolling.EdgeDecorator;
import com.example.mova.scrolling.EndlessScrollRefreshLayout;
import com.example.mova.utils.TimeUtils;
import com.example.mova.activities.JournalComposeActivity;
import com.example.mova.adapters.DatePickerAdapter;
import com.example.mova.adapters.JournalEntryAdapter;
import com.example.mova.model.Post;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link JournalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JournalFragment extends Fragment {

    private DatePickerAdapter dateAdapter;
    private JournalEntryAdapter entryAdapter;

    private Journal journal;
    private Date currDate;

    @BindView(R.id.tvTitle)     protected TextView tvTitle;
    @BindView(R.id.tvDate)      protected TextView tvDate;
//    @BindView(R.id.rvDates)     protected RecyclerView rvDates;
    @BindView(R.id.esrlDates)   protected EndlessScrollRefreshLayout<DatePickerAdapter.ViewHolder> esrlDates; // FIXME: Can I cast it to this generic just like that?
//    @BindView(R.id.rvEntries)   protected RecyclerView rvEntries;
    @BindView(R.id.esrlEntries) protected EndlessScrollRefreshLayout<JournalEntryAdapter.ViewHolder> esrlEntries; // FIXME: Can I cast it to this generic just like that?
    @BindView(R.id.fabCompose)  protected FloatingActionButton fabCompose;

    public JournalFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment JournalFragment.
     */
    public static JournalFragment newInstance() {
        JournalFragment fragment = new JournalFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
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

        currDate = TimeUtils.getToday();

        journal = new Journal(
                (User) User.getCurrentUser(),
                new SortedList.Callback<Date>() {
                    @Override
                    public int compare(Date o1, Date o2) {
                        return Journal.defaultCompareDates(o1, o2);
                    }

                    @Override
                    public void onChanged(int position, int count) {
                        dateAdapter.notifyItemRangeChanged(position, count);
                    }

                    @Override
                    public boolean areContentsTheSame(Date oldItem, Date newItem) {
                        return Journal.defaultDatesEqual(oldItem, newItem);
                    }

                    @Override
                    public boolean areItemsTheSame(Date item1, Date item2) {
                        return Journal.defaultDatesEqual(item1, item2);
                    }

                    @Override
                    public void onInserted(int position, int count) {
                        dateAdapter.notifyItemRangeInserted(position, count);
                    }

                    @Override
                    public void onRemoved(int position, int count) {
                        dateAdapter.notifyItemRangeRemoved(position, count);
                    }

                    @Override
                    public void onMoved(int fromPosition, int toPosition) {
                        dateAdapter.notifyItemMoved(fromPosition, toPosition);
                    }
                },
                Journal.makeDefaultPostSortHandler()
        );

        // On date click, display only the entries for that date
        dateAdapter = new DatePickerAdapter(getActivity(), journal.getDates(), new DatePickerAdapter.OnItemClickListener() {
            @Override
            public void onClick(View v, Date date, int position) {
                displayEntries(date);
            }
        });

        // TODO: Set up embedded media on these, maybe with a component migration for easier use?
        entryAdapter = new JournalEntryAdapter(getActivity(), journal.getEntriesByDate(currDate));

        LinearLayoutManager dateLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true);
        LinearLayoutManager entryLayoutManager = new LinearLayoutManager(getActivity());

        esrlDates.init(
            EndlessScrollRefreshLayout.LayoutSize.match_parent,
            EndlessScrollRefreshLayout.LayoutSize.wrap_content,
            new EndlessScrollRefreshLayout.Handler<DatePickerAdapter.ViewHolder>() {
                @Override
                public void load() {
                    loadEntries();
                }

                @Override
                public void loadMore() {
                    loadMoreEntries();
                }

                @Override
                public RecyclerView.Adapter<DatePickerAdapter.ViewHolder> getAdapter() {
                    return dateAdapter;
                }

                @Override
                public RecyclerView.LayoutManager getLayoutManager() {
                    return dateLayoutManager;
                }

                @Override
                public int[] getColorScheme() {
                    return EndlessScrollRefreshLayout.getDefaultColorScheme();
                }
             }
        );

        esrlEntries.init(
            EndlessScrollRefreshLayout.LayoutSize.match_parent,
            EndlessScrollRefreshLayout.LayoutSize.match_parent,
            new EndlessScrollRefreshLayout.Handler() {
                // TODO: Perhaps only load more entries for that specific date?
                @Override
                public void load() {
                    loadEntries();
                }

                @Override
                public void loadMore() {
                    loadMoreEntries();
                }

                @Override
                public RecyclerView.Adapter<JournalEntryAdapter.ViewHolder> getAdapter() {
                    return entryAdapter;
                }

                @Override
                public RecyclerView.LayoutManager getLayoutManager() {
                    return entryLayoutManager;
                }

                @Override
                public int[] getColorScheme() {
                    return EndlessScrollRefreshLayout.getDefaultColorScheme();
                }
            }
        );

//        rvDates.setAdapter(dateAdapter);
//        rvDates.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true));

//        rvEntries.setLayoutManager(entryLayoutManager);
//        rvEntries.setAdapter(entryAdapter);

        // On fab click, open compose activity
        fabCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), JournalComposeActivity.class);
                startActivityForResult(intent, JournalComposeActivity.COMPOSE_REQUEST_CODE);
            }
        });

        displayEntries(currDate);
        loadEntries();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == JournalComposeActivity.COMPOSE_REQUEST_CODE
                && resultCode == Activity.RESULT_OK) {
            Post journalEntry = data.getParcelableExtra(JournalComposeActivity.KEY_COMPOSED_POST);
            ArrayList<Tag> tags = (ArrayList<Tag>) data.getSerializableExtra(JournalComposeActivity.KEY_COMPOSED_POST_TAGS);
            postJournalEntry(journalEntry, tags);
        }
    }

    private void postJournalEntry(Post journalEntry, List<Tag> tags) {
        journal.postEntry(journalEntry, tags, (e) -> {
            Toast.makeText(getActivity(), "Saved entry!", Toast.LENGTH_SHORT).show();
            if (currDate.equals(TimeUtils.getToday())) {
                entryAdapter.notifyItemInserted(journal.getEntriesByDate(currDate).size() - 1);
            }
        });
    }

    private void displayEntries(Date date) {
        // TODO: Possibly add indicator of date being selected in date picker
        tvDate.setText(TimeUtils.toDateString(date));
        SortedList<Post> entriesFromDate = journal.getEntriesByDate(date);
        entryAdapter.changeSource(entriesFromDate);
    }

    private void loadEntries() {
        journal.loadEntries((e) -> {
            displayEntries(currDate);
            esrlEntries.setRefreshing(false);
            esrlDates.setRefreshing(false);
        });
    }

    private void loadMoreEntries() {
        journal.loadMoreEntries((e) -> {
            displayEntries(currDate);
            esrlEntries.setRefreshing(false);
            esrlDates.setRefreshing(false);
        });
    }
}
