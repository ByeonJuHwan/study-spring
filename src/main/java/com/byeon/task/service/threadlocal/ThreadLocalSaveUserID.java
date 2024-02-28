package com.byeon.task.service.threadlocal;

public interface ThreadLocalSaveUserID {

    void saveUserId(String userId);

    String getUserId();

    void removeStoredUserId();

}
