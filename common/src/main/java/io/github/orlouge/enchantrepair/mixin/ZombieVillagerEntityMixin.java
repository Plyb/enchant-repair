package io.github.orlouge.enchantrepair.mixin;

import io.github.orlouge.enchantrepair.Config;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieVillagerEntity.class)
public abstract class ZombieVillagerEntityMixin extends ZombieEntity {
    @Shadow public abstract VillagerData getVillagerData();

    public ZombieVillagerEntityMixin(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    public ZombieVillagerEntityMixin(World world) {
        super(world);
    }

    @Inject(method = "initialize", at = @At("HEAD"))
    public void setBookOnInitialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, CallbackInfoReturnable<EntityData> cir) {
        if (
            Config.ZOMBIE_LIBRARIAN_SPAWNS_WITH_SWEEPING &&
            spawnReason == SpawnReason.NATURAL &&
            world.getRandom().nextInt(2) == 0 &&
            this.getVillagerData().profession().getKey().get().equals(VillagerProfession.LIBRARIAN)
        ) {
            ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
            ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
            builder.add(world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.SWEEPING_EDGE), 3);
            EnchantmentHelper.set(book, builder.build());
            this.equipStack(EquipmentSlot.OFFHAND, book);
            this.setEquipmentDropChance(EquipmentSlot.OFFHAND, EquipmentDropChances.DEFAULT_CHANCE);
        }
    }
}
