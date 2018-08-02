package tomdrever.encountertracker.list;

import tomdrever.encountertracker.data.BaseInitiativeEntry;

public interface InitiativeListUpdatedListener {
    void onNewEntryAdded(BaseInitiativeEntry entry);

    void onEntryUpdated(BaseInitiativeEntry entry, int position);
}
