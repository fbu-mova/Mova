package com.example.mova.feed;

import android.util.Log;

import androidx.recyclerview.widget.SortedList;

import com.example.mova.components.GoalCheckInComponent;
import com.example.mova.components.JournalMemoryComponent;
import com.example.mova.components.JournalPromptComponent;
import com.example.mova.components.TomorrowFocusPromptComponent;
import com.example.mova.model.Goal;
import com.example.mova.model.Post;
import com.example.mova.model.User;
import com.example.mova.utils.AsyncUtils;
import com.example.mova.utils.GoalUtils;
import com.example.mova.utils.TimeUtils;
import com.example.mova.utils.Wrapper;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
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
        AsyncUtils.waterfall(asyncActions, callback);
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
                        final Wrapper<PrioritizedComponent> pCard = new Wrapper<>();
                        JournalPromptComponent card = new JournalPromptComponent((entry) -> {
                            addTo.remove(pCard.item);
                        });
                        pCard.item = new PrioritizedComponent(card, 100);
                        addTo.add(pCard.item);
                    }
                    cb.call(null);
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

    // TODO: Scale to more than one of each goal if desired
    // TODO: Add conditions to determine whether to display best, worst, or both (and make these metrics not relative)
    // TODO: Maybe add card for adding goals if user doesn't yet have any goals
    // TODO: Make message dependent on time of day
    // TODO: Improve messages generally, maybe randomize or in some other way vary
    protected void makeGoalCheckIns(SortedList<PrioritizedComponent> addTo, List<Goal> goals, AsyncUtils.ItemCallback<Throwable> callback) {
        // Get goals sorted by progress
        GoalUtils goalUtils = new GoalUtils();
        goalUtils.sortGoals(goals, 7, ((User) User.getCurrentUser()), (TreeSet<Prioritized<Goal>> pGoals) -> {
            // Choose top and bottom goals based on quantities in config
            if (pGoals.size() == 1) {
                Prioritized<Goal> goal = pGoals.first();
                GoalCheckInComponent card = new GoalCheckInComponent(goal.item, "Great work so far!");
                card.loadData((e) -> {
                    if (e == null) {
                        addTo.add(new PrioritizedComponent(card, 50));
                    }
                    callback.call(e);
                });
            } else if (pGoals.size() > 0) {
                Prioritized<Goal> worstGoal = pGoals.first(), bestGoal = pGoals.last();
                GoalCheckInComponent worstCard = new GoalCheckInComponent(worstGoal.item, "Keep up the hard work--give " + worstGoal.item.getTitle() + " a bit more of your time if you can.");
                GoalCheckInComponent bestCard = new GoalCheckInComponent(bestGoal.item, "You've been doing great lately--enjoy what's left for today!");
                bestCard.loadData((e) -> {
                    if (e == null) {
                        addTo.add(new PrioritizedComponent(bestCard, 50));
                        worstCard.loadData((e1) -> {
                            if (e1 == null) {
                                addTo.add(new PrioritizedComponent(worstCard, 49));
                            }
                            callback.call(e1);
                        });
                    } else {
                        callback.call(e);
                    }
                });
            }
        });
    }

    protected void makeJournalMemories(SortedList<PrioritizedComponent> addTo, List<Post> entries) {
        // TODO: Randomize further to support random number of cards as opposed to just one
        Random random = new Random();
        int index = random.nextInt(entries.size());
        JournalMemoryComponent card = new JournalMemoryComponent(entries.get(index));
        addTo.add(new PrioritizedComponent(card, 0));
    }

    protected void makeScrapbookMemories(SortedList<PrioritizedComponent> addTo, List<Post> entries) {
        // TODO: Choose random scrapbook entry
    }
}
