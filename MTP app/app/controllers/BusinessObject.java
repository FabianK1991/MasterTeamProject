package controllers;

import java.util.Iterator;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import rdf.Select;
import rdf.Update;


public class BusinessObject extends Controller
{
    public static Result getBusinessObject(String id) throws Exception
    {
        Model model = ModelFactory.createDefaultModel();
        model.add(Select.getBusinessObject(id));

        response().setContentType("application/x-download");
        response().setHeader("Content-disposition", "attachment; filename=model.ttl");
        return ok(Application.modelToString(model));
    }


    public static Result getBusinessObjects() throws Exception
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
        String q = "SELECT DISTINCT ?q {?q rdf:type spa:BusinessObject; ?p ?o. " + filter + " }";

        Set<String> ids = Select.processQueryIDs(q);
        return ok(Json.toJson(ids));
    }


    public static Result getBusinessObjectsPID(String processID) throws Exception
    {
        String q = "SELECT ?q {";
        q += "{<" + processID + ">  spa:usesBusinessObject ?q.}";
        q += "UNION {?q rdf:type spa:BusinessObject. <" + processID + ">  ?p ?a. ?a spa:usesBusinessObject ?q}";
        q += "}";
        Set<String> ids = Select.processQueryIDs(q);
        return ok(Json.toJson(ids));
    }


    public static Result deleteBusinessObject(String id) throws Exception
    {
        Update.deleteBusinessObject(id);
        return ok();
    }
}
