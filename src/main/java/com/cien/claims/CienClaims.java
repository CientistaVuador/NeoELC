package com.cien.claims;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.cien.Module;
import com.cien.PositiveLocation;
import com.cien.Util;
import com.cien.data.Properties;
import com.cien.permissions.CienPermissions;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.event.world.ExplosionEvent;

public class CienClaims extends Module {

	public static final CienClaims CLAIMS = new CienClaims();
	
	private final List<Claim> claims = new ArrayList<>();
	private final List<String> blockedItems = new ArrayList<>();
	private final List<String> dangerEntity = new ArrayList<>();
	private final Properties prop = Properties.getProperties("(Module)CienClaims");
	
	private CienClaims() {
		super("CienClaims");
	}
	
	@Override
	public void preStart() {
		String[] names = Properties.getAllProperties();
		for (String name:names) {
			try {
				if (name.startsWith("(Claim)")) {
					claims.add(new Claim(Properties.getProperties(name)));
				}
			} catch (Exception ex) {
				System.out.println("Erro ao carregar claim -> "+name+" -> "+ex.getClass().getName()+": "+ex.getMessage());
			}
		}
		blockedItems.addAll(Arrays.asList(prop.getArray("blockedItems")));
		dangerEntity.addAll(Arrays.asList(prop.getArray("dangerousEntities")));
	}
	
	@Override
	public void start() {
		run(() -> {
			for (EntityPlayerMP player:Util.getOnlinePlayers()) {
				CienClaims.CLAIMS.addBlocksTo(player.getCommandSenderName(), 100);
				player.addChatMessage(Util.fixColors(Util.getPrefix()+"Você recebeu 100 blocos de claim por jogar no servidor."));
			}
		}, 5*60*20, true);
		run(() -> {
    		for (WorldServer w:Util.getWorlds()) {
    			for (Chunk k:Util.getLoadedChunksOf(w)) {
    				int entities = 0;
    				for (List<?> l:k.entityLists) {
    					for (Object obj:l) {
    						if (!(obj instanceof EntityItem)) {
    							entities++;
    						}
    					}
    				}
    				boolean advice = false;
    				com.cien.claims.Claim c = null;
    				if (entities > 100) {
    					for (List<?> l:k.entityLists) {
        					for (Object obj:l) {
        						if (!(obj instanceof EntityItem)) {
        							Entity e = (Entity)obj;
        							e.setDead();
        							if (c == null && !advice) {
        								c = CienClaims.CLAIMS.getClaimInside(new PositiveLocation((int)e.posX, (int)e.posY, (int)e.posZ), w);
        							}
        							advice = true;
        						}
        					}
        				}
    				}
    				if (advice) {
    					if (c != null) {
    						EntityPlayerMP owner = Util.getOnlinePlayer(c.getOwner());
    						if (owner != null) {
    							Util.sendMessage(owner, Util.getErrorPrefix()+"Aviso: Foram detectadas mais de 100 Entidades em um chunk de seu claim, elas foram removidas.");
    						}
    					}
    				}
    			}
    		}
    		for (com.cien.claims.Claim c:CienClaims.CLAIMS.getClaims()) {
    			int entities = 0;
    			Entity[] ents = c.getEntities();
    			if (ents == null) {
    				continue;
    			}
    			for (Entity e:ents) {
    				if (!(e instanceof EntityItem)) {
    					if (!e.isDead) {
    						entities++;
    					}
    				}
    			}
    			if (entities > c.getSize()) {
    				for (Entity e:ents) {
        				if (!(e instanceof EntityItem)) {
        					if (!e.isDead) {
        						e.setDead();
        					}
        				}
        			}
    				EntityPlayerMP player = Util.getOnlinePlayer(c.getOwner());
        			if (player != null) {
        				Util.sendMessage(player, Util.getErrorPrefix()+"Aviso: Foram detectadas mais de "+c.getSize()+" Entidades em seu claim, elas foram removidas.");
        			}
    			}
    		}
    	}, 5*60*20, true);
	}
	
