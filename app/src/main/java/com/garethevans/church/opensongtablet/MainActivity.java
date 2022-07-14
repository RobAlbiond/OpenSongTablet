package com.garethevans.church.opensongtablet;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.hardware.display.DisplayManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.WindowCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import com.garethevans.church.opensongtablet.appdata.CheckInternet;
import com.garethevans.church.opensongtablet.appdata.FixLocale;
import com.garethevans.church.opensongtablet.appdata.SetTypeFace;
import com.garethevans.church.opensongtablet.appdata.SoftKeyboard;
import com.garethevans.church.opensongtablet.appdata.VersionNumber;
import com.garethevans.church.opensongtablet.autoscroll.Autoscroll;
import com.garethevans.church.opensongtablet.bible.Bible;
import com.garethevans.church.opensongtablet.ccli.CCLILog;
import com.garethevans.church.opensongtablet.ccli.SettingsCCLI;
import com.garethevans.church.opensongtablet.chords.ChordDirectory;
import com.garethevans.church.opensongtablet.chords.ChordDisplayProcessing;
import com.garethevans.church.opensongtablet.chords.CustomChordsFragment;
import com.garethevans.church.opensongtablet.chords.Transpose;
import com.garethevans.church.opensongtablet.controls.Gestures;
import com.garethevans.church.opensongtablet.controls.PageButtons;
import com.garethevans.church.opensongtablet.controls.PedalActions;
import com.garethevans.church.opensongtablet.controls.PedalsFragment;
import com.garethevans.church.opensongtablet.controls.SwipeFragment;
import com.garethevans.church.opensongtablet.controls.Swipes;
import com.garethevans.church.opensongtablet.customslides.CustomSlide;
import com.garethevans.church.opensongtablet.customslides.CustomSlideFragment;
import com.garethevans.church.opensongtablet.customviews.DrawNotes;
import com.garethevans.church.opensongtablet.customviews.GlideApp;
import com.garethevans.church.opensongtablet.databinding.ActivityBinding;
import com.garethevans.church.opensongtablet.export.ExportActions;
import com.garethevans.church.opensongtablet.export.PrepareFormats;
import com.garethevans.church.opensongtablet.filemanagement.AreYouSureBottomSheet;
import com.garethevans.church.opensongtablet.filemanagement.LoadSong;
import com.garethevans.church.opensongtablet.filemanagement.SaveSong;
import com.garethevans.church.opensongtablet.filemanagement.StorageAccess;
import com.garethevans.church.opensongtablet.filemanagement.StorageManagementFragment;
import com.garethevans.church.opensongtablet.highlighter.HighlighterEditFragment;
import com.garethevans.church.opensongtablet.importsongs.ImportOnlineFragment;
import com.garethevans.church.opensongtablet.importsongs.WebDownload;
import com.garethevans.church.opensongtablet.interfaces.ActionInterface;
import com.garethevans.church.opensongtablet.interfaces.DialogReturnInterface;
import com.garethevans.church.opensongtablet.interfaces.DisplayInterface;
import com.garethevans.church.opensongtablet.interfaces.EditSongFragmentInterface;
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
import com.garethevans.church.opensongtablet.performance.DisplayPrevNext;
import com.garethevans.church.opensongtablet.performance.PerformanceFragment;
import com.garethevans.church.opensongtablet.performance.PerformanceGestures;
import com.garethevans.church.opensongtablet.preferences.Preferences;
import com.garethevans.church.opensongtablet.preferences.ProfileActions;
import com.garethevans.church.opensongtablet.presenter.PresenterFragment;
import com.garethevans.church.opensongtablet.presenter.PresenterSettings;
import com.garethevans.church.opensongtablet.presenter.SongSectionsFragment;
import com.garethevans.church.opensongtablet.screensetup.AppActionBar;
import com.garethevans.church.opensongtablet.screensetup.BatteryStatus;
import com.garethevans.church.opensongtablet.screensetup.DoVibrate;
import com.garethevans.church.opensongtablet.screensetup.FontSetupFragment;
import com.garethevans.church.opensongtablet.screensetup.ShowToast;
import com.garethevans.church.opensongtablet.screensetup.ThemeColors;
import com.garethevans.church.opensongtablet.screensetup.ThemeSetupFragment;
import com.garethevans.church.opensongtablet.screensetup.WindowFlags;
import com.garethevans.church.opensongtablet.secondarydisplay.SecondaryDisplay;
import com.garethevans.church.opensongtablet.secondarydisplay.SecondaryDisplaySettingsFragment;
import com.garethevans.church.opensongtablet.setmenu.SetMenuFragment;
import com.garethevans.church.opensongtablet.setprocessing.CurrentSet;
import com.garethevans.church.opensongtablet.setprocessing.SetActions;
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
import com.garethevans.church.opensongtablet.songprocessing.SongActionsMenuFragment;
import com.garethevans.church.opensongtablet.songprocessing.SongSheetHeaders;
import com.garethevans.church.opensongtablet.sqlite.CommonSQL;
import com.garethevans.church.opensongtablet.sqlite.NonOpenSongSQLiteHelper;
import com.garethevans.church.opensongtablet.sqlite.SQLiteHelper;
import com.garethevans.church.opensongtablet.utilities.TimeTools;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

