package samhithak.com.imagegallery;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
/**
 * TODO: Write Javadoc for Image.
 *
 * @author skamaraju
 */
public class Image implements Parcelable {

    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("url_s")
    private String url;

    @SerializedName("descriptionurl")
    private String descriptionUrl;

    @SerializedName("ownername")
    private String user;


    public Image(Parcel in) {
        String[] data = new String[5];

        in.readStringArray(data);
        this.id = data[0];
        this.title = data[1];
        this.url = data[2];
        this.descriptionUrl = data[3];
        this.user = data[4];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                this.id,
                this.title,
                this.url,
                this.descriptionUrl,
                this.user});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    public String getUrl() {
        return this.url;
    }

    public String getTitle() {
        return this.title;
    }

    public String getId() {
        return this.id;
    }

    public String getUser() {
        return this.user;
    }

    public String getDescriptionUrl() {
        return this.descriptionUrl;
    }
}
