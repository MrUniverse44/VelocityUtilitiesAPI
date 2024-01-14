package me.blueslime.utilitiesapi.item.dynamic.executor;

import me.blueslime.utilitiesapi.item.ItemWrapper;
import me.blueslime.utilitiesapi.item.dynamic.DynamicItem;

import java.util.function.Function;

public class FunctionExecutor implements DynamicExecutor {
    private final Function<DynamicItem, ItemWrapper> function;

    public FunctionExecutor(Function<DynamicItem, ItemWrapper> function) {
        this.function = function;
    }

    @Override
    public ItemWrapper build(DynamicItem item) {
        return function.apply(item);
    }
}
