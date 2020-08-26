package notepad;

import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

/**
 *
 * @author killer
 */
public class Notepad extends JFrame {

    JTextArea mainarea;
    JMenuBar mbar;
    JMenu mnFile, mnEdit, mnFormat;
    JMenuItem itmNew, itmOpen, itmSave, itmSaveAs, 
            itmExit, itmCut, itmCopy, itmPaste, itmTextColor;
    JCheckBoxMenuItem wordWrap;
    JFileChooser jc;
    String filename = null;
    String fileContent = null;
    UndoManager undo;
    UndoAction undoAction;
    RedoAction redoAction;
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
        itmCut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainarea.cut();
            }
        });
        itmCopy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainarea.copy();
            }
        });
        itmPaste.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainarea.paste();
            }
        });
        itmExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        itmTextColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color c = JColorChooser.showDialog(rootPane, "Choose Text Color", Color.BLACK);
                mainarea.setForeground(c);
            }
        });
        
        wordWrap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(wordWrap.isSelected()){
                    mainarea.setLineWrap(true);
                    mainarea.setWrapStyleWord(true);
                }else{
                    mainarea.setLineWrap(false);
                    mainarea.setWrapStyleWord(false);
                }
            }
        });
        mainarea.getDocument().addUndoableEditListener(new UndoableEditListener(){
            
            @Override
            public void undoableEditHappened(UndoableEditEvent e){
                undo.addEdit(e.getEdit());
                undoAction.update();
                redoAction.update();
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
        undo = new UndoManager();
        ImageIcon undoIcon = new ImageIcon(getClass().getResource("/img/undo.png"));
        ImageIcon redoIcon = new ImageIcon(getClass().getResource("/img/redo.png"));
        undoAction = new UndoAction(undoIcon);
        redoAction = new RedoAction(redoIcon);
        //menu bar
        mbar = new JMenuBar();

        //menu
        mnFile = new JMenu("File");
        mnFormat = new JMenu("Format");
        mnEdit = new JMenu("Edit");

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
        wordWrap = new JCheckBoxMenuItem("Word Wrap");
        itmTextColor = new JMenuItem("Text Color");

        //itmhelp = new JMenuItem('In');
        //add shortcut to menu item
        itmNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        itmOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        itmSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        itmSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
        itmCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        itmPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
        itmCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));

        //add menu item
        mnFile.add(itmNew);
        mnFile.add(itmOpen);
        mnFile.add(itmSave);
        mnFile.add(itmSaveAs);
        mnFile.addSeparator();
        mnFile.add(itmExit);
        
        mnEdit.add(undoAction);
        mnEdit.add(redoAction);
        mnEdit.add(itmCut);
        mnEdit.add(itmCopy);
        mnEdit.add(itmPaste);
        
        mnFormat.add(wordWrap);
        mnFormat.add(itmTextColor);

        //add menu to menu bar
        mbar.add(mnFile);
        mbar.add(mnEdit);
        mbar.add(mnFormat);

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

    class UndoAction extends AbstractAction{
        public UndoAction(ImageIcon undoIcon){
            super("Undo", undoIcon);
            setEnabled(false);
        }
        @Override
        public void actionPerformed(ActionEvent e){
            try {
                undo.undo();
            } catch (CannotUndoException ex) {
                ex.printStackTrace();
            }
            update();
            redoAction.update();
        }

        protected void update() {
            if(undo.canUndo()){
                setEnabled(true);
                putValue(Action.NAME, "Undo");
            }else{
                setEnabled(false);
                putValue(Action.NAME, "Undo");
            }
        }
    }
    class RedoAction extends AbstractAction{
        public RedoAction(ImageIcon redoIcon){
            super("Redo", redoIcon);
            setEnabled(false);
        }
        @Override
        public void actionPerformed(ActionEvent e){
            try {
                undo.redo();
            } catch (CannotRedoException ex) {
                ex.printStackTrace();
            }
            update();
            undoAction.update();
        }

        protected void update() {
            if(undo.canRedo()){
                setEnabled(true);
                putValue(Action.NAME, "Redo");
            }else{
                setEnabled(false);
                putValue(Action.NAME, "Redo");
            }
        }
    }
    public static void main(String[] args) {
        Notepad notepad = new Notepad();
    }

}
