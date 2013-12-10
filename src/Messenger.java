

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Messenger{

	private List<MessageListener> listeners = new ArrayList<MessageListener>();;
	private MessageListener listener;
	private static Messenger instance = null;
	
	   protected Messenger() {}
	   public synchronized static Messenger getInstance() {
	      if(instance == null) {
	         instance = new Messenger();
	      }
	      return instance;
	   }

        public synchronized void addLogListener(MessageListener listener) {
            listeners.add(listener);
		}



	   public synchronized void log(String message) {

		   for (MessageListener listener : getLogListeners()) {
               if(listener!=null) {
                  ((MessageListener)listener).messageAdded(message);
               }
		   }
		}

       public boolean hasLogListener() {
            return (getLogListeners().size() > 0);
       }


	   private List<MessageListener> getLogListeners() {
		    return listeners;
	   }


    public static void main(String[] args){

        Messenger messenger = Messenger.getInstance();

                  messenger.addLogListener(new MessageListener() {
                      @Override
                      public void messageAdded(String message) {
                          System.out.println("listener is triggers message is: ["+message+"]");
                      }
                  });

        messenger.log("file event now :)");
    }
}
