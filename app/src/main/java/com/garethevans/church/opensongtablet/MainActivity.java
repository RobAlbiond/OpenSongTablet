package com.garethevans.church.opensongtablet;

import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE;
import static com.google.android.material.snackbar.Snackbar.make;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.hardware.display.DisplayManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.WindowCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.mediarouter.media.MediaRouteSelector;
import androidx.mediarouter.media.MediaRouter;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager2.widget.ViewPager2;

import com.garethevans.church.opensongtablet.abcnotation.ABCNotation;
import com.garethevans.church.opensongtablet.animation.CustomAnimation;
import com.garethevans.church.opensongtablet.animation.ShowCase;
import com.garethevans.church.opensongtablet.appdata.AlertChecks;
import com.garethevans.church.opensongtablet.appdata.AlertInfoBottomSheet;
import com.garethevans.church.opensongtablet.appdata.BootUpFragment;
import com.garethevans.church.opensongtablet.appdata.CheckInternet;
import com.garethevans.church.opensongtablet.appdata.FixLocale;
import com.garethevans.church.opensongtablet.appdata.SetTypeFace;
import com.garethevans.church.opensongtablet.appdata.VersionNumber;
import com.garethevans.church.opensongtablet.autoscroll.Autoscroll;
import com.garethevans.church.opensongtablet.bible.Bible;
import com.garethevans.church.opensongtablet.ccli.CCLILog;
import com.garethevans.church.opensongtablet.ccli.SettingsCCLI;
import com.garethevans.church.opensongtablet.chords.CustomChordsFragment;
import com.garethevans.church.opensongtablet.chords.Transpose;
import com.garethevans.church.opensongtablet.controls.Gestures;
import com.garethevans.church.opensongtablet.controls.PageButtons;
import com.garethevans.church.opensongtablet.controls.PedalActions;
import com.garethevans.church.opensongtablet.controls.PedalsFragment;
import com.garethevans.church.opensongtablet.controls.SwipeFragment;
import com.garethevans.church.opensongtablet.controls.Swipes;
import com.garethevans.church.opensongtablet.customslides.CustomSlide;
import com.garethevans.church.opensongtablet.customviews.DrawNotes;
import com.garethevans.church.opensongtablet.databinding.ActivityBinding;
import com.garethevans.church.opensongtablet.export.ExportActions;
import com.garethevans.church.opensongtablet.export.PrepareFormats;
import com.garethevans.church.opensongtablet.filemanagement.AreYouSureBottomSheet;
import com.garethevans.church.opensongtablet.filemanagement.ExportFiles;
import com.garethevans.church.opensongtablet.filemanagement.LoadSong;
import com.garethevans.church.opensongtablet.filemanagement.SaveSong;
import com.garethevans.church.opensongtablet.filemanagement.StorageAccess;
import com.garethevans.church.opensongtablet.filemanagement.StorageManagementFragment;
import com.garethevans.church.opensongtablet.highlighter.HighlighterEditFragment;
import com.garethevans.church.opensongtablet.importsongs.ImportOnlineFragment;
import com.garethevans.church.opensongtablet.importsongs.WebDownload;
import com.garethevans.church.opensongtablet.interfaces.ActionInterface;
import com.garethevans.church.opensongtablet.interfaces.DialogReturnInterface;
import com.garethevans.church.opensongtablet.interfaces.MainActivityInterface;
import com.garethevans.church.opensongtablet.interfaces.MidiAdapterInterface;
import com.garethevans.church.opensongtablet.interfaces.NearbyInterface;
import com.garethevans.church.opensongtablet.interfaces.NearbyReturnActionsInterface;
import com.garethevans.church.opensongtablet.interfaces.SwipeDrawingInterface;
import com.garethevans.church.opensongtablet.links.LinksFragment;
import com.garethevans.church.opensongtablet.metronome.Metronome;
import com.garethevans.church.opensongtablet.midi.Midi;
import com.garethevans.church.opensongtablet.midi.MidiFragment;
import com.garethevans.church.opensongtablet.nearby.NearbyConnections;
import com.garethevans.church.opensongtablet.nearby.NearbyConnectionsFragment;
import com.garethevans.church.opensongtablet.pads.Pad;
import com.garethevans.church.opensongtablet.pdf.MakePDF;
import com.garethevans.church.opensongtablet.pdf.OCR;
import com.garethevans.church.opensongtablet.pdf.PDFSong;
import com.garethevans.church.opensongtablet.performance.DisplayPrevNext;
import com.garethevans.church.opensongtablet.performance.PerformanceFragment;
import com.garethevans.church.opensongtablet.performance.PerformanceGestures;
import com.garethevans.church.opensongtablet.preferences.Preferences;
import com.garethevans.church.opensongtablet.preferences.ProfileActions;
import com.garethevans.church.opensongtablet.presentation.PresentationFragment;
import com.garethevans.church.opensongtablet.screensetup.AppActionBar;
import com.garethevans.church.opensongtablet.screensetup.BatteryStatus;
import com.garethevans.church.opensongtablet.screensetup.DoVibrate;
import com.garethevans.church.opensongtablet.screensetup.FontSetupFragment;
import com.garethevans.church.opensongtablet.screensetup.ShowToast;
import com.garethevans.church.opensongtablet.screensetup.ThemeColors;
import com.garethevans.church.opensongtablet.screensetup.WindowFlags;
import com.garethevans.church.opensongtablet.secondarydisplay.ExternalDisplay;
import com.garethevans.church.opensongtablet.secondarydisplay.MediaRouterCallback;
import com.garethevans.church.opensongtablet.secondarydisplay.MySessionManagerListener;
import com.garethevans.church.opensongtablet.secondarydisplay.PresentationCommon;
import com.garethevans.church.opensongtablet.setmenu.SetMenuFragment;
import com.garethevans.church.opensongtablet.setprocessing.CurrentSet;
import com.garethevans.church.opensongtablet.setprocessing.SetActions;
import com.garethevans.church.opensongtablet.setprocessing.SetActionsFragment;
import com.garethevans.church.opensongtablet.setprocessing.SetManageFragment;
import com.garethevans.church.opensongtablet.songmenu.SongListBuildIndex;
import com.garethevans.church.opensongtablet.songmenu.SongMenuFragment;
import com.garethevans.church.opensongtablet.songmenu.ViewPagerAdapter;
import com.garethevans.church.opensongtablet.songprocessing.ConvertChoPro;
import com.garethevans.church.opensongtablet.songprocessing.ConvertOnSong;
import com.garethevans.church.opensongtablet.songprocessing.ConvertTextSong;
import com.garethevans.church.opensongtablet.songprocessing.EditSongFragment;
import com.garethevans.church.opensongtablet.songprocessing.EditSongFragmentMain;
import com.garethevans.church.opensongtablet.songprocessing.EditSongFragmentTags;
import com.garethevans.church.opensongtablet.songprocessing.ProcessSong;
import com.garethevans.church.opensongtablet.songprocessing.Song;
import com.garethevans.church.opensongtablet.songprocessing.SongSheetHeaders;
import com.garethevans.church.opensongtablet.sqlite.CommonSQL;
import com.garethevans.church.opensongtablet.sqlite.NonOpenSongSQLiteHelper;
import com.garethevans.church.opensongtablet.sqlite.SQLiteHelper;
import com.garethevans.church.opensongtablet.tools.TimeTools;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.CastStateListener;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements MainActivityInterface,
        ActionInterface, NearbyInterface, NearbyReturnActionsInterface, DialogReturnInterface,
        MidiAdapterInterface, SwipeDrawingInterface, BatteryStatus.MyInterface {

    private ActivityBinding myView;

    private MainActivityInterface mainActivityInterface;

    // The helpers sorted alphabetically
    private ABCNotation abcNotation;
    private AlertChecks alertChecks;
    private AppActionBar appActionBar;
    private Autoscroll autoscroll;
    private Bible bible;
    private CCLILog ccliLog;
    private CheckInternet checkInternet;
    private CommonSQL commonSQL;
    private ConvertChoPro convertChoPro;
    private ConvertOnSong convertOnSong;
    private ConvertTextSong convertTextSong;
    private CurrentSet currentSet;
    private CustomAnimation customAnimation;
    private CustomSlide customSlide;
    private DisplayPrevNext displayPrevNext;
    private DoVibrate doVibrate;
    private DrawNotes drawNotes;
    private ExportActions exportActions;
    private ExportFiles exportFiles;
    private FixLocale fixLocale;
    private Gestures gestures;
    private LoadSong loadSong;
    private MakePDF makePDF;
    private MediaRouterCallback mediaRouterCallback;
    private Metronome metronome;
    private Midi midi;
    private NearbyConnections nearbyConnections;
    private NonOpenSongSQLiteHelper nonOpenSongSQLiteHelper;
    private OCR ocr;
    private Pad pad;
    private PageButtons pageButtons;
    private PDFSong pdfSong;
    private PedalActions pedalActions;
    private PerformanceGestures performanceGestures;
    private Preferences preferences;
    private PrepareFormats prepareFormats;
    private PresentationCommon presentationCommon;
    private ProcessSong processSong;
    private ProfileActions profileActions;
    private SaveSong saveSong;
    private SetActions setActions;
    private SetTypeFace setTypeFace;
    private ShowCase showCase;
    private ShowToast showToast;
    private Song song, tempSong, indexingSong;
    private SongListBuildIndex songListBuildIndex;
    private SongSheetHeaders songSheetHeaders;
    private SQLiteHelper sqLiteHelper;
    private StorageAccess storageAccess;
    private Swipes swipes;
    private ThemeColors themeColors;
    private TimeTools timeTools;
    private Transpose transpose;
    private VersionNumber versionNumber;
    private WebDownload webDownload;


    // The navigation controls
    private NavHostFragment navHostFragment;
    private NavController navController;

    // Other views/listeners/helpers
    private WindowFlags windowFlags;
    private BatteryStatus batteryStatus;
    private CastContext castContext;
    private SessionManager sessionManager;
    private CastSession castSession;
    private MySessionManagerListener sessionManagerListener;
    private SongMenuFragment songMenuFragment;
    private SetMenuFragment setMenuFragment;
    private PerformanceFragment performanceFragment;
    private PresentationFragment presentationFragment;
    private BootUpFragment bootUpFragment;
    private EditSongFragment editSongFragment;
    private EditSongFragmentMain editSongFragmentMain;
    private NearbyConnectionsFragment nearbyConnectionsFragment;
    private SwipeFragment swipeFragment;
    private Fragment registeredFragment;
    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager2 viewPager;
    private ActionBar actionBar;
    private AppBarConfiguration appBarConfiguration;
    private DisplayManager displayManager;
    private ExternalDisplay externalDisplay;
    private CastStateListener castStateListener;
    private MediaRouter mediaRouter;
    private MediaRouteSelector mediaRouteSelector;
    private CastDevice castDevice;
    //private PresentationServiceHDMI hdmi;

    // Variables used
    private ArrayList<View> targets;
    private ArrayList<String> infos, dismisses;
    private ArrayList<Boolean> rects;
    private MediaPlayer mediaPlayer1, mediaPlayer2;
    private ArrayList<View> sectionViews;
    private LinearLayout songSheetTitleLayout;
    private ArrayList<Integer> sectionWidths, sectionHeights, songSheetTitleLayoutSize;
    private String whichMode, whattodo, importFilename;
    private Uri importUri;
    private boolean doonetimeactions = true, settingsOpen = false, nearbyOpen = false, showSetMenu,
            pageButtonActive = true, fullIndexRequired;
    private final String TAG = "MainActivity";
    private MenuItem settingsButton;
    private Locale locale;
    private Bitmap screenShot;

    // Set up the activity
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        myView = ActivityBinding.inflate(getLayoutInflater());
        setContentView(myView.getRoot());

        // Initialise helpers
        setupHelpers();

        // Set up the action bar
        setupActionbar();

        // Set up views
        setupViews();

        // Set up the navigation controller
        setupNavigation();

        // One time actions will have been completed
        // Initiate the boot check progress
        doonetimeactions = false;
        startBoot();

    }
    private void setupHelpers() {
        storageAccess = new StorageAccess();
        preferences = new Preferences();

        // The song stuff
        songListBuildIndex = new SongListBuildIndex();

        // The screen display stuff
        customAnimation = new CustomAnimation();
        showCase = new ShowCase();
        showToast = new ShowToast();

        // The app setup
        versionNumber = new VersionNumber();
        fixLocale = new FixLocale();
        checkInternet = new CheckInternet();
        nearbyConnections = new NearbyConnections(this);
        mediaRouterCallback = new MediaRouterCallback();
        doVibrate = new DoVibrate();
        customAnimation = new CustomAnimation();
        presentationCommon = new PresentationCommon();
        webDownload = new WebDownload();
        alertChecks = new AlertChecks();

        // For user preferences
        setTypeFace = new SetTypeFace();
        themeColors = new ThemeColors();
        profileActions = new ProfileActions();

        // The databases
        sqLiteHelper = new SQLiteHelper(this);
        nonOpenSongSQLiteHelper = new NonOpenSongSQLiteHelper(this);
        commonSQL = new CommonSQL();

        // Converting song formats and processing song content
        convertChoPro = new ConvertChoPro();
        convertOnSong = new ConvertOnSong();
        convertTextSong = new ConvertTextSong();
        processSong = new ProcessSong();
        prepareFormats = new PrepareFormats();
        pdfSong = new PDFSong();
        makePDF = new MakePDF();
        songSheetHeaders = new SongSheetHeaders();
        ocr = new OCR();
        transpose = new Transpose();
        abcNotation = new ABCNotation();
        song = new Song();

        // Loading up songs and the indexing
        loadSong = new LoadSong();
        saveSong = new SaveSong();

        // Sets
        currentSet = new CurrentSet();
        setActions = new SetActions();

        // Song actions/features
        performanceGestures = new PerformanceGestures(this,this);
        pageButtons = new PageButtons(this);
        midi = new Midi();
        pedalActions = new PedalActions(this,this);
        pad = new Pad(this, myView.onScreenInfo.pad);
        autoscroll = new Autoscroll(this,myView.onScreenInfo.autoscrollTime,
                myView.onScreenInfo.autoscrollTotalTime,myView.onScreenInfo.autoscroll);
        metronome = new Metronome();
        gestures = new Gestures(this);
        swipes = new Swipes(this);
        timeTools = new TimeTools();
        displayPrevNext = new DisplayPrevNext(this,myView.nextPrevInfo.nextPrevInfoLayout,
                myView.nextPrevInfo.prevButton, myView.nextPrevInfo.nextButton);

        // Other file actions
        ccliLog = new CCLILog();
        exportFiles = new ExportFiles();
        exportActions = new ExportActions();
        bible = new Bible();
        customSlide = new CustomSlide();
    }
    private void setupBatteryStatus() {
        // Battery monitor
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        this.registerReceiver(batteryStatus, filter);
    }
    private void setupActionbar() {
        setSupportActionBar(myView.toolBar.myToolbar);
            actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    @Override
    public void changeActionBarVisible(boolean wasScrolling, boolean scrollButton) {
        if (!whichMode.equals("Presentation") && preferences.getMyPreferenceBoolean(this, "hideActionBar", false)) {
            // If we are are in performance or stage mode and want to hide the actionbar, then move the views up to the top
            myView.fragmentView.setTop(0);
        } else {
            // Otherwise move the content below it
            myView.fragmentView.setTop(actionBar.getHeight());
        }
        appActionBar.toggleActionBar(wasScrolling,scrollButton,myView.drawerLayout.isOpen());
    }
    private void setupViews() {
        windowFlags = new WindowFlags(this.getWindow());
        appActionBar = new AppActionBar(actionBar,myView.toolBar.getRoot(),
                batteryStatus,myView.toolBar.songtitleAb,
                myView.toolBar.songauthorAb, myView.toolBar.songkeyAb,
                myView.toolBar.songcapoAb,myView.toolBar.batteryimage,
                myView.toolBar.batterycharge,myView.toolBar.digitalclock,
                preferences.getMyPreferenceBoolean(this,"hideActionBar",false));
        pageButtons.setMainFABS(this,
                myView.pageButtonRight.actionFAB, myView.pageButtonRight.custom1Button,
                myView.pageButtonRight.custom2Button,myView.pageButtonRight.custom3Button,
                myView.pageButtonRight.custom4Button,myView.pageButtonRight.custom5Button,
                myView.pageButtonRight.custom6Button,myView.pageButtonRight.bottomButtons);
        pageButtons.animatePageButton(this,false);
    }
    private void startBoot() {
        // The BootCheckFragment has already started and displayed the splash logo
        // Now initialise the checks
        Log.d(TAG,"bootUpFragment="+bootUpFragment);
        if (bootUpFragment!=null && bootUpFragment.isAdded()) {
            Log.d(TAG,"bootUpFragment.startOrSetUp() called");
            bootUpFragment.startOrSetUp();
        } else {
            Log.d(TAG, "bootUpFragment is null!!!");
        }
    }
    @Override
    public void initialiseActivity() {
        // Set up song / set menu tabs
        setUpSongMenuTabs();

        // Set the version in the menu
        versionNumber.updateMenuVersionNumber(this, myView.menuTop.versionCode);

        // Set up page buttons
        setListeners();

        // Get the start variables needed for the app
        initialiseStartVariables();

        // Set up battery status
        setupBatteryStatus();

        // Set up the page buttons
        updatePageButtonLayout();

        // Set up nearby
        setupNearby();

        // Initialise the CastContext
        setUpCast();

    }
    private void initialiseStartVariables() {
        themeColors.setThemeName(preferences.getMyPreferenceString(this, "appTheme", "dark"));
        whichMode = preferences.getMyPreferenceString(this, "whichMode", "Performance");

        // Song location
        song.setFilename(preferences.getMyPreferenceString(this,"songfilename","Welcome to OpenSongApp"));
        song.setFolder(preferences.getMyPreferenceString(this, "whichSongFolder", getString(R.string.mainfoldername)));

        // Set
        setActions.preferenceStringToArrays(this,this);

        // Set the locale
        fixLocale.setLocale(this,this);
        locale = fixLocale.getLocale();

        // ThemeColors
        themeColors.getDefaultColors(this,this);

        // Typefaces
        setTypeFace.setUpAppFonts(this,this,new Handler(),new Handler(),new Handler(),new Handler(),new Handler());
    }
    private void setListeners() {
        myView.pageButtonRight.actionFAB.setOnClickListener(v  -> {
            if (pageButtonActive) {
                pageButtonActive = false;
                Handler h = new Handler();
                h.postDelayed(() -> pageButtonActive = true,600);
                animatePageButtons();
            }
        });
        myView.pageButtonRight.actionFAB.setOnLongClickListener(view -> {
            navigateToFragment("opensongapp://settings/controls/pagebuttons",0);
            return true;
        });

        myView.drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            float initialVal = -1.0f;
            boolean decided;

            private void resetVals() {
                decided = false;
                initialVal = -1;
            }

            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                if (!decided && initialVal==-1.0f) {
                    // Just started, so set the inital value
                    initialVal = slideOffset;
                } else if (!decided && initialVal!=-0.0f) {
                    // We have our first value, so now compare.
                    // If we are getting bigger = opening, if smaller, closing
                    hideActionButton(slideOffset > initialVal);
                    decided = true;
                }
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                hideActionButton(true);
                setWindowFlags();
                if (setSongMenuFragment()) {
                    showTutorial("songsetMenu");
                }
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                hideActionButton(myView.drawerLayout.getDrawerLockMode(GravityCompat.START) != DrawerLayout.LOCK_MODE_UNLOCKED);
                hideKeyboard();
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                // Reset the check vals
                resetVals();
            }
        });
    }
    @Override
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view!=null && imm!=null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    private void setWindowFlags() {
        // Fix the page flags
        if (windowFlags==null) {
            windowFlags = new WindowFlags(this.getWindow());
        }
        try {
            windowFlags.setWindowFlags();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        // If pedalsFragment is open, send the keyCode and event there
        if (currentFragment(R.id.pedalsFragment) && ((PedalsFragment)getFragmentFromId(R.id.pedalsFragment)).isListening()) {
            ((PedalsFragment)getFragmentFromId(R.id.pedalsFragment)).keyDownListener(keyCode);
            return false;
        } else {
            pedalActions.commonEventDown(keyCode, null);
        }
        return super.onKeyDown(keyCode, keyEvent);
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent keyEvent) {
        // If pedalsFragment is open, send the keyCode and event there
        if (currentFragment(R.id.pedalsFragment) && ((PedalsFragment)getFragmentFromId(R.id.pedalsFragment)).isListening()) {
            ((PedalsFragment)getFragmentFromId(R.id.pedalsFragment)).commonEventUp();
            return true;
        } else {
            pedalActions.commonEventUp(keyCode,null);
        }
        return super.onKeyUp(keyCode, keyEvent);
    }
    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent keyEvent) {
        // If pedalsFragment is open, send the keyCode and event there
        if (currentFragment(R.id.pedalsFragment) && ((PedalsFragment)getFragmentFromId(R.id.pedalsFragment)).isListening()) {
            ((PedalsFragment)getFragmentFromId(R.id.pedalsFragment)).commonEventLong();
            return true;
        } else {
            pedalActions.commonEventLong(keyCode,null);
        }
        return super.onKeyLongPress(keyCode, keyEvent);
    }
    @Override
    public void onBackPressed() {
        if (navController.getCurrentDestination()!=null &&
                (navController.getCurrentDestination().getId()==R.id.performanceFragment ||
                        navController.getCurrentDestination().getId()==R.id.presentationFragment ||
                        navController.getCurrentDestination().getId()==R.id.storageManagementFragment)) {
            displayAreYouSure("exit", getString(R.string.exit_confirm), null,
                    navController.getCurrentDestination().getNavigatorName(),
                    navHostFragment, null);
        } else {
            super.onBackPressed();
        }
    }




    // Navigation logic
    private void setupNavigation() {
        navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment!=null) {
            navController = navHostFragment.getNavController();
        }
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = new AppBarConfiguration.Builder(R.id.bootUpFragment,
                R.id.performanceFragment, R.id.presentationFragment)
                .setOpenableLayout(myView.drawerLayout)
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }
    @Override
    public void navigateToFragment(String deepLink, int id) {
        // Either sent a deeplink string, or a fragment id
        lockDrawer(true);
        closeDrawer(true);  // Only the Performance and Presentation fragments allow this.  Switched on in these fragments
        hideActionButton(true);
        try {
            if (deepLink!=null) {
                navController.navigate(Uri.parse(deepLink));
            } else {
                navController.navigate(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void popTheBackStack(int id, boolean inclusive) {
        navController.popBackStack(id,inclusive);
    }
    @Override
    public void updateFragment(String fragName, Fragment callingFragment, ArrayList<String> arguments) {
        if (fragName!=null) {
            // The fragName can also be text that hints at a fragment
            switch (fragName) {
                case "StorageManagementFragment":
                    ((StorageManagementFragment)callingFragment).updateFragment();
                    break;

                case "createNewSong":
                    // User was in song menu dialog, clicked on create, then entered a new file name
                    // Check this was successful (saved as arguments)
                    if (arguments!=null && arguments.size()>0 && arguments.get(0).equals("success")) {
                        // Write a blank xml file with the song name in it
                        // TODO
                        song = processSong.initialiseSong(mainActivityInterface,song.getFolder(),"NEWSONGFILENAME");
                        String newSongText = processSong.getXML(this,mainActivityInterface,song);
                        if (storageAccess.doStringWriteToFile(this,mainActivityInterface,"Songs",song.getFolder(), song.getFilename(),newSongText)) {
                            navigateToFragment(null,R.id.editSongFragment);
                        } else {
                            ShowToast.showToast(this,getString(R.string.error));
                        }
                    }
                    break;

                case "duplicateSong":
                    // User was in song menu dialog, clicked on create, then entered a new file name
                    // Check this was successful (saved as arguments)
                    if (arguments!=null && arguments.size()>1 && arguments.get(0).equals("success")) {
                        // We now need to copy the original file.  It's contents are saved in arguments.get(1)
                        if (storageAccess.doStringWriteToFile(this,mainActivityInterface,"Songs",song.getFolder(),song.getFilename(),arguments.get(1))) {
                            doSongLoad(song.getFolder(),song.getFilename(),false);
                        } else {
                            ShowToast.showToast(this,getString(R.string.error));
                        }
                    }
                    break;

                case "set_updateKeys":
                case "set_updateView":
                case "set_updateItem":
                    // User has the set menu open and wants to do something
                    SetMenuFragment setMenuFragment = (SetMenuFragment) getSupportFragmentManager().findFragmentById(R.id.setMenuFragment);
                    if (setMenuFragment!=null) {
                        if (fragName.equals("set_updateView")) {
                            setMenuFragment.updateSet();
                        } else if (fragName.equals("set_updateKeys")){
                            setMenuFragment.updateKeys();
                        } else if (arguments!=null && arguments.size()>0){
                            setMenuFragment.updateItem(Integer.parseInt(arguments.get(0)));
                        }
                    }
                    break;
                case "linksFragment":
                    // Update the values in the links
                    if (callingFragment!=null && callingFragment.isVisible()) {
                        try {
                            ((LinksFragment) callingFragment).setupViews();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case "EditSongFragmentTags":
                    ((EditSongFragmentTags) callingFragment).updateValue();
                    break;

                case "confirmed_EditSongFragmentTags":
                    ((EditSongFragmentTags) callingFragment).removeTags(arguments);
                    break;


            }
        }
    }
    @Override
    public void navHome() {
        lockDrawer(false);
        whichMode = preferences.getMyPreferenceString(this,"whichMode","Performance");
        if (navController.getCurrentDestination()!=null) {
            navController.popBackStack(navController.getCurrentDestination().getId(), true);
        }
        if (whichMode.equals("Presentation")) {
            navigateToFragment("opensongapp://presentation",0);
        } else {
            navigateToFragment("opensongapp://performance",0);
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
    private boolean currentFragment(int fragId) {
        getSupportFragmentManager().executePendingTransactions();
        Fragment fragment = getFragmentFromId(fragId);
        Log.d(TAG, "fragId="+fragId);
        Log.d(TAG, "fragment="+fragment);
        if (fragment!=null) {
            Log.d(TAG, "fragment.isAdded=" + fragment.isAdded());
            Log.d(TAG, "fragment.isInLayout=" + fragment.isInLayout());
            Log.d(TAG, "fragment.isVisible=" + fragment.isVisible());
        }
        if (fragment!=null && fragment.isInLayout()) {
            return true;
        } else {
            return false;
        }
    }
    private Fragment getFragmentFromId(int fragId) {
        return getSupportFragmentManager().findFragmentById(fragId);
    }




    // Nearby stuff
    private void setupNearby() {
        // Set up the Nearby connection service
        nearbyConnections.getUserNickname(this,this);

        // Establish a known state for Nearby
        nearbyConnections.turnOffNearby(this);
    }
    @Override
    public NearbyConnections getNearbyConnections(MainActivityInterface mainActivityInterface) {
        // First update the mainActivityInterface used in nearby connections
        nearbyConnections.setMainActivityInterface(mainActivityInterface);
        // Return a reference to nearbyConnections
        return nearbyConnections;
    }
    @Override
    public NearbyConnections getNearbyConnections() {
        return nearbyConnections;
    }
    @Override
    public void setNearbyOpen(boolean nearbyOpen) {
        this.nearbyOpen = nearbyOpen;
    }
    private void openNearbyFragment() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            navigateToFragment("opensongapp://settings/nearby", 0);
        }
    }
    @Override
    public void startDiscovery(Context c, MainActivityInterface mainActivityInterface) {
        nearbyConnections.startDiscovery(c,mainActivityInterface);
    }
    @Override
    public void startAdvertising(Context c, MainActivityInterface mainActivityInterface) {
        nearbyConnections.startAdvertising(c,mainActivityInterface);
    }
    @Override
    public void stopDiscovery(Context c) {
        nearbyConnections.stopDiscovery(c);
    }
    @Override
    public void stopAdvertising(Context c) {
        nearbyConnections.stopAdvertising(c);
    }
    @Override
    public void turnOffNearby(Context c) {
        nearbyConnections.turnOffNearby(c);
    }
    @Override
    public void updateConnectionsLog() {
        // Send the command to the Nearby Connections fragment (if it exists!)
        try {
            if (nearbyConnectionsFragment!=null && nearbyOpen) {
                try {
                    nearbyConnectionsFragment.updateConnectionsLog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void doSendPayloadBytes(Context c,String infoPayload) {
        // TODO - IV addition to check if needed (obs no FullscreenActivity anymore!
        // // IV - Do not send section 0 payload when loading a song
        //        if (!FullscreenActivity.alreadyloading) {
        nearbyConnections.doSendPayloadBytes(c,infoPayload);
        // }
    }
    @Override
    public boolean requestNearbyPermissions() {
        // Only do this if the user has Google APIs installed, otherwise, there is no point
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            return requestCoarseLocationPermissions() && requestFineLocationPermissions();
        } else {
            installPlayServices();
            // Not allowed on this device
            return false;
        }
    }
    @Override
    public boolean requestCoarseLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            try {
                make(findViewById(R.id.fragmentView), R.string.location_rationale,
                        LENGTH_INDEFINITE).setAction(android.R.string.ok, view -> ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 403)).show();
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 403);
            return false;
        }
    }
    @Override
    public boolean requestFineLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            try {
                make(findViewById(R.id.coordinator), R.string.location_rationale,
                        LENGTH_INDEFINITE).setAction(android.R.string.ok, view -> ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 404)).show();
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 404);
            return false;
        }
    }


    // Secondary screen stuff
    private void setUpCast() {
        try {
            castContext = CastContext.getSharedInstance(this);
            sessionManager = castContext.getSessionManager();
            castSession = castContext.getSessionManager().getCurrentCastSession();
            sessionManagerListener = new MySessionManagerListener(this);

        } catch (Exception e) {
            // No Google Service available
            // Do nothing as the user will see a warning in the settings menu
            Log.d(TAG,"No Google Services");
        }
    }
    private void recoverCastState() {
        castSession = sessionManager.getCurrentCastSession();
        sessionManager.addSessionManagerListener(new MySessionManagerListener(this));
    }
    private void endCastState() {
        if (sessionManager!=null && sessionManagerListener!=null) {
            sessionManager.removeSessionManagerListener(sessionManagerListener);
        }
        castSession = null;
    }
    // THIS BIT CAUSES ERROR WHEN SETCONTENTVIEW CALLED AS DISPLAY IS NULL
    // MAYBE ADD TO SEPARATE EXTERNAL DISPLAY CLASS?
    /*@Override
    public void setDisplay(Display display) {
        this.display. = display;
    }
    @Override
    public Display getDisplay() {
        return display;
    }
    @Override
    public ExternalDisplay getExternalDisplay() {
        if (externalDisplay!=null && display!=null) {
            externalDisplay = new ExternalDisplay(this,display);
        }
        return externalDisplay;
    }*/



    // Instructions sent from fragments for MainActivity to deal with
    @Override
    public void hideActionButton(boolean hide) {
        if (hide) {
            myView.pageButtonRight.actionFAB.setVisibility(View.GONE);
            myView.pageButtonRight.bottomButtons.setVisibility(View.GONE);
            myView.onScreenInfo.info.setVisibility(View.GONE);
            myView.nextPrevInfo.nextPrevInfoLayout.setVisibility(View.GONE);

        } else {
            myView.pageButtonRight.actionFAB.setVisibility(View.VISIBLE);
            myView.pageButtonRight.bottomButtons.setVisibility(View.VISIBLE);
            myView.onScreenInfo.info.setVisibility(View.VISIBLE);
            if (displayPrevNext.getShowPrev() || displayPrevNext.getShowNext()) {
                myView.nextPrevInfo.nextPrevInfoLayout.setVisibility(View.VISIBLE);
            }
            // Do this with a delay
            customAnimation.fadeActionButton(myView.pageButtonRight.actionFAB, themeColors.getPageButtonsSplitAlpha());
        }
    }
    @Override
    public void hideActionBar(boolean hide) {
        if (hide) {
            if (getSupportActionBar()!=null) {
                getSupportActionBar().hide();
            }
        } else {
            if (getSupportActionBar()!=null) {
                getSupportActionBar().show();
            }
        }
    }
    @Override
    public void updateToolbar(String what) {
        // Null titles are for the default song, author, etc.
        // Otherwise a new title is passed as a string
        windowFlags.setWindowFlags();
        appActionBar.setActionBar(this,mainActivityInterface, what);

        if (what!=null || !preferences.getMyPreferenceBoolean(this,"hideActionBar",false)) {
            // Make sure the content shows below the action bar
            myView.fragmentView.setTop(actionBar.getHeight());
        }
    }
    @Override
    public void updateActionBarSettings(String prefName, int intval, float floatval, boolean isvisible) {
        // If the user changes settings from the ActionBarSettingsFragment, they get sent here to deal with
        // So let's pass them on to the AppActionBar helper
        appActionBar.updateActionBarSettings(this,mainActivityInterface,prefName,intval,floatval,isvisible);
    }
    @Override
    public void showTutorial(String what) {
        if (settingsButton==null) {
            invalidateOptionsMenu();
        }
        initialiseArrayLists();
        switch (what) {
            case "performanceView":
                // Try to get the hamburger icon
                Log.d(TAG, "Trying to get hamburger icon: ");
                if (myView.toolBar.getRoot().getChildCount() > 2) {
                    final View view = myView.toolBar.getRoot().getChildAt(2);
                    targets.add(view);
                    infos.add("Open the menu to view and manage your songs and sets");
                }

                for (int i = 0; i < myView.toolBar.getRoot().getChildCount(); ++i) {
                    final View child = myView.toolBar.getRoot().getChildAt(i);
                    if (child != null && child.getClass().toString().contains("ImageView")) {
                        targets.add(child);
                        infos.add("Open the menu to view and manage your songs and sets");
                    }
                }

                targets.add(findViewById(R.id.menuSettings));
                infos.add(getString(R.string.extra_settings));
                targets.add(myView.pageButtonRight.actionFAB);
                infos.add(getString(R.string.action_button_info));
                dismisses.add(null);
                dismisses.add(null);
                dismisses.add(null);
                rects.add(false);
                rects.add(false);
                rects.add(false);
                showCase.sequenceShowCase(this, targets, dismisses, infos, rects, "performanceMode");

                break;
            case "songsetMenu":
                // Initialise the arraylists
                initialiseArrayLists();
                targets.add(Objects.requireNonNull(myView.menuTop.tabs.getTabAt(0)).view);
                targets.add(Objects.requireNonNull(myView.menuTop.tabs.getTabAt(1)).view);
                targets.add(Objects.requireNonNull(myView.viewpager.findViewById(R.id.actionFAB)));
                infos.add(getString(R.string.menu_song_info));
                infos.add(getString(R.string.menu_set_info));
                infos.add(getString(R.string.add_songs) + " / " + getString(R.string.song_actions));
                dismisses.add(null);
                dismisses.add(null);
                dismisses.add(null);
                rects.add(true);
                rects.add(true);
                rects.add(false);
                showCase.sequenceShowCase(this, targets, dismisses, infos, rects, "songSetMenu");
        }
    }
    private void initialiseArrayLists() {
        targets = new ArrayList<>();
        dismisses = new ArrayList<>();
        infos = new ArrayList<>();
        rects = new ArrayList<>();
    }


    // Settings and options menus
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        settingsButton = menu.findItem(R.id.settings_menu_item);
        MenuItem alertButton = menu.findItem(R.id.alert_info_item);

        // Decide if an alert should be shown
        if (alertChecks.showBackup(this,
                preferences.getMyPreferenceInt(this,"runssincebackup",0)) ||
                alertChecks.showPlayServicesAlert(this) ||
                alertChecks.showUpdateInfo(this,versionNumber.getVersionCode(),
                        preferences.getMyPreferenceInt(this,"lastUsedVersion",0))) {
            alertButton.setVisible(true);
        } else if (alertButton!=null){
            alertButton.setVisible(false);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(TAG,item.toString());
        switch (item.toString()) {
            case "Settings":
                if (settingsOpen) {
                    navHome();
                } else {
                    navigateToFragment("opensongapp://preferences",0);
                }
                break;

            case "Information":
                AlertInfoBottomSheet alertInfoBottomSheet = new AlertInfoBottomSheet();
                openDialog(alertInfoBottomSheet,"Alerts");
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void refreshMenuItems() {
        invalidateOptionsMenu();
    }
    @Override
    public void fixOptionsMenu() {
        invalidateOptionsMenu();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mainactivitymenu, menu);

        // Setup the menu item for connecting to cast devices

        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            MenuItem mediaRouteMenuItem = CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), menu, R.id.media_route_menu_item);
        } else {
            Log.d(TAG, "Google Play Services Not Available");
            // TODO
            // Alert the user about the Google Play issues and give them an option to fix it
            // Add it to the menu alerts
        }

        // Set up battery monitor
        batteryStatus = new BatteryStatus();
        batteryStatus.setUpBatteryMonitor(this,myView.toolBar.digitalclock,
                myView.toolBar.batterycharge,
                myView.toolBar.batteryimage,actionBar);
        return true;
    }


    // The drawers and actionbars
    @Override
    public DrawerLayout getDrawer() {
        return myView.drawerLayout;
    }
    @Override
    public ActionBar getMyActionBar() {
        return actionBar;
    }
    @Override
    public void lockDrawer(boolean lock) {
        // This is done whenever we have a settings window open
        if (myView != null) {
            if (lock) {
                settingsOpen = true;
                myView.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            } else {
                settingsOpen = false;
                myView.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }
        }
    }
    @Override
    public void closeDrawer(boolean close) {
        if (close) {
            myView.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            myView.drawerLayout.openDrawer(GravityCompat.START);
        }
    }



    // The song and set menu
    private void setUpSongMenuTabs() {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), this.getLifecycle());
        viewPagerAdapter.createFragment(0);
        songMenuFragment = (SongMenuFragment) viewPagerAdapter.menuFragments[0];
        setMenuFragment = (SetMenuFragment) viewPagerAdapter.createFragment(1);
        viewPager = myView.viewpager;
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(1);
        // Disable the swiping gesture
        viewPager.setUserInputEnabled(false);
        TabLayout tabLayout = myView.menuTop.tabs;
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(getString(R.string.song));
                    tab.setIcon(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_music_note_white_36dp,null));
                    break;
                case 1:
                    tab.setText(getString(R.string.set));
                    tab.setIcon(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_format_list_numbers_white_36dp,null));
                    break;
            }
        }).attach();
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                showSetMenu = position != 0;
                super.onPageSelected(position);
            }
        });
        myView.menuTop.versionCode.setOnClickListener(v -> closeDrawer(true));
    }
    @Override
    public boolean getShowSetMenu() {
        return showSetMenu;
    }

    private boolean setSongMenuFragment() {
        runOnUiThread(() -> {
            if (songMenuFragment!=null) {
                if (showSetMenu) {
                    viewPager.setCurrentItem(1);
                } else {
                    viewPager.setCurrentItem(0);
                }
            }
        });
        return songMenuFragment != null;
    }
    @Override
    public void chooseMenu(boolean showSetMenu) {
        this.showSetMenu = showSetMenu;
        setSongMenuFragment();
        closeDrawer(myView.drawerLayout.isOpen());
    }
    @Override
    public void indexSongs() {
        Log.d(TAG,"indexSong() called");
        new Thread(() -> {
            runOnUiThread(() -> showToast.doIt(this,getString(R.string.search_index_start)));
            songListBuildIndex.setIndexComplete(false);
            songListBuildIndex.fullIndex(this,this);
            runOnUiThread(() -> {
                songListBuildIndex.setIndexRequired(false);
                songListBuildIndex.setIndexComplete(true);
                showToast.doIt(this,getString(R.string.search_index_end));
                updateSongMenu(song);
                updateFragment("set_updateKeys",null,null);
            });
        }).start();
    }
    @Override
    public void moveToSongInSongMenu() {
        Log.d(TAG,"trying to move to song in song menu");
        if (songMenuFragment!=null) {
            try {
                songMenuFragment.moveToSongInMenu(song);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "songMenuFragment not available");
        }
    }
    @Override
    public void refreshSetList() {
        if (setMenuFragment!=null) {
            setMenuFragment.prepareCurrentSet();
        }
    }
    @Override
    public void updateSongMenu(String fragName, Fragment callingFragment, ArrayList<String> arguments) {
        // If the fragName is menuSettingsFragment, we just want to change the alpha index view
        if (fragName!=null && fragName.equals("menuSettingsFragment")) {
            if (songMenuFragment!=null) {
                songMenuFragment.changeAlphabeticalLayout();
            }
        } else {
            // This is a full rebuild
            // If sent called from another fragment the fragName and callingFragment are used to run an update listener
            songListBuildIndex.setIndexComplete(false);
            // Get all of the files as an array list
            ArrayList<String> songIds = storageAccess.listSongs(this, this);
            // Write this to text file
            storageAccess.writeSongIDFile(this, this, songIds);
            // Try to create the basic databases
            sqLiteHelper.resetDatabase(this);
            nonOpenSongSQLiteHelper.initialise(this, this);
            // Add entries to the database that have songid, folder and filename fields
            // This is the minimum that we need for the song menu.
            // It can be upgraded asynchronously in StageMode/PresenterMode to include author/key
            // Also will later include all the stuff for the search index as well
            sqLiteHelper.insertFast(this, this);
            if (fragName != null) {
                //Update the fragment
                updateFragment(fragName, callingFragment, arguments);
            }
            // Now build it properly
            indexSongs();
        }
    }
    @Override
    public void updateSongMenu(Song song) {
        // This only asks for an update from the database
        songListBuildIndex.setIndexComplete(true);
        songListBuildIndex.setIndexRequired(false);
        if (setSongMenuFragment() && songMenuFragment!=null) {
            songMenuFragment.updateSongMenu(song);
        } else {
            Log.d(TAG, "songMenuFragment not available");
        }
    }
    @Override
    public int getPositionOfSongInMenu() {
        if (songMenuFragment!=null) {
            return songMenuFragment.getPositionInSongMenu(song);
        } else {
            return 0;
        }
    }
    @Override
    public Song getSongInMenu(int position) {
        if (position>-1 && songMenuFragment!=null && songMenuFragment.getSongsFound()!=null && songMenuFragment.getSongsFound().size()>position) {
            return songMenuFragment.getSongsFound().get(position);
        }
        return mainActivityInterface.getSong();
    }
    @Override
    public ArrayList<Song> getSongsInMenu() {
        if (songMenuFragment!=null) {
            return songMenuFragment.getSongsFound();
        } else {
            return new ArrayList<>();
        }
    }


    // Page buttons
    private void animatePageButtons() {
        float rotation = myView.pageButtonRight.actionFAB.getRotation();
        pageButtons.animatePageButton(this,rotation == 0);
    }
    @Override
    public void updatePageButtonLayout() {
        // We have changed something about the page buttons (or initialising them
        if (myView.pageButtonRight.actionFAB.getRotation()!=0) {
            pageButtons.animatePageButton(this,false);
        }
        pageButtons.updateColors(this);
        pageButtons.setPageButton(this, myView.pageButtonRight.actionFAB, -1, false);
        for (int x=0; x<6; x++) {
            pageButtons.setPageButton(this, pageButtons.getFAB(x), x, false);
        }
    }



    // Databases
    @Override
    public SQLiteHelper getSQLiteHelper() {
        return sqLiteHelper;
    }
    @Override
    public NonOpenSongSQLiteHelper getNonOpenSongSQLiteHelper() {
        return nonOpenSongSQLiteHelper;
    }
    @Override
    public CommonSQL getCommonSQL() {
        return commonSQL;
    }


    // Song actions
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void sendMidiFromList(int item) {
        if (currentFragment(R.id.midiFragment)) {
            ((MidiFragment)getFragmentFromId(R.id.midiFragment)).sendMidiFromList(item);
        }
    }
    @Override
    public void deleteMidiFromList(int item) {
        if (currentFragment(R.id.midiFragment)) {
            ((MidiFragment)getFragmentFromId(R.id.midiFragment)).deleteMidiFromList(item);
        }
    }
    @Override
    public void registerMidiAction(boolean actionDown, boolean actionUp, boolean actionLong, String note) {
        // If pedalsFragment is open, send the midiNote and event there
        if (currentFragment(R.id.pedalsFragment) && ((PedalsFragment)getFragmentFromId(R.id.pedalsFragment)).isListening()) {
            if (actionDown) {
                ((PedalsFragment) getFragmentFromId(R.id.pedalsFragment)).midiDownListener(note);
            } else if (actionUp) {
                ((PedalsFragment) getFragmentFromId(R.id.pedalsFragment)).commonEventUp();
            } else if (actionLong) {
                ((PedalsFragment) getFragmentFromId(R.id.pedalsFragment)).commonEventLong();
            }
        } else {
            if (actionDown) {
                pedalActions.commonEventDown(-1,note);
            } else if (actionUp) {
                pedalActions.commonEventUp(-1,note);
            } else if (actionLong) {
                pedalActions.commonEventLong(-1,note);
            }
        }
    }
    @Override
    public Midi getMidi(MainActivityInterface mainActivityInterface) {
        // First update the mainActivityInterface used in midi
        midi.setMainActivityInterface(mainActivityInterface);
        // Return a reference to midi
        return midi;
    }
    @Override
    public Midi getMidi() {
        return midi;
    }


    // Sticky notes
    @Override
    public void showSticky(boolean forceshow, boolean hide) {
        // Try to show the sticky note
        Log.d(TAG,"showSticky");
        if (!whichMode.equals("Presentation") && currentFragment(R.id.performanceFragment)) {
            try {
                ((PerformanceFragment) getFragmentFromId(R.id.performanceFragment)).dealWithStickyNotes(forceshow, hide);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    // Metronome
    @Override
    public void metronomeToggle() {
        if (metronome.metronomeValid()) {
            // Toggle the start or stop
            if (!metronome.getIsRunning()) {
                metronome.startMetronome(this,this,this);
            } else {
                metronome.stopMetronome(this);
            }
        } else {
            // Open up the metronome settings
            navigateToFragment("opensongapp://settings/actions/metronome",0);
        }
    }


    // CCLI
    @Override
    public CCLILog getCCLILog() {
        return ccliLog;
    }


    // Song processing


    // The getters for references to the helper classes also needed in fragments
    @Override
    public StorageAccess getStorageAccess() {
        return storageAccess;
    }
    @Override
    public Preferences getPreferences() {
        return preferences;
    }



    @Override
    public SetTypeFace getMyFonts() {
        return setTypeFace;
    }
    @Override
    public ThemeColors getMyThemeColors() {
        return themeColors;
    }
    @Override
    public ExportActions getExportActions() {
        return exportActions;
    }
    @Override
    public ConvertChoPro getConvertChoPro() {
        return convertChoPro;
    }
    @Override
    public ConvertOnSong getConvertOnSong() {
        return convertOnSong;
    }
    @Override
    public ConvertTextSong getConvertTextSong() {
        return convertTextSong;
    }
    @Override
    public ProcessSong getProcessSong() {
        return processSong;
    }
    @Override
    public Song getSong() {
        return song;
    }
    @Override
    public Song getIndexingSong() {
        return indexingSong;
    }
    @Override
    public Song getTempSong() {
        return tempSong;
    }
    @Override
    public PrepareFormats getPrepareFormats() {
        return prepareFormats;
    }
    @Override
    public TimeTools getTimeTools() {
        return timeTools;
    }
    @Override
    public DisplayPrevNext getDisplayPrevNext() {
        return displayPrevNext;
    }
    @Override
    public FragmentManager getMyFragmentManager() {
        return getSupportFragmentManager();
    }
    @Override
    public Bible getBible() {
        return bible;
    }
    @Override
    public CustomSlide getCustomSlide() {
        return customSlide;
    }
    @Override
    public PresentationCommon getPresentationCommon() {
        return presentationCommon;
    }



    // TODO Getters to finish

    // TODO Setters to finish














    @Override
    public void doSongLoad(String folder, String filename, boolean closeDrawer) {
        if (whichMode.equals("Presentation")) {
            if (presentationFragment!=null && presentationFragment.isAdded()) {
                presentationFragment.doSongLoad(folder,filename);
            } else {
                navigateToFragment(null,R.id.performanceFragment);
            }
        } else {
            if (performanceFragment!=null && performanceFragment.isAdded()) {
                performanceFragment.doSongLoad(folder,filename);
            } else {
                navigateToFragment(null,R.id.presentationFragment);
            }
        }
        closeDrawer(closeDrawer);
    }

    @Override
    public void loadSongFromSet(int position) {
        // Get the key of the set item
        String setKey = currentSet.getKey(position);
        String setFolder = currentSet.getFolder(position);
        String setFilename = currentSet.getFilename(position);
        String songKey;
        // Update the index in the set
        currentSet.setIndexSongInSet(position);

        // Get the song key (from the database)
        if (storageAccess.isSpecificFileExtension("imageorpdf",currentSet.getFilename(position))) {
            songKey = nonOpenSongSQLiteHelper.getKey(this,mainActivityInterface,setFolder,setFilename);
        } else {
            songKey = sqLiteHelper.getKey(this,mainActivityInterface,setFolder,setFilename);
        }
        Log.d(TAG,"setKey="+setKey+"  songKey="+songKey);
        Log.d(TAG,"loadSongFromSet() called");
        doSongLoad(setFolder,setFilename,true);
    }

    @Override
    public void updateKeyAndLyrics(Song song) {
        // This is called from the transpose class once it has done its work on the edit song fragment
        //editSongFragmentMain.updateKeyAndLyrics(song);
    }

    @Override
    public void showSaveAllowed(boolean saveAllowed) {
        if (currentFragment(R.id.editSongFragment)) {
            ((EditSongFragment) getFragmentFromId(R.id.editSongFragment)).showSaveAllowed(saveAllowed);
        }
    }

    @Override
    public void registerFragment(Fragment frag, String what) {
        switch (what) {
            case "Performance":
                performanceFragment = (PerformanceFragment) frag;
                break;
            case "Presentation":
                presentationFragment = (PresentationFragment) frag;
                break;
            case "EditSongFragment":
                editSongFragment = (EditSongFragment) frag;
                break;
            case "EditSongFragmentMain":
                editSongFragmentMain = (EditSongFragmentMain) frag;
                break;
            case "NearbyConnectionsFragment":
                nearbyConnectionsFragment = (NearbyConnectionsFragment) frag;
                break;
            case "SwipeFragment":
                swipeFragment = (SwipeFragment) frag;
                break;
            case "BootUpFragment":
                bootUpFragment = (BootUpFragment) frag;
                break;
        }
        registeredFragment = frag;
    }

    @Override
    public void displayAreYouSure(String what, String action, ArrayList<String> arguments, String fragName, Fragment callingFragment, Song song) {
        AreYouSureBottomSheet dialogFragment = new AreYouSureBottomSheet(what,action,arguments,fragName,callingFragment,song);
        dialogFragment.show(this.getSupportFragmentManager(), "areYouSure");
    }

    @Override
    public void confirmedAction(boolean agree, String what, ArrayList<String> arguments, String fragName, Fragment callingFragment, Song song) {
        if (agree) {
            boolean result = false;
            boolean allowToast = true;
            switch(what) {
                case "deleteSong":
                    result = storageAccess.doDeleteFile(this,mainActivityInterface,"Songs",
                            song.getFolder(), song.getFilename());
                    // Now remove from the SQL database
                    if (song.getFiletype().equals("PDF") || song.getFiletype().equals("IMG")) {
                        nonOpenSongSQLiteHelper.deleteSong(this,mainActivityInterface,song.getFolder(),song.getFilename());
                    } else {
                        sqLiteHelper.deleteSong(this, mainActivityInterface, song.getFolder(),song.getFilename());
                    }
                    // TODO
                    // Send a call to reindex?
                    break;

                case "ccliDelete":
                    Uri uri = storageAccess.getUriForItem(this,mainActivityInterface,"Settings","","ActivityLog.xml");
                    result = ccliLog.createBlankXML(this,mainActivityInterface,uri);
                    break;

                case "deleteItem":
                    // Folder and subfolder are passed in the arguments.  Blank arguments.get(2) /filenames mean folders
                    result = storageAccess.doDeleteFile(this,mainActivityInterface,arguments.get(0),arguments.get(1),arguments.get(2));
                    if (arguments.get(2).isEmpty() && arguments.get(0).equals("Songs") && (arguments.get(1).isEmpty()||arguments.get(1)==null)) {
                        // Emptying the entire songs foler, so need to recreate it on finish
                        storageAccess.createFolder(this,mainActivityInterface,"Songs","","");
                    }
                    //Rebuild the song index
                    updateSongMenu(fragName, callingFragment, arguments); // Passing the fragment allows an update to be sent to the calling fragment
                    break;

                case "deleteHighlighter":
                    // Try to send the info back to the highlighter edit fragment
                    try {
                        ((HighlighterEditFragment)callingFragment).doDelete(agree);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    allowToast = false;
                    break;

                case "exit":
                    // Close the app.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        finishAndRemoveTask();
                    } else {
                        finishAffinity();
                    }
                    allowToast = false;
                    break;

                case "newSet":
                    // Clear the current set
                    currentSet.initialiseTheSet();
                    preferences.setMyPreferenceString(this, "setCurrentLastName", "");
                    updateFragment("set_updateView",null,null);
                    result = true;
                    break;

                case "removeThemeTag":
                    // We are about to remove tags from songs.  This is done in the EditSong framgment
                    updateFragment("confirmed_" + fragName, callingFragment, arguments);
                    allowToast = false;
                    break;
            }
            if (allowToast && result) {
                // Don't show toast for exit, but other successful actions
                showToast.doIt(this,getString(R.string.success));
            } else if (allowToast){
                showToast.doIt(this,getString(R.string.error));
            }
        }
    }

    @Override
    public void refreshAll() {
        Log.d(TAG,"refreshAll() called");
    }

    @Override
    public void doExport(String what) {
        Intent intent;
        switch (what) {
            case "ccliLog":
                intent = exportFiles.exportActivityLog(this, mainActivityInterface);
                startActivityForResult(Intent.createChooser(intent, "ActivityLog.xml"), 2222);
        }
    }



    @Override
    public void openDialog(BottomSheetDialogFragment frag, String tag) {
        try {
            frag.show(getSupportFragmentManager(), tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean songChanged() {
        return !song.equals(tempSong);
    }

    @Override
    public void updateSetList() {
        updateFragment("set_updateView",null,null);
    }

    @Override
    public void toggleAutoscroll() {
        if (autoscroll.getIsPaused()) {
            // This sets to the opposite, so un-paused
            autoscroll.pauseAutoscroll();
        } else if (mainActivityInterface.getAutoscroll().getIsAutoscrolling()) {
            autoscroll.stopAutoscroll();
        } else {
            autoscroll.startAutoscroll();
        }
    }

    @Override
    public void fadeoutPad() {
        Log.d(TAG,"fadeoutPad()");
    }
    @Override
    public Pad getPad() {
        return pad;
    }
    @Override
    public boolean playPad() {
        // If the pad is playing, stop else start
        if (pad.isPadPlaying()) {
            Log.d(TAG, "stopPad()");
            pad.stopPad(this);
            return false;
        } else {
            Log.d(TAG, "playPad()");
            pad.startPad(this);
            // Showcase if required
            mainActivityInterface.getShowCase().singleShowCase(this,myView.onScreenInfo.pad,getString(R.string.ok),getString(R.string.pad_playback_info),true,"padPlayback");
            return true;
        }
    }







    @Override
    public void installPlayServices() {
        Snackbar.make(myView.drawerLayout, R.string.play_services_error,
                BaseTransientBottomBar.LENGTH_LONG).setAction(R.string.play_services_how, v -> {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.play_services_help)));
            startActivity(i);
        }).show();
    }



    @Override
    public void fullIndex() {
        Log.d(TAG,"fullIndex() called");
        if (fullIndexRequired) {
            showToast.doIt(this,getString(R.string.search_index_start));
            new Thread(() -> {
                String outcome = songListBuildIndex.fullIndex(this,this);
                Log.d(TAG,"index done");
                if (songMenuFragment!=null) {
                    try {
                        songMenuFragment.updateSongMenu(song);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(() -> {
                    if (!outcome.isEmpty()) {
                        showToast.doIt(this,outcome.trim());
                    }
                    updateFragment("set_updateKeys",null,null);
                });

            }).start();
        }
    }

    @Override
    public void quickSongMenuBuild() {
        fullIndexRequired = true;
        ArrayList<String> songIds = new ArrayList<>();
        try {
            songIds = storageAccess.listSongs(this, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Write a crude text file (line separated) with the song Ids (folder/file)
        storageAccess.writeSongIDFile(this, this, songIds);

        // Try to create the basic databases
        // Non persistent, created from storage at boot (to keep updated) used to references ALL files
        sqLiteHelper.resetDatabase(this);
        // Persistent containing details of PDF/Image files only.  Pull in to main database at boot
        // Updated each time a file is created, deleted, moved.
        // Also updated when feature data (pad, autoscroll, metronome, etc.) is updated for these files
        nonOpenSongSQLiteHelper.initialise(this, this);

        // Add entries to the database that have songid, folder and filename fields
        // This is the minimum that we need for the song menu.
        // It can be upgraded asynchronously in StageMode/PresenterMode to include author/key
        // Also will later include all the stuff for the search index as well
        sqLiteHelper.insertFast(this, this);
    }

    @Override
    public void setFullIndexRequired(boolean fullIndexRequired) {
        this.fullIndexRequired = fullIndexRequired;
    }





    @Override
    public void setSong(Song song) {
        this.song = song;
    }
    @Override
    public void setIndexingSong(Song indexingSong) {
        this.indexingSong = indexingSong;
    }
    @Override
    public void setTempSong(Song tempSong) {
        this.tempSong = tempSong;
    }






    @Override
    public PedalActions getPedalActions() {
        return pedalActions;
    }

    @Override
    public Gestures getGestures() {
        return gestures;
    }

    @Override
    public PerformanceGestures getPerformanceGestures() {
        return performanceGestures;
    }

    @Override
    public DoVibrate getDoVibrate() {
        return doVibrate;
    }

    @Override
    public String getImportFilename() {
        return importFilename;
    }
    @Override
    public Uri getImportUri() {
        return importUri;
    }
    @Override
    public void setImportFilename(String importFilename) {
        this.importFilename = importFilename;
    }
    @Override
    public void setImportUri(Uri importUri) {
        this.importUri = importUri;
    }

    @Override
    public WebDownload getWebDownload() {
        return webDownload;
    }

    @Override
    public ShowToast getShowToast() {
        return showToast;
    }
    @Override
    public String getMode() {
        return whichMode;
    }
    @Override
    public void setMode(String whichMode) {
        this.whichMode = whichMode;
    }


    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public CurrentSet getCurrentSet() {
        return currentSet;
    }
    @Override
    public SetActions getSetActions() {
        return setActions;
    }
    @Override
    public LoadSong getLoadSong() {
        return loadSong;
    }
    @Override
    public SaveSong getSaveSong() {
        return saveSong;
    }


    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public String getWhattodo() {
        return whattodo;
    }

    @Override
    public void setWhattodo(String whattodo) {
        this.whattodo = whattodo;
    }

    @Override
    public PageButtons getPageButtons() {
        return pageButtons;
    }

    @Override
    public Autoscroll getAutoscroll() {
        return autoscroll;
    }



    @Override
    public Metronome getMetronome() {
        return metronome;
    }

    @Override
    public SongListBuildIndex getSongListBuildIndex() {
        return songListBuildIndex;
    }

    @Override
    public CustomAnimation getCustomAnimation() {
        return customAnimation;
    }
    @Override
    public PDFSong getPDFSong() {
        return pdfSong;
    }
    @Override
    public ShowCase getShowCase() {
        return showCase;
    }
    @Override
    public OCR getOCR() {
        return ocr;
    }
    @Override
    public MakePDF getMakePDF() {
        return makePDF;
    }
    @Override
    public VersionNumber getVersionNumber() {
        return versionNumber;
    }
    @Override
    public Transpose getTranspose() {
        return transpose;
    }
    @Override
    public AppActionBar getAppActionBar() {
        return appActionBar;
    }
    @Override
    public Swipes getSwipes() {
        return swipes;
    }

    @Override
    public int getFragmentOpen() {
        return getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getFragments().size()-1).getId();
    }


    @Override
    public void setScreenshot(Bitmap bitmap) {
        screenShot = Bitmap.createBitmap(bitmap);
        bitmap.recycle();
    }

    @Override
    public Bitmap getScreenshot() {
        return screenShot;
    }

    @Override
    public ABCNotation getAbcNotation() {
        return abcNotation;
    }

    @Override
    public AlertChecks getAlertChecks() {
        return alertChecks;
    }

    @Override
    public void setMainActivityInterface(MainActivityInterface mainActivityInterface) {
        this.mainActivityInterface = mainActivityInterface;
    }

    @Override
    public MainActivityInterface getMainActivityInterface() {
        return this;
    }

    @Override
    public DrawNotes getDrawNotes() {
        return drawNotes;
    }

    @Override
    public void setDrawNotes(DrawNotes view) {
        drawNotes = view;
    }

    @Override
    public ProfileActions getProfileActions() {
        return profileActions;
    }

    @Override
    public CheckInternet getCheckInternet() {
        return checkInternet;
    }

    @Override
    public void isWebConnected(Fragment fragment, int fragId, boolean isConnected) {
        // This is the result of an internet connection check
        if (fragment!=null) {
            try {
                if (fragId==R.id.fontSetupFragment) {
                    ((FontSetupFragment) fragment).isConnected(isConnected);
                } else if (fragId==R.id.importOnlineFragment) {
                    ((ImportOnlineFragment) fragment).isConnected(isConnected);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void songSelectDownloadPDF(Fragment fragment, int fragId, Uri uri) {
        if (fragment!=null && fragId==R.id.importOnlineFragment) {
            try {
                ((ImportOnlineFragment)fragment).finishedDownloadPDF(uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }





    @Override
    public void openDocument(String guideId, String location) {
// I could pass the address in as a location string,
        // However, for the user-guide to avoid having to change loads of files
        // I keep them listed here.
        if (guideId!=null && !guideId.isEmpty()) {
            switch (guideId) {
                case "mode":
                    location = "https://www.opensongapp.com/user-guide/the-app-modes";
                    break;
                case "storage":
                    location = "https://www.opensongapp.com/user-guide/setting-up-and-using-opensongapp/setting-up-opensong-tablet";
                    break;
            }
        }

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (location.startsWith("http")) {
                intent.setData(Uri.parse(location));
            } else {
                String mimeType = null;
                if (location.contains(".")) {
                    String extension = location.substring(location.lastIndexOf(".") + 1);
                    Log.d(TAG,"location: "+location);
                    MimeTypeMap myMime = MimeTypeMap.getSingleton();
                    mimeType= myMime.getMimeTypeFromExtension(extension);
                }
                if (mimeType == null) {
                    mimeType = "*/*";
                }
                Uri uri = Uri.parse(location);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Log.d(TAG,"mimeType: "+mimeType);
                intent.setDataAndType(uri,mimeType);
            }

            startActivity(intent);
        } catch (ActivityNotFoundException nf) {
            // No suitable application to open the document
            showToast.doIt(this,getString(R.string.no_suitable_application));
            nf.printStackTrace();

        } catch (Exception e) {
            // Probably no browser installed or no internet permission given.
            e.printStackTrace();
        }
    }



    @Override
    public void setSectionViews(ArrayList<View> views) {
        if (views==null) {
            // Reset the views and their sizes
            sectionViews = null;
            sectionViews = new ArrayList<>();
            sectionWidths = null;
            sectionWidths = new ArrayList<>();
            sectionHeights = null;
            sectionHeights = new ArrayList<>();
        } else {
            sectionViews = views;
        }
    }

    @Override
    public ArrayList<View> getSectionViews() {
        return sectionViews;
    }

    @Override
    public ArrayList<Integer> getSectionWidths() {
        return sectionWidths;
    }

    @Override
    public ArrayList<Integer> getSectionHeights() {
        return sectionHeights;
    }

    @Override
    public void addSectionSize(int width, int height) {
        if (sectionWidths==null) {
            sectionWidths = new ArrayList<>();
        }
        if (sectionHeights==null) {
            sectionHeights = new ArrayList<>();
        }
        sectionWidths.add(width);
        sectionHeights.add(height);
    }

    @Override
    public void setSongSheetTitleLayout(LinearLayout linearLayout) {
        if (songSheetTitleLayout==null) {
            songSheetTitleLayout = new LinearLayout(this);
        }
        if (linearLayout==null) {
            // Remove the views
            songSheetTitleLayout.removeAllViews();
            songSheetTitleLayoutSize = new ArrayList<>();
        } else {
            songSheetTitleLayout.addView(linearLayout);
        }
    }

    @Override
    public LinearLayout getSongSheetTitleLayout() {
        if (songSheetTitleLayout==null) {
            songSheetTitleLayout = new LinearLayout(this);
        }
        return songSheetTitleLayout;
    }

    @Override
    public SongSheetHeaders getSongSheetHeaders() {
        return songSheetHeaders;
    }

    @Override
    public ArrayList<Integer> getSongSheetTitleLayoutSize() {
        return songSheetTitleLayoutSize;
    }

    @Override
    public void setSongSheetTitleLayoutSize(ArrayList<Integer> sizes) {
        songSheetTitleLayoutSize = sizes;
    }

    @Override
    public void enableSwipe(boolean canSwipe) {
        if (currentFragment(R.id.editSongFragment)) {
            ((EditSongFragment) getFragmentFromId(R.id.editSongFragment)).enableSwipe(canSwipe);
        }
    }

    @Override
    public ArrayList<Song> getSongsFound(String whichMenu) {
        switch (whichMenu) {
            case "song":
                if (songMenuFragment != null) {
                    try {
                        return songMenuFragment.getSongsFound();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case "set":
                try {
                    return currentSet.getSetSongObject();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        return new ArrayList<>();
    }





    @Override
    public void toggleMetronome() {
        if (metronome.getIsRunning()) {
            metronome.stopMetronome(mainActivityInterface);
        } else {
            metronome.startMetronome(this,this,mainActivityInterface);
        }
    }






    @Override
    public void updateSizes(int width, int height) {
        if (whichMode.equals("Performance") && currentFragment(R.id.performanceFragment)) {
            ((PerformanceFragment) getFragmentFromId(R.id.performanceFragment)).updateSizes(width, height);
        }
    }





    @Override
    public void gesture5() {
        // TODO
    }

    @Override
    public void selectSection(int i) {
        // TODO
    }

    @Override
    public void prepareSongMenu() {
        // TODO
    }

    @Override
    public void loadSong() {
        doSongLoad(song.getFolder(),song.getFilename(),true);
    }

    @Override
    public void goToPreviousItem() {
        // TODO
    }

    @Override
    public void goToNextItem() {
        // TODO
    }

    @Override
    public void updateValue(Fragment fragment, String fragname, String which, String value) {
        // This takes the info from the TextInputBottomSheet and passes back to the calling fragment
        Log.d(TAG, "fragment: "+fragment);
        Log.d(TAG, "fragname: "+fragname);
        Log.d(TAG, "value: "+value);
        if (fragment!=null) {
            try {
                switch (fragname) {
                    case "SettingsCCLI":
                        ((SettingsCCLI) fragment).updateValue(which, value);
                        break;
                    case "NearbyConnectionsFragment":
                        ((NearbyConnectionsFragment) fragment).updateValue(which, value);
                        break;
                    case "SetManageFragment":
                        ((SetManageFragment) fragment).updateValue(which, value);
                        break;
                    case "EditSongFragmentMain":
                        ((EditSongFragmentMain) fragment).updateValue(value);
                        break;
                    case "CustomChordsFragment":
                        ((CustomChordsFragment) fragment).updateValue(value);
                        break;
                    case "SetActionsFragment":
                        ((SetActionsFragment) fragment).updateValue(value);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void getSwipeValues(int minDistance, int minHeight, int minTime) {
        if (currentFragment(R.id.swipeFragment)) {
            try {
                ((SwipeFragment) getFragmentFromId(R.id.swipeFragment)).getSwipeValues(minDistance, minHeight, minTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    // Get permissions request callback
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            switch (requestCode) {
                case 5503:
                    //startCamera();
                    break;

                case 404:
                case 403:
                    // Access coarse/fine location, so can open the menu at 'Connect devices'
                    // The following checks we have both before navigating
                    Log.d("d", "LOCATION granted!");
                    if (whattodo!=null && whattodo.equals("nearby")) {
                        openNearbyFragment();
                    }
                    break;
            }
        }
    }





    @Override
    public void onStart() {
        super.onStart();
        // Deal with the Cast logic
        // TODO
        /*if (mediaRouter != null && mediaRouteSelector != null) {
            try {
                mediaRouter.addCallback(mediaRouteSelector, mediaRouterCallback,
                        MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
        // Fix the page flags
        setWindowFlags();
    }
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Log.d(TAG,"Configuration changed");
        // Get the language
        fixLocale.setLocale(this,mainActivityInterface);

        // Save a static variable that we have rotated the screen.
        // The media player will look for this.  If found, it won't restart when the song loads
        pad.setOrientationChanged(pad.getCurrentOrientation()!=newConfig.orientation);
        // If orientation has changed, we need to reload the song to get it resized.
        if (pad.getOrientationChanged()) {
            // Set the current orientation
            pad.setCurrentOrientation(newConfig.orientation);
            doSongLoad(song.getFolder(),song.getFilename(),true);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        //setUpCast();

        // Fix the page flags
        setWindowFlags();
    }
    @Override
    protected void onPause() {
        super.onPause();
        endCastState();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        nearbyConnections.turnOffNearby(this);
    }
    @Override
    public void onStop() {
        super.onStop();
        // TODO
        try {
            //mediaRouter.removeCallback(mediaRouterCallback);
        } catch (Exception e) {
            Log.d("StageMode", "Problem removing mediaroutercallback");
        }
        if (batteryStatus!=null) {
            try {
                this.unregisterReceiver(batteryStatus);
            } catch (Exception e) {
                Log.d(TAG, "Battery receiver not registered, so no need to unregister");
            }
        }
    }
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {}
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // Set the fullscreen window flags]
        if (hasFocus) {
            setWindowFlags();
            //appActionBar.showActionBar(settingsOpen);
        }
    }

    @Override
    public void setUpBatteryMonitor() {

    }

    @Override
    public int[] getDisplayMetrics() {
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        int[] displayMetrics = new int[2];
        displayMetrics[1] = metrics.heightPixels;
        displayMetrics[0] = metrics.widthPixels;
        displayMetrics[0] = getWindow().getDecorView().getWidth();
        displayMetrics[1] = getWindow().getDecorView().getHeight();
        return displayMetrics;
    }
}