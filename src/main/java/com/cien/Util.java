package com.cien;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import com.cien.data.Node;
import com.cien.data.Properties;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringTranslate;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;

public class Util {
	
	protected static List<Task> tasks = new ArrayList<>();
	protected static List<ScheduledTask> scheduled = new ArrayList<>();
	protected static int TPS = 0;
	protected static Map<String, Item> items = new HashMap<>();
	protected static Map<String, String> ptBr = new HashMap<>();
	protected static Map<String, String> ptBrCache = new HashMap<>();
	
	protected static void load() {
		System.out.println("Carregando nome de itens...");
		for (int i = 0; i < Short.MAX_VALUE; i++) {
			Item t = Item.getItemById(i);
			if (t != null) {
				String name = Item.itemRegistry.getNameForObject(t);
				items.put(name, t);
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
		System.out.println("Alterando o StatCollector através de reflection para português.");
		boolean sucesso = true;
		try {
			Field[] stringTranslateFields = StringTranslate.class.getDeclaredFields();
			StringTranslate fallback = null;
			for (Field f:stringTranslateFields) {
				f.setAccessible(true);
				if (Modifier.isStatic(f.getModifiers())) {
					Object obj = f.get(null);
					if (obj.getClass() == StringTranslate.class) {
						fallback = (StringTranslate) obj;
						break;
					}
				}
			}
			if (fallback == null) {
				throw new NullPointerException("Instância do StringTranslate não encontrada.");
			}
			Field[] statCollectorFields = StatCollector.class.getDeclaredFields();
			Field statCollector = null;
			for (Field f:statCollectorFields) {
				f.setAccessible(true);
				if (Modifier.isStatic(f.getModifiers())) {
					if (f.get(null) == fallback) {
						statCollector = f;
						break;
					}
				}
			}
			if (statCollector == null) {
				throw new NullPointerException("Campo StringTranslate do StatCollector não encontrado.");
			}
			statCollector.set(null, PortugueseStringTranslate.PORTUGUESE);
			PortugueseStringTranslate.FALLBACK = fallback;
		} catch (Exception ex) {
			sucesso = false;
			System.out.println("Não foi possível alterar o StatCollector: "+ex.getMessage());
			ex.printStackTrace();
		}
		if (sucesso) {
			System.out.println("StatCollector alterado para Português.");
		}
	}
	
	public static int getPlayerDirection(EntityPlayerMP player) {
		return MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
	}
	
	public static Chunk[] getLoadedChunksOf(WorldServer w) {
		List<?> loaded = w.theChunkProviderServer.loadedChunks;
		Chunk[] chunk = new Chunk[loaded.size()];
		for (int i = 0; i < loaded.size(); i++) {
			chunk[i] = (Chunk) loaded.get(i);
		}
		return chunk;
	}
	
	public static int getPlayerDirectionReversed(EntityPlayerMP player) {
		int dir = getPlayerDirection(player);
		switch (dir) {
		case 0:
			return 2;
		case 1:
			return 3;
		case 2:
			return 0;
		case 3:
			return 1;
		}
		return 0;
	}
	
	public static String getPlayerInexact(String p) {
		EntityPlayerMP s = getOnlinePlayerInexact(p);
		if (s != null) {
			return s.getCommandSenderName();
		}
		p = p.toLowerCase();
		String[] prop = Properties.getAllProperties();
		List<String> f = new ArrayList<>();
		for (int i = 0; i < prop.length; i++) {
			String g = prop[i];
			if (g.startsWith("(")) {
				continue;
			}
			f.add(g);
		}
		
		String[] unfiltered = f.toArray(new String[f.size()]);
		String[] filtered = new String[unfiltered.length];
		for (int i = 0; i < filtered.length; i++) {
			filtered[i] = unfiltered[i].toLowerCase();
		}
		
		int index = 0;
		for (String w:filtered) {
			if (w.equals(p)) {
				return unfiltered[index];
			}
			index++;
		}
		
		index = 0;
		for (String w:filtered) {
			if (w.startsWith(p)) {
				return unfiltered[index];
			}
			index++;
		}
		
		index = 0;
		for (String w:filtered) {
			if (w.contains(p)) {
				return unfiltered[index];
			}
			index++;
		}
		
		return null;
	}
	
	public static int convertPlayerDirectionToSignRotation(int dir) {
		switch (dir) {
		case 0:
			return 0;
		case 1:
			return 4;
		case 2:
			return 8;
		case 3:
			return 12;
		}
		return 0;
	}
	
	public static EntityPlayerMP getOnlinePlayerInexact(String p) {
		p = p.toLowerCase();
		EntityPlayerMP[] online = getOnlinePlayers();
		String[] cache = new String[online.length];
		int cacheSize = 0;
		
		int cacheIndex = 0;
		for (EntityPlayerMP player:online) {
			String name = player.getCommandSenderName().toLowerCase();
			cache[cacheSize] = name;
			cacheSize++;
			if (name.equals(p)) {
				return player;
			}
		}
		
		cacheIndex = 0;
		for (EntityPlayerMP player:online) {
			if (cache[cacheIndex].startsWith(p)) {
				return player;
			}
			cacheIndex++;
		}
		
		cacheIndex = 0;
		for (EntityPlayerMP player:online) {
			if (cache[cacheIndex].contains(p)) {
				return player;
			}
			cacheIndex++;
		}
		
		return null;
	}
	
	public static int convertPlayerDirectionToChestRotation(int dir) {
		switch (dir) {
		case 0:
			return 0;
		case 1:
			return 4;
		case 2:
			return 2;
		case 3:
			return 5;
		}
		return 0;
	}
	
	public static String discordColorsToBlackAndWhite(String msg) {
		StringBuilder b = new StringBuilder(msg.length());
		boolean color = false;
		
		boolean append = false;
		boolean spoiler = false;
		boolean cut = false;
		boolean under = false;
		boolean italian = false;
		
		for (char c:msg.toCharArray()) {
			if (c == '§') {
				color = true;
				continue;
			}
			if (color) {
				c = Character.toLowerCase(c);
				if (c == 'l') {
					color = false;
					continue;
				}
				if (c == 'k') {
					spoiler = !spoiler;
					b.append("||");
					color = false;
					continue;
				}
				if (c == 'm') {
					cut = !cut;
					b.append("~~");
					color = false;
					continue;
				}
				if (c == 'n') {
					under = !under;
					b.append("__");
					color = false;
					continue;
				}
				if (c == 'o') {
					italian = !under;
					b.append("_");
					color = false;
					continue;
				}
				if (cut) {
					cut = false;
					b.append("~~");
				}
				if (under) {
					under = false;
					b.append("__");
				}
				if (italian) {
					italian = false;
					b.append("_");
				}
				if (spoiler) {
					spoiler = false;
					b.append("||");
				}
				b.append("**");
				color = false;
				append = !append;
				continue;
			}
			b.append(c);
		}
		if (spoiler) {
			b.append("||");
		}
		if (cut) {
			b.append("~~");
		}
		if (under) {
			b.append("__");
		}
		if (italian) {
			b.append("_");
		}
		if (append) {
			b.append("**");
		}
		return b.toString();
	}
	
	public static String translateUnlocalizedToPortuguese(String unlocalized) {
		String value = ptBrCache.get(unlocalized);
		if (value == null) {
			value = ptBr.get(unlocalized);
			if (value != null) {
				ptBrCache.put(unlocalized, value);
			}
		}
		return value;
	}
	
	public static boolean containsUnlocalizedPortuguese(String unlocalized) {
		return ptBr.containsKey(unlocalized);
	}
	
	public static boolean itemStackEquals(ItemStack a, ItemStack b) {
		if (a.getItemDamage() == b.getItemDamage()) {
			if (Item.getIdFromItem(a.getItem()) == Item.getIdFromItem(b.getItem())) {
				NBTTagCompound itemNbt = a.getTagCompound();
				NBTTagCompound compareNbt = b.getTagCompound();
				if (itemNbt == compareNbt) {
					return true;
				}
				if (itemNbt == null || compareNbt == null) {
					return false;
				}
				return itemNbt.equals(compareNbt);
			}
		}
		return false;
	}
	
	public static int convertChestRotationToSignRotation(int chest) {
		switch (chest) {
		case 0:
			return 0;
		case 4:
			return 4;
		case 2:
			return 8;
		case 5:
			return 12;
		}
		return 0;
	}
	
	
	public static void placeChest(World w, int x, int y, int z, int rotation, boolean trapped) {
		if (trapped) {
			w.setBlock(x, y, z, Blocks.trapped_chest, 0, 2);
		} else {
			w.setBlock(x, y, z, Blocks.chest, 0, 2);
		}
		w.setBlockMetadataWithNotify(x, y, z, rotation, 2);
	}
	
	public static void placeSign(World world, int x, int y, int z, int rotation, String line1, String line2, String line3, String line4) {
		world.setBlock(x, y, z, Blocks.standing_sign, rotation, 2);
		TileEntitySign sign = (TileEntitySign) world.getTileEntity(x, y, z);
		
		String[] result = {line1, line2, line3, line4};
		
		sign.signText = result;
		sign.updateEntity();
	}
	
	public static String[] signSplit(String text) {
		List<String> words = new ArrayList<>();
		StringBuilder builder = new StringBuilder(text.length());
		for (char c:text.toCharArray()) {
			if (c == ' ') {
				if (builder.length() != 0) {
					words.add(builder.toString());
					builder.setLength(0);
				}
				continue;
			}
			builder.append(c);
		}
		if (builder.length() != 0) {
			words.add(builder.toString());
		}
		List<String> list = new ArrayList<>();
		StringBuilder b = new StringBuilder(text.length());
		int spaceLeft = 15;
		for (int i = 0; i < words.size(); i++) {
			String word = words.get(i);
			if (word.length() > 15) {
				if (spaceLeft == 0) {
					i -= 1;
					list.add(b.toString());
					b.setLength(0);
					spaceLeft = 15;
					continue;
				}
				for (char c:word.toCharArray()) {
					if (spaceLeft == 0) {
						list.add(b.toString());
						b.setLength(0);
						spaceLeft = 15;
					}
					b.append(c);
					spaceLeft -= 1;
				}
				continue;
			}
			if ((spaceLeft - (word.length() + 1)) >= 0) {
				b.append(word);
				spaceLeft -= word.length() + 1;
				if (i != (words.size() - 1) && spaceLeft != 0) {
					String next = null;
					if (words.size() > (i + 1)) {
						next = words.get(i + 1);
					}
					if (next != null && (spaceLeft - (next.length() + 1)) >= 0) {
						b.append(' ');
						spaceLeft -= 1;
					}
				}
			} else {
				list.add(b.toString());
				b.setLength(0);
				spaceLeft = 15;
				i -= 1;
			}
		}
		if (b.length() != 0) {
			list.add(b.toString());
		}
		return list.toArray(new String[list.size()]);
	}
	
	public static String getPortugueseItemName(ItemStack stack) {
		return stack.getDisplayName();
	}
	
	public static String getEnglishItemName(ItemStack stack) {
		return PortugueseStringTranslate.FALLBACK.translateKey(stack.getUnlocalizedName()+".name");
	}
	
	public static void sendMessage(ICommandSender player, String msg) {
		player.addChatMessage(msgToComponent(msg));
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
		for (int i = 255; i > 0; i--) {
			if (Block.getIdFromBlock(world.getBlock(x, i, z)) != 0) {
				return i;
			}
		}
		return 255;
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
			player.mcServer.getConfigurationManager().transferPlayerToDimension(player, w.provider.dimensionId, new NoTeleporter(Util.getWorld(w.provider.getDimensionName())));
		}
		player.setPositionAndRotation(x, y, z, yaw, pitch);
		player.setPositionAndUpdate(x, y, z);
		player.addExperienceLevel(0);
		if (oldDim == 1 && oldDim != w.provider.dimensionId) {
			Util.run("Remove player from end: "+player.getDisplayName(), () -> {
				player.mcServer.getConfigurationManager().transferPlayerToDimension(player, w.provider.dimensionId, new NoTeleporter(Util.getWorld(w.provider.getDimensionName())));
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
	
	public static WorldServer[] getWorlds() {
		return DimensionManager.getWorlds();
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
		
		boolean spoiler = false;
		boolean cut = false;
		boolean under = false;
		boolean italian = false;
		boolean bold = false;
		
		boolean color = false;
		
		for (char c:msg.toCharArray()) {
			if (color) {
				color = false;
				boolean modificator = false;
				if (c == 'l') {
					bold = true;
					modificator = true;
				}
				if (c == 'k') {
					spoiler = true;
					modificator = true;
				}
				if (c == 'm') {
					cut = true;
					modificator = true;
				}
				if (c == 'n') {
					under = true;
					modificator = true;
				}
				if (c == 'o') {
					italian = true;
					modificator = true;
				}
				if (!modificator) {
					lastColor = c;
					bold = false;
					spoiler = false;
					cut = false;
					under = false;
					italian = false;
				}
			}
			if (c == '§') {
				color = true;
			}
			b.append(c);
			if (c == ' ') {
				b.append('§');
				b.append(lastColor);
				if (bold) {
					b.append("§l");
				}
				if (spoiler) {
					b.append("§k");
				}
				if (cut) {
					b.append("§m");
				}
				if (under) {
					b.append("§n");
				}
				if (italian) {
					b.append("§o");
				}
			}
		}
		return b.toString();
	}
	
	public static IChatComponent msgToComponent(String msg) {
		StringBuilder b = new StringBuilder(64);
		boolean color = false;
		
		IChatComponent main = new ChatComponentText("");
		
		IChatComponent current = null;
		ChatStyle style = new ChatStyle();
		
		for (char c:msg.toCharArray()) {
			if (color) {
				color = false;
				if (c == 'l') {
					style.setBold(true);
				}
				if (c == 'k') {
					style.setObfuscated(true);
				}
				if (c == 'm') {
					style.setStrikethrough(true);
				}
				if (c == 'n') {
					style.setUnderlined(true);
				}
				if (c == 'o') {
					style.setItalic(true);
				}
				if (c == 'a') {
					style.setBold(false);
					style.setObfuscated(false);
					style.setStrikethrough(false);
					style.setUnderlined(false);
					style.setItalic(false);
					style.setColor(EnumChatFormatting.GREEN);
				}
				if (c == 'b') {
					style.setBold(false);
					style.setObfuscated(false);
					style.setStrikethrough(false);
					style.setUnderlined(false);
					style.setItalic(false);
					style.setColor(EnumChatFormatting.BLUE);
				}
				if (c == 'c') {
					style.setBold(false);
					style.setObfuscated(false);
					style.setStrikethrough(false);
					style.setUnderlined(false);
					style.setItalic(false);
					style.setColor(EnumChatFormatting.RED);
				}
				if (c == 'd') {
					style.setBold(false);
					style.setObfuscated(false);
					style.setStrikethrough(false);
					style.setUnderlined(false);
					style.setItalic(false);
					style.setColor(EnumChatFormatting.LIGHT_PURPLE);
				}
				if (c == 'e') {
					style.setBold(false);
					style.setObfuscated(false);
					style.setStrikethrough(false);
					style.setUnderlined(false);
					style.setItalic(false);
					style.setColor(EnumChatFormatting.YELLOW);
				}
				if (c == 'f') {
					style.setBold(false);
					style.setObfuscated(false);
					style.setStrikethrough(false);
					style.setUnderlined(false);
					style.setItalic(false);
					style.setColor(EnumChatFormatting.WHITE);
				}
				if (c == '0') {
					style.setBold(false);
					style.setObfuscated(false);
					style.setStrikethrough(false);
					style.setUnderlined(false);
					style.setItalic(false);
					style.setColor(EnumChatFormatting.BLACK);
				}
				if (c == '1') {
					style.setBold(false);
					style.setObfuscated(false);
					style.setStrikethrough(false);
					style.setUnderlined(false);
					style.setItalic(false);
					style.setColor(EnumChatFormatting.DARK_BLUE);
				}
				if (c == '2') {
					style.setBold(false);
					style.setObfuscated(false);
					style.setStrikethrough(false);
					style.setUnderlined(false);
					style.setItalic(false);
					style.setColor(EnumChatFormatting.DARK_GREEN);
				}
				if (c == '3') {
					style.setBold(false);
					style.setObfuscated(false);
					style.setStrikethrough(false);
					style.setUnderlined(false);
					style.setItalic(false);
					style.setColor(EnumChatFormatting.AQUA);
				}
				if (c == '4') {
					style.setBold(false);
					style.setObfuscated(false);
					style.setStrikethrough(false);
					style.setUnderlined(false);
					style.setItalic(false);
					style.setColor(EnumChatFormatting.DARK_RED);
				}
				if (c == '5') {
					style.setBold(false);
					style.setObfuscated(false);
					style.setStrikethrough(false);
					style.setUnderlined(false);
					style.setItalic(false);
					style.setColor(EnumChatFormatting.DARK_PURPLE);
				}
				if (c == '6') {
					style.setBold(false);
					style.setObfuscated(false);
					style.setStrikethrough(false);
					style.setUnderlined(false);
					style.setItalic(false);
					style.setColor(EnumChatFormatting.GOLD);
				}
				if (c == '7') {
					style.setBold(false);
					style.setObfuscated(false);
					style.setStrikethrough(false);
					style.setUnderlined(false);
					style.setItalic(false);
					style.setColor(EnumChatFormatting.GRAY);
				}
				if (c == '8') {
					style.setBold(false);
					style.setObfuscated(false);
					style.setStrikethrough(false);
					style.setUnderlined(false);
					style.setItalic(false);
					style.setColor(EnumChatFormatting.DARK_GRAY);
				}
				if (c == '9') {
					style.setBold(false);
					style.setObfuscated(false);
					style.setStrikethrough(false);
					style.setUnderlined(false);
					style.setItalic(false);
					style.setColor(EnumChatFormatting.DARK_AQUA);
				}
			}
			if (c == '§') {
				color = true;
			}
			if (c == ' ') {
				String text = b.toString();
				current = new ChatComponentText(text);
				boolean http = false;
				if (text.startsWith("http")) {
					http = true;
					style.setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, text));
					style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Clique para abrir.")));
				}
				style = style.createDeepCopy();
				if (http) {
					style.setChatClickEvent(null);
					style.setChatHoverEvent(null);
				}
				main.appendSibling(current);
				current.setChatStyle(style);
				b.setLength(0);
				
				
				main.appendSibling(new ChatComponentText(" "));
				continue;
			}
			b.append(c);
		}
		if (b.length() != 0) {
			String text = b.toString();
			current = new ChatComponentText(text);
			if (text.startsWith("http")) {
				style.setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, text));
				style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Clique para abrir.")));
			}
			main.appendSibling(current);
			current.setChatStyle(style);
		}
		return main;
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
