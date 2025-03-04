package com.garethevans.church.opensongtablet.songprocessing;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.garethevans.church.opensongtablet.R;
import com.garethevans.church.opensongtablet.customviews.ExposedDropDownArrayAdapter;
import com.garethevans.church.opensongtablet.databinding.EditSongMainBinding;
import com.garethevans.church.opensongtablet.interfaces.EditSongFragmentInterface;
import com.garethevans.church.opensongtablet.interfaces.MainActivityInterface;
import com.garethevans.church.opensongtablet.preferences.TextInputBottomSheet;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditSongFragmentMain extends Fragment  {

    // The variable used in this fragment
    private EditSongMainBinding myView;
    private MainActivityInterface mainActivityInterface;
    @SuppressWarnings({"unused","FieldCanBeLocal"})
    private final String TAG = "EditSongMain";
    private EditSongFragmentInterface editSongFragmentInterface;
    private String newFolder, new_folder_add_string="", new_folder_string="", new_folder_name_string="";
    private TextInputBottomSheet textInputBottomSheet;
    private ArrayList<String> folders;
    ExposedDropDownArrayAdapter arrayAdapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivityInterface = (MainActivityInterface) context;
        editSongFragmentInterface = (EditSongFragmentInterface) context;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            Log.d(TAG, "clearing focus");
            myView.title.clearFocus();
            myView.author.clearFocus();
            myView.copyright.clearFocus();
            myView.folder.clearFocus();
            myView.filename.clearFocus();
            myView.songNotes.clearFocus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        myView = EditSongMainBinding.inflate(inflater, container, false);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            prepareStrings();

            // Initialise the views
            setupValues();

            // Set listeners
            setUpListeners();
        });

        return myView.getRoot();
    }

    private void prepareStrings() {
        if (getContext()!=null) {
            new_folder_add_string = getString(R.string.new_folder_add);
            new_folder_string = getString(R.string.new_folder);
            new_folder_name_string = getString(R.string.new_folder_name);
        }
    }
    // Initialise the views
    private void setupValues() {
        myView.title.post(() -> myView.title.setText(mainActivityInterface.getTempSong().getTitle()));
        myView.author.post(() -> myView.author.setText(mainActivityInterface.getTempSong().getAuthor()));
        myView.copyright.post(() -> myView.copyright.setText(mainActivityInterface.getTempSong().getCopyright()));
        myView.songNotes.post(() -> {
            myView.songNotes.setText(mainActivityInterface.getTempSong().getNotes());
            mainActivityInterface.getProcessSong().editBoxToMultiline(myView.songNotes);
            mainActivityInterface.getProcessSong().stretchEditBoxToLines(myView.songNotes,5);
        });
        myView.filename.post(() -> myView.filename.setText(mainActivityInterface.getTempSong().getFilename()));
        getFoldersFromStorage();
        newFolder = "+ " + new_folder_add_string;
        folders.add(newFolder);
        if (getContext()!=null) {
            myView.folder.post(() -> {
                arrayAdapter = new ExposedDropDownArrayAdapter(getContext(), myView.folder, R.layout.view_exposed_dropdown_item, folders);
                myView.folder.setAdapter(arrayAdapter);
                myView.folder.setText(mainActivityInterface.getTempSong().getFolder());
            });
        }
        textInputBottomSheet = new TextInputBottomSheet(this,"EditSongFragmentMain",new_folder_string,new_folder_name_string,null,"","",true);

        myView.getRoot().post(() -> myView.getRoot().requestFocus());

        // Resize the bottom padding to the soft keyboard height or half the screen height for the soft keyboard (workaround)
        myView.resizeForKeyboardLayout.post(() -> mainActivityInterface.getWindowFlags().adjustViewPadding(mainActivityInterface,myView.resizeForKeyboardLayout));
    }

    // Sor the view visibility, listeners, etc.
    private void setUpListeners() {
        myView.title.post(() -> myView.title.addTextChangedListener(new MyTextWatcher("title")));
        myView.folder.post(() -> myView.folder.addTextChangedListener(new MyTextWatcher("folder")));
        myView.filename.post(() -> myView.filename.addTextChangedListener(new MyTextWatcher("filename")));
        myView.author.post(() -> myView.author.addTextChangedListener(new MyTextWatcher("author")));
        myView.copyright.post(() -> myView.copyright.addTextChangedListener(new MyTextWatcher("copyright")));
        myView.songNotes.post(() -> myView.songNotes.addTextChangedListener(new MyTextWatcher("notes")));

        // Scroll listener
        myView.nestedScrollView.post(() -> myView.nestedScrollView.setExtendedFabToAnimate(editSongFragmentInterface.getSaveButton()));
    }

    private class MyTextWatcher implements TextWatcher {

        String what;
        String value = "";

        MyTextWatcher(String what) {
            this.what = what;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s != null) {
                value = s.toString();
                if (what.equals("filename")) {
                    value = mainActivityInterface.getStorageAccess().safeFilename(value);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (what.equals("folder") && value.equals(newFolder) && getActivity()!=null) {
                myView.folder.setText(mainActivityInterface.getTempSong().getFolder());
                textInputBottomSheet.show(getActivity().getSupportFragmentManager(),
                        "TextInputBottomSheet");
            } else {
                updateTempSong();
            }
        }

        public void updateTempSong() {
            switch (what) {
                case "folder":
                    if (!what.equals(newFolder)) {
                        mainActivityInterface.getTempSong().setFolder(value);
                    }
                    break;
                case "filename":
                    mainActivityInterface.getTempSong().setFilename(value);
                    break;
                case "title":
                    mainActivityInterface.getTempSong().setTitle(value);
                    break;
                case "copyright":
                    mainActivityInterface.getTempSong().setCopyright(value);
                    break;
                case "author":
                    mainActivityInterface.getTempSong().setAuthor(value);
                    break;
                case "notes":
                    mainActivityInterface.getTempSong().setNotes(value);
                    break;
            }
        }
    }


    public void updateValue(String value) {
        Log.d(TAG,"value:"+value);
        // New folder name given.  If it isn't null/empty try to create it and select it
        boolean selectNewFolder = false;
        if (value!=null && !value.isEmpty()) {
            // Try to create the new folder (this checks that it doesn't already exist)
            selectNewFolder = mainActivityInterface.getStorageAccess().createFolder(
                        "Songs","",value,true);
        }

        if (selectNewFolder) {
            getFoldersFromStorage();
            newFolder = "+ " + new_folder_add_string;
            folders.add(newFolder);
            if (getContext() != null) {
                arrayAdapter = new ExposedDropDownArrayAdapter(getContext(), myView.folder, R.layout.view_exposed_dropdown_item, folders);
                myView.folder.setAdapter(arrayAdapter);
                myView.folder.setText(value);
            }
        }
    }

    private void getFoldersFromStorage() {
        ArrayList<String> songIds = mainActivityInterface.getStorageAccess().listSongs();
        mainActivityInterface.getStorageAccess().writeSongIDFile(songIds);
        folders = mainActivityInterface.getStorageAccess().getSongFolders(songIds,true,null);
    }

    // Finished with this view
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        myView = null;
    }
}
