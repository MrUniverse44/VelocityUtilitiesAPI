package me.blueslime.utilitiesapi.item.dynamic.executor;

import me.blueslime.utilitiesapi.item.ItemWrapper;
import me.blueslime.utilitiesapi.item.dynamic.DynamicItem;

public interface DynamicExecutor {
    ItemWrapper build(DynamicItem item);
}
