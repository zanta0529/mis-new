package com.github.tonivade.resp.Model;

/**
 * @author kkw
 *
 *
 */

public class ChannelStatus {

    private boolean isAuthorized;

    public ChannelStatus(boolean status) {
        this.isAuthorized = status;
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public void setAuthorized(boolean authorized) {
        isAuthorized = authorized;
    }
}
