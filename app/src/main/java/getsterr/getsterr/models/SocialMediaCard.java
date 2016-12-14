package getsterr.getsterr.models;

import android.graphics.drawable.Drawable;

/**
 * Created by samsiu on 11/13/16.
 */

public class SocialMediaCard {
    
    String info;
    int buttonColor;
    int buttonIcon;
    String time;
    String url;

    public SocialMediaCard(String info, int buttonColor, int buttonIcon, String time, String url) {
        this.info = info;
        this.buttonColor = buttonColor;
        this.buttonIcon = buttonIcon;
        this.time = time;
        this.url = url;
    }

    public String getInfo() {
        return info;
    }

    public int getButtonColor() {
        return buttonColor;
    }

    public int getButtonIcon() {
        return buttonIcon;
    }

    public String getTime() {
        return time;
    }

    public String getUrl() {
        return url;
    }
}
