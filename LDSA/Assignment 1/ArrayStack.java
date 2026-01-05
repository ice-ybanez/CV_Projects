package assignment1;

public class ArrayStack implements Stack<Object> {
    private Object[] S;

    public ArrayStack(int capacity) {
        // TASK 2.A.a
        throw new RuntimeException("Not implemented yet!");
    }

    public void push(Object x) {
        // TASK 2.A.b
        throw new RuntimeException("Not implemented yet!");
    }

    public Object pop() {
        // TASK 2.A.c
        throw new RuntimeException("Not implemented yet!");
    }

    public Object peek() {
        // TASK 2.A.d
        throw new RuntimeException("Not implemented yet!");
    }

    public boolean empty() {
        // TASK 2.A.e
        throw new RuntimeException("Not implemented yet!");
    }

    public static void main(String[] args) {
        Stack<Object> test = new ArrayStack(20);
        System.out.println(test.empty());
        for (int i=0; i<10; i++) {
            test.push(i+100);
        }
        System.out.println(test.empty());
        System.out.println(test.peek());
        for (int i=0; i<5; i++) {
            int x = (int)test.pop();
            System.out.print(x + " ");
        }
        System.out.println();
        for (int i=0; i<15; i++) {
            test.push(i);
        }
        while (!test.empty()) {
            int x = (int)test.pop();
            System.out.print(x + " ");
        }
        System.out.println();
    }
}
