package com.github.tartaricacid.touhoulittlemaid.api;

import com.github.tartaricacid.touhoulittlemaid.api.task.FarmHandler;
import com.github.tartaricacid.touhoulittlemaid.api.task.FeedHandler;
import com.github.tartaricacid.touhoulittlemaid.api.util.BaubleItemHandler;
import com.github.tartaricacid.touhoulittlemaid.api.util.ItemDefinition;
import com.google.common.base.Optional;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Snownee
 * @date 2019/7/23 14:53
 */
public class LittleMaidAPI {
    private static ILittleMaidAPI INSTANCE = null;

    public static void setInstance(ILittleMaidAPI instance) {
        INSTANCE = instance;
    }

    /**
     * 注册所有的饰品
     *
     * @param item   被赋予饰品对象的物品
     * @param bauble 饰品对象
     * @return 被赋予的饰品对象
     */
    @Nullable
    public static IMaidBauble registerBauble(ItemDefinition item, IMaidBauble bauble) {
        return INSTANCE.registerBauble(item, bauble);
    }

    @Nullable
    public static IMaidBauble getBauble(ItemDefinition item) {
        return INSTANCE.getBauble(item);
    }

    @Nullable
    public static IMaidBauble getBauble(ItemStack item) {
        return INSTANCE.getBauble(item);
    }

    /**
     * 通过传入 IMaidBauble 对象，得到女仆饰品栏对应的格子位
     *
     * @param bauble 需要匹配的 IMaidBauble 对象
     * @return 如果没找到，会返回 -1
     */
    @Nonnull
    public static int getBaubleSlotInMaid(AbstractEntityMaid maid, IMaidBauble bauble) {
        return INSTANCE.getBaubleSlotInMaid(maid, bauble);
    }

    public static void registerTask(IMaidTask task) {
        INSTANCE.registerTask(task);
    }

    public static Optional<IMaidTask> findTask(ResourceLocation uid) {
        return INSTANCE.findTask(uid);
    }

    public static List<IMaidTask> getTasks() {
        return INSTANCE.getTasks();
    }

    public static IMaidTask getIdleTask() {
        return INSTANCE.getIdleTask();
    }

    public static void registerFarmHandler(FarmHandler handler) {
        INSTANCE.registerFarmHandler(handler);
    }

    public static List<FarmHandler> getFarmHandlers() {
        return INSTANCE.getFarmHandlers();
    }

    public static void registerFeedHandler(FeedHandler handler) {
        INSTANCE.registerFeedHandler(handler);
    }

    public static List<FeedHandler> getFeedHandlers() {
        return INSTANCE.getFeedHandlers();
    }

    public static void registerMultiBlock(IMultiBlock multiBlock) {
        INSTANCE.registerMultiBlock(multiBlock);
    }

    public static List<IMultiBlock> getMultiBlockList() {
        return INSTANCE.getMultiBlockList();
    }


    /**
     * ILittleMaidAPI 用于饰品注册，寻找物品对应饰品对象等杂项性的内容
     */
    public interface ILittleMaidAPI {
        /**
         * 注册饰品
         *
         * @param item   ItemDefinition 类描述的物品
         * @param bauble IMaidBauble 对象
         * @return IMaidBauble 对象
         */
        IMaidBauble registerBauble(ItemDefinition item, IMaidBauble bauble);

        /**
         * 通过 ItemDefinition 对象获取对应的 IMaidBauble 对象
         *
         * @param item ItemDefinition 类描述的物品
         * @return 其对应的 IMaidBauble 对象
         */
        IMaidBauble getBauble(ItemDefinition item);

        /**
         * 通过 ItemStack 对象获取对应的 IMaidBauble 对象
         *
         * @param item ItemStack 类描述的物品
         * @return 其对应的 IMaidBauble 对象
         */
        default IMaidBauble getBauble(ItemStack item) {
            return getBauble(ItemDefinition.of(item));
        }

        /**
         * 在女仆对象饰品栏中获取指定 IMaidBauble 对象的物品格子位
         *
         * @param maid   女仆对象
         * @param bauble 指定的 IMaidBauble 对象
         * @return 格子位，如果没找到，会返回 -1
         */
        default int getBaubleSlotInMaid(AbstractEntityMaid maid, IMaidBauble bauble) {
            BaubleItemHandler handler = maid.getBaubleInv();
            if (handler != null) {
                for (int i = 0; i < handler.getSlots(); i++) {
                    IMaidBauble baubleIn = handler.getBaubleInSlot(i);
                    if (baubleIn == bauble) {
                        return i;
                    }
                }
            }
            return -1;
        }

        /**
         * 注册女仆模式
         *
         * @param task 女仆的模式
         */
        void registerTask(IMaidTask task);

        /**
         * 寻找女仆模式
         *
         * @param uid 女仆模式 id
         * @return 可能包含 IMaidTask 对象
         */
        Optional<IMaidTask> findTask(ResourceLocation uid);

        /**
         * 获取所有可用的模式
         *
         * @return 可用的模式列表
         */
        List<IMaidTask> getTasks();

        /**
         * 获取空闲模式
         *
         * @return 空闲模式
         */
        IMaidTask getIdleTask();

        /**
         * 注册 FarmHandler
         *
         * @param handler FarmHandler
         */
        void registerFarmHandler(FarmHandler handler);

        /**
         * 获取所有的 FarmHandler
         *
         * @return FarmHandler 列表
         */
        List<FarmHandler> getFarmHandlers();

        /**
         * 注册 FeedHandler
         *
         * @param handler FeedHandler
         */
        void registerFeedHandler(FeedHandler handler);

        /**
         * 获取所有的 FeedHandler
         *
         * @return FeedHandler 列表
         */
        List<FeedHandler> getFeedHandlers();

        /**
         * 注册多方块结构
         *
         * @param multiBlock 多方块结构实例
         */
        void registerMultiBlock(IMultiBlock multiBlock);

        /**
         * 获取所有的多方块结构列表
         *
         * @return 多方块结构列表
         */
        List<IMultiBlock> getMultiBlockList();
    }
}
