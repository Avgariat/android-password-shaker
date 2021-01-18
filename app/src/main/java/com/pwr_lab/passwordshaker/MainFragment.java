package com.pwr_lab.passwordshaker;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Main fragment - show generated pass
 */
public class MainFragment extends Fragment {
    private static final String ARG_PASSWORD = "password";

    private String password;
    private TextView passwordView;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance(String password) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PASSWORD, password);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            password = getArguments().getString(ARG_PASSWORD);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View thisView = inflater.inflate(R.layout.fragment_main, container, false);
        passwordView = (TextView) thisView.findViewById(R.id.f_main_password);
        passwordView.setText(password);
        return thisView;
    }

    public void setPassword(String password) {
        this.password = password;
        if (passwordView != null) {
            passwordView.setText(password);
        }
    }
}