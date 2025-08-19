package io.github.orlouge.enchantrepair;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;

import java.util.*;

public class ModifiedGrindstoneHelper {
    public static ItemEnchantmentsComponent filterEnchantments(ItemStack tool, boolean normal, boolean treasure, boolean curse) {
        Set<Object2IntMap.Entry<RegistryEntry<Enchantment>>> enchantments = tool.getEnchantments().getEnchantmentEntries();
        ItemEnchantmentsComponent.Builder extracted = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
        for (Object2IntMap.Entry<RegistryEntry<Enchantment>> enchEntry : enchantments) {
            if (enchEntry.getIntValue() == 0) continue;
            if (enchEntry.getKey() == Enchantments.VANISHING_CURSE) {
                return null;
            }
            if (enchEntry.getKey().isIn(EnchantmentTags.CURSE)) {
                if (curse) extracted.add(enchEntry.getKey(), enchEntry.getIntValue());
            } else if (enchEntry.getKey().isIn(EnchantmentTags.TREASURE)) {
                if (treasure) extracted.add(enchEntry.getKey(), enchEntry.getIntValue());
            } else {
                if (normal) extracted.add(enchEntry.getKey(), enchEntry.getIntValue());
            }
        }
        return extracted.build();
    }
}
