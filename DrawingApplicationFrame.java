
/*
Michael Tufillaro
Compsci 221
10/19/2023
Purpose: To create a GUI that acts as a drawing application, with different options for color, size, gradient, etc.
*/

package java2ddrawingapplication;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class DrawingApplicationFrame extends JFrame
{

   
    private final JPanel topPanel;
    private final JPanel firstLine;
    private final JPanel secondLine;
    
    //creates variables for first line of top panel
    private final JLabel shapeLabel;
    private static final String[] shapes = {"Line", "Oval", "Rectangle"};
    private final JComboBox<String> shapeBox;
    private final JButton colorButton1;
    private Color color1 = Color.BLACK;
    private final JButton colorButton2;
    private Color color2 = Color.BLACK;
    private final JButton undoButton;
    private final JButton clearButton;
    
    //creates variables for second line of top panel
    private final JLabel optionLabel;
    private final JCheckBox fillBox;
    private final JCheckBox gradientBox;
    private final JCheckBox dashBox;
    private final JLabel lineLabel;
    private final JSpinner lineSpinner;
    private final JLabel dashLabel;
    private final JSpinner dashSpinner;
    
    //creates variables for the drawing panel and status panel
    private final DrawPanel drawPanel;
    private final JLabel statusLabel;
    private final JPanel statusPanel;
    private Point startPoint;
    private Point endPoint;
    private ArrayList<MyShapes> shapeList = new ArrayList<MyShapes>();
    private MyShapes shape;
    private boolean firstDrag;
    
    

    
    public DrawingApplicationFrame()
    {
        super("Java 2D Drawings");
        //creates top panel and its first line and sets their colors to cyan
        topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(2,1));
        firstLine = new JPanel();
        topPanel.setBackground(Color.cyan);
        firstLine.setBackground(Color.cyan);
        // creates and adds widgets to firstLine panel
        shapeLabel = new JLabel("Shape:");
        shapeBox = new JComboBox(shapes);
        colorButton1 = new JButton("1st Color...");
        colorButton2 = new JButton("2nd Color...");
        undoButton = new JButton("Undo");
        clearButton = new JButton("Clear");
        
        firstLine.add(shapeLabel);
        firstLine.add(shapeBox);
        firstLine.add(colorButton1);
        firstLine.add(colorButton2);
        firstLine.add(undoButton);
        firstLine.add(clearButton);
        topPanel.add(firstLine);
        
        //creates second line of top panel
        secondLine = new JPanel();
        secondLine.setBackground(Color.cyan);
        // creates and adds widgets to secondLine panel
        optionLabel = new JLabel("Options:");
        fillBox = new JCheckBox("Filled");
        gradientBox = new JCheckBox("Use Gradient");
        dashBox = new JCheckBox("Dashed");
        lineLabel = new JLabel("Line Width:");
        lineSpinner = new JSpinner();
        lineSpinner.setValue(5);
        dashLabel = new JLabel("Dash Length:");
        dashSpinner = new JSpinner();
        dashSpinner.setValue(5);
        
        secondLine.add(optionLabel);
        secondLine.add(fillBox);
        secondLine.add(gradientBox);
        secondLine.add(dashBox);
        secondLine.add(lineLabel);
        secondLine.add(lineSpinner);
        secondLine.add(dashLabel);
        secondLine.add(dashSpinner);
        topPanel.add(secondLine);
        
        // add topPanel to North, drawPanel to Center, and statusLabel to South
        add(topPanel, BorderLayout.NORTH);
        
        drawPanel = new DrawPanel();
        add(drawPanel, BorderLayout.CENTER);
        
        statusPanel = new JPanel();
        statusLabel = new JLabel("(0, 0)");
        statusPanel.setBackground(Color.LIGHT_GRAY);
        statusPanel.setLayout(new BorderLayout());
        statusPanel.add(statusLabel, BorderLayout.WEST);
        add(statusPanel, BorderLayout.SOUTH);

        //create and add handlers for all 4 buttons
        UndoHandler undoHandler = new UndoHandler();
        undoButton.addActionListener(undoHandler);
        ClearHandler clearHandler = new ClearHandler();
        clearButton.addActionListener(clearHandler);
        ColorButtonHandler colorHandler = new ColorButtonHandler();
        colorButton1.addActionListener(colorHandler);
        colorButton2.addActionListener(colorHandler);
    }

    //handlers for undo/clear buttons
    private class UndoHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent event)
        {         
            if (!shapeList.isEmpty())
            {
                shapeList.remove(shapeList.size() - 1);
                repaint();
            }
        }
    }
    private class ClearHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent event)
        {
            if (!shapeList.isEmpty())
            {
                shapeList.clear();
                repaint();
            }
        }
    }
    //handler for color selection buttons
    private class ColorButtonHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent event)
        {
            //depending on which button is pressed, a different color is changed
            if (event.getSource() == colorButton1)
            {
              //pops up a window that lets the user choose a color
              color1 = JColorChooser.showDialog(DrawingApplicationFrame.this, "Choose a color for Color 1:", color1);
              if (color1 == null)
              {
                  //defaults to black if no color was picked
                  color1 = Color.BLACK;
              }
            }
            else if (event.getSource() == colorButton2)
            {
              color2 = JColorChooser.showDialog(DrawingApplicationFrame.this, "Choose a color for Color 2:", color2);
              if (color2 == null)
              {
                  color2 = Color.BLACK;
              }
            }
        }
    }
    
    // Create a private inner class for the DrawPanel.
    private class DrawPanel extends JPanel
    {

        public DrawPanel()
        {
            //creates a handler for any mouse events such as clicking, dragging, or moving.
            MouseHandler mouseHandler = new MouseHandler();
            addMouseListener(mouseHandler);
            addMouseMotionListener(mouseHandler);
            
        }

        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            //loops through and draws all shapes in the shapeList
            for (MyShapes shape: shapeList)
            {
                shape.draw(g2d);
            }

        }


        private class MouseHandler extends MouseAdapter implements MouseMotionListener
        {
            
            public void mousePressed(MouseEvent event)
            {
                //when the mouse is pressed, the coordinates get assigned to start point
                startPoint = new Point(event.getX(), event.getY());
                firstDrag = true;
            }

            public void mouseReleased(MouseEvent event)
            {
                //when the mouse is released after being pressed, the paint method is called
                repaint();
            }
 
            @Override
            public void mouseDragged(MouseEvent event)
            {
                
                //constantly updates the status label to display the mouse's current coordinates
                statusLabel.setText(String.format("(%d, %d)", event.getX(), event.getY()));
                //endpoint is set equal to current coordinates
                endPoint = new Point(event.getX(), event.getY());
                
                //Sets the paint/stroke depending on the options at the top of the frame
                float[] dashLength = {(float)(Integer)dashSpinner.getValue()};
                int lineWidth = (Integer)lineSpinner.getValue();
                
                Paint paint;
                if (gradientBox.isSelected())
                {
                    paint = new GradientPaint(0, 0, color1, 50, 50, color2, true);
                }
                else
                {
                    paint = new GradientPaint(0, 0, color1, 50, 50, color1, true);
                }
                
                Stroke stroke;
                if(dashBox.isSelected())
                {
                    stroke = new BasicStroke(lineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, dashLength, 0);
                } 
                else
                {
                    stroke = new BasicStroke(lineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                }
                
                String shapeType = (String)shapeBox.getSelectedItem();
                //take paint, stroke, points, and shape type and create a corresponding myShape object
                
                if (shapeType == "Line")
                {
                    shape = new MyLine(startPoint, endPoint, paint, stroke);
                    
                }
                else if (shapeType == "Rectangle")
                {
                    shape = new MyRectangle(startPoint, endPoint, paint, stroke, fillBox.isSelected());
                }
                else
                {
                   shape = new MyOval(startPoint, endPoint, paint, stroke, fillBox.isSelected());
                }
                
                //whenever the mouse is dragged, a shape is constantly painted, deleted, and repainted to allow for the shape to appear to move with the cursor
                if (!firstDrag)
                {
                    shapeList.remove(shapeList.size()-1); 
                }
                shapeList.add(shape);
                repaint();
                firstDrag = false;
            }

            @Override
            public void mouseMoved(MouseEvent event)
            {
                //constantly updates the status label to display the mouse's current coordinates
                statusLabel.setText(String.format("(%d, %d)", event.getX(), event.getY()));
            }
            @Override public void mouseExited(MouseEvent event)
            {
                //when the cursor leaves the draw panel, the status label is reset to (0, 0)
                statusLabel.setText("(0, 0)");
            }
        }
    }
}
