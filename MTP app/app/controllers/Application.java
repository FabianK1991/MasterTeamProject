package controllers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import mtp.Mapping;
import mtp.ModelEngine;
import play.cache.Cache;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Http.Request;
import play.mvc.*;
import play.twirl.api.Html;
import spa.api.ProcessInstance;
import spa.api.ProcessModel;
import spa.api.process.buildingblock.Flow;
import spa.api.process.buildingblock.Node;
import views.html.*;

public class Application extends Controller
{
	
	public static Result index() {
	    return ok(index.render("swag"));
	}
	
	public static Result test() {
		String action = request().getQueryString("action");
		
		ProcessModel pm = null;
		Node currentStep = null;
		 
		// Create new process instance
		if( action != null && action.equals("create") && request().getQueryString("process") != null ){
			session("process", request().getQueryString("process"));
			
			pm = Mapping.getProcessModel(session("process"));
			currentStep = ModelEngine.getNextActivity(ModelEngine.getStartNode(pm));
			
			session("step", currentStep.getId());
		}
		// Assume we go a step ahead
		else if( action != null && action.equals("back") ){
			pm = Mapping.getProcessModel(session("process"));
			currentStep = ModelEngine.getPreviousActivity(ModelEngine.getNodeById(session("step"), pm), pm);
			
			session("step", currentStep.getId());
		}
		// Assume we go a step ahead
		else if( session("process") != null ){
			// Store Process Instance
			//Cache.get(session.getId() + "-messages", ProcessInstance.class);
			//Cache.set(session.getId() + "-messages", new ProcessInstance(pm));
			pm = Mapping.getProcessModel(session("process"));
			currentStep = ModelEngine.getNextActivity(ModelEngine.getNodeById(session("step"), pm));
			
			// End reached
			if( currentStep == null ){
				return ok(views.html.end.render(pm.getName() + " - Finished!", pm.getName(), pm.getId()));
			}
			
			// Update session variable
			session("step", currentStep.getId());
		}
		
		
		// Start new example process if no action is given
		if( pm == null ){
			session("process", "http://mail.process/process1");
			
			pm = Mapping.getProcessModel(session("process"));
			currentStep = ModelEngine.getNextActivity(ModelEngine.getStartNode(pm));
			
			session("step", currentStep.getId());
		}
		
		// Render
		return ok(ModelEngine.generateCompleteView(pm, currentStep));
	}
}
