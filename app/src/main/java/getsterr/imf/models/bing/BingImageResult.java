package getsterr.imf.models.bing;

/**
 * Created by adao1 on 12/22/2016.
 */

public class BingImageResult {
    private String webSearchUrl;
    private int totalEstimatedMatches;
    private BingImageObj[] value;

    public String getWebSearchUrl() {
        return webSearchUrl;
    }

    public int getTotalEstimatedMatches() {
        return totalEstimatedMatches;
    }

    public BingImageObj[] getValue() {
        return value;
    }

    public class BingImageObj{
        private String name;
        private String webSearchUrl;
        private String thumbnailUrl;
        private String datePublished;
        private String contentUrl;
        private String hostPageUrl;
        private String contentSize;
        private String encodingFormat;
        private String hostPageDisplayUrl;
        private int width;
        private int height;
        private BingImgThumbnail thumbnail;
        private String imageInsightsToken;
        private String imageId;
        private String accentColor;

        public String getName() {
            return name;
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

        public String getContentUrl() {
            return contentUrl;
        }

        public String getHostPageUrl() {
            return hostPageUrl;
        }

        public String getContentSize() {
            return contentSize;
        }

        public String getEncodingFormat() {
            return encodingFormat;
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

        public BingImgThumbnail getThumbnail() {
            return thumbnail;
        }

        public String getImageInsightsToken() {
            return imageInsightsToken;
        }

        public String getImageId() {
            return imageId;
        }

        public String getAccentColor() {
            return accentColor;
        }

        public class BingImgThumbnail{
            private int width;
            private int height;

            public int getWidth() {
                return width;
            }

            public int getHeight() {
                return height;
            }
        }
    }
}
