package pi_swarm_approx;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import actor.ClusterListener;
import actor.PiWorker;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.FromConfig;
import utility.configs;


public class MainWorker {
	public static void main(String[] args) {
		System.out.println("In MainWorker");

		String port = args[0];
		Config config = configs.getConfig("worker", "master.conf");
		String clusterName = config.getString("clustering.cluster.name");
		
		ActorSystem system = ActorSystem.create(clusterName, config);
		
		system.actorOf(Props.create(ClusterListener.class), String.format("listenerOn%s", port));
		system.actorOf(FromConfig.getInstance().props(Props.create(PiWorker.class)), "router1");
		
    }
}