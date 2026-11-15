package dev.feverbro.neonvisuals;

import net.minecraft.client.Minecraft;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import java.util.List;
import java.util.Locale;

public final class NeonHud {
    private static final int CYAN=0xFF55E7FF, WHITE=0xFFEAF4FF, MUTED=0xFF9EB1C4;
    private NeonHud() {}

    public static void render(GuiGraphics g, DeltaTracker tickCounter) {
        Minecraft mc=Minecraft.getInstance();
        if (mc.player==null || mc.level==null || mc.options.hideGui || mc.screen!=null || !NeonConfig.hudEnabled) return;
        int w=g.guiWidth(), h=g.guiHeight(), mid=w/2;
        if (NeonConfig.coordinates) renderInfo(g,mc,8,8);
        if (NeonConfig.targetHud) renderTarget(g,mc,w-145,8);
        if (NeonConfig.hitmarker) renderHitmarker(g,mid,h/2);
        if (NeonConfig.comboCounter && VisualsManager.combo>1 && VisualsManager.comboTicks>0)
            g.drawCenteredString(mc.font,"COMBO x"+VisualsManager.combo,mid,h/2+28,CYAN);
        if (NeonConfig.damageNumbers && VisualsManager.damageTicks>0)
            g.drawCenteredString(mc.font,VisualsManager.damageText,mid+30,h/2-18,0xFFFF7777);
        if (NeonConfig.cpsCounter)
            g.drawString(mc.font,"CPS "+VisualsManager.cps(),8,NeonConfig.coordinates?45:8,CYAN,true);
        if (NeonConfig.crosshairAccent) renderCrosshair(g,mid,h/2);
        if (NeonConfig.armorHud) renderArmor(g,mc,mid,h-60);
        if (NeonConfig.potionHud) renderPotions(g,mc,w-125,h/2-24);
        if (NeonConfig.keystrokes) renderKeys(g,mc,8,h-73);
    }

    private static void renderInfo(GuiGraphics g,Minecraft mc,int x,int y) {
        String xyz=String.format(Locale.ROOT,"XYZ  %d / %d / %d",mc.player.getBlockX(),mc.player.getBlockY(),mc.player.getBlockZ());
        String fps="FPS  "+mc.getFps();
        int width=Math.max(mc.font.width(xyz),mc.font.width(fps))+17;
        g.fill(x-4,y-4,x+width,y+34,0x99070D17);
        g.fill(x-4,y-4,x-2,y+34,CYAN);
        g.drawString(mc.font,"NEON VISUALS",x+3,y,CYAN,true);
        g.drawString(mc.font,xyz,x+3,y+12,WHITE,true);
        g.drawString(mc.font,fps,x+3,y+23,MUTED,true);
    }

    private static void renderTarget(GuiGraphics g,Minecraft mc,int x,int y) {
        if (!(mc.hitResult instanceof EntityHitResult hit) || !(hit.getEntity() instanceof LivingEntity e)
                || !e.isAlive() || e==mc.player) return;
        int width=135;
        g.fill(x-5,y-5,x+width,y+47,0xB5070D17);
        g.fill(x-5,y-5,x-3,y+47,CYAN);
        String name=mc.font.plainSubstrByWidth(e.getDisplayName().getString(),width-14);
        g.drawString(mc.font,name,x+2,y,WHITE,true);
        g.drawString(mc.font,String.format(Locale.ROOT,"%.1f / %.1f HP",e.getHealth(),e.getMaxHealth()),x+2,y+12,MUTED,true);
        g.fill(x+2,y+25,x+width-10,y+31,0xFF273444);
        float ratio=e.getMaxHealth()<=0?0:Math.max(0,Math.min(1,e.getHealth()/e.getMaxHealth()));
        g.fill(x+2,y+25,x+2+Math.round((width-12)*ratio),y+31,ratio>.5f?0xFF50D890:0xFFFF6478);
        g.drawString(mc.font,String.format(Locale.ROOT,"%.1fm",mc.player.distanceTo(e)),x+2,y+35,MUTED,false);
    }

