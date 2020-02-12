import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Time;
import java.time.LocalTime;

class GUI {

    private static String APP_TITTLE = "APP";
    private JFrame frame;

    private static boolean DEBUG = true;
    private JFrame logger;
    private JScrollPane logScrollPane;
    private JTextArea logTextArea;

    private Desktop desktop;

    private JMenuBar jMenuBar;
    private JMenu fileMenu;
    private JMenu logMenu;
    private JMenuItem newFileMenuItem;
    private JMenuItem newFolderMenuItem;
    private JMenuItem copyFileMenuItem;
    private JMenuItem deleteFileMenuItem;
    private JMenuItem logShowMenuItem;

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
    private JMenuItem copyFilePopupMenuItem;
    private JMenuItem deleteFilePopupMenuItem;

    private JPopupMenu rightClickPopupMenu;

    private File copyFileSource;

    void mainFrameExecute() {
        /* DESKTOP */
        desktop = Desktop.getDesktop();

        /* CREATE FRAME */
        frame = new JFrame(APP_TITTLE);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        /* CREATE LOGGER */
        logger = new JFrame(APP_TITTLE + " LOGGER");
        logger.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        logger.setBounds(900, 0, 530, 820);
        logTextArea = new JTextArea();
        logTextArea.setBackground(Color.WHITE);
        logScrollPane = new JScrollPane(logTextArea);
        logScrollPane.createVerticalScrollBar();
        logger.add(logScrollPane, BorderLayout.CENTER);
        if (DEBUG) {
            logger.setVisible(true);
        }

        /* CREATE MENU BAR */
        jMenuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        logMenu = new JMenu("Log");

        newFileMenuItem = new JMenuItem("New file");
        newFileMenuItem.addActionListener(new NewFileMenuItemActionListener());
        fileMenu.add(newFileMenuItem);
        newFolderMenuItem = new JMenuItem("New folder");
        newFolderMenuItem.addActionListener(new NewFolderMenuItemActionListener());
        fileMenu.add(newFolderMenuItem);
        fileMenu.addSeparator();
        copyFileMenuItem = new JMenuItem("Copy");
        copyFileMenuItem.addActionListener(new CopyMenuItemActionListener());
        fileMenu.add(copyFileMenuItem);
        fileMenu.addSeparator();
        deleteFileMenuItem = new JMenuItem("Delete");
        deleteFileMenuItem.addActionListener(new DeleteFileMenuItemActionListener());
        fileMenu.add(deleteFileMenuItem);

        logShowMenuItem = new JMenuItem("Show log");
        logShowMenuItem.addActionListener(new ShowLogActionListener());
        logMenu.add(logShowMenuItem);

        jMenuBar.add(fileMenu);
        jMenuBar.add(logMenu);

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
        copyFilePopupMenuItem = new JMenuItem("Copy");
        copyFilePopupMenuItem.addActionListener(new CopyMenuItemActionListener());
        deleteFilePopupMenuItem = new JMenuItem("Delete");
        deleteFilePopupMenuItem.addActionListener(new DeleteFileMenuItemActionListener());

        /* CREATE J POPUP MENU */
        rightClickPopupMenu = new JPopupMenu();
        rightClickPopupMenu.add(newFilePopupMenuItem);
        rightClickPopupMenu.add(newFolderPopupMenuItem);
        rightClickPopupMenu.addSeparator();
        rightClickPopupMenu.add(copyFilePopupMenuItem);
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

    private void showFile() {
        try {
            String dir = directionField.getText();
            File[] files = new File(dir).listFiles();
            setTableData(files);
            logTextArea.append(Time.valueOf(LocalTime.now()) + ": Directory " + dir + " displayed.\n");
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            System.out.println("Folder is empty.");
        }

    }

    private void createNewFile() {
        String newFilename = JOptionPane.showInputDialog(frame, "Input file name", APP_TITTLE);
        try {
            File tempFile = new File(directionField.getText() + "\\" + newFilename);
            if (tempFile.createNewFile()) {
                logTextArea.append(Time.valueOf(LocalTime.now()) + ": File " + tempFile + " created.\n");
                System.out.println("File created.");
            } else {
                logTextArea.append(Time.valueOf(LocalTime.now()) + ": File " + tempFile + " not created.\n");
                System.out.println("File not created.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        showFile();
    }

    private void createNewFolder() {
        String newFolderName = JOptionPane.showInputDialog(frame, "input folder name", APP_TITTLE);
        File tempFolder = new File(directionField.getText() + "\\" + newFolderName);
        if (tempFolder.mkdir()) {
            logTextArea.append(Time.valueOf(LocalTime.now()) + ": Folder " + tempFolder + " created.\n");
            System.out.println("Folder created.");
        } else {
            logTextArea.append(Time.valueOf(LocalTime.now()) + ": Folder " + tempFolder + " not created.\n");
            System.out.println("Folder doesn't created. ");
        }
        showFile();

    }

    private void deleteFile() {
        int confirm;
        confirm = JOptionPane.showConfirmDialog(frame, "Napewno chcesz usunąć pliki?", APP_TITTLE, JOptionPane.OK_CANCEL_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int[] tempFiles = table.getSelectedRows();
            File[] files = new File(directionField.getText()).listFiles();
            for (int i = 0; i < tempFiles.length; i++) {
                File tempFile = new File(String.valueOf(files[tempFiles[i]]));
                if (tempFile.delete()) {
                    logTextArea.append(Time.valueOf(LocalTime.now()) + ": File " + tempFile + " deleted.\n");
                    System.out.println("File deleted.");
                } else {
                    logTextArea.append(Time.valueOf(LocalTime.now()) + ": File " + tempFile + " not deleted.\n");
                    System.out.println("File not deleted.");
                }
            }
        }
        showFile();
    }

    private void openFile() {
        int tempDirInt = table.getSelectedRow();
        File[] files = new File(directionField.getText()).listFiles();
        File tempFile = files[tempDirInt].getAbsoluteFile();
        if (tempFile.isDirectory()) {
            directionField.setText(tempFile.toString());
            showFile();
        } else if (tempFile.isFile()) {
            try {
                desktop.open(tempFile);
                logTextArea.append(Time.valueOf(LocalTime.now()) + ": File " + tempFile + " opened.\n");
            } catch (IOException ex) {
                logTextArea.append(Time.valueOf(LocalTime.now()) + ": File " + tempFile + " not opened.\n");
                ex.printStackTrace();
            }
        }
    }

    /**
     * TODO:
     * Zrobić metode getFiles(directionField.getText()).listFiles();
     */

    private File getCopyFileSource() {
        int tempFile = table.getSelectedRow();
        File[] files = new File(directionField.getText()).listFiles();
        copyFileSource = files[tempFile];
        logTextArea.append(Time.valueOf(LocalTime.now()) + ": File " + copyFileSource + " copied to tray.\n");
        return this.copyFileSource;
    }

    private void copyFile(File sourceFile, File destinationFile) {
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

    private void setTableData(File[] files) {

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


    private void setColumnWidth(int column, int width) {
        TableColumn tableColumn = table.getColumnModel().getColumn(column);
        if (width < 0) {
            // use the preferred width of the header
            JLabel label = new JLabel((String) tableColumn.getHeaderValue());
            Dimension preferred = label.getPreferredSize();
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

    class ShowLogActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            logger.setVisible(true);
        }
    }

    class CopyMenuItemActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            getCopyFileSource();
        }
    }

    class TableMouseListener implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
                openFile();
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

        void setFiles(File[] files) {
            this.files = files;
            fireTableDataChanged();
        }
    }


}
