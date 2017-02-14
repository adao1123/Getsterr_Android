package getsterr.imf.models.bing;

/**
 * Created by adao1 on 12/22/2016.
 */

public class BingVideoResult {
    private String _type;
    private String webSearchUrl;
    private int totalEstimateMatches;
    private BingVideoObj[] value;

    public String get_type() {
        return _type;
    }

    public String getWebSearchUrl() {
        return webSearchUrl;
    }

    public int getTotalEstimateMatches() {
        return totalEstimateMatches;
    }

    public BingVideoObj[] getValue() {
        return value;
    }

    public class BingVideoObj{
        private String name;
        private String description;
        private String webSearchUrl;
        private String thumbnailUrl;
        private String datePublished;
        private BingVidCreator creator;
        private String contentUrl;
        private String hostPageUrl;
        private String hostPageDisplayUrl;
        private int width;
        private int height;
        private String duration;
        private String motionThumbnailUrl;
        private int viewCount;
        private String videoId;

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getWebSearchUrl() {
            return webSearchUrl;
        }

        public String getThumbnailUrl() {
            return thumbnailUrl;
        }

        public String getDatePublished() {
            return datePublished;
        }

        public BingVidCreator getCreator() {
            return creator;
        }

        public String getContentUrl() {
            return contentUrl;
        }

        public String getHostPageUrl() {
            return hostPageUrl;
        }

        public String getHostPageDisplayUrl() {
            return hostPageDisplayUrl;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public String getDuration() {
            return duration;
        }

        public String getMotionThumbnailUrl() {
            return motionThumbnailUrl;
        }

        public int getViewCount() {
            return viewCount;
        }

        public String getVideoId() {
            return videoId;
        }

        public class BingVidCreator{
            private String name;

            public String getName() {
                return name;
            }
        }
    }
}
