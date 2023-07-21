package flocking;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;
import java.util.LinkedList;
import java.awt.Point;
import java.util.Comparator;
import java.util.Collections;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Jason Brownlee
 * @version 1.0
 */

public class Entity implements Comparator
{
    //
    // constants
    //
    public final static int VELOCITY_VECTOR_X = 0;
    public final static int VELOCITY_VECTOR_Y = 1;
    private final static Random RANDOM = new Random();



    //
    // class variables
    //
    // the width of an entity when its drawn
    private static int width = 2;

    // the height of an entity when its drawn
    private static int height = 2;



    //
    // instance variables
    //
    private int x = 0; // current x position
    private int y = 0; // current y position

    private int old_x = 0;
    private int old_y = 0;

    private double [] velocityVector = new double[2];

    // changeable configuration for all entities
    private EntityConfiguration config = null;



    /**
     * Constructs this entity, selecting a random starting location based on
     * the povided bounds of the universe
     * @param maxWidth
     * @param maxHeight
     */
    public Entity(int maxWidth,
                  int maxHeight,
                  EntityConfiguration aConfig)
    {
        config = aConfig;

        Point p = getRandomPosition(maxWidth, maxHeight);
        old_x = x = p.x;
        old_y = y = p.y;

        // set initial velocity
        velocityVector[VELOCITY_VECTOR_X] = RANDOM.nextDouble() + (double)(RANDOM.nextInt() % 5) + 1.0;
        velocityVector[VELOCITY_VECTOR_Y] = RANDOM.nextDouble() + (double)(RANDOM.nextInt() % 5) + 1.0;
    }


    /**
     *
     * @param g
     */
    public void draw(Graphics g)
    {
        // draw line from old position, to new position
        g.setColor(config.colour);
        g.drawLine(old_x, old_y, x, y);

        // fill in the shape
        g.setColor(config.colour);
        g.fillOval(x,y,width,height);

        // draw neighborhood size
        if(config.drawCollisionBoundary)
        {
            g.setColor(Color.black);

            // personal space
            g.drawOval(x-(config.minimumCollisionDistance/2)+(width/2),
                       y-(config.minimumCollisionDistance/2)+(height/2),
                       config.minimumCollisionDistance,
                       config.minimumCollisionDistance);
        }

    }

    /**
     * move this entity using flocking rules
     *
     * goal can be null
     *
     * @param population
     * @param maxWidth
     * @param maxHeight
     */
    public void move(LinkedList population,
                     int maxWidth,
                     int maxHeight,
                     Point goal)
    {

        if(config.maxNeighbors > 0)
        {
            // get a cllection of neighbors
            Entity[] neighbors = getNeighbors(population);

            if(neighbors != null)
            {
                // get a velocity vector representing a desire to move toward/away from neighbors
                updateVelocityVector(neighbors);
            }
        }

        // limit the speed on the vector
        limitSpeed();

        // ensure entity has a valid position in the universe
        boundsChecking(maxWidth, maxHeight);

        // move towards center
        if(goal != null)
        {
            shiftTowardsGoal(goal);
        }

        // remember old positions
        old_x = x;
        old_y = y;

        // apply the velocity to the entities current position
        x += (int) velocityVector[VELOCITY_VECTOR_X];
        y += (int) velocityVector[VELOCITY_VECTOR_Y];
    }


    /**
     * desire to move towards a goal
     * @param maxWidth
     * @param maxHeight
     */
    private void shiftTowardsGoal(Point p)
    {
        double adjustmentAmount = 0.2;

        // to the left of center
        if(x < p.x)
        {
            // move right
            velocityVector[VELOCITY_VECTOR_X] += adjustmentAmount;
        }
        // to the right of center
        else if(x > p.x)
        {
            // move left
            velocityVector[VELOCITY_VECTOR_X] -= adjustmentAmount;
        }

        // above center
        if(y < p.y)
        {
            // move down
            velocityVector[VELOCITY_VECTOR_Y] += adjustmentAmount;
        }
        // below center
        else if(y > p.y)
        {
            // move up
            velocityVector[VELOCITY_VECTOR_Y] -= adjustmentAmount;
        }
    }


    /**
     * limit the speed of the velocity vector, leaving the directions (sign)
     * unchanged
     *
     */
    private void limitSpeed()
    {
        // check for going too fast right (positive)
        if(velocityVector[VELOCITY_VECTOR_X] > +config.maxSpeed)
        {
            velocityVector[VELOCITY_VECTOR_X] = +config.maxSpeed;
        }
        // check for going too fast left (negative)
        else if(velocityVector[VELOCITY_VECTOR_X] < -config.maxSpeed)
        {
            velocityVector[VELOCITY_VECTOR_X] = -config.maxSpeed;
        }

        // check for going too fast down (positive)
        if(velocityVector[VELOCITY_VECTOR_Y] > +config.maxSpeed)
        {
            velocityVector[VELOCITY_VECTOR_Y] = +config.maxSpeed;
        }
        // check for going too fast up (negative)
        else if(velocityVector[VELOCITY_VECTOR_Y] < -config.maxSpeed)
        {
            velocityVector[VELOCITY_VECTOR_Y] = -config.maxSpeed;
        }
    }


