package com.physicianconnect.presentation;

public interface LoginView extends Viewable {
    void promptCredentials();
    String getUsername();
    String getPassword();
    void onLoginSuccess(int userId);
    void onLoginFailure(String errorMessage);
}