// TODO TIDY UP
public class MainActivity extends AppCompatActivity implements MainActivityInterface,
        ActionInterface, NearbyInterface, NearbyReturnActionsInterface, DialogReturnInterface,
        MidiAdapterInterface, SwipeDrawingInterface, BatteryStatus.MyInterface,
        DisplayInterface, EditSongFragmentInterface {

    private ActivityBinding myView;

    // The helpers sorted alphabetically
    private ABCNotation abcNotation;
    private AlertChecks alertChecks;
    private AppActionBar appActionBar;
    private Autoscroll autoscroll;
    private Bible bible;
    private CCLILog ccliLog;
    private CheckInternet checkInternet;
    private ChordDirectory chordDirectory;
    private ChordDisplayProcessing chordDisplayProcessing;
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
    private FixLocale fixLocale;
    private Gestures gestures;
    private LoadSong loadSong;
    private MakePDF makePDF;
    private Metronome metronome;
    private Midi midi;
    private NearbyConnections nearbyConnections;
    private NonOpenSongSQLiteHelper nonOpenSongSQLiteHelper;
    private OCR ocr;
    private Pad pad;
    private PageButtons pageButtons;
    private PedalActions pedalActions;
    private PerformanceGestures performanceGestures;
    private Preferences preferences;
    private PrepareFormats prepareFormats;
    private PresenterSettings presenterSettings;
    private ProcessSong processSong;
    private ProfileActions profileActions;
    private SaveSong saveSong;
    private SetActions setActions;
    private SetTypeFace setTypeFace;
    private ShowCase showCase;
    private ShowToast showToast;
    private SoftKeyboard softKeyboard;
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
    private SongMenuFragment songMenuFragment;
    private SetMenuFragment setMenuFragment;
    private PerformanceFragment performanceFragment;
    private PresenterFragment presenterFragment;
    private EditSongFragment editSongFragment;
    private NearbyConnectionsFragment nearbyConnectionsFragment;
    private PedalsFragment pedalsFragment;
    private ViewPager2 viewPager;
    private ActionBar actionBar;
    private AppBarConfiguration appBarConfiguration;
    private SecondaryDisplay[] secondaryDisplays;
    private Display[] connectedDisplays;
    private int prevNumConnectedDisplays = 0;
    private ImageView screenMirror;

    // Variables used
    private ArrayList<View> targets;
    private ArrayList<String> infos;
    private ArrayList<Boolean> rects;
    private ArrayList<View> sectionViews;
    private LinearLayout songSheetTitleLayout;
    private ArrayList<Integer> sectionWidths, sectionHeights, songSheetTitleLayoutSize, sectionColors;
    private String whichMode, whattodo, importFilename;
    private Uri importUri;
    private boolean settingsOpen = false, showSetMenu,
            pageButtonActive = true, fullIndexRequired, menuOpen, firstRun=true;
    private final String TAG = "MainActivity";
    private MenuItem settingsButton;
    private Locale locale;
    private Bitmap screenShot;

    // Set up the activity
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        supportRequestWindowFeature(AppCompatDelegate.FEATURE_ACTION_MODE_OVERLAY);

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
    }

    private void setupHelpers() {
        storageAccess = new StorageAccess(this);
        preferences = new Preferences(this);
        softKeyboard = new SoftKeyboard();

        SoftKeyboard.SoftKeyBoardStatusListener listener = new SoftKeyboard.SoftKeyBoardStatusListener() {
            @Override
            public void onKeyBoardShow(View rootView, int totalScreenHeight) {
                setWindowFlags(false);
            }

            @Override
            public void onKeyBoardHide(View rootView, int totalScreenHeight) {
                setWindowFlags(true);
            }
        };
        softKeyboard.assistActivity(this, listener);


        // The song stuff
        songListBuildIndex = new SongListBuildIndex(this);

        // The screen display stuff
        customAnimation = new CustomAnimation();
        showCase = new ShowCase();
        showToast = new ShowToast(this,myView.fragmentView);

        // The app setup
        versionNumber = new VersionNumber();
        fixLocale = new FixLocale();
        checkInternet = new CheckInternet();
        nearbyConnections = new NearbyConnections(this);
        doVibrate = new DoVibrate();
        customAnimation = new CustomAnimation();
        webDownload = new WebDownload();
        alertChecks = new AlertChecks(this);

        // For user preferences
        setTypeFace = new SetTypeFace(this);
        themeColors = new ThemeColors(this);
        profileActions = new ProfileActions(this);

        // The databases
        sqLiteHelper = new SQLiteHelper(this);
        nonOpenSongSQLiteHelper = new NonOpenSongSQLiteHelper(this);
        commonSQL = new CommonSQL(this);

        // Converting song formats and processing song content
        chordDisplayProcessing = new ChordDisplayProcessing(this);
        chordDirectory = new ChordDirectory();
        convertChoPro = new ConvertChoPro(this);
        convertOnSong = new ConvertOnSong(this);
        convertTextSong = new ConvertTextSong(this);
        processSong = new ProcessSong(this);
        prepareFormats = new PrepareFormats(this);
        songSheetHeaders = new SongSheetHeaders(this);
        ocr = new OCR(this);
        makePDF = new MakePDF(this);
        transpose = new Transpose(this);
        abcNotation = new ABCNotation();
        song = new Song();

        // Loading up songs and the indexing
        loadSong = new LoadSong(this);
        saveSong = new SaveSong(this);

        // Sets
        currentSet = new CurrentSet();
        setActions = new SetActions(this);

        // Song actions/features
        performanceGestures = new PerformanceGestures(this);
        pageButtons = new PageButtons(this);
        midi = new Midi(this);
        pedalActions = new PedalActions(this);
        pad = new Pad(this, myView.onScreenInfo.getPad());
        autoscroll = new Autoscroll(this,myView.onScreenInfo.getAutoscrollTime(),
                myView.onScreenInfo.getAutoscrollTotalTime(),myView.onScreenInfo.getAutoscroll());
        metronome = new Metronome(this);
        gestures = new Gestures(this);
        swipes = new Swipes(this);
        timeTools = new TimeTools();
        displayPrevNext = new DisplayPrevNext(this,myView.nextPrevInfo.nextPrevInfoLayout,
                myView.nextPrevInfo.prevButton, myView.nextPrevInfo.nextButton);

        // Other file actions
        ccliLog = new CCLILog(this);
        exportActions = new ExportActions(this);
        bible = new Bible(this);
        customSlide = new CustomSlide(this);
        presenterSettings = new PresenterSettings(this);
        //mediaRouterCallback = new MediaRouterCallback(this);
    }

    @Override
    public BatteryStatus getBatteryStatus() {
        return batteryStatus;
    }

    private void setupActionbar() {
        setSupportActionBar(myView.toolBar.myToolbar);
            actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void showHideActionBar() {
        // This moves the content depending on the actionbar height (0 if autohide)
        if (settingsOpen) {
            myView.fragmentView.setPadding(0,actionBar.getHeight(),0,0);
        } else {
            myView.fragmentView.setPadding(0,appActionBar.getActionBarHeight(),0,0);
        }
        appActionBar.showActionBar(settingsOpen||menuOpen);
    }

    private void setupViews() {
        windowFlags = new WindowFlags(this.getWindow());
        appActionBar = new AppActionBar(this,this,actionBar, myView.toolBar.myToolbar,
                myView.toolBar.inSet, myView.toolBar.songtitleAb, myView.toolBar.songauthorAb,
                myView.toolBar.songkeyAb, myView.toolBar.songcapoAb, myView.toolBar.digitalclock,
                myView.toolBar.webHelp);
        pageButtons.setMainFABS(
                myView.pageButtonRight.actionFAB, myView.pageButtonRight.custom1Button,
                myView.pageButtonRight.custom2Button,myView.pageButtonRight.custom3Button,
                myView.pageButtonRight.custom4Button,myView.pageButtonRight.custom5Button,
                myView.pageButtonRight.custom6Button,myView.pageButtonRight.bottomButtons);
        pageButtons.animatePageButton(false);
    }

    @Override
    public void setFirstRun(boolean firstRun) {
        this.firstRun = firstRun;
    }
    @Override
    public boolean getFirstRun() {
        return firstRun;
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
        setUpBatteryMonitor();

        // Set up the page buttons
        updatePageButtonLayout();

        // Set up nearby
        setupNearby();

    }
    private void initialiseStartVariables() {
        themeColors.setThemeName(preferences.getMyPreferenceString("appTheme", "dark"));
        whichMode = preferences.getMyPreferenceString("whichMode", "Performance");

        // Song location
        song.setFilename(preferences.getMyPreferenceString("songfilename","Welcome to OpenSongApp"));
        song.setFolder(preferences.getMyPreferenceString("whichSongFolder", getString(R.string.mainfoldername)));

        // Set dealt with elsewhere
        setActions.preferenceStringToArrays();

        // Set the locale
        fixLocale.setLocale(this,this);
        locale = fixLocale.getLocale();

        // ThemeColors
        themeColors.getDefaultColors();

        // Typefaces
        setTypeFace.setUpAppFonts(new Handler(),new Handler(),new Handler(),new Handler(),new Handler());
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
                    if (!whichMode.equals("Presenter")) {
                        hideActionButton(slideOffset > initialVal);
                    }
                    menuOpen = slideOffset>initialVal;
                    decided = true;
                }
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                menuOpen = true;
                hideActionButton(true);
                setWindowFlags(true);
                if (setSongMenuFragment()) {
                    showTutorial("songsetMenu",null);
                }
                showHideActionBar();
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                menuOpen = false;
                if (!whichMode.equals("Presenter")) {
                    hideActionButton(myView.drawerLayout.getDrawerLockMode(GravityCompat.START) != DrawerLayout.LOCK_MODE_UNLOCKED);
                }
                hideKeyboard();
                showHideActionBar();
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
    @Override
    public void setWindowFlags(boolean immersiveOn) {
        // Fix the page flags
        if (windowFlags==null) {
            windowFlags = new WindowFlags(this.getWindow());
        }
        try {
            windowFlags.setWindowFlags(immersiveOn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        // If pedalsFragment is open, send the keyCode and event there
        if (pedalsFragment!=null && pedalsFragment.isListening()) {
            pedalsFragment.keyDownListener(keyCode);
            return true;
        } else {
            Log.d(TAG,"onKeyDown("+keyCode+","+keyEvent.toString());
            pedalActions.commonEventDown(keyCode, null);
            if (pedalActions.getButtonNumber(keyCode,null)>0) {
                return true;
            } else {
                return super.onKeyDown(keyCode, keyEvent);
            }
        }
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent keyEvent) {
        // If pedalsFragment is open, send the keyCode and event there
        if (pedalsFragment!=null && pedalsFragment.isListening()) {
            pedalsFragment.commonEventUp();
        } else if (!settingsOpen) {
            pedalActions.commonEventUp(keyCode,null);
        }
        return super.onKeyUp(keyCode, keyEvent);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent keyEvent) {
        // If pedalsFragment is open, send the keyCode and event there
        if (pedalsFragment!=null && pedalsFragment.isListening()) {
            pedalsFragment.commonEventLong();
            return true;
        } else if (!settingsOpen) {
            pedalActions.commonEventLong(keyCode,null);
        }
        return super.onKeyLongPress(keyCode, keyEvent);
    }
    @Override
    public void onBackPressed() {
        if (navController!=null && navController.getCurrentDestination()!=null) {
            try {
                int id = Objects.requireNonNull(navController.getCurrentDestination()).getId();
                if (id == R.id.performanceFragment || id == R.id.presenterFragment || id == R.id.setStorageLocationFragment) {
                    displayAreYouSure("exit", getString(R.string.exit_confirm), null,
                            navController.getCurrentDestination().getNavigatorName(),
                            navHostFragment, null);
                } else {
                    super.onBackPressed();
                }
            } catch (Exception e) {
                e.printStackTrace();
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public SoftKeyboard getSoftKeyboard() {
        return softKeyboard;
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
                R.id.performanceFragment, R.id.presenterFragment)
                .setOpenableLayout(myView.drawerLayout)
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

    }
    @Override
    public void navigateToFragment(String deepLink, int id) {
        // Either sent a deeplink string, or a fragment id
        lockDrawer(true);
        closeDrawer(true);  // Only the Performance and Presenter fragments allow this.  Switched on in these fragments
        hideActionButton(true);
        runOnUiThread(() -> {
            try {
                if (deepLink!=null) {
                    navController.navigate(Uri.parse(deepLink));
                } else {
                    navController.navigate(id);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
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
                        song = processSong.initialiseSong(song.getFolder(),"NEWSONGFILENAME");
                        String newSongText = processSong.getXML(song);
                        if (storageAccess.doStringWriteToFile("Songs",song.getFolder(), song.getFilename(),newSongText)) {
                            navigateToFragment(null,R.id.editSongFragment);
                        } else {
                            showToast.doIt(getString(R.string.error));
                        }
                    }
                    break;

                case "set_updateKeys":
                case "set_updateView":
                case "set_updateItem":
                    // User has the set menu open and wants to do something
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

                case "presenterFragment_showCase":
                    if (presenterFragment!=null) {
                        presenterFragment.showTutorial();
                    }
                    break;

                case "presenterFragmentSettings":
                    ((SecondaryDisplaySettingsFragment) callingFragment).updateBackground();
                    ((SecondaryDisplaySettingsFragment) callingFragment).updateInfoBackground();
                    ((SecondaryDisplaySettingsFragment) callingFragment).updateLogo();
                    break;

                case "presenterFragmentSongSections":
                    if (presenterFragment!=null) {
                        presenterFragment.getSongViews();
                        presenterFragment.updateButtons();
                    }
                    break;

                case "themeSetupFragment":
                    ((ThemeSetupFragment) callingFragment).updateColors();
                    ((ThemeSetupFragment) callingFragment).updateButtons();
                    break;

                case "setManageFragment":
                    ((SetManageFragment) callingFragment).doRename();
                    break;

            }
        }
    }
    @Override
    public void navHome() {
        lockDrawer(false);
        whichMode = preferences.getMyPreferenceString("whichMode","Performance");
        if (navController.getCurrentDestination()!=null) {
            navController.popBackStack(Objects.requireNonNull(navController.getCurrentDestination()).getId(), true);
        }
        // If we were in bootfragment, the toolbar was translated -200 out of the way (calls to hide didn't work)
        appActionBar.translateAwayActionBar(false);
        if (whichMode.equals("Presenter")) {
            navigateToFragment("opensongapp://presenter",0);
        } else {
            navigateToFragment("opensongapp://performance",0);
        }
        settingsOpen = false;
    }
    @Override
    public boolean onSupportNavigateUp() {
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public Fragment getCurrentFragment() {
        return navHostFragment.getChildFragmentManager().getFragments().get(0);
    }

    private boolean isCurrentFragment(int fragId) {
        runOnUiThread(() -> getSupportFragmentManager().executePendingTransactions());
        int currFrag = -1;
        if (navController!=null && navController.getCurrentDestination()!=null) {
            currFrag = navController.getCurrentDestination().getId();
        }
        return currFrag == fragId;
    }

    // Nearby stuff
    private void setupNearby() {
        // Set up the Nearby connection service
        nearbyConnections.getUserNickname();

        // Establish a known state for Nearby
        nearbyConnections.turnOffNearby();
    }
    @Override
    public NearbyConnections getNearbyConnections(MainActivityInterface mainActivityInterface) {
        // Return a reference to nearbyConnections
        return nearbyConnections;
    }
    @Override
    public NearbyConnections getNearbyConnections() {
        return nearbyConnections;
    }
    private void openNearbyFragment() {
        if (hasNearbyPermissions()) {
            navigateToFragment("opensongapp://settings/nearby", 0);
        }
    }
    private boolean hasNearbyPermissions() {
        if (Build.VERSION.SDK_INT>30) {
            return checkForPermission(Manifest.permission.BLUETOOTH_SCAN) &&
                    checkForPermission(Manifest.permission.BLUETOOTH_ADVERTISE) &&
                    checkForPermission(Manifest.permission.BLUETOOTH_CONNECT);
        } else if (Build.VERSION.SDK_INT==29 || Build.VERSION.SDK_INT==30) {
            return checkForPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            return checkForPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
    }

    @Override
    public void nearbyEnableConnectionButtons() {
        if (settingsOpen && nearbyConnections.getConnectionsOpen() && nearbyConnectionsFragment!=null) {
            try {
                nearbyConnectionsFragment.enableConnectionButtons();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void nearbyUpdateCountdownText(boolean advertise, MaterialButton materialButton) {
        if (settingsOpen && nearbyConnections.getConnectionsOpen() && nearbyConnectionsFragment!=null) {
            try {
                nearbyConnectionsFragment.updateCountdownText(advertise,materialButton);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void startDiscovery() {
        nearbyConnections.startDiscovery();
    }
    @Override
    public void startAdvertising() {
        nearbyConnections.startAdvertising();
    }
    @Override
    public void stopDiscovery() {
        nearbyConnections.stopDiscovery();
    }
    @Override
    public void stopAdvertising() {
        nearbyConnections.stopAdvertising();
    }
    @Override
    public void turnOffNearby() {
        nearbyConnections.turnOffNearby();
    }
    @Override
    public void updateConnectionsLog() {
        // Send the command to the Nearby Connections fragment (if it exists!)
        try {
            if (nearbyConnectionsFragment!=null && nearbyConnections.getConnectionsOpen()) {
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
    public void doSendPayloadBytes(String infoPayload) {
        // TODO - IV addition to check if needed (obs no FullscreenActivity anymore!
        // // IV - Do not send section 0 payload when loading a song
        //        if (!FullscreenActivity.alreadyloading) {
        nearbyConnections.doSendPayloadBytes(infoPayload);
        // }
    }
    @Override
    public boolean requestNearbyPermissions(boolean needPlayServices) {
        // Only do this if the user has Google APIs installed or we don't need them
        // (for midi bluetooth), otherwise, there is no point
        if (!needPlayServices || GoogleApiAvailability.getInstance().
                isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {

            // Check for the relevant Bluetooth permissions (depends on Build.SDK_VERSION)
            if (hasNearbyPermissions()) {
                // We have the required permissions!
                return true;

            } else {
                // Check which one(s) we don't have and ask for them!
                List<String> listPermissionsNeeded = new ArrayList<>();
                if (Build.VERSION.SDK_INT > 30) {
                    if (!checkForPermission(Manifest.permission.BLUETOOTH_SCAN)) {
                        listPermissionsNeeded.add(Manifest.permission.BLUETOOTH_SCAN);
                    }
                    if (!checkForPermission(Manifest.permission.BLUETOOTH_ADVERTISE)) {
                        listPermissionsNeeded.add(Manifest.permission.BLUETOOTH_ADVERTISE);
                    }
                    if (!checkForPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                        listPermissionsNeeded.add(Manifest.permission.BLUETOOTH_CONNECT);
                    }
                    if (listPermissionsNeeded.size() > 0) {
                        String[] permissions = new String[listPermissionsNeeded.size()];
                        for (int x = 0; x < listPermissionsNeeded.size(); x++) {
                            permissions[x] = listPermissionsNeeded.get(x);
                        }
                        requestForPermissions(permissions);
                    }
                } else if (Build.VERSION.SDK_INT == 29 || Build.VERSION.SDK_INT == 30) {
                    requestForPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
                } else {
                    requestForPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION});
                }
                return false;
            }
        } else {
            Log.d(TAG,"GooglePlayServices not installed");
            installPlayServices();
            // Not allowed on this device
            return false;
        }
    }

    @Override
    public boolean checkForPermission(String permission) {
        return ActivityCompat.checkSelfPermission(this,permission) == PackageManager.PERMISSION_GRANTED;
    }
    @Override
    public void requestForPermissions(String[] permissions) {
        ActivityCompat.requestPermissions(this, permissions,403);
    }

    // Instructions sent from fragments for MainActivity to deal with
    @Override
    public void hideActionButton(boolean hide) {
        runOnUiThread(() -> {
            if (hide) {
                myView.pageButtonRight.actionFAB.setVisibility(View.GONE);
                myView.pageButtonRight.bottomButtons.setVisibility(View.GONE);
                myView.onScreenInfo.getInfo().setVisibility(View.GONE);
                myView.nextPrevInfo.nextPrevInfoLayout.setVisibility(View.GONE);

            } else {
                myView.pageButtonRight.actionFAB.setVisibility(View.VISIBLE);
                myView.pageButtonRight.bottomButtons.setVisibility(View.VISIBLE);
                myView.onScreenInfo.getInfo().setVisibility(View.VISIBLE);
                if (displayPrevNext.getShowPrev() || displayPrevNext.getShowNext()) {
                    myView.nextPrevInfo.nextPrevInfoLayout.setVisibility(View.VISIBLE);
                }
                // Do this with a delay
                customAnimation.fadeActionButton(myView.pageButtonRight.actionFAB, themeColors.getPageButtonsSplitAlpha());
            }
        });
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
        // Otherwise a new title is passed as a string (in a settings menu)
        windowFlags.setWindowFlags(true);
        appActionBar.setActionBar(what);
        Log.d(TAG,"what="+what+"  settingsOpen="+settingsOpen);
        menuIconVisibility(what == null || (getMode().equals("Presenter") && !settingsOpen));
        myView.fragmentView.setTop(appActionBar.getActionBarHeight());
    }
    @Override
    public void updateToolbarHelp(String webAddress) {
        // Null or empty web addresses hide the webHelp button
        appActionBar.updateToolbarHelp(webAddress);
    }
    private void menuIconVisibility(boolean isVisible) {
        try {
            if (isVisible) {
                screenMirror.setVisibility(View.VISIBLE);
                myView.toolBar.batteryholder.setVisibility(View.VISIBLE);
            } else {
                screenMirror.setVisibility(View.GONE);
                myView.toolBar.batteryholder.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateActionBarSettings(String prefName, float floatval, boolean isvisible) {
        // If the user changes settings from the ActionBarSettingsFragment, they get sent here to deal with
        // So let's pass them on to the AppActionBar helper
        appActionBar.updateActionBarSettings(prefName,floatval,isvisible);
    }
    @Override
    public void showTutorial(String what, ArrayList<View> viewsToHighlight) {
        //MaterialShowcaseView.resetAll(this);
        if (settingsButton==null) {
            invalidateOptionsMenu();
        }
        initialiseArrayLists();

        String whichShowcase;
        switch (what) {
            case "performanceView":
            default:
                whichShowcase = "performanceMode";
                // The hamburger (song/set menu)
                if (myView.toolBar.getRoot().getChildCount() > 2) {
                    final View view = myView.toolBar.getRoot().getChildAt(2);
                    targets.add(view);
                    infos.add("Open the menu to view and manage your songs and sets");
                } else {
                    for (int i = 0; i < myView.toolBar.getRoot().getChildCount(); ++i) {
                        final View child = myView.toolBar.getRoot().getChildAt(i);
                        if (child != null && child.getClass().toString().contains("ImageView")) {
                            targets.add(child);
                            infos.add("Open the menu to view and manage your songs and sets");
                        }
                    }
                }

                targets.add(findViewById(R.id.menuSettings));
                infos.add(getString(R.string.extra_settings));
                rects.add(false);
                rects.add(false);
                targets.add(myView.pageButtonRight.actionFAB);
                infos.add(getString(R.string.action_button_info));
                rects.add(false);
                break;

            case "presenterSongs":
                whichShowcase = "presenterSongs";
                // The hamburger (song/set menu)
                if (myView.toolBar.getRoot().getChildCount() > 2) {
                    final View view = myView.toolBar.getRoot().getChildAt(2);
                    targets.add(view);
                    infos.add("Open the menu to view and manage your songs and sets");
                } else {
                    for (int i = 0; i < myView.toolBar.getRoot().getChildCount(); ++i) {
                        final View child = myView.toolBar.getRoot().getChildAt(i);
                        if (child != null && child.getClass().toString().contains("ImageView")) {
                            targets.add(child);
                            infos.add("Open the menu to view and manage your songs and sets");
                        }
                    }
                }
                targets.add(findViewById(R.id.menuSettings));
                infos.add(getString(R.string.extra_settings));
                rects.add(false);
                rects.add(false);
                // This relies on views having been sent
                if (viewsToHighlight!=null && viewsToHighlight.size()>6) {
                    targets.add(viewsToHighlight.get(0));
                    infos.add(getString(R.string.song_sections));
                    rects.add(true);
                    targets.add(viewsToHighlight.get(1));
                    infos.add(getString(R.string.logo_info));
                    rects.add(true);
                    targets.add(viewsToHighlight.get(2));
                    infos.add(getString(R.string.blank_screen_info));
                    rects.add(true);
                    targets.add(viewsToHighlight.get(3));
                    infos.add(getString(R.string.black_screen_info));
                    rects.add(true);
                    targets.add(viewsToHighlight.get(4));
                    infos.add(getString(R.string.project_panic));
                    rects.add(true);
                    targets.add(viewsToHighlight.get(5));
                    infos.add(getString(R.string.song_title)+"\n"+getString(R.string.long_press)+" = "+getString(R.string.edit_song));
                    rects.add(true);
                    targets.add(viewsToHighlight.get(6));
                    infos.add(getString(R.string.song_sections_project));
                    rects.add(true);
                }
                break;
            case "songsetMenu":
                // Initialise the arraylists
                whichShowcase = "songsetMenu";
                initialiseArrayLists();
                targets.add(Objects.requireNonNull(myView.menuTop.tabs.getTabAt(0)).view);
                targets.add(Objects.requireNonNull(myView.menuTop.tabs.getTabAt(1)).view);
                targets.add(Objects.requireNonNull(myView.viewpager.findViewById(R.id.actionFAB)));
                infos.add(getString(R.string.menu_song_info));
                infos.add(getString(R.string.menu_set_info));
                infos.add(getString(R.string.add_songs) + " / " + getString(R.string.song_actions));
                rects.add(true);
                rects.add(true);
                rects.add(false);
                break;

        }

        showCase.sequenceShowCase(this, targets, null, infos, rects, whichShowcase);

    }
    private void initialiseArrayLists() {
        targets = new ArrayList<>();
        infos = new ArrayList<>();
        rects = new ArrayList<>();
    }


    // Settings and options menus
    @Override
    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        settingsButton = menu.findItem(R.id.settings_menu_item);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        showHideActionBar();
        if ("Settings".equals(item.toString())) {
            if (settingsOpen) {
                settingsOpen = false;
                navHome();
            } else {
                navigateToFragment("opensongapp://preferences", 0);
                settingsOpen = true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void refreshMenuItems() {
        invalidateOptionsMenu();
    }
    @SuppressLint("PrivateResource")
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mainactivitymenu, menu);

        screenMirror = (ImageView) menu.findItem(R.id.mirror_menu_item).getActionView();
        GlideApp.with(this).load(ContextCompat.getDrawable(this,R.drawable.cast)).into(screenMirror);
        screenMirror.setOnClickListener(view -> startActivity(new Intent("android.settings.CAST_SETTINGS")));
        myView.toolBar.batteryholder.setOnClickListener(view -> navigateToFragment("opensongapp://settings/display/actionbar",0));

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
            showHideActionBar();
        }
    }
    @Override
    public void closeDrawer(boolean close) {
        if (close) {
            myView.drawerLayout.closeDrawer(GravityCompat.START);
            menuOpen = false;
        } else {
            myView.drawerLayout.openDrawer(GravityCompat.START);
            menuOpen = true;
        }
        // Force any soft keyboard to close
        softKeyboard.hideKeyboard(this);
    }

    // The song and set menu
    private void setUpSongMenuTabs() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), this.getLifecycle());
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
                    tab.setIcon(ResourcesCompat.getDrawable(getResources(),R.drawable.music_note,null));
                    break;
                case 1:
                    tab.setText(getString(R.string.set));
                    tab.setIcon(ResourcesCompat.getDrawable(getResources(),R.drawable.list_number,null));
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
        new Thread(() -> {
            runOnUiThread(() -> showToast.doIt(getString(R.string.search_index_start)));
            songListBuildIndex.setIndexComplete(false);
            songListBuildIndex.fullIndex();
            runOnUiThread(() -> {
                songListBuildIndex.setIndexRequired(false);
                songListBuildIndex.setIndexComplete(true);
                showToast.doIt(getString(R.string.search_index_end));
                updateSongMenu(song);
                updateFragment("set_updateKeys",null,null);
            });
        }).start();
    }
    @Override
    public void moveToSongInSongMenu() {
        if (songMenuFragment!=null) {
            try {
                songMenuFragment.moveToSongInMenu(song);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
            ArrayList<String> songIds = storageAccess.listSongs();
            // Write this to text file
            storageAccess.writeSongIDFile(songIds);
            // Try to create the basic databases
            sqLiteHelper.resetDatabase();
            nonOpenSongSQLiteHelper.initialise();
            // Add entries to the database that have songid, folder and filename fields
            // This is the minimum that we need for the song menu.
            // It can be upgraded asynchronously in StageMode/PresenterMode to include author/key
            // Also will later include all the stuff for the search index as well
            sqLiteHelper.insertFast();
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
        if (songListBuildIndex.getIndexComplete()) {
            songListBuildIndex.setIndexComplete(true);
            songListBuildIndex.setIndexRequired(false);
        }
        if (setSongMenuFragment() && songMenuFragment!=null) {
            songMenuFragment.updateSongMenu(song);
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
        return song;
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
        pageButtons.animatePageButton(rotation == 0);
    }
    @Override
    public void updatePageButtonLayout() {
        // We have changed something about the page buttons (or initialising them
        if (myView.pageButtonRight.actionFAB.getRotation()!=0) {
            pageButtons.animatePageButton(false);
        }
        pageButtons.updateColors();
        pageButtons.setPageButton(myView.pageButtonRight.actionFAB, -1, false);
        for (int x=0; x<6; x++) {
            pageButtons.setPageButton(pageButtons.getFAB(x), x, false);
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
        Log.d(TAG,"sendMidiFromList("+item+")");
        if (isCurrentFragment(R.id.midiFragment)) {
            ((MidiFragment)getCurrentFragment()).sendMidiFromList(item);
        }
    }
    @Override
    public void deleteMidiFromList(int item) {
        if (isCurrentFragment(R.id.midiFragment)) {
            ((MidiFragment)getCurrentFragment()).deleteMidiFromList(item);
        }
    }
    @Override
    public void registerMidiAction(boolean actionDown, boolean actionUp, boolean actionLong, String note) {
        // If pedalsFragment is open, send the midiNote and event there
        Log.d(TAG,"currentFragment(pedalsFragment): "+isCurrentFragment(R.id.pedalsFragment));
        try {
            Log.d(TAG, "isListening()=" + ((PedalsFragment) getCurrentFragment()).isListening());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isCurrentFragment(R.id.pedalsFragment) && ((PedalsFragment)getCurrentFragment()).isListening()) {
            if (actionDown) {
                ((PedalsFragment) getCurrentFragment()).midiDownListener(note);
            } else if (actionUp) {
                ((PedalsFragment) getCurrentFragment()).commonEventUp();
            } else if (actionLong) {
                ((PedalsFragment) getCurrentFragment()).commonEventLong();
            }
        } else {
            if (actionDown && !settingsOpen) {
                pedalActions.commonEventDown(-1,note);
            } else if (actionUp && !settingsOpen) {
                pedalActions.commonEventUp(-1,note);
            } else if (actionLong && !settingsOpen) {
                pedalActions.commonEventLong(-1,note);
            }
        }
    }
    @Override
    public Midi getMidi() {
        return midi;
    }

    // Sticky notes
    @Override
    public void showSticky(boolean forceshow, boolean hide) {
        // Try to show the sticky note
        if (!whichMode.equals("Presenter") && performanceFragment!=null) {
            performanceFragment.dealWithStickyNotes(forceshow,hide);
        }
    }

    // Highlighter
    @Override
    public void toggleHighlighter() {
        // Try to show the highlighter
        if (!whichMode.equals("Presenter") && performanceFragment!=null) {
            performanceFragment.toggleHighlighter();
        }
    }

    // Metronome
    @Override
    public void metronomeToggle() {
        if (metronome.metronomeValid()) {
            // Toggle the start or stop
            if (!metronome.getIsRunning()) {
                metronome.startMetronome();
            } else {
                metronome.stopMetronome();
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

    // Capo
    @Override
    public void dealWithCapo() {
        // This checks for song capo and if capo chords are shown
        myView.onScreenInfo.dealWithCapo(this,this);
    }
    @Override
    public void updateOnScreenInfo(String what) {
        switch (what) {
            case "alpha":
                myView.onScreenInfo.updateAlpha(this,this);
                break;
            case "showhide":
                myView.onScreenInfo.showHideViews(this);
                break;
            case "setpreferences":
                myView.onScreenInfo.setPreferences(this,this);
                break;
        }
    }

    // Song processing
    @Override
    public ExtendedFloatingActionButton getSaveButton(){
        if (editSongFragment!=null) {
            return editSongFragment.getSaveButton();
        } else {
            return null;
        }
    }


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
    public ChordDisplayProcessing getChordDisplayProcessing() {
        return chordDisplayProcessing;
    }
    @Override
    public ChordDirectory getChordDirectory() {
        return chordDirectory;
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
    public PresenterSettings getPresenterSettings() {
        return presenterSettings;
    }

    // TODO Getters to finish

    // TODO Setters to finish




    @Override
    public void doSongLoad(String folder, String filename, boolean closeDrawer) {
        if (whichMode.equals("Presenter")) {
            if (presenterFragment!=null) {
                presenterFragment.doSongLoad(folder,filename);
            } else {
                navigateToFragment(null,R.id.presenterFragment);
            }
        } else {
            if (performanceFragment!=null) {
                performanceFragment.doSongLoad(folder,filename);
            } else {
                navigateToFragment(null,R.id.performanceFragment);
            }
        }
        closeDrawer(closeDrawer);
    }

    @Override
    public void loadSongFromSet(int position) {
        if (position >= currentSet.getIndexSongInSet()) {
            displayPrevNext.setSwipeDirection("R2L");
        } else {
            displayPrevNext.setSwipeDirection("L2R");
        }
        // Get the key of the set item
        String setKey = currentSet.getKey(position);
        String setFolder = currentSet.getFolder(position);
        String setFilename = currentSet.getFilename(position);
        String songKey;

        Log.d(TAG,"position="+position+"  setFolder="+setFolder+"  setFilename="+setFilename+"  setKey="+setKey);

        // Update the index in the set
        currentSet.setIndexSongInSet(position);

        // Get the song key (from the database)
        if (storageAccess.isSpecificFileExtension("imageorpdf", currentSet.getFilename(position))) {
            songKey = nonOpenSongSQLiteHelper.getKey(setFolder, setFilename);
        } else {
            if (setFolder.contains("**") || setFolder.contains("../")) {
                Song quickSong = new Song();
                quickSong.setFolder(setFolder);
                quickSong.setFilename(setFilename);
                quickSong = loadSong.doLoadSongFile(quickSong, false);
                songKey = quickSong.getKey();
            } else {
                songKey = sqLiteHelper.getKey(setFolder, setFilename);
            }
        }

        if (setKey != null && songKey != null &&
                !setKey.isEmpty() && !songKey.isEmpty() && !setKey.equals(songKey)) {
            // The set has specified a key that is different from our song
            // We will use a variation of the current song
            String newFolder;
            String newFilename;
            if (!setFolder.contains("**")) {
                // Not a variation already, so we'll make it one with the set key
                newFolder = "**" + getString(R.string.variation);
                newFilename = setFolder + "_" + setFilename + "_" + setKey;
            } else {
                // Already a variation, don't change the file name
                newFolder = setFolder;
                newFilename = setFilename;
            }

            Uri variationUri = storageAccess.getUriForItem("Variations", "", newFilename);

            // Get a tempSong we can write
            Song copySong = new Song();
            if (setFolder.contains("**") || setFolder.contains("../")) {
                // Already a variation (or other), so don't use the database
                copySong.setFilename(setFilename);
                copySong.setFolder(setFolder);
                copySong = loadSong.doLoadSongFile(copySong, false);
            } else {
                // Just a song, so use the database
                copySong = sqLiteHelper.getSpecificSong(setFolder, setFilename);
            }
            copySong.setFolder(newFolder);
            copySong.setFilename(newFilename);

            // Transpose the lyrics
            // Get the number of transpose times
            int transposeTimes = transpose.getTransposeTimes(songKey, setKey);
            copySong.setKey(songKey); // This will be transposed in the following...
            copySong.setLyrics(transpose.doTranspose(copySong,
                    "+1", transposeTimes, copySong.getDetectedChordFormat(),
                    copySong.getDesiredChordFormat()).getLyrics());
            // Get the song XML
            String songXML = processSong.getXML(copySong);
            // If the file already exists, remove it as we might have edited the original
            storageAccess.lollipopCreateFileForOutputStream(true,
                    variationUri, null, "Variations", "", newFilename);

            // Save the song
            storageAccess.doStringWriteToFile("Variations", "", newFilename, songXML);

            setFolder = newFolder;
            setFilename = newFilename;
        }

        // If the set menu is open/exists, try to scroll to this item
        if (setMenuFragment != null) {
            try {
                setMenuFragment.scrollToItem();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        doSongLoad(setFolder, setFilename, true);
    }

    @Override
    public void updateKeyAndLyrics(Song song) {
        // This is called from the transpose class once it has done its work on the edit song fragment
        //editSongFragmentMain.updateKeyAndLyrics(song);
    }

    @Override
    public void registerFragment(Fragment frag, String what) {
        switch (what) {
            case "Performance":
                performanceFragment = (PerformanceFragment) frag;
                break;
            case "Presenter":
                presenterFragment = (PresenterFragment) frag;
                break;
            case "EditSongFragment":
                editSongFragment = (EditSongFragment) frag;
                break;
            case "NearbyConnectionsFragment":
                nearbyConnectionsFragment = (NearbyConnectionsFragment) frag;
                break;
            case "PedalsFragment":
                pedalsFragment = (PedalsFragment) frag;
                break;
        }
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
                    result = storageAccess.doDeleteFile("Songs",
                            song.getFolder(), song.getFilename());
                    // Now remove from the SQL database
                    if (song.getFiletype().equals("PDF") || song.getFiletype().equals("IMG")) {
                        nonOpenSongSQLiteHelper.deleteSong(song.getFolder(),song.getFilename());
                    }
                    sqLiteHelper.deleteSong(song.getFolder(),song.getFilename());
                    // Set the welcome song
                    song.setFilename("Welcome to OpenSongApp");
                    song.setFolder(getString(R.string.mainfoldername));
                    updateSongMenu(song);
                    navHome();
                    break;

                case "ccliDelete":
                    Uri uri = storageAccess.getUriForItem("Settings","","ActivityLog.xml");
                    result = ccliLog.createBlankXML(uri);
                    break;

                case "deleteItem":
                    // Folder and subfolder are passed in the arguments.  Blank arguments.get(2) /filenames mean folders
                    result = storageAccess.doDeleteFile(arguments.get(0),arguments.get(1),arguments.get(2));
                    if (arguments.get(2).isEmpty() && arguments.get(0).equals("Songs") && (arguments.get(1).isEmpty()||arguments.get(1)==null)) {
                        // Emptying the entire songs foler, so need to recreate it on finish
                        storageAccess.createFolder("Songs","","");
                    }
                    //Rebuild the song index
                    updateSongMenu(fragName, callingFragment, arguments); // Passing the fragment allows an update to be sent to the calling fragment
                    break;

                case "deleteHighlighter":
                    // Try to send the info back to the highlighter edit fragment
                    try {
                        ((HighlighterEditFragment)callingFragment).doDelete(true);
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
                    preferences.setMyPreferenceString("setCurrent", "");
                    preferences.setMyPreferenceString("setCurrentLastName", "");
                    // Untick song menu items
                    updateSongList();
                    updateFragment("set_updateView",null,null);
                    result = true;
                    break;

                case "removeThemeTag":
                    // We are about to remove tags from songs.  This is done in the EditSong fragment
                    updateFragment("confirmed_" + fragName, callingFragment, arguments);
                    allowToast = false;
                    break;

                case "resetColors":
                    // We will reset the chosen theme colours to app defaults
                    themeColors.resetTheme();
                    themeColors.getDefaultColors();
                    updateFragment(fragName,callingFragment,null);
                    allowToast = false;
                    break;

                case "renameSet":
                    // We are renaming a set
                    updateFragment(fragName,callingFragment,null);
                    allowToast = false;
                    break;
            }
            if (allowToast && result) {
                // Don't show toast for exit, but other successful actions
                showToast.doIt(getString(R.string.success));
            } else if (allowToast){
                showToast.doIt(getString(R.string.error));
            }
        }
    }

    @Override
    public void updateSetList() {
        updateFragment("set_updateView",null,null);
    }

    @Override
    public void addSetItem(int currentSetPosition) {
        if (setMenuFragment!=null) {
            try {
                setMenuFragment.addSetItem(currentSetPosition);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void updateSongList() {
        if (songMenuFragment!=null) {
            try {
                songMenuFragment.refreshSongList();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void removeSetItem(int currentSetPosition) {
        if (setMenuFragment!=null) {
            try {
                setMenuFragment.removeSetItem(currentSetPosition);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void updateSetTitle() {
        if (setMenuFragment!=null) {
            try {
                setMenuFragment.updateSetTitle();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void toggleAutoscroll() {
        if (autoscroll.getIsPaused()) {
            // This sets to the opposite, so un-paused
            autoscroll.pauseAutoscroll();
        } else if (autoscroll.getIsAutoscrolling()) {
            autoscroll.stopAutoscroll();
        } else {
            autoscroll.startAutoscroll();
        }
    }

    @Override
    public Pad getPad() {
        return pad;
    }
    @Override
    public boolean playPad() {
        // If the pad is playing, stop else start
        if (pad.isPadPlaying()) {
            pad.stopPad();
            return false;
        } else {
            pad.startPad();
            // Showcase if required
            showCase.singleShowCase(this,myView.onScreenInfo.getPad(),getString(R.string.okay),getString(R.string.pad_playback_info),true,"padPlayback");
            return true;
        }
    }







    @Override
    public void installPlayServices() {
        Snackbar.make(myView.drawerLayout, R.string.play_services_error,
                BaseTransientBottomBar.LENGTH_LONG).setAction(R.string.play_services_how, v -> {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.website_play_services_help)));
            startActivity(i);
        }).show();
    }



    @Override
    public void fullIndex() {
        if (fullIndexRequired) {
            showToast.doIt(getString(R.string.search_index_start));
            new Thread(() -> {
                String outcome = songListBuildIndex.fullIndex();
                if (songMenuFragment!=null) {
                    try {
                        songMenuFragment.updateSongMenu(song);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(() -> {
                    if (!outcome.isEmpty()) {
                        showToast.doIt(outcome.trim());
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
            songIds = storageAccess.listSongs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Write a crude text file (line separated) with the song Ids (folder/file)
        storageAccess.writeSongIDFile(songIds);

        // Try to create the basic databases
        // Non persistent, created from storage at boot (to keep updated) used to references ALL files
        sqLiteHelper.resetDatabase();
        // Persistent containing details of PDF/Image files only.  Pull in to main database at boot
        // Updated each time a file is created, deleted, moved.
        // Also updated when feature data (pad, autoscroll, metronome, etc.) is updated for these files
        nonOpenSongSQLiteHelper.initialise();

        // Add entries to the database that have songid, folder and filename fields
        // This is the minimum that we need for the song menu.
        // It can be upgraded asynchronously in StageMode/PresenterMode to include author/key
        // Also will later include all the stuff for the search index as well
        sqLiteHelper.insertFast();
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
        if (whichMode==null) {
            whichMode = preferences.getMyPreferenceString("whichMode", "Performance");
        }
        return whichMode;
    }
    @Override
    public void setMode(String whichMode) {
        this.whichMode = whichMode;
    }


    @Override
    public Locale getLocale() {
        if (locale==null) {
            fixLocale.setLocale(this,this);
            locale = fixLocale.getLocale();
        }
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
    public String getWhattodo() {
        if (whattodo == null) {
            whattodo = "";
        }
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
    public void pdfScrollToPage(int pageNumber) {
        performanceShowSection(pageNumber);
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
    public void openDocument(String location) {
        // Most locations are passed in from the string.xml file.  They are listed under website_xxx
        // Otherwise they are created on the fly (for link files, importing songs, etc).
        if (location!=null) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                if (location.startsWith("http")) {
                    intent.setData(Uri.parse(location));
                } else {
                    String mimeType = null;
                    if (location.contains(".")) {
                        String extension = location.substring(location.lastIndexOf(".") + 1);
                        MimeTypeMap myMime = MimeTypeMap.getSingleton();
                        mimeType = myMime.getMimeTypeFromExtension(extension);
                    }
                    if (mimeType == null) {
                        mimeType = "*/*";
                    }
                    Uri uri = Uri.parse(location);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(uri, mimeType);
                }

                startActivity(intent);
            } catch (ActivityNotFoundException nf) {
                // No suitable application to open the document
                showToast.doIt(getString(R.string.no_suitable_application));
                nf.printStackTrace();

            } catch (Exception e) {
                // Probably no browser installed or no internet permission given.
                e.printStackTrace();
            }
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
            sectionColors = null;
            sectionColors = new ArrayList<>();
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
    public ArrayList<Integer> getSectionColors() {
        return sectionColors;
    }

    @Override
    public void addSectionSize(int position, int width, int height) {
        if (sectionWidths==null) {
            sectionWidths = new ArrayList<>();
        }
        if (sectionHeights==null) {
            sectionHeights = new ArrayList<>();
        }
        sectionWidths.add(position,width);
        sectionHeights.add(position,height);
    }

    @Override
    public void setSectionColors(ArrayList<Integer> colors) {
        sectionColors = colors;
    }

    @Override
    public void setSongSheetTitleLayout(LinearLayout linearLayout) {
        if (songSheetTitleLayout==null) {
            initialiseSongSheetTitleLayout();
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
            initialiseSongSheetTitleLayout();
        }
        return songSheetTitleLayout;
    }

    private void initialiseSongSheetTitleLayout() {
        songSheetTitleLayout = new LinearLayout(this);
        songSheetTitleLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        songSheetTitleLayout.setOrientation(LinearLayout.VERTICAL);
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
    public void enableSwipe(String which, boolean canSwipe) {
        if (which.equals("edit") && editSongFragment!=null) {
            editSongFragment.enableSwipe(canSwipe);
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
    public void updateSizes(int width, int height) {
        /*if (whichMode.equals("Performance") && isCurrentFragment(R.id.performanceFragment)) {
            ((PerformanceFragment)getCurrentFragment()).updateSizes(width,height);
            if (getFragmentFromId(R.id.performanceFragment) != null) {
                //((PerformanceFragment) getFragmentFromId(R.id.performanceFragment)).updateSizes(width, height);
            }
        }*/
    }

    @Override
    public void selectSection(int i) {
        // Only do this if we are not in a settings fragment
        if (!settingsOpen) {
            if (whichMode.equals("Presenter") && presenterFragment != null) {
                presenterFragment.selectSection(i);
            } else if (!whichMode.equals("Presenter") && performanceFragment != null) {
                performanceFragment.selectSection(i);
            }
        } else {
            nearbyConnections.setWaitingForSectionChange(true);
            nearbyConnections.setPendingCurrentSection(i);
        }
    }

    @Override
    public void prepareSongMenu() {
        // TODO
    }

    @Override
    public void loadSong() {
        // If we are not in a settings window, load the song
        // Otherwise it will happen when the user closes the settings fragments
        if (!settingsOpen) {
            doSongLoad(song.getFolder(), song.getFilename(), true);
        }
    }

    @Override
    public void goToPreviousItem() {
        // TODO
    }

    @Override
    public void goToNextItem() {
        // TODO
    }

    // Sent from bottom sheet and requires an update in calling fragment
    @Override
    public void updateValue(Fragment fragment, String fragname, String which, String value) {
        // This takes the info from the TextInputBottomSheet and passes back to the calling fragment
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
                        ((SetManageFragment) fragment).updateValue(value);
                        break;
                    case "EditSongFragmentMain":
                        ((EditSongFragmentMain) fragment).updateValue(value);
                        break;
                    case "CustomChordsFragment":
                        ((CustomChordsFragment) fragment).updateValue(value);
                        break;
                    case "SongSectionsFragment":
                        ((SongSectionsFragment) fragment).updateValue(value);
                        break;
                    case "songActionsMenuFragment":
                        ((SongActionsMenuFragment) fragment).doDuplicate(value);
                        break;
                    case "CustomSlideFragment":
                        ((CustomSlideFragment) fragment).getReusable(value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void getSwipeValues(int minDistance, int minHeight, int minTime) {
        if (isCurrentFragment(R.id.swipeFragment)) {
            try {
                ((SwipeFragment) getCurrentFragment()).getSwipeValues(minDistance, minHeight, minTime);
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
                    if (whattodo!=null && whattodo.equals("nearby")) {
                        openNearbyFragment();
                    }
                    break;
            }
        }
    }






    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Get the language
        fixLocale.setLocale(this,this);

        // Save a static variable that we have rotated the screen.
        // The media player will look for this.  If found, it won't restart when the song loads
        pad.setOrientationChanged(pad.getCurrentOrientation()!=newConfig.orientation);
        // If orientation has changed, we need to reload the song to get it resized.
        // Only do this if we are not in a settings menu though!
        if (!settingsOpen && pad.getOrientationChanged()) {
            // Set the current orientation
            pad.setCurrentOrientation(newConfig.orientation);
            doSongLoad(song.getFolder(),song.getFilename(),true);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

        // Fix the page flags
        setWindowFlags(true);

        // Check displays
        checkDisplays();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Turn off nearby
        nearbyConnections.turnOffNearby();
        // Stop pad timers
        if (pad!=null) {
            pad.stopTimers(1);
            pad.stopTimers(2);
        }
        // Stop metronome timers
        if (metronome!=null) {
            metronome.stopTimers(true);
        }
        // Stop autoscroll timers
        if (autoscroll!=null) {
            autoscroll.stopTimers();
        }

        // Copy the persistent database from app storage to user storage
        Log.d(TAG,"Persistent database backed up: "+nonOpenSongSQLiteHelper.copyUserDatabase());
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // Set the fullscreen window flags]
        setWindowFlags(true);
        if (hasFocus) {
            appActionBar.showActionBar(settingsOpen);
        }
    }

    @Override
    public void setUpBatteryMonitor() {
        if (actionBar!=null && batteryStatus==null) {
            batteryStatus = new BatteryStatus(this,myView.toolBar.batteryimage,
                    myView.toolBar.batterycharge, actionBar);
        }
        if (batteryStatus!=null) {
            batteryStatus.setUpBatteryMonitor();
        }
    }

    @Override
    public int[] getDisplayMetrics() {
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        int[] displayMetrics = new int[3];
        displayMetrics[0] = getWindow().getDecorView().getWidth();
        displayMetrics[1] = getWindow().getDecorView().getHeight();
        displayMetrics[2] = metrics.densityDpi;
        return displayMetrics;
    }

    // The secondary displays (HDMI or Mirroring/Casting)
    @SuppressLint("PrivateResource")
    @Override
    public void checkDisplays() {
        // This checks for connected displays and adjusts the menu item if connected
        DisplayManager displayManager = (DisplayManager) getSystemService(DISPLAY_SERVICE);
        if (displayManager != null) {
            connectedDisplays = displayManager.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION);
            if (screenMirror!=null) {
                if (connectedDisplays.length > 0) {
                    screenMirror.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.cast_connected));
                } else {
                    screenMirror.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.cast));
                }
            }
            // If we have changed the number of connected displays, set them up
            if (connectedDisplays.length > prevNumConnectedDisplays) {
                prevNumConnectedDisplays = connectedDisplays.length;
                setupDisplays();
            }
        } else if (screenMirror!=null){
            screenMirror.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.cast));
        }
    }

    private void setupDisplays() {
        // Go through each connected display and create the secondaryDisplay Presentation class
        // Check there aren't any already connected, if there are, dismiss them
        if (secondaryDisplays!=null) {
            for (SecondaryDisplay secondaryDisplay : secondaryDisplays) {
                if (secondaryDisplay != null && secondaryDisplay.isShowing()) {
                    try {
                        secondaryDisplay.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        // Now reset the secondaryDisplays
        secondaryDisplays = null;
        if (connectedDisplays.length>0) {
            secondaryDisplays = new SecondaryDisplay[connectedDisplays.length];
            for (int c=0; c<connectedDisplays.length; c++) {
                secondaryDisplays[c] = new SecondaryDisplay(this,connectedDisplays[c]);
                secondaryDisplays[c].show();
            }
        }
    }

    @Override
    public void updateDisplay(String what) {
        if (secondaryDisplays!=null) {
            for (SecondaryDisplay secondaryDisplay : secondaryDisplays) {
                if (secondaryDisplay != null && secondaryDisplay.isShowing()) {
                    try {
                        switch (what) {
                            // The song info bar
                            case "initialiseInfoBarRequired":
                                secondaryDisplay.initialiseInfoBarRequired();
                                break;
                            case "setSongInfo":
                                secondaryDisplay.setSongInfo();
                                break;
                            case "setInfoStyles":
                                secondaryDisplay.setInfoStyles();
                                break;
                            case "changeInfoAlignment":
                                secondaryDisplay.changeInfoAlignment();
                                break;
                            case "checkSongInfoShowHide":
                                secondaryDisplay.checkSongInfoShowHide();
                                break;

                            // Song content
                            case "setSongContent":
                                secondaryDisplay.setSongContent();
                                break;
                            case "showSection":
                                secondaryDisplay.showSection(getPresenterSettings().getCurrentSection());
                                break;
                            case "editView":
                                secondaryDisplay.editView();
                                break;
                            case "newSongLoaded":
                                secondaryDisplay.setIsNewSong();
                                break;

                            // The alert bar
                            case "showAlert":
                                secondaryDisplay.showAlert();
                                break;
                            case "updateAlert":
                                secondaryDisplay.updateAlert();
                                break;

                            // The screen setup
                            case "setScreenSizes":
                                secondaryDisplay.setScreenSizes();
                                break;
                            case "changeBackground":
                                secondaryDisplay.changeBackground();
                                break;
                            case "changeRotation":
                                secondaryDisplay.changeRotation();
                                break;

                            // The logo
                            case "changeLogo":
                                secondaryDisplay.changeLogo();
                                break;
                            case "showLogo":
                                secondaryDisplay.showLogo(getPresenterSettings().getLogoOn(), false);
                                break;

                            // Black and blank screen
                            case "showBlackscreen":
                                secondaryDisplay.showBlackScreen();
                                break;
                            case "showBlankscreen":
                                secondaryDisplay.showBlankScreen();
                                break;

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void presenterShowSection(int position) {
        if (secondaryDisplays!=null) {
            for (SecondaryDisplay secondaryDisplay : secondaryDisplays) {
                if (secondaryDisplay != null && secondaryDisplay.isShowing() &&
                        position < getSong().getSongSections().size()) {
                    try {
                        secondaryDisplay.showSection(position);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    @Override
    public void performanceShowSection(int position) {
        // This gets a section from from the user selecting either a PDF page or a Stage Mode section
        // Send it back to Performance Mode to deal with the outcome (scroll to, update display, etc)
        if (performanceFragment!=null) {
            performanceFragment.performanceShowSection(position);
        }
    }

}