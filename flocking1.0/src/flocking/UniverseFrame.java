package flocking;

import javax.swing.JFrame;
import java.awt.HeadlessException;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JSlider;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Jason Brownlee
 * @version 1.0
 */

public class UniverseFrame extends JFrame implements ActionListener, ChangeListener
{
    //
    // constants
    //
    public final static String TITLE = "Flocking - Jason Brownlee";



    //
    // instance variables
    //

    private EntityUniverse universe = null;
    private Swarm swarm = null;
    private EntityConfiguration config = null;
    private boolean desireCenter = false;

    private int initialStepsPerSecond = 40;
    private int initialEntities = 50;

    // size of frame
    private Dimension frameSize = new Dimension(640, 480);

    // gui
    private JSlider stepsPerSecondSlider = null;
    private JSlider totalEntitiesSlider = null;
    private JSlider totalNeighborsSlider = null;
    private JSlider maximumSpeedSlider = null;
    private JSlider accelerationSlider = null;
    private JSlider minimumDistanceSlider = null;
    private JCheckBox drawCollisionCheckbox = null;
    private JCheckBox desireCenterCheckbox = null;

    private JLabel stepsPerSecondValueLabel = null;
    private JLabel totalEntitiesValueLabel = null;
    private JLabel totalNeighborsValueLabel = null;
    private JLabel maximumSpeedValueLabel = null;
    private JLabel accelerationValueLabel = null;
    private JLabel minimumDistanceValueLabel = null;

    private JButton selectColour = null;
    private JButton stopButton = null;
    private JButton startButton = null;


    /**
     * constructor
     */
    public UniverseFrame()
    {
        super(TITLE);
        prepareGUI();

        setSize(frameSize);
        addWindowListener(new WindowCloser());
        addComponentListener(new MyComponentAdapter());
        setVisible(true);

        // kick it off
        swarm.setPopulationSize(initialEntities, universe.getWidth(), universe.getHeight());
        universe.setDrawFrequency(initialStepsPerSecond);
        universe.startUniverse();
    }


    /**
     * initialise gui components and place them on the frame
     */
    private void prepareGUI()
    {
        config = new EntityConfiguration();
        universe = new EntityUniverse();
        swarm = new Swarm();
        universe.addSwarm(swarm);

        JPanel sliders = prepareConfigurationPanel();
        JPanel control = prepreControlPanel();

        stopButton.setEnabled(true);
        startButton.setEnabled(false);

        Container c = getContentPane();
        c.setLayout(new BorderLayout());

        c.add(universe, BorderLayout.CENTER);
        c.add(sliders, BorderLayout.SOUTH);
        c.add(control, BorderLayout.NORTH);
    }


    /**
     * prepare the control panel
     * @return
     */
    private JPanel prepreControlPanel()
    {
        JPanel control = new JPanel();

        stopButton = new JButton("Stop");
        startButton = new JButton("Start");
        selectColour = new JButton("Select Colour");

        stopButton.addActionListener(this);
        startButton.addActionListener(this);
        selectColour.addActionListener(this);

        selectColour.setForeground(config.colour);

        control.add(startButton);
        control.add(stopButton);
        control.add(selectColour);

        return control;
    }

