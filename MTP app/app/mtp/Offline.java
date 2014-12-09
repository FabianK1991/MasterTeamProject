package mtp;

import spa.api.ProcessModel;
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
}
