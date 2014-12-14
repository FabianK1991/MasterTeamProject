package mtp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import play.twirl.api.Html;

import com.hp.hpl.jena.rdf.model.Model;

import spa.api.ProcessModel;
import spa.api.process.buildingblock.Activity;
import spa.api.process.buildingblock.BusinessObject;
import spa.api.process.buildingblock.Flow;
import spa.api.process.buildingblock.Node;

// Save view for process step
public class ModelEngine {
		// test method
	 	public static void main(String[] args) throws Exception
	    {
	 		//ModelEngine.generateBusinessProcessView(Offline.getMailProcessModel());
	 		System.out.println(Offline.getMailProcessModel().getId());
	    }
	 	
	 	public static Node getStartNode(ProcessModel pm){
	 		Map<String, Integer> m = new HashMap<String, Integer>();
	 		
	 		// check which node has no predecessor
	 		Iterator<Node> iterator = pm.getNodes().iterator();
		    while(iterator.hasNext()) {
		        Node n = iterator.next();
		        
		        Iterator<Flow> iterator2 = n.getNextFlows().iterator();
		        while(iterator2.hasNext()) {
		        	Flow f = iterator2.next();
		        	
		        	Integer oldValue = m.get(f.getTo().getId());
		        	
		        	if( oldValue != null ){
		        		m.put(f.getTo().getId(), oldValue + 1);
		        	}
		        	else{
		        		m.put(f.getTo().getId(), 1);
		        	}
		        }
		    }
		    
		    // Check which is start node
		    iterator = pm.getNodes().iterator();
		    while(iterator.hasNext()) {
		    	 Node n = iterator.next();
		    	 
		    	 if( m.get(n.getId()) == null ){
		    		 return n;
		    	 }
		    }
		    
		    return null;
	 	}
	 	
	 	private static String getCuttedID(String id){
	 		if( id.lastIndexOf('/') > 0 ){
	 			return id.substring(id.lastIndexOf('/')+1);
	 		}
	 		else{
	 			return id;
	 		}
	 	}
	 	
	 	/*
	 	 * TODO: Implement
	 	 */
	 	public static Node getPreviousActivity(Node currentNode, ProcessModel pm){
	 		return null;
	 	}
	 	
	 	public static Node getNextActivity(Node currentNode){
	 		List<Node> workingNodes = new ArrayList<Node>();
	 		workingNodes.add(currentNode);
	 		
	 		while( workingNodes.size() > 0 ){
	 			Iterator<Flow> iterator2 = workingNodes.get(0).getNextFlows().iterator();
	 			workingNodes.remove(0);
				
		        while(iterator2.hasNext()) {
		        	Flow f = iterator2.next();
		        	
		        	if( f.getTo() instanceof Activity ){
		        		return f.getTo();
		        	}
		        	else{
		        		workingNodes.add(f.getTo());
		        	}
		        }
	 		}
	 		
	 		return null;
	 	}
	 	
	 	public static Node getNodeById(String id, ProcessModel pm){
	 		Iterator<Node> iterator = pm.getNodes().iterator();
		    while(iterator.hasNext()) {
		        Node n = iterator.next();
		        
		        if( n.getId().equals(id) ){
		        	return n;
		        }
		    }
	 		
	 		return null;
	 	}
	 	
	 	public static Html generateCompleteView(ProcessModel pm, Node currentStep){
	 		// Process View
	 		String ProcessView = mtp.ModelEngine.generateBusinessProcessView(pm, currentStep);
	 		
	 		// Work Area
			Html workArea = Mapping.getWorkAreaViewByNode(pm.getId(), currentStep.getId());
			
			// Related Documents / BO's
			String relatedDocumentsView = generateRelatedDocumentsView(currentStep);
	 		
	 		return views.html.test.render(ProcessView, pm.getName() + " - " + currentStep.getName(), workArea, relatedDocumentsView);
	 	}
	 	
	 	public static String generateRelatedDocumentsView(Node currentStep){
	 		String output = "";
	 		
	 		Iterator<BusinessObject> iterator = currentStep.getBusinessObjects().iterator();
			
	        while(iterator.hasNext()) {
	        	BusinessObject bo = iterator.next();
	        	
	        	output += "<div>ID: " + bo.getId() + "</div><div>NAME: " + bo.getName() + "</div>";
	        }
	 		
	 		return output;
	 	}

		public static String generateBusinessProcessView(ProcessModel pm, Node currentStep){
			Node startNode = ModelEngine.getStartNode(pm);
			
			if( startNode != null ){
				String out = "";
				String scriptString = "var cArrow = $cArrows('#ProcessModelContainer', { render: { strokeStyle: '#000000', lineWidth: 2 } })";
				
				// Let's go!
				List<NodeWrapper> workingNodes = new ArrayList<NodeWrapper>();
				List<String> addedIds = new ArrayList<String>();
				
				NodeWrapper nw = new NodeWrapper();
				nw.n = startNode;
				nw.top = 0;
				nw.left = 0;
				workingNodes.add(nw);
				
				while(workingNodes.size() != 0){
					NodeWrapper currentNodeWrapper = workingNodes.get(0);
					Node currentNode = currentNodeWrapper.n;
					workingNodes.remove(0);
					
					// add to output
					String cssClass = currentNode.getClass().getSimpleName() + " flowObject";
					
					//currentNode.getClass().getSimpleName()
					if( !addedIds.contains(getCuttedID(currentNode.getId())) ){
						if( currentStep != null && currentStep.getId().equals( currentNode.getId() ) ){
							cssClass += " Selected";
						}
						
						out += "<span id=\"Act_"+ getCuttedID(currentNode.getId()) +"\" style=\"left:"+currentNodeWrapper.left+"px;top:"+currentNodeWrapper.top+"px\" class=\""+cssClass+"\">" + getCuttedID(currentNode.getId()) + "</span>";
						addedIds.add(getCuttedID(currentNode.getId()));
					}
					
					// add successor nodes
					int amount = 0;
					List<String> successorNodes = new ArrayList<String>();
					
					Iterator<Flow> iterator = currentNode.getNextFlows().iterator();
			        while(iterator.hasNext()) {
			        	Flow f = iterator.next();
			        	Node suc = f.getTo();
			        	
			        	successorNodes.add(suc.getId());
			        	
			        	
			        }
			        
			        // Sort because sometimes the order is wrong
			        Collections.sort(successorNodes);
			        
			        for(int i=0;i<successorNodes.size();i++){
			        	String suc = successorNodes.get(i);
			        	
			        	scriptString += ".arrow('#Act_" + getCuttedID(currentNode.getId()) + "', '#Act_" + getCuttedID(suc) + "')";
			        	
		        		// Only add if not already worked on
			        	if( !addedIds.contains(getCuttedID(suc)) ){
			        		nw = new NodeWrapper();
							nw.n = ModelEngine.getNodeById(suc, pm);
							nw.left = currentNodeWrapper.left + 100;
							nw.top = currentNodeWrapper.top + (amount * 40);
			        		workingNodes.add(nw);
			        	}
			        	
			        	amount++;
			        }
				}
				
				return out + "<script type=\"text/javascript\">" + scriptString + "</script>";
			}
			
			return null;
		}
}
