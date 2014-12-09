package controllers;

import java.util.HashSet;
import java.util.Set;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import rdf.Select;
import rdf.Update;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.vocabulary.RDF;


public class ProcessInstance extends Controller
{

    public static Result createProcessInstance() throws Exception
    {
        Model model = Application.getModel(request());
        if(model == null) { return internalServerError(); }

        ResIterator idIterator = model.listResourcesWithProperty(RDF.type, model.getResource("http://www.spa.org/core#ProcessInstance"));
        String id = null;
        while(idIterator.hasNext()) {
            id = idIterator.nextResource().toString();
        }

        if(id == null || Select.getProcessInstance(id).size() > 0) { return internalServerError("A process with the ID \"" + id + "\" does already exist. Delete this process or use the update method."); }

        try {
            Update.insertData(model);
        } catch(Exception e) {
            e.printStackTrace();
            return internalServerError();
        }

        return ok(id);
    }


    public static Result updateProcessInstance() throws Exception
    {
        Model model = Application.getModel(request());
        if(model == null) { return internalServerError(); }

        ResIterator idIterator = model.listResourcesWithProperty(RDF.type, model.getResource("http://www.spa.org/core#ProcessInstance"));
        String id = null;

        while(idIterator.hasNext()) {
            id = idIterator.nextResource().toString();
        }

        if(id == null) { return internalServerError(); }
        Update.deleteProcessInstance(id);

        try {
            Update.insertData(model);
        } catch(Exception e) {
            e.printStackTrace();
            return internalServerError();
        }

        return ok(id);

    }


    public static Result getProcessInstance(String id) throws Exception
    {
        Model model = ModelFactory.createDefaultModel();
        model.add(Select.getProcessInstance(id));
        response().setContentType("application/x-download");
        response().setHeader("Content-disposition", "attachment; filename=model.ttl");
        return ok(Application.modelToString(model));
    }


    public static Result deleteProcessInstance(String id) throws Exception
    {
        Update.deleteProcessInstance(id);
        Update.deleteAgents();
        return ok();
    }


    public static Result getProcessInstancesIDsBO() throws Exception
    {
        Set<String> bo = Application.getParameterValues("q", request().queryString());

        Set<String> ids = new HashSet<String>();
        for(String boID : bo) {
            String q = "SELECT ?q {";
            q += "{?q rdf:type spa:ProcessInstance. ?q spa:usesBusinessObjectInstance <" + boID + ">.}";
            q += " UNION {?q rdf:type spa:ProcessInstance. ?s spa:belongsTo ?q. ?s spa:usesBusinessObjectInstance <" + boID + ">. }";
            q += "}";
            ids.addAll(Select.processQueryIDs(q));
        }

        return ok(Json.toJson(ids));
    }


    public static Result getProcessInstanceIDs(String pid) throws Exception
    {
        Set<String> ids = Select.processQueryIDs("SELECT ?q {?q rdf:type spa:ProcessInstance; spa:instantiatesProcess <" + pid + "> }");
        return ok(Json.toJson(ids));
    }

}
