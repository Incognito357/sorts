package com.incognito.tools.sorts.ui;

import com.incognito.tools.sorts.algorithms.BubbleSort;
import com.incognito.tools.sorts.algorithms.InsertionSort;
import com.incognito.tools.sorts.algorithms.MergeSort;
import com.incognito.tools.sorts.algorithms.QuickSort;
import com.incognito.tools.sorts.algorithms.SelectionSort;
import com.incognito.tools.sorts.algorithms.ShaveSort;
import com.incognito.tools.sorts.algorithms.Sort;
import com.incognito.tools.sorts.ui.components.GraphicsPanel;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class MainWindow {
    private JPanel root;
    private JComboBox<Sort> cmbAlgorithm;
    private JButton btnNext;
    private JLabel lblAlgorithm;
    private GraphicsPanel pnlGraphics;
    private JButton btnPlay;
    private JSpinner numSpeed;

    private SwingWorker<Boolean, Void> auto;
    private static final List<Integer> ints = new Random().ints(1800, 1, 1100).boxed().collect(Collectors.toList());
    private static final List<Sort> sorts = Arrays.asList(
            new BubbleSort(ints),
            new SelectionSort(ints),
            new InsertionSort(ints),
            new MergeSort(ints),
            new QuickSort(ints),
            new ShaveSort(ints)
    );

    public MainWindow() {
        JFrame frame = new JFrame("MainWindow");
        $$$setupUI$$$();
        frame.setContentPane(root);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        btnNext.addActionListener(e -> {
            pnlGraphics.getSort().stepNext();
            pnlGraphics.repaint();
        });
        btnPlay.addActionListener(e -> {
            if (pnlGraphics.getSort().isDone()) {
                pnlGraphics.getSort().reset();
                pnlGraphics.repaint();
                btnPlay.setText("Play");
                cmbAlgorithm.setEnabled(true);
            } else if (pnlGraphics.isPlaying()) {
                pnlGraphics.setPlaying(false);
                btnPlay.setText("Play");
                cmbAlgorithm.setEnabled(true);
                auto.cancel(true);
            } else {
                pnlGraphics.setPlaying(true);
                btnPlay.setText("Pause");
                cmbAlgorithm.setEnabled(false);
                auto = new SwingWorker<Boolean, Void>(){
                    @Override
                    protected void done() {
                        btnPlay.setText(isCancelled() ? "Resume": "Reset");
                        cmbAlgorithm.setEnabled(true);
                        pnlGraphics.setPlaying(false);
                    }

                    @Override
                    protected Boolean doInBackground() throws Exception {
                        while (!pnlGraphics.getSort().isDone()) {
                            if (Thread.currentThread().isInterrupted()) {
                                Thread.currentThread().interrupt();
                                return false;
                            }
                            int delay = (Integer) numSpeed.getValue();
                            long wait = System.nanoTime() + (delay * 10000);
                            while (wait > System.nanoTime()){
                                if (Thread.currentThread().isInterrupted()){
                                    Thread.currentThread().interrupt();
                                    return false;
                                }
                            }
                            pnlGraphics.getSort().stepNext();
                            pnlGraphics.repaint();
                        }
                        return true;
                    }
                };
                auto.execute();
            }
        });
        cmbAlgorithm.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                pnlGraphics.setSort(sorts.get(cmbAlgorithm.getSelectedIndex()));
                pnlGraphics.getSort().reset();
                pnlGraphics.repaint();
                btnPlay.setText("Play");
            }
        });
    }

    private void createUIComponents() {
        cmbAlgorithm = new JComboBox<>();
        DefaultComboBoxModel<Sort> model = new DefaultComboBoxModel<>();
        for (Sort sort : sorts) {
            model.addElement(sort);
        }
        cmbAlgorithm.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                value = ((Sort) value).getName();
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        cmbAlgorithm.setModel(model);
        pnlGraphics = new GraphicsPanel();
        pnlGraphics.setSort(sorts.get(0));

        numSpeed = new JSpinner(new SpinnerNumberModel(250, 0, 100000, 1));
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        root = new JPanel();
        root.setLayout(new GridLayoutManager(2, 5, new Insets(0, 0, 0, 0), -1, -1));
        root.setPreferredSize(new Dimension(800, 600));
        root.add(cmbAlgorithm, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblAlgorithm = new JLabel();
        lblAlgorithm.setText("Algorithm");
        root.add(lblAlgorithm, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnNext = new JButton();
        btnNext.setText("Step");
        root.add(btnNext, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pnlGraphics.setDoubleBuffered(false);
        root.add(pnlGraphics, new GridConstraints(1, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(100, 50), new Dimension(200, 200), new Dimension(10000, 10000), 0, false));
        btnPlay = new JButton();
        btnPlay.setText("Play");
        root.add(btnPlay, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        root.add(numSpeed, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(60, -1), new Dimension(60, -1), new Dimension(100, -1), 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return root;
    }
}
