package com.example.mova.model;

import androidx.recyclerview.widget.SortedList;

import com.example.mova.utils.PostConfig;
import com.example.mova.utils.AsyncUtils;
import com.example.mova.utils.LocationUtils;
import com.example.mova.utils.TimeUtils;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

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
     * Gets the date after the given date.
     * If the date does not exist or there is no date after the given date, returns the given date.
     * @param date The date to compare to.
     * @return The next date.
     */
    public Date getNextDate(Date date) {
        int index = dates.indexOf(date);
        if (index < 0) return date;

        Date plusOne = date, minusOne = date;
        if (index + 1 < dates.size()) plusOne = dates.get(index + 1);
        if (index - 1 >= 0)           minusOne = dates.get(index - 1);

        if      (date.compareTo(plusOne) < 0)  return plusOne;
        else if (date.compareTo(minusOne) < 0) return minusOne;
        else                                   return date;
    }

    /**
     * Gets the date before the given date.
     * If the date does not exist or there is no date after the given date, returns the given date.
     * @param date The date to compare to.
     * @return The previous date.
     */
    public Date getPrevDate(Date date) {
        int index = dates.indexOf(date);
        if (index < 0) return date;

        Date plusOne = date, minusOne = date;
        if (index + 1 < dates.size()) plusOne = dates.get(index + 1);
        if (index - 1 >= 0)           minusOne = dates.get(index - 1);

        if      (date.compareTo(plusOne) > 0)  return plusOne;
        else if (date.compareTo(minusOne) > 0) return minusOne;
        else                                   return date;
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
     * Loads and adds all entries to the journal. Clears all previous entries.
     * @param callback The callback to call once all entries have been added.
     */
    public void loadEntries(AsyncUtils.ItemCallback<Throwable> callback) {
        loadEntries(20, (query) -> {}, callback);
    }

    /**
     * Loads and adds all entries to the journal. Clears all previous entries.
     * @param editQuery Is called upon to edit the query in whatever way required before sending.
     * @param callback The callback to call once all entries have been added.
     */
    public void loadEntries(AsyncUtils.ItemCallback<ParseQuery<Post>> editQuery, AsyncUtils.ItemCallback<Throwable> callback) {
        loadEntries(20, editQuery, callback);
    }

    /**
     * Loads and adds all entries to the journal. Clears all previous entries.
     * @param numEntries The number of entries to load.
     * @param callback The callback to call once all entries have been added.
     */
    public void loadEntries(int numEntries, AsyncUtils.ItemCallback<Throwable> callback) {
        loadEntries(20, (query) -> {}, callback);
    }

    /**
     * Loads and adds all entries to the journal. Clears all previous entries.
     * @param numEntries The number of entries to load.
     * @param editQuery Is called upon to edit the query in whatever way required before sending.
     * @param callback The callback to call once all entries have been added.
     */
    public void loadEntries(int numEntries, AsyncUtils.ItemCallback<ParseQuery<Post>> editQuery, AsyncUtils.ItemCallback<Throwable> callback) {
        entries.clear();
        dates.clear();
        loadMoreEntries(numEntries, editQuery, callback);
    }

    /**
     * Loads and adds more entries to the journal.
     * @param callback The callback to call once all entries have been added.
     */
    public void loadMoreEntries(AsyncUtils.ItemCallback<Throwable> callback) {
        loadMoreEntries(20, (query) -> {}, callback);
    }

    /**
     * Loads and adds more entries to the journal.
     * @param editQuery Is called upon to edit the query in whatever way required before sending.
     * @param callback The callback to call once all entries have been added.
     */
    public void loadMoreEntries(AsyncUtils.ItemCallback<ParseQuery<Post>> editQuery, AsyncUtils.ItemCallback<Throwable> callback) {
        loadMoreEntries(20, editQuery, callback);
    }

    /**
     * Loads and adds more entries to the journal.
     * @param numEntries The number of entries to load.
     * @param callback The callback to call once all entries have been added.
     */
    public void loadMoreEntries(int numEntries, AsyncUtils.ItemCallback<Throwable> callback) {
        loadMoreEntries(numEntries, (query) -> {}, callback);
    }

    /**
     * Loads and adds more entries to the journal.
     * @param numEntries The number of entries to load.
     * @param editQuery Is called upon to edit the query in whatever way required before sending.
     * @param callback The callback to call once all entries have been added.
     */
    public void loadMoreEntries(int numEntries, AsyncUtils.ItemCallback<ParseQuery<Post>> editQuery, AsyncUtils.ItemCallback<Throwable> callback) {
        ParseQuery<Post> journalQuery = user.relJournal.getQuery();
        if (entries.size() > 0) {
            journalQuery.whereLessThan(Post.KEY_CREATED_AT, getOldestDate());
        }
        journalQuery.include(Post.KEY_MEDIA);
        journalQuery.setLimit(numEntries);
        editQuery.call(journalQuery);
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

    public void postEntry(Post journalEntry, AsyncUtils.ItemCallback<Throwable> callback) {
        postEntry(new PostConfig(journalEntry), callback);
    }

    public void postEntry(PostConfig config, AsyncUtils.ItemCallback<Throwable> callback) {
        AsyncUtils.ItemCallback<Post> cb = (entry) -> {
            Date today = TimeUtils.getToday();
            SortedList<Post> todayEntries = getEntriesByDate(today);
            todayEntries.add(config.post);
            callback.call(null);
        };

        ParseGeoPoint location = LocationUtils.getCurrentUserLocation();
        if (location != null) config.post.setLocation(location);
        user.postJournalEntry(config, cb);
    }

    public Date getOldestDate() {
        Post oldestPost = getOldestLoadedEntry();
        Date date;
        if (oldestPost == null) {
            date = new Date();
        } else {
            date = oldestPost.getCreatedAt();
        }
        return date;
    }

    public Post getOldestLoadedEntry() {
        // Find the oldest date with at least one entry
        SortedList<Post> entries;
        int i = dates.size() - 1;
        do {
            Date oldest = dates.get(i);
            entries = getEntriesByDate(oldest);
            i--;
        } while (entries.size() == 0 && i > 0);
        // If all dates are empty, return null
        if (entries.size() == 0) return null;
        // Otherwise, fetch the oldest entry from the oldest date
        return entries.get(0);
    }

    public Post getNewestLoadedEntry() {
        // Find the newest date with at least one entry
        SortedList<Post> entries;
        int i = 0;
        do {
            Date oldest = dates.get(i);
            entries = getEntriesByDate(oldest);
            i++;
        } while (entries.size() == 0 && i < dates.size());
        // If all dates are empty, return null
        if (entries.size() == 0) return null;
        // Otherwise, fetch the newest entry from the newest date
        return entries.get(entries.size() - 1);
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
