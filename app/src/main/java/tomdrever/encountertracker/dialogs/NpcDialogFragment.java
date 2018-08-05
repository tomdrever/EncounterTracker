package tomdrever.encountertracker.dialogs;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;

import butterknife.BindView;
import tomdrever.encountertracker.HpListAdapter;
import tomdrever.encountertracker.R;
import tomdrever.encountertracker.data.NpcInitiativeEntry;
import tomdrever.encountertracker.list.InitiativeListUpdatedListener;

public class NpcDialogFragment extends BaseDialogFragment {

    @BindView(R.id.add_hp_item_fab)
    FloatingActionButton addHpItemFab;

    @BindView(R.id.npc_hp_list)
    RecyclerView npcHpListRecyclerView;

    ArrayList<NpcInitiativeEntry.HpItem> npcHpList;
    HpListAdapter hpListAdapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected String getTitle() {
        return "NPC";
    }

    public static NpcDialogFragment newInstance(InitiativeListUpdatedListener listener) {
        NpcDialogFragment fragment = new NpcDialogFragment();
        fragment.initiativeListUpdatedListener = listener;
        return fragment;
    }

    public static NpcDialogFragment newInstance(InitiativeListUpdatedListener listener, NpcInitiativeEntry entry, int position) {
        NpcDialogFragment fragment = new NpcDialogFragment();
        fragment.initiativeListUpdatedListener = listener;
        fragment.entry = entry;
        fragment.position = position;
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (position == -1) {
            npcHpList = new ArrayList<>();
            npcHpList.add(new NpcInitiativeEntry.HpItem(0));
        } else
            npcHpList = ((NpcInitiativeEntry) entry).getHpItems();

        layoutManager = new LinearLayoutManager(getContext());
        npcHpListRecyclerView.setLayoutManager(layoutManager);

        hpListAdapter = new HpListAdapter(getContext(), npcHpList);
        npcHpListRecyclerView.setAdapter(hpListAdapter);

        addHpItemFab.show();
        addHpItemFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hide keyboard
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                View focusView = getActivity().getCurrentFocus();

                if (focusView == null)
                    focusView = new View(getActivity());

                imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);

                // Add HP item
                npcHpList.add(new NpcInitiativeEntry.HpItem(0));
                hpListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected boolean saveEntry() {

        // Get and check name
        String name = entryName.getText().toString();
        if (name.equals("")) return false;

        // Get and check initiative
        String initiative = entryInitiative.getText().toString();
        if (initiative.equals("")) return false;

        // Get and check items
        for (NpcInitiativeEntry.HpItem hpItem : npcHpList) {
            if (hpItem.getMaxHp() == 0)
                return false;
            else {
                if (hpItem.getCurrentHp() == 0) {
                    hpItem.setCurrentHp(hpItem.getMaxHp());
                }
            }
        }

        if (position == -1)
            initiativeListUpdatedListener.onNewEntryAdded(new NpcInitiativeEntry(name, Integer.valueOf(initiative), npcHpList));
        else
            initiativeListUpdatedListener.onEntryUpdated(new NpcInitiativeEntry(name, Integer.valueOf(initiative), npcHpList), position);

        return true;
    }
}
