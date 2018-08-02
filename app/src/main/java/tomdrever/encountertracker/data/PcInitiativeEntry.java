package tomdrever.encountertracker.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorRes;

import tomdrever.encountertracker.R;

public class PcInitiativeEntry extends BaseInitiativeEntry {
    public PcInitiativeEntry(String name, int initiative) {
        super(name, initiative);
    }

    @Override
    public @ColorRes int getColour() {
        return R.color.colorPc;
    }

    // Parcelable

    /** recreate object from parcel */
    private PcInitiativeEntry(Parcel in) {
        super(in);
    }

    public static final Parcelable.Creator<BaseInitiativeEntry> CREATOR
            = new Parcelable.Creator<BaseInitiativeEntry>() {

        public PcInitiativeEntry createFromParcel(Parcel in) {
            return new PcInitiativeEntry(in);
        }

        public PcInitiativeEntry[] newArray(int size) {
            return new PcInitiativeEntry[size];
        }
    };
}
