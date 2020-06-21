package com.github.tartaricacid.touhoulittlemaid.api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * @author Snownee
 * @date 2019/7/24 02:31
 */
public interface IMaidBauble {
    Random RANDOM = new Random();

    /**
     * 在普通远程攻击模式下触发
     *
     * @param entityMaid     女仆对象
     * @param target         女仆瞄准的目标
     * @param baubleItem     所调用的饰品
     * @param distanceFactor 距离因子，其实也就是蓄力时长
     * @param entityArrow    通过女仆背包内物品获取得到的实体箭
     * @return 是否取消后续普通远程攻击事件
     */
    default boolean onRangedAttack(AbstractEntityMaid entityMaid, EntityLivingBase target, ItemStack baubleItem, float distanceFactor, @Nullable EntityArrow entityArrow) {
        return false;
    }

    /**
     * 在弹幕攻击模式下触发
     *
     * @param entityMaid     女仆对象
     * @param target         女仆瞄准的目标
     * @param baubleItem     所调用的饰品
     * @param distanceFactor 距离因子，其实也就是蓄力时长
     * @param entityList     女仆附近的 Mob 列表，用于判定弹幕射击的档数
     * @return 是否取消后续弹幕攻击事件
     */
    default boolean onDanmakuAttack(AbstractEntityMaid entityMaid, EntityLivingBase target, ItemStack baubleItem, float distanceFactor, List<Entity> entityList) {
        return false;
    }

    /**
     * 在游戏进行 tick 时触发的事件
     *
     * @param entityMaid 女仆对象
     * @param baubleItem 所调用的饰品
     */
    default void onTick(AbstractEntityMaid entityMaid, ItemStack baubleItem) {
    }

    /**
     * 在女仆掉落物品之前触发
     *
     * @param entityMaid 女仆
     * @param baubleItem 触发的饰品
     * @return 是否取消后续事件
     */
    default boolean onDropsPre(AbstractEntityMaid entityMaid, ItemStack baubleItem) {
        return false;
    }
}
