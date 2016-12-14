package getsterr.getsterr.models.youtube;

import com.google.gson.annotations.SerializedName;

/**
 * Created by adao1 on 12/7/2016.
 */

public class YoutubeObject {
    private String kind;
    private String etag;
    private String nextPageToken;
    private String prevPageToken;
    private String regionCode;
    private PageInfoObj pageInfo;
    private Resource[] items;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public String getPrevPageToken() {
        return prevPageToken;
    }

    public void setPrevPageToken(String prevPageToken) {
        this.prevPageToken = prevPageToken;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public PageInfoObj getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfoObj pageInfo) {
        this.pageInfo = pageInfo;
    }

    public Resource[] getItems() {
        return items;
    }

    public void setItems(Resource[] items) {
        this.items = items;
    }

    public class PageInfoObj{
        private int totalResults;
        private int resultsPerPage;

    }

    public class Resource{
        private IdObject id;
        private SnippetObject snippet;

        public Resource(IdObject id, SnippetObject snippet) {
            this.id = id;
            this.snippet = snippet;
        }

        public IdObject getId() {
            return id;
        }

        public void setId(IdObject id) {
            this.id = id;
        }

        public SnippetObject getSnippet() {
            return snippet;
        }

        public void setSnippet(SnippetObject snippet) {
            this.snippet = snippet;
        }

        public class SnippetObject{
            private String title;
            private String description;
            private ThumbnailObject thumbnails;



            public SnippetObject(String title, String description, ThumbnailObject thumbnails) {
                this.title = title;
                this.description = description;
                this.thumbnails = thumbnails;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public ThumbnailObject getThumbnails() {
                return thumbnails;
            }

            public void setThumbnails(ThumbnailObject thumbnails) {
                this.thumbnails = thumbnails;
            }

            public class ThumbnailObject{
                @SerializedName("default")
                private ThumbnailTypeObj normal;
                private ThumbnailTypeObj medium;
                private ThumbnailTypeObj high;

                public ThumbnailObject(ThumbnailTypeObj normal, ThumbnailTypeObj medium, ThumbnailTypeObj high) {
                    this.normal = normal;
                    this.medium = medium;
                    this.high = high;
                }

                public ThumbnailTypeObj getNormal() {
                    return normal;
                }

                public void setNormal(ThumbnailTypeObj normal) {
                    this.normal = normal;
                }

                public ThumbnailTypeObj getMedium() {
                    return medium;
                }

                public void setMedium(ThumbnailTypeObj medium) {
                    this.medium = medium;
                }

                public ThumbnailTypeObj getHigh() {
                    return high;
                }

                public void setHigh(ThumbnailTypeObj high) {
                    this.high = high;
                }

                public class ThumbnailTypeObj{
                    private String url;

                    public ThumbnailTypeObj(String url) {
                        this.url = url;
                    }

                    public String getUrl() {
                        return url;
                    }

                    public void setUrl(String url) {
                        this.url = url;
                    }
                }

            }
        }

        public class IdObject{
            private String kind;
            private String videoId;
            private String channelId;
            private String playlistId;

            public IdObject(String kind, String videoId, String channelId, String playlistId) {
                this.kind = kind;
                this.videoId = videoId;
                this.channelId = channelId;
                this.playlistId = playlistId;
            }

            public String getKind() {
                return kind;
            }

            public void setKind(String kind) {
                this.kind = kind;
            }

            public String getVideoId() {
                return videoId;
            }

            public void setVideoId(String videoId) {
                this.videoId = videoId;
            }

            public String getChannelId() {
                return channelId;
            }

            public void setChannelId(String channelId) {
                this.channelId = channelId;
            }

            public String getPlaylistId() {
                return playlistId;
            }

            public void setPlaylistId(String playlistId) {
                this.playlistId = playlistId;
            }
        }
    }

}
