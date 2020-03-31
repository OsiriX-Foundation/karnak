package org.karnak.profile;

import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.karnak.profile.action.Action;
import org.karnak.profile.action.Remove;
import org.karnak.profile.action.Replace;

public class Exemple {
    public void exemple() {
        //Action action = new Action();
        Action remove = new Action(new Remove());
        Action replace = new Action(new Replace());

        //store (init app)
        Profile profile1 = new Profile();
        profile1.register(Tag.StudyInstanceUID, remove);
        profile1.register(Tag.PatientName, replace);

        //execute (stream registry)
        profile1.execute(Tag.StudyInstanceUID, VR.LO);
        profile1.execute(Tag.PatientName, VR.TM);
    }
}