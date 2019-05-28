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

    public Walking(ClientContext ctx, Tile[] path)
    {
        super(ctx);
        this.path = path;
        this.walker = new Walker(ctx);
    }

    @Override
    public boolean isReady()
    {
        return playerIsInactive() && nearBeginningOfPath();
    }

    @Override
    public void execute()
    {
        log.info("Executing");

        if (!playerIsWalking() || !destinationExists() || destinationIsWithin(7, 12))
            walker.walkPath(path);
    }

    private boolean playerIsInactive()
    {
        return ctx.players.local().animation() == -1;
    }

    private boolean nearBeginningOfPath()
    {
        return ctx.players.local().tile().distanceTo(path[0]) < 15;
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
}
