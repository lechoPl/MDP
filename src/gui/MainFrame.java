package gui;

import gui.charts.PolicyChartFrame;
import gui.charts.RmsChartFrame;
import gui.charts.UsabilityChartFrame;
import gui.utility.MyLogger;
import gui.worldEditor.WorldEditorFrame;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import logic.Logic;
import userCode.IAgent;
import userCode.IPolicy;
import world.World;

public class MainFrame extends JFrame {

    protected final MainFrame that = this;

    protected Logic logic = new Logic();

    protected final String frameTitle = "MDP";

    protected JMenuBar menuBar = new JMenuBar();
    protected JMenu menuFile = new JMenu("File");
    protected JMenuItem newWorldMenuItem = new JMenuItem("New World...");
    protected JMenuItem exitMenuItem = new JMenuItem("Exit");

    protected JMenu menuChart = new JMenu("Charts");
    protected JMenuItem usabilityChar = new JMenuItem("Show Usability Chart");
    protected JMenuItem policyDiffrentChar = new JMenuItem("Show Policy Differences Chart");
    protected JMenuItem RmsErrorChar = new JMenuItem("Show RMS ERROR Chart");

    protected JMenu menuSettings = new JMenu("Settings");
    protected JMenu viewSettingsMenu = new JMenu("View");
    protected JCheckBox showStoredPolicy = new JCheckBox("Show stored policy");
    protected JCheckBox showStoredUsability = new JCheckBox("Show stored usability");

    protected JSplitPane verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

    protected JSplitPane mainSplitPane = new JSplitPane();
    protected WorldPanel worldPanel = new WorldPanel();

    protected LeftPanel leftPanel = new LeftPanel(this);

    protected JTextPane output = new JTextPane();
    protected JPanel outputPanel;

    protected Button zoomOutButton;
    protected Button zoomInButton;

    protected WorldEditorFrame editorFrame;

    public MainFrame() {

        this.setTitle(frameTitle);
        this.setSize(1024, 768);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        this.setVisible(true);
        this.setLayout(new BorderLayout());

        menuBar.add(menuFile);
        menuFile.add(newWorldMenuItem);
        newWorldMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                editorFrame.setWorld(null);
                editorFrame.setVisible(true);
            }
        });
        menuFile.add(new JSeparator(JSeparator.HORIZONTAL));
        menuFile.add(exitMenuItem);
        exitMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                System.exit(0);
            }
        });

        menuBar.add(menuChart);
        menuChart.add(usabilityChar);
        usabilityChar.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                UsabilityChartFrame chartFrame = new UsabilityChartFrame(that.logic.GetUsabilityHistory());
                chartFrame.setVisible(true);
            }
        });
        menuChart.add(policyDiffrentChar);
        policyDiffrentChar.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                PolicyChartFrame chartFrame = new PolicyChartFrame(that.logic.GetPolicyHistory(), that.logic.GetStoredPolicy());
                chartFrame.setVisible(true);
            }
        });
        menuChart.add(RmsErrorChar);
        RmsErrorChar.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                RmsChartFrame chartFrame = new RmsChartFrame(that.logic.GetUsabilityHistory(), that.logic.GetStoredUsability());
                chartFrame.setVisible(true);
            }
        });
