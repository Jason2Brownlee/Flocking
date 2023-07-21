package flocking;

import javax.swing.JComponent;
import java.awt.Graphics;
import java.awt.Color;
import javax.swing.SwingUtilities;
import java.util.LinkedList;
import java.util.Iterator;
import java.awt.Point;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Jason Brownlee
 * @version 1.0
 */

public class EntityUniverse extends JComponent implements Runnable
{
    //
    // instance variables
    //

    // the number of times to advance the universe per second
    private long drawFrequency = 30;

    private Color backgroundColor = Color.white;

    // whether or not to run the universe
    private boolean runUniverse = true;

    // all swarms managed by the universe
    private LinkedList swarms = new LinkedList();

    private Thread universeThread = null;



    /**
     * the number of times the universe is stepped per second
     * @param frequency
     */
    public void setDrawFrequency(long frequency)
    {
        drawFrequency = frequency;
    }


    /**
     * start the universe
     */
    public void startUniverse()
    {
        runUniverse = true;
        universeThread = new Thread(this);
        universeThread.start();
    }

    /**
     * stop the universe
     */
    public void stopUniverse()
    {
        runUniverse = false;
        universeThread = null;
    }

    /**
     * runs a new thread with all entities
     */
    public void run()
    {
        while(runUniverse)
        {
            // advance the universe
            advanceUniverse();

            try // may throw an InterruptedExpcetion
            {
                SwingUtilities.invokeAndWait(new Runnable()
                {
                    public void run()
                    {
                        repaint(); // draw the universe
                    }
                });
            }
            catch (Exception e)
            {}

            try // may throw an InterruptedExpcetion
            {
                // draw a set number of times a second
                long sleepTime = (1000 / drawFrequency);
                Thread.sleep(sleepTime);
            }
            catch(Exception e)
            {}
        }
    }


    /**
     * advance the universe
     */
    private void advanceUniverse()
    {
        synchronized(swarms)
        {
            Iterator iterator = swarms.iterator();

            while(iterator.hasNext())
            {
                ((Swarm)iterator.next()).moveSwarm(getWidth(), getHeight());
            }
        }
    }


    /**
     * overriden method, draws the population of entities
     * @param g
     */
    protected void paintComponent(Graphics g)
    {
        // clear the background
        g.setColor(backgroundColor);
        g.fillRect(0, 0, getWidth(), getHeight());

        synchronized(swarms)
        {
            Iterator iterator = swarms.iterator();

            while(iterator.hasNext())
            {
                ((Swarm)iterator.next()).drawSwarm(g);
            }
        }
    }

    /**
     * add a swarm to the universe
     * @param aSwarm
     */
    public void addSwarm(Swarm aSwarm)
    {
        synchronized(swarms)
        {
            swarms.add(aSwarm);
        }
    }


    /**
     * get the center of the universe
     * @return
     */
    public Point getCenter()
    {
        // set a new goal for the entities in the swarm
        int centerX = (getWidth() / 2);
        int centerY = (getHeight() / 2);

        return new Point(centerX, centerY);
    }
}