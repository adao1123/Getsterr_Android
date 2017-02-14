package getsterr.imf.models.giphy;

/**
 * Created by adao1 on 2/13/2017.
 */

public class GiphyObject {
    private Giphy[] data;

    public Giphy[] getData() {
        return data;
    }

    public class Giphy {
        private String id;
        private String slug;
        private String url;
        private String embed_url;
        private String username;
        private String source;
        private String rating;
        private String caption;
        private String content_url;
        private String import_datetime;
        private String trending_datetime;
        private GiphyImages images;

        public String getId() {
            return id;
        }

        public String getSlug() {
            return slug;
        }

        public String getUrl() {
            return url;
        }

        public String getEmbed_url() {
            return embed_url;
        }

        public String getUsername() {
            return username;
        }

        public String getSource() {
            return source;
        }

        public String getRating() {
            return rating;
        }

        public String getCaption() {
            return caption;
        }

        public String getContent_url() {
            return content_url;
        }

        public String getImport_datetime() {
            return import_datetime;
        }

        public String getTrending_datetime() {
            return trending_datetime;
        }

        public GiphyImages getImages() {
            return images;
        }

        public class GiphyImages{
            private GiphyStill original_still;
            private GiphyFixedHeight fixed_height;

            public GiphyStill getOriginal_still() {
                return original_still;
            }

            public GiphyFixedHeight getFixed_height() {
                return fixed_height;
            }

            public class GiphyStill{
                private String url;
                private String width;
                private String height;
                private String size;

                public String getUrl() {
                    return url;
                }

                public String getWidth() {
                    return width;
                }

                public String getHeight() {
                    return height;
                }

                public String getSize() {
                    return size;
                }
            }
            public class GiphyFixedHeight{
                private String url;

                public String getUrl() {
                    return url;
                }
            }

        }

    }
}
