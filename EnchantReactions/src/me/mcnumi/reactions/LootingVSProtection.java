package me.mcnumi.reactions;

import java.util.HashMap;
import java.util.Random;

import me.mcnumi.BountifulReactions;
import me.mcnumi.utils.Cooldowns;
import me.mcnumi.utils.DamageeInfo;
import me.mcnumi.utils.DamagerInfo;
import me.mcnumi.utils.Lang;
import me.mcnumi.utils.PacketUtils;
import net.minecraft.server.v1_9_R2.EnumParticle;
import net.minecraft.server.v1_9_R2.PacketPlayOutWorldParticles;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class LootingVSProtection implements Listener{
		
	private HashMap<String, Long> lootVsProtCooldowns = new HashMap<String, Long>();
	Random ran = new Random();
	int playerChance = ran.nextInt(BountifulReactions.plugin.getConfig().getInt("Reaction-chance.Looting-VS-Protection"));
	
	PacketUtils packetUtils = new PacketUtils();
	Cooldowns cooldowns = new Cooldowns(lootVsProtCooldowns, 
			BountifulReactions.plugin.getConfig().getInt(
						"Cooldown-times.Test"));

	
	@EventHandler
	public boolean onDamage(EntityDamageByEntityEvent e) {
		
	if (BountifulReactions.plugin.getConfig().getBoolean("Enabled-reactions.Looting-VS-Protection"))	{
			
			if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
			
			// =--------------------------Damagee/Damager Variables--------------------------=\\
			Player damager = (Player) e.getDamager();
			ItemStack damagerWeapon = DamagerInfo.getDamagerHeldItem(damager);
			Location damagerLoc = damager.getLocation();
			String damagerName = damager.getName();
			Player damagee = (Player) e.getEntity();
			Location damageeLoc = damagee.getLocation();
			ItemStack damageeHelmet = DamageeInfo.getDamageeHelmet(damagee);
			ItemStack damageeChestplate = DamageeInfo.getDamageeChestplate(damagee);
			ItemStack damageeLeggings = DamageeInfo.getDamageeLeggings(damagee);
			ItemStack damageeBoots = DamageeInfo.getDamageeBoots(damagee);
			// =--------------------------Damagee/Damager Variables--------------------------=\\
					
				if (DamagerInfo.isSword(damagerWeapon)
						&& DamageeInfo.isHelmet(damageeHelmet)
						|| DamageeInfo.isChestplate(damageeChestplate)
						|| DamageeInfo.isLeggings(damageeLeggings)
						|| DamageeInfo.isBoots(damageeBoots)) {
						
					if (DamagerInfo.isItemEnchanted(damagerWeapon, Enchantment.LOOT_BONUS_MOBS) &&
						DamageeInfo.isItemEnchanted(damageeHelmet, Enchantment.PROTECTION_ENVIRONMENTAL) ||
						DamageeInfo.isItemEnchanted(damageeChestplate, Enchantment.PROTECTION_ENVIRONMENTAL) ||
						DamageeInfo.isItemEnchanted(damageeLeggings, Enchantment.PROTECTION_ENVIRONMENTAL) ||
						DamageeInfo.isItemEnchanted(damageeBoots, Enchantment.PROTECTION_ENVIRONMENTAL)) {
					
						/*
						 * 
						 * COOLDOWN TIMING
						 * 
						 */
								
						if(BountifulReactions.plugin.getConfig().getBoolean(
								"Enabled-cooldowns.Looting-VS-Protection")) {
																	 								 
									
							if(cooldowns.isPlayerCooldown(damagerName)) {
								 
								long cooldownSecondsLeft = cooldowns.getSecondsleft((damagerName));

								if (BountifulReactions.plugin.getConfig().getBoolean(
										"Enabled-actionbar.Looting-VS-Protection")) {
												
									if(cooldownSecondsLeft>0) {
													
										packetUtils.sendActionBar(damager,
												Lang.SHADOW_STEP_COOLDOWN.toString().replace(
														"%s", Long.toString(cooldownSecondsLeft)));
													
										return true;
										}
												
									} else if (BountifulReactions.plugin.getConfig().getBoolean(
											"Enabled-chat.Looting-VS-Protection")) {
											 
										if(cooldownSecondsLeft>0) {
									
											damager.sendMessage(
													Lang.SHADOW_STEP_COOLDOWN.toString().replace(
															"%s", Long.toString(cooldownSecondsLeft)));
									
											return true;
											}
										}
								} cooldowns.setPlayerCooldown(damagerName);
									
								/*
								 * 
								 * COOLDOWN TIMING
							     * 
								 */
									
									
									
								} if (BountifulReactions.plugin.getConfig().getBoolean(
								    	"Enabled-chance.Looting-VS-Protection") ||
											
									  BountifulReactions.plugin.getConfig().getBoolean(
										"Enabled-cooldowns.Looting-VS-Protection")) {
										
									if (BountifulReactions.plugin.getConfig().getBoolean(
											"Enabled-cooldowns.Looting-VS-Protection") ||
												
											playerChance == ran.nextInt(
											 BountifulReactions.plugin.getConfig().getInt(
												"Reaction-chance.Looting-VS-Protection"))) {
					
												
						/*
						 * 
						 * PARTICLES
						 * 
						 */
							
					if (BountifulReactions.plugin.getConfig().getBoolean("Enabled-particles.Looting-VS-Protection")) {

						new BukkitRunnable() {	
							double phi = 0;
							Location particleLoc = damagerLoc; 
							PacketPlayOutWorldParticles packet;
							@Override
							public void run() {
								phi += Math.PI/10;
										
								/*
								 * 
								 * XYZ COORDS
								 * 
								 */
								
								for (double theta = 0; theta <= 2*Math.PI; theta += Math.PI/40) {							
									double r = 1.5;
									double x = r*Math.cos(theta);
									double y = r*Math.cos(phi)*-1;
									double z = r*Math.sin(theta);
								/*
								 * 
								 * XYZ COORDS
								 * 
								 * use a for loop for coordinates variable.
								 * don't suck.
								 * 
								 */	
									particleLoc.add(x,y,z);
											
									packet = new PacketPlayOutWorldParticles(
											EnumParticle.SMOKE_NORMAL, true, 
											(float) particleLoc.getX(), 
											(float) particleLoc.getY(),
											(float) particleLoc.getZ(), 0, 0, 0, 0, 2, null);	        
											
									for (Entity p : damagerLoc.getWorld().getNearbyEntities(
											damagerLoc, 
											(float) particleLoc.getX(),
											(float) particleLoc.getY(), 
											(float) particleLoc.getZ())) {
						                    		
										if (p instanceof Player) { 
											((CraftPlayer)p).getHandle().
											playerConnection.sendPacket(packet); 
											} 
										}							
									particleLoc.subtract(x,y,z);
									}
										
								if (phi > 1*Math.PI) {
									this.cancel();
									}
										
								}
						    }.runTaskTimer(BountifulReactions.plugin, 0, 1);
					    }
							
					/*
					 * 
					 * PARTICLES
					 * 
					 */
							
							
					/*
					 * 
					 * DAMAGEE LOCATION
					 * 
					 */
							
							
					double newX;
					double newZ;
					float nang = damageeLoc.getYaw() + 90;
				                	
					if(nang < 0) nang += 360;
				                	
					newX = Math.cos(Math.toRadians(nang));
					newZ = Math.sin(Math.toRadians(nang));
				       
					Location newDamagerLoc = new Location(damageeLoc.getWorld(), 
							damageeLoc.getX() - newX,
							damageeLoc.getY(), 
							damageeLoc.getZ() - newZ,
							damageeLoc.getYaw(), damageeLoc.getPitch());
									
					 /*
			  		  * 
				   	  * DAMAGEE LOCATION
					  * 
					  */
									
					damager.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 200, 1));
					damager.teleport(newDamagerLoc);
								    
					packetUtils.sendActionBar(damager, Lang.SHADOW_STEP_ACTIVATED.toString());
					packetUtils.sendActionBar(damagee, Lang.SHADOW_STEPPED.toString());
															    							    																				
							
							}
						}
					}
				}
			}
		}
		return false;
	}
}
