package com.workerman.app.vo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by bada on 2017-11-20.
 */
@Data
@AllArgsConstructor

public class WifiInformation implements Serializable {
    private static final long serialVersionUID = -4835168041944895102L;

    private String ip;
    private String subnet;
    private String gateway;
    private String dns;
    private String ssid;
    private String mac_address;

}