    /**
     * prepare the configuration panel
     * @return
     */
    private JPanel prepareConfigurationPanel()
    {
        JPanel master = new JPanel();
        master.setLayout(new GridBagLayout());

        totalEntitiesSlider = new JSlider(0, 100, initialEntities);
        stepsPerSecondSlider = new JSlider(20, 100, initialStepsPerSecond);
        int intitalNeigh = (int) Math.round(((double)config.maxNeighbors/(double)initialEntities) * 100.0);
        totalNeighborsSlider = new JSlider(1, 100, intitalNeigh);
        maximumSpeedSlider = new JSlider(1, 100, (int)(config.maxSpeed*10));
        accelerationSlider = new JSlider(1, 100, (int)(config.acceleration*100));
        minimumDistanceSlider = new JSlider(5, 50, config.minimumCollisionDistance);
        drawCollisionCheckbox = new JCheckBox("Draw Personal Space Boundary:", config.drawCollisionBoundary);
        desireCenterCheckbox = new JCheckBox("Desire Center", desireCenter);

        stepsPerSecondSlider.addChangeListener(this);
        totalEntitiesSlider.addChangeListener(this);
        totalNeighborsSlider.addChangeListener(this);
        maximumSpeedSlider.addChangeListener(this);
        accelerationSlider.addChangeListener(this);
        minimumDistanceSlider.addChangeListener(this);
        drawCollisionCheckbox.addActionListener(this);
        desireCenterCheckbox.addActionListener(this);

        stepsPerSecondValueLabel = new JLabel(Integer.toString(initialStepsPerSecond));
        totalEntitiesValueLabel = new JLabel(Integer.toString(totalEntitiesSlider.getValue()));
        totalNeighborsValueLabel = new JLabel(Integer.toString(config.maxNeighbors));
        maximumSpeedValueLabel = new JLabel(Double.toString(config.maxSpeed));
        accelerationValueLabel = new JLabel(Double.toString(config.acceleration));
        minimumDistanceValueLabel = new JLabel(Integer.toString(config.minimumCollisionDistance));

        stepsPerSecondValueLabel.setForeground(Color.gray);
        totalEntitiesValueLabel.setForeground(Color.gray);
        totalNeighborsValueLabel.setForeground(Color.gray);
        maximumSpeedValueLabel.setForeground(Color.gray);
        accelerationValueLabel.setForeground(Color.gray);
        minimumDistanceValueLabel.setForeground(Color.gray);

        JLabel stepsPerSecondLabel = new JLabel("Steps Per-Second: ");
        JLabel totalEntitiesLabel = new JLabel("Total Entities: ");
        JLabel totalNeighborsLabel = new JLabel("Total Neighbors: ");
        JLabel maximumSpeedLabel = new JLabel("Maximum Speed: ");
        JLabel accelerationLabel = new JLabel("Acceleration: ");
        JLabel minimumDistanceLabel = new JLabel("Personal Space Boundary: ");


        // total entities
        master.add(stepsPerSecondLabel, new GridBagConstraints(
                      0,   // grid x
                      0,   // grid y
                      1,   // grid width
                      1,   // grid height
                      0.0, // weight x
                      0.0, // wight y
                      GridBagConstraints.WEST, // anchor
                      GridBagConstraints.NONE,   // fill
                      new Insets(2, 2, 2, 2),   // insets
                      0,   //ipad x
                      0));  // ipad y
        master.add(stepsPerSecondValueLabel, new GridBagConstraints(
                      1, // grid x
                      0, // grid y
                      1, // grid width
                      1, // grid height
                      0.0, // weight x
                      0.0, // wight y
                      GridBagConstraints.WEST, // anchor
                      GridBagConstraints.NONE, // fill
                      new Insets(2, 2, 2, 2), // insets
                      0, //ipad x
                      0));  // ipad y
        master.add(stepsPerSecondSlider, new GridBagConstraints(
                      2, // grid x
                      0, // grid y
                      2, // grid width
                      1, // grid height
                      0.0, // weight x
                      0.0, // wight y
                      GridBagConstraints.WEST, // anchor
                      GridBagConstraints.HORIZONTAL, // fill
                      new Insets(2, 2, 2, 2), // insets
                      0, //ipad x
                      0));  // ipad y

        // total entities
        master.add(totalEntitiesLabel, new GridBagConstraints(
                      0,   // grid x
                      1,   // grid y
                      1,   // grid width
                      1,   // grid height
                      0.0, // weight x
                      0.0, // wight y
                      GridBagConstraints.WEST, // anchor
                      GridBagConstraints.NONE,   // fill
                      new Insets(2, 2, 2, 2),   // insets
                      0,   //ipad x
                      0));  // ipad y
        master.add(totalEntitiesValueLabel, new GridBagConstraints(
                      1, // grid x
                      1, // grid y
                      1, // grid width
                      1, // grid height
                      0.0, // weight x
                      0.0, // wight y
                      GridBagConstraints.WEST, // anchor
                      GridBagConstraints.NONE, // fill
                      new Insets(2, 2, 2, 2), // insets
                      0, //ipad x
                      0));  // ipad y
        master.add(totalEntitiesSlider, new GridBagConstraints(
                      2, // grid x
                      1, // grid y
                      2, // grid width
                      1, // grid height
                      0.0, // weight x
                      0.0, // wight y
                      GridBagConstraints.WEST, // anchor
                      GridBagConstraints.HORIZONTAL, // fill
                      new Insets(2, 2, 2, 2), // insets
                      0, //ipad x
                      0));  // ipad y

        // total neighbors
        master.add(totalNeighborsLabel, new GridBagConstraints(
                      0,   // grid x
                      2,   // grid y
                      1,   // grid width
                      1,   // grid height
                      0.0, // weight x
                      0.0, // wight y
                      GridBagConstraints.WEST, // anchor
                      GridBagConstraints.NONE,   // fill
                      new Insets(2, 2, 2, 2),   // insets
                      0,   //ipad x
                      0));  // ipad y
        master.add(totalNeighborsValueLabel, new GridBagConstraints(
                      1, // grid x
                      2, // grid y
                      1, // grid width
                      1, // grid height
                      0.0, // weight x
                      0.0, // wight y
                      GridBagConstraints.WEST, // anchor
                      GridBagConstraints.NONE, // fill
                      new Insets(2, 2, 2, 2), // insets
                      0, //ipad x
                      0));  // ipad y
        master.add(totalNeighborsSlider, new GridBagConstraints(
                      2, // grid x
                      2, // grid y
                      2, // grid width
                      1, // grid height
                      0.0, // weight x
                      0.0, // wight y
                      GridBagConstraints.WEST, // anchor
                      GridBagConstraints.HORIZONTAL, // fill
                      new Insets(2, 2, 2, 2), // insets
                      0, //ipad x
                      0));  // ipad y

        // max speed
        master.add(maximumSpeedLabel, new GridBagConstraints(
                      0,   // grid x
                      3,   // grid y
                      1,   // grid width
                      1,   // grid height
                      0.0, // weight x
                      0.0, // wight y
                      GridBagConstraints.WEST, // anchor
                      GridBagConstraints.NONE,   // fill
                      new Insets(2, 2, 2, 2),   // insets
                      0,   //ipad x
                      0));  // ipad y
        master.add(maximumSpeedValueLabel, new GridBagConstraints(
                      1, // grid x
                      3, // grid y
                      1, // grid width
                      1, // grid height
                      0.0, // weight x
                      0.0, // wight y
                      GridBagConstraints.WEST, // anchor
                      GridBagConstraints.NONE, // fill
                      new Insets(2, 2, 2, 2), // insets
                      0, //ipad x
                      0));  // ipad y
        master.add(maximumSpeedSlider, new GridBagConstraints(
                      2, // grid x
                      3, // grid y
                      2, // grid width
                      1, // grid height
                      0.0, // weight x
                      0.0, // wight y
                      GridBagConstraints.WEST, // anchor
                      GridBagConstraints.HORIZONTAL, // fill
                      new Insets(2, 2, 2, 2), // insets
                      0, //ipad x
                      0));  // ipad y

        // acceleration
        master.add(accelerationLabel, new GridBagConstraints(
                      0,   // grid x
                      4,   // grid y
                      1,   // grid width
                      1,   // grid height
                      0.0, // weight x
                      0.0, // wight y
                      GridBagConstraints.WEST, // anchor
                      GridBagConstraints.NONE,   // fill
                      new Insets(2, 2, 2, 2),   // insets
                      0,   //ipad x
                      0));  // ipad y
        master.add(accelerationValueLabel, new GridBagConstraints(
                      1, // grid x
                      4, // grid y
                      1, // grid width
                      1, // grid height
                      0.0, // weight x
                      0.0, // wight y
                      GridBagConstraints.WEST, // anchor
                      GridBagConstraints.NONE, // fill
                      new Insets(2, 2, 2, 2), // insets
                      0, //ipad x
                      0));  // ipad y
        master.add(accelerationSlider, new GridBagConstraints(
                      2, // grid x
                      4, // grid y
                      2, // grid width
                      1, // grid height
                      0.0, // weight x
                      0.0, // wight y
                      GridBagConstraints.WEST, // anchor
                      GridBagConstraints.HORIZONTAL, // fill
                      new Insets(2, 2, 2, 2), // insets
                      0, //ipad x
                      0));  // ipad y

        // min distance
        master.add(minimumDistanceLabel, new GridBagConstraints(
                      0,   // grid x
                      5,   // grid y
                      1,   // grid width
                      1,   // grid height
                      0.0, // weight x
                      0.0, // wight y
                      GridBagConstraints.WEST, // anchor
                      GridBagConstraints.NONE,   // fill
                      new Insets(2, 2, 2, 2),   // insets
                      0,   //ipad x
                      0));  // ipad y
        master.add(minimumDistanceValueLabel, new GridBagConstraints(
                      1, // grid x
                      5, // grid y
                      1, // grid width
                      1, // grid height
                      0.0, // weight x
                      0.0, // wight y
                      GridBagConstraints.WEST, // anchor
                      GridBagConstraints.NONE, // fill
                      new Insets(2, 2, 2, 2), // insets
                      0, //ipad x
                      0));  // ipad y
        master.add(minimumDistanceSlider, new GridBagConstraints(
                      2, // grid x
                      5, // grid y
                      2, // grid width
                      1, // grid height
                      0.0, // weight x
                      0.0, // wight y
                      GridBagConstraints.WEST, // anchor
                      GridBagConstraints.HORIZONTAL, // fill
                      new Insets(2, 2, 2, 2), // insets
                      0, //ipad x
                      0));  // ipad y

        // draw distance
        master.add(drawCollisionCheckbox, new GridBagConstraints(
                      0,   // grid x
                      6,   // grid y
                      2,   // grid width
                      1,   // grid height
                      0.0, // weight x
                      0.0, // wight y
                      GridBagConstraints.WEST, // anchor
                      GridBagConstraints.NONE,   // fill
                      new Insets(2, 2, 2, 2),   // insets
                      0,   //ipad x
                      0));  // ipad y
        // draw desire center
        master.add(desireCenterCheckbox, new GridBagConstraints(
                      2,   // grid x
                      6,   // grid y
                      2,   // grid width
                      1,   // grid height
                      0.0, // weight x
                      0.0, // wight y
                      GridBagConstraints.WEST, // anchor
                      GridBagConstraints.NONE,   // fill
                      new Insets(2, 2, 2, 2),   // insets
                      0,   //ipad x
                      0));  // ipad y


        return master;
    }



