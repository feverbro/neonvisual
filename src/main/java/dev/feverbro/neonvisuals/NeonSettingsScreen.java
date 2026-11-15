package dev.feverbro.neonvisuals;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class NeonSettingsScreen extends Screen {
    private final Screen parent;
    private int tab=0;

    public NeonSettingsScreen(Screen parent) {
        super(Component.literal("NeonVisuals"));
        this.parent=parent;
    }

    @Override protected void init() {
        int c=width/2, tw=96, gap=5, start=c-(tw*3+gap*2)/2;
        addTab("COMBAT",start,0); addTab("HUD",start+tw+gap,1); addTab("WORLD",start+2*(tw+gap),2);
        int l=c-160, r=c+6;
        if (tab==0) {
            toggle("Hit particles",()->NeonConfig.hitParticles,v->NeonConfig.hitParticles=v,l,0);
            toggle("Critical effects",()->NeonConfig.criticalEffects,v->NeonConfig.criticalEffects=v,r,0);
            toggle("Hitmarker",()->NeonConfig.hitmarker,v->NeonConfig.hitmarker=v,l,1);
            toggle("Combo counter",()->NeonConfig.comboCounter,v->NeonConfig.comboCounter=v,r,1);
            toggle("Damage numbers",()->NeonConfig.damageNumbers,v->NeonConfig.damageNumbers=v,l,2);
            toggle("Kill effects",()->NeonConfig.killEffects,v->NeonConfig.killEffects=v,r,2);
            cycle("Particle type",()->NeonConfig.hitParticleType,v->NeonConfig.hitParticleType=v,new String[]{"Crit","Stars","Hearts","Enchanted"},l,3);
            cycle("Particle shape",()->NeonConfig.hitParticleShape,v->NeonConfig.hitParticleShape=v,new String[]{"Burst","Ring","Upward"},r,3);
        } else if (tab==1) {
            toggle("Target HUD",()->NeonConfig.targetHud,v->NeonConfig.targetHud=v,l,0);
            toggle("Armor HUD",()->NeonConfig.armorHud,v->NeonConfig.armorHud=v,r,0);
            toggle("Potion HUD",()->NeonConfig.potionHud,v->NeonConfig.potionHud=v,l,1);
            toggle("Keystrokes",()->NeonConfig.keystrokes,v->NeonConfig.keystrokes=v,r,1);
            toggle("Coordinates + FPS",()->NeonConfig.coordinates,v->NeonConfig.coordinates=v,l,2);
            toggle("CPS counter",()->NeonConfig.cpsCounter,v->NeonConfig.cpsCounter=v,r,2);
            toggle("Crosshair accent",()->NeonConfig.crosshairAccent,v->NeonConfig.crosshairAccent=v,l,3);
            toggle("Main HUD",()->NeonConfig.hudEnabled,v->NeonConfig.hudEnabled=v,r,3);
        } else {
            toggle("Fullbright",()->NeonConfig.fullbright,v->NeonConfig.fullbright=v,l,0);
            toggle("Extended fog",()->NeonConfig.extendedFog,v->NeonConfig.extendedFog=v,r,0);
            toggle("Player trail",()->NeonConfig.playerTrail,v->NeonConfig.playerTrail=v,l,1);
            toggle("Jump rings",()->NeonConfig.jumpRings,v->NeonConfig.jumpRings=v,r,1);
            toggle("Ambient particles",()->NeonConfig.ambientParticles,v->NeonConfig.ambientParticles=v,l,2);
            button("Reset world visuals",l,3,()->{
                NeonConfig.fullbright=false; NeonConfig.extendedFog=false; NeonConfig.playerTrail=false;
                NeonConfig.jumpRings=true; NeonConfig.ambientParticles=false; NeonConfig.save(); rebuildWidgets();
            });
        }
        addRenderableWidget(Button.builder(Component.literal("Done"),b->onClose())
                .bounds(c-78,height-34,156,20).build());
    }

    private void addTab(String text,int x,int index) {
        addRenderableWidget(Button.builder(Component.literal(tab==index?"◆ "+text:text),b->{
            tab=index; rebuildWidgets();
        }).bounds(x,43,96,20).build());
    }

    private void toggle(String label,BooleanSupplier get,Consumer<Boolean> set,int x,int row) {
        button(label+"  "+(get.getAsBoolean()?"ON":"OFF"),x,row,()->{
            set.accept(!get.getAsBoolean()); NeonConfig.save(); rebuildWidgets();
        });
    }

    private void cycle(String label,Supplier<String> get,Consumer<String> set,String[] options,int x,int row) {
        button(label+": "+get.get(),x,row,()->{
            int current=0;
            for(int i=0;i<options.length;i++) if(options[i].equals(get.get())) current=i;
            set.accept(options[(current+1)%options.length]); NeonConfig.save(); rebuildWidgets();
        });
    }

    private void button(String text,int x,int row,Runnable action) {
        addRenderableWidget(Button.builder(Component.literal(text),b->action.run())
                .bounds(x,78+row*26,154,20).build());
    }

    @Override public void render(GuiGraphics g,int mouseX,int mouseY,float delta) {
        // Custom overlay intentionally avoids 1.21.11 Screen blur (one blur per frame).
        g.fill(0,0,width,height,0x66030A12);
        int l=width/2-174, r=width/2+174;
        g.fill(l,8,r,height-8,0xE10B1420);
        g.fill(l,8,r,10,0xFF55E7FF);
        g.fill(l,68,r,69,0xFF263747);
        g.drawCenteredString(font,"NEON VISUALS",width/2,19,0xFF55E7FF);
        g.drawCenteredString(font,"CLIENT VISUAL MODULES",width/2,31,0xFF8EA8BA);
        g.drawCenteredString(font,tab==0?"Combat effects • hit visuals • combo":tab==1?"HUD elements • status • keystrokes":"World effects • brightness • fog",width/2,height-53,0xFF8EA8BA);
        super.render(g,mouseX,mouseY,delta);
    }

    @Override public void onClose() { if (minecraft!=null) minecraft.setScreen(parent); }
    @Override public boolean isPauseScreen() { return false; }
}
