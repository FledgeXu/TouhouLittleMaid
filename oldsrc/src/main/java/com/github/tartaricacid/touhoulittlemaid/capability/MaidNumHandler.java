package com.github.tartaricacid.touhoulittlemaid.capability;

import com.github.tartaricacid.touhoulittlemaid.config.GeneralConfig;
import net.minecraft.util.math.MathHelper;

import java.util.concurrent.Callable;

/**
 * @author TartaricAcid
 * @date 2019/12/2 13:13
 **/
public class MaidNumHandler {
    static MaidNumHandler.Factory FACTORY = new MaidNumHandler.Factory();
    private int num = 0;
    private boolean dirty;

    public boolean canAdd() {
        return this.num + 1 <= getMaxNum();
    }

    public void add() {
        this.add(1);
    }

    public void add(int num) {
        if (num + this.num <= getMaxNum()) {
            this.num += num;
        } else {
            this.num = getMaxNum();
        }
        markDirty();
    }

    public void min(int num) {
        if (num <= this.num) {
            this.num -= num;
        } else {
            this.num = 0;
        }
        markDirty();
    }

    public void set(int num) {
        this.num = MathHelper.clamp(num, 0, getMaxNum());
        markDirty();
    }

    public int getMaxNum() {
        return GeneralConfig.MAID_CONFIG.ownerMaxMaidNum;
    }

    public int get() {
        return this.num;
    }

    public void markDirty() {
        dirty = true;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    private static class Factory implements Callable<MaidNumHandler> {
        @Override
        public MaidNumHandler call() {
            return new MaidNumHandler();
        }
    }
}
