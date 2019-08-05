package com.example.mova.model;

import com.example.mova.utils.AsyncUtils;
import com.example.mova.utils.GoalUtils;
import com.example.mova.utils.Wrapper;
import com.parse.ParseClassName;
import com.parse.ParseQuery;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@ParseClassName("Action")
public class Action extends HashableParseObject {

    public static final String KEY_TASK = "task";
    public static final String KEY_IS_DONE = "isDone";
    public static final String KEY_IS_CONNECTED_PARENT = "isConnectedToParent";
    public static final String KEY_COMPLETED_AT = "completedAt";
    public static final String KEY_PARENT_GOAL = "parentGoal";
    public static final String KEY_PARENT_SHARED_ACTION = "parentSharedAction";
    public static final String KEY_PARENT_USER = "parentUser";

    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_MANUAL_CREATED_AT = "manualCreatedAt";

    public static final String KEY_ARCHIVED = "archived";
    public static final String KEY_ARCHIVE_AT = "archiveAt";

    public static final String KEY_RECURRENCE = "recurrence";
    public static final String KEY_RECURRENCE_ID = "recurrenceId";

    //Task

    public String getTask(){
        return getString(KEY_TASK);
    }

    public Action setTask(String task){
        put(KEY_TASK, task);
        return this;
    }

    //isDone

    public boolean getIsDone(){
        return getBoolean(KEY_IS_DONE);
    }

    public Action setIsDone(Boolean bool){
        put(KEY_IS_DONE, bool);
        // Set completion time if done
        if (bool) this.setCompletedAt(Calendar.getInstance().getTime());
        else      this.removeCompletedAt();
        return this;
    }

    public Action setDone(){
        put(KEY_IS_DONE, true);
        this.setCompletedAt(Calendar.getInstance().getTime());
        return this;
    }

    // Is Connected to Parent

    public Boolean getIsConnectedToParent(){
        return getBoolean(KEY_IS_CONNECTED_PARENT);
    }

    public Action setIsConnectedToParent(Boolean bool){
        put(KEY_IS_CONNECTED_PARENT, bool);
        return this;
    }

    //Completed At

    //Get date complete add
    public Date getCompletedAt() {
        return getDate(KEY_COMPLETED_AT);
    }

    public Action setCompletedAt(Date date) {
        put(KEY_COMPLETED_AT, date);
        return this;
    }

    public void removeCompletedAt() {
        put(KEY_COMPLETED_AT, JSONObject.NULL);
    }

    //Parent Goal
    public Goal getParentGoal(){
        return (Goal) getParseObject(KEY_PARENT_GOAL);
    }

    public Action setParentGoal(Goal goal){
        put(KEY_PARENT_GOAL, goal);
        return this;
    }

    //Parent Shared Action

    public SharedAction getParentSharedAction(){
        return (SharedAction) getParseObject(KEY_PARENT_SHARED_ACTION);
    }


    public Action setParentSharedAction(SharedAction sharedAction){
        put(KEY_PARENT_SHARED_ACTION, sharedAction);
        return this;
    }

    public Action setParentSharedAction(SharedAction sharedAction, AsyncUtils.ItemCallback<Action> callback){
        // does what updateWithRelation does but for this specific case

        put(KEY_PARENT_SHARED_ACTION, sharedAction);
        sharedAction.relChildActions.add(this, callback);
        return this;
    }

    //Parent User
    public User getParentUser(){
        return  (User) getParseUser(KEY_PARENT_USER);
    }

    public Action setParentUser(User user){
        put(KEY_PARENT_USER, user);
        return this;
    }

    // Created At (manual data since can't set created at directly on ParseObject);
    @Override
    public Date getCreatedAt() {
        Date manual = getDate(KEY_MANUAL_CREATED_AT);
        Date result = (manual != null) ? manual : super.getCreatedAt();
        return result;
    }

    public Action setCreatedAt(Date date) {
        put(KEY_MANUAL_CREATED_AT, date);
        return this;
    }

    // Archived
    public boolean isArchived() {
        return getBoolean(KEY_ARCHIVED);
    }

    public Action setArchived(boolean archived) {
        put(KEY_ARCHIVED, archived);

        if (getRecurrence() != null) {
            // TODO
        }

        return this;
    }

    // Auto Archive
    public Date getArchiveAt() {
        return getDate(KEY_ARCHIVE_AT);
    }

    public Action setArchiveAt(Date date) {
        put(KEY_ARCHIVE_AT, date);
        return this;
    }

