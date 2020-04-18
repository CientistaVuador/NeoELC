package com.cien;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.cien.data.Node;
import com.cien.data.Properties;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringTranslate;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

public class Util {
	
	protected static List<Task> tasks = new ArrayList<>();
	protected static List<ScheduledTask> scheduled = new ArrayList<>();
	protected static int TPS = 0;
	protected static Map<String, Item> items = new HashMap<>();
	protected static Map<String, String> ptBr = new HashMap<>();
	
	protected static void load() {
		System.out.println("Carregando nome de itens...");
		for (int i = 0; i < Short.MAX_VALUE; i++) {
			Item t = Item.getItemById(i);
			if (t != null) {
				String name = Item.itemRegistry.getNameForObject(t);
				items.put(name, t);
				System.out.println("Item  de ID "+i+" registrado como '"+name+"'");
			}
		}
		System.out.println("Nome de itens carregados.");
		loadPortuguese();
	}
	
	private static void loadPortuguese() {
		System.out.println("Carregando arquivos de linguagem.");
		File mods = new File("mods");
		long tamanho = 0;
		if (mods.exists() && mods.isDirectory()) {
			File[] files = mods.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					if (pathname.isFile()) {
						return pathname.getName().endsWith(".jar");
					}
					return false;
				}
			});
			for (File mod:files) {
				int carregado = 0;
				System.out.println("Procurando arquivos de linguagem em '"+mod.getName()+"'");
				try {
					ZipInputStream zip = new ZipInputStream(new BufferedInputStream(new FileInputStream(mod)));
					ZipEntry entry;
					while ((entry = zip.getNextEntry()) != null) {
						if (entry.getName().endsWith("pt_BR.lang")) {
							BufferedReader reader = new BufferedReader(new InputStreamReader(zip, "UTF-8"));
							String line;
							while ((line = reader.readLine()) != null) {
								if (!line.startsWith("//")) {
									String entryName = null;
									StringBuilder builder = new StringBuilder(64);
									boolean equals = false;
									for (char c:line.toCharArray()) {
										if (c == '=' && !equals) {
											equals = true;
											entryName = builder.toString();
											builder.setLength(0);
											continue;
										}
										builder.append(c);
									}
									String value = builder.toString();
									if (entryName != null && value.length() != 0) {
										carregado++;
										tamanho += entryName.getBytes().length;
										tamanho += value.getBytes().length;
										System.out.println("Entrada '"+entryName+"' definida como '"+value+"'");
										ptBr.put(entryName, value);
									}
								}
							}
						}
					}
					zip.close();
				} catch (Exception ex) {
					System.out.println("Erro ao abrir '"+mod.getName()+"': "+ex.getMessage());
					ex.printStackTrace();
				}
				System.out.println(carregado+" Entradas de linguagem carregadas de '"+mod.getName()+"'");
			}
		}
		System.out.println(tamanho+" Bytes de memória estão sendo ocupados pela linguagem.");
		System.out.println("Arquivos de linguagem carregados.");
		System.out.println("Alterando o StatCollector através de reflection...");
		boolean sucesso = true;
		try {
			Field stringTranslate = StatCollector.class.getDeclaredField("localizedName");
			stringTranslate.setAccessible(true);
			PortugueseStringTranslate.FALLBACK = (StringTranslate) stringTranslate.get(null);
			stringTranslate.set(null, PortugueseStringTranslate.PORTUGUESE);
		} catch (Exception ex) {
			sucesso = false;
			System.out.println("Não foi possível alterar o StatCollector: "+ex.getMessage());
			ex.printStackTrace();
		}
		if (sucesso) {
			System.out.println("Sucesso!");
		}
	}
	
	public static String translateUnlocalizedToPortuguese(String unlocalized) {
		return ptBr.get(unlocalized);
	}
	
	public static boolean containsUnlocalizedPortuguese(String unlocalized) {
		return ptBr.containsKey(unlocalized);
	}
	
	public static String getPortugueseItemName(ItemStack stack) {
		String pt = translateUnlocalizedToPortuguese(stack.getUnlocalizedName());
		if (pt == null) {
			pt = translateUnlocalizedToPortuguese(stack.getUnlocalizedName()+".name");
		}
		if (pt == null) {
			pt = getEnglishItemName(stack);
		}
		return pt;
	}
	
	public static String getEnglishItemName(ItemStack stack) {
		return StatCollector.translateToFallback(stack.getUnlocalizedName()+".name");
	}
	
	public static Node getNodeFromItemStack(String name, ItemStack s, boolean keepNbt) {
		Node n = new Node(name);
		n.setField("id", Util.getItemNameID(s.getItem()));
		n.setField("meta", Integer.toString(s.getItemDamage()));
		n.setField("amount", Integer.toString(s.stackSize));
		if (keepNbt) {
			if (s.getTagCompound() != null) {
				n.setField("nbt", s.getTagCompound().toString());
			} else {
				n.setField("nbt", "{}");
			}
		} else {
			n.setField("nbt", "{}");
		}
		return n;
	}
	
	public static ItemStack getItemStackFromNode(Node n) {
		Item t = Util.getItemFromNameID(n.getField("id"));
		int meta = Integer.parseInt(n.getField("meta"));
		int amount = Integer.parseInt(n.getField("amount"));
		String nbt = n.getField("nbt");
		ItemStack s = new ItemStack(t, amount, meta);
		try {
			s.setTagCompound((NBTTagCompound) JsonToNBT.func_150315_a(nbt));
		} catch (NBTException e) {
			e.printStackTrace();
		}
		return s;
	}
	
	public static String getItemNameID(Item t) {
		return Item.itemRegistry.getNameForObject(t);
	}
	
	public static Item getItemFromNameID(String id) {
		Item t = items.get(id);
		if (t == null) {
			throw new InvalidItemNameID("Nome ID de Item Inválido -> '"+id+"', cheque configurações ou verifique se algum mod foi removido.");
		}
		return t;
	}
	
	public static Task run(String name, Runnable r, int ticks) {
		Task t = new Task(name, r, ticks);
		tasks.add(t);
		return t;
	}
	
	public static Task run(String name, Runnable r) {
		Task t = new Task(name, r, 0);
		tasks.add(t);
		return t;
	}
	
	public static ScheduledTask schedule(String name, Runnable r, int ticks) {
		ScheduledTask t = new ScheduledTask(name, r, ticks);
		scheduled.add(t);
		return t;
	}
	
	public static EntityPlayerMP[] getOnlinePlayers() {
		ServerConfigurationManager manager = getServerManager();
		List<?> l = manager.playerEntityList;
		List<EntityPlayerMP> list = new ArrayList<>();
		for (Object o:l) {
			if (o instanceof EntityPlayerMP) {
				list.add((EntityPlayerMP)o);
			}
		}
		return list.toArray(new EntityPlayerMP[list.size()]);
	}
	
	public static boolean exist(String player) {
		return Properties.hasProperties(player);
	}
	
	public static int getHighestYAt(int x, int z, WorldServer world) {
		return world.getHeightValue(x, z);
	}
	
	public static boolean isOnline(String player) {
		for (EntityPlayerMP p:getOnlinePlayers()) {
			if (player.equals(p.getCommandSenderName())) {
				return true;
			}
		}
		return false;
	}
	
	public static EntityPlayerMP getOnlinePlayer(String player) {
		for (EntityPlayerMP p:getOnlinePlayers()) {
			if (player.equals(p.getCommandSenderName())) {
				return p;
			}
		}
		return null;
	}
	
	public static String getModExclusivePrefix() {
		return "(EXC)";
	}
	
	public static int getRealLenghtOfMessage(String message) {
		boolean color = false;
		int count = 0;
		for (char c:message.toCharArray()) {
			if (color) {
				color = false;
				continue;
			}
			if (c == '§' || c == '&') {
				color = true;
				continue;
			}
			count++;
		}
		return count;
	}
	
	public static void teleportPlayer(EntityPlayerMP player, World w, float x, float y, float z, float pitch, float yaw) {
		int oldDim = player.worldObj.provider.dimensionId;
		if (oldDim != w.provider.dimensionId) {
			player.mcServer.getConfigurationManager().transferPlayerToDimension(player, w.provider.dimensionId);
		}
		player.setPositionAndRotation(x, y, z, yaw, pitch);
		player.setPositionAndUpdate(x, y, z);
		player.addExperienceLevel(0);
		if (oldDim == 1 && oldDim != w.provider.dimensionId) {
			Util.run("Remove player from end: "+player.getDisplayName(), () -> {
				player.mcServer.getConfigurationManager().transferPlayerToDimension(player, w.provider.dimensionId);
	            player.setPositionAndUpdate(x, y, z);
	            player.getServerForPlayer().updateEntityWithOptionalForce(player, false);
			}, 2);
        }
	}
	
	public static int getTPS() {
		return TPS;
	}
	
	public static WorldServer getWorld(String name) {
		for (WorldServer sv:DimensionManager.getWorlds()) {
			if (sv.provider.getDimensionName().equals(name)) {
				return sv;
			}
		}
		return null;
	}
	
	public static ServerConfigurationManager getServerManager() {
		return FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager();
	}
	
	public static void sendMessageToEveryone(String msg) {
		ServerConfigurationManager manager = getServerManager();
		manager.sendChatMsg(new ChatComponentText(fixColors(msg)));
	}
	
	public static String fixColors(String msg) {
		StringBuilder b = new StringBuilder(64);
		char lastColor = 'f';
		boolean color = false;
		for (char c:msg.toCharArray()) {
			if (color) {
				color = false;
				lastColor = c;
			}
			if (c == '§') {
				color = true;
			}
			if (c == ' ') {
				b.append(' ');
				b.append('§');
				b.append(lastColor);
				continue;
			}
			b.append(c);
		}
		return b.toString();
	}
	
	public static String getPrefix() {
		return "§8§l[§bNeo§6ELC§8§l]§6 ";
	}
	
	public static String getErrorPrefix() {
		return "§8§l[§bNeo§6ELC§8§l]§c ";
	}
	
	private Util() {
		
	}
}
