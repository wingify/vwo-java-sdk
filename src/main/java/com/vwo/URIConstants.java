package com.vwo;

public enum URIConstants {

    BASE_URL("dev.visualwebsiteoptimizer.com"),
    ACCOUNT_SETTINGS("/server-side/settings"),
    TRACK_USER("/server-side/track-user"),
    TRACK_GOAL("/server-side/track-goal");


    private final String uri;

    URIConstants(String uri) {
        this.uri=uri;
    }

    @Override
    public String toString(){
        return this.uri;
    }
}
