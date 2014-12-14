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
		 
		// Create new process instance
		if( action != null && action.equals("create") && request().getQueryString("process") != null ){
			session("process", request().getQueryString("process"));
			
			ProcessModel pm = Mapping.getProcessModel(session("process"));
			Node startNode = ModelEngine.getNextActivity(ModelEngine.getStartNode(pm));
			
			session("step", startNode.getId());
			
			String ProcessView = mtp.ModelEngine.generateBusinessProcessView(pm, startNode);
			Html workArea = views.html.defaultView.render();
			
			return ok(views.html.test.render(ProcessView, pm.getName() + " - " + startNode.getName(), workArea));
		}
		// Assume we go a step ahead
		else if( session("process") != null ){
			//Cache.get(session.getId() + "-messages", ProcessInstance.class);
			//Cache.set(session.getId() + "-messages", new ProcessInstance(pm));
			
			ProcessModel pm = Mapping.getProcessModel(session("process"));
			Node currentStep = ModelEngine.getNextActivity(ModelEngine.getNodeById(session("step"), pm));
			
			// End reached
			if( currentStep == null ){
				return ok(views.html.end.render(pm.getName() + " - Finished!", pm.getName()));
			}
			
			// Update session variable
			session("step", currentStep.getId());
			
			// Render
			String ProcessView = mtp.ModelEngine.generateBusinessProcessView(pm, currentStep);
			Html workArea = Mapping.getWorkAreaViewByNode(pm.getId(), currentStep.getId());
			
			return ok(views.html.test.render(ProcessView, pm.getName() + " - " + currentStep.getName(), workArea));
		}
		
		// Render example process
		return ok(views.html.test.render(mtp.ModelEngine.generateBusinessProcessView(mtp.Offline.getMailProcessModel(), null), action, views.html.defaultView.render()));
	}
}
