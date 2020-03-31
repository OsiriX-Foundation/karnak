package org.karnak.profile;

import java.util.List;
import org.dcm4che3.data.VR;
import org.karnak.profile.action.Action;
import java.util.ArrayList;
import java.util.HashMap;

public class Profile {

    private final HashMap<Integer, Action> actionMap = new HashMap<>();
    private List<Action> history = new ArrayList<Action>();

    public void register(Integer tag, Action action) {
        actionMap.put(tag, action);
    }

    public void execute(Integer tag, VR vr) {
        Action action = actionMap.get(tag);
        if (action == null) {
            throw new IllegalStateException("no command registered for " + tag);
        }
        this.history.add(action); // optional 
        action.execute(vr);        
    }
 }