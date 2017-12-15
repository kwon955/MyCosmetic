package com.example.myapplication;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ToolbarActivity1 extends AppCompatActivity {
    private static final int GALLERY_CODE = 10;
    private TextView TextView;
    private FirebaseAuth auth;
    private FirebaseStorage storage;
    private FirebaseDatabase database;
    private EditText name;
    private EditText memo;
    private EditText sellbydate;
    private EditText open;
    private Button button;
    private String imagePath;
    private ImageView imageView;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private Toolbar toolbar;

    TextView tList[] = new TextView[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar1);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();

        imageView = (ImageView) findViewById(R.id.imageView_cosmetic);
        name = (EditText) findViewById(R.id.name_text);
        memo = (EditText) findViewById(R.id.memo_text);
        sellbydate = (EditText) findViewById(R.id.sbd_text);
        open = (EditText) findViewById(R.id.open_text);
        button = (Button) findViewById(R.id.button_ok);


        tList[0] = (TextView) findViewById(R.id.id1);
        tList[1] = (TextView) findViewById(R.id.id2);
        tList[2] = (TextView) findViewById(R.id.id3);


         /*권한*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, GALLERY_CODE);

            }
        });

    }


    @SuppressWarnings("StatementWithEmptyBody")
    void colorReset() {
        for (TextView x : tList) {
            x.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id1:
                break;
            case R.id.id2:
                startActivityForResult(new Intent(ToolbarActivity1.this, MyCos.class), 2);
                overridePendingTransition(0, 0);
                break;
            case R.id.id3:
                startActivityForResult(new Intent(ToolbarActivity1.this, RakeActivity.class), 3);
                overridePendingTransition(0, 0);
                break;
            case R.id.button_ok:

                if (imagePath == null) {
                    Intent intent = new Intent(ToolbarActivity1.this, MyCos.class);
                    startActivity(intent);
                } else {
                    upload(imagePath);
                    Intent intent = new Intent(ToolbarActivity1.this, MyCos.class);
                    startActivity(intent);

                }

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GALLERY_CODE) {
            imagePath = getPath(data.getData());
            File f = new File(imagePath);
            imageView.setImageURI(Uri.fromFile(f));
        }

        switch (data.getIntExtra("result", 6)) {
            case 2:
                startActivityForResult(new Intent(ToolbarActivity1.this, MyCos.class), 2);
                overridePendingTransition(0, 0);
                break;
            case 3:
                startActivityForResult(new Intent(ToolbarActivity1.this, RakeActivity.class), 3);
                overridePendingTransition(0, 0);
                break;
            default:
                break;
        }
    }


    public String getPath(Uri uri) {

        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(this, uri, proj, null, null, null);

        Cursor cursor = cursorLoader.loadInBackground();
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(index);

    }

    private void upload(String uri) {


        StorageReference storageRef = storage.getReferenceFromUrl("gs://kwondaeuk-a4c57.appspot.com/");

        final Uri file = Uri.fromFile(new File(uri));
        StorageReference riversRef = storageRef.child("images/" + file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);

// Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                @SuppressWarnings("VisibleForTests")
                Uri downloadUrl = taskSnapshot.getDownloadUrl();


                ImageDTO imageDTO = new ImageDTO();
                imageDTO.imageUrl = downloadUrl.toString();
                imageDTO.name = name.getText().toString();
                imageDTO.memo = memo.getText().toString();
                imageDTO.open = open.getText().toString();
                imageDTO.sellbydate = sellbydate.getText().toString();
                imageDTO.uid = auth.getCurrentUser().getUid();
                imageDTO.userId = auth.getCurrentUser().getEmail();
                imageDTO.imageName = file.getLastPathSegment();
                database.getReference().child("images").push().setValue(imageDTO);
            }
        });
    }
    String get_date(String a,String b) throws ParseException {
        DateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date today = new Date(sdf.parse(b).getTime());
        Date date = new Date(sdf.parse(a).getTime());
        int diff = today.getDate() - date.getDate();
        return String.valueOf(diff);
    }
}