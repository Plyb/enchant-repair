package io.github.orlouge.enchantrepair;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.block.ChiseledBookshelfBlock;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.*;

public class ModifiedEnchantingHelper {
    public static final double BOOK_CHANCE_MEAN_INTERCEPT = 0;
    public static final double BOOK_CHANCE_MEAN_SLOPE = 0.28;
    public static final double BOOK_CHANCE_STD_INTERCEPT = 0.75;
    public static final double BOOK_CHANCE_STD_SLOPE = 0.125;

    public static int randomEnchantmentLevelPenalty(int maxLevel, int playerLevel, Random random) {
        if (!Config.RANDOM_ENCHANTMENT_PENALTY) return 0;
        float mean = Config.RANDOM_ENCHANTMENT_PENALTY_MEAN_INTERCEPT - Config.RANDOM_ENCHANTMENT_PENALTY_MEAN_SLOPE * playerLevel;
        return (int) Math.min(maxLevel / 2, Math.max(0, Math.round(random.nextGaussian() * Config.RANDOM_ENCHANTMENT_PENALTY_STDDEV + mean)));
    }

    public static int maximumBookCostAtLevel(int playerLevel, Random random) {
        double level = playerLevel * Config.BOOK_ENCHANTMENT_LEVEL_FACTOR;
        double mean = BOOK_CHANCE_MEAN_INTERCEPT + BOOK_CHANCE_MEAN_SLOPE * level;
        double std = BOOK_CHANCE_STD_INTERCEPT + BOOK_CHANCE_STD_SLOPE * level;
        return (int) Math.ceil(random.nextGaussian() * std + mean);
    }

    public static double enchantDamageChance(int playerLevel) {
        return Math.min(Config.ENCHANT_DAMAGE_CHANCE * 0.1, 0.3 * Config.ENCHANT_DAMAGE_CHANCE / Math.max(1, playerLevel));
    }

    public static Collection<StoredBook> getAvailableEnchantedBooks(World world, BlockPos tablePos) {
        List<StoredBook> books = new LinkedList<>();
        for (BlockPos offset : EnchantingTableBlock.POWER_PROVIDER_OFFSETS) {
            BlockPos pos = tablePos.add(offset);
            if (world.getBlockState(pos).getBlock() instanceof ChiseledBookshelfBlock && world.getBlockEntity(pos) instanceof ChiseledBookshelfBlockEntity entity) {
                for (int slot = 0; slot < entity.size(); slot++) {
                    ItemStack itemStack = entity.getStack(slot);
                    if (itemStack != null && !itemStack.isEmpty() && itemStack.isOf(Items.ENCHANTED_BOOK)) {
                        ItemEnchantmentsComponent enchantments = itemStack.getComponents().get(DataComponentTypes.STORED_ENCHANTMENTS);
                        if (enchantments != null && !enchantments.isEmpty()) {
                            books.add(new StoredBook(pos, slot, enchantments));
                        }
                    }
                }
            }
        }
        return books;
    }

    public static Pair<List<EnchantmentLevelEntry>, List<StoredBook>> generateEnchantmentsFromBooks(ItemStack item, Collection<StoredBook> books, int playerLevel, Random random) {
        List<StoredBook> remainingBooks = new ArrayList<>(books);
        List<StoredBook> consumedBooks = new LinkedList<>();
        Set<RegistryEntry<Enchantment>> attemptedEnchantments = new HashSet<>();
        Set<RegistryEntry<Enchantment>> preexistingEnchantments = item.getEnchantments().getEnchantments();
        List<EnchantmentLevelEntry> selectedEnchantments = new LinkedList<>();
        int accumulatedCost = 0;
        int maxCost = maximumBookCostAtLevel(playerLevel, random);
        while (remainingBooks.size() > 0) {
            StoredBook book = remainingBooks.remove(random.nextInt(remainingBooks.size()));
            ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
            boolean invalid = false;
            for (Object2IntMap.Entry<RegistryEntry<Enchantment>> enchantment : book.enchantments.getEnchantmentEntries()) {
                if (!enchantment.getKey().value().isAcceptableItem(item) || preexistingEnchantments.stream().anyMatch(entry -> entry.matchesKey(enchantment.getKey().getKey().get()))) continue;
                if (attemptedEnchantments.contains(enchantment.getKey()) || selectedEnchantments.stream().anyMatch(e2 -> !Enchantment.canBeCombined(enchantment.getKey(), e2.enchantment()))) {
                    invalid = true;
                    break;
                }
                builder.add(enchantment.getKey(), enchantment.getValue());
            }
            ItemEnchantmentsComponent enchantments = builder.build();
            if (invalid || enchantments.isEmpty()) continue;
            int cost = 0;
            for (Object2IntMap.Entry<RegistryEntry<Enchantment>> enchantment : enchantments.getEnchantmentEntries()) {
                int enchCost = 0;
                enchCost += Math.round(Math.max(1, (float) 5 * enchantment.getValue() / enchantment.getKey().value().getMaxLevel()));
                if (enchantment.getKey().isIn(EnchantmentTags.TREASURE) && !enchantment.getKey().isIn(EnchantmentTags.CURSE)) enchCost += 1;
                cost = Math.max(cost, enchCost);
            }
            if (accumulatedCost > maxCost) break;
            accumulatedCost += cost;
            attemptedEnchantments.addAll(enchantments.getEnchantments());
            enchantments.getEnchantmentEntries().stream().map(e -> new EnchantmentLevelEntry(e.getKey(), e.getValue())).forEach(selectedEnchantments::add);
            if (book.consume()) {
                consumedBooks.add(book);
            }
        }
        return new Pair<>(selectedEnchantments, consumedBooks);
    }

    public record StoredBook(BlockPos bookshelfPos, int slot, ItemEnchantmentsComponent enchantments) {
        public boolean consume() {
            return this.enchantments.getEnchantmentEntries().stream().anyMatch(e -> (Config.BOOK_ENCHANTMENT_CONSUME_VANISHING && e.equals(Enchantments.VANISHING_CURSE)) || (Config.BOOK_ENCHANTMENT_CONSUME_TREASURE && e.getKey().isIn(EnchantmentTags.TREASURE)) || (Config.BOOK_ENCHANTMENT_CONSUME_NONTREASURE && !e.getKey().isIn(EnchantmentTags.TREASURE)));
        }
    }

    public static boolean HasEnchantment(ItemStack stack, RegistryKey<Enchantment> enchantment) {
        return stack.getEnchantments().getEnchantments().stream().anyMatch(enchantmentEntry ->
            enchantmentEntry.getKey().get().equals(enchantment)
            && EnchantmentHelper.getLevel(enchantmentEntry, stack) > 0
        );
    }
}
