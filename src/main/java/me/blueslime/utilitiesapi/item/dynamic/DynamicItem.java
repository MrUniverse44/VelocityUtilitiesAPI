package me.blueslime.utilitiesapi.item.dynamic;

import me.blueslime.utilitiesapi.item.ItemWrapper;
import org.bukkit.entity.Player;

public class DynamicItem {
    private final ItemWrapper wrapper;
    private final Player player;

    private DynamicItem(ItemWrapper wrapper, Player player) {
        this.wrapper = wrapper;
        this.player = player;
    }


    public ItemWrapper getWrapper() {
        return wrapper;
    }

    public Player getPlayer() {
        return player;
    }

    public static DynamicItem build(ItemWrapper wrapper, Player player) {
        return new DynamicItem(wrapper, player);
    }
}
