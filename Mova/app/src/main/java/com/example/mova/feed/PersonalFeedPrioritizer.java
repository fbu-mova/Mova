package com.example.mova.feed;

import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.SortedList;

import com.example.mova.components.Component;
import com.example.mova.components.JournalPromptComponent;
import com.example.mova.components.TomorrowFocusPromptComponent;
import com.example.mova.model.Goal;
import com.example.mova.model.Post;
import com.example.mova.model.User;
import com.example.mova.utils.AsyncUtils;
import com.example.mova.utils.GoalUtils;
import com.example.mova.utils.TimeUtils;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

public class PersonalFeedPrioritizer extends Prioritizer<ParseObject> {

    public PersonalFeedPrioritizer() {
        super();
        // TODO: Write priorities
    }

    @Override
    public float priorityOf(ParseObject item) {
        return 0;
    }

    public void makeCards(SortedList<PrioritizedComponent> addTo, AsyncUtils.ItemCallback<Throwable> callback) {
        ArrayList<AsyncUtils.ExecuteManyCallback> asyncActions = new ArrayList<>();

        asyncActions.add((Integer position, AsyncUtils.ItemCallback<Throwable> cb) -> makeSpecialCards(addTo, cb));

        // Get recent goals, add their respective check-in cards
        ParseQuery<Goal> goalQuery = ((User) User.getCurrentUser()).relGoals.getQuery();
        goalQuery.include(Goal.KEY_FROM_GROUP);
        goalQuery.setLimit(10);
        goalQuery.orderByDescending(Goal.KEY_CREATED_AT);
        asyncActions.add((Integer position, AsyncUtils.ItemCallback<Throwable> cb) ->
            goalQuery.findInBackground((goals, e) -> {
                if (e != null) {
                    cb.call(e);
                } else if (goals.size() > 0) {
                    makeGoalCheckIns(addTo, goals, cb);
                }
        }));

        // Get recent journal entries, add their respective memory cards
        // TODO: Write a more meaningful query here
        ParseQuery<Post> journalQuery = ((User) User.getCurrentUser()).relJournal.getQuery();
        journalQuery.orderByDescending(Post.KEY_CREATED_AT);
        journalQuery.setLimit(20);
        asyncActions.add((Integer position, AsyncUtils.ItemCallback<Throwable> cb) ->
            journalQuery.findInBackground((entries, e) -> {
                if (e != null) {
                    cb.call(e);
                } else if (entries.size() > 0) {
                    makeJournalMemories(addTo, entries);
                    cb.call(null);
                }
        }));

        // Get recent scrapbook entries, add their respective memory cards
        ParseQuery<Post> scrapbookQuery = ((User) User.getCurrentUser()).relScrapbook.getQuery();
        scrapbookQuery.orderByDescending(Post.KEY_CREATED_AT);
        scrapbookQuery.setLimit(20);
        asyncActions.add((Integer position, AsyncUtils.ItemCallback<Throwable> cb) ->
            scrapbookQuery.findInBackground((entries, e) -> {
                if (e != null) {
                    cb.call(e);
                } else if (entries.size() > 0) {
                    makeScrapbookMemories(addTo, new ArrayList<>());
                    cb.call(null);
                }
        }));

        // Execute all queries and return via final callback
        AsyncUtils.executeMany(asyncActions, callback);
    }

    protected void makeSpecialCards(SortedList<PrioritizedComponent> addTo, AsyncUtils.ItemCallback<Throwable> callback) {
        // Get all values required for conditionals
        Date now = new Date();
        // FIXME: These times are totally arbitrary--do they seem alright?
        Date morningEnd = TimeUtils.setTime(now, "11:00:00:000");
        Date eveningStart = TimeUtils.setTime(now, "17:00:00:000");

        List<AsyncUtils.ExecuteManyCallback> asyncActions = new ArrayList<>();

        // Check number of journal entries to determine whether to add journal prompt
        ParseQuery<Post> journalQuery = ((User) User.getCurrentUser()).relJournal.getQuery();
        journalQuery.whereGreaterThan(Post.KEY_CREATED_AT, TimeUtils.getToday());
        asyncActions.add((i, cb) ->
            journalQuery.findInBackground((entries, e) -> {
                if (e != null) {
                    cb.call(e);
                } else {
                    if (entries.size() == 0) {
                        JournalPromptComponent card = new JournalPromptComponent();
                        addTo.add(new PrioritizedComponent(card, 100));
                        cb.call(null);
                    }
                }
        }));

        if (now.compareTo(eveningStart) >= 0) {
            asyncActions.add((i, cb) -> {
                TomorrowFocusPromptComponent card = new TomorrowFocusPromptComponent(0, 5) {
                    @Override
                    public void onLoadGoals(Throwable e) {
                        if (e != null) {
                            Log.e("PersonalFeedPrioritizer", "Failed to build TomorrowFocusPromptComponent", e);
                            cb.call(e);
                        } else {
                            addTo.add(new PrioritizedComponent(this, 100));
                            cb.call(null);
                        }
                    }
                };
            });
        }

        AsyncUtils.executeMany(asyncActions, callback);
    }

    protected void makeGoalCheckIns(SortedList<PrioritizedComponent> addTo, List<Goal> goals, AsyncUtils.ItemCallback<Throwable> callback) {
        // Get goals sorted by progress
        GoalUtils goalUtils = new GoalUtils();
        goalUtils.sortGoals(goals, 7, ((User) User.getCurrentUser()), (TreeSet<Prioritized<Goal>> pGoals) -> {
            // Choose top and bottom goals based on quantities in config
            // TODO: Scale to more than one of each goal if desired
            Prioritized<Goal> worstGoal = pGoals.first(), bestGoal = pGoals.last();
            // TODO: Add conditions to determine whether to display best, worst, or both
            // TODO: Create goal check-in cards with variable messages
            callback.call(null);
        });
    }

    protected void makeJournalMemories(SortedList<PrioritizedComponent> addTo, List<Post> entries) {
        // TODO: Choose random journal entry
    }

    protected void makeScrapbookMemories(SortedList<PrioritizedComponent> addTo, List<Post> entries) {
        // TODO: Choose random scrapbook entry
    }
}
