package com.example.mova.fragments.Personal;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mova.R;
import com.example.mova.activities.LoginActivity;
import com.example.mova.fragments.PersonalFragment;
import com.example.mova.utils.AsyncUtils;
import com.parse.ParseUser;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    public static String KEY_SWITCH_FRAGMENT = "switchFragment";
    private AsyncUtils.ItemCallback<PersonalFragment.FragmentName> switchFragment;

    @BindView(R.id.btnLogout) protected Button btnLogout;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ProfileFragment.
     */
    public static ProfileFragment newInstance(AsyncUtils.ItemCallback<PersonalFragment.FragmentName> switchFragment) {
        ProfileFragment fragment = new ProfileFragment();
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
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser currentUser = ParseUser.getCurrentUser();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.putExtra("username",currentUser.getUsername());
                currentUser.logOut();
                startActivity(intent);
            }
        });

    }
}
