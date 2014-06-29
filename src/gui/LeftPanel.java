package gui;

import gui.utility.FileManagement;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;
import world.World;

public class LeftPanel extends JPanel {

    MainFrame frame;

    protected JTabbedPane tabbedPane = new JTabbedPane();
    protected JSpinner discountSpinner;

    protected JList worldList;
    protected JLabel worldName;
    protected JLabel aWorld;
    protected JLabel bWorld;
    protected JLabel rWorld;
    protected JLabel algorithmName;
    protected JLabel iterationCoutLabel;
    protected JLabel storedNameLabel;

    protected JPanel algorithmPanel;
    protected JPanel cardPanel;

    protected JList agentList;
    protected JList policyList;

    protected JPanel agentPanel;
    protected JPanel policyPanel;

    protected JRadioButton agentButton;
    protected JRadioButton policyButton;

    protected JPanel cardControllPanel;
    protected JSpinner policyIterateSpinner;
    protected JSpinner agentStepSpinner;
    protected JSpinner agentIterateSpinner;

    protected JPanel worldInfoPanel;
    protected JPanel algorithmInfoPanel;

    public LeftPanel(MainFrame f) {
        frame = f;

        this.setLayout(new BorderLayout(5, 5));
        this.add(tabbedPane, BorderLayout.CENTER);

        initAlgorithmTab();
        
        initWorldTab();

        initWorldInfoPanel();

        initAlgorithmPanel();

        initControllPanel();

        JPanel panelWraper = new JPanel(new BorderLayout());
        panelWraper.add(worldInfoPanel, BorderLayout.NORTH);
        panelWraper.add(algorithmInfoPanel, BorderLayout.SOUTH);

        JPanel southPanel = new JPanel(new BorderLayout(5, 5));
        southPanel.add(panelWraper, BorderLayout.NORTH);
        southPanel.add(cardControllPanel, BorderLayout.SOUTH);
        this.add(southPanel, BorderLayout.SOUTH);

    }

