package flocking;

import java.awt.Color;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Jason Brownlee
 * @version 1.0
 */

public class EntityConfiguration
{
    // the maximum number of neighbors an entity can have
    int maxNeighbors = 4;

    // the maximum speed that an entity can move in any direction
    double maxSpeed = 5.0;

    // the acceleration applied to an entities desire to move toward or away from something
    double acceleration = 0.30;

    // the minimim distance an entity wants to be from other entities
    int minimumCollisionDistance = 10;

    // the colour all entites have
    Color colour = Color.blue;

    // whether or not to draw the collision boundary around each entity
    boolean drawCollisionBoundary = false;



    /**
     * set the configuration of from the provided configuration
     * @param config
     */
    public void populationConfig(EntityConfiguration config)
    {
        maxNeighbors             = config.maxNeighbors;
        maxSpeed                 = config.maxSpeed;
        acceleration             = config.acceleration;
        minimumCollisionDistance = config.minimumCollisionDistance;
        colour                   = config.colour;
        drawCollisionBoundary    = config.drawCollisionBoundary;
    }

}