//        menuChart.add(new JSeparator(JSeparator.HORIZONTAL));
//        menuChart.add(storedUsabilityLabel);
//        menuChart.add(storedPolicyLabel);

        menuBar.add(menuSettings);
        menuSettings.add(viewSettingsMenu);
        viewSettingsMenu.add(showStoredPolicy);
        showStoredPolicy.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                worldPanel.setShowStoredPolicy(showStoredPolicy.isSelected());
            }
        });
        viewSettingsMenu.add(showStoredUsability);
        showStoredUsability.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                worldPanel.setShowStoredUsability(showStoredUsability.isSelected());
            }
        });

        this.add(menuBar, BorderLayout.PAGE_START);

        mainSplitPane.add(leftPanel, JSplitPane.LEFT);

        //world view
        initWorldView();

        //output panel
        initOutput();

        //----
        mainSplitPane.setDividerSize(3);
        verticalSplitPane.add(mainSplitPane);
        verticalSplitPane.add(outputPanel);
        verticalSplitPane.setDividerSize(3);
        this.add(verticalSplitPane, BorderLayout.CENTER);

        this.validate();

        verticalSplitPane.setDividerLocation(0.8);
        verticalSplitPane.setResizeWeight(0);

        mainSplitPane.setDividerLocation(0.2);
        mainSplitPane.setResizeWeight(0);

        editorFrame = new WorldEditorFrame();
    }

    protected void initWorldView() {
        JPanel tempPanel = new JPanel(new BorderLayout());
        JScrollPane tempScrollPane = new JScrollPane(worldPanel);

        tempPanel.add(tempScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new BorderLayout());
        JPanel buttonLeftPanel = new JPanel();
        buttonLeftPanel.setAlignmentX(LEFT_ALIGNMENT);
        buttonPanel.add(buttonLeftPanel, BorderLayout.WEST);

        zoomOutButton = new Button("-");
        zoomOutButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                worldPanel.zoomOut();
            }
        });
        buttonLeftPanel.add(zoomOutButton);

        zoomInButton = new Button("+");
        zoomInButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                worldPanel.zoomIn();
            }
        });
        buttonLeftPanel.add(zoomInButton);

        JPanel buttonRightPanel = new JPanel();
        buttonRightPanel.setAlignmentX(RIGHT_ALIGNMENT);
        buttonPanel.add(buttonRightPanel, BorderLayout.EAST);

        Button editWorldButton = new Button("Edit");
        editWorldButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (editorFrame != null && worldPanel != null && worldPanel.getWorld() != null) {
                    editorFrame.setWorld(worldPanel.getWorld().copy());
                    editorFrame.setVisible(true);
                }
            }
        });
        buttonRightPanel.add(editWorldButton);

        Button resetButton = new Button("Reset");
        resetButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (logic != null) {
                    logic.reset();
                    leftPanel.setIterationCountLabel(String.valueOf(logic.GetUsabilityHistory().size()));

                    worldPanel.refresh();
                }
            }
        });
        buttonRightPanel.add(resetButton);

        tempPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainSplitPane.add(tempPanel, JSplitPane.RIGHT);
    }

    protected void initOutput() {

        JScrollPane outputScrollPane = new JScrollPane(output);
        outputScrollPane.setBorder(new EmptyBorder(3, 5, 5, 5));

        output.setEditable(false);
        MyLogger.setTextPane(output);

        TitledBorder scrollPaneBorder = BorderFactory.createTitledBorder(" Output ");
        outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(scrollPaneBorder);

        outputPanel.add(outputScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        Button clearBurron = new Button("Clear");
        clearBurron.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                MyLogger.clear();
            }
        });
        buttonPanel.add(clearBurron);
        outputPanel.add(buttonPanel, BorderLayout.AFTER_LINE_ENDS);
    }

    public void setWorld(World w) {
        logic.setWorld(w);

        leftPanel.setWorldInfo(w);
        worldPanel.setLogic(logic);
        worldPanel.resetStoredPolicy();
        worldPanel.resetStoredUsability();

        leftPanel.setIterationCountLabel(String.valueOf(logic.GetUsabilityHistory().size()));
        leftPanel.setStoredAlgName("");
//        storedPolicyLabel.setText(storedPolicyPrefix);
//        storedUsabilityLabel.setText(storedUsabilityPrefix);
    }

    public World getWorld() {
        return logic.getWorld();
    }

    void setAgent(IAgent loadAgent) {
        try {
            logic.setAgent(loadAgent);
            leftPanel.setIterationCountLabel(String.valueOf(logic.GetUsabilityHistory().size()));

            worldPanel.refresh();

        } catch (Exception ex) {
            String msg = ex.toString() + "\n";
            for (StackTraceElement e : ex.getStackTrace()) {
                msg += "\t" + e.toString() + "\n";
            }
            MyLogger.append(msg);

        }

    }

    void setPolicy(IPolicy loadPolicy) {
        try {
            logic.setPolicy(loadPolicy);
            leftPanel.setIterationCountLabel(String.valueOf(logic.GetUsabilityHistory().size()));

            worldPanel.refresh();
        } catch (Exception ex) {
            String msg = ex.toString() + "\n";
            for (StackTraceElement e : ex.getStackTrace()) {
                msg += "\t" + e.toString() + "\n";
            }
            MyLogger.append(msg);

        }
    }

    void iterateeAgent(int i) {
        try {
            logic.iterateeAgent(i);

            leftPanel.setIterationCountLabel(String.valueOf(logic.GetUsabilityHistory().size()));
            worldPanel.refresh();
        } catch (Exception ex) {
            String msg = ex.toString() + "\n";
            for (StackTraceElement e : ex.getStackTrace()) {
                msg += "\t" + e.toString() + "\n";
            }
            MyLogger.append(msg);

        }
    }

    void stepAgent(int i) {
        try {
            logic.stepAgent(i);

            leftPanel.setIterationCountLabel(String.valueOf(logic.GetUsabilityHistory().size()));
            worldPanel.refresh();
        } catch (Exception ex) {
            String msg = ex.toString() + "\n";
            for (StackTraceElement e : ex.getStackTrace()) {
                msg += "\t" + e.toString() + "\n";
            }
            MyLogger.append(msg);

        }
    }

    void setDiscount(double d) {
        try {
            logic.setDiscount(d);

            leftPanel.setIterationCountLabel(String.valueOf(logic.GetUsabilityHistory().size()));
            worldPanel.refresh();
        } catch (Exception ex) {
            String msg = ex.toString() + "\n";
            for (StackTraceElement e : ex.getStackTrace()) {
                msg += "\t" + e.toString() + "\n";
            }
            MyLogger.append(msg);

        }

    }

    void iteratePolicy(int i) {
        try {
            logic.iteratePolicy(i);

            leftPanel.setIterationCountLabel(String.valueOf(logic.GetUsabilityHistory().size()));
            worldPanel.refresh();
        } catch (Exception ex) {
            String msg = ex.toString() + "\n";
            for (StackTraceElement e : ex.getStackTrace()) {
                msg += "\t" + e.toString() + "\n";
            }
            MyLogger.append(msg);

        }

    }

    void calculatePolicy() {
        try {
            logic.calculatePolicy();

            leftPanel.setIterationCountLabel(String.valueOf(logic.GetUsabilityHistory().size()));
            worldPanel.refresh();
        } catch (Exception ex) {
            String msg = ex.toString() + "\n";
            for (StackTraceElement e : ex.getStackTrace()) {
                msg += "\t" + e.toString() + "\n";
            }
            MyLogger.append(msg);

        }
    }

    void store() {
        logic.StoreUsability();
        logic.StorePolicy();

        leftPanel.setStoredAlgName(leftPanel.getAlgorithmName() + " ( "+ logic.GetUsabilityHistory().size()  +" )");
        
        worldPanel.setStoredPolicy(logic.GetStoredPolicy());
        worldPanel.setStoredUsability(logic.getCurrentUsefulness());
    }

    public static void main(String[] argc) {
        new MainFrame();
    }
}
