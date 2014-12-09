package controllers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Http.Request;
import play.mvc.Result;
import rdf.Select;
import rdf.Update;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;


public class Application extends Controller
{

    // public static Result index()
    // {
    // return ok(index.render("Your new application is ready."));
    // }

    public static Result getAll() throws Exception
    {
        response().setContentType("application/x-download");
        response().setHeader("Content-disposition", "attachment; filename=model.ttl");
        return ok(modelToString(Select.getAll()));
    }


    public static Result deleteAll() throws Exception
    {
        Update.deleteAll();
        return ok();
    }


    public static Set<String> getParameterValues(String q, Map<String, String[]> map)
    {
        Set<String> values = new HashSet<String>();

        if(map.containsKey(q)) {
            values.addAll(new HashSet<String>(Arrays.asList(map.get(q))));
        }

        return values;
    }


    public static Model getModel(Request request)
    {
        MultipartFormData body = request().body().asMultipartFormData();
        FilePart modelFile = body.getFile("model");
        if(modelFile != null) {
            File file = modelFile.getFile();
            try {
                Model model = ModelFactory.createDefaultModel();
                model.read(file.getAbsolutePath(), "TURTLE");
                return model;
            } catch(Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }


    public static String modelToString(Model model)
    {
        OutputStream stream = new ByteArrayOutputStream();
        model.write(stream, "TURTLE");

        String modelS = new String(((ByteArrayOutputStream) stream).toByteArray());
        return modelS;
    }
}
