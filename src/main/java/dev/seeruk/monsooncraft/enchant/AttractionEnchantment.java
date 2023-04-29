package dev.seeruk.monsooncraft.enchant;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;

public class AttractionEnchantment extends Enchantment {
    protected AttractionEnchantment() {
        super(Rarity.RARE, EnchantmentTarget.DIGGER, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinPower(int level) {
        return level * 10;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    public static final boolean isHoldingAttractionTool(PlayerEntity player) {
        var stack = player.getMainHandStack();
        return EnchantmentHelper.getLevel(Enchantments.ATTRACTION, stack) > 0;
    }
}