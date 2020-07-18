package notepad;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.StringTokenizer;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

/**
 *
 * @author killer
 */
public class Notepad extends JFrame {

    JTextArea mainarea;
    JMenuBar mbar;
    JMenu mnFile, mnEdit, mnFormat, mnHelp;
    JMenuItem itmNew, itmOpen, itmSave, itmSaveAs, 
            itmExit, itmCut, itmCopy, itmPaste;
    JFileChooser jc;
    String filename = null;
    String fileContent = null;

    public Notepad() {
        initComponent();
        itmSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                save();
            }
        });
        itmSaveAs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAs();
            }
        });
        itmOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                open();
            }
        });
        itmNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openNew();
            }
        });
        itmExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    private void initComponent() {
        jc = new JFileChooser(".");
        mainarea = new JTextArea();
        getContentPane().add(mainarea);
        getContentPane().add(new JScrollPane(mainarea), BorderLayout.CENTER);
        setTitle("Untitled document");
        setSize(800, 600);

        //menu bar
        mbar = new JMenuBar();

        //menu
        mnFile = new JMenu("File");
        mnFormat = new JMenu("Format");
        mnEdit = new JMenu("Edit");
        mnHelp = new JMenu("Help");

        //add icon
        ImageIcon newIcon = new ImageIcon(getClass().getResource("/img/new.png"));
        ImageIcon openIcon = new ImageIcon(getClass().getResource("/img/open.png"));
        ImageIcon saveIcon = new ImageIcon(getClass().getResource("/img/save.png"));
        ImageIcon exitIcon = new ImageIcon(getClass().getResource("/img/exit.png"));
        ImageIcon cutIcon = new ImageIcon(getClass().getResource("/img/cut.png"));
        ImageIcon copyIcon = new ImageIcon(getClass().getResource("/img/copy.png"));
        ImageIcon pasteIcon = new ImageIcon(getClass().getResource("/img/paste.png"));

        //Menu Item
        itmNew = new JMenuItem("New", newIcon);
        itmOpen = new JMenuItem("Open", openIcon);
        itmSave = new JMenuItem("Save", saveIcon);
        itmSaveAs = new JMenuItem("Save As", saveIcon);
        itmExit = new JMenuItem("Exit", exitIcon);
        itmCut = new JMenuItem("Cut", cutIcon);
        itmCopy = new JMenuItem("Copy", copyIcon);
        itmPaste = new JMenuItem("Paste", pasteIcon);

        //add shortcut to menu item
        itmNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        itmOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        itmSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        itmSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));

        //add menu item
        mnFile.add(itmNew);
        mnFile.add(itmOpen);
        mnFile.add(itmSave);
        mnFile.add(itmSaveAs);
        mnFile.add(itmExit);
        
        mnEdit.add(itmCut);
        mnEdit.add(itmCopy);
        mnEdit.add(itmPaste);

        //add menu to menu bar
        mbar.add(mnFile);
        mbar.add(mnEdit);
        mbar.add(mnFormat);
        mbar.add(mnHelp);

        //add menu bar to frame
        setJMenuBar(mbar);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void save() {

        PrintWriter fout = null;
        try {
            if (filename == null) {
                saveAs();
            } else {
                fout = new PrintWriter(new FileWriter(filename));
                String s = mainarea.getText();
                StringTokenizer st = new StringTokenizer(s, System.getProperty("line.separator"));
                while (st.hasMoreElements()) {
                    fout.println(st.nextToken());
                }
                fileContent = mainarea.getText();
                JOptionPane.showMessageDialog(rootPane, "File saved");
            }

        } catch (IOException e) {
        } finally {
            if (fout != null) {
                fout.close();
            }
        }
    }

    private void saveAs() {
        PrintWriter fout = null;
        int retval = -1;
        try {
            retval = jc.showSaveDialog(this);
            if (retval == JFileChooser.APPROVE_OPTION) {
                fout = new PrintWriter(new FileWriter(jc.getSelectedFile()));
                String s = mainarea.getText();
                StringTokenizer st = new StringTokenizer(s, System.getProperty("line.separator"));
                while (st.hasMoreElements()) {
                    fout.println(st.nextToken());
                }
                fileContent = mainarea.getText();
                filename = jc.getSelectedFile().getName();
                setTitle(filename);
                JOptionPane.showMessageDialog(rootPane, "File saved");

            }
        } catch (IOException e) {
        } finally {
            fout.close();
        }
    }

    private void open() {
        if (!mainarea.getText().equals("") && !mainarea.getText().equals(fileContent)) {
            if (filename == null) {
                int option = JOptionPane.showConfirmDialog(rootPane, "Do you want to save changes?");
                if (option == 0) {
                    saveAs();
                    openFile();
                } else if (option == 2) {
                } else {
                    openFile();
                }
            } else {
                int option = JOptionPane.showConfirmDialog(rootPane, "Do you want to save changes?");
                if (option == 0) {
                    save();
                    openFile();
                } else if (option == 2) {
                } else {
                    openFile();
                }
            }

        } else {
            openFile();
        }

    }

    private void openFile() {
        try {
            int retval = jc.showOpenDialog(this);
            if (retval == JFileChooser.APPROVE_OPTION) {
                mainarea.setText(null);
                Reader in = new FileReader(jc.getSelectedFile());
                filename = jc.getSelectedFile().getName();
                setTitle(filename);
                char[] buff = new char[10000000];
                int nch;
                while ((nch = in.read(buff, 0, buff.length)) != -1) {
                    mainarea.append(new String(buff, 0, nch));
                }
                fileContent = mainarea.getText();
            }
        } catch (Exception e) {
        }
    }

    private void openNew() {
        if (!mainarea.getText().equals("") && !mainarea.getText().equals(fileContent)) {
            if (filename == null) {
                int option = JOptionPane.showConfirmDialog(rootPane, "Do you want to save changes?");
                if (option == 0) {
                    saveAs();
                    clear();
                } else if (option == 2) {
                } else {
                    clear();
                }
            } else {
                int option = JOptionPane.showConfirmDialog(rootPane, "Do you want to save changes?");
                if (option == 0) {
                    save();
                    clear();
                } else if (option == 2) {
                } else {
                    clear();
                }
            }
        } else {
            clear();
        }
    }

    private void clear() {
        mainarea.setText(null);
        setTitle("Untitled document");
        filename = null;
        fileContent = null;
    }

    public static void main(String[] args) {
        Notepad notepad = new Notepad();
    }

}
