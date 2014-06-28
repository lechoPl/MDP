package gui.worldEditor;

import enums.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;
import world.*;

public class EditFieldPanel extends JPanel {

    WorldEditorFrame frame;
    Field field;

    JRadioButton emptyButton = new JRadioButton("None");
    JRadioButton startButton = new JRadioButton("Start");
    JRadioButton terminalButton = new JRadioButton("Terminal");
    JRadioButton forbiddenButton = new JRadioButton("Forbidden");
    JRadioButton specialButton = new JRadioButton("Special");

    JSpinner rewardSpinner;

    protected ActionListener typeAction;
    protected ChangeListener rewardChange;

    public EditFieldPanel(WorldEditorFrame f) {
        frame = f;

        this.typeAction = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent ae) {

                if (field == null) {
                    return;
                }

                if (field.type == FieldType.START) {
                    setEnableStartType(true);
                }

                if (emptyButton.isSelected()) {
                    field.type = FieldType.EMPTY;
                }

                if (startButton.isSelected()) {
                    field.type = FieldType.START;
                }

                if (terminalButton.isSelected()) {
                    field.type = FieldType.TERMINAL;
                }

                if (specialButton.isSelected()) {
                    field.type = FieldType.SPECIAL;
                }

                if (forbiddenButton.isSelected()) {
                    field.type = FieldType.FORBIDDEN;
                }

                frame.refresh();
            }
        };

        this.rewardChange = new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent ce) {
                if (field == null) {
                    return;
                }

                double val = (double) rewardSpinner.getValue();
                field.reward = val;

                frame.refresh();
            }
        };

        ButtonGroup typeGroup = new ButtonGroup();

        typeGroup.add(emptyButton);
        typeGroup.add(startButton);
        typeGroup.add(terminalButton);
        typeGroup.add(forbiddenButton);
        typeGroup.add(specialButton);

        emptyButton.addActionListener(typeAction);
        startButton.addActionListener(typeAction);
        terminalButton.addActionListener(typeAction);
        forbiddenButton.addActionListener(typeAction);
        specialButton.addActionListener(typeAction);

        JPanel buttonsPanel = new JPanel(new GridLayout(5, 1));
        buttonsPanel.add(emptyButton);
        buttonsPanel.add(startButton);
        buttonsPanel.add(terminalButton);
        buttonsPanel.add(forbiddenButton);
        buttonsPanel.add(specialButton);

        JPanel propertiesPanel = new JPanel(new BorderLayout(10, 5));

        JPanel buttonPanelWraper = new JPanel(new BorderLayout(10, 5));
        buttonPanelWraper.add(new JLabel("Type"), BorderLayout.WEST);
        buttonPanelWraper.add(buttonsPanel, BorderLayout.EAST);
        propertiesPanel.add(buttonPanelWraper, BorderLayout.NORTH);

        SpinnerNumberModel model = new SpinnerNumberModel(0, -Double.MAX_VALUE, Double.MAX_VALUE, 0.1);
        rewardSpinner = new JSpinner(model);
        rewardSpinner.setMinimumSize(new Dimension(0, 0));
        rewardSpinner.setMaximumSize(new Dimension(50, 50));
        rewardSpinner.setPreferredSize(new Dimension(80, 20));
        rewardSpinner.addChangeListener(rewardChange);

        JFormattedTextField txt = ((JSpinner.NumberEditor) rewardSpinner.getEditor()).getTextField();
        ((NumberFormatter) txt.getFormatter()).setAllowsInvalid(false);

        JPanel rewardPanel = new JPanel(new BorderLayout(10, 5));
        rewardPanel.add(new JLabel("Reward"), BorderLayout.WEST);
        rewardPanel.add(rewardSpinner, BorderLayout.EAST);

        propertiesPanel.add(rewardPanel, BorderLayout.SOUTH);

        this.add(propertiesPanel);

        this.setBorder(BorderFactory.createTitledBorder(" Field "));

        resetField();
    }

    public void setField(Field f) {
        field = f;

        if (field == null) {
            rewardSpinner.setEnabled(false);
            emptyButton.setEnabled(false);
            startButton.setEnabled(false);
            terminalButton.setEnabled(false);
            forbiddenButton.setEnabled(false);
            specialButton.setEnabled(false);

            emptyButton.setSelected(true);
            rewardSpinner.setValue(0);
            return;
        } else {
            rewardSpinner.setEnabled(true);
            emptyButton.setEnabled(true);
            startButton.setEnabled(true);
            terminalButton.setEnabled(true);
            forbiddenButton.setEnabled(true);
            specialButton.setEnabled(true);
        }

        switch (field.type) {
            case EMPTY:
                emptyButton.setSelected(true);
                break;
            case START:
                startButton.setSelected(true);
                break;
            case SPECIAL:
                specialButton.setSelected(true);
                break;
            case TERMINAL:
                terminalButton.setSelected(true);
                break;
            case FORBIDDEN:
                forbiddenButton.setSelected(true);
                break;
        }

        rewardSpinner.setValue(field.reward);

    }

    public void resetField() {
        field = null;
        rewardSpinner.setEnabled(false);
        emptyButton.setEnabled(false);
        startButton.setEnabled(false);
        terminalButton.setEnabled(false);
        forbiddenButton.setEnabled(false);
        specialButton.setEnabled(false);
    }

    public void setEnableStartType(boolean b) {
        if (field != null) {
            startButton.setEnabled(b);
        }
    }

}
