package org.karnak.profile;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.karnak.profile.action.Action;
import org.karnak.profile.action.Remove;
import org.karnak.profile.action.Replace;

public class Exemple {
    public void exemple(Attributes attributes) {
        //Action action = new Action();
        Action remove = new Remove();
        // Action replace = new Replace();

        //store (init app)
        Profile profile1 = new Profile();
        profile1.register(Tag.StudyInstanceUID, remove);
        profile1.register(Tag.PatientName, remove);

        //execute (stream registry)
        // profile1.execute();
    }
}