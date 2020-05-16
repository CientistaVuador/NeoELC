package com.cien.economy;

import com.cien.PositiveLocation;
import com.cien.Util;
import com.cien.claims.CienClaims;
import com.cien.claims.Claim;
import com.cien.data.Node;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.world.WorldServer;

public class ChestShop {

	private final ItemStack item;
	private final boolean keepNbt;
	private final boolean buy;
	private final LongDecimal price;
	private final int x;
	private final int y;
	private final int z;
	private final String world;
	private final String owner;
	private final boolean unlimited;
	
	public Shop shop = null;
	
	private String[] signTextCache = null;
	
	public ChestShop(ItemStack item, boolean nbt, boolean buy, LongDecimal price, int x, int y, int z, String world, String owner, boolean unlimited) {
		this.keepNbt = nbt;
		if (this.keepNbt) {
			this.item = item;
		} else {
			ItemStack f = item.copy();
			try {
				f.setTagCompound((NBTTagCompound)JsonToNBT.func_150315_a("{}"));
			} catch (NBTException e) {
				e.printStackTrace();
			}
			this.item = f;
		}
		this.buy = buy;
		this.price = price;
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
		this.owner = owner;
		this.unlimited = unlimited;
	}
	
	public ChestShop(Node n) {
		this.item = Util.getItemStackFromNode(n.getNode("item"));
		this.keepNbt = Boolean.parseBoolean(n.getField("nbt"));
		this.buy = Boolean.parseBoolean(n.getField("buy"));
		this.price = LongDecimal.parse(n.getField("price"));
		this.x = Integer.parseInt(n.getField("x"));
		this.y = Integer.parseInt(n.getField("y"));
		this.z = Integer.parseInt(n.getField("z"));
		this.world = n.getField("world");
		this.owner = n.getField("owner");
		this.unlimited = Boolean.parseBoolean(n.getField("unlimited"));
	}
	
	public Shop getShop() {
		return shop;
	}
	
	public Node toNode() {
		Node n = new Node(toString());
		n.addNode(Util.getNodeFromItemStack("item", this.item, true));
		n.setField("nbt", Boolean.toString(this.keepNbt));
		n.setField("buy", Boolean.toString(this.buy));
		n.setField("price", this.price.toString());
		n.setField("x", Integer.toString(this.x));
		n.setField("y", Integer.toString(this.y));
		n.setField("z", Integer.toString(this.z));
		n.setField("world", this.world);
		n.setField("owner", this.owner);
		n.setField("unlimited", Boolean.toString(this.unlimited));
		return n;
	}
	
	public boolean isBuy() {
		return buy;
	}
	
	public boolean isKeepNbt() {
		return keepNbt;
	}
	
	public boolean itemEquals(ItemStack s) {
		if (s.getItemDamage() == item.getItemDamage()) {
			if (Item.getIdFromItem(s.getItem()) == Item.getIdFromItem(item.getItem())) {
				if (keepNbt) {
					NBTTagCompound itemNbt = item.getTagCompound();
					NBTTagCompound compareNbt = s.getTagCompound();
					if (itemNbt == compareNbt) {
						return true;
					}
				    if (itemNbt != null && itemNbt.hasNoTags() && compareNbt == null) {
				    	return true;
				    }
				    if (compareNbt != null && compareNbt.hasNoTags() && itemNbt == null) {
				    	return true;
				    }
					if (itemNbt == null || compareNbt == null) {
						return false;
					}
					return itemNbt.equals(compareNbt);
				} 
				return true;
			}
		}
		return false;
	}
	
	public ItemStack getItem() {
		return item.copy();
	}
	
	public boolean isUnlimited() {
		return this.unlimited;
	}
	
	public String getOwner() {
		return owner;
	}
	
	public LongDecimal getPrice() {
		return price;
	}
	
