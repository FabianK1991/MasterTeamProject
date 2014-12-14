package mtp;

import play.mvc.Http;
import play.twirl.api.Html;
import spa.api.ProcessModel;

/*
 * TODO: handle through database instead of hardcode
 */
public class Mapping {
	public static ProcessModel getProcessModel(String id){
		if( id.equals("http://mail.process/process1") ){
			return Offline.getMailProcessModel();
		}
		
		return null;
	}
	
	public static Html getWorkAreaViewByNode(String ProcessModelId, String StepId){
		
		
		return views.html.defaultView.render();
	}
}
