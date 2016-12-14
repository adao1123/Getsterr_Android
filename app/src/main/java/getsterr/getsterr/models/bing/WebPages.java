package getsterr.getsterr.models.bing;

import java.util.List;

/**
 * Created by samsiu on 11/21/16.
 */

public class WebPages {

    String webSearchUrl;
    int totalEstimatedMatches;
    List<Value> value;

    public String getWebSearchUrl() {
        return webSearchUrl;
    }

    public int getTotalEstimatedMatches() {
        return totalEstimatedMatches;
    }

    public List<Value> getValue() {
        return value;
    }
}