	@Override
	public void registerCommands(FMLServerStartingEvent event) {
		event.registerServerCommand(new com.cien.claims.commands.Blocks());
    	event.registerServerCommand(new com.cien.claims.commands.Claim());
    	event.registerServerCommand(new com.cien.claims.commands.ClaimAtual());
    	event.registerServerCommand(new com.cien.claims.commands.Pos1());
    	event.registerServerCommand(new com.cien.claims.commands.Pos2());
    	event.registerServerCommand(new com.cien.claims.commands.Expand());
    	event.registerServerCommand(new com.cien.claims.commands.SetFlag());
    	event.registerServerCommand(new com.cien.claims.commands.VerFlags());
    	event.registerServerCommand(new com.cien.claims.commands.SetBlockedItem());
    	event.registerServerCommand(new com.cien.claims.commands.BlockedItems());
    	event.registerServerCommand(new com.cien.claims.commands.EntidadesClaim());
    	event.registerServerCommand(new com.cien.claims.commands.SetDangerousEntity());
    	event.registerServerCommand(new com.cien.claims.commands.DangerousEntities());
    	event.registerServerCommand(new com.cien.claims.commands.MeusClaims());
    	event.registerServerCommand(new com.cien.claims.commands.TpClaim());
    	event.registerServerCommand(new com.cien.claims.commands.Abandonclaim());
    	event.registerServerCommand(new com.cien.claims.commands.TransferirClaim());
    	event.registerServerCommand(new com.cien.claims.commands.TrustList());
    	event.registerServerCommand(new com.cien.claims.commands.Trust());
    	event.registerServerCommand(new com.cien.claims.commands.ETrust());
    	event.registerServerCommand(new com.cien.claims.commands.Untrust());
    	event.registerServerCommand(new com.cien.claims.commands.IgnoreClaims());
    	event.registerServerCommand(new com.cien.claims.commands.MoveClaim());
	}
	
	public void verifyChunksAndClaims() {
		for (WorldServer w:Util.getWorlds()) {
			for (Chunk k:Util.getLoadedChunksOf(w)) {
				int entities = 0;
				for (List<?> l:k.entityLists) {
					for (Object obj:l) {
						if (!(obj instanceof EntityItem)) {
							entities++;
						}
					}
				}
				boolean advice = false;
				com.cien.claims.Claim c = null;
				if (entities > 100) {
					for (List<?> l:k.entityLists) {
    					for (Object obj:l) {
    						if (!(obj instanceof EntityItem)) {
    							Entity e = (Entity)obj;
    							e.setDead();
    							if (c == null && !advice) {
    								c = CienClaims.CLAIMS.getClaimInside(new PositiveLocation((int)e.posX, (int)e.posY, (int)e.posZ), w);
    							}
    							advice = true;
    						}
    					}
    				}
				}
				if (advice) {
					if (c != null) {
						EntityPlayerMP owner = Util.getOnlinePlayer(c.getOwner());
						if (owner != null) {
							Util.sendMessage(owner, Util.getErrorPrefix()+"Aviso: Foram detectadas mais de 100 Entidades em um chunk de seu claim, elas foram removidas.");
						}
					}
				}
			}
		}
		for (com.cien.claims.Claim c:CienClaims.CLAIMS.getClaims()) {
			int entities = 0;
			Entity[] ents = c.getEntities();
			if (ents == null) {
				continue;
			}
			for (Entity e:ents) {
				if (!(e instanceof EntityItem)) {
					if (!e.isDead) {
						entities++;
					}
				}
			}
			if (entities > c.getSize()) {
				for (Entity e:ents) {
    				if (!(e instanceof EntityItem)) {
    					if (!e.isDead) {
    						e.setDead();
    					}
    				}
    			}
				EntityPlayerMP player = Util.getOnlinePlayer(c.getOwner());
    			if (player != null) {
    				Util.sendMessage(player, Util.getErrorPrefix()+"Aviso: Foram detectadas mais de "+c.getSize()+" Entidades em seu claim, elas foram removidas.");
    			}
			}
		}
	}
	
	public String[] getDangerousEntities() {
		return dangerEntity.toArray(new String[dangerEntity.size()]);
	}
	
