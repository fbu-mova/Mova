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
 * Use the {@link PersonalFeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonalFeedFragment extends Fragment {

    public static String KEY_SWITCH_FRAGMENT = "switchFragment";
    private AsyncUtils.ItemCallback<PersonalFragment.FragmentName> switchFragment;

    public PersonalFeedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment PersonalFeedFragment.
     */
    public static PersonalFeedFragment newInstance(AsyncUtils.ItemCallback<PersonalFragment.FragmentName> switchFragment) {
        PersonalFeedFragment fragment = new PersonalFeedFragment();
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
        return inflater.inflate(R.layout.fragment_personal_feed, container, false);
    }
}
