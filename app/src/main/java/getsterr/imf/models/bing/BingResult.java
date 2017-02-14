package getsterr.imf.models.bing;

/**
 * Created by samsiu on 11/21/16.
 */

public class BingResult {

    String _type;
    WebPages webPages;
    RelatedSearches relatedSearches;
    Videos videos;
    RankingResponse rankingResponse;

    public String get_type() {
        return _type;
    }

    public WebPages getWebPages() {
        return webPages;
    }

    public RelatedSearches getRelatedSearches() {
        return relatedSearches;
    }

    public Videos getVideos() {
        return videos;
    }

    public RankingResponse getRankingResponse() {
        return rankingResponse;
    }
}
