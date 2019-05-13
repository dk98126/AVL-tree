package avlMap;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Данный класс представляет контейнер map на основе AVL дерева
 * Можно хранить любые ключи, в которых реализован метод compareTo
 * Можно хранить любые значения
 *
 * Также данный класс может отрисовывать сбалансированное дерево поиска средствами библиотек
 * Swing и AWT,
 * однако для этого ключи и значения должны реализовывать метод toString()
 *
 * В прямоугольнике узла ключ находится сверху, значение снизу
 *
 * @param <K> тип ключа
 * @param <V> тип значения
 * @author Даниил Краснов
 * @version 1.0
 */
public class Map<K extends Comparable<K>, V> extends JComponent {

    //настройки для графического отображения
    private static final int FONT_SIZE = 10;
    public static final Color FONT_COLOR = Color.BLACK;
    private static final Color NODE_COLOR = Color.WHITE;
    private static final Color CONNECTION_COLOR = Color.BLACK;
    private static final int WIDTH_DELIMITER = 5;
    private static final int HEIGHT_DELIMITER = 50;
    private static final int DEFAULT_WIDTH = 10000;
    private static final int DEFAULT_HEIGHT = 1080;

    private Node<K, V> root;

    public Map() {
        this.root = null;
    }

    /**
     * Добавить (заменить, в случае равного ключа) значение элемента по ключу
     *
     * @param key   ключ
     * @param value значение
     */
    public void put(K key, V value) {
        Node<K, V> newNode = new Node<>(key, value);
        if (root == null) {
            root = newNode;
        } else {
            Node<K, V> tmpNode = put(root, newNode);
            //посчитаем новую высоту для каждого узла
            ArrayList<Node<K, V>> allNodes = breadthFirstSearch();
            for (Node<K, V> node :
                    allNodes) {
                node.height = treeHeight(node) + 1;
            }
            //проверяем условия балансировки и при выполнении балансируем
            if (tmpNode != null) {
                while (tmpNode != root && !balance(tmpNode)) {
                    tmpNode = tmpNode.p;
                }
                balance(root);
            }
        }
    }

    //рекурсивная функия для public put
    private Node<K, V> put(Node<K, V> parent, Node<K, V> newNode) {
        if (parent.key == null) {
            parent.key = newNode.key;
            parent.value = newNode.value;
            parent.l = new Node<>(parent);
            parent.r = new Node<>(parent);
            return parent;
        } else if (newNode.compareTo(parent) > 0) {
            return put(parent.r, newNode);
        } else if (newNode.compareTo(parent) < 0) {
            return put(parent.l, newNode);
        } else {
            parent.value = newNode.value;
            return null;
        }
    }

    /**
     * Получение значения по ключу
     *
     * @param key ключ
     * @return знаение
     */
    public V get(K key) {
        return get(root, key);
    }

    //для рекурсивного высова из public get
    private V get(Node<K, V> node, K key) {
        if (node.key == null)
            return null;
        if (key.compareTo(node.key) > 0)
            return get(node.r, key);
        else if (key.compareTo(node.key) < 0)
            return get(node.l, key);
        else
            return node.value;
    }

    /**
     * Очистка дерева
     */
    public void clear() {
        root = null;
    }

    /**
     * Удаление элемента
     *
     * @param key ключ, по которому удалять
     * @return удалившееся значение
     */

    public V delete(K key) {
        Node<K, V> node = delete(root, key);
        //если не нашли
        if (node == null) return null;
        V previousValue = node.value;
        //удаление листа
        if (node.l.key == null && node.r.key == null) {
            node.key = null;
            node.height = 0;
        } else
            //удаление, когда один левый потомок
            if (node.l.key != null && node.r.key == null) {
                if (node != root) {
                    if (node.isLeft(node.p)) {
                        node.p.l = node.l;
                    } else
                        node.p.r = node.l;
                    node.l.p = node.p;
                } else {
                    root = node.l;
                    node.l.p = null;
                }
                node.key = null;
                node = node.l;
            } else
                //удаление, когда один правый потомок
                if (node.r.key != null && node.l.key == null) {
                    if (node != root) {
                        if (node.isLeft(node.p)) {
                            node.p.l = node.r;
                        } else
                            node.p.r = node.r;
                        node.r.p = node.p;
                    } else {
                        root = node.r;
                        node.r.p = null;
                    }
                    node.key = null;
                    node = node.r;
                } else
                    //если есть 2 потомка
                    if (node.r.key != null && node.l.key != null) {
                        //найдем наименьший из правого поддерева
                        Node<K, V> tmpNode = node.r;
                        while (tmpNode.l.key != null)
                            tmpNode = tmpNode.l;
                        K tmpKey = tmpNode.key;
                        V tmpValue = tmpNode.value;
                        tmpNode.key = null;
                        tmpNode.value = null;
                        tmpNode.height = 0;
                        node.key = tmpKey;
                        node.value = tmpValue;
                        node = tmpNode;
                    }
        ArrayList<Node<K, V>> allNodes = breadthFirstSearch();
        for (Node<K, V> node1 :
                allNodes) {
            node1.height = treeHeight(node1) + 1;
        }
        //проверяем условия балансировки и при выполнении балансируем
        if (node != root)
            node = node.p;
        while (node != root && !balance(node)) {
            node = node.p;
        }
        balance(root);
        return previousValue;
    }

