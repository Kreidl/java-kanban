package manager;

import tasks.Task;

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
        if (nodesOfTasks.containsKey(id)) {
            removeNode(nodesOfTasks.get(id));
        }
    }

    private void linkLast(Task task) {
        final Node node = new Node(tail, task, null);
        if (head == null) { //Список пустой-в начало
            head = node;
        } else {  //Список не пустой-прикрепляем в конец
            tail.setNext(node);
        }
        tail = node;
        nodesOfTasks.put(task.getTaskId(), node);
    }

    private void removeNode(Node node) {
        if (node == head) {
            if (node != tail) {
                node.getNext().setPrev(null);
                head = node.getNext();
            } else {
                head = null;
                tail = null;
            }
        } else if (node == tail) {
            node.getPrev().setNext(null);
            tail = node.getPrev();
        } else {
            node.getNext().setPrev(node.getPrev());
            node.getPrev().setNext(node.getNext());
        }
        nodesOfTasks.remove(node.getElement().getTaskId());
    }

    private List<Task> getTasks() {
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