    public Action removeAutoArchive() {
        put(KEY_ARCHIVE_AT, JSONObject.NULL);
        return this;
    }

    public boolean shouldAutoArchive() {
        Date archiveAt = getArchiveAt();
        Date now = new Date();
        return archiveAt != null && now.compareTo(archiveAt) >= 0;
    }

    // Recurrence
    public List<Recurrence> getRecurrence() {
        String recurrenceStr = getString(KEY_RECURRENCE);
        return Recurrence.parse(recurrenceStr);
    }

    public Action setRecurrence(Recurrence recurrence) {
        put(KEY_RECURRENCE, recurrence.toString());
        return this;
    }

    public Action setRecurrence(List<Recurrence> recurrence) {
        put(KEY_RECURRENCE, Recurrence.toString(recurrence));
        return this;
    }

    public Action addRecurrence(Recurrence recurrence) {
        String existingRecurrence = getString(KEY_RECURRENCE);
        String compounded = Recurrence.add(existingRecurrence, recurrence);
        put(KEY_RECURRENCE, compounded);
        return this;
    }

    public Action removeRecurrence()  {
        put(KEY_RECURRENCE, JSONObject.NULL);
        put(KEY_RECURRENCE_ID, JSONObject.NULL);
        return this;
    }

    public boolean isRecurring() {
        List<Recurrence> recurrence = getRecurrence();
        return recurrence.size() > 0;
    }

    /**
     * Determines whether or not the action's next recurrence(s) should be created and added to the DB.
     * @return The recurrences for which the action should be duplicated.
     */
    public void shouldAddRecur(AsyncUtils.TwoItemCallback<List<Recurrence>, Throwable> callback) {
        List<Recurrence> recurrence = getRecurrence();

        // Check for no recurrence or empty recurrence
        if (recurrence.size() == 0
            || recurrence.contains(Recurrence.makeEmpty())) callback.call(new ArrayList<>(), null);

        AsyncUtils.ItemCallback<List<Recurrence>> checkRecurrences = (xRecurrence) -> {
            List<Recurrence> result = new ArrayList<>();
            Date now = new Date();

            for (Recurrence r : xRecurrence) {
                // If the next relative date has already happened, then true
                Date nextDate = r.nextRelativeDate(getCreatedAt());
                if (now.compareTo(nextDate) >= 0) {
                    result.add(r);
                }
            }

            callback.call(result, null);
        };

        // Check for shared recurrence
        if (recurrence.contains(Recurrence.makeShared())) {
            getParentSharedAction().fetchIfNeededInBackground((object, e) -> {
                if (e != null) callback.call(new ArrayList<>(), e);
                else {
                    SharedAction sharedAction = (SharedAction) object;
                    List<Recurrence> sharedRecurrence = sharedAction.getRecurrence();
                    checkRecurrences.call(sharedRecurrence);
                }
            });
        } else {
            checkRecurrences.call(recurrence);
        }
    }

