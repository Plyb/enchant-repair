package io.github.orlouge.enchantrepair.mixin;

import io.github.orlouge.enchantrepair.Config;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(CreeperEntity.class)
public class CreeperEntityMixin extends HostileEntity {
    @Shadow @Final private static TrackedData<Boolean> CHARGED;

    protected CreeperEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "dropEquipment", at = @At("HEAD"))
    public void dropBlastProtIfCharged(ServerWorld world, DamageSource source, boolean playerCaused, CallbackInfo ci) {
        if (!Config.CHARGED_CREEPER_DROPS_BLAST_PROT) return;
        var looting = getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.LOOTING);
        var lootingLevel = playerCaused
                ? Math.max(0, source.getWeaponStack().getEnchantments().getLevel(looting))
                : 0;

        if (this.dataTracker.get(CHARGED) && this.getRandom().nextFloat() < 0.5 + 0.2 * lootingLevel) {
            ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
            ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
            var blastProtection = getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.BLAST_PROTECTION);
            builder.add(blastProtection, Math.min(4, this.getRandom().nextInt(4 + lootingLevel) + 1));
            EnchantmentHelper.set(book, builder.build());
            this.dropStack(world, book);
        }
    }
}