	public String getWorld() {
		return world;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getZ() {
		return z;
	}
	
	public boolean addItemStackToChestInventory(ItemStack s) {
		TileEntityChest chest = getChestTileEntity();
		if (s.stackSize == 0) {
			return true;
		}
		for (int i = 0; i < chest.getSizeInventory(); i++) {
			ItemStack f = chest.getStackInSlot(i);
			if (s.stackSize == 0) {
				return true;
			}
			if (f == null) {
				continue;
			}
			if (Util.itemStackEquals(f, s)) {
				if (f.stackSize < f.getMaxStackSize()) {
					int spaceLeft = f.getMaxStackSize() - f.stackSize;
					if (spaceLeft > 0) {
						if (spaceLeft >= s.stackSize) {
							f.stackSize = f.stackSize + s.stackSize;
							s.stackSize = 0;
							return true;
						} else {
							s.stackSize = s.stackSize - spaceLeft;
							f.stackSize = f.stackSize + spaceLeft;
						}
					}
				}
			}
		}
		if (s.stackSize == 0) {
			return true;
		}
		for (int i = 0; i < chest.getSizeInventory(); i++) {
			ItemStack f = chest.getStackInSlot(i);
			if (f == null) {
				chest.setInventorySlotContents(i, s);
				return true;
			}
		}
		return false;
	}
	
	public boolean transferOneToPlayer(EntityPlayerMP player) {
		InventoryPlayer invPlayer = player.inventory;
		TileEntityChest chestInv = getChestTileEntity();
		if (unlimited) {
			ItemStack stack = getItem();
			stack.stackSize = 1;
			return invPlayer.addItemStackToInventory(stack);
		}
		for (int i = 0; i < chestInv.getSizeInventory(); i++) {
			ItemStack s = chestInv.getStackInSlot(i);
			if (s == null) {
				continue;
			}
			if (itemEquals(s)) {
				ItemStack copy = s.copy();
				copy.stackSize = 1;
				if (invPlayer.addItemStackToInventory(copy)) {
					if (s.stackSize == 1) {
						chestInv.setInventorySlotContents(i, null);
					} else {
						s.stackSize = s.stackSize - 1;
					}
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}
	
	public boolean transferOneToChest(EntityPlayerMP player) {
		InventoryPlayer invPlayer = player.inventory;
		if (unlimited) {
			for (int i = 0; i < invPlayer.getSizeInventory(); i++) {
				ItemStack s = invPlayer.getStackInSlot(i);
				if (s == null) {
					continue;
				}
				if (itemEquals(s)) {
					ItemStack copy = s.copy();
					copy.stackSize = 1;
					if (s.stackSize == 1) {
						invPlayer.setInventorySlotContents(i, null);
					} else {
						s.stackSize = s.stackSize - 1;
					}
					return true;
				}
			}
			return false;
		}
		for (int i = 0; i < invPlayer.getSizeInventory(); i++) {
			ItemStack s = invPlayer.getStackInSlot(i);
			if (s == null) {
				continue;
			}
			if (itemEquals(s)) {
				ItemStack copy = s.copy();
				copy.stackSize = 1;
				if (addItemStackToChestInventory(copy)) {
					if (s.stackSize == 1) {
						invPlayer.setInventorySlotContents(i, null);
					} else {
						s.stackSize = s.stackSize - 1;
					}
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}
	
	public int transferToPlayer(EntityPlayerMP player, int amount) {
		int transfered = 0;
		for (int i = 0; i < amount; i++) {
			if (transferOneToPlayer(player)) {
				transfered++;
			}
		}
		return transfered;
	}
	
	public int transferToChest(EntityPlayerMP player, int amount) {
		int transfered = 0;
		for (int i = 0; i < amount; i++) {
			if (transferOneToChest(player)) {
				transfered++;
			}
		}
		return transfered;
	}
	
	public TileEntityChest getChestTileEntity() {
		WorldServer world = Util.getWorld(this.world);
		if (world == null) {
			return null;
		}
		TileEntity ent = world.getTileEntity(x, y, z);
		if (ent == null) {
			return null;
		}
		if (ent instanceof TileEntityChest) {
			return (TileEntityChest) ent;
		}
		return null;
	}
	
	public TileEntitySign getSignTileEntity() {
		WorldServer world = Util.getWorld(this.world);
		if (world == null) {
			return null;
		}
		TileEntity ent = world.getTileEntity(x, y+1, z);
		if (ent == null) {
			return null;
		}
		if (ent instanceof TileEntitySign) {
			return (TileEntitySign) ent;
		}
		return null;
	}
	
	public String[] getSignText() {
		if (signTextCache  == null) {
			String[] lines = new String[4];
			if (buy) {
				if (getPrice().isZero()) {
					lines[0] = "§4C$ Grátis";
				} else {
					lines[0] = "§4C$ "+getPrice().toMinimizedString();
				}
			} else {
				if (getPrice().isZero()) {
					lines[0] = "§aC$ Grátis";
				} else {
					lines[0] = "§aC$ "+getPrice().toMinimizedString();
				}
			}
			String displayName = this.item.getDisplayName();
			if (Item.getIdFromItem(this.item.getItem()) == Item.getIdFromItem(Items.enchanted_book)) {
				if (!keepNbt) {
					displayName = "L. Encantado (Qualquer)";
				} else {
					StringBuilder displayNameBuilder = new StringBuilder(64);
					displayNameBuilder.append("L. Encantado (");
					ItemEnchantedBook book = (ItemEnchantedBook) item.getItem();
					NBTTagList nbttaglist = book.func_92110_g(this.item);

			        if (nbttaglist != null)
			        {
			            for (int i = 0; i < nbttaglist.tagCount(); ++i)
			            {
			                short short1 = nbttaglist.getCompoundTagAt(i).getShort("id");
			                short short2 = nbttaglist.getCompoundTagAt(i).getShort("lvl");

			                if (Enchantment.enchantmentsList[short1] != null)
			                {
			                	String enchant = Enchantment.enchantmentsList[short1].getTranslatedName(short2);
			                	displayNameBuilder.append(enchant);
			                	if (i != (nbttaglist.tagCount() - 1)) {
			                		displayNameBuilder.append(',');
			                	}
			                }
			            }
			        }
			        displayNameBuilder.append(')');
			        displayName = displayNameBuilder.toString();
				}
			}
			String[] itemNameLines = Util.signSplit(displayName);
			for (int i = 1; i < 4; i++) {
				if (i > itemNameLines.length) {
					lines[i] = "";
					continue;
				}
				lines[i] = itemNameLines[i - 1];
			}
			signTextCache = lines;
		}
		return signTextCache;
	}
	
	public boolean isWorldLoaded() {
		return Util.getWorld(this.world) != null;
	}
	
	public boolean isSignValid() {
		if (!isWorldLoaded()) {
			return true;
		}
		TileEntitySign sign = getSignTileEntity();
		if (sign == null) {
			return false;
		}
		String[] text = getSignText();
		String[] textFound = sign.signText;
		String a = text[0];
		String b = textFound[0];
		if (a == null && b == null) {
			return true;
		}
		if (a == null || b == null) {
			return false;
		}
		if (!a.equals(b)) {
			return false;
		}
		return true;
	}
	
	public boolean canPlaceChest() {
		if (!isWorldLoaded()) {
			return true;
		}
		WorldServer server = Util.getWorld(this.world);
		if (!server.isAirBlock(x, y, z) || !server.isAirBlock(x, y, z)) {
			return false;
		}
		boolean trapped = false;
		
		byte up = getChestOnUp();
		byte down = getChestOnDown();
		byte left = getChestOnLeft();
		byte right = getChestOnRight();
		
		if (left == 1 || down == 1 || up == 1 || right == 1) {
			trapped = true;
		}
		if (left == 2 || down == 2 || up == 2 || right == 2) {
			if (trapped) {
				return false;
			}
		}
		return true;
	}
	
	public boolean hasChestConflict() {
		if (!isWorldLoaded()) {
			return false;
		}
		boolean trapped = false;
		
		byte up = getChestOnUp();
		byte down = getChestOnDown();
		byte left = getChestOnLeft();
		byte right = getChestOnRight();
		
		if (left == 1 || down == 1 || up == 1 || right == 1) {
			trapped = true;
		}
		if (left == 2 || down == 2 || up == 2 || right == 2) {
			if (trapped) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isChestValid() {
		if (!isWorldLoaded()) {
			return true;
		}
		TileEntity ent = getChestTileEntity();
		if (ent == null) {
			ent = getChestTileEntity();
		}
		return ent != null;
	}
	
	public Claim getCurrentClaim() {
		if (!isWorldLoaded()) {
			return null;
		}
		Claim current = CienClaims.CLAIMS.getClaimInside(new PositiveLocation(x, y, z), Util.getWorld(this.world));
		return current;
	}
	
	public boolean isValid() {
		if (!isWorldLoaded()) {
			return true;
		}
		Claim current = CienClaims.CLAIMS.getClaimInside(new PositiveLocation(x, y, z), Util.getWorld(this.world));
		if (current == null) {
			return false;
		}
		if (!current.getOwner().equals(this.owner)) {
			return false;
		}
		return isChestValid() && isSignValid();
	}
	
	public WorldServer getWorldInstance() {
		return Util.getWorld(this.world);
	}
	
	public boolean placeSignAndChest(int playerRotation) {
		if (!isWorldLoaded()) {
			return false;
		}
		WorldServer server = Util.getWorld(this.world);
		if (!server.isAirBlock(x, y, z) || !server.isAirBlock(x, y, z)) {
			return false;
		}
		Claim current = getCurrentClaim();
		if (current == null) {
			return false;
		}
		if (!current.getOwner().equals(this.owner)) {
			return false;
		}
		
		boolean trapped = false;
		
		byte up = getChestOnUp();
		byte down = getChestOnDown();
		byte left = getChestOnLeft();
		byte right = getChestOnRight();
		
		if (left == 1 || down == 1 || up == 1 || right == 1) {
			trapped = true;
		}
		if (left == 2 || down == 2 || up == 2 || right == 2) {
			if (trapped) {
				return false;
			}
		}
		
		Util.placeChest(server, x, y, z, Util.convertPlayerDirectionToChestRotation(playerRotation), trapped);
		String[] lines = getSignText();
		Util.placeSign(server, x, y+1, z, Util.convertPlayerDirectionToSignRotation(playerRotation), lines[0], lines[1], lines[2], lines[3]);
		return true;
	}
	
	//0 - No Chest, 1 - Normal Chest, 2 - Trapped Chest
	public byte getChestOnLeft() {
		if (!isWorldLoaded()) {
			return 0;
		}
		PositiveLocation loc = new PositiveLocation(x, y, z);
		PositiveLocation left = loc.add(-1, 0, 0);
		Block b = left.getBlockAt(getWorldInstance());
		if (Block.getIdFromBlock(b) == Block.getIdFromBlock(Blocks.chest)) {
			return 1;
		}
		if (Block.getIdFromBlock(b) == Block.getIdFromBlock(Blocks.trapped_chest)) {
			return 2;
		}
		return 0;
	}
	
	//0 - No Chest, 1 - Normal Chest, 2 - Trapped Chest
	public byte getChestOnRight() {
		if (!isWorldLoaded()) {
			return 0;
		}
		PositiveLocation loc = new PositiveLocation(x, y, z);
		PositiveLocation left = loc.add(1, 0, 0);
		Block b = left.getBlockAt(getWorldInstance());
		if (Block.getIdFromBlock(b) == Block.getIdFromBlock(Blocks.chest)) {
			return 1;
		}
		if (Block.getIdFromBlock(b) == Block.getIdFromBlock(Blocks.trapped_chest)) {
			return 2;
		}
		return 0;
	}
	
	//0 - No Chest, 1 - Normal Chest, 2 - Trapped Chest
	public byte getChestOnUp() {
		if (!isWorldLoaded()) {
			return 0;
		}
		PositiveLocation loc = new PositiveLocation(x, y, z);
		PositiveLocation left = loc.add(0, 0, 1);
		Block b = left.getBlockAt(getWorldInstance());
		if (Block.getIdFromBlock(b) == Block.getIdFromBlock(Blocks.chest)) {
			return 1;
		}
		if (Block.getIdFromBlock(b) == Block.getIdFromBlock(Blocks.trapped_chest)) {
			return 2;
		}
		return 0;
	}
	
	//0 - No Chest, 1 - Normal Chest, 2 - Trapped Chest
	public byte getChestOnDown() {
		if (!isWorldLoaded()) {
			return 0;
		}
		PositiveLocation loc = new PositiveLocation(x, y, z);
		PositiveLocation left = loc.add(0, 0, -1);
		Block b = left.getBlockAt(getWorldInstance());
		if (Block.getIdFromBlock(b) == Block.getIdFromBlock(Blocks.chest)) {
			return 1;
		}
		if (Block.getIdFromBlock(b) == Block.getIdFromBlock(Blocks.trapped_chest)) {
			return 2;
		}
		return 0;
	}
}
