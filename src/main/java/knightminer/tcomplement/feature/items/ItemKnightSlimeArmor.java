package knightminer.tcomplement.feature.items;

import knightminer.tcomplement.feature.ModuleFeature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.library.SlimeBounceHandler;
import slimeknights.tconstruct.tools.common.network.BouncedPacket;

public class ItemKnightSlimeArmor extends ItemArmorBase {

	public ItemKnightSlimeArmor(EntityEquipmentSlot slot) {
		super(ModuleFeature.knightSlimeArmor, slot);
	}

	// stolen from RUBBERY BOUNCY BOUNCERY WOOOOO
	@SubscribeEvent
	public static void onFall(LivingFallEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		if(entity == null) {
			return;
		}
		ItemStack feet = entity.getItemStackFromSlot(EntityEquipmentSlot.FEET);
		if(feet.getItem() != ModuleFeature.knightSlimeBoots) {
			return;
		}

		// thing is wearing slime boots. let's get bouncyyyyy
		boolean isClient = entity.getEntityWorld().isRemote;
		if(!entity.isSneaking() && event.getDistance() > 2) {
			event.setDamageMultiplier(0);
			entity.fallDistance = 0;
			if(isClient) {
				entity.motionY *= -0.9;
				entity.isAirBorne = true;
				entity.onGround = false;
				double f = 0.91d + 0.04d;
				entity.motionX /= f;
				entity.motionZ /= f;
			}
			else {
				event.setCanceled(true); // we don't care about previous cancels, since we just bounceeeee
			}
			entity.playSound(SoundEvents.ENTITY_SLIME_SQUISH, 1f, 1f);
			SlimeBounceHandler.addBounceHandler(entity, entity.motionY);
			TinkerNetwork.sendToServer(new BouncedPacket());
		}
		else if(!isClient && entity.isSneaking()) {
			event.setDamageMultiplier(0.2f);
		}
	}

	private static final EntityEquipmentSlot[] SLOTS = {EntityEquipmentSlot.FEET, EntityEquipmentSlot.HEAD, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.CHEST};

	@SubscribeEvent
	public static void onKnockback(LivingKnockBackEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		// count slimey armor with a weight
		int bounciness = 0;
		for(int i = 0; i < SLOTS.length; i++) {
			if(entity.getItemStackFromSlot(SLOTS[i]).getItem() instanceof ItemKnightSlimeArmor) {
				bounciness++;
			}
		}
		if(bounciness == 0) {
			return;
		}

		// scale based on number of pieces worn
		event.setStrength(event.getStrength() * (1.25f + (bounciness / 8f)));
	}
}
