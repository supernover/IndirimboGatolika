package com.Gatolika.Indirimboo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Uploading extends AppCompatActivity {
    Uri uriSong, image;
    byte[] bytes;
    String fileName, songUrl, imageUrl;
    String songLength;
    private StorageReference storageReference;
    ProgressDialog progressDialog;
    EditText selectSongNameEditText;
    EditText artistName;
    ImageView selectImage;
    Button uploadButton;
    ImageButton selectSong;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_song);




        Objects.requireNonNull(getSupportActionBar()).setTitle("\uD835\uDD18\uD835\uDD2D\uD835\uDD29\uD835\uDD2C\uD835\uDD1E\uD835\uDD21 \uD835\uDD36\uD835\uDD2C\uD835\uDD32\uD835\uDD2F \uD835\uDD2C\uD835\uDD34\uD835\uDD2B \uD835\uDD10\uD835\uDD32\uD835\uDD30\uD835\uDD26\uD835\uDD20 /???\uD835\uDD25\uD835\uDD2C\uD835\uDD26\uD835\uDD2F");
        storageReference = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(this);

        selectSongNameEditText = findViewById(R.id.selectSong);
        selectImage = findViewById(R.id.selectImage);
        uploadButton = findViewById(R.id.uploadSongButton);
        artistName = findViewById(R.id.artistNameEditText);
        selectSong = findViewById(R.id.selectSongButton);

        selectSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickSong();
            }
        });

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });


        new FancyGifDialog.Builder(this)
                .setTitle("WARNING!") // You can also send title like R.string.from_resources
                .setMessage("\uD835\uDD13\uD835\uDD29\uD835\uDD22\uD835\uDD1E\uD835\uDD30\uD835\uDD22 \uD835\uDD32\uD835\uDD2D\uD835\uDD29\uD835\uDD2C\uD835\uDD1E\uD835\uDD21 \uD835\uDD2A\uD835\uDD32\uD835\uDD30\uD835\uDD26\uD835\uDD20 \uD835\uDD31\uD835\uDD25\uD835\uDD1E\uD835\uDD31  \uD835\uDD1F\uD835\uDD22\uD835\uDD29\uD835\uDD2C\uD835\uDD2B\uD835\uDD24 \uD835\uDD31\uD835\uDD2C \uD835\uDD20\uD835\uDD1E\uD835\uDD31\uD835\uDD25\uD835\uDD2C\uD835\uDD29\uD835\uDD26\uD835\uDD20 \uD835\uDD20\uD835\uDD25\uD835\uDD32\uD835\uDD2F\uD835\uDD20\uD835\uDD25 \uD835\uDD2C\uD835\uDD2B\uD835\uDD29\uD835\uDD36 ,???\uD835\uDD23 \uD835\uDD36\uD835\uDD2C\uD835\uDD32 \uD835\uDD32\uD835\uDD30\uD835\uDD22 \uD835\uDD20\uD835\uDD2C\uD835\uDD2D\uD835\uDD36\uD835\uDD2F\uD835\uDD26\uD835\uDD24\uD835\uDD25\uD835\uDD31\uD835\uDD22\uD835\uDD21 \uD835\uDD2A\uD835\uDD32\uD835\uDD30\uD835\uDD26\uD835\uDD20 \uD835\uDD34\uD835\uDD26\uD835\uDD31\uD835\uDD25\uD835\uDD2C\uD835\uDD32\uD835\uDD31 \uD835\uDD1E \uD835\uDD29\uD835\uDD26\uD835\uDD20\uD835\uDD22\uD835\uDD2B\uD835\uDD30\uD835\uDD22 \uD835\uDD1E\uD835\uDD2B\uD835\uDD21  \uD835\uDD2B\uD835\uDD2C\uD835\uDD2B \uD835\uDD20\uD835\uDD1E\uD835\uDD31\uD835\uDD25\uD835\uDD2C\uD835\uDD29\uD835\uDD26\uD835\uDD20 \uD835\uDD2A\uD835\uDD32\uD835\uDD30\uD835\uDD26\uD835\uDD20  \uD835\uDD25\uD835\uDD22\uD835\uDD2F\uD835\uDD22, \uD835\uDD36\uD835\uDD2C\uD835\uDD32 \uD835\uDD20\uD835\uDD1E\uD835\uDD2B \uD835\uDD24\uD835\uDD22\uD835\uDD31 \uD835\uDD1E \uD835\uDD20\uD835\uDD2C\uD835\uDD2D\uD835\uDD36\uD835\uDD2F\uD835\uDD26\uD835\uDD24\uD835\uDD25\uD835\uDD31 \uD835\uDD30\uD835\uDD31\uD835\uDD2F\uD835\uDD26\uD835\uDD28\uD835\uDD22. \uD835\uDD26\uD835\uDD2B\uD835\uDD21\uD835\uDD26\uD835\uDD2F\uD835\uDD26\uD835\uDD2A\uD835\uDD1F\uD835\uDD2C \uD835\uDD0A\uD835\uDD1E\uD835\uDD31\uD835\uDD2C\uD835\uDD29\uD835\uDD26\uD835\uDD28\uD835\uDD1E \uD835\uDD20\uD835\uDD2C\uD835\uDD32\uD835\uDD29\uD835\uDD21 \uD835\uDD1F\uD835\uDD29\uD835\uDD2C\uD835\uDD20\uD835\uDD28 \uD835\uDD36\uD835\uDD2C\uD835\uDD32\uD835\uDD2F \uD835\uDD1E\uD835\uDD20\uD835\uDD20\uD835\uDD22\uD835\uDD30\uD835\uDD30 \uD835\uDD1E\uD835\uDD2B\uD835\uDD21 \uD835\uDD2F\uD835\uDD22\uD835\uDD2A\uD835\uDD2C\uD835\uDD33\uD835\uDD22 \uD835\uDD36\uD835\uDD2C\uD835\uDD32\uD835\uDD2F  \uD835\uDD2A\uD835\uDD32\uD835\uDD30\uD835\uDD26\uD835\uDD20  \uD835\uDD21\uD835\uDD26\uD835\uDD2F\uD835\uDD22\uD835\uDD20\uD835\uDD31\uD835\uDD29\uD835\uDD36.") // or pass like R.string.description_from_resources
                .setNegativeBtnText("Cancel") // or pass it like android.R.string.cancel
                .setPositiveBtnBackground(R.color.infoColor) // or pass it like R.color.positiveButton
                .setPositiveBtnText("Ok") // or pass it like android.R.string.ok
                .setNegativeBtnBackground(R.color.errorColor) // or pass it like R.color.negativeButton
                .setGifResource(R.drawable.attention)   //Pass your Gif here
                .isCancellable(true)
                .OnPositiveClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {


                        Toast.makeText(Uploading.this,"Ok",Toast.LENGTH_SHORT).show();
                    }
                })
                .OnNegativeClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        Intent intent = new Intent(getApplicationContext(), MusicMainActivity.class);
                        startActivity(intent);
                        Toast.makeText(Uploading.this,"Cancel",Toast.LENGTH_SHORT).show();
                    }
                })
                .build();
    }

    // SELECT THE SONG TO UPLOAD FROM MOBILE STORAGE
    private void pickSong() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent,1);
    }

    private void  pickImage(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,2);
    }

    // AFTER SELECTING THE SONG FROM MOBILE STORAGE
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == 1 && resultCode == RESULT_OK) {
                uriSong = data.getData();
//                Log.i("uri", songName.toString());
                fileName = getFileName(uriSong);
                selectSongNameEditText.setText(fileName);
                songLength = getSongDuration(uriSong);
                Log.i("duration", songLength);
            }
            if (requestCode == 2 && resultCode == RESULT_OK){
//                Log.i("image",data.toString());
                image = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),image);
                    selectImage.setImageBitmap(bitmap);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
                    bytes = byteArrayOutputStream.toByteArray();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void upload(View view){
        if (uriSong == null){
            Toast.makeText(this, "Please select a song", Toast.LENGTH_SHORT).show();
        }
        else if (selectSongNameEditText.getText().toString().equals("")){
            Toast.makeText(this, "Song name cannot be empty!", Toast.LENGTH_SHORT).show();
        }
        else if(artistName.getText().toString().equals("")){
            Toast.makeText(this, "Please add Artist, album name", Toast.LENGTH_SHORT).show();
        }
        else if (image == null){
            Toast.makeText(this, "Please select a Thumbnail", Toast.LENGTH_SHORT).show();
        }
        else {
            fileName = selectSongNameEditText.getText().toString();
            String artist = artistName.getText().toString();
            uploadImageToServer(bytes,fileName);
            uploadFileToServer(uriSong,fileName,artist,songLength);
        }
    }

    public void uploadImageToServer(byte[] image, String fileName) {
        UploadTask uploadTask = storageReference.child("Thumbnails").child(fileName).putBytes(image);
        progressDialog.show();
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                while (!task.isComplete());
                Uri urlsong = task.getResult();
                imageUrl = urlsong.toString();
//                Log.i("image url", imageUrl);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("image url", "failed");
            }
        });
    }

    // METHOD TO HANDEL SONG UPLOAD TO THE STORAGE SERVER
    public void uploadFileToServer(Uri uri, final String songName, final String artist, final String duration){
        StorageReference filePath = storageReference.child("Audios").child(songName);
        progressDialog.show();
        filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Log.i("success", "upload");
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlSong = uriTask.getResult();
                songUrl = urlSong.toString();
//                Log.i("success url ", songUrl);
                uploadDetailsToDatabase(fileName,songUrl,imageUrl,artist,duration);
//                progressDialog.dismiss();
            }

        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                int currentProgress = (int) progress;
                progressDialog.setMessage("Uploading: " + currentProgress + "%");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Log.i("success", "upload");
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Upload Failed! Please Try again!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // UPLOAD SONG NAME AND URL TO REALTIME DATABASE
    public void uploadDetailsToDatabase(String songName, String songUrl, String imageUrl, String artistName, String songDuration){

        Song song = new Song(songName,songUrl,imageUrl,artistName,songDuration);
        FirebaseDatabase.getInstance().getReference("Songs")
                .push().setValue(song).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.i("database", "upload success");
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Song Uploaded to Database", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    // METHOD TO GET THE SONG NAME
    public String getFileName(Uri uri) {
        String result = null;
        if (Objects.equals(uri.getScheme(), "content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            assert result != null;
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public String getSongDuration(Uri song){
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(getApplicationContext(),song);
        String durationString = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long time = Long.parseLong(durationString);
        int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(time);
        int totalSeconds = (int) TimeUnit.MILLISECONDS.toSeconds(time);
        int seconds = totalSeconds-(minutes*60);
        if (String.valueOf(seconds).length() == 1){
            return minutes + ":0" + seconds;
        }else {
            return minutes + ":" + seconds;
        }
    }
}