package com.byeon.task.service.threadlocal;

public class ThreadLocalUserIdService implements ThreadLocalSaveUserID{
    private final ThreadLocal<String> userIdStore = new ThreadLocal<>();

    @Override
    public void saveUserId(String userId) {
        userIdStore.set(userId);
    }

    @Override
    public String getUserId() {
        return userIdStore.get();
    }

    @Override
    public void removeStoredUserId() {
        userIdStore.remove();
    }
}
