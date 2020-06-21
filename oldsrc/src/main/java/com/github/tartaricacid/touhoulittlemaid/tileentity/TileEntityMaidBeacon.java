package com.github.tartaricacid.touhoulittlemaid.tileentity;

import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityPowerPoint;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static com.github.tartaricacid.touhoulittlemaid.config.GeneralConfig.MISC_CONFIG;
import static com.github.tartaricacid.touhoulittlemaid.tileentity.TileEntityMaidBeacon.Effect.getEffectByIndex;

/**
 * @author TartaricAcid
 * @date 2019/10/8 15:09
 **/
public class TileEntityMaidBeacon extends TileEntity implements ITickable {
    public static final String POTION_INDEX_TAG = "PotionIndex";
    public static final String STORAGE_POWER_TAG = "StoragePower";
    private int potionIndex = -1;
    private float storagePower;

    @Override
    public void update() {
        if (!this.world.isRemote && this.world.getTotalWorldTime() % 80L == 0L) {
            if (potionIndex != -1 && storagePower >= this.getEffectCost()) {
                storagePower = storagePower - this.getEffectCost();
                updateBeaconEffect(getEffectByIndex(potionIndex).potion);
            }
            updateAbsorbPower();
        }
    }

    private void updateBeaconEffect(Potion potion) {
        List<EntityMaid> list = this.world.getEntitiesWithinAABB(EntityMaid.class, new AxisAlignedBB(pos).grow(8, 8, 8));
        for (EntityMaid maid : list) {
            if (maid.isEntityAlive()) {
                maid.addPotionEffect(new PotionEffect(potion, 100, 1, true, true));
            }
        }
    }

    private void updateAbsorbPower() {
        List<EntityPowerPoint> list = this.world.getEntitiesWithinAABB(EntityPowerPoint.class, new AxisAlignedBB(pos).grow(3, 3, 3));
        for (EntityPowerPoint powerPoint : list) {
            if (powerPoint.isEntityAlive()) {
                float addNum = this.getStoragePower() + powerPoint.powerValue / 100.0f;
                if (addNum <= this.getMaxStorage()) {
                    this.setStoragePower(addNum);
                    powerPoint.spawnExplosionParticle();
                    powerPoint.setDead();
                }
            }
        }
    }

    @Nonnull
    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), -1, writeToNBT(new NBTTagCompound()));
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        readBeaconNBT(getTileData());
    }

    public void readBeaconNBT(NBTTagCompound compound) {
        if (compound.hasKey(POTION_INDEX_TAG)) {
            potionIndex = compound.getInteger(POTION_INDEX_TAG);
        }
        if (compound.hasKey(STORAGE_POWER_TAG)) {
            storagePower = compound.getFloat(STORAGE_POWER_TAG);
        }
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        writeBeaconNBT(getTileData());
        return super.writeToNBT(compound);
    }

    public NBTTagCompound writeBeaconNBT(NBTTagCompound compound) {
        compound.setInteger(POTION_INDEX_TAG, potionIndex);
        compound.setFloat(STORAGE_POWER_TAG, storagePower);
        return compound;
    }

    public int getPotionIndex() {
        return potionIndex;
    }

    public void setPotionIndex(int potionIndex) {
        this.potionIndex = potionIndex;
        markDirty();
    }

    public float getStoragePower() {
        return storagePower;
    }

    public void setStoragePower(float storagePower) {
        this.storagePower = storagePower;
        markDirty();
    }

    public float getEffectCost() {
        return (float) (MISC_CONFIG.shrineLampEffectCost / 900);
    }

    public float getMaxStorage() {
        return (float) MISC_CONFIG.shrineLampMaxStorage;
    }

    public enum Effect {
        // 各种效果
        SPEED(MobEffects.SPEED),
        FIRE_RESISTANCE(MobEffects.FIRE_RESISTANCE),
        STRENGTH(MobEffects.STRENGTH),
        RESISTANCE(MobEffects.RESISTANCE),
        REGENERATION(MobEffects.REGENERATION);

        public static final Effect[] VALUES = values();

        private Potion potion;

        Effect(Potion potion) {
            this.potion = potion;
        }

        public static Effect getEffectByIndex(int index) {
            for (Effect effect : VALUES) {
                if (index == effect.ordinal()) {
                    return effect;
                }
            }
            return SPEED;
        }
    }
}
