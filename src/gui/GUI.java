package gui;

import java.io.File;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import controller.Controller;

/**
 * Main GUI class that initializes the user interface.
 * @author Idicious
 *
 */
public class GUI extends JFrame {

	private static final long serialVersionUID = 2941318999657277463L;
	
	private Container contents; 
	private JFileChooser fileChooser; 
	private JTextArea textArea;
	private JScrollPane scrollPane;

	/**
	 * Initializes the GUI of the application
	 */
	public GUI() {
		
		this.setLookAndFeel();
			
		this.setTitle("Filewalker");
		this.setMinimumSize(new Dimension(250, 100));
		
		this.setJMenuBar(makeMenu());
		
		this.contents = getContentPane();
		this.contents.setLayout(new BorderLayout());
		
		this.makeContentWindow();
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.pack();
		this.setLocationRelativeTo(null); // Centers application on screen
		this.setVisible(true);
	}
	
	/**
	 * Prints given string to text area and appends it with \n, then if 
	 * toEndOfLine is true sets the Caret to the end of the text.
	 * @param text String to display
	 * @param toEndOfLine Whether to move Caret to end of line
	 */
	public void displayLine(String text, boolean toEndOfLine) {
		textArea.append(text + "\n");
		
		if(toEndOfLine) {
			textArea.setCaretPosition(textArea.getDocument().getLength());
		}
	}

	/**
	 * Makes the main contentwindow area
	 */
	private void makeContentWindow() {
		this.initializeFileChooser();
		
		JButton open = new JButton("Choose file"); // Button that opens file chooser
		open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int returnValue = fileChooser.showOpenDialog(null);
		        if (returnValue == JFileChooser.APPROVE_OPTION) {
		          File selectedFile = fileChooser.getSelectedFile();
		          Controller.getInstance().choosePath(selectedFile.toPath());
		        } 
			}
		});
		

		this.textArea = new JTextArea("Choose a file or directory. \n\n", 10, 30);
		this.textArea.setEditable(false);
		this.textArea.setWrapStyleWord(true);
		
		this.scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		this.contents.add(open, BorderLayout.NORTH);
		this.contents.add(scrollPane, BorderLayout.CENTER);
	}
	
	/**
	 * Creates and sets all the options for the FileChooser
	 */
	private void initializeFileChooser()
	{
		this.fileChooser = new JFileChooser();
		this.fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	}

	/**
	 * Gets the current OS and sets the look and feel of application to that.
	 */
	private void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			/* Default look and feel */
		}
	}

	/**
	 * Makes the menubar of the application
	 * @return
	 */
	private JMenuBar makeMenu() {
		JMenuBar menuBar = new JMenuBar();
    	
    	JMenu file = new JMenu("File");
    	menuBar.add(file);
	    	JMenuItem quit = new JMenuItem("Quit");
	    	quit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					System.exit(EXIT_ON_CLOSE);
				}
	    	});
	    	file.add(quit);
    	
    	JMenu help = new JMenu("Help");
    	menuBar.add(help);
			JMenuItem about = new JMenuItem("About");
			about.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) { 
					openInfoBox();
				}
			});
			help.add(about);
    	
		return menuBar;
	}

	protected void openInfoBox() {
		String title = "Info";
		String message =  "This program iterates over the filesystem from the chosen directory or file downwards. \n";
			   message += "Every file it finds it will attempt to copy to a destination directory and add an entry \n";
			   message += "to the database containing the copied path and extension group. \n";
			    
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
	}
}
