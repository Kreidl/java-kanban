package Manager1;

import Tasks1.Task;

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

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }
}
