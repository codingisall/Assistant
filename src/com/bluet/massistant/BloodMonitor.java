package com.bluet.massistant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BloodMonitor extends BaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.blood_monitor, null, false);
        getActivity().getWindow().setTitle("血液回收机");
        return view;
    }

}