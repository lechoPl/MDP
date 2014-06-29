package gui;

import enums.Action;
import enums.FieldType;
import gui.utility.MyLogger;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import logic.Logic;
import world.Field;
import world.State;
import world.World;

public class WorldPanel extends JPanel {

    protected World world;

    protected Action[][] storedPolicy;
    protected Double[][] storedUsability;

    protected Logic logic;

    protected int fieldSize;

    protected int xTranslate = 0;
    protected int yTranslate = 0;

    protected JPanel that = this;
    protected Point selectedField = null;
    protected boolean edit = false;

    protected boolean showStoredPolicy = false;
    protected boolean showStoredUsability = false;
    
    protected Color fieldColor = this.getBackground();
    protected Color forbiddenColor = Color.GRAY;
    protected Color agentColor = Color.YELLOW;
    protected Color fieldSymbolColor = Color.BLACK;
    protected Color policyColor = Color.BLACK;
    protected Color usabilityColor = Color.BLACK;
    protected Color storedPolicySameColor = Color.GREEN;
    protected Color storedPolicyDifferentColor = Color.RED;
    protected Color storedUsabilityColor = Color.GRAY;
    protected Color borderColor = Color.BLACK;
    protected Color selectedColor = Color.RED;

    public WorldPanel() {

        setFiledSize(50);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                super.mouseClicked(me);

                if (me.getButton() == MouseEvent.BUTTON1 && me.getClickCount() == 1) {
                    Point p = me.getPoint();

                    if (world != null) {

                        int x = (p.x - xTranslate) / fieldSize;
                        int y = world.getM() - 1 - (p.y - yTranslate) / fieldSize;

                        if (x >= 0 && x < world.getN() && y >= 0 && y < world.getM()) {
                            selectedField = new Point(x, y);
                        } else {
                            selectedField = null;
                        }
                    } else {
                        selectedField = null;
                    }

                    that.repaint();
                }
            }
        });
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World w) {
        world = w;
        resetSelected();

        refresh();
    }

    public void setLogic(Logic l) {
        this.logic = l;

        resetSelected();
        refresh();
    }

    public void refresh() {
        if (edit) {
            if (world != null) {
                int width = world.getN() * fieldSize;
                int height = world.getM() * fieldSize;

                this.setPreferredSize(new Dimension(width, height));
                this.revalidate();
                this.repaint();
            }
        } else {
            if (logic != null && logic.getWorld() != null) {

                int width = logic.getWorld().getN() * fieldSize;
                int height = logic.getWorld().getM() * fieldSize;

                this.setPreferredSize(new Dimension(width, height));
                this.revalidate();
                this.repaint();
            }
        }
    }

    public int getFiledSize() {
        return fieldSize;
    }

    public void setFiledSize(int size) {
        fieldSize = size;

        if (world != null) {
            int width = world.getN() * fieldSize;
            int height = world.getM() * fieldSize;

            this.setPreferredSize(new Dimension(width, height));
        }

        this.setFont(new Font("TimesRoman", Font.BOLD, fieldSize / 4));
    }

    public Field getSelected() {
        if (selectedField == null) {
            return null;
        }

        return world.getField(selectedField.x, selectedField.y);
    }

    public void resetSelected() {
        selectedField = null;
    }

    public void setEdit(boolean val) {
        edit = val;
    }

    public void zoomOut() {
        fieldSize -= 10;
        if (fieldSize < 0) {
            fieldSize = 0;
        }

        refresh();
    }

    public void zoomIn() {
        fieldSize += 10;

        refresh();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.clearRect(0, 0, this.getSize().width, this.getSize().height);

        if (!edit && logic != null) {
            world = logic.getWorld();
        }

        if (world != null) {
            if (world.getN() * fieldSize < this.getSize().width) {
                xTranslate = (this.getSize().width - world.getN() * fieldSize) / 2;
            } else {
                xTranslate = 0;
            }

            if (world.getM() * fieldSize < this.getSize().height) {
                yTranslate = (this.getSize().height - world.getM() * fieldSize) / 2;
            } else {
                yTranslate = 0;
            }

            g.translate(xTranslate, yTranslate);
            
            int temp = 0;
            
            Color bg = this.getBackground();
            for (int x = 0; x < world.getN(); x++) {

                int xPos = x * fieldSize;

                for (int y = world.getM()-1; y >= 0 ; y--) {

                    int yPos = (world.getM() - 1 - y) * fieldSize;

                    Field tempField = world.getField(x, y);
                    if (tempField != null) {
                        String fieldSymbol = String.valueOf(FieldType.getChar(tempField.type));
                        String reward = "";
                        g.setColor(fieldColor);
                        switch (tempField.type) {
                            case FORBIDDEN:
                                g.setColor(forbiddenColor);
                                break;
                            case SPECIAL:
                                reward = Double.toString(tempField.reward);
                                break;
                            case TERMINAL:
                                reward = Double.toString(tempField.reward);
                                break;
                        }
                        g.fillRect(xPos, yPos, fieldSize, fieldSize);

                        if (!edit && logic != null && new State(x, y).compareTo(logic.getCurrentAgentState()) == 0) {
                            g.setColor(agentColor);
                            g.fillOval(xPos, yPos, fieldSize, fieldSize);
                        }

                        g.setColor(fieldSymbolColor);
                        g.drawString(fieldSymbol, xPos + 5, yPos + g.getFontMetrics().getHeight());
                        g.drawString(reward, xPos + 5, yPos + g.getFontMetrics().getHeight() * 2);
                    }

                    if (!edit && logic != null && !world.isForbidden(x, y) && !world.isTermina(new State(x, y))) {
                        
                        if (logic.getOptimalActions() != null) {
                            g.setColor(policyColor);
                            String act = Action.toString(logic.getOptimalActions()[x][y]);
                            g.drawString(act, xPos + fieldSize - g.getFontMetrics().stringWidth(act) - 5, yPos + g.getFontMetrics().getHeight());

                        }

                        if (logic.getCurrentUsefulness() != null) {
                            g.setColor(usabilityColor);
                            Double val = logic.getCurrentUsefulness()[x][y];
                            g.drawString(String.format("%.8f", val), xPos + 5, yPos + g.getFontMetrics().getHeight() * 3);
                        }

                        if (showStoredPolicy && storedPolicy != null) {
                            g.setColor(storedPolicyDifferentColor);
                            if(logic.getOptimalActions() != null && storedPolicy[x][y] == logic.getOptimalActions()[x][y]) {
                                g.setColor(storedPolicySameColor);
                            }
                            String act = Action.toString(storedPolicy[x][y]);
                            g.drawString(act, xPos + fieldSize - g.getFontMetrics().stringWidth(act) - 5, yPos + 2 * g.getFontMetrics().getHeight());
                        }

                        if (showStoredUsability && storedUsability != null) {
                            g.setColor(storedUsabilityColor);
                            Double val = storedUsability[x][y];
                            g.drawString(String.format("%.8f", val), xPos + 5, yPos + g.getFontMetrics().getHeight() * 4);
                        }
                    }
                    
                    g.setColor(borderColor);
                    g.drawRect(xPos, yPos, fieldSize, fieldSize);
                }
            }

            g.clearRect(world.getN() * fieldSize + 1, 0, this.getSize().width, this.getSize().height);
            g.clearRect(0, world.getM() * fieldSize + 1, this.getSize().width, this.getSize().height);

            if (edit && selectedField != null) {
                g.setColor(selectedColor);

                int xPos = selectedField.x * fieldSize;
                int yPos = (world.getM() - 1 - selectedField.y) * fieldSize;
                g.drawRect(xPos, yPos, fieldSize, fieldSize);
            }
        }
    }

    public void setShowStoredPolicy(boolean selected) {
        showStoredPolicy = selected;

        this.repaint();
    }

    public void setShowStoredUsability(boolean selected) {
        showStoredUsability = selected;

        this.repaint();
    }

    public void resetStoredPolicy() {
        storedPolicy = null;
    }

    public void resetStoredUsability() {
        storedUsability = null;
    }

    public void setStoredPolicy(Action[][] p) {
        if (p == null || world == null) {
            return;
        }

        if (p.length != world.getN() || p[0] == null || p[0].length != world.getM()) {
            MyLogger.append("Set stored policy error: wrong world size");
            return;
        }

        storedPolicy = p;

        repaint();
    }

    public void setStoredUsability(Double[][] a) {
        if (a == null || world == null) {
            return;
        }

        if (a.length != world.getN() || a[0] == null || a[0].length != world.getM()) {
            MyLogger.append("Set stored usability error: wrong world size");
            return;
        }

        storedUsability = a;

        repaint();
    }

}
