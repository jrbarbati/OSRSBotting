package rock;

public class Rock
{
    private int inventoryId;
    private int[] rockIds;

    public Rock(int inventoryId, int[] rockIds)
    {
        this.inventoryId = inventoryId;
        this.rockIds = rockIds;
    }

    public int getInventoryId()
    {
        return this.inventoryId;
    }

    public int[] getRockIds()
    {
        return this.rockIds;
    }
}
