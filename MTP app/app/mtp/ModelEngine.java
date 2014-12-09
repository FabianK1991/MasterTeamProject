package mtp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
	
		public static void generateBusinessProcessView(ProcessModel pm){
			Node startNode = ModelEngine.getStartNode(pm);
			
			if( startNode != null ){
				// Let's go!
				System.out.println(ModelEngine.getStartNode(pm).getId());
			}
		}
}
