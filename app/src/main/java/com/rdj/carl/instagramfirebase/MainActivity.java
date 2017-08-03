package com.rdj.carl.instagramfirebase;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final int CHOOSER_IMAGE = 1;
    private static final String TAG = "MainActivity";
    private Button bDownload;
    private Button bUpload;
    private ImageView ivImage;
    private FirebaseApplication firebaseApplication;
    /*********************************************/
    private StorageReference storageReference;
    /*********************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseApplication = (FirebaseApplication) getApplicationContext();
        storageReference = firebaseApplication.getStorageReference();

        bDownload = (Button) findViewById(R.id.bDownload);
        bUpload = (Button) findViewById(R.id.bUpload);
        ivImage = (ImageView) findViewById(R.id.ivImage);

        ivImage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), CHOOSER_IMAGE);
            }
        });


        bUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StorageReference reference = storageReference.child("postImages/" + "prueba.png");

                ivImage.setDrawingCacheEnabled(true);
                ivImage.buildDrawingCache();
                Bitmap bitmap = ivImage.getDrawingCache();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] data = baos.toByteArray();


                UploadTask uploadTask = reference.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error en subida");
                        e.printStackTrace();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(firebaseApplication, "Se subio correctamente", Toast.LENGTH_SHORT).show();
                        String downloadUri = taskSnapshot.getDownloadUrl().getPath();
                        Log.w(TAG, "Imagen URL: "+downloadUri);
                    }
                });
            }
        });




        bDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final File file;
                try {
                    file = File.createTempFile("robot", "jpg");
                    storageReference.child("robot.jpg").getFile(file)
                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                                    ivImage.setImageBitmap(bitmap);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Ocurrio un error al mostrar la imagen");
                            e.printStackTrace();
                        }
                    });
                }catch (Exception e){
                    Log.e(TAG, "Ocurrió un error en la descarga de imágenes");
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSER_IMAGE){
            Uri uri = data.getData();
            Toast.makeText(firebaseApplication, uri.toString(), Toast.LENGTH_SHORT).show();
            if (uri != null){
                ivImage.setImageURI(uri);
            }
        }
    }
}
