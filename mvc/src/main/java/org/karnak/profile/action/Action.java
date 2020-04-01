package org.karnak.profile.action;

import org.dcm4che3.data.VR;
public class Action {

   private ActionInterface action;

   public Action(ActionInterface action) {
      this.action = action;
   }

   public void execute(VR vr){
      this.action.execute();
   }
}