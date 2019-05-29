package tasks;

import logger.Logger;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.ClientContext;

import java.util.concurrent.Callable;

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
        log.info("Executing");

        prepareToBank();
        openBank();
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
        return ctx.bank.nearest().tile().distanceTo(ctx.players.local()) <= 20;
    }

    private void prepareToBank()
    {
        if (ctx.bank.opened())
            return;

        putBankIntoViewport();
    }

    private void putBankIntoViewport()
    {
        if (ctx.bank.inViewport())
            return;

        ctx.camera.turnTo(ctx.bank.nearest());
    }

    private void openBank()
    {
        boolean openSuccessful = ctx.bank.open();

        if (openSuccessful)
            Condition.wait(new Callable<Boolean>()
            {
                @Override
                public Boolean call() throws Exception
                {
                    return ctx.bank.opened();
                }
            }, 250, 20);
    }

    private void depositItems()
    {
        for (Integer itemId : itemIds)
        {
            ctx.inventory.select().id(itemId).poll().interact("Deposit-All");
            waitRandomAmountOfTime(118, 306);
        }
    }

    private void waitRandomAmountOfTime(int min, int max)
    {
        pause((long) Random.nextInt(min, max));
    }

    private void pause(long waitTime)
    {
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
