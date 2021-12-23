package com.workerman.app.vo;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by bada
 */
@Data
@EqualsAndHashCode(callSuper = false)
/* 푸시 수신 정보*/
public class WorkerMemberPushData implements Serializable {
    private static final long serialVersionUID = -1014301065500304171L;

    String badge_count="";
    String body="";
    String title="";
    String work_no="";
    String action_type="";
    String admin_no="";
    String sendTime;
}

