package tasks;

import logger.Logger;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;

import java.awt.*;
import java.util.concurrent.Callable;

public class Mining extends Task
{
    private static final long FIVE_HOURS_MILLIS = 3600 * 5000;

    private Logger log = new Logger(this.getClass());
    private int[] oreIds;
    private long startTime;

    public Mining(ClientContext ctx, int[] oreIds)
    {
        super(ctx);
        this.oreIds = oreIds;
    }

    public Mining(ClientContext ctx, int[] oreIds, long startTime)
    {
        this(ctx, oreIds);
        this.startTime = startTime;
    }

    @Override
    public boolean isReady()
    {
        return playerIsInactive() && !inventoryIsFull() && nearOre();
    }

    @Override
    public void execute()
    {
        waitRandomAmountOfTime(587, 1105, System.currentTimeMillis() - startTime);

        log.info("Executing");

        prepareToMine();
        missClick(0.137);
        mineCoal();
        waitUntilMiningIsFinished();
    }

    private boolean playerIsInactive()
    {
        return ctx.players.local().animation() == -1;
    }

    private boolean inventoryIsFull()
    {
        return ctx.inventory.select().count() == 28;
    }

    private boolean nearOre()
    {
        for (int oreId : oreIds)
            if (ctx.objects.select().id(oreId).nearest().poll().tile().distanceTo(ctx.players.local()) < 20)
                return true;

        return false;
    }

    private void waitRandomAmountOfTime(int min, int max, long totalRunningTime)
    {
        pause(calculateTimeToWait(min, max, totalRunningTime));
    }

    private void prepareToMine()
    {
        GameObject nearestOre = fetchNearestOre();

        if (nearestOre.inViewport())
            return;

        putOreIntoViewport(nearestOre);
    }

    private void missClick(double chanceOfMissClicking)
    {
        if (Math.random() > chanceOfMissClicking)
            return;

        GameObject nearestCoal = fetchNearestOre();
        Point nearestCoalCenter = nearestCoal.centerPoint();

        log.info(String.format("Miss Clicking....Nearest Coal is at (%f, %f) with height: %d and width: %d",
                nearestCoalCenter.getX(),
                nearestCoalCenter.getY(),
                nearestCoal.height(),
                nearestCoal.width())
        );

        click(
                (int) nearestCoalCenter.getX() + (randomDirection() * (nearestCoal.width() / 2) + Random.nextInt(1, 4)),
                (int) nearestCoalCenter.getY() + (randomDirection() * (nearestCoal.height() / 2) + Random.nextInt(2, 6))
        );
    }

    private void mineCoal()
    {
        GameObject nearestCoal = fetchNearestOre();
        nearestCoal.interact("Mine");
    }

    private void waitUntilMiningIsFinished()
    {
        Condition.wait(new Callable<Boolean>()
        {
            @Override
            public Boolean call() throws Exception
            {
                return ctx.players.local().animation() != -1;
            }
        });
    }

    private void putOreIntoViewport(GameObject ore)
    {
        ctx.camera.turnTo(ore.tile());
    }

    private void click(int x, int y)
    {
        // TODO: Implement!
    }

    private int randomDirection()
    {
        return Math.random() < 0.5 ? 1 : -1;
    }

    private GameObject fetchNearestOre()
    {
        return ctx.objects.select().id(oreIds).nearest().poll();
    }

    /**
     * Calculates a random weight time between min and max but actual wait time is stretched out as the running time of
     * the script increases
     */
    private long calculateTimeToWait(int min, int max, long totalRunningTime)
    {
        double randomNumber = Math.random();

        if (randomNumber < 0.003)
            return Random.nextInt(60523, 127443);

        if (randomNumber < 0.05)
            return Random.nextInt(2519, 28111);

        long waitTime = (long) Random.nextInt(min, max);
        return (long) Math.floor(waitTime * (1 / (1 - (totalRunningTime / FIVE_HOURS_MILLIS))));
    }

    private void pause(long waitTime)
    {
        log.info(String.format("Sleeping for %d millis", waitTime));
        try
        {
            Thread.sleep(waitTime);
        }
        catch (InterruptedException e)
        {
            log.error("Caught an Interrupted Exception while sleeping", e);
        }
    }
}
