package assignment;

import java.util.LinkedList;
import java.util.Random;

public class BinarySearchTree<T> extends BinarySearchTreeImpl<T> implements SearchTree<T> {
    public void insert(int key, T value) {
        if (root==null) {
            root = new Node<T>(key, value);
        }
        else {
            insert(root, key, value);
        }
    }

    public void delete(int key) {
        Node<T> x = search(root, key);
        if (x!=null) {
            delete(x);
        }
    }

    public T search(int key) {
        Node<T> x = search(root, key);
        return x==null?null:x.value;
    }

    public int minimum() {
        return root==null?-1:minimum(root).key;
    }

    public int maximum() {
        return root==null?-1:maximum(root).key;
    }

    public int successor(int key) {
        Node<T> x = search(root, key);
        if (x==null) {
            return -1;
        }
        Node<T> s = successor(x);
        return s==null?-1:s.key;
    }

   public int predecessor(int key) {
        Node<T> x = search(root, key);
        if (x==null) {
            return -1;
        }
        Node<T> p = predecessor(x);
        return p==null?-1:p.key;
    }

    public LinkedList<T> inorder() {
        return inorderTreeWalk(root);
    }

    public int depth() {
        return depth(root);
    }

    public static void main(String[] args) {
        SearchTree<String> tree = new BinarySearchTree<String>();
        System.out.println("search in empty tree = " + tree.search(0));
        System.out.println("minimum in empty tree = " + tree.minimum());
        System.out.println("minimum in empty tree = " + tree.maximum());
        System.out.println("successor in empty tree = " + tree.successor(0));
        System.out.println("depth in empty tree = " + tree.depth());
        tree.delete(0);
        new Random(4321).ints(20, 1, 20).forEach(
                k -> {
                    tree.insert(k, String.valueOf(k*10));
                    System.out.printf("insert %2d: ",k);
                    tree.inorder().forEach(
                            x -> System.out.printf("%4s", x)
                    );
                    System.out.println();
                    System.out.println("depth = " + tree.depth());
                    System.out.println("minimum = " + tree.minimum());
                    System.out.println("maximum = " + tree.maximum());
                }
        );
        System.out.print("search: ");
        for (int i=0; i<=20; i++) {
            System.out.printf("%5s", tree.search(i));
        }
        System.out.println();
        System.out.print("successors: ");
        for (int i=0; i<=20; i++) {
            System.out.printf("%5d", tree.successor(i));
        }
        System.out.println();
        System.out.print("predecessors: ");
        for (int i=0; i<=20; i++) {
            System.out.printf("%5d", tree.predecessor(i));
        }
        System.out.println();
        new Random(1234).ints(20, 1, 20).forEach(
                k -> {
                    tree.delete(k);
                    System.out.printf("delete %2d: ",k);
                    tree.inorder().forEach(
                            x -> System.out.printf("%4s", x)
                    );
                    System.out.println();
                }
        );
    }
}
