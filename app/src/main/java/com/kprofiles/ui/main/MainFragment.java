package com.kprofiles.ui.main;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.kprofiles.R;
import com.topjohnwu.superuser.Shell;

public class MainFragment extends Fragment {

    private Context context;
    private Activity activity;
    private SharedPreferences sharedpreferences;
    private int pos;

    // Exported node path's
    private static final String KP_AUTO_NODE = "/sys/module/kprofiles/parameters/auto_kprofiles";
    private static final String KP_MODE_NODE = "/sys/module/kprofiles/parameters/mode";

    // Exported key's
    private static final String AUTO_ENABLE = "Y";
    private static final String AUTO_DISABLE = "N";

    // Exported modes
    private static final String DISABLED = "0";
    private static final String BATTERY = "1";
    private static final String BALANCED = "2";
    private static final String PERFORMANCE = "3";

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getContext();
        this.activity = getActivity();

        // Prompt user to get su
        Shell.cmd("echo 0").exec();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View promptView = inflater.inflate(R.layout.fragment_main, container, false);

        setHasOptionsMenu(true);
        sharedpreferences = activity.getSharedPreferences("com.kprofiles", Context.MODE_PRIVATE);

        // Define all switches, spinners and etc here
        final SwitchMaterial kprofile_auto = promptView.findViewById(R.id.kprofile_auto);
        final AppCompatSpinner kprofile_mode = promptView.findViewById(R.id.kprofile_mode);
        final MaterialButton mode_apply = promptView.findViewById(R.id.mode_apply);



        // Check weather sharedpref's have values and set them as they are. Otherwise disable everything to stock
        //// KP Auto ( doesn't reapply actual function but just moves the switch where user defined it to be )
        final boolean kpauto = sharedpreferences.getBoolean("kp_auto", false);
        if (kpauto == true) {
            kprofile_auto.setChecked(true);
        } else {
            kprofile_auto.setChecked(false);
        }

        //// KP Mode
        if (sharedpreferences.getString("kp_mode", "None").equals("None")) {
            kprofile_mode.setSelection(0, true);
        } else if (sharedpreferences.getString("kp_mode", "Disabled").equals("Disabled")) {
            kprofile_mode.setSelection(1, true);
        } else if (sharedpreferences.getString("kp_mode", "Battery").equals("Battery")) {
            kprofile_mode.setSelection(2, true);
        } else if (sharedpreferences.getString("kp_mode", "Balanced").equals("Balanced")) {
            kprofile_mode.setSelection(3, true);
        } else if (sharedpreferences.getString("kp_mode", "Performance").equals("Performance")) {
            kprofile_mode.setSelection(4, true);
        }

        // Auto Kprofile switch
        kprofile_auto.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(getActivity(),
                        "Auto KP enabled", Toast.LENGTH_SHORT).show();
                sharedpreferences.edit().putBoolean("kp_auto", true).apply();
                Shell.cmd("echo " + AUTO_ENABLE + " > " + KP_AUTO_NODE).exec();

            } else {
                Toast.makeText(getActivity(),
                        "Auto KP disabled", Toast.LENGTH_SHORT).show();
                sharedpreferences.edit().putBoolean("kp_auto", false).apply();
                Shell.cmd("echo " + AUTO_DISABLE + " > " + KP_AUTO_NODE).exec();
            }
        });

        // Kprofile mode ( spinner selection )
        kprofile_mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView adapter, View v, int i, long lng) {
                Log.d("kpm_mode selection: ", kprofile_mode.getSelectedItem().toString());
                if (kprofile_mode.getSelectedItem().toString().equals("None")) {
                    sharedpreferences.edit().remove("kp_mode").apply();
                    sharedpreferences.edit().putString("kp_mode", "None").apply();
                    return;
                } else if (kprofile_mode.getSelectedItem().toString().equals("Disabled")) {
                    sharedpreferences.edit().remove("kp_mode").apply();
                    sharedpreferences.edit().putString("kp_mode", "Disabled").apply();
                    return;
                } else if (kprofile_mode.getSelectedItem().toString().equals("Battery")) {
                    sharedpreferences.edit().remove("kp_mode").apply();
                    sharedpreferences.edit().putString("kp_mode", "Battery").apply();
                    return;
                } else if (kprofile_mode.getSelectedItem().toString().equals("Balanced")) {
                    sharedpreferences.edit().remove("kp_mode").apply();
                    sharedpreferences.edit().putString("kp_mode", "Balanced").apply();
                    return;
                } else if (kprofile_mode.getSelectedItem().toString().equals("Performance")) {
                    sharedpreferences.edit().remove("kp_mode").apply();
                    sharedpreferences.edit().putString("kp_mode", "Performance").apply();
                    return;
                } else {
                    Toast.makeText(getActivity(),
                            R.string.kp_performance, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            @Override
            public void onNothingSelected(AdapterView arg0) {
                Toast.makeText(activity, "Nothing selected", Toast.LENGTH_SHORT).show();

            }
        });

        // KProfile mode ( apply ) also make sure that selected value matches before apply
        mode_apply.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (sharedpreferences.getString("kp_mode", "None").equals("None")) {
                    Log.d("This is:", "None");
                } else if (sharedpreferences.getString("kp_mode", "Disabled").equals("Disabled")) {
                    Log.d("This is:", "Disabled");
                    Shell.cmd("echo " + DISABLED + " > " + KP_MODE_NODE).exec();
                } else if (sharedpreferences.getString("kp_mode", "Battery").equals("Battery")) {
                    Log.d("This is:", "Battery");
                    Shell.cmd("echo 0 " + BATTERY + " > " + KP_MODE_NODE).exec();
                } else if (sharedpreferences.getString("kp_mode", "Balanced").equals("Balanced")) {
                    Log.d("This is:", "Balanced");
                    Shell.cmd("echo " + BALANCED + " > " + KP_MODE_NODE).exec();
                } else if (sharedpreferences.getString("kp_mode", "Performance").equals("Performance")) {
                    Log.d("This is:", "Performance");
                    Shell.cmd("echo " + PERFORMANCE + " > " + KP_MODE_NODE).exec();
                }
            }
        });

        return promptView;
    }

    public static void saveString(Context context, String key, String value) {
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        sharedPrefs.edit().putString(key, value).apply();
    }

    public static String getString(Context context, String key, String defaultValue) {
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharedPrefs.getString(key, defaultValue);
    }
}