package xyz.quazaros.compacttools.item.CustomItems;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.effect.EnchantmentEffectTarget;
import net.minecraft.enchantment.provider.EnchantmentProvider;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.quazaros.compacttools.util.ModTags;

import java.util.Set;

public class CustomToolItem extends Item {
    private final ToolMaterial material;
    private final Set<Block> mineableBlocks;
    private final float speed;
    private final float attackSpeed;

    public CustomToolItem(ToolMaterial material, Item.Settings settings) {
        super(settings.maxDamage(material.durability()).enchantable(material.enchantmentValue()));
        this.material = material;
        this.mineableBlocks = Set.of(
                Blocks.COBWEB, Blocks.BAMBOO
        );
        this.speed = 1.0F;
        this.attackSpeed = -2.8F;
    }

    @Override
    public float getMiningSpeed(ItemStack stack, BlockState state) {
        if (state.isOf(Blocks.COBWEB)) {
            return 50F;
        } else if (state.isOf(Blocks.BAMBOO)) {
            return 1000F;
        } else if (state.isIn(ModTags.Blocks.ALL_MINEABLE)) {
            return material.speed();
        }
        return 1.0F;
    }

    @Override
    public boolean isCorrectForDrops(ItemStack itemStack, BlockState state) {
        if (!state.isIn(ModTags.Blocks.ALL_MINEABLE)) {return false;}

        ToolMaterial material = this.material;

        if (state.isIn(BlockTags.NEEDS_DIAMOND_TOOL)) {
            return material == ToolMaterial.DIAMOND || material == ToolMaterial.NETHERITE;
        }
        if (state.isIn(BlockTags.NEEDS_IRON_TOOL)) {
            return material == ToolMaterial.IRON || material == ToolMaterial.DIAMOND || material == ToolMaterial.NETHERITE;
        }
        if (state.isIn(BlockTags.NEEDS_STONE_TOOL)) {
            return material != ToolMaterial.WOOD && material != ToolMaterial.GOLD;
        }

        return true;
    }

    @Override
    public boolean canMine(ItemStack stack, BlockState state, World world, BlockPos pos, LivingEntity user) {
        return !state.isAir();
    }

    @Override
    public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damage(1, attacker, EquipmentSlot.MAINHAND);
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!world.isClient() && state.getHardness(world, pos) != 0.0F) {
            stack.damage(1, miner, EquipmentSlot.MAINHAND);
        }
        return true;
    }
}
