package pi_swarm_approx;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.CompletableFuture;
import com.typesafe.config.Config;

import actor.Master;
import utility.configs; 

public class MainMaster{	
	public static void main(String[] args) {
		System.out.println("In MainMaster");
		String port = args[0];
        Config config = configs.getConfig("master", "master.conf");
		String clusterName = config.getString("clustering.cluster.name");
		
		ActorSystem system = ActorSystem.create(clusterName, config);
		System.out.println("Created master actor system");
		ActorRef master = system.actorOf(Props.create(Master.class), "master");
		
		FiniteDuration interval = Duration.create(2, TimeUnit.SECONDS);
        Timeout timeout = new Timeout(Duration.create(5, TimeUnit.SECONDS));
        ExecutionContext ec = system.dispatcher();
        //AtomicInteger counter = new AtomicInteger();
         
        system.scheduler().schedule(interval, interval, () -> Patterns.ask(master, new JobMessage("hello"), timeout)
        		.onComplete(result -> {
                    System.out.println(result);
                    return CompletableFuture.completedFuture(result);
                }, ec)
        		, ec);
	}
}