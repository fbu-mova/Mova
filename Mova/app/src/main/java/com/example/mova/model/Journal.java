package com.example.mova.model;

import android.widget.Toast;

import androidx.recyclerview.widget.SortedList;

import com.example.mova.utils.AsyncUtils;
import com.example.mova.utils.TimeUtils;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Loads and handles journal entries for a given user.
 * Does not yet handle reloading or loading more entries.
 */
public class Journal {
    private User user;
    private SortedList.Callback<Date> dateSortHandler;
    private SortedList.Callback<Post> postSortHandler;

    private SortedList<Date> dates;
    private HashMap<Date, SortedList<Post>> entries;

    public Journal(User user) {
        this(user, makeDefaultDateSortHandler(), makeDefaultPostSortHandler());
    }

    public Journal(User user, SortedList.Callback<Date> dateSortHandler, SortedList.Callback<Post> postSortHandler) {
        this.user = user;
        this.dateSortHandler = dateSortHandler;
        this.postSortHandler = postSortHandler;
        this.dates = new SortedList<>(Date.class, dateSortHandler);
        this.entries = new HashMap<>();
    }

    // -- ACCESSORS -- //

    public SortedList<Date> getDates() {
        return dates;
    }

    public HashMap<Date, SortedList<Post>> getEntries() {
        return entries;
    }

    /**
     * Gets the list of entries for a specific date from the hashmap.
     * If no list has been defined for that date, creates and puts an empty list at that date.
     * If no date has been stored that matches that date, creates and adds that date to the list of dates.
     * @param date
     * @return
     */
    public SortedList<Post> getEntriesByDate(Date date) {
        SortedList<Post> entriesFromDate = entries.get(date);
        if (entriesFromDate == null) {
            entriesFromDate = new SortedList<>(Post.class, postSortHandler);
        }
        entries.put(date, entriesFromDate);
        if (dates.indexOf(date) < 0) dates.add(date);
        return entriesFromDate;
    }

    /**
     * Adds an entry to the list for a specific date.
     * If no list yet exists, creates the list first, and handles date creation.
     * @param date
     * @param entry
     */
    public void addEntry(Date date, Post entry) {
        SortedList<Post> entries = getEntriesByDate(date);
        entries.add(entry);
    }

    /**
     * Loads and adds all entries to the journal.
     * For now, should only be called once; it never clears the entries.
     * @param callback The callback to call once all entries have been added.
     */
    public void loadEntries(AsyncUtils.ItemCallback<Throwable> callback) {
        // TODO: Handle unique queries eventually
        ParseQuery<Post> journalQuery = user.relJournal.getQuery();
        journalQuery.findInBackground((List<Post> list, ParseException e) -> {
            if (e != null) {
                callback.call(e);
                return;
            }

            for (Post entry : list) {
                Date date = entry.getCreatedAt();
                date = TimeUtils.normalizeToDay(date);
                addEntry(date, entry);
            }
            callback.call(null);
        });
    }

    public void postEntry(Post journalEntry, List<Tag> tags, AsyncUtils.ItemCallback<Throwable> callback) {
        user.postJournalEntry(journalEntry, tags, (entry) -> {
            Date today = TimeUtils.getToday();
            SortedList<Post> todayEntries = getEntriesByDate(today);
            todayEntries.add(journalEntry);
            callback.call(null);
        });
    }

    // -- STATIC METHODS FOR DEFAULT SORTEDLIST.CALLBACK HANDLING -- //

    public static int defaultCompareDates(Date a, Date b) {
        return b.compareTo(a);
    }

    public static boolean defaultDatesEqual(Date a, Date b) {
        return a.equals(b);
    }

    public static int defaultComparePosts(Post a, Post b) {
        return a.getCreatedAt().compareTo(b.getCreatedAt());
    }

    public static boolean defaultPostsEqual(Post a, Post b) {
        return a.equals(b);
    }

    public static SortedList.Callback<Date> makeDefaultDateSortHandler() {
        return new SortedList.Callback<Date>() {
            @Override
            public int compare(Date o1, Date o2) {
                return defaultCompareDates(o1, o2);
            }

            @Override
            public void onChanged(int position, int count) {

            }

            @Override
            public boolean areContentsTheSame(Date oldItem, Date newItem) {
                return defaultDatesEqual(oldItem, newItem);
            }

            @Override
            public boolean areItemsTheSame(Date item1, Date item2) {
                return defaultDatesEqual(item1, item2);
            }

            @Override
            public void onInserted(int position, int count) {

            }

            @Override
            public void onRemoved(int position, int count) {

            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {

            }
        };
    }

    public static SortedList.Callback<Post> makeDefaultPostSortHandler() {
        return new SortedList.Callback<Post>() {
            @Override
            public int compare(Post o1, Post o2) {
                return defaultComparePosts(o1, o2);
            }

            @Override
            public void onChanged(int position, int count) {

            }

            @Override
            public boolean areContentsTheSame(Post oldItem, Post newItem) {
                return defaultPostsEqual(oldItem, newItem);
            }

            @Override
            public boolean areItemsTheSame(Post item1, Post item2) {
                return defaultPostsEqual(item1, item2);
            }

            @Override
            public void onInserted(int position, int count) {

            }

            @Override
            public void onRemoved(int position, int count) {

            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {

            }
        };
    }
}
