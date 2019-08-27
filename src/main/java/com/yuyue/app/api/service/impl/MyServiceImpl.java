package com.yuyue.app.api.service.impl;

import com.yuyue.app.api.domain.Feedback;
import com.yuyue.app.api.mapper.FeedbackMapper;
import com.yuyue.app.api.service.MyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "MyService")
public class MyServiceImpl implements MyService {

    @Autowired
    private FeedbackMapper feedbackMapper;

    @Override
    public void insertFeedback(Feedback feedback) { feedbackMapper.insertFeedback(feedback); }
}
