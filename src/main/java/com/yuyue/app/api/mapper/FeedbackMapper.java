package com.yuyue.app.api.mapper;

import com.yuyue.app.api.domain.Feedback;
import org.apache.ibatis.annotations.Insert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface FeedbackMapper extends MyBaseMapper<Feedback> {

    @Transactional
    @Insert("INSERT INTO yuyue_feedback (id,contact,pictureUrl,details,userId) VALUES " +
            "(#{id},#{contact},#{pictureUrl},#{details},#{userId})")
    void insertFeedback(Feedback feedback);
}
