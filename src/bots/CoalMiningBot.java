package bots;

import logger.Logger;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import tasks.Banking;
import tasks.Mining;
import tasks.Task;
import tasks.Walking;

import java.util.ArrayList;
import java.util.List;

@Script.Manifest(name="Mining", description = "Automatically Mines Coal from Dwarven Mine", properties = "author=VirHircinus; topic=999; client=4;")
public class CoalMiningBot extends PollingScript<ClientContext>
{
    private static final int[] COAL_ROCK_IDS = {11366, 11367};
    private static final int COAL_INVENTORY_ID = 453;
    private static final Tile[] path = {new Tile(3012, 3356, 0), new Tile(3022, 3361, 0), new Tile(3031, 3366, 0), new Tile(3041, 3368, 0), new Tile(3051, 3369, 0), new Tile(3060, 3374, 0), new Tile(3058, 9777, 0)};

    private List<Task> tasks = new ArrayList<Task>();
    private Logger log = new Logger(this.getClass());

    @Override
    public void start()
    {
        log.info("Started");

        tasks.add(new Mining(ctx, COAL_ROCK_IDS, System.currentTimeMillis()));
        tasks.add(new Banking(ctx, COAL_INVENTORY_ID));

        //TODO: Debug and figure out walking...
//        tasks.add(new Walking(ctx, path));
    }

    @Override
    public void poll()
    {
        for (Task task : tasks)
        {
            if (ctx.controller.isStopping() || ctx.controller.isSuspended())
                break;

            if (task.isReady())
            {
                task.execute();
                break;
            }
        }
    }

    @Override
    public void suspend()
    {
        log.info("Paused");
    }

    @Override
    public void resume()
    {
        log.info("Resumed");
    }

    @Override
    public void stop()
    {
        log.info("Stopped");
    }
}
