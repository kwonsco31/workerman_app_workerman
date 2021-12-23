package com.workerman.app.net;

import com.workerman.app.net.model.ResBaseItem;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by bada on 2017-12-05.
 */
@Data
@EqualsAndHashCode(callSuper=false)
/*서버 점검 응답형식 선언*/
public class ResponseCheckServer extends ResBaseItem {

    CheckServer result;

    @Data
    public static class CheckServer{
        String banner;
        String state;
        String message;
    }
}
