package gui.worldEditor;

import gui.WorldPanel;
import gui.utility.FileManagement;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;
import world.World;

public class WorldEditorFrame extends JFrame {

    protected final String frameTitle = "World Editor";

    protected final WorldEditorFrame that = this;

    protected JPanel worldPropertiesPanel = new JPanel();
    protected EditFieldPanel fieldProperties = new EditFieldPanel(this);
    protected JPanel buttonsPanel = new JPanel();
    protected WorldPanel worldPanel = new WorldPanel();

    protected JLabel nameLabel = new JLabel();
    protected JSpinner rewardSpinner;
    protected JSpinner nSpinner;
    protected JSpinner mSpinner;
    protected ChangeListener rewardChange;
    protected ChangeListener nChange;
    protected ChangeListener mChange;
    protected JSlider probabilitySlider;
    protected ChangeListener probabilityChange;
    protected JLabel aLabel = new JLabel();
    protected JLabel bLabel = new JLabel();

    protected JButton saveButton = new JButton("Save");
    protected JButton saveAsButton = new JButton("Save As...");
    protected ActionListener saveAction;
    protected ActionListener saveAsAction;

    public WorldEditorFrame() {
        initFrame();

        World world = new World();
        world.setSize(1, 1);

        setWorld(world);
    }

