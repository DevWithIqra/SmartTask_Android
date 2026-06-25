package com.iqra.smarttask.utils;

import android.text.TextUtils;
import android.util.Patterns;

public class Validator {

    public static boolean isValidEmail(String email) {

        return !TextUtils.isEmpty(email)
                && Patterns.EMAIL_ADDRESS.matcher(email).matches();

    }

    public static boolean isValidPassword(String password) {

        return password.length() >= 6;

    }

}