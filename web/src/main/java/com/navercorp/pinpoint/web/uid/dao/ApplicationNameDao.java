package com.navercorp.pinpoint.web.uid.dao;

import com.navercorp.pinpoint.common.server.uid.ApplicationUid;
import com.navercorp.pinpoint.common.server.uid.ServiceUid;

import java.util.List;

public interface ApplicationNameDao {

    List<String> selectApplicationNames(ServiceUid serviceUid);

    String selectApplicationName(ServiceUid serviceUid, ApplicationUid applicationUid);

    void deleteApplicationName(ServiceUid serviceUid, ApplicationUid applicationUid);
}
