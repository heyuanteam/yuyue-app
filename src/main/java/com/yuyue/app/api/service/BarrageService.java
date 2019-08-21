package com.yuyue.app.api.service;

import com.yuyue.app.api.domain.Barrage;

import java.util.List;

public interface BarrageService {
    List<Barrage> getBarrages(String videoId);
    void addBarrage(Barrage barrage);
}
