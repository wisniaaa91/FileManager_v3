import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;


class GUI {

    private String APP_TITTLE = "APP";
    private JFrame frame;

    private JMenuBar jMenuBar;
    private JMenu fileMenu;
    private JMenuItem newFileMenuItem;
    private JMenuItem deleteFileMenuItem;
    private JMenuItem newFolderMenuItem;

    private JPanel mainPanel;
    private JPanel directionPanel;
    private JPanel rightPanel;

    private JTextField directionField;

    private JButton goButton;
    private JButton upButton;

    private JList mainList;
    private JScrollPane listScrollPane;

    private JPopupMenu rightClickPopupMenu;

    private JDialog newDialog;

    void mainFrameExecute() {
        /** CREATE FRAME */
        frame = new JFrame(APP_TITTLE);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        /** CREATE MENU BAR */
        jMenuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        newFileMenuItem = new JMenuItem("New file");
        newFileMenuItem.addActionListener(new NewMenuItemActionListener());
        fileMenu.add(newFileMenuItem);
        deleteFileMenuItem = new JMenuItem("Delete");
        deleteFileMenuItem.addActionListener(new DeleteMenuItemActionListener());
        fileMenu.add(deleteFileMenuItem);
        newFolderMenuItem = new JMenuItem("New folder");
        newFolderMenuItem.addActionListener();
        jMenuBar.add(fileMenu);

        /** CREATE MAIN PANEL */
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        /** CREATE DIRECTION TEXT FIELD */
        directionField = new JTextField("C:\\");
        directionField.setPreferredSize(new Dimension(600, 24));
        directionField.addKeyListener(new PressEnterKey());

        /** CREATE GO BUTTON */
        goButton = new JButton("Go");
        goButton.addActionListener(new GoButtonActionListener());

        /** CREATE UP BUTTON */
        upButton = new JButton("Up");
        upButton.addActionListener(new UpButtonActionListener());

        /** CREATE RIGHT PANEL */
        rightPanel = new JPanel();

        /** CREATE DIRECTION PANEL */
        directionPanel = new JPanel();
        directionPanel.add(upButton);
        directionPanel.add(directionField);
        directionPanel.add(goButton);

        /** CREATE J POPUP MENU */
        rightClickPopupMenu = new JPopupMenu();
        rightClickPopupMenu.add(newFileMenuItem);
        rightClickPopupMenu.add(deleteFileMenuItem);

        /** CREATE JLIST */
        mainList = new JList();
        mainList.setCellRenderer(new FileListCellRenderer());
        mainList.addMouseListener(new ListMouseListener());
        listScrollPane = new JScrollPane(mainList);

        /** ADD ELEMENTS TO MAIN PANEL */
        mainPanel.add(directionPanel, BorderLayout.NORTH);
        mainPanel.add(rightPanel, BorderLayout.EAST);
        mainPanel.add(listScrollPane, BorderLayout.CENTER);

        /** ADD ELEMENTS TO MAINFRAME */
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.setJMenuBar(jMenuBar);

        /** SHOW MAINFRAME */
        frame.setVisible(true);
    }

    /**
     * METHOD FOR GUI
     */
    void showListOfFile() {
        File[] files = new File(directionField.getText()).listFiles();
        mainList.setListData(files);
    }

    void createNewFile() {
        newDialog = new JDialog();
        String newFilename = JOptionPane.showInputDialog(frame, "Input file name");
        try {
            File tempFile = new File(directionField.getText() + "\\" + newFilename);
            if (tempFile.createNewFile()) {
                System.out.println("File created.");
            } else {
                System.out.println("File doesn't created.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void createNewFolder() {
        newDialog = new JDialog();
        String newFolderName = JOptionPane.showInputDialog(frame, "input folder name");

        File tempFolder = new File(directionField.getText() + "\\" + newFolderName);
        if (tempFolder.mkdir()) {
            System.out.println("Folder created.");
        } else {
            System.out.println("Folder doesn't created. ");
        }


    }

    void deleteFile() {
        if (mainList.getSelectedValuesList().size() > 1) {
            ArrayList<Object> tempFilesList = new ArrayList<>(mainList.getSelectedValuesList());
            for (Object file : tempFilesList) {
                File tempFile = new File(file.toString());
                tempFile.delete();
                System.out.println(tempFile.getName() + " file deleted.");
            }
        } else if (mainList.getSelectedValuesList().size() == 1) {
            File tempFile = new File(mainList.getSelectedValue().toString());
            tempFile.delete();
            System.out.println(tempFile.getName() + " file deleted.");
        }
    }

    void copyFile(File sourceFile, File destinationFile) {
        try {
            File source = new File(sourceFile.getPath());
            File destination = new File(destinationFile.getPath());
            InputStream inStream = new FileInputStream(source);
            OutputStream outStream = new FileOutputStream(destination);

            byte[] buffer = new byte[1024];

            int length;
            while ((length = inStream.read(buffer)) > 0) {
                outStream.write(buffer, 0, length);
            }
            inStream.close();
            outStream.close();
            System.out.println("File is copied successful!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * SUBCLASS FOR SUPPORT
     */

    class NewMenuItemActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            createNewFile();
            showListOfFile();
        }
    }

    class DeleteMenuItemActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            deleteFile();
            showListOfFile();
        }
    }

    class PressEnterKey implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
        }

        @Override
        public void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_ENTER) {
                showListOfFile();
            }
        }
    }

    class GoButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            showListOfFile();
        }
    }

    class UpButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Path tempDir = Paths.get(directionField.getText());
                directionField.setText(tempDir.getParent().toString());
                File[] files = new File(directionField.getText()).listFiles();
                mainList.setListData(files);
            } catch (NullPointerException e1) {
                System.out.println("It is impossible.");
            }
        }
    }

    class ListMouseListener implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
                String tempDir = mainList.getSelectedValue().toString();
                directionField.setText(tempDir);
                File[] files = new File(directionField.getText()).listFiles();
                mainList.setListData(files);
            }


        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e) && e.getClickCount() == 1) {
                if (!mainList.isSelectionEmpty()) {
                    if (e.isPopupTrigger()) {
                        rightClickPopupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }


}
