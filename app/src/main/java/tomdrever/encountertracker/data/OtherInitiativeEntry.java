package tomdrever.encountertracker.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorRes;

import tomdrever.encountertracker.R;

public class OtherInitiativeEntry extends BaseInitiativeEntry{
    public OtherInitiativeEntry(String name, int initiative) {
        super(name, initiative);
    }

    public @ColorRes int getColour() {
        return R.color.colorOther;
    }

    // Parcelable

    /** recreate object from parcel */
    private OtherInitiativeEntry(Parcel in) {
        super(in);
    }

    public static final Parcelable.Creator<BaseInitiativeEntry> CREATOR
            = new Parcelable.Creator<BaseInitiativeEntry>() {

        public OtherInitiativeEntry createFromParcel(Parcel in) {
            return new OtherInitiativeEntry(in);
        }

        public OtherInitiativeEntry[] newArray(int size) {
            return new OtherInitiativeEntry[size];
        }
    };
}
