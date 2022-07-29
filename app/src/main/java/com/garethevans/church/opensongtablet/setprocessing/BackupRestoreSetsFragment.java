package com.garethevans.church.opensongtablet.setprocessing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.garethevans.church.opensongtablet.R;
import com.garethevans.church.opensongtablet.databinding.SettingsSetsBackupsBinding;
import com.garethevans.church.opensongtablet.interfaces.MainActivityInterface;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class BackupRestoreSetsFragment extends Fragment {

    private MainActivityInterface mainActivityInterface;
    private SettingsSetsBackupsBinding myView;
    private ArrayList<String> chosenSets;
    private String backupFilename;
    private Uri backupUri;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private boolean success = false;
    private final String setSeparator = "__";

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivityInterface = (MainActivityInterface) context;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        myView = SettingsSetsBackupsBinding.inflate(inflater, container, false);

        Window w = requireActivity().getWindow();
        if (w != null) {
            w.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }

        if (mainActivityInterface.getWhattodo().equals("restoresets")) {
            mainActivityInterface.updateToolbar(getString(R.string.restore_sets));
            setupFileChooserListener();
            initialiseLauncher();
            openFilePicker();
        } else {
            // Set up views
            setupViews();
        }

        myView.nestedScrollView.setExtendedFabToAnimate(myView.createBackupFAB);
        return myView.getRoot();
    }

    private void initialiseLauncher () {
        // Initialise the launcher if we are importing/restoring
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                try {
                    Intent data = result.getData();
                    if (data != null) {
                        backupUri = data.getData();
                        String importFilename = mainActivityInterface.getStorageAccess().getFileNameFromUri(backupUri);
                        if (importFilename.endsWith(".osbs")) {
                            myView.backupName.setText(importFilename);
                            setupViews();
                        } else {
                            myView.backupName.setText(getString(R.string.unknown));
                            myView.createBackupFAB.setEnabled(false);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setupFileChooserListener() {
        myView.backupName.setFocusable(false);
        myView.backupName.setFocusableInTouchMode(false);
        myView.backupName.setOnClickListener(v -> openFilePicker());
    }

    private void openFilePicker() {
        // Open the file picker and when the user has picked a file, deal with it
        Intent loadIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        Uri uri = mainActivityInterface.getStorageAccess().
                getUriForItem("Backups","",null);
        loadIntent.setDataAndType(uri,"application/*");
        loadIntent.putExtra("android.provider.extra.INITIAL_URI", uri);
        loadIntent.putExtra("android.content.extra.SHOW_ADVANCED", true);
        activityResultLauncher.launch(loadIntent);
    }

    private void setupViews() {
        // Clear any existing items!
        myView.foundSetsListView.removeAllViews();

        if (mainActivityInterface.getWhattodo().equals("backupsets")) {
            // Create a temp filename
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd", mainActivityInterface.getLocale());
            String formattedDate = df.format(cal.getTime());
            myView.backupName.setText("OpenSongSetBackup_" + formattedDate + ".osbs");

            mainActivityInterface.updateToolbar(getString(R.string.backup_sets));

            myView.overWrite.setVisibility(View.GONE);

            myView.createBackupFAB.setText(getString(R.string.backup));
            myView.createBackupFAB.setOnClickListener(view -> doBackup());

            // Add the checkboxes
            addCheckBoxes(mainActivityInterface.getStorageAccess().listFilesInFolder("Sets",""));


        } else {
            // Filename is set when user selects a file
            myView.overWrite.setVisibility(View.VISIBLE);

            myView.createBackupFAB.setText(getString(R.string.import_basic));
            myView.createBackupFAB.setOnClickListener(view -> doImport());

            myView.progressBar.setVisibility(View.VISIBLE);

            // Get a list of the sets in the zip file (alphabetically)
            // Do this in a new Thread
            new Thread(() -> {
                ArrayList<String> setList = new ArrayList<>();

                InputStream inputStream = mainActivityInterface.getStorageAccess().getInputStream(backupUri);
                ZipInputStream zipInputStream = new ZipInputStream(inputStream);
                ZipEntry ze;

                try {
                    while ((ze = zipInputStream.getNextEntry()) != null) {
                        setList.add(ze.getName());
                    }
                    zipInputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                requireActivity().runOnUiThread(() -> {
                    // Add the checkboxes
                    addCheckBoxes(setList);
                    myView.progressBar.setVisibility(View.GONE);
                });
            }).start();
        }
    }

    private void addCheckBoxes(ArrayList<String> setList) {
        // To make this more readable, keep the MAIN sets separate from folder sets
        ArrayList<String> mainSets = new ArrayList<>();
        ArrayList<String> folderSets = new ArrayList<>();

        for (String set:setList) {
            if (!set.contains(setSeparator)) {
                mainSets.add(set);
            } else {
                folderSets.add(set);
            }
        }
        Collections.sort(mainSets);
        Collections.sort(folderSets);

        // Now we've sorted, Go through the list and add a new checkbox item for each one
        addSetCheckBoxes(mainSets);
        addSetCheckBoxes(folderSets);

        myView.createBackupFAB.setEnabled(myView.foundSetsListView.getChildCount() > 0);
    }

    private void addSetCheckBoxes(ArrayList<String> set) {
        for (String setItem:set) {
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setText(niceSetItem(setItem));
            checkBox.setTag(setItem);
            checkBox.setChecked(true);
            checkBox.setPadding(16, 32, 16, 32);
            myView.foundSetsListView.addView(checkBox);
        }
    }

    private String niceSetItem(String setItem) {
        // This returns sets with categories in brackets
        if (!setItem.contains(setSeparator)) {
            return "(" + getString(R.string.mainfoldername) + ") " + setItem;
        } else {
            String[] bits = setItem.split(setSeparator);
            if (bits.length==2) {
                return "(" + bits[0] + ") " + bits[1];
            } else {
                return setItem;
            }
        }
    }

    private void doBackup() {
        // Now we make a zip file from the selected sets (.osbs file to be precise!)
        myView.progressBar.setVisibility(View.VISIBLE);

        // Get a note of the chosen set filenames
        getChosenSets();

        backupFilename = myView.backupName.getText().toString();

        // Do the main lifting in a new thread
        new Thread(() -> {
            if (backupFilename.isEmpty()) {
                backupFilename = "OpenSongSetBackup.osbs";
            }
            if (!backupFilename.endsWith(".osbs")) {
                backupFilename = backupFilename + ".osbs";
            }
            Uri backupUri = mainActivityInterface.getStorageAccess().getUriForItem("Backups","",backupFilename);
            mainActivityInterface.getStorageAccess().lollipopCreateFileForOutputStream(true, backupUri,null,"Backups","",backupFilename);
            OutputStream outputStream = mainActivityInterface.getStorageAccess().getOutputStream(backupUri);
            ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
            ZipEntry ze;
            byte[] tempBuff = new byte[1024];

            // For each selected set, get a uri reference and input stream and add to the zip output stream
            for (int x=0; x<chosenSets.size(); x++) {
                try {
                    Uri thisUri = mainActivityInterface.getStorageAccess().getUriForItem("Sets", "", chosenSets.get(x));
                    InputStream inputStream = mainActivityInterface.getStorageAccess().getInputStream(thisUri);
                    ze = new ZipEntry(chosenSets.get(x));
                    zipOutputStream.putNextEntry(ze);
                    if (!ze.isDirectory()) {
                        int len;
                        while ((len = inputStream.read(tempBuff)) > 0) {
                            zipOutputStream.write(tempBuff, 0, len);
                        }
                    }
                    zipOutputStream.closeEntry();
                    inputStream.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                zipOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            requireActivity().runOnUiThread(() -> {
                if (myView!=null) {
                    myView.progressBar.setVisibility(View.GONE);
                }
                Intent intent = mainActivityInterface.getExportActions().exportBackup(backupUri, backupFilename);
                startActivity(Intent.createChooser(intent, getString(R.string.backup_info)));
            });
        }).start();

    }

    private void doImport() {
        myView.progressBar.setVisibility(View.VISIBLE);
        InputStream inputStream = mainActivityInterface.getStorageAccess().getInputStream(backupUri);
        ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(inputStream));
        success = false;
        boolean overwrite = myView.overWrite.isChecked();

        new Thread(() -> {
            // Get a note of the chosen sets
            getChosenSets();

            ZipEntry ze;
            byte[] buffer = new byte[8192];

            try {
                while ((ze = zipInputStream.getNextEntry()) != null) {
                    if (chosenSets.contains(ze.getName())) {
                        Uri file_uri = mainActivityInterface.getStorageAccess().getUriForItem("Sets", "", ze.getName());
                        boolean exists = mainActivityInterface.getStorageAccess().uriExists(file_uri);
                        if (!exists || overwrite) {
                            mainActivityInterface.getStorageAccess().lollipopCreateFileForOutputStream(true, file_uri, null, "Sets", "", ze.getName());
                            OutputStream outputStream = mainActivityInterface.getStorageAccess().getOutputStream(file_uri);
                            // Write the file
                            int count;
                            while ((count = zipInputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, count);
                            }
                            outputStream.close();
                        }
                    }
                }
                success = true;
                zipInputStream.closeEntry();

            } catch (Exception e) {
                e.printStackTrace();
                success = false;
            }

            requireActivity().runOnUiThread(() -> {
                myView.progressBar.setVisibility(View.GONE);
                if (success) {
                    mainActivityInterface.getShowToast().doIt(getString(R.string.success));
                } else {
                    mainActivityInterface.getShowToast().doIt(getString(R.string.error));
                }
            });

        }).start();
    }

    private void getChosenSets() {
        chosenSets = new ArrayList<>();
        for (int x=0; x<myView.foundSetsListView.getChildCount(); x++) {
            if (((CheckBox) myView.foundSetsListView.getChildAt(x)).isChecked()) {
                chosenSets.add(((CheckBox) myView.foundSetsListView.getChildAt(x)).getTag().toString());
            }
        }
    }
}
