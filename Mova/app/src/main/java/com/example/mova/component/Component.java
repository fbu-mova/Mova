package com.example.mova.component;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mova.activities.DelegatedResultActivity;

/**
 * Bundles a view's logic, state, and binding.
 */
public abstract class Component {

    private DelegatedResultActivity activity;
    private boolean isActive = false;

    protected boolean isActive() {
        return isActive;
    }

    protected DelegatedResultActivity getActivity() {
        return activity;
    }

    /**
     * Returns the component's most recently used ViewHolder.
     * @return The ViewHolder.
     */
    public abstract ViewHolder getViewHolder();

    /**
     * Returns a string representation of the component.
     * Intended for use in ComponentManager and other component keying systems.
     * @return A string representation of the component.
     */
    public abstract String getName();

    /**
     * Sets the manager of this component to enable component swapping.
     */
    public abstract void setManager(ComponentManager manager);

    /**
     * Serves as a factory for creating correctly typed inflaters.
     * Should return the same inflater otherwise statically created.
     * @return The correctly typed inflater for the component.
     */
    public abstract Inflater makeInflater();

    /**
     * Runs any activity-related setup required for a component to be rendered.
     * @param activity The activity in which to set up the component.
     */
    public void launch(DelegatedResultActivity activity) {
        // Destroy component on active activity if activities differ
        if (this.activity != null && !this.activity.equals(activity)) {
            onDestroy();
            isActive = false;
        }
        // If not yet active, launch component on activity
        if (!isActive) {
            this.activity = activity;
            onLaunch();
            isActive = true;
        }
    }

    /**
     * Fires when the component is launched.
     * Should perform any activity-related setup required for render.
     */
    protected abstract void onLaunch();

    /**
     * Renders any relevant data or events to the component's ViewHolder.
     * If the component has not yet been launched, launches it.
     * @param holder The ViewHolder with which to render the component.
     */
    public void render(DelegatedResultActivity activity, ViewHolder holder) {
        launch(activity);
        onRender(holder);
    }

    /**
     * Fires when teh component should be rendered.
     * Should render any updates to the provided ViewHolder.
     * @param holder The ViewHolder with which to render the component.
     */
    protected abstract void onRender(ViewHolder holder);

    /**
     * Destroys the component, performing any necessary cleanup in the process.
     */
    public void destroy() {
        onDestroy();
        activity = null;
    }

    /**
     * Fires when the component should be destroyed before deactivation.
     * Should perform any cleanup required on the resources the component used while active.
     */
    protected abstract void onDestroy();

    /**
     * The Component's ViewHolder, analogous to a RecyclerView's ViewHolder.
     * Should not perform any logic related to a component's state--the component must handle its
     * own state, with ViewHolder serving only as a wrapper of views.
     */
    public static abstract class ViewHolder extends RecyclerView.ViewHolder {
        protected View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
        }

        /**
         * Returns the View created when making the ViewHolder.
         * @return The View. Null if not yet inflated.
         */
        public View getView() {
            return view;
        }
    }

    /**
     * Responsible for creating all elements necessary for the creation of a component.
     * If any logic is required to determine which kind of layout to use, etc., it should be done
     * here, ideally in a constructor or factory method. As a result, classes wrapping generic
     * components should request inflater factories (see adapters for an example).
     */
    public static abstract class Inflater {
        /**
         * Inflates the component's layout into the given activity.
         * @param activity The Activity into which to inflate the component.
         * @param parent The ViewGroup into which to inflate the component.
         */
        public abstract ViewHolder inflate(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot);
    }

    /**
     * Checks whether the provided holder is of the correct ViewHolder class.
     * If not, throws an exception.
     * @param holder The ViewHolder to check.
     * @param klass  The correct ViewHolder class.
     * @throws ClassCastException Throws if the holder is not of the correct class.
     */
    public static void checkViewHolderClass(ViewHolder holder, Class klass) throws ClassCastException {
        Class holderClass = holder.getClass();
        while (holderClass != klass) {
            if (holderClass == Component.class
                || holderClass == Object.class
                || holderClass == null) {
                throw new ClassCastException("Provided ViewHolder is of invalid type. Expected " + klass.getCanonicalName() + ", received " + holder.getClass().getCanonicalName() + ".");
            }
            holderClass = holderClass.getSuperclass();
        }
    }
}
