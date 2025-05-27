package Manager;

import Tasks.Task;

public class Node {
    private Task element;
    private Node next;
    private Node prev;

    Node(Node prev, Task element, Node next) {
        this.element = element;
        this.prev = prev;
        this.next = next;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public Task getElement() {
        return element;
    }

    public void setElement(Task element) {
        this.element = element;
    }

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }
}
