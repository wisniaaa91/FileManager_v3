import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static javax.swing.JOptionPane.YES_NO_OPTION;

class GUI {

    private String APP_TITTLE = "APP";
    private JFrame frame;

    private Desktop desktop;

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

    private JTable table;
    private JScrollPane listScrollPane;
    private MyFileTableModel fileTableModel;
    private boolean cellSizesSet = false;
    private int rowIconPadding = 6;

    private JMenuItem newFilePopupMenuItem;
    private JMenuItem newFolderPopupMenuItem;
    private JMenuItem deleteFilePopupMenuItem;

    private JPopupMenu rightClickPopupMenu;

    private JDialog newDialog;

    void mainFrameExecute() {
        /* DESKTOP */
        desktop = Desktop.getDesktop();

        /* CREATE FRAME */
        frame = new JFrame(APP_TITTLE);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        /* CREATE MENU BAR */
        jMenuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        newFileMenuItem = new JMenuItem("New file");
        newFileMenuItem.addActionListener(new NewFileMenuItemActionListener());
        fileMenu.add(newFileMenuItem);
        newFolderMenuItem = new JMenuItem("New folder");
        newFolderMenuItem.addActionListener(new NewFolderMenuItemActionListener());
        fileMenu.add(newFolderMenuItem);
        fileMenu.addSeparator();
        deleteFileMenuItem = new JMenuItem("Delete");
        deleteFileMenuItem.addActionListener(new DeleteFileMenuItemActionListener());
        fileMenu.add(deleteFileMenuItem);
        jMenuBar.add(fileMenu);

        /* CREATE MAIN PANEL */
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        /* CREATE DIRECTION TEXT FIELD */
        directionField = new JTextField("C:\\");
        directionField.setPreferredSize(new Dimension(600, 24));
        directionField.addKeyListener(new PressEnterKey());

        /* CREATE GO BUTTON */
        goButton = new JButton("Go");
        goButton.addActionListener(new GoButtonActionListener());

        /* CREATE UP BUTTON */
        upButton = new JButton("Up");
        upButton.addActionListener(new UpButtonActionListener());

        /* CREATE RIGHT PANEL */
        rightPanel = new JPanel();

        /* CREATE DIRECTION PANEL */
        directionPanel = new JPanel();
        directionPanel.add(upButton);
        directionPanel.add(directionField);
        directionPanel.add(goButton);

        /* CREATE POPUP MENU ITEM */
        newFilePopupMenuItem = new JMenuItem("New file");
        newFilePopupMenuItem.addActionListener(new NewFileMenuItemActionListener());
        newFolderPopupMenuItem = new JMenuItem("New folder");
        newFolderPopupMenuItem.addActionListener(new NewFolderMenuItemActionListener());
        deleteFilePopupMenuItem = new JMenuItem("Delete");
        deleteFilePopupMenuItem.addActionListener(new DeleteFileMenuItemActionListener());

        /* CREATE J POPUP MENU */
        rightClickPopupMenu = new JPopupMenu();
        rightClickPopupMenu.add(newFilePopupMenuItem);
        rightClickPopupMenu.add(newFolderPopupMenuItem);
        rightClickPopupMenu.addSeparator();
        rightClickPopupMenu.add(deleteFilePopupMenuItem);

        /* CREATE J TABLE */
        table = new JTable(new MyFileTableModel(new File("C:\\").listFiles()));
        listScrollPane = new JScrollPane(table);
        table.addMouseListener(new TableMouseListener());

        /* ADD ELEMENTS TO MAIN PANEL */
        mainPanel.add(directionPanel, BorderLayout.NORTH);
        mainPanel.add(rightPanel, BorderLayout.EAST);
        mainPanel.add(listScrollPane, BorderLayout.CENTER);

        /* ADD ELEMENTS TO MAINFRAME */
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.setJMenuBar(jMenuBar);

        /* SHOW MAINFRAME */
        frame.setVisible(true);
    }

    void showFile() {
        String dir = directionField.getText();
        File[] files = new File(dir).listFiles();
        setTableData(files);
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
        showFile();
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
        showFile();

    }

    void deleteFile() {
        newDialog = new JDialog();
        boolean confirm;
        /**
         * TODO:
         *  Dialog potwierdzenia delete i delete
         */
        if (table.getSelectionModel().getSelectedItemsCount() > 1) {
            ArrayList<Object> tempFilesList = new ArrayList<>(table.getSelectionModel().getSelectedItemsCount());
            for (Object file : tempFilesList) {
                File tempFile = new File(file.toString());
                tempFile.delete();
                System.out.println(tempFile.getName() + " file deleted.");
            }
        } else if (table.getSelectionModel().getSelectedItemsCount() == 1) {
            File tempFile = new File();
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

    void setTableData(File[] files) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (fileTableModel == null) {
                    fileTableModel = new MyFileTableModel(new File("C:\\").listFiles());
                    table.setModel(fileTableModel);
                }
                fileTableModel.setFiles(files);
                if (!cellSizesSet) {
                    Icon icon = FileSystemView.getFileSystemView().getSystemIcon(files[0]);

                    table.setRowHeight(icon.getIconHeight() + rowIconPadding);
                    setColumnWidth(0, -1);
                }
            }

        });
    }

    private void setColumnWidth(int column, int width) {
        TableColumn tableColumn = table.getColumnModel().getColumn(column);
        if (width < 0) {
            // use the preferred width of the header..
            JLabel label = new JLabel((String) tableColumn.getHeaderValue());
            Dimension preferred = label.getPreferredSize();
            // altered 10->14 as per camickr comment.
            width = (int) preferred.getWidth() + 14;
        }
        tableColumn.setPreferredWidth(width);
        tableColumn.setMaxWidth(width);
        tableColumn.setMinWidth(width);
    }

    class NewFileMenuItemActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            createNewFile();
        }
    }

    class NewFolderMenuItemActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            createNewFolder();
        }
    }

    class DeleteFileMenuItemActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            deleteFile();
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
                showFile();
            }
        }
    }

    class GoButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            showFile();
        }
    }

    class UpButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Path tempPath = Paths.get(directionField.getText());
                String tempDir = tempPath.getParent().toString();
                directionField.setText(tempDir);
                showFile();

            } catch (NullPointerException ex) {
                System.out.println("I can't do that");
            }
        }
    }

    class TableMouseListener implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
                int tempDirInt = table.getSelectedRow();
                File[] files = new File(directionField.getText()).listFiles();
                File tempFile = files[tempDirInt].getAbsoluteFile();
                directionField.setText(tempFile.toString());
                showFile();
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e) && e.getClickCount() == 1) {

                if (e.isPopupTrigger()) {
                    rightClickPopupMenu.show(e.getComponent(), e.getX(), e.getY());
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

    static class MyFileTableModel extends AbstractTableModel {
        private File[] files;
        private String[] columnNames = {
                "ico", "Name", "Path"};

        MyFileTableModel(File[] files) {
            this.files = files;
        }

        @Override
        public int getRowCount() {
            return files.length;
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {

            File file = files[rowIndex];
            switch (columnIndex) {
                case 0:
                    return FileSystemView.getFileSystemView().getSystemIcon(file);
                case 1:
                    return FileSystemView.getFileSystemView().getSystemDisplayName(file);
                case 2:
                    return file.getPath();

            }
            return "";
        }

        @Override
        public Class getColumnClass(int col) {
            return getValueAt(0, col).getClass();
        }

        public void setFiles(File[] files) {
            this.files = files;
            fireTableDataChanged();
        }
    }


}
