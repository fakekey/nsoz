package com.nsoz.map.zones;

import org.jetbrains.annotations.NotNull;
import com.nsoz.map.Map;
import com.nsoz.map.Waypoint;
import com.nsoz.map.world.World;
import com.nsoz.model.Char;
import com.nsoz.util.Log;

public class WarCandy extends ZWorld {

    public WarCandy(Map map, World world) {
        super(0, map.tilemap, map);
        setWorld(world);
    }

    @Override
    public void requestChangeMap(@NotNull Char p) {
        Waypoint wp = tilemap.findWaypoint(p.x, p.y);
        if (wp == null) {
            return;
        }
        Zone z = world.find(wp.next);
        Log.info(z);
    }

}