    private Node<K, V> delete(Node<K, V> node, K key) {
        if (node.key == null)
            return null;
        if (key.compareTo(node.key) > 0)
            return delete(node.r, key);
        else if (key.compareTo(node.key) < 0)
            return delete(node.l, key);
        else
            return node;
    }

    /**
     * Балансировка
     *
     * @param node узел, относительно которого будут происходить вращения
     * @return истина, если балансировка была выполнена, иначе ложь
     */
    private boolean balance(Node<K, V> node) {
        //случай 1
        if (node.l.height - node.r.height == 2)
            if (node.l.l.height >= node.l.r.height) {
                rightRotation(node);
                return true;
            }
        //случай 2
        if (node.l.height - node.r.height == 2)
            if (node.l.l.height < node.l.r.height) {
                bigRightRotation(node);
                return true;
            }
        //случай 3
        if (node.r.height - node.l.height == 2)
            if (node.r.r.height >= node.r.l.height) {
                leftRotation(node);
                return true;
            }
        //случай 4
        if (node.r.height - node.l.height == 2)
            if (node.r.r.height < node.r.l.height) {
                bigLeftRotation(node);
                return true;
            }
        return false;
    }

    private void leftRotation(Node<K, V> node) {
        boolean isRoot = node.p == null;
        Node<K, V> A = node;
        Node<K, V> B = A.r;
        Node<K, V> C = B.l;

        if (!isRoot) {
            B.p = A.p;
            if (A.isLeft(A.p))
                A.p.l = B;
            else
                A.p.r = B;
        } else {
            B.p = null;
            root = B;
        }
        A.p = B;
        B.l = A;
        A.r = C;
        C.p = A;
    }

    private void bigLeftRotation(Node<K, V> node) {
        Node<K, V> A = node;
        Node<K, V> B = A.r;
        rightRotation(B);
        leftRotation(A);
    }

    private void rightRotation(Node<K, V> node) {
        boolean isRoot = node.p == null;
        Node<K, V> A = node;
        Node<K, V> B = A.l;
        Node<K, V> C = B.r;

        if (!isRoot) {
            B.p = A.p;
            if (A.isLeft(A.p))
                A.p.l = B;
            else
                A.p.r = B;
        } else {
            B.p = null;
            root = B;
        }
        A.p = B;
        B.r = A;
        A.l = C;
        C.p = A;
    }

    private void bigRightRotation(Node<K, V> node) {
        Node<K, V> A = node;
        Node<K, V> B = A.l;
        leftRotation(B);
        rightRotation(A);
    }

    //поиск в ширину
    private ArrayList<Node<K, V>> breadthFirstSearch() {
        LinkedList<Node<K, V>> queue = new LinkedList<>();
        ArrayList<Node<K, V>> visitedNodes = new ArrayList<>();

        if (root == null) return null;
        queue.add(root);
        while (queue.size() > 0) {
            Node<K, V> currentNode = queue.removeFirst();
            visitedNodes.add(currentNode);
            if (currentNode.l.key != null) {
                queue.add(currentNode.l);
            }
            if (currentNode.r.key != null) {
                queue.add(currentNode.r);
            }
        }

        return visitedNodes;
    }

    //возвращает высоту дерева
    private int treeHeight() {
        return treeHeight(root);
    }

    //для рекурсивного вызова из treeHeight()
    private int treeHeight(Node<K, V> node) {
        if (node == null) return 0;
        if (node.key == null)
            return -1;

        int leftH = treeHeight(node.l);
        int rightH = treeHeight(node.r);

        if (leftH > rightH)
            return leftH + 1;
        else
            return rightH + 1;
    }

    //==================================================================================================================
    //Далее идут методы для отрисовки дерева
    //==================================================================================================================

    @Override
    protected void paintComponent(Graphics g) {
        if (root == null || root.key == null) return;
        Graphics2D g2 = (Graphics2D) g;
        ArrayList<Node<K, V>> breadthNodes = breadthFirstSearch();
        Font nodeFont = new Font("Nimbus", Font.PLAIN, FONT_SIZE);
        g2.setFont(nodeFont);
        for (Node<K, V> node :
                breadthNodes) {
            setNodeRectangle(node, g2);
        }
        drawTree(breadthNodes, g2, getTreePixelWidth(g2));
    }

