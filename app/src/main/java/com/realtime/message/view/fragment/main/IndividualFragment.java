package com.realtime.message.view.fragment.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.realtime.message.databinding.FragmentIndividualBinding;
import com.realtime.message.model.User;
import com.realtime.message.viewmodel.IndividualViewModel;

public class IndividualFragment extends Fragment {
    FragmentIndividualBinding individualBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        individualBinding = FragmentIndividualBinding.inflate(inflater, container, false);
        IndividualViewModel viewModel = new IndividualViewModel(getActivity());
        individualBinding.setIndividualViewModel(viewModel);
        individualBinding.getIndividualViewModel().getCurrentUser();
        individualBinding.getIndividualViewModel().getUserLiveData().observe(getActivity(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                individualBinding.setUser(user);
            }
        });


        return individualBinding.getRoot();
    }
}
