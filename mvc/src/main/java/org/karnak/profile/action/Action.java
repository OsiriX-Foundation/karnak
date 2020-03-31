package org.karnak.profile.action;

import org.dcm4che3.data.VR;
import org.karnak.profile.action.ActionVR;
public class Action {

   private ActionVR actionVR;

   public Action(ActionVR actionVR) {
      this.actionVR = actionVR;
   }

   public void execute(VR vr){
      switch(vr){
         case LO:
            this.actionVR.LO();
            break;
         case TM:
            this.actionVR.TM();
            break;
         default:
      }
   }
}