package assignment1;

public class DoubleLinkedList implements List<Object> {
    private class ListNode {
        public ListNode(Object x) {
            key = x;
        }
        public Object key;
        public ListNode prev = null;
        public ListNode next = null;
    }

    public DoubleLinkedList()
    {
        // TASK 1.A
        throw new RuntimeException("Not implemented yet!");
    }

    public void prepend(Object x) {
        // TASK 1.B
        throw new RuntimeException("Not implemented yet!");
    }

    public Object getFirst() {
        // TASK 1.C
        throw new RuntimeException("Not implemented yet!");
    }

    public void deleteFirst() {
        // TASK 1.D
        throw new RuntimeException("Not implemented yet!");
    }

    public void append(Object x) {
        // TASK 1.E
        throw new RuntimeException("Not implemented yet!");
    }

    public Object getLast() {
        // TASK 1.F
        throw new RuntimeException("Not implemented yet!");
    }

    public void deleteLast() {
        // TASK 1.G
        throw new RuntimeException("Not implemented yet!");
    }

    public boolean empty() {
        // TASK 1.H
        throw new RuntimeException("Not implemented yet!");
    }

    public static void main(String[] args) {
        List<Object> test = new DoubleLinkedList();
        System.out.println(test.empty());
        for (int i=0; i<10; i++) {
            test.prepend(i + 100);
        }
        System.out.println(test.empty());
        for (int i=0; i<5; i++) {
            int x = (int)test.getFirst();
            System.out.print(x + " ");
            test.deleteFirst();
        }
        System.out.println();
        for (int i=0; i<10; i++) {
            test.append(i + 200);
        }
        while (!test.empty()) {
            int x = (int)test.getLast();
            System.out.print(x + " ");
            test.deleteLast();
        }
    }
}
