package com.klitjens.test.items;

import com.klitjens.test.Test;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

public class ItemBase extends Item {

    public ItemBase() {
        super(new Item.Properties().group(Test.TAB));
    }
}
