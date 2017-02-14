package getsterr.imf.models.instagram;

/**
 * Created by adao1 on 12/8/2016.
 */

public class InstagramUserResponse {
    private InstagramUserInfo data;

    public InstagramUserResponse(InstagramUserInfo data) {
        this.data = data;
    }

    public InstagramUserInfo getData() {
        return data;
    }

    public class InstagramUserInfo{
        private String id;
        private String username;
        private String full_name;
        private String profile_picture;
        private String bio;
        private String website;

        public InstagramUserInfo(String id, String username, String full_name, String profile_picture, String bio, String website) {
            this.id = id;
            this.username = username;
            this.full_name = full_name;
            this.profile_picture = profile_picture;
            this.bio = bio;
            this.website = website;
        }

        public String getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public String getFull_name() {
            return full_name;
        }

        public String getProfile_picture() {
            return profile_picture;
        }

        public String getBio() {
            return bio;
        }

        public String getWebsite() {
            return website;
        }
    }
}
