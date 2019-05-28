package tasks;

import logger.Logger;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;

import java.awt.*;
import java.util.concurrent.Callable;

public class Mining extends Task
{
    private static final long TWO_AND_A_HALF_HOURS_MILLIS = 3600 * 2500;

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
        // TODO: Fine tune so it's ready only when near coal.
        return playerIsInactive() && !inventoryIsFull() && nearCoal();
    }

    @Override
    public void execute()
    {
        // TODO: Look into swinging camera around to get better look at coal (if needed)
        waitRandomAmountOfTime(157L, 429L, System.currentTimeMillis() - startTime);

        log.info("Executing");

        missClick(0.063);
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

    private boolean nearCoal()
    {
        // TODO: Implement!
        return false;
    }

    private void missClick(double chanceOfMissClicking)
    {
        if (Math.random() > chanceOfMissClicking)
            return;

        GameObject nearestCoal = fetchNearestCoal();
        Point nearestCoalCenter = nearestCoal.centerPoint();

        log.info(String.format("Miss Clicking....Nearest Coal is at (%f, %f)",
                nearestCoalCenter.getX(),
                nearestCoalCenter.getY())
        );

        click(
                (int) nearestCoalCenter.getX() + (randomDirection() * (nearestCoal.width() / 2) + randomNumber(1, 4)),
                (int) nearestCoalCenter.getY() + (randomDirection() * (nearestCoal.height() / 2) + randomNumber(2, 6))
        );
    }

    private void click(int x, int y)
    {
        // TODO: Implement!
    }

    private int randomDirection()
    {
        return Math.random() < 0.5 ? 1 : -1;
    }

    private int randomNumber(long min, long max)
    {
        return (int) (Math.floor(Math.random() * (max - min)) + min);
    }

    private void mineCoal()
    {
        GameObject nearestCoal = fetchNearestCoal();
        nearestCoal.interact("Mine");
    }

    private GameObject fetchNearestCoal()
    {
        return ctx.objects.select().id(oreIds).nearest().poll();
    }

    private void waitUntilMiningIsFinished()
    {
        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.players.local().animation() != -1;
            }
        });
    }

    private void waitRandomAmountOfTime(long min, long max, long totalRunningTime)
    {
        pause(calculateTimeToWait(min, max, totalRunningTime));
    }

    /**
     * Calculates a random weight time between min and max but actual wait time is stretched out as the running time of
     * the script increases
     */
    private long calculateTimeToWait(long min, long max, long totalRunningTime)
    {
        if (Math.random() < .075)
        {
            return 2542;
        }

        long waitTime = (long) randomNumber(min, max);
        return (long) Math.floor(waitTime * (1 / (1 - (totalRunningTime / TWO_AND_A_HALF_HOURS_MILLIS))));
    }

    private void pause(long waitTime)
    {
        try {
            Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            log.error("Caught an Interrupted Exception while sleeping", e);
        }
    }
}
