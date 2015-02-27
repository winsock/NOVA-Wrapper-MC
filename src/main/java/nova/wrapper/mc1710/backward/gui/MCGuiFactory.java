package nova.wrapper.mc1710.backward.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;
import nova.core.entity.Entity;
import nova.core.gui.Gui;
import nova.core.gui.factory.GuiFactory;
import nova.core.util.exception.NovaException;
import nova.core.util.transform.Vector3i;
import nova.wrapper.mc1710.backward.entity.BWEntityPlayer;
import nova.wrapper.mc1710.backward.gui.MCGui.MCContainer;
import nova.wrapper.mc1710.backward.gui.MCGui.MCGuiScreen;
import nova.wrapper.mc1710.launcher.NovaMinecraft;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IGuiHandler;

public class MCGuiFactory extends GuiFactory {

	private static Optional<Gui> guiToOpen = Optional.empty();
	private static List<Gui> idMappedGUIs = new ArrayList<>();

	@Override
	public void registerGui(Gui gui, String modID) {
		super.registerGui(gui, modID);
		idMappedGUIs.add(gui);
	}

	@Override
	protected boolean bind(Gui gui, Entity entity, Vector3i pos) {
		BWEntityPlayer player = (BWEntityPlayer) entity;
		guiToOpen = Optional.of(gui);
		if (player.entity.worldObj.isRemote != gui.hasServerSide()) {
			int id = idMappedGUIs.contains(idMappedGUIs) ? idMappedGUIs.indexOf(idMappedGUIs) : -1;
			player.entity.openGui(NovaMinecraft.id, id, player.entity.getEntityWorld(), pos.x, pos.y, pos.z);
			return true;
		}
		return false;
	}

	@Override
	protected void unbind(Gui gui) {
		FMLCommonHandler.instance().showGuiScreen(null);
	}
	
	@Override
	public Optional<Gui> getActiveGuiImpl() {
		GuiScreen gui = Minecraft.getMinecraft().currentScreen;
		if (gui instanceof MCGuiScreen) {
			return Optional.of(((MCGuiScreen) gui).getGui().getComponent());
		}
		return Optional.empty();
	}

	@Override
	public Optional<Gui> getActiveGuiImpl(Entity player) {
		BWEntityPlayer entityPlayer = (BWEntityPlayer) player;
		Container container = entityPlayer.entity.openContainer;
		if (container instanceof MCContainer) {
			return Optional.of(((MCContainer) container).getGui().getComponent());
		}
		return Optional.empty();
	}

	public static class GuiHandler implements IGuiHandler {

		@Override
		public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
			if(guiToOpen.isPresent()) {
				return ((MCGui) guiToOpen.get().getNative()).getContainer();
			}
			return null;
		}

		@Override
		public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
			try {
				if (guiToOpen.isPresent()) {
					return ((MCGui) guiToOpen.get().getNative()).getGuiScreen();
				} else {
					return ((MCGui) idMappedGUIs.get(id).getNative()).getGuiScreen();
				}
			} catch (Exception e) {
				throw new NovaException("Couldn't get client side instance for the provided GUI of id " + id + " !");
			}
		}	
	}
}
