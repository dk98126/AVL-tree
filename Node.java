package avlMap;

import java.awt.*;

/**
 * Узел дерева
 *
 * @param <K>   Ключ
 * @param <V>   Значение
 */
public class Node<K extends Comparable<K>, V> implements Comparable<Node> {
    K key;
    V value;
    Node<K, V> p;     //parent
    Node<K, V> l;     //left
    Node<K, V> r;     //right;
    int height = 0;

    //для отрисовки
    Rectangle nodeRectangle;

    private Node(K key, V value, Node<K, V> parent) {
        this.key = key;
        this.value = value;
        p = parent;
        l = new Node<>(this);
        r = new Node<>(this);
    }

    Node(K key, V value) {
        this(key, value, null);
    }

    Node(Node<K, V> parent) {
        this.p = parent;
        key = null;
        value = null;
        l = null;
        r = null;
    }

    /**
     * Левый ли текущий узел относительно родителя
     *
     * @param parent    родитель
     * @return  если левый, то истина, иначе ложь
     */
    boolean isLeft(Node<K, V> parent) {
        if (parent.l.key == null) return false;
        return parent.l.key.compareTo(this.key) == 0;
    }

    //для отрисовки
    void setNodeRectangle(Rectangle nodeRectangle) {
        this.nodeRectangle = nodeRectangle;
    }

    //для отрисовки
    Rectangle getNodeRectangle() {
        return nodeRectangle;
    }

    @Override
    public int compareTo(Node node) {
        return this.key.compareTo((K) node.key);
    }
}
