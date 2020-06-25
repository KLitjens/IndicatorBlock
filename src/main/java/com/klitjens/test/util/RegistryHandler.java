package com.klitjens.test.util;

import com.klitjens.test.Test;
import com.klitjens.test.blocks.BlockItemBase;
import com.klitjens.test.blocks.IndicatorBlock;
import com.klitjens.test.items.ItemBase;
import net.minecraft.block.Block;
import net.minecraft.block.ContainerBlock;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RegistryHandler {
    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, Test.MOD_ID);
    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, Test.MOD_ID);

    public static void init() {

        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    //Items
    public static final RegistryObject<Item> INDICATOR = ITEMS.register("indicator", ItemBase::new);

    //Blocks
    public static final RegistryObject<Block> INDICATOR_BLOCK = BLOCKS.register("indicator_block", IndicatorBlock::new);

    //Block items
    public static final RegistryObject<Item> INDICATOR_BLOCK_ITEM = ITEMS.register("indicator_block", () -> new BlockItemBase(INDICATOR_BLOCK.get()));
}
