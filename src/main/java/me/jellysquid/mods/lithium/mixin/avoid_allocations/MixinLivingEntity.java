package me.jellysquid.mods.lithium.mixin.avoid_allocations;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("InvalidMemberReference")
@Mixin(LivingEntity.class)
public class MixinLivingEntity {
    private static final EquipmentSlot[] SLOTS = EquipmentSlot.values();

    @Redirect(method = {"tick", "writeCustomDataToTag"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EquipmentSlot;values()[Lnet/minecraft/entity/EquipmentSlot;"))
    private EquipmentSlot[] redirectEquipmentSlotsClone() {
        return SLOTS;
    }
}
