package samhithak.com.imagegallery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter {
    private List<Image> mImageList;
    private WeakReference<Context> mContextRef;
    private AdapterView.OnItemClickListener mOnItemClickListener;
    private int lastPosition = -1;

    public GalleryAdapter(Context context) {
        mContextRef = new WeakReference<>(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContextRef.get());
        View top = inflater.inflate(R.layout.fragment_gallery_item_row, parent, false);
        return new GalleryViewHolder(top);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        GalleryViewHolder viewHolder = (GalleryViewHolder) holder;
        viewHolder.bindView(mImageList.get(position));
        setAnimation(viewHolder.mImageView,position);
    }

    @Override
    public int getItemCount() {
        if (mImageList != null) {
            return mImageList.size();
        }
        return 0;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    private void onItemHolderClick(GalleryViewHolder galleryViewHolder) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(null, galleryViewHolder.itemView, galleryViewHolder.getPosition(), galleryViewHolder.getItemId());
        }
    }

    public void swapData(List<Image> images) {
        mImageList = images;
        notifyDataSetChanged();
    }


    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(mContextRef.get(), android.R.anim.fade_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public Image getImage(int position) {
        return mImageList.get(position);
    }

    private class GalleryViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnClickListener {
        private ImageView mImageView;
        public GalleryViewHolder(View itemView) {
            super(itemView);
        }

        public void bindView(Image image) {
            mImageView = (ImageView) itemView.findViewById(R.id.image);
            mImageView.setOnClickListener(this);
            Picasso.with(mContextRef.get())
                    .load(image.getUrl())
                    .fit()
                    .into(mImageView);
        }


        @Override
        public void onClick(View v) {
            onItemHolderClick(this);
        }
    }
}
