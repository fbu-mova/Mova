package com.example.mova.model;

import com.example.mova.components.Component;
import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Media")
public class Media extends HashableParseObject {

    public static final String KEY_TYPE = "type";
    public static final String KEY_PARENT = "parent";
    public static final String KEY_POST = "contentPost";
    public static final String KEY_GROUP = "contentGroup";
    public static final String KEY_EVENT = "contentEvent";
    public static final String KEY_GOAL = "contentGoal";
    public static final String KEY_ACTION = "contentAction";
    public static final String KEY_TEXT = "contentText";

    //Type
    public ContentType getType(){
        int type = getInt(KEY_TYPE);
        return ContentType.fromValue(type);
    }

    public Media setType(ContentType type){
        put(KEY_TYPE, type);
        return this;
    }

    //Parent
    public User getParent(){
        return (User) getParseUser(KEY_PARENT);
    }

    public Media setParent(User user){
        put(KEY_PARENT, user);
        return this;
    }

    //ContentText
    public String getContentText() {
        return getString(KEY_TEXT);
    }

    public Media setContentText(String text) {
        put(KEY_TEXT, text);
        return this;
    }

    //ContentPost
    public Post getContentPost(){
        return (Post) getParseObject(KEY_POST);
    }

    public void setContentPost(Post post){
        put(KEY_POST, post);
    }

    //ContentGroup
    public Group getContentGroup(){
        return (Group) getParseObject(KEY_GROUP);
    }

    public void setContentGroup(Group group){
        put(KEY_GROUP,group);
    }

    //ContentEvent
    public Event getContentEvent(){
        return (Event) getParseObject(KEY_EVENT);
    }

    public void setContentEvent(Event event){
        put(KEY_EVENT, event);
    }

    //ContentGoal
    public Goal getContentGoal(){
        return (Goal) getParseObject(KEY_GOAL);
    }

    public void setContentGoal(Goal goal){
        put(KEY_GOAL, goal);
    }

    //ContentAction
    public Action getContentAction(){
        return (Action) getParseObject(KEY_ACTION);
    }

    public void setContentAction(Action action){
        put(KEY_ACTION, action);
    }

    // Handle content item regardless of type
    public Object getContent() {
        switch (getType()) {
            case Post:
                return getContentPost();
            case Group:
                return getContentGroup();
            case Event:
                return getContentEvent();
            case Goal:
                return getContentGoal();
            case Action:
                return getContentAction();
            case Text:
            default:
                return getContentText();
        }
    }

    public void setContent(Object content) {
        if (content instanceof Post) {
            setType(ContentType.Post);
            setContentPost((Post) content);
        } else if (content instanceof Group) {
            setType(ContentType.Group);
            setContentGroup((Group) content);
        } else if (content instanceof Event) {
            setType(ContentType.Event);
            setContentEvent((Event) content);
        } else if (content instanceof Goal) {
            setType(ContentType.Goal);
            setContentGoal((Goal) content);
        } else if (content instanceof Action) {
            setType(ContentType.Action);
            setContentAction((Action) content);
        } else {
            setType(ContentType.Text);
            setContentText(content.toString());
        }
    }

    public Class getObjectType() {
        switch (getType()) {
            case Post:
                return Post.class;
            case Group:
                return Group.class;
            case Event:
                return Event.class;
            case Goal:
                return Goal.class;
            case Action:
                return Action.class;
            case Text:
            default:
                return String.class;
        }
    }

    public Component makeComponent() {
        // TODO
        return null;
    }

    public static enum ContentType {
        Text(0),
        Post(1),
        Group(2),
        Event(3),
        Goal(4),
        Action(5);

        private final int value;
        private ContentType(int value) {
            this.value = value;
        }

        public static ContentType fromValue(int value) {
            switch (value) {
                case 1:
                    return Post;
                case 2:
                    return Group;
                case 3:
                    return Event;
                case 4:
                    return Goal;
                case 5:
                    return Action;
                case 6:
                default:
                    return Text;
            }
        }

        public String toKey() {
            switch (this) {
                case Post:
                    return KEY_POST;
                case Group:
                    return KEY_GROUP;
                case Event:
                    return KEY_EVENT;
                case Goal:
                    return KEY_GOAL;
                case Action:
                    return KEY_ACTION;
                case Text:
                default:
                    return KEY_TEXT;
            }
        }

        public int getValue() {
            return value;
        }
    }
}
