package com.macro.mall.model;

import java.io.Serializable;
import java.util.Date;

public class UmsMemberCheckin implements Serializable {
    private Long id;
    private Long memberId;
    private Date checkinDate;
    private Integer integration;
    private Integer continuousDays;
    private Date createTime;

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }

    public Date getCheckinDate() { return checkinDate; }
    public void setCheckinDate(Date checkinDate) { this.checkinDate = checkinDate; }

    public Integer getIntegration() { return integration; }
    public void setIntegration(Integer integration) { this.integration = integration; }

    public Integer getContinuousDays() { return continuousDays; }
    public void setContinuousDays(Integer continuousDays) { this.continuousDays = continuousDays; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}