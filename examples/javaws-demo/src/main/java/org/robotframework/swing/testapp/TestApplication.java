package org.robotframework.swing.testapp;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestApplication {
    private JPanel panel;
    private JFrame frame;

    public static void main(String[] args) {
        new TestApplication().runTestApplication();
    }

    public void runTestApplication() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createFrame();
                createMainPanel();
                addMenuBar();
                addComponentsToMainPanel();
                addMainPanelToFrame();
                showGUI();
            }
        });
        
        startTestLibrary();
    }

    private void showGUI() {
        frame.pack();
        frame.setVisible(true);
    }

    private void addMainPanelToFrame() {
        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(panel, BorderLayout.CENTER);
    }

    private void createMainPanel() {
        panel = new JPanel();
        panel.setName("Main Panel");
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.CENTER);
        panel.setLayout(flowLayout);
    }

    private void addMenuBar() {
        frame.setJMenuBar(new MyMenuBar());
    }

    private void createFrame() {
        frame = new JFrame("Test App") {
            public Dimension getPreferredSize() {
                return new Dimension(500, 500);
            }
        };
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void addComponentsToMainPanel() {
        panel.add(new SystemExitButton());
    }
    
    private void startTestLibrary() {
        new ClassPathXmlApplicationContext("org/robotframework/rmiServiceContext.xml");
    }
}
