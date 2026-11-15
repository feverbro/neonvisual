package dev.feverbro.neonvisuals;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import java.util.ArrayDeque;

public final class VisualsManager {
    private static final ArrayDeque<Long> ATTACKS=new ArrayDeque<>();
    private static LivingEntity target;
    private static float oldHealth;
    private static boolean deathFx;
    private static Double oldGamma;
    private static boolean wasGround=true;
    private static int trailTicks, ambientTicks;

    public static int combo, comboTicks, hitmarkerTicks, damageTicks;
    public static String damageText="";
    private VisualsManager() {}

    public static void onAttack(LocalPlayer player, Entity attacked) {
        long now=System.currentTimeMillis();
        trimAttacks(now);
        ATTACKS.addLast(now);
        combo=comboTicks>0?combo+1:1;
        comboTicks=36;
        hitmarkerTicks=8;

        if (attacked instanceof LivingEntity living) {
            target=living;
            oldHealth=living.getHealth();
            deathFx=false;
            boolean critical=player.fallDistance>0 && !player.onGround()
                    && !player.isInWater() && !player.onClimbable() && !player.isPassenger();
            if ((critical && NeonConfig.criticalEffects) || (!critical && NeonConfig.hitParticles))
                hitParticles(living,critical);
        }
    }

    private static void trimAttacks(long now) {
        while (!ATTACKS.isEmpty() && now-ATTACKS.peekFirst()>1000L) ATTACKS.removeFirst();
    }

    private static void hitParticles(LivingEntity entity, boolean critical) {
        Minecraft mc=Minecraft.getInstance();
        if (mc.level==null) return;
        ParticleOptions type=switch (NeonConfig.hitParticleType) {
            case "Stars" -> ParticleTypes.END_ROD;
            case "Hearts" -> ParticleTypes.HEART;
            case "Enchanted" -> ParticleTypes.ENCHANT;
            default -> ParticleTypes.CRIT;
        };
        int n=critical?20:14;
        double cx=entity.getX(), cy=entity.getY()+entity.getBbHeight()*0.58, cz=entity.getZ();
        for (int i=0;i<n;i++) {
            double a=Math.PI*2*i/n;
            double r=NeonConfig.hitParticleShape.equals("Ring")?0.48:0.18+Math.random()*0.4;
            double x=cx+Math.cos(a)*r, z=cz+Math.sin(a)*r;
            double y=cy+(NeonConfig.hitParticleShape.equals("Burst")?(Math.random()-0.5)*0.6:0);
            double vy=NeonConfig.hitParticleShape.equals("Upward")?0.08+Math.random()*0.1:0.015;
            mc.level.addParticle(type,x,y,z,Math.cos(a)*0.1,vy,Math.sin(a)*0.1);
        }
        if (critical) for (int i=0;i<7;i++) {
            double a=Math.PI*2*i/7;
            mc.level.addParticle(ParticleTypes.ELECTRIC_SPARK,cx+Math.cos(a)*0.35,cy,cz+Math.sin(a)*0.35,
                    Math.cos(a)*0.08,0.05,Math.sin(a)*0.08);
        }
    }

    public static void tick(Minecraft mc) {
        LocalPlayer p=mc.player;
        if (p==null || mc.level==null) { restoreOptions(mc); target=null; ATTACKS.clear(); return; }

        if (NeonConfig.fullbright) {
            if (oldGamma==null) oldGamma=mc.options.gamma().get();
            if (mc.options.gamma().get()<1.0) mc.options.gamma().set(1.0);
        } else restoreOptions(mc);

        if (hitmarkerTicks>0) hitmarkerTicks--;
        if (comboTicks>0) comboTicks--; else combo=0;
        if (damageTicks>0) damageTicks--;
        trimAttacks(System.currentTimeMillis());
        trackDamage(mc);
        movementEffects(mc,p);
    }

    private static void trackDamage(Minecraft mc) {
        if (target==null || target.level()!=mc.level) return;
        float hp=target.getHealth();
        if (hp<oldHealth-0.05f) {
            damageText=String.format(java.util.Locale.ROOT,"-%.1f",oldHealth-hp);
            damageTicks=28;
            if (hp<=0 && !deathFx && NeonConfig.killEffects) {
                deathFx=true;
                for (int i=0;i<24;i++) {
                    double a=Math.PI*2*i/24, v=0.1+Math.random()*0.08;
                    mc.level.addParticle(ParticleTypes.TOTEM_OF_UNDYING,target.getX(),
                            target.getY()+target.getBbHeight()*0.5,target.getZ(),
                            Math.cos(a)*v,0.05+Math.random()*0.1,Math.sin(a)*v);
                }
            }
        }
        oldHealth=hp;
    }

    private static void movementEffects(Minecraft mc, LocalPlayer p) {
        if (NeonConfig.playerTrail && ++trailTicks>=3) {
            trailTicks=0;
            if (p.onGround() && p.getDeltaMovement().horizontalDistanceSqr()>0.0025)
                mc.level.addParticle(ParticleTypes.WAX_ON,p.getX(),p.getY()+0.08,p.getZ(),0,0.008,0);
        }
        if (NeonConfig.jumpRings && wasGround && !p.onGround() && p.getDeltaMovement().y>0.08) {
            for (int i=0;i<18;i++) {
                double a=Math.PI*2*i/18;
                mc.level.addParticle(ParticleTypes.END_ROD,p.getX()+Math.cos(a)*0.7,p.getY()+0.03,
                        p.getZ()+Math.sin(a)*0.7,Math.cos(a)*0.012,0.008,Math.sin(a)*0.012);
            }
        }
        wasGround=p.onGround();
        if (NeonConfig.ambientParticles && ++ambientTicks>=5) {
            ambientTicks=0;
            double x=p.getX()+(Math.random()-0.5)*5, y=p.getY()+0.5+Math.random()*2,
                    z=p.getZ()+(Math.random()-0.5)*5;
            mc.level.addParticle(ParticleTypes.END_ROD,x,y,z,0,0.012,0);
        }
    }

    public static int cps() { return ATTACKS.size(); }

    public static void restoreOptions(Minecraft mc) {
        if (oldGamma!=null) {
            mc.options.gamma().set(oldGamma);
            oldGamma=null;
        }
    }
}
