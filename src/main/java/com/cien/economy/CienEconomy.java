package com.cien.economy;

import java.util.ArrayList;
import java.util.List;

import com.cien.PositiveLocation;
import com.cien.Util;
import com.cien.claims.Claim;
import com.cien.data.Properties;
import com.cien.permissions.CienPermissions;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

public class CienEconomy {
	
	public static final CienEconomy ECONOMY = new CienEconomy();
	
	private final Properties data = Properties.getProperties("(Module)CienEconomy");
	private final List<Shop> shops = new ArrayList<>();
	
	private CienEconomy() {
		Util.run("Load Shops", () -> {
			System.out.println("Carregando Lojas...");
			for (String s:Properties.getAllProperties()) {
				try {
					if (!s.startsWith("(Shop)")) {
						continue;
					}
					shops.add(new Shop(Properties.getProperties(s)));
				} catch (Exception ex) {
					System.out.println("Erro ao carregar loja "+s+": "+ex.getMessage());
				}
			}
			System.out.println("Lojas Carregadas");
		});
		Util.schedule("Advice Player", () -> {
			for (Shop s:getShops()) {
				if (s.isValid()) {
					for (ChestShop f:s.getChestShops()) {
						if (f.isValid()) {
							PositiveLocation shopPos = new PositiveLocation(f.getX(), f.getY(), f.getZ());
							player_for:
							for (EntityPlayerMP player:Util.getOnlinePlayers()) {
								if (!s.getWorld().equals(player.worldObj.provider.getDimensionName())) {
									continue player_for;
								}
								PositiveLocation playerPos = new PositiveLocation((int)player.posX, (int)player.posY, (int)player.posZ);
								float dist = playerPos.distance(shopPos);
								if (dist < 10) {
									Properties prop = Properties.getProperties(player.getCommandSenderName());
									Object nextUpdate = prop.getMemory("..NextSHOPUpdate");
									if (nextUpdate == null || System.currentTimeMillis() > (long)nextUpdate) {
										prop.setMemory("..NextSHOPUpdate", System.currentTimeMillis() + 30*60*1000);
										Util.sendMessage(player, Util.getPrefix()+"Como usar uma loja:");
										Util.sendMessage(player, " §6Clique com o direito na placa da loja e");
										Util.sendMessage(player, " §apara comprar digite /c <quantidade>");
										Util.sendMessage(player, " §ce para vender digite /v <quantidade>");
										Util.sendMessage(player, Util.getPrefix()+"Lembre se: ");
										Util.sendMessage(player, " §6Placas com o preço em §cvermelho §6são para comprar e");
										Util.sendMessage(player, " §6placas com o preço em §averde §6são para vender.");
									}
								}
							}
						}
					}
				}
			}
		}, 5);
	}
	
	public void addShop(Shop s) {
		shops.add(s);
	}
	
	public boolean removeShop(Shop s) {
		return shops.remove(s);
	}
	
	public Shop[] getShops() {
		return shops.toArray(new Shop[shops.size()]);
	}
	
	public Shop getShopByID(int id) {
		for (Shop s:getShops()) {
			if (s.getID() == id) {
				return s;
			}
		}
		return null;
	}
	
	public Shop getShopOf(String player) {
		for (Shop s:getShops()) {
			if (s.getOwner().equals(player)) {
				return s;
			}
		}
		return null;
	}
	
	public int nextID() {
		String id = data.get("nextID");
		if (id == null) {
			data.set("nextID", "1");
			return 0;
		}
		int next = Integer.parseInt(id);
		data.set("nextID", Integer.toString(next+1));
		return next;
	}
	
	public LongDecimal getPlayerMoney(String player) {
		Properties prop = Properties.getProperties(player);
		String money = prop.get("money");
		if (money == null) {
			return LongDecimal.valueOf(0);
		}
		return LongDecimal.parse(money);
	}
	
	public void setPlayerMoney(String player, LongDecimal dec) {
		Properties prop = Properties.getProperties(player);
		prop.set("money", dec.toString());
	}
	
	public void addPlayerMoney(String player, LongDecimal dec) {
		setPlayerMoney(player, getPlayerMoney(player).sum(dec));
	}
	
	public boolean removePlayerMoney(String player, LongDecimal dec) {
		LongDecimal money = getPlayerMoney(player);
		if (money.isBiggerThan(dec) || money.equals(dec)) {
			LongDecimal result = money.minus(dec);
			setPlayerMoney(player, result);
			return true;
		}
		return false;
	}
	
	public boolean canRemovePlayerMoney(String player, LongDecimal dec) {
		LongDecimal money = getPlayerMoney(player);
		if (money.isBiggerThan(dec) || money.equals(dec)) {
			return true;
		}
		return false;
	}
	
