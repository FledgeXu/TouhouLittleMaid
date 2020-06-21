package com.github.tartaricacid.touhoulittlemaid.client.renderer.tileentity;

import com.github.tartaricacid.touhoulittlemaid.block.BlockGarageKit;
import com.github.tartaricacid.touhoulittlemaid.client.resources.CustomResourcesLoader;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.MaidBlocks;
import com.github.tartaricacid.touhoulittlemaid.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.concurrent.ExecutionException;

/**
 * @author TartaricAcid
 * @date 2019/7/7 15:36
 **/
@SideOnly(Side.CLIENT)
public class TileEntityItemStackGarageKitRenderer extends TileEntityItemStackRenderer {
    public static final TileEntityItemStackGarageKitRenderer INSTANCE = new TileEntityItemStackGarageKitRenderer();

    @Override
    public void renderByItem(@Nonnull ItemStack itemStackIn) {
        if (itemStackIn.getItem() == Item.getItemFromBlock(MaidBlocks.GARAGE_KIT)) {
            World world = Minecraft.getMinecraft().world;
            float renderItemScale = 1.0f;
            GlStateManager.pushMatrix();

            if (Minecraft.isAmbientOcclusionEnabled()) {
                GlStateManager.shadeModel(GL11.GL_SMOOTH);
            } else {
                GlStateManager.shadeModel(GL11.GL_FLAT);
            }

            String entityId = BlockGarageKit.getEntityId(itemStackIn);
            Entity entity;
            try {
                entity = ClientProxy.ENTITY_CACHE.get(entityId, () -> {
                    Entity e = EntityList.createEntityByIDFromName(new ResourceLocation(entityId), world);
                    if (e == null) {
                        return new EntityMaid(world);
                    } else {
                        return e;
                    }
                });
                if (entity instanceof EntityMaid) {
                    ((EntityMaid) entity).setModelId(BlockGarageKit.getModelId(itemStackIn));
                    renderItemScale = CustomResourcesLoader.MAID_MODEL.getModelRenderItemScale(BlockGarageKit.getModelId(itemStackIn));
                    // 缓存的对象往往有一些奇怪的东西，一并清除
                    ((EntityMaid) entity).setShowSasimono(false);
                    ((EntityMaid) entity).hurtResistantTime = 0;
                    ((EntityMaid) entity).hurtTime = 0;
                    ((EntityMaid) entity).deathTime = 0;
                    ((EntityMaid) entity).setSitting(false);
                    ((EntityMaid) entity).setBackpackLevel(EntityMaid.EnumBackPackLevel.EMPTY);
                    for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
                        entity.setItemStackToSlot(slot, ItemStack.EMPTY);
                    }
                }
                entity.readFromNBT(BlockGarageKit.getEntityData(itemStackIn));
            } catch (ExecutionException e) {
                return;
            }

            GlStateManager.enableColorMaterial();
            GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            final boolean lightmapEnabled = GL11.glGetBoolean(GL11.GL_TEXTURE_2D);
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);

            GlStateManager.scale(renderItemScale, renderItemScale, renderItemScale);
            Minecraft.getMinecraft().getRenderManager().setRenderShadow(false);
            Minecraft.getMinecraft().getRenderManager().renderEntity(entity,
                    0.875, 0.25, 0.75, 0, 0, true);
            Minecraft.getMinecraft().getRenderManager().setRenderShadow(true);

            GlStateManager.popMatrix();

            GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            if (lightmapEnabled) {
                GlStateManager.enableTexture2D();
            } else {
                GlStateManager.disableTexture2D();
            }
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
            GlStateManager.enableBlend();
        }
    }
}
