package actor;

import java.util.ArrayList;
import java.util.List;

import com.typesafe.config.ConfigFactory;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.Member;
import pi_swarm_approx.JobMessage;

public class Master extends AbstractActor{	
	private boolean hasBackends;
    private Cluster cluster = Cluster.get(getContext().system());
    
    @Override
    public void preStart() {
        cluster.subscribe(self(), ClusterEvent.MemberUp.class);
    }
    
    @Override
    public void postStop() {
        cluster.unsubscribe(self());
    }

	@Override
	public Receive createReceive() {
		System.out.println("Master actor receievd msg");
		return receiveBuilder()
                .match(JobMessage.class, job -> !hasBackends, this::onJobReceivedNoWorker)
                .match(JobMessage.class, this::onJobReceived)
                .match(ClusterEvent.MemberUp.class, this::onMemberUp)
                .build();
	}
	
	public void onJobReceivedNoWorker(JobMessage job) {
		System.out.println("No worker");
        sender().tell("Failed job", sender());
    }
	
	private void onJobReceived(JobMessage job) {
		System.out.println("Work sent");
		String workerIP = ConfigFactory.load("master.conf").getString("clustering.seed1.ip");
        String seedPort = ConfigFactory.load("master.conf").getString("clustering.seed1.port");
        String address = String.format("akka://%s@" + workerIP + ":" + seedPort, "MasterSystem");
        
        System.out.println("$$$$$$$$$$$" + workerIP + "-" + seedPort);
        getContext().actorSelection(address + "/user/router1").forward(job, getContext());
    }
        
        //System.out.println("$$$" + workers.size() + "  " + worker.path());
	
	 private void onMemberUp(ClusterEvent.MemberUp memberUp) { 
		 Member member = memberUp.member();
	        if (member.hasRole("worker")) {
	        	System.out.println("Worker is up!");
	            hasBackends = true;
	        }
	 }
}