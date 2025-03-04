package com.garethevans.church.opensongtablet.preferences;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.garethevans.church.opensongtablet.R;
import com.garethevans.church.opensongtablet.databinding.SettingsProfilesBinding;
import com.garethevans.church.opensongtablet.interfaces.MainActivityInterface;

public class ProfileFragment extends Fragment {

    private SettingsProfilesBinding myView;
    private MainActivityInterface mainActivityInterface;
    private ActivityResultLauncher<Intent> activityLoadResultLauncher, activitySaveResultLauncher;
    private String profile_string="", website_profiles_string="";
    private String webAddress;

    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.updateToolbar(profile_string);
        mainActivityInterface.updateToolbarHelp(webAddress);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivityInterface = (MainActivityInterface) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = SettingsProfilesBinding.inflate(inflater,container,false);

        prepareStrings();
        webAddress = website_profiles_string;

        // Setup helpers
        setupHelpers();

        // Initialise launcher
        initialiseLauncher();

        // Setup listeners
        setupListeners();

        return myView.getRoot();
    }

    private void prepareStrings() {
        if (getContext()!=null) {
            profile_string = getString(R.string.profile);
            website_profiles_string = getString(R.string.website_profiles);
        }
    }

    private void setupHelpers() {
        mainActivityInterface.registerFragment(this,"ProfileFragment");
    }

    private void initialiseLauncher() {
        activityLoadResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> doLoadSave(result,"load"));
        activitySaveResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> doLoadSave(result,"save"));
    }

    private void doLoadSave(ActivityResult result, String which) {
        boolean success = false;
        String extrainfo = "";
        if (result.getResultCode()==Activity.RESULT_OK) {
            Intent intent = result.getData();
            if (intent==null) {
                extrainfo += " (intent null) ";
            } else if (intent.getData()==null) {
                extrainfo += " (intent.getData() null) ";
            }
            if (intent!=null && intent.getData()!=null) {
                if (which.equals("load")) {
                    success = mainActivityInterface.getProfileActions().loadProfile(intent.getData());
                } else {
                    success = mainActivityInterface.getProfileActions().saveProfile(intent.getData());
                }
            }
        } else {
            extrainfo += " (Wrong result code:"+result.getResultCode()+") ";
        }
        if (success && getContext()!=null) {
            mainActivityInterface.getShowToast().doIt(getString(R.string.success));
        } else if (getContext()!=null){
            mainActivityInterface.getShowToast().doIt(getString(R.string.error)+extrainfo);
        }
    }

    private void setupListeners() {
        myView.loadButton.setOnClickListener(v -> loadProfile());
        myView.saveButton.setOnClickListener(v -> saveProfile());
        myView.resetButton.setOnClickListener(v -> resetPreferences());
    }

    private void loadProfile() {
        // Open the file picker and when the user has picked a file, deal with it
        Intent loadIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        Uri uri = mainActivityInterface.getStorageAccess().
                getUriForItem("Profiles","",null);
        loadIntent.setDataAndType(uri,"application/xml");
        String [] mimeTypes = {"application/*", "application/xml", "text/xml"};
        loadIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        loadIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        loadIntent.putExtra("android.provider.extra.INITIAL_URI", uri);
        loadIntent.putExtra("android.content.extra.SHOW_ADVANCED", true);
        activityLoadResultLauncher.launch(loadIntent);
    }

    private void saveProfile() {
        // Open the file picker and when the user has picked a file, deal with it
        Intent saveIntent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        Uri uri = mainActivityInterface.getStorageAccess().getUriForItem("Profiles","",null);
        saveIntent.setDataAndType(uri,"application/xml");
        saveIntent.putExtra("android.provider.extra.INITIAL_URI", uri);
        saveIntent.putExtra("android.content.extra.SHOW_ADVANCED", true);
        saveIntent.putExtra(Intent.EXTRA_TITLE,"MyProfile");
        activitySaveResultLauncher.launch(saveIntent);
    }

    private void resetPreferences() {
        // Reset the preferences and start again
        if (getContext()!=null) {
            mainActivityInterface.getProfileActions().resetPreferences();
            NavOptions navOptions = new NavOptions.Builder()
                    .setPopUpTo(R.id.setStorageLocationFragment, true)
                    .build();
            NavHostFragment.findNavController(this)
                    .navigate(Uri.parse(getString(R.string.deeplink_set_storage)), navOptions);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mainActivityInterface.registerFragment(null,"ProfileFragment");
    }
}
