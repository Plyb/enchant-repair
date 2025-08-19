package io.github.orlouge.enchantrepair.mixin;

import io.github.orlouge.enchantrepair.Config;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentDropChances;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrownedEntity.class)
public class DrownedEntityMixin extends ZombieEntity {
    public DrownedEntityMixin(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    public DrownedEntityMixin(World world) {
        super(world);
    }

    @Inject(method = "initEquipment", cancellable=true, at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/entity/mob/DrownedEntity;equipStack(Lnet/minecraft/entity/EquipmentSlot;Lnet/minecraft/item/ItemStack;)V"))
    public void equipLoyalty(Random random, LocalDifficulty localDifficulty, CallbackInfo ci) {
        if (Config.DROWNED_SPAWNS_WITH_LOYALTY && random.nextInt(5) == 0) {
            ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
            ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
            RegistryEntry<Enchantment> loyalty = getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.LOYALTY);
            builder.add(loyalty, random.nextInt(3) + 1);
            EnchantmentHelper.set(book, builder.build());
            this.equipStack(EquipmentSlot.OFFHAND, book);
            this.setEquipmentDropChance(EquipmentSlot.OFFHAND, EquipmentDropChances.DEFAULT_CHANCE);
            ci.cancel();
        }
    }
}
