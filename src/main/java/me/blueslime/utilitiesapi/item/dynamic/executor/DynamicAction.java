package me.blueslime.utilitiesapi.item.dynamic.executor;

import me.blueslime.utilitiesapi.item.ItemWrapper;
import me.blueslime.utilitiesapi.item.dynamic.DynamicItem;

/**
 * Creates your custom DynamicExecutor using an abstract usage
 */
public abstract class DynamicAction implements DynamicExecutor {
    public abstract ItemWrapper build(DynamicItem item);
}
