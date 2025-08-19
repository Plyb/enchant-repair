package io.github.orlouge.enchantrepair.fabric;

import io.github.orlouge.enchantrepair.EnchantRepairMod;
import io.github.orlouge.enchantrepair.ModifiedLootTables;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.loot.LootPool;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

import java.util.Collection;
import java.util.function.Function;

public class ExampleModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        EnchantRepairMod.init();
        LootTableEvents.MODIFY.register(((key, tableBuilder, source, registries) -> {
            RegistryWrapper<Enchantment> enchantmentRegistry = registries.getOrThrow(RegistryKeys.ENCHANTMENT);
            Collection<Function<RegistryWrapper<Enchantment>, LootPool.Builder>> poolBuilders = ModifiedLootTables.POOLS.get(key);
            if (poolBuilders != null) {
                poolBuilders.stream()
                        .map(poolBuilder -> poolBuilder.apply(enchantmentRegistry))
                        .forEach(tableBuilder::pool);
            }
        }));
    }
}
