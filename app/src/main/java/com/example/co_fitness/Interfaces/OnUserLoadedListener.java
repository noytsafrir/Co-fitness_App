package com.example.co_fitness.Interfaces;

import com.example.co_fitness.Model.User;

public interface OnUserLoadedListener {
    void onUserLoaded(User user);
    void onUserNotFound();
    void onDataError(String errorMessage);
}