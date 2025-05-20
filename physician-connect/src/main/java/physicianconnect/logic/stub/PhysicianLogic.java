package logic.stub;

import persistence.stub.*;
import objects.Physician;
import java.util.*;

public class PhysicianLogic {

    public static PhysicianStub tempDB  = PhysicianStub.getInstance();

    public Physician getPhysicianById(int physicianId){
        Physician result = null;
        try {
            if(physicianId > 0){
                result = tempDB.getPhysician(physicianId);
            }    
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        
        return result;
    }

    public Map<Integer, Physician> getAllPhysicians(){
        return tempDB.getAllPhysicians();
    }

    public Physician addPhysician(){ 
        Physician result = null;
        // realistically we should have json or something coming from UI to body
        Physician tempDoc = new Physician(
            0,
         "Pankaj",
          "Jhanji",
           "jhanjip@myumanitoba.ca",
            123
        );

        try{
            if(tempDoc.getUserId() == 0){
            result = tempDB.addPhysician(tempDoc);
            }
        } catch (Exception e){
            System.err.println(e.getMessage());
        }
        
        return result;
    }

    public Physician updatePhysician(){
        // realistically we'd have a Physician object as our parameter
        Physician result = null;
        Physician tempDoc = new Physician(
            1,
         "Pankaj",
          "Jhanji",
           "jhanjip@myumanitoba.ca",
            123
        );
        
        try {
            if(tempDoc.getUserId() > 0){
                result = tempDB.updatePhysician(tempDoc);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return result;
    }

    public Physician deletePhysicianById(int physicianId){
        Physician result = null;
        try {
            if(physicianId > 0){
                result = tempDB.deletePhysician(physicianId);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return result;
    }
    
}
