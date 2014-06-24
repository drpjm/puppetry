package edu.gatech.grits.puppetctrl.gui;

import java.awt.*;
import javax.swing.*;
import java.io.*;

public class Console extends JPanel {

    private static int IDEAL_SIZE = 300000;
    private static int MAX_EXCESS = 1000;

    private PipedInputStream piOut;
    private PipedInputStream piErr;
    private PipedOutputStream poOut;
    private PipedOutputStream poErr;
    private JTextArea textArea;
    private int searchFromPosition = 0;
    private String latestSearchTerm = "";

    public Console() throws IOException {
        // Set up System.out
        piOut = new PipedInputStream();
        poOut = new PipedOutputStream(piOut);
        System.setOut(new PrintStream(poOut, true));

        // Set up System.err
        piErr = new PipedInputStream();
        poErr = new PipedOutputStream(piErr);
        System.setErr(new PrintStream(poErr, true));

        // Set up GUI
        setLayout(new BorderLayout());
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        // Create reader threads
        new ReaderThread(piOut, "Thread-OuputStream").start();
        new ReaderThread(piErr, "Thread-ErrorStream").start();
    }

    public void search(String searchTerm) {
        searchFromPosition = 0;
        int newCaretPosition = textArea.getText().indexOf(searchTerm, searchFromPosition);
        if (newCaretPosition >= 0) {
            textArea.setCaretPosition(newCaretPosition);
            searchFromPosition = newCaretPosition;
            latestSearchTerm = searchTerm;
        }
        else {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    public void next() {
        int newCaretPosition = textArea.getText().indexOf(latestSearchTerm, searchFromPosition);
        if (newCaretPosition >= 0) {
            textArea.setCaretPosition(newCaretPosition);
            searchFromPosition = newCaretPosition;
        }
    }

    private class ReaderThread extends Thread {
        private PipedInputStream inputStream;

        public ReaderThread(PipedInputStream inputStream, String name) {
            this.inputStream = inputStream;
            this.setName(name);
        }

        public void run() {
            final byte[] buffer = new byte[1024];
            try {
                while (true) {
                    final int length = inputStream.read(buffer);
                    if (length == -1)
                        break;

                    textArea.append(new String(buffer, 0, length));
                    int documentLength = textArea.getDocument().getLength();
                    textArea.setCaretPosition(documentLength);

                    int excess = textArea.getDocument().getLength() - IDEAL_SIZE;
                    if (excess >= MAX_EXCESS) {
                        textArea.replaceRange("", 0, excess);
                    }

                    Thread.sleep(150);
                }
            }
            catch (Exception e) {
            	e.printStackTrace();
            }
        }
    }
}

