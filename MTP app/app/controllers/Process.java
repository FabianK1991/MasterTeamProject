package controllers;

import java.util.HashSet;
import java.util.Iterator;
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


public class Process extends Controller
{

    public static Result createProcess() throws Exception
    {
        Model model = Application.getModel(request());
        if(model == null) { return internalServerError(); }

        ResIterator idIterator = model.listResourcesWithProperty(RDF.type, model.getResource("http://www.spa.org/core#Process"));
        String id = null;

        while(idIterator.hasNext()) {
            id = idIterator.nextResource().toString();
        }

        if(id == null || Select.getProcess(id).size() > 0) { return internalServerError("A process with the ID \"" + id + "\" does already exist. Delete this process or use the update method."); }

        try {
            Update.insertData(model);
        } catch(Exception e) {
            e.printStackTrace();
            return internalServerError();
        }
        return ok(id);
    }


    public static Result updateProcess() throws Exception
    {
        Model model = Application.getModel(request());
        if(model == null) { return internalServerError(); }

        ResIterator idIterator = model.listResourcesWithProperty(RDF.type, model.getResource("http://www.spa.org/core#Process"));
        String id = null;

        while(idIterator.hasNext()) {
            id = idIterator.nextResource().toString();
        }

        if(id == null) { return internalServerError(); }
        Update.deleteProcess(id);

        try {
            Update.insertData(model);
        } catch(Exception e) {
            e.printStackTrace();
            return internalServerError();
        }

        return ok(id);
    }


    public static Result deleteProcess(String id) throws Exception
    {
        Update.deleteProcess(id);
        return ok();
    }


    public static Result getProcess(String id) throws Exception
    {
        Model model = ModelFactory.createDefaultModel();
        model.add(Select.getProcess(id));

        response().setContentType("application/x-download");
        response().setHeader("Content-disposition", "attachment; filename=model.ttl");
        return ok(Application.modelToString(model));
    }


    public static Result getProcessIDsKeyword() throws Exception
    {
        Set<String> keywords = Application.getParameterValues("q", request().queryString());

        // filter
        String filter = "FILTER regex(?o, \"";
        Iterator<String> iter = keywords.iterator();
        while(iter.hasNext()) {
            filter += iter.next();
            if(iter.hasNext()) {
                filter += "|";
            }
        }
        filter += "";
        filter += "\", \"i\")";

        // query
        String q = "SELECT DISTINCT ?q {{?q rdf:type <http://www.spa.org/core#Process>; ?p ?o. " + filter + " }";
        q += "UNION {?q rdf:type <http://www.spa.org/core#Process>; ?p ?a. ?a ?p2 ?o. " + filter + " }}";

        Set<String> ids = Select.processQueryIDs(q);
        return ok(Json.toJson(ids));
    }


    public static Result getProcessIDsBO() throws Exception
    {
        Set<String> bo = Application.getParameterValues("q", request().queryString());

        Set<String> ids = new HashSet<String>();
        for(String boID : bo) {
            String q = "SELECT ?q {";
            q += "{?q rdf:type spa:Process. ?q spa:usesBusinessObject <" + boID + ">.}";
            q += " UNION {?q rdf:type spa:Process. ?q spa:composedOf ?a. ?a spa:usesBusinessObject <" + boID + ">. }";
            q += "}";
            ids.addAll(Select.processQueryIDs(q));
        }

        return ok(Json.toJson(ids));
    }
}