    /**
     * Creates and adds the next recurring action if the action is recurring.
     * Adds empty to the recurrence of the current action to deprecate it.
     * @return The new action. Null if no action was created.
     */
    public void addRecur(AsyncUtils.TwoItemCallback<List<Action>, Throwable> callback) {
        shouldAddRecur((toAdd, e) -> {
            // Set up output, counter for most recent recurrence
            List<Action> actions = new ArrayList<>();
            final Wrapper<Action> mostRecentAction = new Wrapper<>();
            mostRecentAction.item = this;

            if (toAdd.size() == 0) callback.call(actions, null);

            AsyncUtils.ItemCallback<Action> saveAction = (action) -> {
                if (action.getCreatedAt().compareTo(mostRecentAction.item.getCreatedAt()) > 0) {
                    mostRecentAction.item = action;
                }

                actions.add(action);
            };

            AsyncUtils.EmptyCallback finalSteps = () -> {
                // Deactivate all old recurrences relative to most recent recurrence
                addRecurrence(Recurrence.makeEmpty());
                for (Action action : actions) {
                    if (action.equals(mostRecentAction.item)) continue;
                    action.addRecurrence(Recurrence.makeEmpty());
                }

                callback.call(actions, null);
            };

            // If the action is connected to a SharedAction and its recurrence is determined by that SharedAction...
            if (toAdd.contains(Recurrence.makeShared())) {
                if (toAdd.size() > 1) throw new IllegalArgumentException("Actions must contain only one shared recurrence indicator.");

                // Save all shared
                getParentSharedAction().getAllMoreRecentRecurs((sharedActions, e1) -> {
                    if (e1 != null) callback.call(new ArrayList<>(), e1);
                    else {
                        // When ready, pull down all recurs from SharedActions to Actions
                        AsyncUtils.ItemCallback<List<SharedAction>> pullRecurs = (recurs) -> {
                            AsyncUtils.executeMany(
                                recurs.size(),
                                (i, cb) -> {
                                    SharedAction sA = recurs.get(i);
                                    Action action = pullDownSharedAction(sA, User.getCurrentUser());
                                    GoalUtils.saveActionOnSharedGoal(sA, action, sA.getGoal(), false, (e3) -> {
                                        if (e3 != null) {
                                            saveAction.call(action);
                                        }
                                        cb.call(e3);
                                    });
                                },
                                (e3) -> {
                                    if (e3 != null) callback.call(actions, e3);
                                    else            finalSteps.call();
                                }
                            );
                        };

                        // If SharedAction has not yet recurred, force it to recur, and pull the data
                        if (sharedActions.size() == 0) {
                            getParentSharedAction().addRecur((recurs, e2) -> {
                                if (e2 != null) callback.call(actions, e2);
                                pullRecurs.call(recurs);
                            });
                        } else {
                            pullRecurs.call(sharedActions);
                        }
                    }
                });
            } else { // Otherwise, work with each recurrence independent of shared functionality
                AsyncUtils.executeMany(
                    toAdd.size(),
                    (i, cb) -> {
                        Recurrence r = toAdd.get(i);
                        Date manual = r.nextRelativeDate(getCreatedAt());
                        Action action = makeDatedRecurAction(manual);
                        action.saveInBackground((e1) -> {
                            if (e1 == null) {
                                saveAction.call(action);
                            }
                            cb.call(e1);
                        });
                    },
                    (e1) -> {
                        if (e1 != null) callback.call(actions, e1);
                        else            finalSteps.call();
                    }
                );
            }
        });
    }

    private Action makeDatedRecurAction(Date manual) {
        Action action = new Action()
                .setCreatedAt(manual)
                .setTask(getTask())
                .setParentGoal(getParentGoal())
                .setIsConnectedToParent(getIsConnectedToParent())
                .setRecurrenceId(getRecurrenceId())
                .setRecurrence(getRecurrence());

        if (getIsConnectedToParent()) action.setParentSharedAction(getParentSharedAction());

        return action;
    }

    /**
     * Creates an Action based on the data in a SharedAction.
     * @param sharedAction The SharedAction providing the data.
     * @return The Action.
     */
    public static Action pullDownSharedAction(SharedAction sharedAction, User user) {
        Action action = new Action()
                .setParentUser(user)
                .setTask(sharedAction.getTask())
                .setParentSharedAction(sharedAction)
                .setParentGoal(sharedAction.getGoal())
                .setIsConnectedToParent(true)
                .setCreatedAt(sharedAction.getCreatedAt())
                .setIsDone(false);

        if (sharedAction.isRecurring()) {
            action.setRecurrence(Recurrence.makeShared());
            if (sharedAction.getRecurrence().contains(Recurrence.makeEmpty())) {
                action.addRecurrence(Recurrence.makeEmpty());
            }
        }

        return action;
    }

    // Recurrence ID
    // TODO: Determine what methods would be more helpful here, and put closer bounds in place for setting these
    public String getRecurrenceId() {
        return getString(KEY_RECURRENCE_ID);
    }

    public Action setRecurrenceId(String id) {
        put(KEY_RECURRENCE_ID, id);
        return this;
    }

    /**
     * Fetches the most recent copy of a recurring action.
     * Provides null if the action is not recurring or no action could be found.
     */
    public void getMostRecentRecur(AsyncUtils.TwoItemCallback<SharedAction, Throwable> cb) {
        if (!isRecurring()) cb.call(null, null);

        String id = getRecurrenceId();
        ParseQuery<SharedAction> query = new ParseQuery<>(SharedAction.class);

        query.whereEqualTo(KEY_RECURRENCE_ID, id);
        // FIXME: Will this query work?
        query.orderByDescending(KEY_MANUAL_CREATED_AT);
        query.addDescendingOrder(KEY_CREATED_AT);

        query.setLimit(1);
        query.findInBackground((sharedActions, e) -> {
            SharedAction item = (sharedActions != null && sharedActions.size() > 0) ? sharedActions.get(0) : null;
            cb.call(item, e);
        });
    }
}
