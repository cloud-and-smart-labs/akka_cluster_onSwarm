package actor;

import java.util.ArrayList;

import akka.actor.AbstractActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.Member;
import pi_swarm_approx.JobMessage;
import utility.Point;

public class PiWorker extends AbstractActor {
	  private Cluster cluster = Cluster.get(getContext().system());	
	  
	  private ArrayList<Point> points;
	  
	  private void throwDarts(int size) {
	    for (int i = 0; i < size; i++) {
	    	points.add(Point.genRandPoint());
	    }
	  }
	  
	  private void approximatePi(JobMessage job) {
		  System.out.println("In pi approx");
		this.throwDarts(50);
	    int total = 0; // Keep track of total points thrown.
	    int inside = 0; // Keep track of points inside the circle.
	        for (Point p : points) {
	            if (p.x * p.x + p.y * p.y <= 1) {
	                inside += 1;
	            }
	            total += 1;
	        }
	        float pi_val = 4 * ((float) inside) / total;
	        System.out.println(pi_val);
	        sender().tell(pi_val, self());
		    }
		  
		  @Override
		  public void preStart() {
			  cluster.subscribe(self(), ClusterEvent.MemberUp.class);
			  points = new ArrayList<Point>();
		  }
		  
		  @Override
		    public void postStop() {
		        cluster.unsubscribe(self());
		    }
		  
		  @Override
		    public Receive createReceive() {
			  System.out.println("PiWorker actor received msg");
			  return receiveBuilder()
					  .match(JobMessage.class, this::approximatePi)
		              .build();
		    }
	}