package Manager1;

import Tasks1.Subtask;
import Tasks1.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private Map<Integer, Node> nodesOfTasks = new HashMap<>();

    private Node head;
    private Node tail;

    @Override
    public ArrayList<Task> getHistory() {
        return (ArrayList<Task>) getTasks();
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        if (nodesOfTasks.containsKey(task.getTaskId())) {
            remove(task.getTaskId());
        }
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Node node : nodesOfTasks.values()) {
            if (node.getElement() instanceof Subtask) {
                if (((Subtask) node.getElement()).getEpicId() == id) {
                    tasks.add(node.getElement());
                }
            }
        }
        for (Task task : tasks) {
            removeNode(nodesOfTasks.get(task.getTaskId()));
        }
        if (nodesOfTasks.containsKey(id)) {
            removeNode(nodesOfTasks.get(id));
        }
    }

    public void linkLast(Task task) {
        if (nodesOfTasks.isEmpty()) {
            Node newNode = new Node(null, task, null);
            nodesOfTasks.put(task.getTaskId(), newNode);
            head = newNode;
            tail = newNode;
        } else if (nodesOfTasks.containsKey(task.getTaskId())) {
            removeNode(nodesOfTasks.get(task.getTaskId()));
            Node newNode = new Node(tail, task, null);
            nodesOfTasks.put(task.getTaskId(), newNode);
            this.tail = newNode;
        } else {
            Node newNode = new Node(tail, task, null);
            nodesOfTasks.put(task.getTaskId(), newNode);
            tail.setNext(newNode);
            this.tail = newNode;
        }
    }

    public void removeNode(Node node) {
        if (node == head && node ==tail) {
            nodesOfTasks.clear();
        } else if (node == head) {
            node.getNext().setPrev(null);
            head = node.getNext();
            nodesOfTasks.remove(node.getElement().getTaskId());
        } else if (node == tail) {
            node.getPrev().setNext(null);
            tail = node.getPrev();
            nodesOfTasks.remove(node.getElement().getTaskId());
        } else {
            node.getNext().setPrev(node.getPrev());
            node.getPrev().setNext(node.getNext());
            nodesOfTasks.remove(node.getElement().getTaskId());
        }
    }

    public List<Task> getTasks() {
        List<Task> tasksList = new ArrayList<>();
        Node node = head;
        while (node != null) {
            tasksList.add(node.getElement());
            node = node.getNext();
        }
        return tasksList;
    }

    public Map<Integer, Node> getNodesOfTasks() {
        return nodesOfTasks;
    }

    public Node getHead() {
        return head;
    }

    public Node getTail() {
        return tail;
    }
}
