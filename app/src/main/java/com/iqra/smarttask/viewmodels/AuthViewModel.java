package com.iqra.smarttask.viewmodels;

import androidx.lifecycle.ViewModel;

import com.iqra.smarttask.repository.AuthRepository;

public class AuthViewModel extends ViewModel {

    private final AuthRepository authRepository;

    public AuthViewModel() {
        authRepository = new AuthRepository();
    }

    public AuthRepository getAuthRepository() {
        return authRepository;
    }
}