package ch.cf.leflaneur.model;

/**
 * Created by christianfallegger on 13.04.17.
 */

public class AppState {
    private boolean guideMode;
    private boolean searchMode;
    private boolean summaryMode;
    private boolean audioMode;

    private boolean debugMode = false;

    public AppState() {
        this.summaryMode = false;
        this.guideMode = false;
        this.searchMode = false;
        this.audioMode = false;
    }

    public boolean isGuideMode() {
        return guideMode;
    }

    public void setGuideMode(boolean guideMode) {
        if (guideMode) {
            this.searchMode = false;
            this.summaryMode = false;
        }
        this.guideMode = guideMode;
    }

    public boolean isSearchMode() {
        return searchMode;
    }

    public void setSearchMode(boolean searchMode) {
        if (!this.guideMode) {
            this.searchMode = searchMode;
        }
    }

    public boolean isSummaryMode() {
        return this.summaryMode;
    }

    public void setSummaryMode(Navigation navigation) {
        if (navigation.isRouteSet() && !this.guideMode){
            this.setSummaryMode(true);
        }else{
            this.setSummaryMode(false);
        }

    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setSummaryMode(boolean summaryMode) {
        this.summaryMode = summaryMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public boolean isAudioMode() {
        return audioMode;
    }

    public void setAudioMode(boolean audioMode) {
        this.audioMode = audioMode;
    }
}
