package com.rdj.carl.instagramfirebase;

import android.app.Application;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by SEVEN on 8/2/2017.
 */

public class FirebaseApplication extends Application {
    private FirebaseStorage firebaseStorage;

    @Override
    public void onCreate() {
        super.onCreate();
        firebaseStorage = FirebaseStorage.getInstance();
    }

    public StorageReference getStorageReference(){
        return firebaseStorage.getReference();
    }
}
