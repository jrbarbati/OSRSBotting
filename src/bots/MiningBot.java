package bots;

import input.Input;
import logger.Logger;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import rock.Rocks;
import tasks.Banking;
import tasks.Mining;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

@Script.Manifest(
        name="Mining",
        description = "Automatically Mines Coal from Dwarven Mine",
        properties = "author=VirHircinus; topic=999; client=4;"
)
public class MiningBot extends PollingScript<ClientContext>
{
    private List<Task> tasks = new ArrayList<Task>();
    private Logger log = new Logger(this.getClass());
    private Rocks rocks = new Rocks();

    @Override
    public void start()
    {
        log.info("Started");

        String selectedOre = Input.popupDialogWithOptions("Which ore would you like to mine?", rocks.types());

        tasks.add(new Mining(ctx, rocks.getRock(selectedOre).getRockIds(), System.currentTimeMillis()));
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
