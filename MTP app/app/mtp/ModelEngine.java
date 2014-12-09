package mtp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;

import spa.api.ProcessModel;
import spa.api.process.buildingblock.Flow;
import spa.api.process.buildingblock.Node;

// Save view for process step
public class ModelEngine {
		// test method
	 	public static void main(String[] args) throws Exception
	    {
	 		ModelEngine.generateBusinessProcessView(Offline.getMailProcessModel());
	    }
	 	
	 	
	 	
	 	private static Node getStartNode(ProcessModel pm){
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

		public static String generateBusinessProcessView(ProcessModel pm){
			Node startNode = ModelEngine.getStartNode(pm);
			
			if( startNode != null ){
				String out = "";
				String scriptString = "var cArrow = $cArrows('#ProcessModelContainer')";
				
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
						out += "<span id=\"Act_"+ getCuttedID(currentNode.getId()) +"\" style=\"left:"+currentNodeWrapper.left+"px;top:"+currentNodeWrapper.top+"px\" class=\""+cssClass+"\">" + getCuttedID(currentNode.getId()) + "</span>";
						addedIds.add(getCuttedID(currentNode.getId()));
					}
					
					// add successor nodes
					int amount = 0;
					Iterator<Flow> iterator = currentNode.getNextFlows().iterator();
			        while(iterator.hasNext()) {
			        	Flow f = iterator.next();
			        	Node suc = f.getTo();
			        	
			        	scriptString += ".arrow('#Act_" + getCuttedID(currentNode.getId()) + "', '#Act_" + getCuttedID(suc.getId()) + "')";
			        	
		        		// Only add if not already worked on
			        	if( !addedIds.contains(getCuttedID(suc.getId())) ){
			        		nw = new NodeWrapper();
							nw.n = suc;
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
