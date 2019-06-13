package bots;

import input.Input;
import logger.Logger;
import org.powerbot.script.PaintListener;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;
import rock.Rocks;
import tasks.Mining;
import tasks.Task;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Script.Manifest(
        name="Mining",
        description = "Automatically Mines Coal from Dwarven Mine",
        properties = "author=VirHircinus; topic=999; client=4;"
)
public class MiningBot extends PollingScript<ClientContext> implements PaintListener
{
    private Logger log = new Logger(this.getClass());
    private List<Task> tasks = new ArrayList<>();
    private Rocks rocks = new Rocks();
    private Map<String, Integer> startingXp = new HashMap<>();

    @Override
    public void start()
    {
        log.info("Started");

        startingXp.put("Mining", currentXp(Constants.SKILLS_MINING));

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

    @Override
    public void repaint(Graphics graphics)
    {
        Graphics2D g = (Graphics2D) graphics;

        int miningXpGained = calculateXpGained("Mining", Constants.SKILLS_MINING);

        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, 200, 80);

        g.setColor(new Color(255, 255, 255, 180));
        g.drawString("Mining Bot Session Stats", 20, 20);

        g.drawString(String.format("Total XP Gained: %d", miningXpGained), 20, 40);
        g.drawString(String.format("XP/Hour: %.0f", calculateXpPerHour(miningXpGained)), 20, 60);
    }

    private int calculateXpGained(String skillKey, int skill)
    {
        return startingXp.containsKey(skillKey)
                ? currentXp(skill) - startingXp.get(skillKey)
                : 0;
    }

    private double calculateXpPerHour(int xpGained)
    {
        return xpGained / runtimeInHours();
    }

    private int currentXp(int skill)
    {
        return ctx.skills.experience(skill);
    }

    private double runtimeInHours()
    {
        return getTotalRuntime() / 3600000d;
    }
}
