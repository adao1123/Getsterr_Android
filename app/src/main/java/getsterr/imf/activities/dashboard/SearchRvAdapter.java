package getsterr.imf.activities.dashboard;

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

import com.bumptech.glide.Glide;
import com.pinterest.android.pdk.PDKPin;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.models.Tweet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import getsterr.imf.R;
import getsterr.imf.models.bing.BingImageResult;
import getsterr.imf.models.bing.BingVideoResult;
import getsterr.imf.models.bing.Value;
import getsterr.imf.models.facebook.FacebookFeedObject;
import getsterr.imf.models.giphy.GiphyObject;
import getsterr.imf.models.instagram.InstagramResponseObj.InstagramData;
import getsterr.imf.models.youtube.YoutubeObject.Resource;

/**
 * Created by adao on 3/15/17.
 */

public class SearchRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = DashBoardRVAdapter.class.getSimpleName();

    public static final int BING_RESULT_CARD = 0;
    public static final int YOUTUBE_RESULT_CARD = 3;
    public static final int BING_IMAGE_RESULT_CARD = 6;
    public static final int BING_VIDEO_RESULT_CARD = 7;
    public static final int GIPHY_RESULT_CARD = 8;


    List<Object> cardItems;
    public final CardClickListener cardClickListener;
    public final YoutubeCardClickListener youtubeCardClickListener;
    public final CardLongClickListener cardLongClickListener;
    public final LastResultShownListener lastResultShownListener;
    Context context;

    public SearchRvAdapter(List<Object> cardItems, CardClickListener cardClickListener, YoutubeCardClickListener youtubeCardClickListener, CardLongClickListener cardLongClickListener, LastResultShownListener lastResultShownListener){
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
        else if(cardItems.get(position) instanceof Resource) return YOUTUBE_RESULT_CARD;
        else if(cardItems.get(position) instanceof BingImageResult.BingImageObj) return BING_IMAGE_RESULT_CARD;
        else if(cardItems.get(position) instanceof BingVideoResult.BingVideoObj) return BING_VIDEO_RESULT_CARD;
        else if(cardItems.get(position) instanceof GiphyObject.Giphy) return GIPHY_RESULT_CARD;
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
            case YOUTUBE_RESULT_CARD:
                View view4 = inflater.inflate(R.layout.rv_item_youtube_search, parent, false);
                viewHolder = new YoutubeSearchViewHolder(view4);
                break;
            case BING_IMAGE_RESULT_CARD:
                View view7 = inflater.inflate(R.layout.rv_item_youtube_search, parent,false);
                viewHolder = new BingImageViewHolder(view7);
                break;
            case BING_VIDEO_RESULT_CARD:
                View view8 = inflater.inflate(R.layout.rv_item_youtube_search, parent,false);
                viewHolder = new BingImageViewHolder(view8);
                break;
            case GIPHY_RESULT_CARD:
                View view9 = inflater.inflate(R.layout.rv_item_giphy, parent,false);
                viewHolder = new GiphyViewHolder(view9);
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
            case YOUTUBE_RESULT_CARD:
                YoutubeSearchViewHolder youtubeSearchViewHolder = (YoutubeSearchViewHolder) viewHolder;
                configureYoutubeViewHolder(youtubeSearchViewHolder,position);
                break;
            case BING_IMAGE_RESULT_CARD:
                BingImageViewHolder bingImageViewHolder = (BingImageViewHolder) viewHolder;
                configureBingImageViewHolder(bingImageViewHolder, position);
                break;
            case BING_VIDEO_RESULT_CARD:
                BingImageViewHolder bingVideoViewHolder = (BingImageViewHolder) viewHolder;
                configureBingVideoViewHolder(bingVideoViewHolder, position);
                break;
            case GIPHY_RESULT_CARD:
                GiphyViewHolder giphyViewHolder = (GiphyViewHolder) viewHolder;
                configureGiphyViewHolder(giphyViewHolder, position);
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

    private void configureGiphyViewHolder(GiphyViewHolder viewHolder, int position){
        GiphyObject.Giphy giphy = (GiphyObject.Giphy) cardItems.get(position);
        if (giphy.getImages().getOriginal_still()!=null) {
            Glide.with(context).load(giphy.getImages().getFixed_height_small_still().getUrl()).asGif().thumbnail(0.5f).into(viewHolder.imageView);
            Log.i(TAG, "configureGiphyViewHolder: gif url: " + giphy.getImages().getOriginal_still().getUrl());
        }
        viewHolder.bind(cardClickListener, giphy.getUrl());
//        if (position>=cardItems.size()-2) lastResultShownListener.onLastResultShown(position+2,'m');
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

    public static class GiphyViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public GiphyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.giphy_iv);
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
}