    private void drawNode(Node<K, V> node, Graphics2D g2, int x, int y) {
        Rectangle rectangle = node.getNodeRectangle();
        rectangle.setLocation(x, y);
        //задать цвет для узла
        g2.setColor(NODE_COLOR);
        g2.fill(rectangle);
        g2.setColor(CONNECTION_COLOR);
        g2.draw(rectangle);
        //задать цвет для шрифта
        g2.setColor(FONT_COLOR);
        g2.drawString(("" + node.key), x, y + (int) node.getNodeRectangle().getHeight() / 2);
        g2.drawString("" + node.value, x, y + (int) node.getNodeRectangle().getHeight());
        g2.drawLine(rectangle.x, rectangle.y + rectangle.height / 2, rectangle.x + rectangle.width, rectangle.y + rectangle.height / 2);
        g2.setColor(CONNECTION_COLOR);
    }

    private void setNodeRectangle(Node<K, V> node, Graphics2D g2) {
        FontRenderContext context = g2.getFontRenderContext();
        Rectangle2D keyBounds = g2.getFont().getStringBounds(("" + node.key), context);
        Rectangle2D valueBounds = g2.getFont().getStringBounds("" + node.value, context);
        Rectangle2D maxBounds = (keyBounds.getWidth() > valueBounds.getWidth()) ? keyBounds : valueBounds;
        Rectangle rectangle = new Rectangle((int) maxBounds.getWidth(), (int) maxBounds.getHeight() * 2);
        node.setNodeRectangle(rectangle);
    }

    private void drawTree(ArrayList<Node<K, V>> breadthNodes, Graphics2D g2, int treePixelWidth) {
        if (!breadthNodes.isEmpty()) {
            if (breadthNodes.size() == 1)
                drawNode(breadthNodes.get(0), g2, treePixelWidth / 2, 0);
            else {
                drawNode(breadthNodes.get(0), g2, treePixelWidth / 2, 0);
                Node<K, V> node;
                for (int i = 1; i < breadthNodes.size(); i++) {
                    node = breadthNodes.get(i);
                    if (node.isLeft(node.p)) {
                        Rectangle pRect = node.p.nodeRectangle;
                        if (node.p.p == null)
                            drawNode(node, g2, pRect.x / 2, pRect.y + pRect.height + HEIGHT_DELIMITER);
                        else {
                            Rectangle gRect = node.p.p.nodeRectangle;
                            if (!node.p.isLeft(node.p.p))
                                drawNode(node, g2, pRect.x - (pRect.x - gRect.x) / 2, pRect.y + pRect.height + HEIGHT_DELIMITER);
                            else
                                drawNode(node, g2, pRect.x - (gRect.x - pRect.x) / 2, pRect.y + pRect.height + HEIGHT_DELIMITER);
                        }
                    } else {
                        Rectangle pRect = node.p.nodeRectangle;
                        if (node.p.p == null)
                            drawNode(node, g2, pRect.x + pRect.x / 2, pRect.y + pRect.height + HEIGHT_DELIMITER);
                        else {
                            Rectangle gRect = node.p.p.nodeRectangle;
                            if (!node.p.isLeft(node.p.p))
                                drawNode(node, g2, pRect.x + (pRect.x - gRect.x) / 2, pRect.y + pRect.height + HEIGHT_DELIMITER);
                            else
                                drawNode(node, g2, pRect.x + (gRect.x - pRect.x) / 2, pRect.y + pRect.height + HEIGHT_DELIMITER);
                        }
                    }
                }

                for (Node<K, V> parent :
                        breadthNodes) {
                    if (parent.l.key != null) {
                        Node<K, V> son = parent.l;
                        g2.drawLine((int) parent.nodeRectangle.getCenterX(), (int) parent.nodeRectangle.getMaxY(), (int) son.nodeRectangle.getCenterX(), (int) son.nodeRectangle.getMinY());
                    }
                    if (parent.r.key != null) {
                        Node<K, V> son = parent.r;
                        g2.drawLine((int) parent.nodeRectangle.getCenterX(), (int) parent.nodeRectangle.getMaxY(), (int) son.nodeRectangle.getCenterX(), (int) son.nodeRectangle.getMinY());
                    }
                }
            }
        }
    }

    private int getTreePixelWidth(Graphics2D g2) {
        int widthPeriod = findNodeMaxWidth(g2) + WIDTH_DELIMITER;
        return widthPeriod * (int) Math.pow(2, treeHeight());
    }

    private int findNodeMaxWidth(Graphics2D g2) {
        ArrayList<Node<K, V>> breadthNodes = breadthFirstSearch();
        ArrayList<Integer> widths = new ArrayList<>();
        for (Node<K, V> node :
                breadthNodes) {
            setNodeRectangle(node, g2);
            widths.add(node.getNodeRectangle().width);
        }
        return Collections.max(widths);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
}
