package me.bukkit.kitpvp.coreclasses.datastructures.binarytree;

import me.bukkit.kitpvp.coreclasses.algorithms.SequentialSearch;

import java.util.function.Consumer;

/*
    Custom coded by WhiteWalker72
 */

public class BinaryTree<K extends Comparable<K>, V> {

    private BTNode<K, V> root;
    private int size = 0;
    private int inOrderArrayCounter;
    private final boolean lowHigh;

    public BinaryTree() {
        this.lowHigh = true;
    }

    public BinaryTree(boolean lowHigh) {
        this.lowHigh = lowHigh;
    }

    public BinaryTree(BTNode<K, V>[] startArray) {
        this(true, startArray);
    }

    public BinaryTree(boolean lowHigh, BTNode<K, V>[] startArray) {
        this.lowHigh = lowHigh;

        BTNode<K, V> median = findMedian(startArray);
        int medianIndex = SequentialSearch.searchIndex(startArray, median);
        if (medianIndex > 0) {
            root = median;
            size++;
            startArray[medianIndex] = null;
        }

        for (BTNode<K, V> node : startArray)
            add(node);
    }


    public void add(K key, V value) {
        add(new BTNode<>(key, value));
    }

    public void add(BTNode<K, V> node) {
        if(node == null)
            return;
        if (root == null) {
            root = node;
            size++;
            return;
        }

        BTNode<K, V> current = root;
        BTNode<K, V> previous = null;
        boolean useLeft = false;

        while (current != null) {
            useLeft = lowHigh ? node.getKey().compareTo(current.getKey()) < 0 : node.getKey().compareTo(current.getKey()) > 0;
            previous = current;
            current = useLeft ? current.getLeft() : current.getRight();
        }

        if (useLeft)
            previous.setLeft(node);
        else
            previous.setRight(node);

        size++;
    }


    public void printPreOrder() {
        printPreOrder(root);
    }

    private void printPreOrder(BTNode<K, V> node) {
        if(node == null)
            return;
        System.out.print(node.getKey() + ", ");
        printPreOrder(node.getLeft());
        printPreOrder(node.getRight());
    }


    public void printInorder(){
        printInOrderRec(root);
    }

    private void printInOrderRec(BTNode<K, V> node){
        if (node == null)
            return;
        printInOrderRec(node.getLeft());
        System.out.print(node.getKey() + ", ");
        printInOrderRec(node.getRight());
    }

    public void forEach(Consumer<? super BTNode> action) {
        doForEachInOrder(action, root);
    }

    private void doForEachInOrder(Consumer<? super BTNode> action, BTNode<K, V> node) {
        if (node == null)
            return;
        doForEachInOrder(action, node.getLeft());
        action.accept(node);
        doForEachInOrder(action, node.getRight());
    }

    public BTNode<K, V>[] getInOrderArray() {
        BTNode<K, V>[] array = new BTNode[size];
        inOrderArrayCounter = 0;
        forEach(btNode -> array[inOrderArrayCounter++] = btNode);
        return array;
    }

    private BTNode<K, V> traversal(K key, BTNode<K, V> node) {
        if (node == null)
            return null;
        if (key.compareTo(node.getKey()) == 0)
            return node;
        else if (key.compareTo(node.getKey()) < 0)
            return traversal(key, node.getLeft());
        else
            return traversal(key, node.getRight());
    }

    public V find(K key) {
        BTNode<K, V> node = traversal(key, root);
        if (node == null)
            throw new IllegalArgumentException("This key isn't in the binary tree");
        else
            return node.getValue();
    }

    public void remove(K key) {
        BTNode<K, V> removeNode = traversal(key, root);
        if (removeNode == null)
            return;
        delete(removeNode);
        size--;

    }

    private void delete(BTNode<K, V> node) {
        if (node.isLeaf()) { // No children
            if (node.getParent().equals(node))
                node.getParent().setLeft(null);
            else
                node.getParent().setRight(null);
        }
        else if (node.getRight() == null) { // Child left
            if (node.getParent().getLeft().equals(node))
                node.getParent().setLeft(node.getLeft());
            else
                node.getParent().setRight(node.getLeft());
        }
        else if (node.getLeft() == null) { // Child right
            if (node.getParent().getLeft() == null)
                node.getParent().setLeft(node.getRight());
            else
                node.getParent().setRight(node.getRight());
        }
        else { // Has two children
            BTNode<K, V> maxRightNode = node.getLeft();
            while (maxRightNode.getRight() != null)
                maxRightNode = maxRightNode.getRight();
            node.setKey(maxRightNode.getKey());
            node.setValue(maxRightNode.getValue());
            delete(maxRightNode);
        }

    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return toString(root);
    }


    private String toString(BTNode<K, V> node) {
        String builder = toString(node.getLeft()) +
                toString(node.getRight()) +
                "(" + node.getKey().toString() + ", " + node.getValue().toString() + ") ";
        if (node == null)
            return "";
        return builder;
    }

    private BTNode findMedian(BTNode<K, V>[] array) {
        sort(array);
        return array[array.length/2];
    }

    private void sort(BTNode<K, V>[] array) {
        BTNode<K, V> temp;
        for (int i = 1; i < array.length; i++) {
            for (int j = i; j > 0; j--) {
                if (array[j].getKey().compareTo(array[j - 1].getKey()) < 0) {
                    temp = array[j];
                    array[j] = array[j - 1];
                    array[j - 1] = temp;
                }
            }
        }
    }

}
