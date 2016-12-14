package getsterr.getsterr.models.bing;

/**
 * Created by samsiu on 11/13/16.
 */

public class BingResultCard {

    String title;
    String time;
    String snippet;
    String url;

    public BingResultCard(String title, String time, String snippet, String url) {
        this.title = title;
        this.time = time;
        this.snippet = snippet;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public String getSnippet() {
        return snippet;
    }

    public String getUrl() {
        return url;
    }
}
