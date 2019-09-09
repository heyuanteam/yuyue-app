package com.yuyue.app.api.service.impl;
import com.yuyue.app.api.domain.Barrage;
import com.yuyue.app.api.mapper.BarrageMapper;
import com.yuyue.app.api.service.BarrageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service(value = "BarrageService")
public class BarrageServiceImpl implements BarrageService {

    @Autowired
    private BarrageMapper barrageMapper;

    @Override
    public List<Barrage> getBarrages(String videoId,int startTime,int endTime) {
        return barrageMapper.getBarrages(videoId,startTime,endTime);
    }

    @Override
    public void addBarrage(Barrage barrage) { barrageMapper.addBarrage(barrage); }
}