    /**
     * implementation of the actionlistener interface
     * @param ae
     */
    public void actionPerformed(ActionEvent ae)
    {
        Object src = ae.getSource();

        if(src == stopButton)
        {
            universe.stopUniverse();
            stopButton.setEnabled(false);
            startButton.setEnabled(true);
        }
        else if(src == startButton)
        {
            universe.startUniverse();
            stopButton.setEnabled(true);
            startButton.setEnabled(false);
        }
        else if(src == selectColour)
        {
            Color c = JColorChooser.showDialog(this, "Select Entity Color", config.colour);;

            if(c!=null)
            {
                config.colour = c;
                selectColour.setForeground(config.colour);

                // update the configuration
                swarm.setConfiguration(config);
            }
        }
        else if(src == drawCollisionCheckbox)
        {
            config.drawCollisionBoundary = (drawCollisionCheckbox.isSelected());

            // update the configuration
            swarm.setConfiguration(config);
        }
        else if(src == desireCenterCheckbox)
        {
            desireCenter = desireCenterCheckbox.isSelected();

            if(desireCenter)
            {
                swarm.setGoal(universe.getCenter());
            }
            else
            {
                swarm.setGoal(null);
            }
        }
    }


    /**
     * implementation of the changelistener interface
     * @param ce
     */
    public void stateChanged(ChangeEvent ce)
    {
        Object src = ce.getSource();

        if(src == stepsPerSecondSlider)
        {
            universe.setDrawFrequency(stepsPerSecondSlider.getValue());
            stepsPerSecondValueLabel.setText(Integer.toString(stepsPerSecondSlider.getValue()));
            return; // do not update config
        }
        else if(src == totalEntitiesSlider)
        {
            swarm.setPopulationSize(totalEntitiesSlider.getValue(), universe.getWidth(), universe.getHeight());
            totalEntitiesValueLabel.setText(Integer.toString(totalEntitiesSlider.getValue()));

            // check if neighborhood is bigger than populationsize
           if(config.maxNeighbors > totalEntitiesSlider.getValue())
           {
               config.maxNeighbors = totalEntitiesSlider.getValue();
               totalNeighborsValueLabel.setText(Integer.toString(config.maxNeighbors));
           }

           // rescale the neighborhood value
           int intitalNeigh = (int) Math.round(((double)config.maxNeighbors/(double)totalEntitiesSlider.getValue()) * 100.0);
           totalNeighborsSlider.setValue(intitalNeigh);
        }
        else if(src == totalNeighborsSlider)
        {
            int value = totalNeighborsSlider.getValue();
            double total = ((double)swarm.getPopulationSize() * ((double)value/100.0));
            config.maxNeighbors = (int) Math.round(total);
            totalNeighborsValueLabel.setText(Integer.toString(config.maxNeighbors));
        }
        else if(src == maximumSpeedSlider)
        {
            config.maxSpeed = (maximumSpeedSlider.getValue() / 10.0);
            maximumSpeedValueLabel.setText(Double.toString(config.maxSpeed));
        }
        else if(src == accelerationSlider)
        {
            config.acceleration = ((double)accelerationSlider.getValue() / 100.0);
            accelerationValueLabel.setText(Double.toString(config.acceleration));
        }
        else if(src == minimumDistanceSlider)
        {
            config.minimumCollisionDistance = minimumDistanceSlider.getValue();
            minimumDistanceValueLabel.setText(Integer.toString(config.minimumCollisionDistance));
        }

        // update the configuration
        swarm.setConfiguration(config);
    }


    /**
     * <p>Title: </p>
     * <p>Description: </p>
     * <p>Copyright: Copyright (c) 2003</p>
     * <p>Company: </p>
     * @author Jason Brownlee
     * @version 1.0
     */
    protected class WindowCloser extends WindowAdapter
    {
        public void windowClosing(WindowEvent we)
        {
            universe.stopUniverse();
            System.exit(0);
        }
    }

    /**
     * <p>Title: </p>
     * <p>Description: </p>
     * <p>Copyright: Copyright (c) 2003</p>
     * <p>Company: </p>
     * @author Jason Brownlee
     * @version 1.0
     */
    protected class MyComponentAdapter extends ComponentAdapter
    {
        /**
         * called each time the frame is resized
         * @param e
         */
        public void componentResized(ComponentEvent e)
        {
            if(desireCenter)
            {
                swarm.setGoal(universe.getCenter());
            }
        }
    }

}