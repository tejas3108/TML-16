package siesgst.edu.in.tml16.helpers;

/**
 * Created by vishal on 3/2/16.
 */
public class FeedNews {
    private String postMessage;
    private String postImage;
    private String postLink;
    private String likes;
    private String comments;

    public void setPostMessage(String postMessage) {
        this.postMessage = postMessage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public void setPostLink(String postLink) {
        this.postLink = postLink;
    }

    public void setNoOfLikes(String likes) { this.likes = likes;}

    public void setNoOfComments(String comments) { this.comments = comments;}

    public String getPostMessage() {
        return postMessage;
    }

    public String getPostImage() {
        return postImage;
    }

    public String getPostLink() {
        return postLink;
    }

    public String getLikes() { return likes;}

    public String getComments() { return comments;}
}
