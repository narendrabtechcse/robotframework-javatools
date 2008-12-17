package org.robotframework.example;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Application {
	public static void main(String[] args) {
	    startTestLibrary();
		startApplicationIfCredentialsAreOK();
	}

    private static void startApplicationIfCredentialsAreOK() {
        String password = JOptionPane.showInputDialog("Password please (hint: it's 'robot')");
		
		if ("robot".equals(password)) {
			startApplication();
		} else {
			System.exit(1);
		}
    }

    private static void startApplication() {
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	JFrame frame = new JFrame("Main Application");
            	frame.setPreferredSize(new Dimension(300, 300));
            	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            	
            	frame.getContentPane().setLayout(new FlowLayout());
            	frame.getContentPane().add(new JPanel() {{
            	    setBackground(Color.WHITE);
            	    add(new JButton("exit") {{
                        addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                System.exit(0);
                            }
                        });
                    }});  
            	}});
            	
            	frame.pack();
            	frame.setVisible(true);
            }
        });
	}
    
    private static void startTestLibrary() {
        new ClassPathXmlApplicationContext("org/robotframework/example/rmiServiceContext.xml");
    }
}
