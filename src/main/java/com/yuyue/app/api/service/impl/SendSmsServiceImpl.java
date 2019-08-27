package com.yuyue.app.api.service.impl;

import com.yuyue.app.api.domain.JPush;
import com.yuyue.app.api.mapper.SendSmsMapper;
import com.yuyue.app.api.service.SendSmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(value = "SendSmsService")
public class SendSmsServiceImpl implements SendSmsService {

    @Autowired
    private SendSmsMapper sendSmsMapper;

    @Override
    public List<JPush> getValid() { return sendSmsMapper.getValid(); }
}
