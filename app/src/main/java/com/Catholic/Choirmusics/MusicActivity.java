package com.Catholic.Choirmusics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.widget.SearchView;


import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.imaginativeworld.oopsnointernet.callbacks.ConnectionCallback;
import org.imaginativeworld.oopsnointernet.dialogs.pendulum.DialogPropertiesPendulum;
import org.imaginativeworld.oopsnointernet.dialogs.pendulum.NoInternetDialogPendulum;
import org.imaginativeworld.oopsnointernet.dialogs.signal.DialogPropertiesSignal;
import org.imaginativeworld.oopsnointernet.dialogs.signal.NoInternetDialogSignal;
import org.imaginativeworld.oopsnointernet.snackbars.fire.NoInternetSnackbarFire;
import org.imaginativeworld.oopsnointernet.snackbars.fire.SnackbarPropertiesFire;

import java.util.ArrayList;
import java.util.List;


public class MusicActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private boolean checkPermission = false;
    ProgressDialog progressDialog;
    ListView listView;
    List<String> songsNameList;
    List<String> songsUrlList;
    List<String> songsArtistList;
    List<String> songsDurationList;
    ListAdapter adapter;
    JcPlayerView jcPlayerView;
    List<JcAudio> jcAudios;
    List<String> thumbnail;
    private AppOpenAdManager appOpenAdManager;

    private int numActivityRestarted = 0;
    private InterstitialAd mInterstitialAd;
    View alertView;
    private AdView adViewMessage;


    // No Internet Dialog: Pendulum
    private NoInternetDialogPendulum noInternetDialogPendulum;

    // No Internet Dialog: Signal
    private NoInternetDialogSignal noInternetDialogSignal;

    // No Internet Snackbar: Fire
    private NoInternetSnackbarFire noInternetSnackbarFire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);





        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        forceUpdate();


        adViewMessage = findViewById(R.id.adViewMessage);


        adViewMessage = (AdView)findViewById(R.id.adViewMessage);
        AdRequest request = new AdRequest.Builder().build();
        adViewMessage.loadAd(request);
        adViewMessage.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });


        LayoutInflater inflate = LayoutInflater.from(this);
        alertView = inflate.inflate(R.layout.dialog, null);
        Button btn= (Button) alertView.findViewById(R.id.btn);

        showDialog();

        appOpenAdManager = ((MyApplication) getApplication()).getAppOpenAdManager();
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3063877521249388/9384119552");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });







        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setMessage("Please Wait...");
        listView = findViewById(R.id.songsList);
        songsNameList = new ArrayList<>();
        songsUrlList = new ArrayList<>();
        songsArtistList = new ArrayList<>();
        songsDurationList = new ArrayList<>();
        jcAudios = new ArrayList<>();
        thumbnail = new ArrayList<>();
        jcPlayerView = findViewById(R.id.jcplayer);
        retrieveSongs();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    //log.d("TAG", "The intersitial wasn't loaded yet.");
                }
                Song song = (Song)adapter.getItem(i);
                jcPlayerView.playAudio(JcAudio.createFromURL(song.getSongName(), song.getSongUrl()));
                jcPlayerView.setVisibility(View.VISIBLE);
                jcPlayerView.createNotification();
                adapter.notifyDataSetChanged();
            }
        });



        // No Internet Dialog: Signal
        NoInternetDialogSignal.Builder builder = new NoInternetDialogSignal.Builder(
                this,
                getLifecycle()
        );

        DialogPropertiesSignal properties = builder.getDialogProperties();

        properties.setConnectionCallback(new ConnectionCallback() { // Optional
            @Override
            public void hasActiveConnection(boolean hasActiveConnection) {
                // ...
            }
        });

        properties.setCancelable(false); // Optional
        properties.setNoInternetConnectionTitle("No Internet"); // Optional
        properties.setNoInternetConnectionMessage("Check your Internet connection and try again"); // Optional
        properties.setShowInternetOnButtons(true); // Optional
        properties.setPleaseTurnOnText("Please turn on"); // Optional
        properties.setWifiOnButtonText("Wifi"); // Optional
        properties.setMobileDataOnButtonText("Mobile data"); // Optional

        properties.setOnAirplaneModeTitle("No Internet"); // Optional
        properties.setOnAirplaneModeMessage("You have turned on the airplane mode."); // Optional
        properties.setPleaseTurnOffText("Please turn off"); // Optional
        properties.setAirplaneModeOffButtonText("Airplane mode"); // Optional
        properties.setShowAirplaneModeOffButtons(true); // Optional

        builder.build();




    }

    // RETRIEVING THE SONGS FROM THE SERVER
   /* public void retrieveSongs() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Songs");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Song song = ds.getValue(Song.class);
                    songsNameList.add(song.getSongName());
                    songsUrlList.add(song.getSongUrl());
                    songsArtistList.add(song.getSongArtist());
                    songsDurationList.add(song.getSongDuration());
                    thumbnail.add(song.getImageUrl());

                    jcAudios.add(JcAudio.createFromURL(song.getSongName(), song.getSongUrl()));
                }
                adapter = new ListAdapter(getApplicationContext(), songsNameList, thumbnail, songsArtistList, songsDurationList);
                jcPlayerView.initPlaylist(jcAudios, null);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MusicActivity.this, "FAILED!", Toast.LENGTH_SHORT).show();
            }
        });
    }*/

    // RETRIEVING THE SONGS FROM THE SERVER
    public void retrieveSongs() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Songs");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Song> songsList = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Song song = ds.getValue(Song.class);
                    songsList.add(song);
                    jcAudios.add(JcAudio.createFromURL(song.getSongName(), song.getSongUrl()));
                }
                adapter = new ListAdapter(getApplicationContext(), songsList);
                jcPlayerView.initPlaylist(jcAudios, null);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MusicActivity.this, "FAILED!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.searchmenu, menu);

        MenuItem searchItem = menu.findItem(R.id.search);

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search...");
        searchView.setOnQueryTextListener(this);
        searchView.setIconified(false);

        return true;
    }

    @SuppressLint("LongLogTag")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.search) {
            return true;
        }
        else if (id == R.id.Report){
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                //log.d("TAG", "The intersitial wasn't loaded yet.");
            }

                Intent intent = new Intent (Intent.ACTION_VIEW , Uri.parse("mailto:" + "iradukundadidier2@gmail.com"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Reporting musics does not belong to our community ");
                intent.putExtra(Intent.EXTRA_TEXT, "your_text");
                startActivity(intent);
            }

        else if(id == R.id.Rate){
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                //log.d("TAG", "The intersitial wasn't loaded yet.");
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getString(R.string.packegname)));
            startActivity(intent);
        }
        else if (id ==R.id.sharemenu){
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                //log.d("TAG", "The intersitial wasn't loaded yet.");
            }

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    "Hey ! check out this  music service that gives you access to Hundreds  of catholic  songs. through Catholic app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);


        }
        else  if(id == R.id.uploadItem){
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                //log.d("TAG", "The intersitial wasn't loaded yet.");
            }

            if (validatePermissions()){
                Intent intent = new Intent(this,UploadSongActivity.class);
                startActivity(intent);
            }

        }

        return super.onOptionsItemSelected(item);
    }

    // METHOD TO HANDEL RUNTIME PERMISSIONS
    private boolean validatePermissions(){
        Dexter.withContext(getApplicationContext())
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        checkPermission = true;
                    }
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        checkPermission = false;
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
        return checkPermission;

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        numActivityRestarted++;

        if (canShowAppOpenAd()) {
            appOpenAdManager.showAdIfAvailable();
        }
    }

    private boolean canShowAppOpenAd() {
        return numActivityRestarted % 3 == 0;
    }

    public void showDialog(){

        Dialog alertDialog = new Dialog(MusicActivity.this);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(alertView);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {


        Toast.makeText(this, "Query Inserted", Toast.LENGTH_SHORT).show();
        return false;


    }

    @Override
    public boolean onQueryTextChange(String newText) {

        adapter.getFilter().filter(newText);
        return false;

    }
    public void forceUpdate()
    {
        PackageManager packageManager = this.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo =  packageManager.getPackageInfo(getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String currentVersion = packageInfo.versionName;
        new ForceUpdateAsync(currentVersion,MusicActivity.this).execute();
    }

}