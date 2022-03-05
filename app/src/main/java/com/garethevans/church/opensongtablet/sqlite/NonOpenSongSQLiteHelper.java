package com.garethevans.church.opensongtablet.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import com.garethevans.church.opensongtablet.interfaces.MainActivityInterface;
import com.garethevans.church.opensongtablet.songprocessing.Song;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public class NonOpenSongSQLiteHelper extends SQLiteOpenHelper {

    private final Uri appDB, userDB;
    private final File appDBFile;
    private final String TAG = "NonOSSQLHelper";
    private final MainActivityInterface mainActivityInterface;

    public NonOpenSongSQLiteHelper(Context c) {
        super(c, SQLite.NON_OS_DATABASE_NAME, null, DATABASE_VERSION);
        mainActivityInterface = (MainActivityInterface) c;
        appDBFile = new File(c.getExternalFilesDir("Database"), SQLite.NON_OS_DATABASE_NAME);
        appDB = Uri.fromFile(appDBFile);
        userDB = mainActivityInterface.getStorageAccess().getUriForItem(
                "Settings", "", SQLite.NON_OS_DATABASE_NAME);

        // Check for a previous version in user storage
        // If it exists and isn't empty, copy it in to the appDB
        // If if doesn't exist, or is empty copy our appDB to the userDb
        importDatabase();
    }

    // Database Version
    private static final int DATABASE_VERSION = 2;

    private SQLiteDatabase getDB() {
        // The version we use has to be in local app storage unfortunately.  We can copy this though
        SQLiteDatabase db2 = SQLiteDatabase.openOrCreateDatabase(appDBFile,null);
        if (db2.getVersion()!=DATABASE_VERSION) {
            // Check we have the columns we need!
            db2.setVersion(DATABASE_VERSION);
            mainActivityInterface.getCommonSQL().updateTable(db2);
        }
        return db2;
    }

    private void importDatabase() {
        // This copies in the version in the settings folder if it exists and isn't empty
        if (mainActivityInterface.getStorageAccess().uriExists(userDB) &&
        mainActivityInterface.getStorageAccess().getFileSizeFromUri(userDB)>0) {
            InputStream inputStream = mainActivityInterface.getStorageAccess().getInputStream(userDB);
            OutputStream outputStream = mainActivityInterface.getStorageAccess().getOutputStream(appDB);
            Log.d(TAG,"User database copied in: "+mainActivityInterface.getStorageAccess().copyFile(inputStream,outputStream));
        } else {
            mainActivityInterface.getStorageAccess().lollipopCreateFileForOutputStream(true, userDB,null,"Settings","",
                    SQLite.NON_OS_DATABASE_NAME);
            Log.d(TAG,"Copy appDB to userDB: "+copyUserDatabase());
        }
    }

    public boolean copyUserDatabase() {
        // This copies the app persistent database into the user's OpenSong folder
        InputStream inputStream = mainActivityInterface.getStorageAccess().getInputStream(appDB);
        OutputStream outputStream = mainActivityInterface.getStorageAccess().getOutputStream(userDB);
        return mainActivityInterface.getStorageAccess().copyFile(inputStream,outputStream);
    }

    @Override
    public void onCreate(SQLiteDatabase db2) {
        // If the table doesn't exist, create it.
        db2.execSQL(SQLite.CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db2, int oldVersion, int newVersion) {
        // Do nothing here as we manually update the table to match
        db2.execSQL("DROP TABLE IF EXISTS " + SQLite.TABLE_NAME + ";");
    }
    public void initialise() {
        // If the database doesn't exist, create it
        try (SQLiteDatabase db2 = getDB()) {
            onCreate(db2);
        }
    }



    // Create, delete and update entries
    public void createSong(String folder, String filename) {
        // Creates a basic song entry to the database (id, songid, folder, file)
        try (SQLiteDatabase db2 = getDB()) {
            mainActivityInterface.getCommonSQL().createSong(db2, folder, filename);
        } catch (OutOfMemoryError | Exception e) {
            e.printStackTrace();
        }
    }
    public boolean deleteSong(String folder, String filename) {
        int i;
        try (SQLiteDatabase db2 = getDB()) {
            i = mainActivityInterface.getCommonSQL().deleteSong(db2,folder,filename);
        } catch (OutOfMemoryError | Exception e) {
            e.printStackTrace();
            return false;
        }
        return i > -1;
    }
    public void updateSong(Song thisSong) {
        try (SQLiteDatabase db2 = getDB()) {
            mainActivityInterface.getCommonSQL().updateSong(db2,thisSong);
        } catch (OutOfMemoryError | Exception e) {
            e.printStackTrace();
        }
    }

    public boolean renameSong(String oldFolder, String newFolder, String oldName, String newName) {
        try (SQLiteDatabase db2 = getDB()) {
            return mainActivityInterface.getCommonSQL().renameSong(db2, oldFolder,newFolder,oldName,newName);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getKey(String folder, String filename) {
        try (SQLiteDatabase db2 = getDB()) {
            return mainActivityInterface.getCommonSQL().getKey(db2, folder, filename);
        } catch (Exception | OutOfMemoryError e) {
            e.printStackTrace();
            return "";
        }
    }

    // Check if a song exists
    public boolean songExists(String folder, String filename) {
        try (SQLiteDatabase db = getDB()) {
            return mainActivityInterface.getCommonSQL().songExists(db, folder, filename);
        } catch (OutOfMemoryError | Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // TODO MIGHT REMOVE AS THE CONTENTS OF THIS DATABASE ARE PULLED INTO THE MAIN ONE AT RUNTIME
    // Find specific song
    public Song getSpecificSong(String folder, String filename) {
        // This gets basic info from the normal temporary SQLite database
        // It then also adds in any extra stuff found in the NonOpenSongSQLite database
        Song thisSong = new Song();
        String songId = mainActivityInterface.getCommonSQL().getAnySongId(folder,filename);
        try (SQLiteDatabase db = mainActivityInterface.getSQLiteHelper().getDB()) {
            // Get the basics - error here returns the basic stuff as an exception
            thisSong = mainActivityInterface.getCommonSQL().getSpecificSong(db,folder,filename);

            // Now look to see if there is extra information in the saved NonOpenSongDatabase
            try (SQLiteDatabase db2 = getDB()) {
                if (mainActivityInterface.getCommonSQL().songExists(db2,folder,filename)) {
                    // Get the more detailed values for the PDF/Image
                    thisSong = mainActivityInterface.getCommonSQL().getSpecificSong(db2,folder,filename);

                    // Update the values in the temporary main database (used for song menu and features)
                    mainActivityInterface.getCommonSQL().updateSong(db,thisSong);
                }
            } catch (OutOfMemoryError | Exception e) {
                e.printStackTrace();
                thisSong.setFolder(folder);
                thisSong.setFilename(filename);
                thisSong.setSongid(songId);
            }
        } catch (OutOfMemoryError | Exception e) {
            e.printStackTrace();
            thisSong.setFolder(folder);
            thisSong.setFilename(filename);
            thisSong.setSongid(songId);
        }
        return thisSong;
    }

    // TODO Flush entries that aren't in the filesystem, or alert the user to issues (perhaps asking to update the entry?


    // TODO Allow user to backup this database.  After opening, copy to the OpenSong/ folder (out of the system storage)?
}
