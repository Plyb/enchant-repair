package io.github.orlouge.enchantrepair;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.EmptyEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.function.SetEnchantmentsLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.function.Function;

public class ModifiedLootTables {

    public static final Map<RegistryKey<LootTable>, Collection<Function<RegistryWrapper<Enchantment>, LootPool.Builder>>> POOLS = new HashMap<>(Map.ofEntries(
            chestPool("village/village_weaponsmith", randomPool(List.of(
                    book(Enchantments.SHARPNESS, 1, 3, 30),
                    book(Enchantments.SWEEPING_EDGE, 1, 2, 20),
                    book(Enchantments.KNOCKBACK, 1, 1, 10)
            ), 50, 2)),
            chestBook("village/village_toolsmith", Enchantments.UNBREAKING, 1, 2, 70),
            chestBook("village/village_temple", Enchantments.LOOTING, 1, 2, 80),
            chestBook("village/village_fletcher", Enchantments.POWER, 1, 3, 80),
            chestBook("village/village_armorer", Enchantments.PROTECTION, 1, 3, 80),
            chestBook("ancient_city", Enchantments.PROTECTION, 4, 4, 15),
            chestBook("ancient_city_ice_box", Enchantments.FROST_WALKER, 2, 2, 100),
            chestPool("abandoned_mineshaft", randomPool(List.of(
                    book(Enchantments.FORTUNE, 1, 2, 20),
                    book(Enchantments.EFFICIENCY, 1, 4, 20),
                    book(Enchantments.BANE_OF_ARTHROPODS, 1, 5, 30)
            ), 30, 1)),
            chestPool("simple_dungeon", randomPool(List.of(
                    book(Enchantments.SILK_TOUCH, 1, 1, 20),
                    book(Enchantments.AQUA_AFFINITY, 1, 1, 20),
                    book(Enchantments.SMITE, 1, 5, 50)
            ), 10, 1)),
            chestPool("buried_treasure", randomPool(List.of(
                    book(Enchantments.RESPIRATION, 3, 3, 30),
                    book(Enchantments.LUCK_OF_THE_SEA, 1, 3, 40),
                    book(Enchantments.CHANNELING, 1, 1, 20)
            ), 10, 1)),
            chestPool("underwater_ruin_big", randomPool(List.of(
                    book(Enchantments.IMPALING, 1, 5, 50),
                    book(Enchantments.DEPTH_STRIDER, 1, 3, 30)
            ), 20, 2)),
            chestPool("shipwreck_treasure", randomPool(List.of(
                    book(Enchantments.RESPIRATION, 1, 2, 20),
                    book(Enchantments.LURE, 1, 3, 40)
            ), 40, 1)),
            chestBook("ruined_portal", Enchantments.FIRE_PROTECTION, 1, 2, 50),
            chestBook("igloo_chest", Enchantments.FROST_WALKER, 1, 2, 30),
            chestPool("desert_pyramid", randomPool(List.of(
                    book(Enchantments.INFINITY, 1, 1, 5),
                    book(Enchantments.PUNCH, 1, 2, 10),
                    book(Enchantments.FEATHER_FALLING, 1, 3, 15)
            ), 70, 2)),
            chestPool("jungle_temple", randomPool(List.of(
                    book(Enchantments.LOOTING, 3, 3, 20),
                    book(Enchantments.UNBREAKING, 3, 3, 30),
                    book(Enchantments.RIPTIDE, 1, 3, 30)
            ), 20, 2)),
            chestPool("pillager_outpost", randomPool(List.of(
                    book(Enchantments.PIERCING, 1, 3, 30),
                    book(Enchantments.QUICK_CHARGE, 1, 3, 30)
            ), 40, 3)),
            chestPool("woodland_mansion", randomPool(List.of(
                    book(Enchantments.PIERCING, 4, 5, 20),
                    book(Enchantments.MULTISHOT, 1, 1, 10),
                    book(Enchantments.PROJECTILE_PROTECTION, 1, 4, 30),
                    book(Enchantments.THORNS, 1, 2, 20),
                    book(Enchantments.MENDING, 1, 1, 10)
            ), 10, 3)),
            chestPool("end_city_treasure", randomPool(List.of(
                    book(Enchantments.MENDING, 1, 1, 30),
                    book(Enchantments.FEATHER_FALLING, 4, 4, 15)
            ), 55, 1)),
            chestPool("bastion_treasure", randomPool(List.of(
                    book(Enchantments.SHARPNESS, 4, 5, 40),
                    book(Enchantments.FORTUNE, 3, 3, 20)
            ), 40, 2)),
            chestPool("nether_bridge", randomPool(List.of(
                    book(Enchantments.FIRE_ASPECT, 1, 2, 5),
                    book(Enchantments.FIRE_PROTECTION, 3, 5, 5),
                    book(Enchantments.FLAME, 1, 2, 5)
            ), 85, 2)),
            chestPool("stronghold_library", randomPool(List.of(
                    book(Enchantments.POWER, 4, 5, 20),
                    book(Enchantments.EFFICIENCY, 5, 5, 10),
                    book(Enchantments.BINDING_CURSE, 1, 1, 5)
            ), 65, 4)),
            pool("gameplay/hero_of_the_village/librarian_gift", randomPool(List.of(
                    book(Enchantments.FROST_WALKER, 1, 2, 1),
                    book(Enchantments.UNBREAKING, 3, 3, 1)
            ), 98, 1)),
            pool("gameplay/piglin_bartering", randomPool(List.of(
                    book(Enchantments.KNOCKBACK, 1, 2, 500),
                    book(Enchantments.SWEEPING_EDGE, 3, 3, 25),
                    book(Enchantments.MENDING, 1, 1, 5),
                    book(Enchantments.SWIFT_SNEAK, 1, 3, 10)
            ), 99460, 1)),
            pool("gameplay/fishing/treasure", randomPool(List.of(
                    book(Enchantments.LOYALTY, 1, 3, 1),
                    book(Enchantments.RESPIRATION, 1, 3, 1),
                    book(Enchantments.LUCK_OF_THE_SEA, 1, 3, 1)
            ), 97, 1))
    ));

