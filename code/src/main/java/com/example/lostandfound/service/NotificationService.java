package com.example.lostandfound.service;

import com.example.lostandfound.domain.entity.User;

public interface NotificationService {
    void notify(User recipient, String message);
}