	public ChestShop getChestShop(String world, int x, int y, int z) {
		for (Shop s:getShops()) {
			ChestShop h = s.getChestShop(world, x, y, z);
			if (h != null) {
				return h;
			}
		}
		return null;
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public void onPlayerInteractWithShop(PlayerInteractEvent event) {
		EntityPlayerMP player = (EntityPlayerMP) event.entityPlayer;
		if (event.action.equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)) {
			ChestShop f = getChestShop(player.worldObj.provider.getDimensionName(), event.x, event.y, event.z);
			if (f != null) {
				if (player.getCommandSenderName().equals(f.getOwner())) {
					Util.sendMessage(player, Util.getErrorPrefix()+"Você não pode comprar ou vender na própria loja.");
				} else {
					event.setCanceled(true);
					if (!f.isValid()) {
						Util.sendMessage(player, Util.getErrorPrefix()+"Loja Inválida.");
					} else {
						Properties prop = Properties.getProperties(player.getCommandSenderName());
						if (prop.getMemory("SHOP_ACTION") != null) {
							Util.sendMessage(player, Util.getErrorPrefix()+"Aguarde...");
							return;
						}
						if (f.isBuy()) {
							Util.sendMessage(player, Util.getPrefix()+"Comprando "+f.getItem().getDisplayName()+":");
						} else {
							Util.sendMessage(player, Util.getPrefix()+"Vendendo "+f.getItem().getDisplayName()+":");
						}
						Util.sendMessage(player, "  §6Dono: "+f.getOwner());
						Util.sendMessage(player, "  §6Preço: C$ "+f.getPrice().toFormattedString());
						if (f.isBuy()) {
							Util.sendMessage(player, Util.getPrefix()+"Use /c <Quantidade> para comprar!");
							Util.sendMessage(player, Util.getPrefix()+"ou /c 0 para cancelar.");
						} else {
							Util.sendMessage(player, Util.getPrefix()+"Use /v <Quantidade> para vender!");
							Util.sendMessage(player, Util.getPrefix()+"ou /v 0 para cancelar.");
						}
						prop.setMemory("SHOP_ACTION", f);
						Util.run("Cancel Shop Action for "+player.getCommandSenderName(), () -> {
							if (prop.getMemory("SHOP_ACTION") != null) {
								EntityPlayerMP p = Util.getOnlinePlayer(prop.getName());
								prop.setMemory("SHOP_ACTION", null);
								if (p != null) {
									Util.sendMessage(p, Util.getErrorPrefix()+"Compra/venda cancelada.");
								}
							}
						}, 20*10);
					}
				}
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = false)
	public void onBlockBreak(BreakEvent event) {
		EntityPlayerMP player = (EntityPlayerMP) event.getPlayer();
		if (player != null) {
			ChestShop f = getChestShop(player.worldObj.provider.getDimensionName(), event.x, event.y, event.z);
			if (f != null) {
				if (f.getOwner().equals(player.getCommandSenderName())) {
					f.shop.removeChestShop(f);
					Util.sendMessage(player, Util.getPrefix()+"Loja Removida.");
				} else if (CienPermissions.PERMISSIONS.hasPermission(player.getCommandSenderName(), "admin.eco")){
					f.shop.removeChestShop(f);
					Util.sendMessage(player, Util.getPrefix()+"Loja Removida.");
				} else {
					event.setCanceled(true);
					Util.sendMessage(player, Util.getErrorPrefix()+"Sem Permissão para remover.");
				}
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = false)
	public void onPlayerInteract(PlayerInteractEvent event) {
		EntityPlayerMP player = (EntityPlayerMP) event.entityPlayer;
		if (event.action.equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)) {
			Properties prop = Properties.getProperties(player.getCommandSenderName());
			Object[] mem = (Object[])prop.getMemory("SHOP_TO_BUILD");
			if (mem != null) {
				boolean buy = (boolean) mem[0];
				LongDecimal preco = (LongDecimal) mem[1];
				boolean nbt = (boolean) mem[2];
				ItemStack item = (ItemStack) mem[3];
				boolean unlimited = (boolean) mem[4];
				Shop shop = CienEconomy.ECONOMY.getShopOf(player.getCommandSenderName());
				if (shop == null) {
					Util.sendMessage(player, Util.getErrorPrefix()+"Você deve ter uma loja definida para isso.");
					return;
				}
				if (preco.isNegative()) {
					Util.sendMessage(player, Util.getErrorPrefix()+"O Preço é negativo.");
					return;
				}
				ChestShop chest = new ChestShop(item, nbt, buy, preco, event.x, event.y+1, event.z, player.worldObj.provider.getDimensionName(), player.getCommandSenderName(), unlimited);
				if (chest.placeSignAndChest(Util.getPlayerDirectionReversed(player))) {
					shop.addChestShop(chest);
					prop.setMemory("SHOP_TO_BUILD", null);
				} else {
					Claim current = chest.getCurrentClaim();
					if (current == null) {
						Util.sendMessage(player, Util.getErrorPrefix()+"Você deve estar em um claim.");
					}
					if (current != null) {
						if (!current.getOwner().equals(chest.getOwner())) {
							Util.sendMessage(player, Util.getErrorPrefix()+"O Claim deve ser seu.");
						}
					}
					if (chest.hasChestConflict()) {
						Util.sendMessage(player, Util.getErrorPrefix()+"Não pode ter conflito de baús.");
					}
					if (!chest.canPlaceChest()) {
						Util.sendMessage(player, Util.getErrorPrefix()+"A Loja deve possuir dois blocos livres acima do selecionado.");
					}
				}
			}
		}
	}
}