    private static void renderHitmarker(GuiGraphics g,int x,int y) {
        if (VisualsManager.hitmarkerTicks<=0) return;
        int d=5+(8-VisualsManager.hitmarkerTicks)/2, c=0xFFFFFFFF;
        g.fill(x-d-3,y-d-3,x-d-1,y-d+2,c); g.fill(x-d-3,y-d-3,x-d+2,y-d-1,c);
        g.fill(x+d+1,y-d-3,x+d+3,y-d+2,c); g.fill(x+d-2,y-d-3,x+d+3,y-d-1,c);
        g.fill(x-d-3,y+d+1,x-d-1,y+d+6,c); g.fill(x-d-3,y+d+4,x-d+2,y+d+6,c);
        g.fill(x+d+1,y+d+1,x+d+3,y+d+6,c); g.fill(x+d-2,y+d+4,x+d+3,y+d+6,c);
    }

    private static void renderCrosshair(GuiGraphics g,int x,int y) {
        int c=0xCC55E7FF;
        g.fill(x-1,y-8,x+1,y-5,c); g.fill(x-1,y+5,x+1,y+8,c);
        g.fill(x-8,y-1,x-5,y+1,c); g.fill(x+5,y-1,x+8,y+1,c);
    }

    private static void renderArmor(GuiGraphics g,Minecraft mc,int center,int y) {
        List<EquipmentSlot> slots=List.of(EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET);
        int x=center-40;
        g.fill(x-5,y-4,x+84,y+23,0x99070D17);
        for (int i=0;i<slots.size();i++) {
            ItemStack stack=mc.player.getItemBySlot(slots.get(i));
            if (stack.isEmpty()) continue;
            int ix=x+i*19;
            g.renderItem(stack,ix,y);
            int max=stack.getMaxDamage();
            if (max>0) {
                int left=max-stack.getDamageValue();
                int color=left>max*.5?0xFF63E6A8:left>max*.2?0xFFFFC857:0xFFFF6478;
                g.fill(ix+1,y+17,ix+17,y+19,0xFF202A35);
                g.fill(ix+1,y+17,ix+1+Math.round(16f*left/max),y+19,color);
            }
        }
    }

    private static void renderPotions(GuiGraphics g,Minecraft mc,int x,int y) {
        int shown=0;
        for (MobEffectInstance fx:mc.player.getActiveEffects()) {
            if (shown>=5) break;
            String name=fx.getEffect().value().getDisplayName().getString();
            String time=fx.isInfiniteDuration()?"∞":formatTime(fx.getDuration());
            String line=mc.font.plainSubstrByWidth(name+" "+time,112);
            int rowY=y+shown*13;
            g.fill(x-4,rowY-2,x+mc.font.width(line)+5,rowY+11,0x99070D17);
            g.drawString(mc.font,line,x,rowY,WHITE,true);
            shown++;
        }
    }

    private static String formatTime(int ticks) {
        int sec=Math.max(0,ticks)/20;
        return String.format(Locale.ROOT,"%d:%02d",sec/60,sec%60);
    }

    private static void renderKeys(GuiGraphics g,Minecraft mc,int x,int y) {
        drawKey(g,mc,"W",x+19,y,mc.options.keyUp.isDown());
        drawKey(g,mc,"A",x,y+19,mc.options.keyLeft.isDown());
        drawKey(g,mc,"S",x+19,y+19,mc.options.keyDown.isDown());
        drawKey(g,mc,"D",x+38,y+19,mc.options.keyRight.isDown());
        drawKey(g,mc,"SP",x,y+38,mc.options.keyJump.isDown());
        drawKey(g,mc,"SH",x+26,y+38,mc.options.keyShift.isDown());
        drawKey(g,mc,"ST",x+52,y+38,mc.options.keySprint.isDown());
    }

    private static void drawKey(GuiGraphics g,Minecraft mc,String s,int x,int y,boolean down) {
        int width=s.length()==1?17:24;
        g.fill(x,y,x+width,y+17,down?0xDD1C8495:0xAA0A111C);
        g.fill(x,y,x+width,y+1,down?CYAN:0xFF314253);
        g.drawCenteredString(mc.font,s,x+width/2,y+4,WHITE);
    }
}
