package tomdrever.encountertracker.dialogs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import tomdrever.encountertracker.data.OtherInitiativeEntry;
import tomdrever.encountertracker.list.InitiativeListUpdatedListener;

public class OtherEntryDialogFragment extends BaseDialogFragment {


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected String getTitle() {
        return "Entry";
    }

    public static OtherEntryDialogFragment newInstance(InitiativeListUpdatedListener listener) {
        OtherEntryDialogFragment fragment = new OtherEntryDialogFragment();
        fragment.initiativeListUpdatedListener = listener;
        return fragment;
    }

    public static OtherEntryDialogFragment newInstance(InitiativeListUpdatedListener listener, OtherInitiativeEntry entry, int position) {
        OtherEntryDialogFragment fragment = new OtherEntryDialogFragment();
        fragment.initiativeListUpdatedListener = listener;
        fragment.entry = entry;
        fragment.position = position;
        return fragment;
    }

    @Override
    protected boolean saveEntry() {
        // Get and check name
        String name = entryName.getText().toString();
        if (name.equals("")) return false;

        // Get and check initiative
        String initiative = entryInitiative.getText().toString();
        if (initiative.equals("")) return false;

        if (position == -1)
            initiativeListUpdatedListener.onNewEntryAdded(new OtherInitiativeEntry(name, Integer.valueOf(initiative)));
        else
            initiativeListUpdatedListener.onEntryUpdated(new OtherInitiativeEntry(name, Integer.valueOf(initiative)), position);

        return true;
    }
}