    public void initFrame() {
        this.rewardChange = new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent ce) {
                if (worldPanel.getWorld() == null) {
                    return;
                }

                double val = (double) rewardSpinner.getValue();
                worldPanel.getWorld().setR(val);

                that.reset();
            }
        };

        this.nChange = new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent ce) {

                if (worldPanel.getWorld() == null) {
                    return;
                }

                int val = (int) nSpinner.getValue();

                worldPanel.getWorld().setSize(val, worldPanel.getWorld().getM());

                that.reset();
            }
        };

        this.mChange = new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent ce) {
                if (worldPanel.getWorld() == null) {
                    return;
                }

                int val = (int) mSpinner.getValue();
                worldPanel.getWorld().setSize(worldPanel.getWorld().getN(), val);

                that.reset();
            }
        };

        this.probabilityChange = new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent ce) {

                if (worldPanel.getWorld() == null) {
                    return;
                }

                int val = (int) probabilitySlider.getValue();
                worldPanel.getWorld().setA(val / new Double(probabilitySlider.getMaximum()));

                aLabel.setText(String.format("%.3f", worldPanel.getWorld().getA()));
                bLabel.setText(String.format("%.3f", worldPanel.getWorld().getB()));

                that.reset();
            }
        };

        this.saveAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (worldPanel.getWorld() != null) {
                    FileManagement.SaveWorld(worldPanel.getWorld(), worldPanel.getWorld().getName());

                    nameLabel.setText(worldPanel.getWorld().getName());
                }
            }
        };

        this.saveAsAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (worldPanel.getWorld() != null) {

                    Object result = JOptionPane.showInputDialog(that, "Enter world name:", "Save As...", JOptionPane.QUESTION_MESSAGE);

                    if (result != null) {
                        FileManagement.SaveWorld(worldPanel.getWorld(), String.valueOf(result));

                        nameLabel.setText(worldPanel.getWorld().getName());
                    }
                }
            }
        };

        this.setTitle(frameTitle);
        this.setSize(640, 480);
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);

        this.setLayout(new BorderLayout());

        JPanel westPanel = new JPanel(new BorderLayout(10, 10));

        initWorlProperties();
        initButtonPanel();
        JPanel propertiesWarper = new JPanel(new BorderLayout(10, 5));
        propertiesWarper.add(worldPropertiesPanel, BorderLayout.NORTH);
        propertiesWarper.add(fieldProperties, BorderLayout.SOUTH);
        westPanel.add(propertiesWarper, BorderLayout.NORTH);
        westPanel.add(buttonsPanel, BorderLayout.CENTER);
        this.add(westPanel, BorderLayout.WEST);

        worldPanel.setEdit(true);
        worldPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                super.mouseClicked(me);

                fieldProperties.setField(worldPanel.getSelected());

                if (worldPanel.getWorld() != null) {
                    fieldProperties.setEnableStartType(worldPanel.getWorld().getStartState() == null);
                }
            }
        });
        this.add(new JScrollPane(worldPanel), BorderLayout.CENTER);

        this.validate();
    }

    public void initWorlProperties() {

        JPanel worldPropertiesWraper = new JPanel(new BorderLayout(10, 5));

        JPanel spnnersPanel = new JPanel(new BorderLayout(10, 5));

        SpinnerNumberModel modelNSize = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
        nSpinner = new JSpinner(modelNSize);
        nSpinner.setPreferredSize(new Dimension(80, 20));
        nSpinner.addChangeListener(nChange);
        ((NumberFormatter) ((JSpinner.NumberEditor) nSpinner.getEditor()).getTextField().
                getFormatter()).setAllowsInvalid(false);

        JPanel nPanel = new JPanel(new BorderLayout(10, 5));
        nPanel.add(new JLabel("N"), BorderLayout.WEST);
        nPanel.add(nSpinner, BorderLayout.EAST);

        spnnersPanel.add(nPanel, BorderLayout.NORTH);

        SpinnerNumberModel modelMSize = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
        mSpinner = new JSpinner(modelMSize);
        mSpinner.setPreferredSize(new Dimension(80, 20));
        mSpinner.addChangeListener(mChange);
        ((NumberFormatter) ((JSpinner.NumberEditor) mSpinner.getEditor()).getTextField().
                getFormatter()).setAllowsInvalid(false);

        JPanel mPanel = new JPanel(new BorderLayout(10, 5));
        mPanel.add(new JLabel("M"), BorderLayout.WEST);
        mPanel.add(mSpinner, BorderLayout.EAST);

        spnnersPanel.add(mPanel, BorderLayout.CENTER);

        SpinnerNumberModel model = new SpinnerNumberModel(0, -Double.MAX_VALUE, Double.MAX_VALUE, 0.1);
        rewardSpinner = new JSpinner(model);
        rewardSpinner.setPreferredSize(new Dimension(80, 20));
        rewardSpinner.addChangeListener(rewardChange);

        ((NumberFormatter) ((JSpinner.NumberEditor) rewardSpinner.getEditor()).getTextField().
                getFormatter()).setAllowsInvalid(false);

        JPanel rewardPanel = new JPanel(new BorderLayout(10, 5));
        rewardPanel.add(new JLabel("Reward"), BorderLayout.WEST);
        rewardPanel.add(rewardSpinner, BorderLayout.EAST);

        spnnersPanel.add(rewardPanel, BorderLayout.SOUTH);

        JPanel namePanel = new JPanel(new BorderLayout(10, 5));
        namePanel.add(new JLabel("Name"), BorderLayout.WEST);
        namePanel.add(nameLabel, BorderLayout.EAST);

        JPanel probabilityPanel = new JPanel(new BorderLayout(10, 5));
        probabilitySlider = new JSlider(0, 100);
        probabilitySlider.setPreferredSize(new Dimension(100, 20));
        probabilitySlider.addChangeListener(probabilityChange);
        probabilityPanel.add(probabilitySlider, BorderLayout.NORTH);
        JPanel aPanel = new JPanel(new BorderLayout(10, 10));
        aPanel.add(new JLabel("a"), BorderLayout.WEST);
        aPanel.add(aLabel, BorderLayout.EAST);
        probabilityPanel.add(aPanel, BorderLayout.CENTER);

        JPanel bPanel = new JPanel(new BorderLayout(10, 5));
        bPanel.add(new JLabel("b"), BorderLayout.WEST);
        bPanel.add(bLabel, BorderLayout.EAST);
        probabilityPanel.add(bPanel, BorderLayout.SOUTH);

        worldPropertiesWraper.add(namePanel, BorderLayout.NORTH);
        worldPropertiesWraper.add(spnnersPanel, BorderLayout.CENTER);
        worldPropertiesWraper.add(probabilityPanel, BorderLayout.SOUTH);

        worldPropertiesPanel.setBorder(BorderFactory.createTitledBorder(" World "));
        worldPropertiesPanel.add(worldPropertiesWraper);
    }

    public void initButtonPanel() {
        saveButton.addActionListener(saveAction);
        buttonsPanel.add(saveButton);

        saveAsButton.addActionListener(saveAsAction);
        buttonsPanel.add(saveAsButton);
    }

    public void setWorld(World w) {
        worldPanel.setWorld(w);

        if (w != null) {
            nameLabel.setText(w.getName());
            nSpinner.setValue(w.getN());
            mSpinner.setValue(w.getM());
            rewardSpinner.setValue(w.getR());
            probabilitySlider.setValue((int) (w.getA() * 100));
            aLabel.setText(Double.toString(w.getA()));
            bLabel.setText(Double.toString(w.getB()));
        } else {
            nameLabel.setText("---");
            World world = new World();
            world.setSize(1, 1);
            this.setWorld(world);
            return;
        }

        reset();
    }

    @Override
    public void hide() {
        //ewentualne potwierdzenie

        reset();

        super.hide();
    }

    public void reset() {
        worldPanel.resetSelected();
        fieldProperties.resetField();

        refresh();
    }

    public void refresh() {
        worldPanel.refresh();
        worldPanel.repaint();
    }
}
