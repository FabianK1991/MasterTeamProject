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

import play.mvc.*;
import views.html.*;

public class Application extends Controller
{
	
	public static Result index() {
	    return ok(index.render("swag"));
		//return ok("Hello world");
	}
	
	public static Result test() {
		return ok(views.html.test.render("Yolo", "Test"));
	}
}
