package com.example.mova.fragments.Personal;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.mova.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PersonalFeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonalFeedFragment extends Fragment {

    public PersonalFeedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment PersonalFeedFragment.
     */
    // TODO: Rename and change types and count of parameters
    public static PersonalFeedFragment newInstance(String param1, String param2) {
        PersonalFeedFragment fragment = new PersonalFeedFragment();
        Bundle args = new Bundle();
        // TODO: Add any args with keys set on the class
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // TODO: Set any parameter values if needed
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_personal_feed, container, false);
    }
}
