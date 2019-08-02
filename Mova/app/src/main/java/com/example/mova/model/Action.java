package com.example.mova.model;

import com.example.mova.utils.AsyncUtils;
import com.example.mova.utils.TimeUtils;
import com.parse.ParseClassName;

import org.json.JSONObject;

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
        return this;
    }

    // Archive At
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

    // Recurrence ID
    // TODO: Determine what methods would be more helpful here, and put closer bounds in place for setting these
    public String getRecurrenceId() {
        return getString(KEY_RECURRENCE_ID);
    }

    public Action setRecurrenceId(String id) {
        put(KEY_RECURRENCE_ID, id);
        return this;
    }
}
