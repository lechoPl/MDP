package gui;

import enums.Action;
import enums.FieldType;
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

    protected Logic logic;

    protected int fieldSize;

    protected int xTranslate = 0;
    protected int yTranslate = 0;

    protected JPanel that = this;
    protected Point selectedField = null;
    protected boolean edit = false;

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

            Color bg = this.getBackground();
            for (int x = 0; x < world.getN(); x++) {

                int xPos = x * fieldSize;

                for (int y = 0; y < world.getM(); y++) {

                    int yPos = (world.getM() - 1 - y) * fieldSize;

                    Field tempField = world.getField(x, y);
                    if (tempField != null) {
                        String fieldSymbol = String.valueOf(FieldType.getChar(tempField.type));
                        String reward = "";
                        g.setColor(bg);
                        switch (tempField.type) {
                            case FORBIDDEN:
                                g.setColor(Color.GRAY);
                                break;
                            case SPECIAL:
                                reward = Double.toString(tempField.reward);
                                break;
                            case TERMINAL:
                                reward = Double.toString(tempField.reward);
                                break;
                        }
                        g.fillRect(xPos, yPos, fieldSize, fieldSize);

                        if (!edit && logic != null && new State(x,y).compareTo(logic.getCurrentAgentState()) == 0) {
                            g.setColor(Color.YELLOW);
                            g.fillOval(xPos, yPos, fieldSize, fieldSize);
                        }
                        
                        g.setColor(Color.BLACK);
                        g.drawString(fieldSymbol, xPos + 5, yPos + g.getFontMetrics().getHeight());
                        g.drawString(reward, xPos + 5, yPos + g.getFontMetrics().getHeight() * 2);
                    }

                    if (!edit && logic != null) {
                        if (logic.getOptimalActions() != null && !world.isForbidden(x, y) && !world.isTermina(new State(x, y))) {
                            String act = Action.toString(logic.getOptimalActions()[x][y]);
                            g.drawString(act, xPos + fieldSize - g.getFontMetrics().stringWidth(act) - 5, yPos + g.getFontMetrics().getHeight());

                        }

                        if (logic.getCurrentUsefulness() != null && !world.isForbidden(x, y) && !world.isTermina(new State(x, y))) {
                            Double val = logic.getCurrentUsefulness()[x][y];
                            g.drawString(String.format("%.8f", val), xPos + 5, yPos + g.getFontMetrics().getHeight() * 3);

                        }
                    }
                    g.setColor(Color.BLACK);
                    g.drawRect(xPos, yPos, fieldSize, fieldSize);
                }
            }

            g.clearRect(world.getN() * fieldSize + 1, 0, this.getSize().width, this.getSize().height);
            g.clearRect(0, world.getM() * fieldSize + 1, this.getSize().width, this.getSize().height);

            if (edit && selectedField != null) {
                g.setColor(Color.RED);

                int xPos = selectedField.x * fieldSize;
                int yPos = (world.getM() - 1 - selectedField.y) * fieldSize;
                g.drawRect(xPos, yPos, fieldSize, fieldSize);
            }
        }
    }

}
