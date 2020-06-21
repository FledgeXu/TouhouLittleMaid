package com.github.tartaricacid.touhoulittlemaid.entity.ai;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaidPredicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class EntityMaidPickup extends EntityAIBase {
    private EntityMaid entityMaid;
    private float speed;
    private Entity entityPickup;
    private int countTime;
    private List<Entity> list;

    public EntityMaidPickup(EntityMaid entityMaid, float speed) {
        this.entityMaid = entityMaid;
        this.speed = speed;
        this.countTime = 10;
        setMutexBits(1 | 2);
    }

    @Override
    public boolean shouldExecute() {
        // 一些状态判定，如果状态不对，不进行拾取
        if (entityMaid.guiOpening || !entityMaid.isPickup() || entityMaid.isSitting() || !entityMaid.isTamed()) {
            return false;
        }

        // 计时判定，时间不到不进行拾取
        if (countTime > 0) {
            countTime--;
            return false;
        }

        countTime = 10;

        // 获取初始拾取列表
        list = this.entityMaid.world.getEntitiesInAABBexcluding(entityMaid,
                this.entityMaid.getEntityBoundingBox().grow(8, 2, 8),
                EntityMaidPredicate.IS_PICKUP);

        // 列表不为空，就执行
        return !list.isEmpty();
    }

    @Override
    public void updateTask() {
        // 检查拾取的对象
        if (entityPickup != null && entityPickup.isEntityAlive() && !entityPickup.isInWater()
                && entityMaid.isWithinHomeDistanceFromPosition(new BlockPos(entityPickup))
                && entityMaid.getNavigator().getPathToEntityLiving(entityPickup) != null) {
            entityMaid.getLookHelper().setLookPositionWithEntity(entityPickup, 30f, entityMaid.getVerticalFaceSpeed());
            entityMaid.getNavigator().tryMoveToXYZ(entityPickup.posX, entityPickup.posY, entityPickup.posZ, speed);
            return;
        }
        // 移除不符合的实体对象
        if (entityPickup != null) {
            list.remove(entityPickup);
        }
        // 拾取对象为空，或者没活着，那就遍历获取下一个
        for (Entity entity : list) {
            if (entity.isDead || !entityMaid.canEntityBeSeen(entity)) {
                continue;
            }
            // 物品
            if (entity instanceof EntityItem && entityMaid.pickupItem((EntityItem) entity, true)) {
                entityPickup = entity;
                return;
            }
            // 经验球
            if (entity instanceof EntityXPOrb) {
                entityPickup = entity;
                return;
            }
            // 弓箭
            if (entity instanceof EntityArrow && entityMaid.pickupArrow((EntityArrow) entity, true)) {
                entityPickup = entity;
                return;
            }
        }
        // 如果都不符合，那就清空列表
        list.clear();
    }

    @Override
    public void resetTask() {
        entityPickup = null;
        list.clear();
        entityMaid.getNavigator().clearPath();
    }

    @Override
    public boolean shouldContinueExecuting() {
        return !entityMaid.guiOpening && !list.isEmpty() && !entityMaid.isSitting() && entityMaid.isPickup();
    }
}
