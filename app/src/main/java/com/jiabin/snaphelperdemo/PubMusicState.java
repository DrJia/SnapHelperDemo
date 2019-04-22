package com.jiabin.snaphelperdemo;

public class PubMusicState {

    public boolean isLoading;
    public boolean isPausing;
    public boolean isPlaying;
    public boolean isError;
    public boolean isDownloading;
    public boolean isActivating;//激活态，代表位于第一位

    @Override
    public String toString() {
        return "PubMusicState{" +
                "isLoading=" + isLoading +
                ", isPausing=" + isPausing +
                ", isPlaying=" + isPlaying +
                ", isError=" + isError +
                ", isDownloading=" + isDownloading +
                ", isActivating=" + isActivating +
                '}';
    }
}
