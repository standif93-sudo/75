package com.example.forgeeggmod;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;

public class ForgeEntity extends Mob {

    public ForgeEntity(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0F));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ARMOR, 2.0);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide) {
            // При взаимодействии создаем структуру кузницы и сундук с лутом
            createForgeStructure(player);
            this.discard(); // Удаляем сущность после активации
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private void createForgeStructure(Player player) {
        BlockPos pos = this.blockPosition();
        Level level = this.level();

        // Создаем простую структуру кузницы (3x3 платформа из камня с наковальней)
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockPos blockPos = pos.offset(x, -1, z);
                if (level.isEmptyBlock(blockPos)) {
                    level.setBlock(blockPos, Blocks.STONE_BRICKS.defaultBlockState(), 3);
                }
            }
        }

        // Ставим наковальню в центре
        BlockPos anvilPos = pos.above(-1);
        level.setBlock(anvilPos, Blocks.ANVIL.defaultBlockState(), 3);

        // Ставим сундук рядом
        BlockPos chestPos = pos.offset(1, -1, 0);
        level.setBlock(chestPos, Blocks.CHEST.defaultBlockState(), 3);

        // Заполняем сундук лутом
        BlockEntity blockEntity = level.getBlockEntity(chestPos);
        if (blockEntity instanceof ChestBlockEntity chestEntity) {
            Container container = chestEntity;
            
            // 64 алмаза (стак алмазов)
            ItemStack diamondStack = new ItemStack(Items.DIAMOND, 64);
            container.setItem(0, diamondStack);
            
            // 64 хлеба (стак хлеба)
            ItemStack breadStack = new ItemStack(Items.BREAD, 64);
            container.setItem(1, breadStack);
        }
    }
}
