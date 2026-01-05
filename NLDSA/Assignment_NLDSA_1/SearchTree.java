package assignment;

import java.util.LinkedList;

public interface SearchTree<T> {
    void insert(int key, T value);
    void delete(int key);

    T search(int key);
    int minimum();
    int maximum();

    int predecessor(int key);
    int successor(int key);

    LinkedList<T> inorder();
    int depth();
}
