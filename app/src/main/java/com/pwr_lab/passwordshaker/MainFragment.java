package com.pwr_lab.passwordshaker;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Main fragment - show generated pass
 */
public class MainFragment extends Fragment implements View.OnClickListener {
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
        passwordView = thisView.findViewById(R.id.f_main_password);
        passwordView.setText(password);
        passwordView.setOnClickListener(this);
        return thisView;
    }

    public void setPassword(String password) {
        this.password = password;
        // basically if onCreateView has not been called yet
        if (passwordView != null) {
            passwordView.setText(password);
        }
    }

    @Override
    public void onClick(View v) {
        if (password == null || password.length() == 0) return;

        FragmentActivity activity = getActivity();
        if (activity == null) return;

        ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);

        ClipData clipData = ClipData.newPlainText("new-password", password);

        clipboard.setPrimaryClip(clipData);
        Toast.makeText(getActivity(), "Password copied to clipboard!", Toast.LENGTH_SHORT).show();
    }
}