package flocking;

import java.awt.Graphics;
import java.util.Iterator;
import java.util.LinkedList;
import java.awt.Point;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Jason Brownlee
 * @version 1.0
 */

public class Swarm
{
    //
    // instance variables
    //

    // configuration for all entities in this swarm
    private EntityConfiguration swarmConfig = new EntityConfiguration();

    // the population of entities
    private LinkedList population = new LinkedList();

    // goal for the swarm
    private Point goal = null;


    /**
     * set the goal for this swarm
     * @param p
     */
    public synchronized void setGoal(Point p)
    {
        goal = p;
    }


    /**
     * set the configuration for this swarm
     * @param config
     */
    public synchronized void setConfiguration(EntityConfiguration config)
    {
        swarmConfig.populationConfig(config);
    }


    /**
     * give each entity a chance to move
     */
    public synchronized void moveSwarm(int universeWidth,
                                       int universeHeight)
    {
        // move all entities in the population
        if(! population.isEmpty())
        {
            // get a cloned copy of the reference list to all entities
            // entities sort the list, thus change the order, giving them a cloned list
            // means they can change the order as much as they want, leaving the iterator
            // to operate in the same order every invocation
            LinkedList referenceList = (LinkedList) population.clone();

            Iterator iterator = population.iterator();

            while(iterator.hasNext())
            {
                ((Entity)iterator.next()).move(referenceList, universeWidth, universeHeight, goal);
            }
        }
    }


    /**
     * draw all entities in the swarm
     * @param g
     */
    public synchronized void drawSwarm(Graphics g)
    {
        // only draw when we have a population
        if(! population.isEmpty())
        {
            Iterator iterator = population.iterator();

            while(iterator.hasNext())
            {
                ((Entity)iterator.next()).draw(g);
            }
        }
    }


    /**
     * increase or decrease the size of the population dynamically
     */
    public synchronized void setPopulationSize(int size,
                                               int universeWidth,
                                               int universeHeight)
    {
        // check if anything needs to be done
        if(population.size() != size)
        {
            // get a lock on the population
            synchronized(population)
            {
                // how much are we out
                int difference = Math.abs(population.size() - size);

                // check if more entities are needed
                if (population.size() < size)
                {
                    for(int i=0; i<difference; i++)
                    {
                        population.add(new Entity(universeWidth, universeHeight, swarmConfig));
                    }
                }
                // entities need to be removed
                else
                {
                    for(int i=0; i<difference; i++)
                    {
                        population.remove(0);
                    }
                }
            }
        }
    }

    /**
     *
     * @return
     */
    public int getPopulationSize()
    {
        return population.size();
    }
}