package xyz.bluspring.debugcullboxes.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.bluspring.debugcullboxes.client.DebugCullBoxesClient;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V", shift = At.Shift.BEFORE))
    private <E extends Entity> void dcb$renderCullBox(E entity, double d, double e, double f, float g, float h, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        if (DebugCullBoxesClient.renderCullboxes && (!entity.isInvisible() || entity instanceof HangingEntity) && !Minecraft.getInstance().showOnlyReducedInfo()) {
            renderCullbox(poseStack, multiBufferSource.getBuffer(RenderType.lines()), entity, h);
        }
    }

    @Unique
    private void renderCullbox(PoseStack poseStack, VertexConsumer vertexConsumer, Entity entity, float f) {
        AABB aABB = entity.getBoundingBoxForCulling().move(-entity.getX(), -entity.getY(), -entity.getZ());
        LevelRenderer.renderLineBox(poseStack, vertexConsumer, aABB, 1.0F, 1.0F, 1.0F, 1.0F);
        if (entity instanceof EnderDragon) {
            double d = -Mth.lerp(f, entity.xOld, entity.getX());
            double e = -Mth.lerp(f, entity.yOld, entity.getY());
            double g = -Mth.lerp(f, entity.zOld, entity.getZ());

            EnderDragonPart[] var11 = ((EnderDragon) entity).getSubEntities();

            for (EnderDragonPart enderDragonPart : var11) {
                poseStack.pushPose();
                double h = d + Mth.lerp(f, enderDragonPart.xOld, enderDragonPart.getX());
                double i = e + Mth.lerp(f, enderDragonPart.yOld, enderDragonPart.getY());
                double j = g + Mth.lerp(f, enderDragonPart.zOld, enderDragonPart.getZ());
                poseStack.translate(h, i, j);
                LevelRenderer.renderLineBox(poseStack, vertexConsumer, enderDragonPart.getBoundingBoxForCulling().move(-enderDragonPart.getX(), -enderDragonPart.getY(), -enderDragonPart.getZ()), 0.25F, 1.0F, 0.0F, 1.0F);
                poseStack.popPose();
            }
        }

        if (entity instanceof LivingEntity) {
            float k = 0.01F;
            LevelRenderer.renderLineBox(poseStack, vertexConsumer, aABB.minX, entity.getEyeHeight() - 0.01F, aABB.minZ, aABB.maxX, entity.getEyeHeight() + 0.01F, aABB.maxZ, 1.0F, 0.0F, 0.0F, 1.0F);
        }

        Entity entity2 = entity.getVehicle();
        if (entity2 != null) {
            float l = Math.min(entity2.getBbWidth(), entity.getBbWidth()) / 2.0F;
            float m = 0.0625F;
            Vec3 vec3 = entity2.getPassengerRidingPosition(entity).subtract(entity.position());
            LevelRenderer.renderLineBox(poseStack, vertexConsumer, vec3.x - (double)l, vec3.y, vec3.z - (double)l, vec3.x + (double)l, vec3.y + 0.0625, vec3.z + (double)l, 1.0F, 1.0F, 0.0F, 1.0F);
        }

        Vec3 vec32 = entity.getViewVector(f);
        Matrix4f matrix4f = poseStack.last().pose();
        Matrix3f matrix3f = poseStack.last().normal();
        vertexConsumer.vertex(matrix4f, 0.0F, entity.getEyeHeight(), 0.0F).color(0, 0, 255, 255).normal(matrix3f, (float)vec32.x, (float)vec32.y, (float)vec32.z).endVertex();
        vertexConsumer.vertex(matrix4f, (float)(vec32.x * 2.0), (float)((double)entity.getEyeHeight() + vec32.y * 2.0), (float)(vec32.z * 2.0)).color(0, 0, 255, 255).normal(matrix3f, (float)vec32.x, (float)vec32.y, (float)vec32.z).endVertex();
    }
}
