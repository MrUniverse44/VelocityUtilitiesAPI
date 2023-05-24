package me.blueslime.utilitiesapi.item.dynamic.executor;

import me.blueslime.utilitiesapi.item.ItemWrapper;
import me.blueslime.utilitiesapi.item.dynamic.DynamicItem;

public class DefaultExecutor implements DynamicExecutor {
    @Override
    public ItemWrapper build(DynamicItem item) {
        return item.getWrapper();
    }
}
