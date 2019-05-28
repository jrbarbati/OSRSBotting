package tasks;

import logger.Logger;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Npc;

public class Banking extends Task
{
    private final static int BANKER_ID = 8682;

    private Logger log = new Logger(this.getClass());
    private int[] itemIds;

    public Banking(ClientContext ctx, int... itemIds)
    {
        super(ctx);
        this.itemIds = itemIds;
    }

    @Override
    public boolean isReady()
    {
        return playerIsInactive() && !inventoryIsEmpty() && nearBank();
    }

    @Override
    public void execute()
    {
        // TODO: Look into swinging camera around to get better look at bank/banker (if needed)
        log.info("Executing");

        prepareToBank();
        depositItems();
    }

    private boolean playerIsInactive()
    {
        return ctx.players.local().animation() == -1;
    }

    private boolean inventoryIsEmpty()
    {
        return ctx.inventory.select().count() == 0;
    }

    private boolean nearBank()
    {
        // TODO: Implement!
        return false;
    }

    private void prepareToBank()
    {
        Npc banker = ctx.npcs.select().id(BANKER_ID).nearest().poll();
        banker.interact("Bank");
    }

    private void depositItems()
    {
        for (Integer itemId : itemIds)
        {
            ctx.inventory.select().id(itemId).poll().interact("Deposit-All");
            waitRandomAmountOfTime(118, 306);
        }
    }

    private void waitRandomAmountOfTime(long min, long max)
    {
        pause(calculateTimeToWait(min, max, Math.random()));
    }

    /**
     * Calculates a random weight time between min and max but actual wait time is stretched out as the running time of
     * the script increases
     */
    private long calculateTimeToWait(long min, long max, double random)
    {
        return (long) (Math.floor(random * (max - min)) + min);
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