	public boolean hasBlockedItem(String s) {
		for (String f:blockedItems.toArray(new String[blockedItems.size()])) {
			if (f.equals(s)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasDangerousEntity(String s) {
		for (String f:dangerEntity.toArray(new String[dangerEntity.size()])) {
			if (f.equals(s)) {
				return true;
			}
		}
		return false;
	}
	
	public void setDangerousEntity(String s, boolean b) {
		for (String f:dangerEntity.toArray(new String[dangerEntity.size()])) {
			if (f.equals(s)) {
				if (!b) {
					dangerEntity.remove(s);
				}
				return;
			}
		}
		dangerEntity.add(s);
		prop.setArray("dangerousEntities", dangerEntity.toArray(new String[dangerEntity.size()]));
	}
	
	public boolean isIgnoringClaims(String player) {
		Properties prop = Properties.getProperties(player);
		if (!CienPermissions.PERMISSIONS.hasPermission(player, "admin.ignoreclaims")) {
			return false;
		}
		String ig = prop.get("ignoringClaims");
		if (ig == null) {
			return false;
		}
		return Boolean.parseBoolean(ig);
	}
	
	public void setIgnoringClaims(String player, boolean b) {
		Properties prop = Properties.getProperties(player);
		prop.set("ignoringClaims", Boolean.toString(b));
	}
	
	public void setBlockedItem(String s, boolean b) {
		for (String f:blockedItems.toArray(new String[blockedItems.size()])) {
			if (f.equals(s)) {
				if (!b) {
					blockedItems.remove(s);
				}
				return;
			}
		}
		blockedItems.add(s);
		prop.setArray("blockedItems", blockedItems.toArray(new String[blockedItems.size()]));
	}
	
	public String[] blockedItems() {
		return blockedItems.toArray(new String[blockedItems.size()]);
	}
	
	public int nextID() {
		String id = prop.get("nextID");
		if (id == null) {
			prop.set("nextID", "1");
			return 0;
		}
		int i = Integer.parseInt(id);
		prop.set("nextID", Integer.toString(i+1));
		return i;
	}
	
	public void addClaim(Claim c) {
		claims.add(c);
	}
	
	public boolean removeClaim(Claim c) {
		return claims.remove(c);
	}
	
	public Claim[] getClaims() {
		return claims.toArray(new Claim[claims.size()]);
	}
	
	public Claim[] getClaims(String owner) {
		List<Claim> c = new ArrayList<>();
		for (Claim f:getClaims()) {
			if (f.getOwner().equals(owner)) {
				c.add(f);
			}
		}
		return c.toArray(new Claim[c.size()]);
	}
	
	public Claim getClaimInside(EntityPlayerMP player) {
		for (Claim f:getClaims()) {
			if (f.isInside(player)) {
				return f;
			}
		}
		return null;
	}
	
	public Claim getClaimInside(PositiveLocation loc, WorldServer w) {
		for (Claim f:getClaims()) {
			if (!f.getWorld().equals(w.provider.getDimensionName())) {
				continue;
			}
			if (f.isInside(loc)) {
				return f;
			}
		}
		return null;
	}
	
	public Claim getClaimInsideShield(PositiveLocation loc, WorldServer w) {
		for (Claim f:getClaims()) {
			if (!f.getWorld().equals(w.provider.getDimensionName())) {
				continue;
			}
			if (f.getShield().isInside(loc)) {
				return f;
			}
		}
		return null;
	}
	
	public long getBlocksOf(String player) {
		Properties prop = Properties.getProperties(player);
		String blocks = prop.get("claimBlocks");
		if (blocks == null) {
			return 0;
		}
		try {
			return Long.parseLong(blocks);
		} catch (NumberFormatException ex) {
			return 0;
		}
	}
	
	public Claim getClaim(int id) {
		for (Claim c:getClaims()) {
			if (c.getId() == id) {
				return c;
			}
		}
		return null;
	}
	
	public void setBlocksOf(String player, long blocks) {
		Properties prop = Properties.getProperties(player);
		prop.set("claimBlocks", Long.toString(blocks));
	}
	
	public void addBlocksTo(String player, long blocks) {
		setBlocksOf(player, getBlocksOf(player)+blocks);
	}
	
	public void removeBlocksOf(String player, long blocks) {
		addBlocksTo(player, blocks * -1);
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = false)
	public void onEntityDamage(LivingHurtEvent event) {
		EntityLivingBase ent = event.entityLiving;
		if (ent instanceof EntityPlayerMP) {
			return;
		}
		Entity damager = event.source.getSourceOfDamage();
		Claim inside = getClaimInside(new PositiveLocation((int)ent.posX, (int)ent.posY, (int)ent.posZ), (WorldServer)ent.worldObj);
		if (inside != null) {
			String name = EntityList.getEntityString(ent);
			if (inside.getFlag("permitirDanoEm#"+name) || inside.getFlag("permitirDanoEm#*")) {
				return;
			}
			if (damager instanceof EntityPlayerMP) {
				EntityPlayerMP mp = (EntityPlayerMP)damager;
				if (mp.getCommandSenderName().equals(inside.getOwner())) {
					return;
				}
				if (inside.getFlag("danificarEntidades#"+mp.getCommandSenderName()) || inside.getFlag("danificarEntidades#*")) {
					return;
				}
			}
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = false)
	public void onExplosion(ExplosionEvent.Start event) {
		Claim c = getClaimInsideShield(new PositiveLocation((int)event.explosion.explosionX, (int)event.explosion.explosionY, (int)event.explosion.explosionZ), (WorldServer)event.world);
		if (c != null) {
			if (!c.getFlag("permitirExplosao")) {
				event.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = false)
	public void onBlockBreak(BreakEvent event) {
		if (event.getPlayer() == null) {
			return;
		}
		EntityPlayerMP player = (EntityPlayerMP) event.getPlayer();
		if (CienClaims.CLAIMS.isIgnoringClaims(player.getCommandSenderName())) {
			return;
		}
		com.cien.claims.Claim c = CienClaims.CLAIMS.getClaimInside(new PositiveLocation(event.x, event.y, event.z), (WorldServer)event.world);
		if (c == null) {
			return;
		}
		if (c.getOwner().equals(player.getCommandSenderName())) {
			return;
		}
		if (c.getFlag("permitirQuebrar#"+player.getCommandSenderName())) {
			return;
		}
		if (c.getFlag("permitirQuebrar#*")) {
			return;
		}
		if (c.getFlag("quebrar#"+Block.getIdFromBlock(event.block))) {
			return;
		}
		if (c.getFlag("quebrar#"+Block.getIdFromBlock(event.block)+":*")) {
			return;
		}
		if (c.getFlag("quebrar#"+Block.getIdFromBlock(event.block)+":"+event.blockMetadata)) {
			return;
		}
		if (c.getFlag("quebrar#*:*")) {
			return;
		}
		if (c.getFlag("quebrar#*")) {
			return;
		}
		event.setCanceled(true);
	}
	
	public Claim[] getClaimsOfWorld(String world) {
		List<Claim> list = new ArrayList<>();
		for (Claim c:getClaims()) {
			if (c.getWorld().equals(world)) {
				list.add(c);
			}
		}
		return list.toArray(new Claim[list.size()]);
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = false)
	public void onBlockPlace(PlaceEvent event) {
		if (event.player == null) {
			return;
		}
		EntityPlayerMP player = (EntityPlayerMP) event.player;
		if (isIgnoringClaims(player.getCommandSenderName())) {
			return;
		}
		com.cien.claims.Claim c = CienClaims.CLAIMS.getClaimInside(new PositiveLocation(event.x, event.y, event.z), (WorldServer)event.world);
		if (c == null) {
			return;
		}
		if (c.getOwner().equals(player.getCommandSenderName())) {
			return;
		}
		if (c.getFlag("permitirColocar#"+player.getCommandSenderName())) {
			return;
		}
		if (c.getFlag("permitirColocar#*")) {
			return;
		}
		if (c.getFlag("colocar#"+Block.getIdFromBlock(event.block))) {
			return;
		}
		if (c.getFlag("colocar#"+Block.getIdFromBlock(event.block)+":*")) {
			return;
		}
		if (c.getFlag("colocar#"+Block.getIdFromBlock(event.block)+":"+event.blockMetadata)) {
			return;
		}
		if (c.getFlag("colocar#*:*")) {
			return;
		}
		if (c.getFlag("colocar#*")) {
			return;
		}
		event.setCanceled(true);
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = false)
	public void onItemUse(PlayerInteractEvent event) {
		if (event.entityPlayer == null) {
			return;
		}
		EntityPlayerMP player = (EntityPlayerMP) event.entityPlayer;
		if (isIgnoringClaims(player.getCommandSenderName())) {
			return;
		}
		if (player.getCurrentEquippedItem() == null) {
			return;
		}
		com.cien.claims.Claim c = CienClaims.CLAIMS.getClaimInside(new PositiveLocation((int)player.posX, (int)player.posY, (int)player.posZ), (WorldServer)event.world);
		if (c == null) {
			return;
		}
		if (c.getOwner().equals(player.getCommandSenderName())) {
			return;
		}
		if (c.getFlag("permitirUsarItem#*")) {
			return;
		}
		if (c.getFlag("permitirUsarItem#"+player.getCommandSenderName())) {
			return;
		}
		if (c.getFlag("usarItem#*")) {
			return;
		}
		if (c.getFlag("usarItem#*:*")) {
			return;
		}
		if (c.getFlag("usarItem#"+Item.getIdFromItem(player.getCurrentEquippedItem().getItem()))) {
			return;
		}
		if (c.getFlag("usarItem#"+Item.getIdFromItem(player.getCurrentEquippedItem().getItem())+":*")) {
			return;
		}
		if (c.getFlag("usarItem#"+Item.getIdFromItem(player.getCurrentEquippedItem().getItem())+":"+player.getCurrentEquippedItem().getItemDamage())) {
			return;
		}
		event.setCanceled(true);
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = false)
	public void onBlockUse(PlayerInteractEvent event) {
		if (event.entityPlayer == null) {
			return;
		}
		EntityPlayerMP player = (EntityPlayerMP) event.entityPlayer;
		if (isIgnoringClaims(player.getCommandSenderName())) {
			return;
		}
		com.cien.claims.Claim c = CienClaims.CLAIMS.getClaimInside(new PositiveLocation(event.x, event.y, event.z), (WorldServer)event.world);
		Block block = event.world.getBlock(event.x, event.y, event.z);
		if (block == null) {
			return;
		}
		if (Block.getIdFromBlock(block) == 0) {
			return;
		}
		if (c == null) {
			return;
		}
		if (!event.action.equals(PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) && !event.action.equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)) {
			return;
		}
		if (c.getOwner().equals(player.getCommandSenderName())) {
			return;
		}
		if (c.getFlag("permitirUsarBloco#*")) {
			return;
		}
		if (c.getFlag("permitirUsarBloco#"+player.getCommandSenderName())) {
			return;
		}
		if (c.getFlag("usarBloco#*")) {
			return;
		}
		if (c.getFlag("usarBloco#*:*")) {
			return;
		}
		if (c.getFlag("usarBloco#"+Block.getIdFromBlock(block))) {
			return;
		}
		if (c.getFlag("usarBloco#"+Block.getIdFromBlock(block)+":*")) {
			return;
		}
		if (c.getFlag("usarBloco#"+Block.getIdFromBlock(block)+":"+event.world.getBlockMetadata(event.x, event.y, event.z))) {
			return;
		}
		event.setCanceled(true);
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerWalk(LivingUpdateEvent event) {
		if (event.entityLiving instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP)event.entityLiving;
			if (isIgnoringClaims(player.getCommandSenderName())) {
				return;
			}
			com.cien.claims.Claim c = CienClaims.CLAIMS.getClaimInside(new PositiveLocation((int)player.posX, (int)player.posY, (int)player.posZ), (WorldServer)player.worldObj);
			if (c == null) {
				return;
			}
			if (c.getOwner().equals(player.getCommandSenderName())) {
				return;
			}
			if (c.getFlag("permitirEntrar#*")) {
				return;
			}
			if (c.getFlag("permitirEntrar#"+player.getCommandSenderName())) {
				return;
			}
			PositiveLocation loc = kickOutOfClaimPosition(player, c);
			int x = loc.getX();
			int z = loc.getZ();
			int y = player.worldObj.getHeightValue(x, z);
			Util.teleportPlayer(player, player.worldObj, x, y, z, player.rotationPitch, player.rotationYaw);
			CienClaims.CLAIMS.run(() -> {
				Util.teleportPlayer(player, player.worldObj, x, y, z, player.rotationPitch, player.rotationYaw);
			}, 10);
		}
	}
	
	private PositiveLocation kickOutOfClaimPosition(EntityPlayerMP player, Claim c) {
		PositiveLocation playerPos = new PositiveLocation((int)player.posX, (int)player.posY, (int)player.posZ);
		int left_up = playerPos.distanceXZ(c.getUpperLeftCorner());
		int right_up = playerPos.distanceXZ(c.getUpperRightCorner());
		int left_down = playerPos.distanceXZ(c.getDownLeftCorner());
		int right_down = playerPos.distanceXZ(c.getDownRightCorner());
		
		int[] values = {left_up, left_down, right_down, right_up};
		Arrays.sort(values);
		int small = values[0];
		
		if (small == left_up) {
			return c.getUpperLeftCorner().add(-3, 0, 3);
		}
		if (small == right_up) {
			return c.getUpperRightCorner().add(3, 0, 3);
		}
		if (small == left_down) {
			return c.getDownLeftCorner().add(-3, 0, -3);
		}
		if (small == right_down) {
			return c.getDownRightCorner().add(3, 0, -3);
		}
		return c.getUpperLeftCorner().add(-3, 0, 3);
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onEntityInteract(EntityInteractEvent event) {
		EntityPlayerMP player = (EntityPlayerMP) event.entityPlayer;
		if (!(event.target instanceof EntityPlayerMP)) {
			return;
		}
		if (player == null) {
			return;
		}
		ItemStack hand = player.getCurrentEquippedItem();
		if (hand == null) {
			return;
		}
		Claim c = getClaimInside((EntityPlayerMP) event.target);
		if (c == null) {
			return;
		}
		String handName = Util.getItemNameID(hand.getItem());
		if (hasBlockedItem(handName+":"+hand.getItemDamage())) {
			if (!c.getFlag("permitirUsarItemBloqueado#"+player.getCommandSenderName()) && !c.getFlag("permitirUsarItemBloqueado#*")) {
				event.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = false)
	public void onBlockedItemUse(PlayerInteractEvent event) {
		if (event.entityPlayer == null) {
			return;
		}
		EntityPlayerMP player = (EntityPlayerMP) event.entityPlayer;
		if (isIgnoringClaims(player.getCommandSenderName())) {
			return;
		}
		if (player.getCurrentEquippedItem() == null) {
			return;
		}
		ItemStack hand = player.getCurrentEquippedItem();
		if (!CienClaims.CLAIMS.hasBlockedItem(Util.getItemNameID(hand.getItem())+":"+hand.getItemDamage())) {
			return;
		}
		for (Claim claim:CienClaims.CLAIMS.getClaims()) {
			if (!claim.getWorld().equals(player.worldObj.provider.getDimensionName())) {
				continue;
			}
			if (claim.getShield().isInside(player)) {
				if (claim.getOwner().equals(player.getCommandSenderName())) {
					continue;
				}
				if (claim.getFlag("permitirUsarItem#*")) {
					continue;
				}
				if (claim.getFlag("permitirUsarItem#"+player.getCommandSenderName())) {
					continue;
				}
				if (claim.getFlag("usarItem#*")) {
					continue;
				}
				if (claim.getFlag("usarItem#*:*")) {
					continue;
				}
				if (claim.getFlag("usarItem#"+Item.getIdFromItem(player.getCurrentEquippedItem().getItem()))) {
					continue;
				}
				if (claim.getFlag("usarItem#"+Item.getIdFromItem(player.getCurrentEquippedItem().getItem())+":*")) {
					continue;
				}
				if (claim.getFlag("usarItem#"+Item.getIdFromItem(player.getCurrentEquippedItem().getItem())+":"+player.getCurrentEquippedItem().getItemDamage())) {
					continue;
				}
				event.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = false)
	public void onEntityDamage(LivingAttackEvent event) {
		if (event.entity instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) event.entity;
			Claim c = CienClaims.CLAIMS.getClaimInside(player);
			if (c != null) {
				if (!c.getFlag("permitirDano")) {
					event.setCanceled(true);
				}
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onEntityExists(ServerTickEvent event) {
		for (WorldServer sv:DimensionManager.getWorlds()) {
			List<?> entity = sv.loadedEntityList;
			entityFor:
			for (Object o:entity.toArray(new Object[entity.size()])) {
				if (o instanceof Entity) {
					if (!(o instanceof EntityPlayerMP)) {
						Entity n = (Entity)o;
						String name = EntityList.getEntityString(n);
						if (!hasDangerousEntity(name)) {
							continue entityFor;
						}
						PositiveLocation loc = new PositiveLocation((int)n.posX, (int)n.posY, (int)n.posZ);
						Claim[] claims = getClaimsOfWorld(sv.provider.getDimensionName());
						claimFor:
						for (Claim f:claims) {
							if (f.getShield().isInside(loc)) {
								if (f.getFlag("permitirEntidade#*")) {
									continue claimFor;
								}
								if (f.getFlag("permitirEntidade#"+name)) {
									continue claimFor;
								}
								EntityPlayerMP pl = Util.getOnlinePlayer(f.getOwner());
								if (pl != null) {
									if (f.getShield().isInside(pl)) {
										Properties prop = Properties.getProperties(pl.getCommandSenderName());
										Object timeObj = prop.getMemory("entityAdviceTime");
										long time = 0;
										if (timeObj != null) {
											time = (long) timeObj;
										}
										if (System.currentTimeMillis() >= time) {
											pl.addChatComponentMessage(Util.fixColors(Util.getErrorPrefix()+"A Entidade '"+name+"' foi removida pois é considerada perigosa, dê permissão para ela com /setflag permitirEntidade#"+name));
											prop.setMemory("entityAdviceTime", System.currentTimeMillis()+10000);
										}
									}
								}
								n.setDead();
								break claimFor;
							}
						}
					}
				}
			}
		}
	}
}
