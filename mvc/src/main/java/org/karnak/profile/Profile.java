package org.karnak.profile;

import java.util.List;

import org.dcm4che3.data.Attributes;
import org.karnak.profile.action.Action;
import org.karnak.profile.action.Remove;
import org.karnak.profile.action.Replace;

import java.util.ArrayList;
import java.util.HashMap;

public class Profile {

    private final HashMap<Integer, Action> actionMap = new HashMap<>();
    private List<Action> history = new ArrayList<Action>();

    /*
    private Action remove = new Remove();
    private Action replace = new Replace();
    public Profile(String path) {
        read JSONFILE in jsonProfile
        foreach [Tag, Action]in jsonProfile:
            if (Action === 'X')
                this.register(Tag, remove)
            if (Action === 'D')
                this.register(Tag, replace)
    }
    */
    public void register(Integer tag, Action action) {
        actionMap.put(tag, action);
    }

    public void execute(Attributes attributes) {
        for (int tag: attributes.tags()) {
            Action action = actionMap.get(tag);
            if (action != null) {
                this.history.add(action); // optional
                action.execute(attributes, tag);
            }
            // Default: remove ? White List system.
        }
    }
}