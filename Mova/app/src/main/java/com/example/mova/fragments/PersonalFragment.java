package com.example.mova.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.mova.R;
import com.example.mova.fragments.Personal.GoalsFragment;
import com.example.mova.fragments.Personal.JournalFragment;
import com.example.mova.fragments.Personal.PersonalFeedFragment;
import com.example.mova.fragments.Personal.ProfileFragment;
import com.example.mova.fragments.Personal.ProgressFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PersonalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonalFragment extends Fragment {

    @BindView(R.id.bottom_navigation_personal) BottomNavigationView bottomNavigationView;

    public PersonalFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment PersonalFragment.
     */
    public static PersonalFragment newInstance() {
        PersonalFragment fragment = new PersonalFragment();
        Bundle args = new Bundle();
        // TODO: Add any parameters necessary
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // TODO: Add any parameters necessary
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // implement fragment manager
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                FragmentName name = FragmentName.fromMenuItem(menuItem.getItemId());
                switchFragment(name);
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_personal_feed);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_personal, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    public void switchFragment(FragmentName name) {
        final FragmentManager fragmentManager = getChildFragmentManager();
        Fragment fragment;
        switch (name) {
            case Progress:
                Toast.makeText(getContext(), "Switches to progress", Toast.LENGTH_SHORT).show();
                fragment = ProgressFragment.newInstance();
                break;
            case Journal:
                Toast.makeText(getContext(), "Switches to journal", Toast.LENGTH_SHORT).show();
                fragment = JournalFragment.newInstance();
                break;
            case Feed:
                Toast.makeText(getContext(), "Switches to feed", Toast.LENGTH_SHORT).show();
                fragment = PersonalFeedFragment.newInstance();
                break;
            case Goals:
                Toast.makeText(getContext(), "Switches to goals", Toast.LENGTH_SHORT).show();
                fragment = GoalsFragment.newInstance();
                break;
            case Profile:
                Toast.makeText(getContext(), "Switches to profile", Toast.LENGTH_SHORT).show();
                fragment = ProfileFragment.newInstance();
                break;
            default:
                return;
        }
        fragmentManager.popBackStackImmediate(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentManager.beginTransaction().replace(R.id.flPersonalContainer, fragment).commit();

    }

    public enum FragmentName {
        Progress,
        Journal,
        Feed,
        Goals,
        Profile;

        public static FragmentName fromMenuItem(int menuItemId) {
            switch (menuItemId) {
                case R.id.action_progress:
                    return Progress;
                case R.id.action_journal:
                    return Journal;
                case R.id.action_goals:
                    return Goals;
                case R.id.action_profile:
                    return Profile;
                case R.id.action_personal_feed:
                default:
                    return Feed;
            }
        }
    }
}