    private static Map.Entry<RegistryKey<LootTable>, Collection<Function<RegistryWrapper<Enchantment>, LootPool.Builder>>> chestBook(String chest, RegistryKey<Enchantment> enchantmentKey, int minLevel, int maxLevel, int chance) {
        if (chance == 100) return chestPool(chest, bookPool(enchantmentKey, minLevel, maxLevel));
        else return chestPool(chest, randomPool(Collections.singleton(book(enchantmentKey, minLevel, maxLevel, chance)), 100 - chance, 1));
    }

    private static Map.Entry<RegistryKey<LootTable>, Collection<Function<RegistryWrapper<Enchantment>, LootPool.Builder>>> pool(String lootTable, Function<RegistryWrapper<Enchantment>, LootPool.Builder> pool) {
        return Map.entry(key(Identifier.of("minecraft", lootTable)), Collections.singleton(pool));
    }

    private static Map.Entry<RegistryKey<LootTable>, Collection<Function<RegistryWrapper<Enchantment>, LootPool.Builder>>> chestPool(String chest, Function<RegistryWrapper<Enchantment>, LootPool.Builder> pool) {
        return Map.entry(key(Identifier.of("minecraft", "chests/" + chest)), Collections.singleton(pool));
    }

    private static Function<RegistryWrapper<Enchantment>, LootPool.Builder> bookPool(RegistryKey<Enchantment> enchantmentKey, int minLevel, int maxLevel) {
        return registry -> LootPool.builder().with(book(enchantmentKey, minLevel, maxLevel, 1).apply(registry));
    }

    private static Function<RegistryWrapper<Enchantment>, LootPool.Builder> randomPool(Collection<Function<RegistryWrapper<Enchantment>, LeafEntry.Builder<?>>> entries, int emptyWeight, int rolls) {
        return (registry) -> {
            LootPool.Builder builder = LootPool.builder();
            if (emptyWeight > 0) builder = builder.with(EmptyEntry.builder().weight(emptyWeight));
            for (var entry : entries) builder = builder.with(entry.apply(registry));
            if (rolls > 1) builder = builder.rolls(ConstantLootNumberProvider.create(rolls));
            return builder;
        };
    }

    private static Function<RegistryWrapper<Enchantment>, LeafEntry.Builder<?>> book(RegistryKey<Enchantment> enchantmentKey, int minLevel, int maxLevel, int weight) {
        return (registry) -> {
            RegistryEntry<Enchantment> enchantmentEntry = registry.getOrThrow(enchantmentKey);
            LootNumberProvider provider;
            if (maxLevel > minLevel) provider = UniformLootNumberProvider.create(minLevel, maxLevel);
            else provider = ConstantLootNumberProvider.create(minLevel);
            return ItemEntry.builder(Items.BOOK).apply(new SetEnchantmentsLootFunction.Builder().enchantment(enchantmentEntry, provider)).weight(weight);
        };
    }

    public static RegistryKey<LootTable> key(Identifier id) {
        return  RegistryKey.of(RegistryKeys.LOOT_TABLE, id);
    }
}
