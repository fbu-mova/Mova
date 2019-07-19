package com.example.mova.fragments.Personal;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.mova.R;
import com.example.mova.fragments.PersonalFragment;
import com.example.mova.utils.AsyncUtils;

import org.parceler.Parcels;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GoalsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GoalsFragment extends Fragment {

    public static String KEY_SWITCH_FRAGMENT = "switchFragment";
    private AsyncUtils.ItemCallback<PersonalFragment.FragmentName> switchFragment;

    public GoalsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment GoalsFragment.
     */
    public static GoalsFragment newInstance(AsyncUtils.ItemCallback<PersonalFragment.FragmentName> switchFragment) {
        GoalsFragment fragment = new GoalsFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_SWITCH_FRAGMENT, Parcels.wrap(switchFragment)); // FIXME: Will this work?
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            switchFragment = Parcels.unwrap(getArguments().getParcelable(KEY_SWITCH_FRAGMENT));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_goals, container, false);
    }
}
