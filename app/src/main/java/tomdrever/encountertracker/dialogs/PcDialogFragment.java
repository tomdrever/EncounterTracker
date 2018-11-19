package tomdrever.encountertracker.dialogs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import tomdrever.encountertracker.R;
import tomdrever.encountertracker.data.PcInitiativeEntry;
import tomdrever.encountertracker.list.InitiativeListUpdatedListener;

public class PcDialogFragment extends BaseDialogFragment {
    @Override
    protected String getTitle() {
        return "PC";
    }

    public static PcDialogFragment newInstance(InitiativeListUpdatedListener listener) {
        PcDialogFragment fragment = new PcDialogFragment();
        fragment.initiativeListUpdatedListener = listener;
        return fragment;
    }

    public static PcDialogFragment newInstance(InitiativeListUpdatedListener listener, PcInitiativeEntry entry, int position) {
        PcDialogFragment fragment = new PcDialogFragment();
        fragment.initiativeListUpdatedListener = listener;
        fragment.entry = entry;
        fragment.position = position;
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up autocomplete for PC names
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        HashSet<String> names = (HashSet<String>) sharedPref.getStringSet(getString(R.string.names_pref), new HashSet<String>());


        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, new ArrayList<>(names));
        AutoCompleteTextView acTextView = (AutoCompleteTextView) entryName;
        acTextView.setAdapter(adapter);

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
            initiativeListUpdatedListener.onNewEntryAdded(new PcInitiativeEntry(name, Integer.valueOf(initiative)));
        else
            initiativeListUpdatedListener.onEntryUpdated(new PcInitiativeEntry(name, Integer.valueOf(initiative)), position);

        return true;
    }
}
