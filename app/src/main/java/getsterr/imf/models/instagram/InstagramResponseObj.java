package getsterr.imf.models.instagram;

import com.google.gson.annotations.SerializedName;

/**
 * Created by adao1 on 12/7/2016.
 */

public class InstagramResponseObj {
    @SerializedName("data")
    private InstagramData[] instagramData;

    public InstagramResponseObj(InstagramData[] instagramData) {
        this.instagramData = instagramData;
    }

    public InstagramData[] getInstagramData() {
        return instagramData;
    }

    public class InstagramData{
        private String type;
        private String[] tags;
        private InstagramCaptionObj caption;
        private InstagramLikesObj likes;
        private String link;
        private InstagramUserObj user;
        private String created_time;
        private InstagramImageObj images;
        private String id;

        public InstagramData(String type, String[] tags, InstagramCaptionObj caption, InstagramLikesObj likes, String link, InstagramUserObj user, String created_time, InstagramImageObj images, String id) {
            this.type = type;
            this.tags = tags;
            this.caption = caption;
            this.likes = likes;
            this.link = link;
            this.user = user;
            this.created_time = created_time;
            this.images = images;
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public String[] getTags() {
            return tags;
        }

        public InstagramCaptionObj getCaption() {
            return caption;
        }

        public InstagramLikesObj getLikes() {
            return likes;
        }

        public String getLink() {
            return link;
        }

        public InstagramUserObj getUser() {
            return user;
        }

        public String getCreated_time() {
            return created_time;
        }

        public InstagramImageObj getImages() {
            return images;
        }

        public String getId() {
            return id;
        }

        public class InstagramCaptionObj {
            private String created_time;
            private String text;
            private String id;
            private InstagramFromObj from;

            public InstagramCaptionObj(String created_time, String text, String id, InstagramFromObj from) {
                this.created_time = created_time;
                this.text = text;
                this.id = id;
                this.from = from;
            }

            public String getCreated_time() {
                return created_time;
            }

            public String getText() {
                return text;
            }

            public String getId() {
                return id;
            }

            public InstagramFromObj getFrom() {
                return from;
            }

            public class InstagramFromObj{
                private String username;
                private String id;

                public InstagramFromObj(String username, String id) {
                    this.username = username;
                    this.id = id;
                }

                public String getUsername() {
                    return username;
                }

                public String getId() {
                    return id;
                }
            }
        }

        public class InstagramLikesObj{
            private int count;

            public InstagramLikesObj(int count) {
                this.count = count;
            }

            public int getCount() {
                return count;
            }
        }

        public class InstagramUserObj{
            private String username;
            private String profil_picture;
            private String id;
            private String full_name;

            public InstagramUserObj(String username, String profil_picture, String id, String full_name) {
                this.username = username;
                this.profil_picture = profil_picture;
                this.id = id;
                this.full_name = full_name;
            }

            public String getUsername() {
                return username;
            }

            public String getProfil_picture() {
                return profil_picture;
            }

            public String getId() {
                return id;
            }

            public String getFull_name() {
                return full_name;
            }
        }

        public class InstagramImageObj{
            private InstagramImageResObj low_resolution;
            private InstagramImageResObj thumbnail;
            private InstagramImageResObj standard_resolution;

            public InstagramImageObj(InstagramImageResObj low_resolution, InstagramImageResObj thumbnail, InstagramImageResObj standard_resolution) {
                this.low_resolution = low_resolution;
                this.thumbnail = thumbnail;
                this.standard_resolution = standard_resolution;
            }

            public InstagramImageResObj getLow_resolution() {
                return low_resolution;
            }

            public InstagramImageResObj getThumbnail() {
                return thumbnail;
            }

            public InstagramImageResObj getStandard_resolution() {
                return standard_resolution;
            }

            public class InstagramImageResObj{
                private String url;
                private int width;
                private int height;

                public InstagramImageResObj(String url, int width, int height) {
                    this.url = url;
                    this.width = width;
                    this.height = height;
                }

                public String getUrl() {
                    return url;
                }

                public int getWidth() {
                    return width;
                }

                public int getHeight() {
                    return height;
                }
            }
        }
    }

}
