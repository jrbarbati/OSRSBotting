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
    private final static int[] COAL_ROCK_IDS = {11366, 11367};
    private static final int COAL_INVENTORY_ID = 453;
    private static final Tile[] dwarvenMineToBank = {new Tile(3058, 9776, 0), new Tile(3061, 3376, 0), new Tile(3042, 3369, 0), new Tile(3022, 3367, 0)};
    private static final Tile[] bankToDwarvenMine =  {new Tile(3015, 3362, 0), new Tile(3035, 3367, 0), new Tile(3055, 3370, 0), new Tile(3058, 9776, 0)};

    private List<Task> tasks = new ArrayList<Task>();
    private Logger     log   = new Logger(this.getClass());

    @Override
    public void start()
    {
        log.info("Started");

        tasks.add(new Mining(ctx, COAL_ROCK_IDS, System.currentTimeMillis()));
        tasks.add(new Walking(ctx, dwarvenMineToBank));
        tasks.add(new Walking(ctx, bankToDwarvenMine));
        tasks.add(new Banking(ctx, COAL_INVENTORY_ID));
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
