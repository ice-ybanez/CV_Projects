package assignment;

import java.util.LinkedList;

public class BinarySearchTreeImpl<T> {
    protected static class Node<T> {
        public Node(int key, T value) {
            this.key = key;
            this.value = value;
        }
        public int key;
        public T value;
        public Node<T> parent = null;
        public Node<T> left = null;
        public Node<T> right = null;
    }
    protected Node<T> root = null;

    protected void insert(Node<T> x, int key, T value) {
        // inserts key-value pair into subtree rooted at node
        if (x == null) {    // if tree is empty
            root = new Node<>(key, value);  // set new node as root
            return;
        }
        // determine WHERE to insert new node
        if (key < x.key) {
            // insert into left subtree
            if (x.left == null) {
                x.left = new Node<>(key, value);
                x.left.parent = x;  // setting parent
            } else {
                insert(x.left, key, value); // recursive
            }
        }
        else if (key > x.key) {
            // insert into right subtree
            if (x.right == null) {
                x.right = new Node<>(key, value);
                x.right.parent = x; // setting parent
            } else {
                insert(x.right, key, value); // recursive
            }
        }
        else {
            x.value = value; // replace value if key already exists
        }
    }

    protected LinkedList<T> inorderTreeWalk(Node<T> x) {
        // returns values of all nodes contained in subtree rooted at that node
        LinkedList<T> result = new LinkedList<>();

        if (x != null) {    // if tree is empty
            // go through left subtree
            result.addAll(inorderTreeWalk(x.left));
            // check current node
            result.add(x.value);
            // go through right subtree
            result.addAll(inorderTreeWalk(x.right));
        }

        return result;
    }

    protected Node<T> search(Node<T> x, int key) {
        // searches for node with given key
        while (x != null && x.key != key) {
            // go through left if key is smaller
            if (key < x.key) {
                x = x.left;
            }
            // otherwise, go through/traverse right
            else {
                x = x.right;
            }
        }
        return x;   // return node if found.
    }

    protected int depth(Node<T> x) {
        // returns the depth of subtree rooted at x
        if (x == null) return 0;    // empty tree has 0 depth

        int leftDepth = depth(x.left);  // depth of left subtree
        int rightDepth = depth(x.right);    // depth of right subtree
        return Math.max(leftDepth, rightDepth) + 1; // max depth + 1
    }

    protected Node<T> minimum(Node<T> x) {
        // returns the node containing the smallest key in subtree
        while (x.left != null) {
            x = x.left;     // move left until smallest key is found
        }
        return x;
    }

    protected Node<T> maximum(Node<T> x) {
        // returns the node containing the largest key in the subtree
        while (x.right != null) {
            x = x.right;    // move right until largest key is found
        }
        return x;
    }

    protected Node<T> successor(Node<T> x) {
        // returns the node containing the next larger key (successor)
        if (x.right != null) {
            return minimum(x.right);    // successor is the smallest key in right subtree
        }
        Node<T> y = x.parent;
        while (y != null && x == y.right) {
            x = y;
            y = y.parent;
        }
        return y;   // return successor node
    }

    protected Node<T> predecessor(Node<T> x) {
        // returns the node containing the next smaller key (predecessor)
        if (x.left != null) {
            return maximum(x.left);     // predecessor is the largest key in the left subtree
        }
        Node<T> y = x.parent;
        while (y != null && x == y.left) {
            x = y;
            y = y.parent;
        }
        return y;   // return predecessor node
    }

    protected void delete(Node<T> z) {
        // from lecture slide explaining how to delete elements
        // if one or both children are null, connect parent of z with appropriate non-null child of z
        if (z.left == null) {
        transplant(z, z.right);
        }
        else if (z.right == null) {
        transplant(z, z.left);
        }
        else {
        Node<T> y = minimum(z.right);   // otherwise, find successor y
            if (y != z.right) {    // if y is not the right child yet, make it so
                transplant(y, y.right);     // cut out y from tree, reconnecting its only child
                y.right = z.right;      // insert y between z and z's right child
                y.right.parent = y;
            }
        transplant(z, y);   // now that successor y is right child of z, replace z with y
        y.left = z.left;    // connect remaining left child of z back to y
        y.left.parent = y;
        }
    }

    private void transplant(Node<T> u, Node<T> v) {
        // from lecture slide page 58
        // replaces a subtree as child of its parent w/ other subtree
        if (u.parent == null) {
            root = v;
        } else if (u == u.parent.left) {
            u.parent.left = v;
        } else {
            u.parent.right = v;
        }
        if (v != null) {
            v.parent = u.parent;
        }
    }
}