    /**
     * adjust positions to ensure x and y values are 1 unit within the bounds
     * @param maxWidth
     * @param maxHeight
     */
    private void boundsChecking(int maxWidth, int maxHeight)
    {
        int validMinimumX = (1 + width);
        int validMaximumX = ((maxWidth-1) - width);
        int validMinimumY = (1 + height);
        int validMaximumY = ((maxHeight-1) - height);

        // validate x positions
        if(x < validMinimumX)
        {
            velocityVector[VELOCITY_VECTOR_X] = +1;
        }
        else if(x > validMaximumX)
        {
            velocityVector[VELOCITY_VECTOR_X] = -1;
        }

        // validate y positions
        if(y < validMinimumY)
        {
            velocityVector[VELOCITY_VECTOR_Y] = +1;
        }
        else if(y > validMaximumY)
        {
            velocityVector[VELOCITY_VECTOR_Y] = -1;
        }
    }


    /**
     * Evaluates this entities position against all of its neighbors, and
     * updates the velocity vector (direction and speed).
     *
     * @param neighbors
     * @return
     */
    private void updateVelocityVector(Entity [] neighbors)
    {
        double force = 0.0;
        int distance = 0;

        // enumerate all neighbors, evaluation thier distance from self
        for(int i=0; i<neighbors.length; i++)
        {
            Entity current = neighbors[i];

            // calculate distance for this entity to to the i'th entity
            distance = distanceToEntity(current);

            // ensure the distance is meaningful
            if(distance > 0)
            {
                // too close to entity
                if(distance <= config.minimumCollisionDistance)
                {
                    // move away from collision
                    force = (config.acceleration * -1.0);
                }
                // far enough away from entity
                else
                {
                    // move towards entity
                    force = (config.acceleration * +1.0);
                }

                // apply force to x axis
                if(x < current.x)
                {
                    velocityVector[VELOCITY_VECTOR_X] += force; // apply to the right
                }
                else if(x > current.x)
                {
                    velocityVector[VELOCITY_VECTOR_X] -= force; // apply to the left
                }

                // apply force to y axis
                if(y < current.y)
                {
                    velocityVector[VELOCITY_VECTOR_Y] += force; // apply down
                }
                else if(y > current.y)
                {
                    velocityVector[VELOCITY_VECTOR_Y] -= force; // apply up
                }
            }
        }
    }


    /**
     * get a random position in the universe
     * @param maxWidth
     * @param maxHeight
     * @return
     */
    private Point getRandomPosition(int maxWidth, int maxHeight)
    {
        x = (Math.abs(RANDOM.nextInt()) % maxWidth);
        y = (Math.abs(RANDOM.nextInt()) % maxHeight);
        return new Point(x,y);
    }


    /**
     * get a collection of all neighbors for this entity from the entire population
     *
     * @param population : returns null if entity has no neighbors
     * @return
     */
    private Entity [] getNeighbors(LinkedList population)
    {
        // sort the population by their distance from this entity
        // where the closest entities are at the top of the list
        Collections.sort(population, this);

        int numNeighbors = Math.min(population.size(), config.maxNeighbors);

        // take the top 'few' as neighbors
        Entity [] neighbors = new Entity[numNeighbors];

        for(int i=0; i<neighbors.length; i++)
        {
            neighbors[i] = (Entity) population.get(i);
        }

        return neighbors;
    }



    /**
     * Calculate the Euclidean distance from this entity to the provided entity.
     *
     * @param ent
     * @return
     */
    private int distanceToEntity(Entity ent)
    {
        // calculate the difference between the position of this entity and the
        // provided entity
        int xDifference = (x - ent.x);
        int yDifference = (y - ent.y);

        // sum the squares of the difference
        int result = (xDifference * xDifference) + (yDifference * yDifference);

        // take square root to convert back to pixels
        result = (int) Math.sqrt(result);

        return result;
    }

    /**
     * compare two objects using distance from self to both entities as the measure
     * where closer objects are bubbled to the top
     *
     * First is less than second: return a negative
     * First is equal to second return zero
     * First is more than second return a positive
     *
     * @param o1
     * @param o2
     * @return
     */
    public int compare(Object o1, Object o2)
    {
        int dist1 = distanceToEntity((Entity) o1);
        int dist2 = distanceToEntity((Entity) o2);

        if(dist1 < dist2)
        {
            return -1; // first is smaller
        }
        else if(dist1 > dist2)
        {
            return +1; // first is larger
        }

        // equal distance from self
        return 0;
    }


    /**
     * Indicates whether some other object is "equal to" this Comparator
     * @param obj
     * @return
     */
    public boolean equals(Object obj)
    {
        Entity ent = (Entity) obj;
        return (x == ent.x && y == ent.y);
    }

}