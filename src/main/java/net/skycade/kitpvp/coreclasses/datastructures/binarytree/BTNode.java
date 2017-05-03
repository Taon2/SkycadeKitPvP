package net.skycade.kitpvp.coreclasses.datastructures.binarytree;

public class BTNode<K, V> {

    private K key;
    private V value;

    private BTNode<K, V> left;
    private  BTNode<K, V> right;
    private  BTNode<K, V> parent;

    public BTNode(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public BTNode<K, V> getLeft() {
        return left;
    }

    public void setLeft(BTNode<K, V> left) {
        this.left = left;
    }

    public BTNode<K, V> getRight() {
        return right;
    }

    public void setRight(BTNode<K, V> right) {
        this.right = right;
    }

    public BTNode<K, V> getParent() {
        return parent;
    }

    public void setParent(BTNode<K, V> parent) {
        this.parent = parent;
    }

    public boolean isLeaf() {
        return left == null && right == null;
    }

}