    protected void initAlgorithmTab() {
        algorithmPanel = new JPanel(new BorderLayout());

        agentButton = new JRadioButton("Agent");
        agentButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (agentButton.isSelected()) {
                    CardLayout cl = (CardLayout) (cardPanel.getLayout());
                    cl.show(cardPanel, "agent");

                    algorithmPanel.revalidate();
                }
            }
        });
        policyButton = new JRadioButton("Policy", true);
        policyButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (policyButton.isSelected()) {
                    CardLayout cl = (CardLayout) (cardPanel.getLayout());
                    cl.show(cardPanel, "policy");

                    algorithmPanel.revalidate();
                }
            }
        });

        JPanel buttonPanelTop = new JPanel();
        buttonPanelTop.add(policyButton);
        buttonPanelTop.add(agentButton);

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(agentButton);
        buttonGroup.add(policyButton);

        /** Agnet panel * */
        agentPanel = new JPanel(new BorderLayout());

        agentList = new JList();
        agentList.setListData(FileManagement.getAgentsFileName().toArray());
        agentList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                super.mouseClicked(me);

                if (me.getButton() == MouseEvent.BUTTON1 && me.getClickCount() == 2) {
                    if (agentList.isSelectionEmpty()) {
                        return;
                    }

                    String agentFile = String.valueOf(agentList.getSelectedValue());

                    if (agentFile != null && !agentFile.isEmpty()) {
                        frame.setAgent(FileManagement.loadAgent(agentFile));
                        algorithmName.setText(agentFile);

                        CardLayout cl = (CardLayout) (cardControllPanel.getLayout());
                        cl.show(cardControllPanel, "agent");

                        cardControllPanel.revalidate();
                    }
                }
            }
        });

        JScrollPane listScrollPane = new JScrollPane(agentList);
        listScrollPane.setBorder(new EmptyBorder(0, 10, 10, 10));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                agentList.setListData(FileManagement.getAgentsFileName().toArray());
            }
        });
        JButton loadButton = new JButton("Load");
        loadButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (agentList.isSelectionEmpty()) {
                    return;
                }

                String agentFile = String.valueOf(agentList.getSelectedValue());

                if (agentFile != null && !agentFile.isEmpty()) {
                    frame.setAgent(FileManagement.loadAgent(agentFile));
                    algorithmName.setText(agentFile);

                    CardLayout cl = (CardLayout) (cardControllPanel.getLayout());
                    cl.show(cardControllPanel, "agent");

                    cardControllPanel.revalidate();
                }
            }
        });

        JPanel buttonPanelDown = new JPanel();
        buttonPanelDown.add(loadButton);
        buttonPanelDown.add(refreshButton);

        agentPanel.add(listScrollPane, BorderLayout.CENTER);
        agentPanel.add(buttonPanelDown, BorderLayout.SOUTH);

        /** Policy Panel * */
        policyPanel = new JPanel(new BorderLayout());

        policyList = new JList();
        policyList.setListData(FileManagement.getPolicyFileName().toArray());
        policyList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                super.mouseClicked(me);

                if (me.getButton() == MouseEvent.BUTTON1 && me.getClickCount() == 2) {
                    if (policyList.isSelectionEmpty()) {
                        return;
                    }

                    String policyFile = String.valueOf(policyList.getSelectedValue());

                    if (policyFile != null && !policyFile.isEmpty()) {
                        frame.setPolicy(FileManagement.loadPolicy(policyFile));
                        algorithmName.setText(policyFile);

                        CardLayout cl = (CardLayout) (cardControllPanel.getLayout());
                        cl.show(cardControllPanel, "policy");
                        cardControllPanel.revalidate();
                    }
                }
            }
        });

        JScrollPane policyListScrollPane = new JScrollPane(policyList);
        policyListScrollPane.setBorder(new EmptyBorder(0, 10, 10, 10));

        JButton refreshPolicyButton = new JButton("Refresh");
        refreshPolicyButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                policyList.setListData(FileManagement.getPolicyFileName().toArray());
            }
        });
        JButton loadPolicyButton = new JButton("Load");
        loadPolicyButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (policyList.isSelectionEmpty()) {
                    return;
                }

                String policyFile = String.valueOf(policyList.getSelectedValue());

                if (policyFile != null && !policyFile.isEmpty()) {
                    frame.setPolicy(FileManagement.loadPolicy(policyFile));
                    algorithmName.setText(policyFile);

                    CardLayout cl = (CardLayout) (cardControllPanel.getLayout());
                    cl.show(cardControllPanel, "policy");
                    cardControllPanel.revalidate();
                }
            }
        });

        JPanel buttonPolicyPanelDown = new JPanel();
        buttonPolicyPanelDown.add(loadPolicyButton);
        buttonPolicyPanelDown.add(refreshPolicyButton);

        policyPanel.add(policyListScrollPane, BorderLayout.CENTER);
        policyPanel.add(buttonPolicyPanelDown, BorderLayout.SOUTH);

        /** * */
        cardPanel = new JPanel(new CardLayout());
        cardPanel.add(policyPanel, "policy");
        cardPanel.add(agentPanel, "agent");

        CardLayout cl = (CardLayout) (cardPanel.getLayout());
        cl.show(cardPanel, "policy");

        algorithmPanel.add(buttonPanelTop, BorderLayout.PAGE_START);
        algorithmPanel.add(cardPanel, BorderLayout.CENTER);
        tabbedPane.add("Algorithm", algorithmPanel);
    }

    protected void initWorldTab() {
        JPanel worldPanel = new JPanel(new BorderLayout());

        worldList = new JList();
        worldList.setListData(FileManagement.getWorldsFileName().toArray());
        worldList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                super.mouseClicked(me);

                if (me.getButton() == MouseEvent.BUTTON1 && me.getClickCount() == 2) {
                    if (worldList.isSelectionEmpty()) {
                        return;
                    }

                    String worldName = String.valueOf(worldList.getSelectedValue());

                    if (worldName != null && !worldName.isEmpty()) {
                        frame.setWorld(FileManagement.loadWorld(worldName));
                    }
                }
            }
        });

        JScrollPane listScrollPane = new JScrollPane(worldList);
        listScrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                worldList.setListData(FileManagement.getWorldsFileName().toArray());
            }
        });
        JButton loadButton = new JButton("Load");
        loadButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {

                if (worldList.isSelectionEmpty()) {
                    return;
                }

                String worldName = String.valueOf(worldList.getSelectedValue());

                if (worldName != null && !worldName.isEmpty()) {
                    frame.setWorld(FileManagement.loadWorld(worldName));
                }
            }
        });

        JPanel buttonPanelDown = new JPanel();
        buttonPanelDown.add(loadButton);
        buttonPanelDown.add(refreshButton);

        worldPanel.add(listScrollPane, BorderLayout.CENTER);
        worldPanel.add(buttonPanelDown, BorderLayout.SOUTH);

        tabbedPane.add("World", worldPanel);
    }

    protected void initWorldInfoPanel() {
        GridLayout worldInfoLayout = new GridLayout(4, 2);
        worldInfoLayout.setHgap(10);
        JPanel mainPanel = new JPanel(worldInfoLayout);
        mainPanel.setBorder(new EmptyBorder(3, 3, 3, 3));

        // world name
        JLabel worldLabel = new JLabel("Name");
        worldLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        worldName = new JLabel();
        mainPanel.add(worldLabel);
        mainPanel.add(worldName);

        // world 'a'
        JLabel aWorldLabel = new JLabel("a");
        aWorldLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        aWorld = new JLabel();
        mainPanel.add(aWorldLabel);
        mainPanel.add(aWorld);

        //world 'b'
        JLabel bWorldLabel = new JLabel("b");
        bWorldLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        bWorld = new JLabel();
        mainPanel.add(bWorldLabel);
        mainPanel.add(bWorld);

        //world 'r'
        JLabel rWorldLabel = new JLabel("r");
        rWorldLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        rWorld = new JLabel();
        mainPanel.add(rWorldLabel);
        mainPanel.add(rWorld);

        worldInfoPanel = new JPanel();
        worldInfoPanel.setBorder(BorderFactory.createTitledBorder(" World "));
        worldInfoPanel.add(mainPanel);
    }

    protected void initAlgorithmPanel() {
        JPanel panelWraper = new JPanel(new BorderLayout());
        
        GridLayout propertiesLayout = new GridLayout(4, 2);
        propertiesLayout.setHgap(10);
        JPanel propertiesPanel = new JPanel(propertiesLayout);
        propertiesPanel.setBorder(new EmptyBorder(3, 3, 3, 3));
        
        // name
        JLabel algorithmLabel = new JLabel("Name");
        algorithmLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        algorithmName = new JLabel();
        propertiesPanel.add(algorithmLabel);
        propertiesPanel.add(algorithmName);
        
        // iteration
        JLabel iterationLabel = new JLabel("Iteration");
        iterationLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        iterationCoutLabel = new JLabel();
        propertiesPanel.add(iterationLabel);
        propertiesPanel.add(iterationCoutLabel);
        
        // dicount
        JLabel discountLabel = new JLabel("Discount");
        discountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        propertiesPanel.add(discountLabel);

        SpinnerNumberModel model = new SpinnerNumberModel(0.9, -Double.MAX_VALUE, Double.MAX_VALUE, 0.1);
        discountSpinner = new JSpinner(model);
        discountSpinner.setMinimumSize(new Dimension(0, 0));
        discountSpinner.setMaximumSize(new Dimension(1000, 1000));
        discountSpinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent ce) {
                frame.setDiscount((double) discountSpinner.getValue());
            }
        });

        JFormattedTextField txt = ((JSpinner.NumberEditor) discountSpinner.getEditor()).getTextField();
        ((NumberFormatter) txt.getFormatter()).setAllowsInvalid(false);

        propertiesPanel.add(discountSpinner);
        
        // stored name
        JLabel storedLabel = new JLabel("Stored Name");
        storedLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        storedNameLabel = new JLabel();
        propertiesPanel.add(storedLabel);
        propertiesPanel.add(storedNameLabel);        
        
        // store button
        JButton storeUsability = new JButton("Store Usability");
        storeUsability.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                frame.store();
            }
        });
        JPanel tempButtonPanel = new JPanel();
        tempButtonPanel.add(storeUsability);

        panelWraper.add(propertiesPanel, BorderLayout.CENTER);
        panelWraper.add(tempButtonPanel, BorderLayout.SOUTH);

        algorithmInfoPanel = new JPanel(new BorderLayout());
        algorithmInfoPanel.setBorder(BorderFactory.createTitledBorder(" Algorithm "));
        algorithmInfoPanel.add(panelWraper);
    }

    protected void initControllPanel() {
        JFormattedTextField txt;

        /** Policy * */
        GridLayout policyLayout = new GridLayout(2, 2);
        policyLayout.setHgap(10);
        policyLayout.setVgap(5);
        JPanel policyControllPanel = new JPanel(policyLayout);
        policyControllPanel.setBorder(new EmptyBorder(3, 3, 3, 3));

        JButton policyIterateButton = new JButton("Iterate");
        policyIterateButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                frame.iteratePolicy((int) policyIterateSpinner.getValue());
            }
        });
        policyControllPanel.add(policyIterateButton);

        SpinnerNumberModel pisModel = new SpinnerNumberModel(1, 1, 1000000, 1);
        policyIterateSpinner = new JSpinner(pisModel);
        policyIterateSpinner.setMinimumSize(new Dimension(0, 0));
        policyIterateSpinner.setMaximumSize(new Dimension(1000, 1000));

        txt = ((JSpinner.NumberEditor) policyIterateSpinner.getEditor()).getTextField();
        ((NumberFormatter) txt.getFormatter()).setAllowsInvalid(false);

        policyControllPanel.add(policyIterateSpinner);

        JButton policyCalculateButton = new JButton("Calculate");
        policyCalculateButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                frame.calculatePolicy();
            }
        });
        policyControllPanel.add(policyCalculateButton);

        /** Agent * */
        GridLayout agentLayout = new GridLayout(2, 2);
        agentLayout.setHgap(10);
        agentLayout.setVgap(5);
        JPanel agentControllPanel = new JPanel(agentLayout);
        agentControllPanel.setBorder(new EmptyBorder(3, 3, 3, 3));

        JButton agentStepButton = new JButton("Step");
        agentStepButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                frame.stepAgent((int) agentStepSpinner.getValue());
            }
        });
        agentControllPanel.add(agentStepButton);

        SpinnerNumberModel assModel = new SpinnerNumberModel(1, 1, 1000000, 1);
        agentStepSpinner = new JSpinner(assModel);
        agentStepSpinner.setMinimumSize(new Dimension(0, 0));
        agentStepSpinner.setMaximumSize(new Dimension(1000, 1000));

        txt = ((JSpinner.NumberEditor) agentStepSpinner.getEditor()).getTextField();
        ((NumberFormatter) txt.getFormatter()).setAllowsInvalid(false);

        agentControllPanel.add(agentStepSpinner);

        JButton agentIterateButton = new JButton("Iterate");
        agentIterateButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                frame.iterateeAgent((int) agentIterateSpinner.getValue());
            }
        });
        agentControllPanel.add(agentIterateButton);

        SpinnerNumberModel aisModel = new SpinnerNumberModel(1, 1, 1000000, 1);
        agentIterateSpinner = new JSpinner(aisModel);
        agentIterateSpinner.setMinimumSize(new Dimension(0, 0));
        agentIterateSpinner.setMaximumSize(new Dimension(1000, 1000));

        txt = ((JSpinner.NumberEditor) agentIterateSpinner.getEditor()).getTextField();
        ((NumberFormatter) txt.getFormatter()).setAllowsInvalid(false);

        agentControllPanel.add(agentIterateSpinner);

        /** Other * */
        cardControllPanel = new JPanel(new CardLayout());
        cardControllPanel.add(policyControllPanel, "policy");
        cardControllPanel.add(agentControllPanel, "agent");
        cardControllPanel.add(new JPanel(), "empty");

        CardLayout cl = (CardLayout) (cardControllPanel.getLayout());
        cl.show(cardControllPanel, "empty");

        this.add(cardControllPanel, BorderLayout.PAGE_END);
    }

    public void setWorldInfo(World w) {
        if (w != null) {
            worldName.setText(w.getName());
            aWorld.setText(String.format("%f", w.getA()));
            bWorld.setText(String.format("%f", w.getB()));
            rWorld.setText(String.format("%f", w.getR()));
        }
    }

    public void setAlgorithmName(String str) {
        algorithmName.setText(str);
    }

    public String getAlgorithmName() {
        return algorithmName.getText();
    }

    public void setIterationCountLabel(String val) {
        iterationCoutLabel.setText(val);
    }
    
    public void setStoredAlgName(String val) {
        storedNameLabel.setText(val);
    }
}
