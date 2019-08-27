package com.yuyue.app.api.service;

import com.yuyue.app.api.domain.JPush;

import java.util.List;

public interface SendSmsService {
    List<JPush> getValid();

    void updateValid(String status, String id);
}
