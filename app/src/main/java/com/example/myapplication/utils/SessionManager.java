package com.example.myapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "ElearningPrefs";
    private static final String KEY_USER_ID   = "userId";
    private static final String KEY_USER_ROLE = "userRole";
    private static final String KEY_USER_NAME = "userName";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs  = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveSession(String userId, String role, String name) {
        editor.putString(KEY_USER_ID,   userId);
        editor.putString(KEY_USER_ROLE, role);
        editor.putString(KEY_USER_NAME, name);
        editor.apply();
    }

    public String getUserId()   { return prefs.getString(KEY_USER_ID,   null); }
    public String getUserRole() { return prefs.getString(KEY_USER_ROLE, null); }
    public String getUserName() { return prefs.getString(KEY_USER_NAME, null); }
    public boolean isLoggedIn() { return getUserId() != null; }
    public void clearSession()  { editor.clear().apply(); }
}
