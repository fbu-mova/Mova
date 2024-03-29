package com.example.mova.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.mova.R;
import com.example.mova.components.EventThumbnailComponent;
import com.example.mova.components.GroupThumbnailComponent;
import com.example.mova.components.PostComponent;
import com.example.mova.components.ProfileFriendComponent;
import com.example.mova.components.ProfileGroupComponent;
import com.example.mova.fragments.Social.EventsFragment;
import com.example.mova.fragments.Social.ExploreFragment;
import com.example.mova.fragments.Social.GroupsFragment;
import com.example.mova.fragments.Social.ScrapbookFragment;
import com.example.mova.fragments.Social.SocialFeedFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SocialFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SocialFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SocialFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    @BindView(R.id.bottom_navigation_social)
    BottomNavigationView bottomNavigationView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SocialFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SocialFragment.
     */
    // TODO: Rename and change types and count of parameters
    public static SocialFragment newInstance(String param1, String param2) {
        SocialFragment fragment = new SocialFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_social, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final FragmentManager fragmentManager = getChildFragmentManager();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment;
                switch (menuItem.getItemId()){
                    case R.id.action_scrapbook:
                        //Toast.makeText(getContext(), "Switched to scrapbook", Toast.LENGTH_SHORT).show();
                        fragment = new ScrapbookFragment();
                        break;
                    case R.id.action_events:
                        //Toast.makeText(getContext(), "Switched to events", Toast.LENGTH_SHORT).show();
                        fragment = new EventsFragment();
                        break;
                    case R.id.action_social_feed:
                        //Toast.makeText(getContext(), "Switched to social feed", Toast.LENGTH_SHORT).show();
                        fragment = new SocialFeedFragment();
                        break;
                    case R.id.action_groups:
                        //Toast.makeText(getContext(), "Switched to groups", Toast.LENGTH_SHORT).show();
                        fragment = new GroupsFragment();
                        break;
                    case R.id.action_explore:
                        //Toast.makeText(getContext(), "Switched to scrapbook", Toast.LENGTH_SHORT).show();
                        fragment = new ExploreFragment();
                        break;
                     default:
                         return true;
                }
                fragmentManager.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.flSocialContainer, fragment)
                        .commit();
                //Todo fix manager is already executing a transaction
                try {
                    if (ProfileFriendComponent.manager != null) {
                        ProfileFriendComponent.manager.popBackStack(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                    if (ProfileGroupComponent.manager != null) {
                        ProfileGroupComponent.manager.popBackStack(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                    if (GroupThumbnailComponent.manager != null) {
                        GroupThumbnailComponent.manager.popBackStack(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                    if (EventThumbnailComponent.manager != null) {
                        EventThumbnailComponent.manager.popBackStack(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                    if (PostComponent.manager != null){
                        PostComponent.manager.popBackStack(0,FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                    if (SocialFeedFragment.manager != null) {
                        SocialFeedFragment.manager.popBackStack(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                    if (GroupsFragment.manager != null) {
                        GroupsFragment.manager.popBackStack(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                    if (EventsFragment.manager != null) {
                        EventsFragment.manager.popBackStack(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                } catch (Exception e) {}
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_social_feed);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
