

import java.util.ArrayList;
import java.util.List;

public class Logger {

	private List<LogListener> listeners = new ArrayList<LogListener>();
	private LogListener listener;
	private static Logger instance = null;
	
	   protected Logger() {
	      // Exists only to defeat instantiation.
	   }
	   public synchronized static Logger getInstance() {
	      if(instance == null) {
	         instance = new Logger();
	      }
	      return instance;
	   }

        public void addLogListener(LogListener listener) {
		    listeners.add(listener);
		}
	   
	   public void log(String message) {
           System.out.println(message);
		   for (LogListener listener : listeners) {
		       System.out.println("PIEP");
		   }
		}
	   
	   public boolean hasLogListener() {
		   boolean listenerFound = false;
		   int index = 0;
		   
		   LogListener[] listeners = getLogListeners();
		   while(!listenerFound && index < listeners.length) {
		    // check if listeners are the same object.
		    listenerFound = (listeners[index] == listener);
		    // increase index on iteration.
		    index++;
		   }
		   return listenerFound;
	   }
	
	   private LogListener[] getLogListeners() {
		
		return null;
	}
}
