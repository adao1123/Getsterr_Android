package getsterr.getsterr.models.facebook;

import java.util.List;

public class FacebookFeedObject {

    List<FbData> data;
    Paging paging;

    public List<FbData> getData() {
        return data;
    }

    public void setData(List<FbData> data) {
        this.data = data;
    }

    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }

    public class FbData{
        String message;
        String created_time;
        String id;
        String link;
        String description;
        String picture;
        String full_picture;
        String story;
        String permalink_url;
        FromUser from;

        public String getFull_picture() {
            return full_picture;
        }

        public FromUser getFrom() {
            return from;
        }

        public String getDescription() {
            return description;
        }

        public String getPicture() {
            return picture;
        }

        public String getStory() {
            return story;
        }

        public String getPermalink_url() {
            return permalink_url;
        }

        public String getLink() {
            return link;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getCreated_time() {
            return created_time;
        }

        public void setCreated_time(String created_time) {
            this.created_time = created_time;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
        public class FromUser{
            String name;
            String id;

            public String getName() {
                return name;
            }

            public String getId() {
                return id;
            }
        }
    }

    public class Paging{
        String previous;
        String next;

        public String getPrevious() {
            return previous;
        }

        public void setPrevious(String previous) {
            this.previous = previous;
        }

        public String getNext() {
            return next;
        }

        public void setNext(String next) {
            this.next = next;
        }
    }

}
