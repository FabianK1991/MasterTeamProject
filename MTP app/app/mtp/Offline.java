package mtp;

import java.util.HashSet;
import java.util.Set;

import spa.api.ProcessModel;
import spa.api.process.buildingblock.Activity;
import spa.api.process.buildingblock.BusinessObject;
import spa.api.process.buildingblock.Event;
import spa.api.process.buildingblock.Flow;
import spa.api.process.buildingblock.Gateway;
import spa.api.process.buildingblock.Event.EventType;
import spa.api.process.buildingblock.Gateway.GatewayType;
import spa.example.MailProcess;

public class Offline {
	public static ProcessModel getMailProcessModel(){
		try {
			return MailProcess.createProcessModel();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	static String nsm  = "http://material.order/";
	
	public static ProcessModel getMaterialOrderProcessModel()
    {
        // create the process
        ProcessModel pm = new ProcessModel();
        pm.setId(nsm + "process2");

        // set name and keywords
        pm.setName("Material Order Process");
        pm.getKeywords().add("material");
        pm.getKeywords().add("order");

        // create activities
        Event start = new Event(pm, EventType.Start);
        start.setId(nsm + "start1");

        Activity scan = new Activity(pm);
        scan.setId(nsm + "scan");
        scan.setName("Scan Mail");
        
        Set<BusinessObject> as = new HashSet<BusinessObject>();
        BusinessObject myBO = new BusinessObject(pm);
        myBO.setId(nsm + "BO1");
        myBO.setName("Sales Order");
        
        as.add(myBO);
        
        scan.setBusinessObjects(as);

        Gateway xor = new Gateway(pm, GatewayType.XOR);
        xor.setId(nsm + "spam_xor");

        Activity read = new Activity(pm);
        read.setId(nsm + "read");
        read.setName("Read Email");

        Activity answer = new Activity(pm);
        answer.setId(nsm + "answer");
        answer.setName("Answer Email");

        Activity delete = new Activity(pm);
        delete.setId(nsm + "delete");
        delete.setName("Delete Email");

        Event end = new Event(pm, EventType.End);
        end.setId(nsm + "end1");

        // add activities to process model
        pm.getNodes().add(start);
        pm.getNodes().add(scan);
        pm.getNodes().add(xor);
        pm.getNodes().add(read);
        pm.getNodes().add(answer);
        pm.getNodes().add(delete);
        pm.getNodes().add(end);

        // create flows
        Flow fl1 = new Flow(pm, start, scan);
        fl1.setId(nsm + "f1");

        Flow fl2 = new Flow(pm, scan, xor);
        fl2.setId(nsm + "f2");

        Flow fl3 = new Flow(pm, xor, read, "no spam");
        fl3.setId(nsm + "f3");

        Flow fl4 = new Flow(pm, read, answer);
        fl4.setId(nsm + "f4");

        Flow fl5 = new Flow(pm, answer, end);
        fl5.setId(nsm + "f5");

        Flow fl6 = new Flow(pm, xor, delete, "spam");
        fl6.setId(nsm + "f6");

        Flow fl7 = new Flow(pm, delete, end);
        fl7.setId(nsm + "f7");

        start.getNextFlows().add(fl1);
        scan.getNextFlows().add(fl2);
        xor.getNextFlows().add(fl3);
        read.getNextFlows().add(fl4);
        answer.getNextFlows().add(fl5);

        xor.getNextFlows().add(fl6);
        delete.getNextFlows().add(fl7);

        return pm;
    }
}
