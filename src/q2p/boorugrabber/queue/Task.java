package q2p.boorugrabber.queue;

import java.util.LinkedList;

public interface Task {
	LinkedList<Task> work();
}