package samhithak.com.imagegallery;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ImageFragment extends Fragment implements View.OnClickListener {
    private Image mImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mImage = bundle.getParcelable(HomeActivity.IMAGE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View top = inflater.inflate(R.layout.fragment_image, container, false);
        TextView title = (TextView) top.findViewById(R.id.title);
        TextView user = (TextView) top.findViewById(R.id.user);
        TextView descriptionUrl = (TextView) top.findViewById(R.id.description_url);
        TextView id = (TextView) top.findViewById(R.id.photo_id);
        ImageView image = (ImageView) top.findViewById(R.id.image);
        final FloatingActionButton share = (FloatingActionButton) top.findViewById(R.id.btn_share);
        final FloatingActionButton twitter = (FloatingActionButton) top.findViewById(R.id.btn_twitter);
        final FloatingActionButton sms = (FloatingActionButton) top.findViewById(R.id.btn_sms);
        final FloatingActionButton email = (FloatingActionButton) top.findViewById(R.id.btn_email);
        twitter.setOnClickListener(this);
        sms.setOnClickListener(this);
        email.setOnClickListener(this);

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int visibility = View.GONE;
                int shareIcon;
                if(twitter.getVisibility() == View.GONE) {
                    visibility = View.VISIBLE;
                    shareIcon = R.drawable.icon_share_close;
                } else {
                    shareIcon = R.drawable.icon_share_white;
                }

                twitter.setVisibility(visibility);
                sms.setVisibility(visibility);
                email.setVisibility(visibility);
                share.setImageDrawable(getResources().getDrawable(shareIcon));
            }
        });

        if (mImage != null) {
            title.setText(getResources().getString(R.string.title, mImage.getTitle()));
            user.setText(getResources().getString(R.string.user, mImage.getUser()));
            descriptionUrl.setText(getResources().getString(R.string.description_url, mImage.getDescriptionUrl()));
            id.setText(getResources().getString(R.string.id, mImage.getId()));
            Picasso.with(getContext()).load(mImage.getUrl()).into(image);
        }

        return top;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_twitter:
                String url = "http://www.twitter.com/intent/tweet?text=" + mImage.getUrl();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
            case R.id.btn_email:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Photo from Flickr");
                emailIntent.putExtra(Intent.EXTRA_TEXT, mImage.getUrl());
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
                break;
            case R.id.btn_sms:
                Intent smsIntent = new Intent(Intent.ACTION_SEND);
                smsIntent.setType("text/plain");
                smsIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Photo from Flickr");
                smsIntent.putExtra(android.content.Intent.EXTRA_TEXT, mImage.getUrl());
                startActivity(Intent.createChooser(smsIntent, "Send a message..."));
                break;
        }

    }
}
