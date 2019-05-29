package tasks;

import logger.Logger;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.TilePath;

public class Walking extends Task
{
    private Logger log = new Logger(this.getClass());
    private TilePath path;

    public Walking(ClientContext ctx, Tile[] path)
    {
        super(ctx);
        this.path = new TilePath(ctx, path);
    }

    @Override
    public boolean isReady()
    {
        return nearBeginningOfPath() || nearEndOfPath();
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
            if (nearBeginningOfPath())
            {
                log.info("Walking From Bank To Dwarven Mine");
                path.traverse();
            }

            if (nearEndOfPath())
            {
                log.info("Walking From Dwarven Mine To Bank");
                path.reverse().traverse();
            }
        }
    }

    private boolean nearBeginningOfPath()
    {
        return nearTile(path.start(), 15);
    }

    private boolean nearEndOfPath()
    {
        return nearTile(path.end(), 15);
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
