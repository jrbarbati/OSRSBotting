package tasks;

import logger.Logger;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import walker.Walker;

public class Walking extends Task
{
    private Logger log = new Logger(this.getClass());
    private Tile[] path;
    private Walker walker;
    private boolean alreadyWalking;
    private boolean alreadyWalkingReverse;

    public Walking(ClientContext ctx, Tile[] path)
    {
        super(ctx);
        this.path = path;
        this.walker = new Walker(ctx);
        this.alreadyWalking = false;
        this.alreadyWalkingReverse = false;
    }

    @Override
    public boolean isReady()
    {
        return playerIsInactive() && ((nearBeginningOfPath() || alreadyWalking) || (nearEndOfPath() || alreadyWalkingReverse));
    }

    @Override
    public void execute()
    {
        log.info("Executing");

        log.info("Player Not Walking? %b", !playerIsWalking());
        log.info("Destination Doesn't Exist? %b", !destinationExists());
        log.info("Destination is between 7 and 12? %b", destinationIsWithin(7, 12));

        if (!playerIsWalking() || !destinationExists() || destinationIsWithin(7, 12))
        {
            if (nearBeginningOfPath() || alreadyWalking)
            {
                log.info("Walking From Bank To Dwarven Mine");
                this.alreadyWalking = walker.walkPath(path);
                log.info("Still walking? %b", alreadyWalking);
            }

            if (nearEndOfPath() || alreadyWalkingReverse)
            {
                log.info("Walking From Dwarven Mine To Bank");
                this.alreadyWalkingReverse = walker.walkPathReverse(path);
                log.info("Still walking? %b", alreadyWalkingReverse);
            }
        }
    }

    private boolean playerIsInactive()
    {
        return ctx.players.local().animation() == -1;
    }

    private boolean nearBeginningOfPath()
    {
        return nearTile(path[0], 15);
    }

    private boolean nearEndOfPath()
    {
        return nearTile(path[path.length - 1], 15);
    }

    private boolean playerIsWalking()
    {
        return ctx.players.local().inMotion();
    }

    private boolean destinationExists()
    {
        return ctx.movement.destination().equals(Tile.NIL);
    }

    private boolean destinationIsWithin(int minDistance, int maxDistance)
    {
        return ctx.movement.destination().distanceTo(ctx.players.local()) < Random.nextInt(minDistance, maxDistance);
    }

    private boolean nearTile(Tile tile, int maxDistance)
    {
        return ctx.players.local().tile().distanceTo(tile) <= maxDistance;
    }
}
