package getsterr.getsterr.activities.dashboard;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.pinterest.android.pdk.PDKPin;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.models.Tweet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import getsterr.getsterr.R;
import getsterr.getsterr.models.bing.BingImageResult;
import getsterr.getsterr.models.bing.BingVideoResult;
import getsterr.getsterr.models.bing.Value;
import getsterr.getsterr.models.facebook.FacebookFeedObject;
import getsterr.getsterr.models.instagram.InstagramResponseObj.InstagramData;
import getsterr.getsterr.models.youtube.YoutubeObject.Resource;

/**
 * Created by samsiu on 11/13/16.
 */

public class DashBoardRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = DashBoardRVAdapter.class.getSimpleName();

    public static final int BING_RESULT_CARD = 0;
    public static final int FACEBOOK_MEDIA_CARD = 1;
    public static final int PINTEREST_RESULT_CARD = 2;
    public static final int YOUTUBE_RESULT_CARD = 3;
    public static final int INSTAGRAM_MEDIA_CARD = 4;
    public static final int TWITTER_MEDIA_CARD = 5;
    public static final int BING_IMAGE_RESULT_CARD = 6;
    public static final int BING_VIDEO_RESULT_CARD = 7;


    List<Object> cardItems;
    public final CardClickListener cardClickListener;
    public final YoutubeCardClickListener youtubeCardClickListener;
    public final CardLongClickListener cardLongClickListener;
    public final LastResultShownListener lastResultShownListener;
    Context context;

    public DashBoardRVAdapter(List<Object> cardItems, CardClickListener cardClickListener, YoutubeCardClickListener youtubeCardClickListener, CardLongClickListener cardLongClickListener, LastResultShownListener lastResultShownListener){
        this.cardItems = cardItems;
        this.cardClickListener = cardClickListener;
        this.youtubeCardClickListener = youtubeCardClickListener;
        this.cardLongClickListener = cardLongClickListener;
        this.lastResultShownListener = lastResultShownListener;
    }

    /**
     * Set new items to list
     * @param cardItems
     */
    public void setItems(List<Object> cardItems){
        this.cardItems = cardItems;
    }

    /**
     * Interface to pass object urls to DashBoardActivity
     */
    public interface CardClickListener{
        void onCardClick(String cardUrl);
    }

    /**
     * Interface to pass object urls to DashBoardActivity
     */
    public interface YoutubeCardClickListener{
        void onYoutubeCardClick(String videoId);
    }

    /**
     * Interface to pass object urls to DashBoardActivity from a long click
     */
    public interface CardLongClickListener{
        void onCardLongClick(String url);
    }

    /**
     * Interface to let Dashboard Activity know when last card is shown to load more results
     */
    public interface LastResultShownListener{
        void onLastResultShown(int offset, char searchType);
    }


    /**
     * Look at each position of list passed from DashBoardActivity and select the correct viewtype associated with position
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position){
        if (cardItems.get(position) instanceof Value) return BING_RESULT_CARD;
        else if(cardItems.get(position) instanceof FacebookFeedObject.FbData) return FACEBOOK_MEDIA_CARD;
            // Value is the GSON model for Bing search result
        else if(cardItems.get(position) instanceof PDKPin) return PINTEREST_RESULT_CARD;
        else if(cardItems.get(position) instanceof Resource) return YOUTUBE_RESULT_CARD;
        else if(cardItems.get(position) instanceof InstagramData) return INSTAGRAM_MEDIA_CARD;
        else if(cardItems.get(position) instanceof Tweet) return TWITTER_MEDIA_CARD;
        else if(cardItems.get(position) instanceof BingImageResult.BingImageObj) return BING_IMAGE_RESULT_CARD;
        else if(cardItems.get(position) instanceof BingVideoResult.BingVideoObj) return BING_VIDEO_RESULT_CARD;
        return -1;
    }

    /**
     * Based on the viewtype returned, create the corresponding viewholder
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch(viewType){
            case BING_RESULT_CARD:
                View view1 = inflater.inflate(R.layout.rv_item_bing_search, parent, false);
                viewHolder = new BingResultViewHolder(view1);
                break;
            case FACEBOOK_MEDIA_CARD:
                View view2 = inflater.inflate(R.layout.rv_item_social_facebook, parent, false);
                viewHolder = new FacebookViewHolder(view2);
                break;
            case PINTEREST_RESULT_CARD:
                View view3 = inflater.inflate(R.layout.rv_item_social_pinterest, parent, false);
                viewHolder = new PinterestViewHolder(view3);
                break;
            case YOUTUBE_RESULT_CARD:
                View view4 = inflater.inflate(R.layout.rv_item_youtube_search, parent, false);
                viewHolder = new YoutubeSearchViewHolder(view4);
                break;
            case INSTAGRAM_MEDIA_CARD:
                View view5 = inflater.inflate(R.layout.rv_item_social_twitter, parent, false);
                viewHolder = new InstagramViewHolder(view5);
                break;
            case TWITTER_MEDIA_CARD:
                View view6 = inflater.inflate(R.layout.rv_item_social_twitter, parent, false);
                viewHolder = new TwitterViewHolder(view6);
                break;
            case BING_IMAGE_RESULT_CARD:
                View view7 = inflater.inflate(R.layout.rv_item_youtube_search, parent,false);
                viewHolder = new BingImageViewHolder(view7);
                break;
            case BING_VIDEO_RESULT_CARD:
                View view8 = inflater.inflate(R.layout.rv_item_youtube_search, parent,false);
                viewHolder = new BingImageViewHolder(view8);
                break;
            default:
                View view = inflater.inflate(R.layout.rv_item_bing_search, parent, false);
                viewHolder = new BingResultViewHolder(view);
                break;
        }
        return viewHolder;
    }

    /**
     * Bind the data from the social media object that is best associated with the viewholder
     * @param viewHolder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position){
        switch(viewHolder.getItemViewType()){
            case BING_RESULT_CARD:
                BingResultViewHolder bingResultViewHolder = (BingResultViewHolder)viewHolder;
                configureBingResultViewHolder(bingResultViewHolder, position);
                break;
            case FACEBOOK_MEDIA_CARD:
                FacebookViewHolder facebookViewHolder = (FacebookViewHolder)viewHolder;
                configureFacebookViewHolder(facebookViewHolder, position);
                break;
            case PINTEREST_RESULT_CARD:
                PinterestViewHolder pinterestViewHolder = (PinterestViewHolder)viewHolder;
                configurePinterestViewHolder(pinterestViewHolder, position);
                break;
            case YOUTUBE_RESULT_CARD:
                YoutubeSearchViewHolder youtubeSearchViewHolder = (YoutubeSearchViewHolder) viewHolder;
                configureYoutubeViewHolder(youtubeSearchViewHolder,position);
                break;
            case INSTAGRAM_MEDIA_CARD:
                InstagramViewHolder instagramViewHolder = (InstagramViewHolder) viewHolder;
                configureInstagramViewHolder(instagramViewHolder,position);
                break;
            case TWITTER_MEDIA_CARD:
                TwitterViewHolder twitterViewHolder = (TwitterViewHolder) viewHolder;
                configureTwitterViewHolder(twitterViewHolder, position);
                break;
            case BING_IMAGE_RESULT_CARD:
                BingImageViewHolder bingImageViewHolder = (BingImageViewHolder) viewHolder;
                configureBingImageViewHolder(bingImageViewHolder, position);
                break;
            case BING_VIDEO_RESULT_CARD:
                BingImageViewHolder bingVideoViewHolder = (BingImageViewHolder) viewHolder;
                configureBingVideoViewHolder(bingVideoViewHolder, position);
                break;
            default:
                BingResultViewHolder vHolder = (BingResultViewHolder)viewHolder;
                configureBingResultViewHolder(vHolder, position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return cardItems == null ? 0 : cardItems.size();
    }

    /**
     * Display the data from the returned Bing search object
     * @param bingResultViewHolder
     * @param position
     */
    private void configureBingResultViewHolder(BingResultViewHolder bingResultViewHolder, int position){
        //BingResultCard bingResultCard = (BingResultCard) cardItems.get(position);
        Value value = (Value)cardItems.get(position);
        bingResultViewHolder.bingResultTitleTextView.setText(value.getName());
//        bingResultViewHolder.bingResultTimeTextView.setText(value.getDateLastCrawled());
        bingResultViewHolder.bingResultSnippetTextView.setText(value.getSnippet());
        Log.i(TAG, "configureBingResultViewHolder: url - " + value.getUrl());
        Log.i(TAG, "configureBingResultViewHolder: display url - " + value.getDisplayUrl());
        bingResultViewHolder.bindBingResultCardClick(cardClickListener, value.getUrl());
        bingResultViewHolder.bindLongClick(cardLongClickListener, value.getUrl());
        if (position>=cardItems.size()-1) lastResultShownListener.onLastResultShown(position+2,'w');
    }

    /**
     * Display the data from the returned Facebook news feed object
     * @param facebookViewHolder
     * @param position
     */
    private void configureFacebookViewHolder(FacebookViewHolder facebookViewHolder, int position){
        //SocialMediaCard socialMediaCard = (SocialMediaCard) cardItems.get(position);
        FacebookFeedObject.FbData fbFeed = (FacebookFeedObject.FbData)cardItems.get(position);
        Log.i(TAG, "configureFacebookViewHolder: link " + fbFeed.getPermalink_url());
        facebookViewHolder.bodyTv.setText(fbFeed.getMessage());

        facebookViewHolder.postedByTv.setText(fbFeed.getFrom().getName() + " posted: ");
        Log.i(TAG, "configureFacebookViewHolder: picture url - " + fbFeed.getPicture());
        if (fbFeed.getFull_picture()!=null) Picasso.with(context).load(fbFeed.getFull_picture()).into(facebookViewHolder.pictureIv);
        else facebookViewHolder.pictureIv.setVisibility(View.GONE);
        // Once resource for ImageButton is changed, rounded corner disappears
        facebookViewHolder.iconIb.setImageResource(R.drawable.ic_facebook);
        facebookViewHolder.circleFrame.getBackground().setColorFilter(facebookViewHolder.color, PorterDuff.Mode.SRC_ATOP);
        facebookViewHolder.iconIb.getBackground().setColorFilter(facebookViewHolder.color, PorterDuff.Mode.SRC_ATOP);
        facebookViewHolder.iconIb.setBackgroundResource(R.drawable.roundcorner);

        facebookViewHolder.timeTv.setText(getTimeDiffFacebook(fbFeed.getCreated_time()));
        facebookViewHolder.bindFacebookCardClick(cardClickListener, fbFeed.getPermalink_url());
    }

    /**
     * Display the data from the returned Pinterest Pin object
     * @param pinterestViewHolder
     * @param position
     */
    private void configurePinterestViewHolder(PinterestViewHolder pinterestViewHolder, int position){
        PDKPin pin = (PDKPin)cardItems.get(position);
//        if (pin.getBoard()!=null)pinterestViewHolder.postedByTv.setText(pin.getBoard().getName()+" pinned:");
//        else pinterestViewHolder.postedByTv.setVisibility(View.GONE);
        pinterestViewHolder.postedByTv.setText("New Pin: ");
//        if (pin.getUser()!=null)pinterestViewHolder.postedByTv.setText(pin.getUser().getFirstName()+" "+pin.getUser().getLastName()+" pinned:");
        pinterestViewHolder.pinterestInfoTextView.setText(pin.getNote());
        Picasso.with(context).load(pin.getImageUrl()).into(pinterestViewHolder.pinterestImageView);
        // Once resource for ImageButton is changed, rounded corner disappears
        pinterestViewHolder.pinterestImageButton.setImageResource(R.drawable.pinterest_white);
//        pinterestViewHolder.circleFrame.getBackground().setColorFilter(pinterestViewHolder.color, PorterDuff.Mode.SRC_ATOP);
        pinterestViewHolder.pinterestImageButton.getBackground().setColorFilter(pinterestViewHolder.color, PorterDuff.Mode.SRC_ATOP);
        pinterestViewHolder.pinterestImageButton.setBackgroundResource(R.drawable.roundcorner);

        pinterestViewHolder.pinterestTimeTextView.setText(getTimeDiffPinterest(pin.getCreatedAt().toString()));
        Log.i(TAG, "configurePinterestViewHolder: url link "+pin.getLink());
        pinterestViewHolder.bindPinterestCardClick(cardClickListener, pin.getLink());
    }

    /**
     * Display the data from the returned InstagramData object
     * @param instagramViewHolder
     * @param position
     */
    private void configureInstagramViewHolder(InstagramViewHolder instagramViewHolder, int position){
        InstagramData instagramData = (InstagramData) cardItems.get(position);

        if (instagramData.getCaption()!=null)instagramViewHolder.instagramInfoTextView.setText(instagramData.getCaption().getText());
        instagramViewHolder.postedByTv.setText(instagramData.getUser().getUsername()+" posted:");
        // Once resource for ImageButton is changed, rounded corner disappears
        instagramViewHolder.instagramImageButton.setImageResource(R.drawable.ic_instagram);
        instagramViewHolder.circleFrame.getBackground().setColorFilter(instagramViewHolder.color, PorterDuff.Mode.SRC_ATOP);
//        instagramViewHolder.instagramImageButton.getBackground().setColorFilter(instagramViewHolder.color, PorterDuff.Mode.SRC_ATOP);
//        instagramViewHolder.instagramImageButton.getBackground().setTint(instagramViewHolder.color);
        Picasso.with(context).load(instagramData.getImages().getStandard_resolution().getUrl()).into(instagramViewHolder.instagramImageView);
        instagramViewHolder.instagramImageButton.setBackgroundResource(R.drawable.roundcorner);
        Date time=new java.util.Date((long)Integer.parseInt(instagramData.getCreated_time())*1000);
        instagramViewHolder.instagramTimeTextView.setText(getTimeDiffInstagram(time.toString()));
        Log.i(TAG, "configureInstagramViewHolder: url - " + instagramData.getLink());
        instagramViewHolder.bindInstagramCardClick(cardClickListener, instagramData.getLink());
    }

    private void configureTwitterViewHolder(TwitterViewHolder twitterViewHolder, int position){
        Tweet tweet = (Tweet) cardItems.get(position);
        twitterViewHolder.postedByTv.setText(tweet.user.name + " tweeted: ");
        twitterViewHolder.twitterInfoTextView.setText(tweet.text);
        twitterViewHolder.twitterTimeTextView.setText(getTimeDiffTwitter(tweet.createdAt));
        twitterViewHolder.twitterImageButton.setImageResource(R.drawable.ic_twitter);
        twitterViewHolder.circleFrame.getBackground().setColorFilter(twitterViewHolder.color, PorterDuff.Mode.SRC_ATOP);
        twitterViewHolder.twitterImageButton.getBackground().setColorFilter(twitterViewHolder.color, PorterDuff.Mode.SRC_ATOP);
        Picasso.with(context).load(tweet.user.profileImageUrl).into(twitterViewHolder.twitterImageView);
        if (tweet.entities.urls.size()>0) {
            Log.i(TAG, "configureTwitterViewHolder: url link - " + tweet.entities.urls.get(0).expandedUrl);
            twitterViewHolder.bindTwitterCardClick(cardClickListener,tweet.entities.urls.get(0).expandedUrl);
        }else twitterViewHolder.bindTwitterCardClick(cardClickListener,"https://www.twitter.com/"); //temp source, no link

    }

    private void configureYoutubeViewHolder(YoutubeSearchViewHolder youtubeSearchViewHolder, int position){
        Resource resource = (Resource)cardItems.get(position);
        youtubeSearchViewHolder.bind(youtubeCardClickListener, resource.getId().getVideoId());
        youtubeSearchViewHolder.titleTv.setText(resource.getSnippet().getTitle());
        youtubeSearchViewHolder.descriptionTv.setText(resource.getSnippet().getDescription());
        Picasso.with(context).load(resource.getSnippet().getThumbnails().getHigh().getUrl())
                .into(youtubeSearchViewHolder.thumbnailIv);

    }

    private void configureBingImageViewHolder(BingImageViewHolder viewHolder, int position){
        BingImageResult.BingImageObj bingImageObj = (BingImageResult.BingImageObj) cardItems.get(position);
        viewHolder.titleTv.setText(bingImageObj.getName());
        viewHolder.descriptionTv.setText(bingImageObj.getHostPageDisplayUrl());
        Picasso.with(context).load(bingImageObj.getThumbnailUrl()).into(viewHolder.thumbnailIv);
        viewHolder.bind(cardClickListener, bingImageObj.getContentUrl());
        if (position>=cardItems.size()-1) lastResultShownListener.onLastResultShown(position+2,'i');
    }

    private void configureBingVideoViewHolder(BingImageViewHolder viewHolder, int position){
        BingVideoResult.BingVideoObj bingVideoObj = (BingVideoResult.BingVideoObj) cardItems.get(position);
        viewHolder.titleTv.setText(bingVideoObj.getName());
        viewHolder.descriptionTv.setText(bingVideoObj.getDescription());
        Picasso.with(context).load(bingVideoObj.getThumbnailUrl()).into(viewHolder.thumbnailIv);
        viewHolder.bind(cardClickListener, bingVideoObj.getContentUrl());
        if (position>=cardItems.size()-1) lastResultShownListener.onLastResultShown(position+2,'v');
    }

    /**
     * ViewHolder for Facebook Ojbects from SocialMediaList
     */
    public static class FacebookViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView bodyTv;
        TextView postedByTv;
        ImageButton iconIb;
        ImageView pictureIv;
        TextView timeTv;
        FrameLayout circleFrame;
        int color;

        FacebookViewHolder(View itemView){
            super(itemView);
            circleFrame = (FrameLayout)itemView.findViewById(R.id.dash_social_media_circle);
            cardView = (CardView)itemView.findViewById(R.id.dashboard_social_media_cardView);
            bodyTv = (TextView)itemView.findViewById(R.id.dash_social_media_info_textView);
            iconIb = (ImageButton)itemView.findViewById(R.id.dash_social_media_imageButton);
            postedByTv = (TextView)itemView.findViewById(R.id.dash_social_media_postedby);
            pictureIv = (ImageView)itemView.findViewById(R.id.dash_social_media_imageView);
            timeTv = (TextView)itemView.findViewById(R.id.dash_social_media_time_textView);
            color = itemView.getResources().getColor(R.color.facebook);
        }

        public void bindFacebookCardClick(final CardClickListener cardClickListener, final String cardUrl){
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cardClickListener.onCardClick(cardUrl);
                }
            });
        }
    }

    /**
     * ViewHolder for Pinterest Objects from Social Media List
     */
    public static class PinterestViewHolder extends RecyclerView.ViewHolder{
        CardView pinterestCardView;
        TextView pinterestInfoTextView;
        ImageButton pinterestImageButton;
        TextView postedByTv;
        ImageView pinterestImageView;
        FrameLayout circleFrame;
        TextView pinterestTimeTextView;
        int color;

        PinterestViewHolder(View itemView){
            super(itemView);
            pinterestCardView = (CardView)itemView.findViewById(R.id.dashboard_social_media_cardView);
            pinterestInfoTextView = (TextView)itemView.findViewById(R.id.dash_social_media_info_textView);
            pinterestImageButton = (ImageButton)itemView.findViewById(R.id.dash_social_media_imageButton);
            circleFrame = (FrameLayout)itemView.findViewById(R.id.dash_social_media_circle);
            postedByTv = (TextView)itemView.findViewById(R.id.dash_social_media_postedby);
            pinterestImageView = (ImageView)itemView.findViewById(R.id.dash_social_media_imageView);
            pinterestTimeTextView = (TextView)itemView.findViewById(R.id.dash_social_media_time_textView);
            color = itemView.getResources().getColor(R.color.pinterest);
        }

        public void bindPinterestCardClick(final CardClickListener cardClickListener, final String cardUrl){
            pinterestCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cardClickListener.onCardClick(cardUrl);
                }
            });
        }
    }

    /**
     * ViewHolder for Instagram Objects from Social Media List
     */
    public static class InstagramViewHolder extends RecyclerView.ViewHolder{
        CardView instagramCardView;
        TextView instagramInfoTextView;
        ImageButton instagramImageButton;
        ImageView instagramImageView;
        FrameLayout circleFrame;
        TextView postedByTv;
        TextView instagramTimeTextView;
        int color;

        InstagramViewHolder(View itemView){
            super(itemView);
            instagramCardView = (CardView)itemView.findViewById(R.id.dashboard_social_media_cardView);
            instagramInfoTextView = (TextView)itemView.findViewById(R.id.dash_social_media_info_textView);
            postedByTv = (TextView)itemView.findViewById(R.id.dash_social_media_postedby);
            instagramImageButton = (ImageButton)itemView.findViewById(R.id.dash_social_media_imageButton);
            circleFrame = (FrameLayout)itemView.findViewById(R.id.dash_social_media_circle);
            instagramTimeTextView = (TextView)itemView.findViewById(R.id.dash_social_media_time_textView);
            instagramImageView = (ImageView)itemView.findViewById(R.id.dash_social_media_imageView);
            color = itemView.getResources().getColor(R.color.instagram);
        }

        public void bindInstagramCardClick(final CardClickListener cardClickListener, final String cardUrl){
            instagramCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cardClickListener.onCardClick(cardUrl);
                }
            });
        }
    }

    /**
     * ViewHolder for Twiter Objects from Social Media List
     */
    public static class TwitterViewHolder extends RecyclerView.ViewHolder{
        CardView twitterCardView;
        TextView twitterInfoTextView;
        ImageButton twitterImageButton;
        FrameLayout circleFrame;
        ImageView twitterImageView;
        TextView twitterTimeTextView;
        TextView postedByTv;
        int color;

        TwitterViewHolder(View itemView){
            super(itemView);
            twitterCardView = (CardView)itemView.findViewById(R.id.dashboard_social_media_cardView);
            twitterInfoTextView = (TextView)itemView.findViewById(R.id.dash_social_media_info_textView);
            circleFrame = (FrameLayout)itemView.findViewById(R.id.dash_social_media_circle);
            twitterImageButton = (ImageButton)itemView.findViewById(R.id.dash_social_media_imageButton);
            twitterTimeTextView = (TextView)itemView.findViewById(R.id.dash_social_media_time_textView);
            postedByTv = (TextView)itemView.findViewById(R.id.dash_social_media_postedby);
            twitterImageView = (ImageView)itemView.findViewById(R.id.dash_social_media_imageView);
            color = itemView.getResources().getColor(R.color.twitter);
        }

        public void bindTwitterCardClick(final CardClickListener cardClickListener, final String cardUrl){
            twitterCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cardClickListener.onCardClick(cardUrl);
                }
            });
        }
    }

    /**
     * ViewHolder for Bing Search results
     */
    public static class BingResultViewHolder extends RecyclerView.ViewHolder{
        CardView bingResultCardView;
        TextView bingResultTitleTextView;
        TextView bingResultTimeTextView;
        TextView bingResultSnippetTextView;

        BingResultViewHolder(View itemView){
            super(itemView);
            bingResultCardView = (CardView)itemView.findViewById(R.id.dashboard_bing_result_cardView);
            bingResultTitleTextView = (TextView)itemView.findViewById(R.id.dash_bing_result_title_textView);
            bingResultTimeTextView = (TextView)itemView.findViewById(R.id.dash_bing_result_time_textView);
            bingResultSnippetTextView = (TextView)itemView.findViewById(R.id.dash_bing_result_snippet_textView);
        }

        public void bindBingResultCardClick(final CardClickListener cardClickListener, final String cardUrl){
            bingResultCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cardClickListener.onCardClick(cardUrl);
                }
            });
        }

        public void bindLongClick(final CardLongClickListener cardLongClickListener, final String shareUrl){
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    cardLongClickListener.onCardLongClick(shareUrl);
                    return true;
                }
            });
        }
    }

    /**
     * ViewHolder for Youtube Search results
     */
    public static class YoutubeSearchViewHolder extends RecyclerView.ViewHolder{
        public TextView titleTv;
        public TextView descriptionTv;
        public ImageView thumbnailIv;
        public YoutubeSearchViewHolder(View itemView) {
            super(itemView);
            titleTv = (TextView)itemView.findViewById(R.id.rv_item_title);
            descriptionTv = (TextView) itemView.findViewById(R.id.rv_item_description);
            thumbnailIv = (ImageView) itemView.findViewById(R.id.rv_item_iv);
        }
        public void bind(final YoutubeCardClickListener listener, final String videoId){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onYoutubeCardClick(videoId);
                }
            });
        }
    }
    /**
     * ViewHolder for Youtube Search results
     */
    public static class BingImageViewHolder extends RecyclerView.ViewHolder{
        public TextView titleTv;
        public TextView descriptionTv;
        public ImageView thumbnailIv;
        public BingImageViewHolder(View itemView) {
            super(itemView);
            titleTv = (TextView)itemView.findViewById(R.id.rv_item_title);
            descriptionTv = (TextView) itemView.findViewById(R.id.rv_item_description);
            thumbnailIv = (ImageView) itemView.findViewById(R.id.rv_item_iv);
        }
        public void bind(final CardClickListener listener, final String url){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onCardClick(url);
                }
            });
        }
    }

    private String getTimeDiffFacebook(String clockString){
        String[] clockArray = clockString.split("T");
        String timeString = clockArray[1].substring(0,8);
        String dateString = clockArray[0];
        Log.i(TAG, "getTimeDiffFacebook: clockString - "+clockString);
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd/HH:mm:ss");
        Date postedDate = null;
        try{
            postedDate = timeFormat.parse(dateString+"/"+timeString);
        }catch (ParseException e){
            e.printStackTrace();
        }
        Log.i(TAG, "getTimeDiffFacebook: posted date - " + postedDate);
        long difference = System.currentTimeMillis() - postedDate.getTime();
        if (TimeUnit.MILLISECONDS.toDays(difference)==0.0&&TimeUnit.MILLISECONDS.toHours(difference)==0
                &&TimeUnit.MILLISECONDS.toMinutes(difference)==0)return TimeUnit.MILLISECONDS.toSeconds(difference) + " sec\nago";
        if (TimeUnit.MILLISECONDS.toDays(difference)==0.0&&TimeUnit.MILLISECONDS.toHours(difference)==0)
            return TimeUnit.MILLISECONDS.toMinutes(difference) + " min\nago";
        if (TimeUnit.MILLISECONDS.toDays(difference)==0.0)return TimeUnit.MILLISECONDS.toHours(difference) + " hours\nago";
        else return TimeUnit.MILLISECONDS.toDays(difference) + " days\nago";
    }
    private String getTimeDiffPinterest(String clockString){
        String parsedString = clockString.substring(4,7)+clockString.substring(8,10)+clockString.substring(24,28)
                + clockString.substring(11,13)+clockString.substring(14,16)+clockString.substring(17,19);
        Log.i(TAG, "getTimeDiffPinterest: parsed string" + parsedString);
        SimpleDateFormat timeFormat = new SimpleDateFormat("MMMddyyyyHHmmss");
        Date postedDate = null;
        try{
            postedDate = timeFormat.parse(parsedString);
        }catch (ParseException e){
            e.printStackTrace();
        }
        long difference = System.currentTimeMillis() - postedDate.getTime();
        if (TimeUnit.MILLISECONDS.toDays(difference)==0.0&&TimeUnit.MILLISECONDS.toHours(difference)==0
                &&TimeUnit.MILLISECONDS.toMinutes(difference)==0)return TimeUnit.MILLISECONDS.toSeconds(difference) + " sec\nago";
        if (TimeUnit.MILLISECONDS.toDays(difference)==0.0&&TimeUnit.MILLISECONDS.toHours(difference)==0)
            return TimeUnit.MILLISECONDS.toMinutes(difference) + " min\nago";
        if (TimeUnit.MILLISECONDS.toDays(difference)==0.0)return TimeUnit.MILLISECONDS.toHours(difference) + " hours\nago";
        else return TimeUnit.MILLISECONDS.toDays(difference) + " days\nago";
    }
    private String getTimeDiffTwitter(String clockString){
        Log.i(TAG, "getTimeDiffTwitter: response string - "+clockString);
//        String changedTimeZone = String.valueOf(Integer.parseInt(clockString.substring(11,13))-8);
//        String parsedString = clockString.substring(4,7)+clockString.substring(8,10)+clockString.substring(26,30)
//                + changedTimeZone+clockString.substring(14,16)+clockString.substring(17,19);
        String parsedString = clockString.substring(4,7)+clockString.substring(8,10)+clockString.substring(26,30)
                + clockString.substring(11,13)+clockString.substring(14,16)+clockString.substring(17,19);
        Log.i(TAG, "getTimeDiffPinterest: parsed string - " + parsedString);
        SimpleDateFormat timeFormat = new SimpleDateFormat("MMMddyyyyHHmmss");
        timeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        timeFormat.setTimeZone(TimeZone.getTimeZone("PST"));
        Date postedDate = null;
        try{
            postedDate = timeFormat.parse(parsedString);
        }catch (ParseException e){
            e.printStackTrace();
        }
        Log.i(TAG, "getTimeDiffTwitter: postedDate - " + postedDate.toString());
        long difference = System.currentTimeMillis() - postedDate.getTime();
        if (TimeUnit.MILLISECONDS.toDays(difference)==0.0&&TimeUnit.MILLISECONDS.toHours(difference)==0
                &&TimeUnit.MILLISECONDS.toMinutes(difference)==0)return TimeUnit.MILLISECONDS.toSeconds(difference) + " sec\nago";
        if (TimeUnit.MILLISECONDS.toDays(difference)==0.0&&TimeUnit.MILLISECONDS.toHours(difference)==0)
            return TimeUnit.MILLISECONDS.toMinutes(difference) + " min\nago";
        if (TimeUnit.MILLISECONDS.toDays(difference)==0.0)return TimeUnit.MILLISECONDS.toHours(difference) + " hours\nago";
        else return TimeUnit.MILLISECONDS.toDays(difference) + " days\nago";
    }
    private String getTimeDiffInstagram(String clockString){
        String parsedString = clockString.substring(4,7)+clockString.substring(8,10)+clockString.substring(24,28)
                + clockString.substring(11,13)+clockString.substring(14,16)+clockString.substring(17,19);
        Log.i(TAG, "getTimeDiffPinterest: parsed string" + parsedString);
        SimpleDateFormat timeFormat = new SimpleDateFormat("MMMddyyyyHHmmss");
        Date postedDate = null;
        try{
            postedDate = timeFormat.parse(parsedString);
        }catch (ParseException e){
            e.printStackTrace();
        }
        Log.i(TAG, "getTimeDiffInstagram: posted date - " + postedDate.toString());
        Date currentDate = new Date(System.currentTimeMillis());
        Log.i(TAG, "getTimeDiffInstagram: currite date - " + currentDate.toString());
        long difference = System.currentTimeMillis() - postedDate.getTime();
        if (TimeUnit.MILLISECONDS.toDays(difference)==0.0&&TimeUnit.MILLISECONDS.toHours(difference)==0
                &&TimeUnit.MILLISECONDS.toMinutes(difference)==0)return TimeUnit.MILLISECONDS.toSeconds(difference) + " sec\nago";
        if (TimeUnit.MILLISECONDS.toDays(difference)==0.0&&TimeUnit.MILLISECONDS.toHours(difference)==0)
            return TimeUnit.MILLISECONDS.toMinutes(difference) + " min\nago";
        if (TimeUnit.MILLISECONDS.toDays(difference)==0.0)return TimeUnit.MILLISECONDS.toHours(difference) + " hours\nago";
        else return TimeUnit.MILLISECONDS.toDays(difference) + " days\nago";
    }
}
