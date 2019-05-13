package avlMap;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

/**
 * Тестировочный класс для Map
 */

public class MapTest {

    private static final int ANIMATION_SPEED = 1;

    public static void main(String[] args) {
        //создали map
        Map<Integer, String> map = new Map<>();

        //создания фрейма и добавление туда map
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();
        JScrollPane scrollPane = new JScrollPane(panel);
        frame.add(scrollPane);
        panel.add(map);
        frame.pack();
        frame.setTitle("Tree");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        panel.setBackground(Color.WHITE);

        //добавим в дерево 100 случайных элементов и покажем анимацию добавления
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            map.put(random.nextInt(100), "str" + random.nextInt(100));
            SwingUtilities.updateComponentTreeUI(panel);
            sleep(ANIMATION_SPEED);
        }

        //пауза на 5 скунд
        sleep(5000);

        //теперь с анимацией удалим 100 случайных элементов
        for (int i = 0; i < 100; i++) {
            map.delete(random.nextInt(100));
            SwingUtilities.updateComponentTreeUI(panel);
            sleep(ANIMATION_SPEED);
        }

        sleep(5000);

        map.clear();
        SwingUtilities.updateComponentTreeUI(panel);
    }

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


