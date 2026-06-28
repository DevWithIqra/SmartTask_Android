package com.iqra.smarttask.repository;

import com.google.firebase.auth.FirebaseAuth;

public class AuthRepository {

    private final FirebaseAuth firebaseAuth;

    public AuthRepository() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }
}