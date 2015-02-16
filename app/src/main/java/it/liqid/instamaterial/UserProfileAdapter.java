package it.liqid.instamaterial;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by niccolo on 11/02/15.
 */
public class UserProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_PROFILE_HEADER = 0;
    public static final int TYPE_PROFILE_OPTIONS = 1;
    public static final int TYPE_PHOTO = 2;

    private static final int USER_OPTIONS_ANIMATION_DELAY = 300;
    private static final int MAX_PHOTO_ANIMATION_DELAY = 600;

    private static final int MIN_ITEMS_COUNT = 2;
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();

    private final Context context;
    private final int cellSize;
    private final int avatarSize;

    private final String profilePhoto;
    private final List<String> photos;

    private boolean locekdAnimations = false;
    private long profileHeaderAnimationStartTime = 0;
    private int lastAnimatedItem = 0;

    public UserProfileAdapter(Context context) {
        this.context = context;
        this.cellSize = Utils.getScreenWidth(context) / 3;
        this.avatarSize = context.getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size);
        this.profilePhoto = context.getString(R.string.user_profile_photo);
        this.photos = Arrays.asList(context.getResources().getStringArray(R.array.user_photos));
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_PROFILE_HEADER;
        } else if (position == 1) {
            return TYPE_PROFILE_OPTIONS;
        } else {
            return TYPE_PHOTO;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (TYPE_PROFILE_HEADER == viewType) {
            final View view = LayoutInflater.from(context).inflate(R.layout.view_user_profile_header, parent, false);
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            layoutParams.setFullSpan(true);
            view.setLayoutParams(layoutParams);
            return new ProfileHeaderViewHolder(view);
        } else if (TYPE_PROFILE_OPTIONS == viewType) {
            final View view = LayoutInflater.from(context).inflate(R.layout.view_user_profile_options, parent, false);
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            layoutParams.setFullSpan(true);
            view.setLayoutParams(layoutParams);
            return new ProfileOptionsViewHolder(view);
        } else if (TYPE_PHOTO == viewType) {
            final View view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false);
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            layoutParams.setFullSpan(false);
            layoutParams.width = cellSize;
            layoutParams.height = cellSize;
            view.setLayoutParams(layoutParams);
            return new PhotoViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (TYPE_PROFILE_HEADER == viewType) {
            bindProfileHeader((ProfileHeaderViewHolder) holder);
        } else if (TYPE_PROFILE_OPTIONS == viewType) {
            bindProfileOptions((ProfileOptionsViewHolder) holder);
        } else if (TYPE_PHOTO == viewType) {
            bindPhoto((PhotoViewHolder) holder, position);
        }

    }

    private void bindProfileHeader(final ProfileHeaderViewHolder holder) {
        Picasso.with(context)
                .load(profilePhoto)
                .placeholder(R.drawable.img_circle_placeholder)
                .resize(avatarSize, avatarSize)
                .centerCrop()
                .transform(new CircleTransformation())
                .into(holder.ivUserProfilePhoto);

        holder.vUserProfileRoot.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                holder.vUserProfileRoot.getViewTreeObserver().removeOnPreDrawListener(this);
                animateUserProfileHeader(holder);
                return false;
            }
        });
    }

    private void bindProfileOptions(final ProfileOptionsViewHolder holder) {
        holder.vButtons.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                holder.vButtons.getViewTreeObserver().removeOnPreDrawListener(this);
                holder.vUnderline.getLayoutParams().width = holder.btnGrid.getWidth();
                holder.vUnderline.requestLayout();
                animateUserProfileOptions(holder);
                return false;
            }
        });
    }

    private void bindPhoto(final PhotoViewHolder holder, int position) {
        Picasso.with(context)
                .load(photos.get(position - MIN_ITEMS_COUNT))
                .resize(cellSize, cellSize)
                .centerCrop()
                .into(holder.ivPhoto, new Callback() {
                    @Override
                    public void onSuccess() {
                        animatePhoto(holder);
                    }

                    @Override
                    public void onError() {

                    }
                });

        if (lastAnimatedItem < position)
            lastAnimatedItem = position;
    }

    /**
     * ANIMATIONS
     */
    private void animateUserProfileHeader(ProfileHeaderViewHolder holder) {

        if (!locekdAnimations) {
            profileHeaderAnimationStartTime = System.currentTimeMillis();

            holder.vUserProfileRoot.setTranslationY(-holder.vUserProfileRoot.getHeight());
            holder.ivUserProfilePhoto.setTranslationY(-holder.ivUserProfilePhoto.getHeight());
            holder.vUserDetails.setTranslationY(-holder.vUserDetails.getHeight());
            holder.vUserStats.setAlpha(0);

            holder.vUserProfileRoot.animate().translationY(0).setDuration(300).setInterpolator(INTERPOLATOR);
            holder.ivUserProfilePhoto.animate().translationY(0).setDuration(300).setStartDelay(200).setInterpolator(INTERPOLATOR);
            holder.vUserDetails.animate().translationY(0).setDuration(300).setStartDelay(200).setInterpolator(INTERPOLATOR);
            holder.vUserStats.animate().alpha(1).setDuration(200).setStartDelay(400).setInterpolator(INTERPOLATOR).start();
        }
    }

    private void animateUserProfileOptions(ProfileOptionsViewHolder holder) {

        if (!locekdAnimations) {
            holder.vButtons.setTranslationY(-holder.vButtons.getHeight());
            holder.vUnderline.setScaleX(0);

            holder.vButtons.animate().translationY(0).setDuration(300).setStartDelay(USER_OPTIONS_ANIMATION_DELAY).setInterpolator(INTERPOLATOR);
            holder.vUnderline.animate().scaleX(1).setDuration(200).setStartDelay(USER_OPTIONS_ANIMATION_DELAY  + 300).setInterpolator(INTERPOLATOR).start();
        }

    }

    private void animatePhoto(PhotoViewHolder holder) {
        if (!locekdAnimations) {
            if (lastAnimatedItem == holder.getPosition()) {
                setLockedAnimations(true);
            }

            long animationDelay = profileHeaderAnimationStartTime + MAX_PHOTO_ANIMATION_DELAY - System.currentTimeMillis();
            if (profileHeaderAnimationStartTime == 0) {
                animationDelay = holder.getPosition() * 30 + MAX_PHOTO_ANIMATION_DELAY;
            } else if (animationDelay < 0) {
                animationDelay = holder.getPosition() * 30;
            } else {
                animationDelay += holder.getPosition() * 30;
            }

            holder.flRoot.setScaleX(0);
            holder.flRoot.setScaleY(0);
            holder.flRoot.animate()
                    .scaleY(1)
                    .scaleX(1)
                    .setDuration(200)
                    .setInterpolator(INTERPOLATOR)
                    .setStartDelay(animationDelay)
                    .start();
        }
    }

    public void setLockedAnimations(boolean locekdAnimations) {
        this.locekdAnimations = locekdAnimations;
    }

    @Override
    public int getItemCount() {
        return MIN_ITEMS_COUNT + photos.size();
    }

    /**
     * VIEW HOLDERs
     */
    static class ProfileHeaderViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.ivUserProfilePhoto)
        ImageView ivUserProfilePhoto;
        @InjectView(R.id.vUserDetails)
        View vUserDetails;
        @InjectView(R.id.btnFollow)
        Button btnFollow;
        @InjectView(R.id.vUserStats)
        View vUserStats;
        @InjectView(R.id.vUserProfileRoot)
        View vUserProfileRoot;

        public ProfileHeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    static class ProfileOptionsViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.btnGrid)
        ImageButton btnGrid;
        @InjectView(R.id.btnList)
        ImageButton btnList;
        @InjectView(R.id.btnMap)
        ImageButton btnMap;
        @InjectView(R.id.btnTagged)
        ImageButton btnComments;
        @InjectView(R.id.vUnderline)
        View vUnderline;
        @InjectView(R.id.vButtons)
        View vButtons;

        public ProfileOptionsViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.flRoot)
        FrameLayout flRoot;
        @InjectView(R.id.ivPhoto)
        ImageView ivPhoto;

        public PhotoViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }
}
