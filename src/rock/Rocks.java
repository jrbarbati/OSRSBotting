package rock;

import java.util.HashMap;
import java.util.Map;

public class Rocks
{
    private Map<String, Rock> rocks;

    public Rocks()
    {
        this.rocks = new HashMap<String, Rock>() {{
            put("mithril", new Mithril(447, new int[] {11372, 11373}));
            put("coal", new Coal(453, new int[] {11366, 11367}));
        }};
    }

    public String[] types()
    {
        return rocks.keySet().toArray(new String[] {});
    }

    public Rock getRock(String type)
    {
        return rocks.get(type);
    }
}
