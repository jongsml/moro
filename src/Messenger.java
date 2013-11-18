

import java.util.ArrayList;
import java.util.List;

public class Messenger {

	private List<MessageListener> listeners = new ArrayList<MessageListener>();
	private MessageListener listener;
	private static Messenger instance = null;
	
	   protected Messenger() {
	      // Exists only to defeat instantiation.
	   }
	   public synchronized static Messenger getInstance() {
	      if(instance == null) {
	         instance = new Messenger();
	      }
	      return instance;
	   }

        public void addLogListener(MessageListener listener) {
		    listeners.add(listener);
		}
	   
	   public void log(String message) {
           System.out.println(message);
		   for (MessageListener listener : listeners) {
		       System.out.println("PIEP");
		   }
		}
	   
	   public boolean hasLogListener() {
		   boolean listenerFound = false;
		   int index = 0;
		   
		   MessageListener[] listeners = getLogListeners();
		   while(!listenerFound && index < listeners.length) {
		    // check if listeners are the same object.
		    listenerFound = (listeners[index] == listener);
		    // increase index on iteration.
		    index++;
		   }
		   return listenerFound;
	   }
	
	   private MessageListener[] getLogListeners() {
		
		return null;
	}
}
