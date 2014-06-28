package gui.utility;

import javax.swing.JTextPane;

public class MyLogger {

    protected static JTextPane textPane;

    public static void setTextPane(JTextPane pane) {
        textPane = pane;
    }

    public static void clear() {
        if (textPane != null) {
            textPane.setText("");
        }
    }

    public static void append(String str) {
        if (textPane != null) {
            if (!textPane.getText().isEmpty()) {
                textPane.setText(textPane.getText() + "\n>> " + str);
            } else {
                textPane.setText(">> " + str);
            }
        }
    }
}
